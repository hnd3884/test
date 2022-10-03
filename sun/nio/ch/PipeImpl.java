package sun.nio.ch;

import java.nio.channels.SocketChannel;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.security.SecureRandom;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.nio.channels.spi.SelectorProvider;
import java.util.Random;
import java.nio.channels.Pipe;

class PipeImpl extends Pipe
{
    private static final int NUM_SECRET_BYTES = 16;
    private static final Random RANDOM_NUMBER_GENERATOR;
    private SourceChannel source;
    private SinkChannel sink;
    
    PipeImpl(final SelectorProvider selectorProvider) throws IOException {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new Initializer(selectorProvider));
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getCause();
        }
    }
    
    @Override
    public SourceChannel source() {
        return this.source;
    }
    
    @Override
    public SinkChannel sink() {
        return this.sink;
    }
    
    static {
        RANDOM_NUMBER_GENERATOR = new SecureRandom();
    }
    
    private class Initializer implements PrivilegedExceptionAction<Void>
    {
        private final SelectorProvider sp;
        private IOException ioe;
        
        private Initializer(final SelectorProvider sp) {
            this.ioe = null;
            this.sp = sp;
        }
        
        @Override
        public Void run() throws IOException {
            final LoopbackConnector loopbackConnector = new LoopbackConnector();
            loopbackConnector.run();
            if (this.ioe instanceof ClosedByInterruptException) {
                this.ioe = null;
                final Thread thread = new Thread(loopbackConnector) {
                    @Override
                    public void interrupt() {
                    }
                };
                thread.start();
                while (true) {
                    try {
                        thread.join();
                    }
                    catch (final InterruptedException ex) {
                        continue;
                    }
                    break;
                }
                Thread.currentThread().interrupt();
            }
            if (this.ioe != null) {
                throw new IOException("Unable to establish loopback connection", this.ioe);
            }
            return null;
        }
        
        private class LoopbackConnector implements Runnable
        {
            @Override
            public void run() {
                ServerSocketChannel open = null;
                SocketChannel open2 = null;
                SocketChannel accept = null;
                try {
                    final ByteBuffer allocate = ByteBuffer.allocate(16);
                    final ByteBuffer allocate2 = ByteBuffer.allocate(16);
                    final InetAddress byName = InetAddress.getByName("127.0.0.1");
                    assert byName.isLoopbackAddress();
                    SocketAddress socketAddress = null;
                    while (true) {
                        if (open == null || !open.isOpen()) {
                            open = ServerSocketChannel.open();
                            open.socket().bind(new InetSocketAddress(byName, 0));
                            socketAddress = new InetSocketAddress(byName, open.socket().getLocalPort());
                        }
                        open2 = SocketChannel.open(socketAddress);
                        PipeImpl.RANDOM_NUMBER_GENERATOR.nextBytes(allocate.array());
                        do {
                            open2.write(allocate);
                        } while (allocate.hasRemaining());
                        allocate.rewind();
                        accept = open.accept();
                        do {
                            accept.read(allocate2);
                        } while (allocate2.hasRemaining());
                        allocate2.rewind();
                        if (allocate2.equals(allocate)) {
                            break;
                        }
                        accept.close();
                        open2.close();
                    }
                    PipeImpl.this.source = new SourceChannelImpl(Initializer.this.sp, open2);
                    PipeImpl.this.sink = new SinkChannelImpl(Initializer.this.sp, accept);
                }
                catch (final IOException ex) {
                    try {
                        if (open2 != null) {
                            open2.close();
                        }
                        if (accept != null) {
                            accept.close();
                        }
                    }
                    catch (final IOException ex2) {}
                    Initializer.this.ioe = ex;
                }
                finally {
                    try {
                        if (open != null) {
                            open.close();
                        }
                    }
                    catch (final IOException ex3) {}
                }
            }
        }
    }
}
