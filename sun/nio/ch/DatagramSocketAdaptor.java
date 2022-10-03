package sun.nio.ch;

import java.net.NetworkInterface;
import java.nio.channels.DatagramChannel;
import java.net.StandardSocketOptions;
import java.net.SocketOption;
import java.nio.channels.ClosedChannelException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketAddress;
import java.io.IOException;
import java.net.DatagramSocketImpl;
import java.net.DatagramSocket;

public class DatagramSocketAdaptor extends DatagramSocket
{
    private final DatagramChannelImpl dc;
    private volatile int timeout;
    private static final DatagramSocketImpl dummyDatagramSocket;
    
    private DatagramSocketAdaptor(final DatagramChannelImpl dc) throws IOException {
        super(DatagramSocketAdaptor.dummyDatagramSocket);
        this.timeout = 0;
        this.dc = dc;
    }
    
    public static DatagramSocket create(final DatagramChannelImpl datagramChannelImpl) {
        try {
            return new DatagramSocketAdaptor(datagramChannelImpl);
        }
        catch (final IOException ex) {
            throw new Error(ex);
        }
    }
    
    private void connectInternal(final SocketAddress socketAddress) throws SocketException {
        final int port = Net.asInetSocketAddress(socketAddress).getPort();
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("connect: " + port);
        }
        if (socketAddress == null) {
            throw new IllegalArgumentException("connect: null address");
        }
        if (this.isClosed()) {
            return;
        }
        try {
            this.dc.connect(socketAddress);
        }
        catch (final Exception ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    @Override
    public void bind(SocketAddress socketAddress) throws SocketException {
        try {
            if (socketAddress == null) {
                socketAddress = new InetSocketAddress(0);
            }
            this.dc.bind(socketAddress);
        }
        catch (final Exception ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    @Override
    public void connect(final InetAddress inetAddress, final int n) {
        try {
            this.connectInternal(new InetSocketAddress(inetAddress, n));
        }
        catch (final SocketException ex) {}
    }
    
    @Override
    public void connect(final SocketAddress socketAddress) throws SocketException {
        if (socketAddress == null) {
            throw new IllegalArgumentException("Address can't be null");
        }
        this.connectInternal(socketAddress);
    }
    
    @Override
    public void disconnect() {
        try {
            this.dc.disconnect();
        }
        catch (final IOException ex) {
            throw new Error(ex);
        }
    }
    
    @Override
    public boolean isBound() {
        return this.dc.localAddress() != null;
    }
    
    @Override
    public boolean isConnected() {
        return this.dc.remoteAddress() != null;
    }
    
    @Override
    public InetAddress getInetAddress() {
        return this.isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getAddress() : null;
    }
    
    @Override
    public int getPort() {
        return this.isConnected() ? Net.asInetSocketAddress(this.dc.remoteAddress()).getPort() : -1;
    }
    
    @Override
    public void send(final DatagramPacket datagramPacket) throws IOException {
        synchronized (this.dc.blockingLock()) {
            if (!this.dc.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            try {
                synchronized (datagramPacket) {
                    final ByteBuffer wrap = ByteBuffer.wrap(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                    if (this.dc.isConnected()) {
                        if (datagramPacket.getAddress() == null) {
                            final InetSocketAddress inetSocketAddress = (InetSocketAddress)this.dc.remoteAddress();
                            datagramPacket.setPort(inetSocketAddress.getPort());
                            datagramPacket.setAddress(inetSocketAddress.getAddress());
                            this.dc.write(wrap);
                        }
                        else {
                            this.dc.send(wrap, datagramPacket.getSocketAddress());
                        }
                    }
                    else {
                        this.dc.send(wrap, datagramPacket.getSocketAddress());
                    }
                }
            }
            catch (final IOException ex) {
                Net.translateException(ex);
            }
        }
    }
    
    private SocketAddress receive(final ByteBuffer byteBuffer) throws IOException {
        if (this.timeout == 0) {
            return this.dc.receive(byteBuffer);
        }
        this.dc.configureBlocking(false);
        try {
            final SocketAddress receive;
            if ((receive = this.dc.receive(byteBuffer)) != null) {
                return receive;
            }
            long n = this.timeout;
            while (this.dc.isOpen()) {
                final long currentTimeMillis = System.currentTimeMillis();
                final int poll = this.dc.poll(Net.POLLIN, n);
                final SocketAddress receive2;
                if (poll > 0 && (poll & Net.POLLIN) != 0x0 && (receive2 = this.dc.receive(byteBuffer)) != null) {
                    return receive2;
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
                this.dc.configureBlocking(true);
            }
            catch (final ClosedChannelException ex) {}
        }
    }
    
    @Override
    public void receive(final DatagramPacket datagramPacket) throws IOException {
        synchronized (this.dc.blockingLock()) {
            if (!this.dc.isBlocking()) {
                throw new IllegalBlockingModeException();
            }
            try {
                synchronized (datagramPacket) {
                    final ByteBuffer wrap = ByteBuffer.wrap(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
                    datagramPacket.setSocketAddress(this.receive(wrap));
                    datagramPacket.setLength(wrap.position() - datagramPacket.getOffset());
                }
            }
            catch (final IOException ex) {
                Net.translateException(ex);
            }
        }
    }
    
    @Override
    public InetAddress getLocalAddress() {
        if (this.isClosed()) {
            return null;
        }
        SocketAddress localAddress = this.dc.localAddress();
        if (localAddress == null) {
            localAddress = new InetSocketAddress(0);
        }
        final InetAddress address = ((InetSocketAddress)localAddress).getAddress();
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            try {
                securityManager.checkConnect(address.getHostAddress(), -1);
            }
            catch (final SecurityException ex) {
                return new InetSocketAddress(0).getAddress();
            }
        }
        return address;
    }
    
    @Override
    public int getLocalPort() {
        if (this.isClosed()) {
            return -1;
        }
        try {
            final SocketAddress localAddress = this.dc.getLocalAddress();
            if (localAddress != null) {
                return ((InetSocketAddress)localAddress).getPort();
            }
        }
        catch (final Exception ex) {}
        return 0;
    }
    
    @Override
    public void setSoTimeout(final int timeout) throws SocketException {
        this.timeout = timeout;
    }
    
    @Override
    public int getSoTimeout() throws SocketException {
        return this.timeout;
    }
    
    private void setBooleanOption(final SocketOption<Boolean> socketOption, final boolean b) throws SocketException {
        try {
            this.dc.setOption(socketOption, b);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    private void setIntOption(final SocketOption<Integer> socketOption, final int n) throws SocketException {
        try {
            this.dc.setOption(socketOption, n);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
        }
    }
    
    private boolean getBooleanOption(final SocketOption<Boolean> socketOption) throws SocketException {
        try {
            return this.dc.getOption(socketOption);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return false;
        }
    }
    
    private int getIntOption(final SocketOption<Integer> socketOption) throws SocketException {
        try {
            return this.dc.getOption(socketOption);
        }
        catch (final IOException ex) {
            Net.translateToSocketException(ex);
            return -1;
        }
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
    public void setReuseAddress(final boolean b) throws SocketException {
        this.setBooleanOption(StandardSocketOptions.SO_REUSEADDR, b);
    }
    
    @Override
    public boolean getReuseAddress() throws SocketException {
        return this.getBooleanOption(StandardSocketOptions.SO_REUSEADDR);
    }
    
    @Override
    public void setBroadcast(final boolean b) throws SocketException {
        this.setBooleanOption(StandardSocketOptions.SO_BROADCAST, b);
    }
    
    @Override
    public boolean getBroadcast() throws SocketException {
        return this.getBooleanOption(StandardSocketOptions.SO_BROADCAST);
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
    public void close() {
        try {
            this.dc.close();
        }
        catch (final IOException ex) {
            throw new Error(ex);
        }
    }
    
    @Override
    public boolean isClosed() {
        return !this.dc.isOpen();
    }
    
    @Override
    public DatagramChannel getChannel() {
        return this.dc;
    }
    
    static {
        dummyDatagramSocket = new DatagramSocketImpl() {
            @Override
            protected void create() throws SocketException {
            }
            
            @Override
            protected void bind(final int n, final InetAddress inetAddress) throws SocketException {
            }
            
            @Override
            protected void send(final DatagramPacket datagramPacket) throws IOException {
            }
            
            @Override
            protected int peek(final InetAddress inetAddress) throws IOException {
                return 0;
            }
            
            @Override
            protected int peekData(final DatagramPacket datagramPacket) throws IOException {
                return 0;
            }
            
            @Override
            protected void receive(final DatagramPacket datagramPacket) throws IOException {
            }
            
            @Deprecated
            @Override
            protected void setTTL(final byte b) throws IOException {
            }
            
            @Deprecated
            @Override
            protected byte getTTL() throws IOException {
                return 0;
            }
            
            @Override
            protected void setTimeToLive(final int n) throws IOException {
            }
            
            @Override
            protected int getTimeToLive() throws IOException {
                return 0;
            }
            
            @Override
            protected void join(final InetAddress inetAddress) throws IOException {
            }
            
            @Override
            protected void leave(final InetAddress inetAddress) throws IOException {
            }
            
            @Override
            protected void joinGroup(final SocketAddress socketAddress, final NetworkInterface networkInterface) throws IOException {
            }
            
            @Override
            protected void leaveGroup(final SocketAddress socketAddress, final NetworkInterface networkInterface) throws IOException {
            }
            
            @Override
            protected void close() {
            }
            
            @Override
            public Object getOption(final int n) throws SocketException {
                return null;
            }
            
            @Override
            public void setOption(final int n, final Object o) throws SocketException {
            }
        };
    }
}
