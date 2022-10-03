package sun.nio.ch;

import java.io.Closeable;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.nio.channels.ShutdownChannelGroupException;
import java.util.concurrent.RejectedExecutionException;
import java.io.FileDescriptor;
import java.nio.channels.Channel;
import java.util.Iterator;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import sun.misc.Unsafe;

class Iocp extends AsynchronousChannelGroupImpl
{
    private static final Unsafe unsafe;
    private static final long INVALID_HANDLE_VALUE = -1L;
    private static final boolean supportsThreadAgnosticIo;
    private final ReadWriteLock keyToChannelLock;
    private final Map<Integer, OverlappedChannel> keyToChannel;
    private int nextCompletionKey;
    private final long port;
    private boolean closed;
    private final Set<Long> staleIoSet;
    
    Iocp(final AsynchronousChannelProvider asynchronousChannelProvider, final ThreadPool threadPool) throws IOException {
        super(asynchronousChannelProvider, threadPool);
        this.keyToChannelLock = new ReentrantReadWriteLock();
        this.keyToChannel = new HashMap<Integer, OverlappedChannel>();
        this.staleIoSet = new HashSet<Long>();
        this.port = createIoCompletionPort(-1L, 0L, 0, this.fixedThreadCount());
        this.nextCompletionKey = 1;
    }
    
    Iocp start() {
        this.startThreads(new EventHandlerTask());
        return this;
    }
    
    static boolean supportsThreadAgnosticIo() {
        return Iocp.supportsThreadAgnosticIo;
    }
    
    void implClose() {
        synchronized (this) {
            if (this.closed) {
                return;
            }
            this.closed = true;
        }
        close0(this.port);
        synchronized (this.staleIoSet) {
            final Iterator<Long> iterator = this.staleIoSet.iterator();
            while (iterator.hasNext()) {
                Iocp.unsafe.freeMemory(iterator.next());
            }
            this.staleIoSet.clear();
        }
    }
    
    @Override
    boolean isEmpty() {
        this.keyToChannelLock.writeLock().lock();
        try {
            return this.keyToChannel.isEmpty();
        }
        finally {
            this.keyToChannelLock.writeLock().unlock();
        }
    }
    
    @Override
    final Object attachForeignChannel(final Channel channel, final FileDescriptor fileDescriptor) throws IOException {
        return this.associate(new OverlappedChannel() {
            @Override
            public <V, A> PendingFuture<V, A> getByOverlapped(final long n) {
                return null;
            }
            
            @Override
            public void close() throws IOException {
                channel.close();
            }
        }, 0L);
    }
    
    @Override
    final void detachForeignChannel(final Object o) {
        this.disassociate((int)o);
    }
    
    @Override
    void closeAllChannels() {
        final OverlappedChannel[] array = new OverlappedChannel[32];
        int i;
        do {
            this.keyToChannelLock.writeLock().lock();
            i = 0;
            try {
                final Iterator<Integer> iterator = this.keyToChannel.keySet().iterator();
                while (iterator.hasNext()) {
                    array[i++] = this.keyToChannel.get(iterator.next());
                    if (i >= 32) {
                        break;
                    }
                }
            }
            finally {
                this.keyToChannelLock.writeLock().unlock();
            }
            for (int j = 0; j < i; ++j) {
                try {
                    array[j].close();
                }
                catch (final IOException ex) {}
            }
        } while (i > 0);
    }
    
