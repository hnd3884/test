package java.net;

import sun.misc.SharedSecrets;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.misc.JavaIOFileDescriptorAccess;

class DualStackPlainSocketImpl extends AbstractPlainSocketImpl
{
    static JavaIOFileDescriptorAccess fdAccess;
    private final boolean exclusiveBind;
    private boolean isReuseAddress;
    static final int WOULDBLOCK = -2;
    
    public DualStackPlainSocketImpl(final boolean exclusiveBind) {
        this.exclusiveBind = exclusiveBind;
    }
    
    public DualStackPlainSocketImpl(final FileDescriptor fd, final boolean exclusiveBind) {
        this.fd = fd;
        this.exclusiveBind = exclusiveBind;
    }
    
    @Override
    void socketCreate(final boolean b) throws IOException {
        if (this.fd == null) {
            throw new SocketException("Socket closed");
        }
        DualStackPlainSocketImpl.fdAccess.set(this.fd, socket0(b, false));
    }
    
    @Override
    void socketConnect(final InetAddress inetAddress, final int n, final int n2) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (inetAddress == null) {
            throw new NullPointerException("inet address argument is null.");
        }
        if (n2 <= 0) {
            connect0(checkAndReturnNativeFD, inetAddress, n);
        }
        else {
            configureBlocking(checkAndReturnNativeFD, false);
            try {
                if (connect0(checkAndReturnNativeFD, inetAddress, n) == -2) {
                    waitForConnect(checkAndReturnNativeFD, n2);
                }
            }
            finally {
                configureBlocking(checkAndReturnNativeFD, true);
            }
        }
        if (this.localport == 0) {
            this.localport = localPort0(checkAndReturnNativeFD);
        }
    }
    
    @Override
    void socketBind(final InetAddress address, final int localport) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (address == null) {
            throw new NullPointerException("inet address argument is null.");
        }
        bind0(checkAndReturnNativeFD, address, localport, this.exclusiveBind);
        if (localport == 0) {
            this.localport = localPort0(checkAndReturnNativeFD);
        }
        else {
            this.localport = localport;
        }
        this.address = address;
    }
    
    @Override
    void socketListen(final int n) throws IOException {
        listen0(this.checkAndReturnNativeFD(), n);
    }
    
    @Override
    void socketAccept(final SocketImpl socketImpl) throws IOException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (socketImpl == null) {
            throw new NullPointerException("socket is null");
        }
        int n = -1;
        final InetSocketAddress[] array = { null };
        if (this.timeout <= 0) {
            n = accept0(checkAndReturnNativeFD, array);
        }
        else {
            configureBlocking(checkAndReturnNativeFD, false);
            try {
                waitForNewConnection(checkAndReturnNativeFD, this.timeout);
                n = accept0(checkAndReturnNativeFD, array);
                if (n != -1) {
                    configureBlocking(n, true);
                }
            }
            finally {
                configureBlocking(checkAndReturnNativeFD, true);
            }
        }
        DualStackPlainSocketImpl.fdAccess.set(socketImpl.fd, n);
        final InetSocketAddress inetSocketAddress = array[0];
        socketImpl.port = inetSocketAddress.getPort();
        socketImpl.address = inetSocketAddress.getAddress();
        socketImpl.localport = this.localport;
    }
    
    @Override
    int socketAvailable() throws IOException {
        return available0(this.checkAndReturnNativeFD());
    }
    
    @Override
    void socketClose0(final boolean b) throws IOException {
        if (this.fd == null) {
            throw new SocketException("Socket closed");
        }
        if (!this.fd.valid()) {
            return;
        }
        final int value = DualStackPlainSocketImpl.fdAccess.get(this.fd);
        DualStackPlainSocketImpl.fdAccess.set(this.fd, -1);
        close0(value);
    }
    
    @Override
    void socketShutdown(final int n) throws IOException {
        shutdown0(this.checkAndReturnNativeFD(), n);
    }
    
    @Override
    void socketSetOption(final int n, final boolean isReuseAddress, final Object o) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (n == 4102) {
            return;
        }
        int n2 = 0;
        switch (n) {
            case 4: {
                if (this.exclusiveBind) {
                    this.isReuseAddress = isReuseAddress;
                    return;
                }
            }
            case 1:
            case 8:
            case 4099: {
                n2 = (isReuseAddress ? 1 : 0);
                break;
            }
            case 3:
            case 4097:
            case 4098: {
                n2 = (int)o;
                break;
            }
            case 128: {
                if (isReuseAddress) {
                    n2 = (int)o;
                    break;
                }
                n2 = -1;
                break;
            }
            default: {
                throw new SocketException("Option not supported");
            }
        }
        setIntOption(checkAndReturnNativeFD, n, n2);
    }
    
    @Override
    int socketGetOption(final int n, final Object o) throws SocketException {
        final int checkAndReturnNativeFD = this.checkAndReturnNativeFD();
        if (n == 15) {
            localAddress(checkAndReturnNativeFD, (InetAddressContainer)o);
            return 0;
        }
        if (n == 4 && this.exclusiveBind) {
            return this.isReuseAddress ? 1 : -1;
        }
        final int intOption = getIntOption(checkAndReturnNativeFD, n);
        switch (n) {
            case 1:
            case 4:
            case 8:
            case 4099: {
                return (intOption == 0) ? -1 : 1;
            }
            default: {
                return intOption;
            }
        }
    }
    
    @Override
    void socketSendUrgentData(final int n) throws IOException {
        sendOOB(this.checkAndReturnNativeFD(), n);
    }
    
    private int checkAndReturnNativeFD() throws SocketException {
        if (this.fd == null || !this.fd.valid()) {
            throw new SocketException("Socket closed");
        }
        return DualStackPlainSocketImpl.fdAccess.get(this.fd);
    }
    
    static native void initIDs();
    
    static native int socket0(final boolean p0, final boolean p1) throws IOException;
    
    static native void bind0(final int p0, final InetAddress p1, final int p2, final boolean p3) throws IOException;
    
    static native int connect0(final int p0, final InetAddress p1, final int p2) throws IOException;
    
    static native void waitForConnect(final int p0, final int p1) throws IOException;
    
    static native int localPort0(final int p0) throws IOException;
    
    static native void localAddress(final int p0, final InetAddressContainer p1) throws SocketException;
    
    static native void listen0(final int p0, final int p1) throws IOException;
    
    static native int accept0(final int p0, final InetSocketAddress[] p1) throws IOException;
    
    static native void waitForNewConnection(final int p0, final int p1) throws IOException;
    
    static native int available0(final int p0) throws IOException;
    
    static native void close0(final int p0) throws IOException;
    
    static native void shutdown0(final int p0, final int p1) throws IOException;
    
    static native void setIntOption(final int p0, final int p1, final int p2) throws SocketException;
    
    static native int getIntOption(final int p0, final int p1) throws SocketException;
    
    static native void sendOOB(final int p0, final int p1) throws IOException;
    
    static native void configureBlocking(final int p0, final boolean p1) throws IOException;
    
    static {
        DualStackPlainSocketImpl.fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
        initIDs();
    }
}
