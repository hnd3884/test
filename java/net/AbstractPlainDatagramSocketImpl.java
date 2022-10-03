package java.net;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.net.ResourceManager;

abstract class AbstractPlainDatagramSocketImpl extends DatagramSocketImpl
{
    int timeout;
    boolean connected;
    private int trafficClass;
    protected InetAddress connectedAddress;
    private int connectedPort;
    private static final String os;
    private static final boolean connectDisabled;
    
    AbstractPlainDatagramSocketImpl() {
        this.timeout = 0;
        this.connected = false;
        this.trafficClass = 0;
        this.connectedAddress = null;
        this.connectedPort = -1;
    }
    
    @Override
    protected synchronized void create() throws SocketException {
        ResourceManager.beforeUdpCreate();
        this.fd = new FileDescriptor();
        try {
            this.datagramSocketCreate();
        }
        catch (final SocketException ex) {
            ResourceManager.afterUdpClose();
            this.fd = null;
            throw ex;
        }
    }
    
    @Override
    protected synchronized void bind(final int n, final InetAddress inetAddress) throws SocketException {
        this.bind0(n, inetAddress);
    }
    
    protected abstract void bind0(final int p0, final InetAddress p1) throws SocketException;
    
    @Override
    protected abstract void send(final DatagramPacket p0) throws IOException;
    
    @Override
    protected void connect(final InetAddress connectedAddress, final int connectedPort) throws SocketException {
        this.connect0(connectedAddress, connectedPort);
        this.connectedAddress = connectedAddress;
        this.connectedPort = connectedPort;
        this.connected = true;
    }
    
    @Override
    protected void disconnect() {
        this.disconnect0(this.connectedAddress.holder().getFamily());
        this.connected = false;
        this.connectedAddress = null;
        this.connectedPort = -1;
    }
    
    @Override
    protected abstract int peek(final InetAddress p0) throws IOException;
    
    @Override
    protected abstract int peekData(final DatagramPacket p0) throws IOException;
    
    @Override
    protected synchronized void receive(final DatagramPacket datagramPacket) throws IOException {
        this.receive0(datagramPacket);
    }
    
    protected abstract void receive0(final DatagramPacket p0) throws IOException;
    
    @Override
    protected abstract void setTimeToLive(final int p0) throws IOException;
    
    @Override
    protected abstract int getTimeToLive() throws IOException;
    
    @Deprecated
    @Override
    protected abstract void setTTL(final byte p0) throws IOException;
    
    @Deprecated
    @Override
    protected abstract byte getTTL() throws IOException;
    
    @Override
    protected void join(final InetAddress inetAddress) throws IOException {
        this.join(inetAddress, null);
    }
    
    @Override
    protected void leave(final InetAddress inetAddress) throws IOException {
        this.leave(inetAddress, null);
    }
    
    @Override
    protected void joinGroup(final SocketAddress socketAddress, final NetworkInterface networkInterface) throws IOException {
        if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        this.join(((InetSocketAddress)socketAddress).getAddress(), networkInterface);
    }
    
    protected abstract void join(final InetAddress p0, final NetworkInterface p1) throws IOException;
    
    @Override
    protected void leaveGroup(final SocketAddress socketAddress, final NetworkInterface networkInterface) throws IOException {
        if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        this.leave(((InetSocketAddress)socketAddress).getAddress(), networkInterface);
    }
    
    protected abstract void leave(final InetAddress p0, final NetworkInterface p1) throws IOException;
    
    @Override
    protected void close() {
        if (this.fd != null) {
            this.datagramSocketClose();
            ResourceManager.afterUdpClose();
            this.fd = null;
        }
    }
    
    protected boolean isClosed() {
        return this.fd == null;
    }
    
    @Override
    protected void finalize() {
        this.close();
    }
    
    @Override
    public void setOption(final int n, final Object o) throws SocketException {
        if (this.isClosed()) {
            throw new SocketException("Socket Closed");
        }
        switch (n) {
            case 4102: {
                if (o == null || !(o instanceof Integer)) {
                    throw new SocketException("bad argument for SO_TIMEOUT");
                }
                final int intValue = (int)o;
                if (intValue < 0) {
                    throw new IllegalArgumentException("timeout < 0");
                }
                this.timeout = intValue;
                return;
            }
            case 3: {
                if (o == null || !(o instanceof Integer)) {
                    throw new SocketException("bad argument for IP_TOS");
                }
                this.trafficClass = (int)o;
                break;
            }
            case 4: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad argument for SO_REUSEADDR");
                }
                break;
            }
            case 32: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad argument for SO_BROADCAST");
                }
                break;
            }
            case 15: {
                throw new SocketException("Cannot re-bind Socket");
            }
            case 4097:
            case 4098: {
                if (o == null || !(o instanceof Integer) || (int)o < 0) {
                    throw new SocketException("bad argument for SO_SNDBUF or SO_RCVBUF");
                }
                break;
            }
            case 16: {
                if (o == null || !(o instanceof InetAddress)) {
                    throw new SocketException("bad argument for IP_MULTICAST_IF");
                }
                break;
            }
            case 31: {
                if (o == null || !(o instanceof NetworkInterface)) {
                    throw new SocketException("bad argument for IP_MULTICAST_IF2");
                }
                break;
            }
            case 18: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad argument for IP_MULTICAST_LOOP");
                }
                break;
            }
            default: {
                throw new SocketException("invalid option: " + n);
            }
        }
        this.socketSetOption(n, o);
    }
    
    @Override
    public Object getOption(final int n) throws SocketException {
        if (this.isClosed()) {
            throw new SocketException("Socket Closed");
        }
        Object o = null;
        switch (n) {
            case 4102: {
                o = new Integer(this.timeout);
                break;
            }
            case 3: {
                o = this.socketGetOption(n);
                if ((int)o == -1) {
                    o = new Integer(this.trafficClass);
                    break;
                }
                break;
            }
            case 4:
            case 15:
            case 16:
            case 18:
            case 31:
            case 32:
            case 4097:
            case 4098: {
                o = this.socketGetOption(n);
                break;
            }
            default: {
                throw new SocketException("invalid option: " + n);
            }
        }
        return o;
    }
    
    protected abstract void datagramSocketCreate() throws SocketException;
    
    protected abstract void datagramSocketClose();
    
    protected abstract void socketSetOption(final int p0, final Object p1) throws SocketException;
    
    protected abstract Object socketGetOption(final int p0) throws SocketException;
    
    protected abstract void connect0(final InetAddress p0, final int p1) throws SocketException;
    
    protected abstract void disconnect0(final int p0);
    
    protected boolean nativeConnectDisabled() {
        return AbstractPlainDatagramSocketImpl.connectDisabled;
    }
    
    @Override
    abstract int dataAvailable();
    
    static {
        os = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("os.name"));
        connectDisabled = AbstractPlainDatagramSocketImpl.os.contains("OS X");
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                return null;
            }
        });
    }
}