    private void wakeup() {
        try {
            postQueuedCompletionStatus(this.port, 0);
        }
        catch (final IOException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
    @Override
    void executeOnHandlerTask(final Runnable runnable) {
        synchronized (this) {
            if (this.closed) {
                throw new RejectedExecutionException();
            }
            this.offerTask(runnable);
            this.wakeup();
        }
    }
    
    @Override
    void shutdownHandlerTasks() {
        int threadCount = this.threadCount();
        while (threadCount-- > 0) {
            this.wakeup();
        }
    }
    
    int associate(final OverlappedChannel overlappedChannel, final long n) throws IOException {
        this.keyToChannelLock.writeLock().lock();
        int n2;
        try {
            if (this.isShutdown()) {
                throw new ShutdownChannelGroupException();
            }
            do {
                n2 = this.nextCompletionKey++;
            } while (n2 == 0 || this.keyToChannel.containsKey(n2));
            if (n != 0L) {
                createIoCompletionPort(n, this.port, n2, 0);
            }
            this.keyToChannel.put(n2, overlappedChannel);
        }
        finally {
            this.keyToChannelLock.writeLock().unlock();
        }
        return n2;
    }
    
    void disassociate(final int n) {
        boolean b = false;
        this.keyToChannelLock.writeLock().lock();
        try {
            this.keyToChannel.remove(n);
            if (this.keyToChannel.isEmpty()) {
                b = true;
            }
        }
        finally {
            this.keyToChannelLock.writeLock().unlock();
        }
        if (b && this.isShutdown()) {
            try {
                this.shutdownNow();
            }
            catch (final IOException ex) {}
        }
    }
    
    void makeStale(final Long n) {
        synchronized (this.staleIoSet) {
            this.staleIoSet.add(n);
        }
    }
    
    private void checkIfStale(final long n) {
        synchronized (this.staleIoSet) {
            if (this.staleIoSet.remove(n)) {
                Iocp.unsafe.freeMemory(n);
            }
        }
    }
    
    private static IOException translateErrorToIOException(final int n) {
        String s = getErrorMessage(n);
        if (s == null) {
            s = "Unknown error: 0x0" + Integer.toHexString(n);
        }
        return new IOException(s);
    }
    
    private static native void initIDs();
    
    private static native long createIoCompletionPort(final long p0, final long p1, final int p2, final int p3) throws IOException;
    
    private static native void close0(final long p0);
    
    private static native void getQueuedCompletionStatus(final long p0, final CompletionStatus p1) throws IOException;
    
    private static native void postQueuedCompletionStatus(final long p0, final int p1) throws IOException;
    
    private static native String getErrorMessage(final int p0);
    
    static {
        unsafe = Unsafe.getUnsafe();
        IOUtil.load();
        initIDs();
        supportsThreadAgnosticIo = (Integer.parseInt(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.version")).split("\\.")[0]) >= 6);
    }
    
    private class EventHandlerTask implements Runnable
    {
        @Override
        public void run() {
            final Invoker.GroupAndInvokeCount groupAndInvokeCount = Invoker.getGroupAndInvokeCount();
            final boolean b = groupAndInvokeCount != null;
            final CompletionStatus completionStatus = new CompletionStatus();
            boolean b2 = false;
            try {
                while (true) {
                    if (groupAndInvokeCount != null) {
                        groupAndInvokeCount.resetInvokeCount();
                    }
                    b2 = false;
                    try {
                        getQueuedCompletionStatus(Iocp.this.port, completionStatus);
                    }
                    catch (final IOException ex) {
                        ex.printStackTrace();
                        return;
                    }
                    if (completionStatus.completionKey() == 0 && completionStatus.overlapped() == 0L) {
                        final Runnable pollTask = Iocp.this.pollTask();
                        if (pollTask == null) {
                            break;
                        }
                        b2 = true;
                        pollTask.run();
                    }
                    else {
                        OverlappedChannel overlappedChannel = null;
                        Iocp.this.keyToChannelLock.readLock().lock();
                        try {
                            overlappedChannel = Iocp.this.keyToChannel.get(completionStatus.completionKey());
                            if (overlappedChannel == null) {
                                Iocp.this.checkIfStale(completionStatus.overlapped());
                                continue;
                            }
                        }
                        finally {
                            Iocp.this.keyToChannelLock.readLock().unlock();
                        }
                        final PendingFuture<Object, Object> byOverlapped = overlappedChannel.getByOverlapped(completionStatus.overlapped());
                        if (byOverlapped == null) {
                            Iocp.this.checkIfStale(completionStatus.overlapped());
                        }
                        else {
                            synchronized (byOverlapped) {
                                if (byOverlapped.isDone()) {
                                    continue;
                                }
                            }
                            final int error = completionStatus.error();
                            final ResultHandler resultHandler = (ResultHandler)byOverlapped.getContext();
                            b2 = true;
                            if (error == 0) {
                                resultHandler.completed(completionStatus.bytesTransferred(), b);
                            }
                            else {
                                resultHandler.failed(error, translateErrorToIOException(error));
                            }
                        }
                    }
                }
            }
            finally {
                if (Iocp.this.threadExit(this, b2) == 0 && Iocp.this.isShutdown()) {
                    Iocp.this.implClose();
                }
            }
        }
    }
    
    private static class CompletionStatus
    {
        private int error;
        private int bytesTransferred;
        private int completionKey;
        private long overlapped;
        
        int error() {
            return this.error;
        }
        
        int bytesTransferred() {
            return this.bytesTransferred;
        }
        
        int completionKey() {
            return this.completionKey;
        }
        
        long overlapped() {
            return this.overlapped;
        }
    }
    
    interface OverlappedChannel extends Closeable
    {
         <V, A> PendingFuture<V, A> getByOverlapped(final long p0);
    }
    
    interface ResultHandler
    {
        void completed(final int p0, final boolean p1);
        
        void failed(final int p0, final IOException p1);
    }
}
