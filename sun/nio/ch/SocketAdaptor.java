package sun.nio.ch;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.io.OutputStream;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.nio.channels.ClosedChannelException;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.net.SocketException;
import java.net.SocketImpl;
import java.io.InputStream;
import java.net.Socket;

class SocketAdaptor extends Socket
{
    private final SocketChannelImpl sc;
    private volatile int timeout;
    private InputStream socketInputStream;
    
    private SocketAdaptor(final SocketChannelImpl sc) throws SocketException {
        super((SocketImpl)null);
        this.timeout = 0;
        this.socketInputStream = null;
        this.sc = sc;
    }
    
    public static Socket create(final SocketChannelImpl socketChannelImpl) {
        try {
            return new SocketAdaptor(socketChannelImpl);
        }
        catch (final SocketException ex) {
            throw new InternalError("Should not reach here");
        }
    }
    
    @Override
    public SocketChannel getChannel() {
        return this.sc;
    }
    
    @Override
    public void connect(final SocketAddress socketAddress) throws IOException {
        this.connect(socketAddress, 0);
    }
    
    @Override
    public void connect(final SocketAddress socketAddress, final int n) throws IOException {
        if (socketAddress == null) {
            throw new IllegalArgumentException("connect: The address can't be null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("connect: timeout can't be negative");
        }
        synchronized (this.sc.blockingLock()) {
            if (!this.sc.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            try {
                if (n == 0) {
                    this.sc.connect(socketAddress);
                    return;
                }
                this.sc.configureBlocking(false);
                try {
                    if (this.sc.connect(socketAddress)) {
                        return;
                    }
                    long n2 = n;
                    while (this.sc.isOpen()) {
                        final long currentTimeMillis = System.currentTimeMillis();
                        if (this.sc.poll(Net.POLLCONN, n2) <= 0 || !this.sc.finishConnect()) {
                            n2 -= System.currentTimeMillis() - currentTimeMillis;
                            if (n2 <= 0L) {
                                try {
                                    this.sc.close();
                                }
                                catch (final IOException ex) {}
                                throw new SocketTimeoutException();
                            }
                            continue;
                        }
                    }
                    throw new ClosedChannelException();
                }
                finally {
                    try {
                        this.sc.configureBlocking(true);
                    }
                    catch (final ClosedChannelException ex2) {}
                }
            }
            catch (final Exception ex3) {
                Net.translateException(ex3, true);
            }
        }
    }
    
    @Override
    public void bind(final SocketAddress socketAddress) throws IOException {
        try {
            this.sc.bind(socketAddress);
        }
        catch (final Exception ex) {
            Net.translateException(ex);
        }
    }
    
    @Override
    public InetAddress getInetAddress() {
        final SocketAddress remoteAddress = this.sc.remoteAddress();
        if (remoteAddress == null) {
            return null;
        }
        return ((InetSocketAddress)remoteAddress).getAddress();
    }
    
    @Override
    public InetAddress getLocalAddress() {
        if (this.sc.isOpen()) {
            final InetSocketAddress localAddress = this.sc.localAddress();
            if (localAddress != null) {
                return Net.getRevealedLocalAddress(localAddress).getAddress();
            }
        }
        return new InetSocketAddress(0).getAddress();
    }
    
    @Override
    public int getPort() {
        final SocketAddress remoteAddress = this.sc.remoteAddress();
        if (remoteAddress == null) {
            return 0;
        }
        return ((InetSocketAddress)remoteAddress).getPort();
    }
    
    @Override
    public int getLocalPort() {
        final InetSocketAddress localAddress = this.sc.localAddress();
        if (localAddress == null) {
            return -1;
        }
        return localAddress.getPort();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.sc.isOpen()) {
            throw new SocketException("Socket is closed");
        }
        if (!this.sc.isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (!this.sc.isInputOpen()) {
            throw new SocketException("Socket input is shutdown");
        }
        if (this.socketInputStream == null) {
            try {
                this.socketInputStream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>)new PrivilegedExceptionAction<InputStream>() {
                    @Override
                    public InputStream run() throws IOException {
                        return new SocketInputStream();
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (IOException)ex.getException();
            }
        }
        return this.socketInputStream;
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (!this.sc.isOpen()) {
            throw new SocketException("Socket is closed");
        }
        if (!this.sc.isConnected()) {
            throw new SocketException("Socket is not connected");
        }
        if (!this.sc.isOutputOpen()) {
            throw new SocketException("Socket output is shutdown");
        }
        OutputStream outputStream;
        try {
            outputStream = AccessController.doPrivileged((PrivilegedExceptionAction<OutputStream>)new PrivilegedExceptionAction<OutputStream>() {
                @Override
                public OutputStream run() throws IOException {
                    return Channels.newOutputStream(SocketAdaptor.this.sc);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        return outputStream;
    }
    
    private void setBooleanOption(final SocketOption<Boolean> socketOption, final boolean b) throws SocketException {
        try {
            this.sc.setOption(socketOption, b);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    private void setIntOption(final SocketOption<Integer> socketOption, final int n) throws SocketException {
        try {
            this.sc.setOption(socketOption, n);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    private boolean getBooleanOption(final SocketOption<Boolean> socketOption) throws SocketException {
        try {
            return this.sc.getOption(socketOption);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return false;
        }
    }
    
    private int getIntOption(final SocketOption<Integer> socketOption) throws SocketException {
        try {
            return this.sc.getOption(socketOption);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return -1;
        }
    }
    
    @Override
    public void setTcpNoDelay(final boolean b) throws SocketException {
        this.setBooleanOption(StandardSocketOptions.TCP_NODELAY, b);
    }
    
    @Override
    public boolean getTcpNoDelay() throws SocketException {
        return this.getBooleanOption(StandardSocketOptions.TCP_NODELAY);
    }
    
    @Override
    public void setSoLinger(final boolean b, int n) throws SocketException {
        if (!b) {
            n = -1;
        }
        this.setIntOption(StandardSocketOptions.SO_LINGER, n);
    }
    
    @Override
    public int getSoLinger() throws SocketException {
        return this.getIntOption(StandardSocketOptions.SO_LINGER);
    }
    
    @Override
    public void sendUrgentData(final int n) throws IOException {
        if (this.sc.sendOutOfBandData((byte)n) == 0) {
            throw new IOException("Socket buffer full");
        }
    }
    
    @Override
    public void setOOBInline(final boolean b) throws SocketException {
        this.setBooleanOption(ExtendedSocketOption.SO_OOBINLINE, b);
    }
    
    @Override
    public boolean getOOBInline() throws SocketException {
        return this.getBooleanOption(ExtendedSocketOption.SO_OOBINLINE);
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout can't be negative");
        }
        this.timeout = timeout;
    }
    
    @Override
    public int getSoTimeout() throws SocketException {
        return this.timeout;
    }
    
    @Override
    public void setSendBufferSize(final int n) throws SocketException {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid send size");
        }
        this.setIntOption(StandardSocketOptions.SO_SNDBUF, n);
    }
    
    @Override
    public int getSendBufferSize() throws SocketException {
        return this.getIntOption(StandardSocketOptions.SO_SNDBUF);
    }
    
    @Override
    public void setReceiveBufferSize(final int n) throws SocketException {
        if (n <= 0) {
            throw new IllegalArgumentException("Invalid receive size");
        }
        this.setIntOption(StandardSocketOptions.SO_RCVBUF, n);
    }
    
    @Override
    public int getReceiveBufferSize() throws SocketException {
        return this.getIntOption(StandardSocketOptions.SO_RCVBUF);
    }
    
    @Override
    public void setKeepAlive(final boolean b) throws SocketException {
        this.setBooleanOption(StandardSocketOptions.SO_KEEPALIVE, b);
    }
    
    @Override
    public boolean getKeepAlive() throws SocketException {
        return this.getBooleanOption(StandardSocketOptions.SO_KEEPALIVE);
    }
    
    @Override
    public void setTrafficClass(final int n) throws SocketException {
        this.setIntOption(StandardSocketOptions.IP_TOS, n);
    }
    
    @Override
    public int getTrafficClass() throws SocketException {
        return this.getIntOption(StandardSocketOptions.IP_TOS);
    }
    
    @Override
    public void setReuseAddress(final boolean b) throws SocketException {
        this.setBooleanOption(StandardSocketOptions.SO_REUSEADDR, b);
    }
    
    @Override
    public boolean getReuseAddress() throws SocketException {
        return this.getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
    }
    
    @Override
    public void close() throws IOException {
        this.sc.close();
    }
    
    @Override
    public void shutdownInput() throws IOException {
        try {
            this.sc.shutdownInput();
        }
        catch (final Exception ex) {
            Net.translateException(ex);
        }
    }
    
    @Override
    public void shutdownOutput() throws IOException {
        try {
            this.sc.shutdownOutput();
        }
        catch (final Exception ex) {
            Net.translateException(ex);
        }
    }
    
    @Override
    public String toString() {
        if (this.sc.isConnected()) {
            return "Socket[addr=" + this.getInetAddress() + ",port=" + this.getPort() + ",localport=" + this.getLocalPort() + "]";
        }
        return "Socket[unconnected]";
    }
    
    @Override
    public boolean isConnected() {
        return this.sc.isConnected();
    }
    
    @Override
    public boolean isBound() {
        return this.sc.localAddress() != null;
    }
    
    @Override
    public boolean isClosed() {
        return !this.sc.isOpen();
    }
    
    @Override
    public boolean isInputShutdown() {
        return !this.sc.isInputOpen();
    }
    
    @Override
    public boolean isOutputShutdown() {
        return !this.sc.isOutputOpen();
    }
    
    private class SocketInputStream extends ChannelInputStream
    {
        private SocketInputStream() {
            super(SocketAdaptor.this.sc);
        }
        
        @Override
        protected int read(final ByteBuffer byteBuffer) throws IOException {
            synchronized (SocketAdaptor.this.sc.blockingLock()) {
                if (!SocketAdaptor.this.sc.isBlocking()) {
                    throw new IllegalBlockingModeException();
                }
                if (SocketAdaptor.this.timeout == 0) {
                    return SocketAdaptor.this.sc.read(byteBuffer);
                }
                SocketAdaptor.this.sc.configureBlocking(false);
                try {
                    final int read;
                    if ((read = SocketAdaptor.this.sc.read(byteBuffer)) != 0) {
                        return read;
                    }
                    long n = SocketAdaptor.this.timeout;
                    while (SocketAdaptor.this.sc.isOpen()) {
                        final long currentTimeMillis = System.currentTimeMillis();
                        final int read2;
                        if (SocketAdaptor.this.sc.poll(Net.POLLIN, n) > 0 && (read2 = SocketAdaptor.this.sc.read(byteBuffer)) != 0) {
                            return read2;
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
                        SocketAdaptor.this.sc.configureBlocking(true);
                    }
                    catch (final ClosedChannelException ex) {}
                }
            }
        }
    }
}
