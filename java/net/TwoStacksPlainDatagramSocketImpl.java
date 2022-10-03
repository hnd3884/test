package java.net;

import sun.net.ResourceManager;
import java.io.IOException;
import java.io.FileDescriptor;

class TwoStacksPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl
{
    private FileDescriptor fd1;
    private InetAddress anyLocalBoundAddr;
    private int fduse;
    private int lastfd;
    private final boolean exclusiveBind;
    private boolean reuseAddressEmulated;
    private boolean isReuseAddress;
    
    TwoStacksPlainDatagramSocketImpl(final boolean exclusiveBind) {
        this.anyLocalBoundAddr = null;
        this.fduse = -1;
        this.lastfd = -1;
        this.exclusiveBind = exclusiveBind;
    }
    
    @Override
    protected synchronized void create() throws SocketException {
        this.fd1 = new FileDescriptor();
        try {
            super.create();
        }
        catch (final SocketException ex) {
            this.fd1 = null;
            throw ex;
        }
    }
    
    @Override
    protected synchronized void bind(final int n, final InetAddress anyLocalBoundAddr) throws SocketException {
        super.bind(n, anyLocalBoundAddr);
        if (anyLocalBoundAddr.isAnyLocalAddress()) {
            this.anyLocalBoundAddr = anyLocalBoundAddr;
        }
    }
    
    @Override
    protected synchronized void bind0(final int n, final InetAddress inetAddress) throws SocketException {
        this.bind0(n, inetAddress, this.exclusiveBind);
    }
    
    @Override
    protected synchronized void receive(final DatagramPacket datagramPacket) throws IOException {
        try {
            this.receive0(datagramPacket);
        }
        finally {
            this.fduse = -1;
        }
    }
    
    @Override
    public Object getOption(final int n) throws SocketException {
        if (this.isClosed()) {
            throw new SocketException("Socket Closed");
        }
        if (n == 15) {
            if (this.fd != null && this.fd1 != null && !this.connected) {
                return this.anyLocalBoundAddr;
            }
            return this.socketLocalAddress((this.connectedAddress == null) ? -1 : this.connectedAddress.holder().getFamily());
        }
        else {
            if (n == 4 && this.reuseAddressEmulated) {
                return this.isReuseAddress;
            }
            return super.getOption(n);
        }
    }
    
    @Override
    protected void socketSetOption(final int n, final Object o) throws SocketException {
        if (n == 4 && this.exclusiveBind && this.localPort != 0) {
            this.reuseAddressEmulated = true;
            this.isReuseAddress = (boolean)o;
        }
        else {
            this.socketNativeSetOption(n, o);
        }
    }
    
    @Override
    protected boolean isClosed() {
        return this.fd == null && this.fd1 == null;
    }
    
    @Override
    protected void close() {
        if (this.fd != null || this.fd1 != null) {
            this.datagramSocketClose();
            ResourceManager.afterUdpClose();
            this.fd = null;
            this.fd1 = null;
        }
    }
    
    protected synchronized native void bind0(final int p0, final InetAddress p1, final boolean p2) throws SocketException;
    
    @Override
    protected native void send(final DatagramPacket p0) throws IOException;
    
    @Override
    protected synchronized native int peek(final InetAddress p0) throws IOException;
    
    @Override
    protected synchronized native int peekData(final DatagramPacket p0) throws IOException;
    
    @Override
    protected synchronized native void receive0(final DatagramPacket p0) throws IOException;
    
    @Override
    protected native void setTimeToLive(final int p0) throws IOException;
    
    @Override
    protected native int getTimeToLive() throws IOException;
    
    @Deprecated
    @Override
    protected native void setTTL(final byte p0) throws IOException;
    
    @Deprecated
    @Override
    protected native byte getTTL() throws IOException;
    
    @Override
    protected native void join(final InetAddress p0, final NetworkInterface p1) throws IOException;
    
    @Override
    protected native void leave(final InetAddress p0, final NetworkInterface p1) throws IOException;
    
    @Override
    protected native void datagramSocketCreate() throws SocketException;
    
    @Override
    protected native void datagramSocketClose();
    
    protected native void socketNativeSetOption(final int p0, final Object p1) throws SocketException;
    
    @Override
    protected native Object socketGetOption(final int p0) throws SocketException;
    
    @Override
    protected native void connect0(final InetAddress p0, final int p1) throws SocketException;
    
    protected native Object socketLocalAddress(final int p0) throws SocketException;
    
    @Override
    protected native void disconnect0(final int p0);
    
    @Override
    native int dataAvailable();
    
    private static native void init();
    
    static {
        init();
    }
}
