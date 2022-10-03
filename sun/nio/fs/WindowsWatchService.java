package sun.nio.fs;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.NotDirectoryException;
import com.sun.nio.file.SensitivityWatchEventModifier;
import com.sun.nio.file.ExtendedWatchEventModifier;
import java.util.HashMap;
import java.util.Map;
import sun.misc.Unsafe;
import java.util.Set;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent;
import java.nio.file.Path;
import java.io.IOException;

class WindowsWatchService extends AbstractWatchService
{
    private static final int WAKEUP_COMPLETION_KEY = 0;
    private final Poller poller;
    private static final int ALL_FILE_NOTIFY_EVENTS = 351;
    
    WindowsWatchService(final WindowsFileSystem windowsFileSystem) throws IOException {
        long createIoCompletionPort;
        try {
            createIoCompletionPort = WindowsNativeDispatcher.CreateIoCompletionPort(-1L, 0L, 0L);
        }
        catch (final WindowsException ex) {
            throw new IOException(ex.getMessage());
        }
        (this.poller = new Poller(windowsFileSystem, this, createIoCompletionPort)).start();
    }
    
    @Override
    WatchKey register(final Path path, final WatchEvent.Kind<?>[] array, final WatchEvent.Modifier... array2) throws IOException {
        return this.poller.register(path, array, array2);
    }
    
    @Override
    void implClose() throws IOException {
        this.poller.close();
    }
    
    private static class WindowsWatchKey extends AbstractWatchKey
    {
        private final FileKey fileKey;
        private volatile long handle;
        private Set<? extends WatchEvent.Kind<?>> events;
        private boolean watchSubtree;
        private NativeBuffer buffer;
        private long countAddress;
        private long overlappedAddress;
        private int completionKey;
        private boolean errorStartingOverlapped;
        
        WindowsWatchKey(final Path path, final AbstractWatchService abstractWatchService, final FileKey fileKey) {
            super(path, abstractWatchService);
            this.handle = -1L;
            this.fileKey = fileKey;
        }
        
        WindowsWatchKey init(final long handle, final Set<? extends WatchEvent.Kind<?>> events, final boolean watchSubtree, final NativeBuffer buffer, final long countAddress, final long overlappedAddress, final int completionKey) {
            this.handle = handle;
            this.events = events;
            this.watchSubtree = watchSubtree;
            this.buffer = buffer;
            this.countAddress = countAddress;
            this.overlappedAddress = overlappedAddress;
            this.completionKey = completionKey;
            return this;
        }
        
        long handle() {
            return this.handle;
        }
        
        Set<? extends WatchEvent.Kind<?>> events() {
            return this.events;
        }
        
        void setEvents(final Set<? extends WatchEvent.Kind<?>> events) {
            this.events = events;
        }
        
        boolean watchSubtree() {
            return this.watchSubtree;
        }
        
        NativeBuffer buffer() {
            return this.buffer;
        }
        
        long countAddress() {
            return this.countAddress;
        }
        
        long overlappedAddress() {
            return this.overlappedAddress;
        }
        
        FileKey fileKey() {
            return this.fileKey;
        }
        
        int completionKey() {
            return this.completionKey;
        }
        
        void setErrorStartingOverlapped(final boolean errorStartingOverlapped) {
            this.errorStartingOverlapped = errorStartingOverlapped;
        }
        
        boolean isErrorStartingOverlapped() {
            return this.errorStartingOverlapped;
        }
        
        void invalidate() {
            ((WindowsWatchService)this.watcher()).poller.releaseResources(this);
            this.handle = -1L;
            this.buffer = null;
            this.countAddress = 0L;
            this.overlappedAddress = 0L;
            this.errorStartingOverlapped = false;
        }
        
        @Override
        public boolean isValid() {
            return this.handle != -1L;
        }
        
        @Override
        public void cancel() {
            if (this.isValid()) {
                ((WindowsWatchService)this.watcher()).poller.cancel(this);
            }
        }
    }
    
    private static class FileKey
    {
        private final int volSerialNumber;
        private final int fileIndexHigh;
        private final int fileIndexLow;
        
        FileKey(final int volSerialNumber, final int fileIndexHigh, final int fileIndexLow) {
            this.volSerialNumber = volSerialNumber;
            this.fileIndexHigh = fileIndexHigh;
            this.fileIndexLow = fileIndexLow;
        }
        
