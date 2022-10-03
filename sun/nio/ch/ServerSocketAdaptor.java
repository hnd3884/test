package sun.nio.ch;

import java.net.StandardSocketOptions;
import java.net.SocketException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.ClosedChannelException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.ServerSocket;

class ServerSocketAdaptor extends ServerSocket
{
    private final ServerSocketChannelImpl ssc;
    private volatile int timeout;
    
    public static ServerSocket create(final ServerSocketChannelImpl serverSocketChannelImpl) {
        try {
            return new ServerSocketAdaptor(serverSocketChannelImpl);
        }
        catch (final IOException ex) {
            throw new Error(ex);
        }
    }
    
    private ServerSocketAdaptor(final ServerSocketChannelImpl ssc) throws IOException {
        this.timeout = 0;
        this.ssc = ssc;
    }
    
    @Override
    public void bind(final SocketAddress socketAddress) throws IOException {
        this.bind(socketAddress, 50);
    }
    
    @Override
    public void bind(SocketAddress socketAddress, final int n) throws IOException {
        if (socketAddress == null) {
            socketAddress = new InetSocketAddress(0);
        }
        try {
            this.ssc.bind(socketAddress, n);
        }
        catch (final Exception ex) {
            Net.translateException(ex);
        }
    }
    
    @Override
    public InetAddress getInetAddress() {
        if (!this.ssc.isBound()) {
            return null;
        }
        return Net.getRevealedLocalAddress(this.ssc.localAddress()).getAddress();
    }
    
    @Override
    public int getLocalPort() {
        if (!this.ssc.isBound()) {
            return -1;
        }
        return Net.asInetSocketAddress(this.ssc.localAddress()).getPort();
    }
    
    @Override
    public Socket accept() throws IOException {
        synchronized (this.ssc.blockingLock()) {
            if (!this.ssc.isBound()) {
                throw new IllegalBlockingModeException();
            }
            try {
                if (this.timeout == 0) {
                    final SocketChannel accept = this.ssc.accept();
                    if (accept == null && !this.ssc.isBlocking()) {
                        throw new IllegalBlockingModeException();
                    }
                    return accept.socket();
                }
                else {
                    if (!this.ssc.isBlocking()) {
                        throw new IllegalBlockingModeException();
                    }
                    this.ssc.configureBlocking(false);
                    try {
                        final SocketChannel accept2;
                        if ((accept2 = this.ssc.accept()) != null) {
                            return accept2.socket();
                        }
                        long n = this.timeout;
                        while (this.ssc.isOpen()) {
                            final long currentTimeMillis = System.currentTimeMillis();
                            final SocketChannel accept3;
                            if (this.ssc.poll(Net.POLLIN, n) > 0 && (accept3 = this.ssc.accept()) != null) {
                                return accept3.socket();
                            }
                            n -= System.currentTimeMillis() - currentTimeMillis;
                            if (n <= 0L) {
                                throw new SocketTimeoutException();
                            }
                        }
                        throw new ClosedChannelException();
                    }
                    finally {
                        try {
                            this.ssc.configureBlocking(true);
                        }
                        catch (final ClosedChannelException ex) {}
                    }
                }
            }
            catch (final Exception ex2) {
                Net.translateException(ex2);
                assert false;
                return null;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.ssc.close();
    }
    
    @Override
    public ServerSocketChannel getChannel() {
        return this.ssc;
    }
    
    @Override
    public boolean isBound() {
        return this.ssc.isBound();
    }
    
    @Override
    public boolean isClosed() {
        return !this.ssc.isOpen();
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        this.timeout = timeout;
    }
    
    @Override
    public int getSoTimeout() throws SocketException {
        return this.timeout;
    }
    
    @Override
    public void setReuseAddress(final boolean b) throws SocketException {
        try {
            this.ssc.setOption(StandardSocketOptions.SO_REUSEADDR, b);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    @Override
    public boolean getReuseAddress() throws SocketException {
        try {
            return this.ssc.getOption(StandardSocketOptions.SO_REUSEADDR);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return false;
        }
    }
    
    @Override
    public String toString() {
        if (!this.isBound()) {
            return "ServerSocket[unbound]";
        }
        return "ServerSocket[addr=" + this.getInetAddress() + ",localport=" + this.getLocalPort() + "]";
    }
    
    @Override
    public void setReceiveBufferSize(final int n) throws SocketException {
        if (n <= 0) {
            throw new IllegalArgumentException("size cannot be 0 or negative");
        }
        try {
            this.ssc.setOption(StandardSocketOptions.SO_RCVBUF, n);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    @Override
    public int getReceiveBufferSize() throws SocketException {
        try {
            return this.ssc.getOption(StandardSocketOptions.SO_RCVBUF);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return -1;
        }
    }
}
