package java.net;

import sun.misc.SharedSecrets;
import java.io.Serializable;
import java.io.IOException;
import sun.misc.JavaIOFileDescriptorAccess;

class DualStackPlainDatagramSocketImpl extends AbstractPlainDatagramSocketImpl
{
    static JavaIOFileDescriptorAccess fdAccess;
    private final boolean exclusiveBind;
    private boolean reuseAddressEmulated;
    private boolean isReuseAddress;
    
    DualStackPlainDatagramSocketImpl(final boolean exclusiveBind) {
        this.exclusiveBind = exclusiveBind;
    }
    
    @Override
    protected void datagramSocketCreate() throws SocketException {
        if (this.fd == null) {
            throw new SocketException("Socket closed");
        }
        DualStackPlainDatagramSocketImpl.fdAccess.set(this.fd, socketCreate(false));
    }
    
    @Override
    protected synchronized void bind0(final int localPort, final InetAddress inetAddress) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (inetAddress == null) {
            throw new NullPointerException("argument address");
        }
        socketBind(checkAndReturnNativeFD, inetAddress, localPort, this.exclusiveBind);
        if (localPort == 0) {
            this.localPort = socketLocalPort(checkAndReturnNativeFD);
        }
        else {
            this.localPort = localPort;
        }
    }
    
    @Override
    protected synchronized int peek(InetAddress address) throws IOException {
        this.checkAndReturnNativeFD();
        if (address == null) {
            throw new NullPointerException("Null address in peek()");
        }
        final DatagramPacket datagramPacket = new DatagramPacket(new byte[1], 1);
        final int peekData = this.peekData(datagramPacket);
        address = datagramPacket.getAddress();
        return peekData;
    }
    
    @Override
    protected synchronized int peekData(final DatagramPacket datagramPacket) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (datagramPacket == null) {
            throw new NullPointerException("packet");
        }
        if (datagramPacket.getData() == null) {
            throw new NullPointerException("packet buffer");
        }
        return socketReceiveOrPeekData(checkAndReturnNativeFD, datagramPacket, this.timeout, this.connected, true);
    }
    
    @Override
    protected synchronized void receive0(final DatagramPacket datagramPacket) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (datagramPacket == null) {
            throw new NullPointerException("packet");
        }
        if (datagramPacket.getData() == null) {
            throw new NullPointerException("packet buffer");
        }
        socketReceiveOrPeekData(checkAndReturnNativeFD, datagramPacket, this.timeout, this.connected, false);
    }
    
    @Override
    protected void send(final DatagramPacket datagramPacket) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (datagramPacket == null) {
            throw new NullPointerException("null packet");
        }
        if (datagramPacket.getAddress() == null || datagramPacket.getData() == null) {
            throw new NullPointerException("null address || null buffer");
        }
        socketSend(checkAndReturnNativeFD, datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength(), datagramPacket.getAddress(), datagramPacket.getPort(), this.connected);
    }
    
    @Override
    protected void connect0(final InetAddress inetAddress, final int n) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (inetAddress == null) {
            throw new NullPointerException("address");
        }
        socketConnect(checkAndReturnNativeFD, inetAddress, n);
    }
    
    @Override
    protected void disconnect0(final int n) {
        if (this.fd == null || !this.fd.valid()) {
            return;
        }
        socketDisconnect(DualStackPlainDatagramSocketImpl.fdAccess.get(this.fd));
    }
    
    @Override
    protected void datagramSocketClose() {
        if (this.fd == null || !this.fd.valid()) {
            return;
        }
        socketClose(DualStackPlainDatagramSocketImpl.fdAccess.get(this.fd));
        DualStackPlainDatagramSocketImpl.fdAccess.set(this.fd, -1);
    }
    
    @Override
    protected void socketSetOption(final int n, final Object o) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        int n2 = 0;
        switch (n) {
            case 3:
            case 4097:
            case 4098: {
                n2 = (int)o;
                break;
            }
            case 4: {
                if (this.exclusiveBind && this.localPort != 0) {
                    this.reuseAddressEmulated = true;
                    this.isReuseAddress = (boolean)o;
                    return;
                }
            }
            case 32: {
                n2 = (((boolean)o) ? 1 : 0);
                break;
            }
            default: {
                throw new SocketException("Option not supported");
            }
        }
        socketSetIntOption(checkAndReturnNativeFD, n, n2);
    }
    
    @Override
    protected Object socketGetOption(final int n) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (n == 15) {
            return socketLocalAddress(checkAndReturnNativeFD);
        }
        if (n == 4 && this.reuseAddressEmulated) {
            return this.isReuseAddress;
        }
        final int socketGetIntOption = socketGetIntOption(checkAndReturnNativeFD, n);
        Serializable s = null;
        switch (n) {
            case 4:
            case 32: {
                s = ((socketGetIntOption == 0) ? Boolean.FALSE : Boolean.TRUE);
                break;
            }
            case 3:
            case 4097:
            case 4098: {
                s = new Integer(socketGetIntOption);
                break;
            }
            default: {
                throw new SocketException("Option not supported");
            }
        }
        return s;
    }
    
    @Override
    protected void join(final InetAddress inetAddress, final NetworkInterface networkInterface) throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    @Override
    protected void leave(final InetAddress inetAddress, final NetworkInterface networkInterface) throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    @Override
    protected void setTimeToLive(final int n) throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    @Override
    protected int getTimeToLive() throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    @Deprecated
    @Override
    protected void setTTL(final byte b) throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    @Deprecated
    @Override
    protected byte getTTL() throws IOException {
        throw new IOException("Method not implemented!");
    }
    
    private int checkAndReturnNativeFD() throws SocketException {
        if (this.fd == null || !this.fd.valid()) {
            throw new SocketException("Socket closed");
        }
        return DualStackPlainDatagramSocketImpl.fdAccess.get(this.fd);
    }
    
    private static native void initIDs();
    
    private static native int socketCreate(final boolean p0);
    
    private static native void socketBind(final int p0, final InetAddress p1, final int p2, final boolean p3) throws SocketException;
    
    private static native void socketConnect(final int p0, final InetAddress p1, final int p2) throws SocketException;
    
    private static native void socketDisconnect(final int p0);
    
    private static native void socketClose(final int p0);
    
    private static native int socketLocalPort(final int p0) throws SocketException;
    
    private static native Object socketLocalAddress(final int p0) throws SocketException;
    
    private static native int socketReceiveOrPeekData(final int p0, final DatagramPacket p1, final int p2, final boolean p3, final boolean p4) throws IOException;
    
    private static native void socketSend(final int p0, final byte[] p1, final int p2, final int p3, final InetAddress p4, final int p5, final boolean p6) throws IOException;
    
    private static native void socketSetIntOption(final int p0, final int p1, final int p2) throws SocketException;
    
    private static native int socketGetIntOption(final int p0, final int p1) throws SocketException;
    
    @Override
    native int dataAvailable();
    
    static {
        DualStackPlainDatagramSocketImpl.fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        initIDs();
    }
}