        @Override
        public int hashCode() {
            return this.volSerialNumber ^ this.fileIndexHigh ^ this.fileIndexLow;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof FileKey)) {
                return false;
            }
            final FileKey fileKey = (FileKey)o;
            return this.volSerialNumber == fileKey.volSerialNumber && this.fileIndexHigh == fileKey.fileIndexHigh && this.fileIndexLow == fileKey.fileIndexLow;
        }
    }
    
    private static class Poller extends AbstractPoller
    {
        private static final Unsafe UNSAFE;
        private static final short SIZEOF_DWORD = 4;
        private static final short SIZEOF_OVERLAPPED = 32;
        private static final short OFFSETOF_HEVENT;
        private static final short OFFSETOF_NEXTENTRYOFFSET = 0;
        private static final short OFFSETOF_ACTION = 4;
        private static final short OFFSETOF_FILENAMELENGTH = 8;
        private static final short OFFSETOF_FILENAME = 12;
        private static final int CHANGES_BUFFER_SIZE = 16384;
        private final WindowsFileSystem fs;
        private final WindowsWatchService watcher;
        private final long port;
        private final Map<Integer, WindowsWatchKey> ck2key;
        private final Map<FileKey, WindowsWatchKey> fk2key;
        private int lastCompletionKey;
        
        Poller(final WindowsFileSystem fs, final WindowsWatchService watcher, final long port) {
            this.fs = fs;
            this.watcher = watcher;
            this.port = port;
            this.ck2key = new HashMap<Integer, WindowsWatchKey>();
            this.fk2key = new HashMap<FileKey, WindowsWatchKey>();
            this.lastCompletionKey = 0;
        }
        
        @Override
        void wakeup() throws IOException {
            try {
                WindowsNativeDispatcher.PostQueuedCompletionStatus(this.port, 0L);
            }
            catch (final WindowsException ex) {
                throw new IOException(ex.getMessage());
            }
        }
        
        @Override
        Object implRegister(final Path path, final Set<? extends WatchEvent.Kind<?>> events, final WatchEvent.Modifier... array) {
            final WindowsPath windowsPath = (WindowsPath)path;
            boolean b = false;
            for (final WatchEvent.Modifier modifier : array) {
                if (modifier == ExtendedWatchEventModifier.FILE_TREE) {
                    b = true;
                }
                else {
                    if (modifier == null) {
                        return new NullPointerException();
                    }
                    if (!(modifier instanceof SensitivityWatchEventModifier)) {
                        return new UnsupportedOperationException("Modifier not supported");
                    }
                }
            }
            long createFile;
            try {
                createFile = WindowsNativeDispatcher.CreateFile(windowsPath.getPathForWin32Calls(), 1, 7, 3, 1107296256);
            }
            catch (final WindowsException ex) {
                return ex.asIOException(windowsPath);
            }
            boolean b2 = false;
            try {
                WindowsFileAttributes attributes;
                try {
                    attributes = WindowsFileAttributes.readAttributes(createFile);
                }
                catch (final WindowsException ex2) {
                    return ex2.asIOException(windowsPath);
                }
                if (!attributes.isDirectory()) {
                    return new NotDirectoryException(windowsPath.getPathForExceptionMessage());
                }
                final FileKey fileKey = new FileKey(attributes.volSerialNumber(), attributes.fileIndexHigh(), attributes.fileIndexLow());
                final WindowsWatchKey windowsWatchKey = this.fk2key.get(fileKey);
                if (windowsWatchKey != null && b == windowsWatchKey.watchSubtree()) {
                    windowsWatchKey.setEvents(events);
                    return windowsWatchKey;
                }
                int n = ++this.lastCompletionKey;
                if (n == 0) {
                    n = ++this.lastCompletionKey;
                }
                try {
                    WindowsNativeDispatcher.CreateIoCompletionPort(createFile, this.port, n);
                }
                catch (final WindowsException ex3) {
                    return new IOException(ex3.getMessage());
                }
                final int n2 = 16420;
                final NativeBuffer nativeBuffer = NativeBuffers.getNativeBuffer(n2);
                final long address = nativeBuffer.address();
                final long n3 = address + n2 - 32L;
                final long n4 = n3 - 4L;
                Poller.UNSAFE.setMemory(n3, 32L, (byte)0);
                try {
                    this.createAndAttachEvent(n3);
                    WindowsNativeDispatcher.ReadDirectoryChangesW(createFile, address, 16384, b, 351, n4, n3);
                }
                catch (final WindowsException ex4) {
                    this.closeAttachedEvent(n3);
                    nativeBuffer.release();
                    return new IOException(ex4.getMessage());
                }
                WindowsWatchKey windowsWatchKey2;
                if (windowsWatchKey == null) {
                    windowsWatchKey2 = new WindowsWatchKey(windowsPath, this.watcher, fileKey).init(createFile, events, b, nativeBuffer, n4, n3, n);
                    this.fk2key.put(fileKey, windowsWatchKey2);
                }
                else {
                    this.ck2key.remove(windowsWatchKey.completionKey());
                    this.releaseResources(windowsWatchKey);
                    windowsWatchKey2 = windowsWatchKey.init(createFile, events, b, nativeBuffer, n4, n3, n);
                }
                this.ck2key.put(n, windowsWatchKey2);
                b2 = true;
                return windowsWatchKey2;
            }
            finally {
                if (!b2) {
                    WindowsNativeDispatcher.CloseHandle(createFile);
                }
            }
        }
        
        private void releaseResources(final WindowsWatchKey windowsWatchKey) {
            if (!windowsWatchKey.isErrorStartingOverlapped()) {
                try {
                    WindowsNativeDispatcher.CancelIo(windowsWatchKey.handle());
                    WindowsNativeDispatcher.GetOverlappedResult(windowsWatchKey.handle(), windowsWatchKey.overlappedAddress());
                }
                catch (final WindowsException ex) {}
            }
            WindowsNativeDispatcher.CloseHandle(windowsWatchKey.handle());
            this.closeAttachedEvent(windowsWatchKey.overlappedAddress());
            windowsWatchKey.buffer().cleaner().clean();
        }
        
        private void createAndAttachEvent(final long n) throws WindowsException {
            Poller.UNSAFE.putAddress(n + Poller.OFFSETOF_HEVENT, WindowsNativeDispatcher.CreateEvent(false, false));
        }
        
        private void closeAttachedEvent(final long n) {
            final long address = Poller.UNSAFE.getAddress(n + Poller.OFFSETOF_HEVENT);
            if (address != 0L && address != -1L) {
                WindowsNativeDispatcher.CloseHandle(address);
            }
        }
        
        @Override
        void implCancelKey(final WatchKey watchKey) {
            final WindowsWatchKey windowsWatchKey = (WindowsWatchKey)watchKey;
            if (windowsWatchKey.isValid()) {
                this.fk2key.remove(windowsWatchKey.fileKey());
                this.ck2key.remove(windowsWatchKey.completionKey());
                windowsWatchKey.invalidate();
            }
        }
        
        @Override
        void implCloseAll() {
            this.ck2key.values().forEach(WindowsWatchKey::invalidate);
            this.fk2key.clear();
            this.ck2key.clear();
            WindowsNativeDispatcher.CloseHandle(this.port);
        }
        
        private WatchEvent.Kind<?> translateActionToEvent(final int n) {
            switch (n) {
                case 3: {
                    return StandardWatchEventKinds.ENTRY_MODIFY;
                }
                case 1:
                case 5: {
                    return StandardWatchEventKinds.ENTRY_CREATE;
                }
                case 2:
                case 4: {
                    return StandardWatchEventKinds.ENTRY_DELETE;
                }
                default: {
                    return null;
                }
            }
        }
        
        private void processEvents(final WindowsWatchKey windowsWatchKey, final int n) {
            long address = windowsWatchKey.buffer().address();
            int i;
            do {
                final WatchEvent.Kind<?> translateActionToEvent = this.translateActionToEvent(Poller.UNSAFE.getInt(address + 4L));
                if (windowsWatchKey.events().contains(translateActionToEvent)) {
                    final int int1 = Poller.UNSAFE.getInt(address + 8L);
                    if (int1 % 2 != 0) {
                        throw new AssertionError((Object)"FileNameLength is not a multiple of 2");
                    }
                    final char[] array = new char[int1 / 2];
                    Poller.UNSAFE.copyMemory(null, address + 12L, array, Unsafe.ARRAY_CHAR_BASE_OFFSET, int1);
                    windowsWatchKey.signalEvent(translateActionToEvent, WindowsPath.createFromNormalizedPath(this.fs, new String(array)));
                }
                i = Poller.UNSAFE.getInt(address + 0L);
                address += i;
            } while (i != 0);
        }
        
        @Override
        public void run() {
            while (true) {
                WindowsNativeDispatcher.CompletionStatus getQueuedCompletionStatus;
                try {
                    getQueuedCompletionStatus = WindowsNativeDispatcher.GetQueuedCompletionStatus(this.port);
                }
                catch (final WindowsException ex) {
                    ex.printStackTrace();
                    return;
                }
                if (getQueuedCompletionStatus.completionKey() == 0L) {
                    if (this.processRequests()) {
                        break;
                    }
                    continue;
                }
                else {
                    final WindowsWatchKey windowsWatchKey = this.ck2key.get((int)getQueuedCompletionStatus.completionKey());
                    if (windowsWatchKey == null) {
                        continue;
                    }
                    boolean b = false;
                    final int error = getQueuedCompletionStatus.error();
                    final int bytesTransferred = getQueuedCompletionStatus.bytesTransferred();
                    if (error == 1022) {
                        windowsWatchKey.signalEvent(StandardWatchEventKinds.OVERFLOW, null);
                    }
                    else if (error != 0 && error != 234) {
                        b = true;
                    }
                    else {
                        if (bytesTransferred > 0) {
                            this.processEvents(windowsWatchKey, bytesTransferred);
                        }
                        else if (error == 0) {
                            windowsWatchKey.signalEvent(StandardWatchEventKinds.OVERFLOW, null);
                        }
                        try {
                            WindowsNativeDispatcher.ReadDirectoryChangesW(windowsWatchKey.handle(), windowsWatchKey.buffer().address(), 16384, windowsWatchKey.watchSubtree(), 351, windowsWatchKey.countAddress(), windowsWatchKey.overlappedAddress());
                        }
                        catch (final WindowsException ex2) {
                            b = true;
                            windowsWatchKey.setErrorStartingOverlapped(true);
                        }
                    }
                    if (!b) {
                        continue;
                    }
                    this.implCancelKey(windowsWatchKey);
                    windowsWatchKey.signal();
                }
            }
        }
        
        static {
            UNSAFE = Unsafe.getUnsafe();
            OFFSETOF_HEVENT = (short)((Poller.UNSAFE.addressSize() == 4) ? 16 : 24);
        }
    }
}
