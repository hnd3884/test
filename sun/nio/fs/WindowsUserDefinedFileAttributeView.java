package sun.nio.fs;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.OpenOption;
import java.util.Set;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.io.IOException;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import sun.misc.Unsafe;

class WindowsUserDefinedFileAttributeView extends AbstractUserDefinedFileAttributeView
{
    private static final Unsafe unsafe;
    private final WindowsPath file;
    private final boolean followLinks;
    
    private String join(final String s, final String s2) {
        if (s2 == null) {
            throw new NullPointerException("'name' is null");
        }
        return s + ":" + s2;
    }
    
    private String join(final WindowsPath windowsPath, final String s) throws WindowsException {
        return this.join(windowsPath.getPathForWin32Calls(), s);
    }
    
    WindowsUserDefinedFileAttributeView(final WindowsPath file, final boolean followLinks) {
        this.file = file;
        this.followLinks = followLinks;
    }
    
    private List<String> listUsingStreamEnumeration() throws IOException {
        final ArrayList list = new ArrayList();
        try {
            final WindowsNativeDispatcher.FirstStream findFirstStream = WindowsNativeDispatcher.FindFirstStream(this.file.getPathForWin32Calls());
            if (findFirstStream != null) {
                final long handle = findFirstStream.handle();
                try {
                    final String name = findFirstStream.name();
                    if (!name.equals("::$DATA")) {
                        list.add(name.split(":")[1]);
                    }
                    String findNextStream;
                    while ((findNextStream = WindowsNativeDispatcher.FindNextStream(handle)) != null) {
                        list.add(findNextStream.split(":")[1]);
                    }
                }
                finally {
                    WindowsNativeDispatcher.FindClose(handle);
                }
            }
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.file);
        }
        return (List<String>)Collections.unmodifiableList((List<?>)list);
    }
    
    private List<String> listUsingBackupRead() throws IOException {
        long createFile = -1L;
        try {
            int n = 33554432;
            if (!this.followLinks && this.file.getFileSystem().supportsLinks()) {
                n |= 0x200000;
            }
            createFile = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), Integer.MIN_VALUE, 1, 3, n);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.file);
        }
        NativeBuffer nativeBuffer = null;
        final ArrayList list = new ArrayList();
        try {
            nativeBuffer = NativeBuffers.getNativeBuffer(4096);
            final long address = nativeBuffer.address();
            long context = 0L;
            try {
                while (true) {
                    final WindowsNativeDispatcher.BackupResult backupRead = WindowsNativeDispatcher.BackupRead(createFile, address, 20, false, context);
                    context = backupRead.context();
                    if (backupRead.bytesTransferred() == 0) {
                        break;
                    }
                    final int int1 = WindowsUserDefinedFileAttributeView.unsafe.getInt(address + 0L);
                    final long long1 = WindowsUserDefinedFileAttributeView.unsafe.getLong(address + 8L);
                    final int int2 = WindowsUserDefinedFileAttributeView.unsafe.getInt(address + 16L);
                    if (int2 > 0 && WindowsNativeDispatcher.BackupRead(createFile, address, int2, false, context).bytesTransferred() != int2) {
                        break;
                    }
                    if (int1 == 4) {
                        final char[] array = new char[int2 / 2];
                        WindowsUserDefinedFileAttributeView.unsafe.copyMemory(null, address, array, Unsafe.ARRAY_CHAR_BASE_OFFSET, int2);
                        final String[] split = new String(array).split(":");
                        if (split.length == 3) {
                            list.add(split[1]);
                        }
                    }
                    if (int1 == 9) {
                        throw new IOException("Spare blocks not handled");
                    }
                    if (long1 <= 0L) {
                        continue;
                    }
                    WindowsNativeDispatcher.BackupSeek(createFile, long1, context);
                }
            }
            catch (final WindowsException ex2) {
                throw new IOException(ex2.errorString());
            }
            finally {
                if (context != 0L) {
                    try {
                        WindowsNativeDispatcher.BackupRead(createFile, 0L, 0, true, context);
                    }
                    catch (final WindowsException ex3) {}
                }
            }
        }
        finally {
            if (nativeBuffer != null) {
                nativeBuffer.release();
            }
            WindowsNativeDispatcher.CloseHandle(createFile);
        }
        return (List<String>)Collections.unmodifiableList((List<?>)list);
    }
    
    @Override
    public List<String> list() throws IOException {
        if (System.getSecurityManager() != null) {
            this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
        }
        if (this.file.getFileSystem().supportsStreamEnumeration()) {
            return this.listUsingStreamEnumeration();
        }
        return this.listUsingBackupRead();
    }
    
    @Override
    public int size(final String s) throws IOException {
        if (System.getSecurityManager() != null) {
            this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
        }
        FileChannel fileChannel = null;
        try {
            final HashSet set = new HashSet();
            set.add(StandardOpenOption.READ);
            if (!this.followLinks) {
                set.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
            }
            fileChannel = WindowsChannelFactory.newFileChannel(this.join(this.file, s), null, set, 0L);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), s));
        }
        try {
            final long size = fileChannel.size();
            if (size > 2147483647L) {
                throw new ArithmeticException("Stream too large");
            }
            return (int)size;
        }
        finally {
            fileChannel.close();
        }
    }
    
    @Override
    public int read(final String s, final ByteBuffer byteBuffer) throws IOException {
        if (System.getSecurityManager() != null) {
            this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
        }
        FileChannel fileChannel = null;
        try {
            final HashSet set = new HashSet();
            set.add(StandardOpenOption.READ);
            if (!this.followLinks) {
                set.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
            }
            fileChannel = WindowsChannelFactory.newFileChannel(this.join(this.file, s), null, set, 0L);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), s));
        }
        try {
            if (fileChannel.size() > byteBuffer.remaining()) {
                throw new IOException("Stream too large");
            }
            int n = 0;
            while (byteBuffer.hasRemaining()) {
                final int read = fileChannel.read(byteBuffer);
                if (read < 0) {
                    break;
                }
                n += read;
            }
            return n;
        }
        finally {
            fileChannel.close();
        }
    }
    
    @Override
    public int write(final String s, final ByteBuffer byteBuffer) throws IOException {
        if (System.getSecurityManager() != null) {
            this.checkAccess(this.file.getPathForPermissionCheck(), false, true);
        }
        long createFile = -1L;
        try {
            int n = 33554432;
            if (!this.followLinks) {
                n |= 0x200000;
            }
            createFile = WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), Integer.MIN_VALUE, 7, 3, n);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(this.file);
        }
        try {
            final HashSet set = new HashSet();
            if (!this.followLinks) {
                set.add(WindowsChannelFactory.OPEN_REPARSE_POINT);
            }
            set.add(StandardOpenOption.CREATE);
            set.add(StandardOpenOption.WRITE);
            set.add(StandardOpenOption.TRUNCATE_EXISTING);
            FileChannel fileChannel = null;
            try {
                fileChannel = WindowsChannelFactory.newFileChannel(this.join(this.file, s), null, set, 0L);
            }
            catch (final WindowsException ex2) {
                ex2.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), s));
            }
            try {
                final int remaining = byteBuffer.remaining();
                while (byteBuffer.hasRemaining()) {
                    fileChannel.write(byteBuffer);
                }
                return remaining;
            }
            finally {
                fileChannel.close();
            }
        }
        finally {
            WindowsNativeDispatcher.CloseHandle(createFile);
        }
    }
    
    @Override
    public void delete(final String s) throws IOException {
        if (System.getSecurityManager() != null) {
            this.checkAccess(this.file.getPathForPermissionCheck(), false, true);
        }
        final String join = this.join(WindowsLinkSupport.getFinalPath(this.file, this.followLinks), s);
        try {
            WindowsNativeDispatcher.DeleteFile(join);
        }
        catch (final WindowsException ex) {
            ex.rethrowAsIOException(join);
        }
    }
    
    static {
        unsafe = Unsafe.getUnsafe();
    }
}
