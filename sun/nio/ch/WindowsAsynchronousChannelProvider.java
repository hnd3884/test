package sun.nio.ch;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.IllegalChannelGroupException;
import java.util.concurrent.ExecutorService;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ThreadFactory;
import java.io.IOException;
import java.nio.channels.spi.AsynchronousChannelProvider;

public class WindowsAsynchronousChannelProvider extends AsynchronousChannelProvider
{
    private static volatile Iocp defaultIocp;
    
    private Iocp defaultIocp() throws IOException {
        if (WindowsAsynchronousChannelProvider.defaultIocp == null) {
            synchronized (WindowsAsynchronousChannelProvider.class) {
                if (WindowsAsynchronousChannelProvider.defaultIocp == null) {
                    WindowsAsynchronousChannelProvider.defaultIocp = new Iocp(this, ThreadPool.getDefault()).start();
                }
            }
        }
        return WindowsAsynchronousChannelProvider.defaultIocp;
    }
    
    @Override
    public AsynchronousChannelGroup openAsynchronousChannelGroup(final int n, final ThreadFactory threadFactory) throws IOException {
        return new Iocp(this, ThreadPool.create(n, threadFactory)).start();
    }
    
    @Override
    public AsynchronousChannelGroup openAsynchronousChannelGroup(final ExecutorService executorService, final int n) throws IOException {
        return new Iocp(this, ThreadPool.wrap(executorService, n)).start();
    }
    
    private Iocp toIocp(final AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        if (asynchronousChannelGroup == null) {
            return this.defaultIocp();
        }
        if (!(asynchronousChannelGroup instanceof Iocp)) {
            throw new IllegalChannelGroupException();
        }
        return (Iocp)asynchronousChannelGroup;
    }
    
    @Override
    public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(final AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        return new WindowsAsynchronousServerSocketChannelImpl(this.toIocp(asynchronousChannelGroup));
    }
    
    @Override
    public AsynchronousSocketChannel openAsynchronousSocketChannel(final AsynchronousChannelGroup asynchronousChannelGroup) throws IOException {
        return new WindowsAsynchronousSocketChannelImpl(this.toIocp(asynchronousChannelGroup));
    }
}
