package java.net;

import sun.net.ResourceManager;
import java.io.IOException;
import java.io.FileDescriptor;

class TwoStacksPlainSocketImpl extends AbstractPlainSocketImpl
{
    private FileDescriptor fd1;
    private InetAddress anyLocalBoundAddr;
    private int lastfd;
    private final boolean exclusiveBind;
    private boolean isReuseAddress;
    
    public TwoStacksPlainSocketImpl(final boolean exclusiveBind) {
        this.anyLocalBoundAddr = null;
        this.lastfd = -1;
        this.exclusiveBind = exclusiveBind;
    }
    
    public TwoStacksPlainSocketImpl(final FileDescriptor fd, final boolean exclusiveBind) {
        this.anyLocalBoundAddr = null;
        this.lastfd = -1;
        this.fd = fd;
        this.exclusiveBind = exclusiveBind;
    }
    
    @Override
    protected synchronized void create(final boolean b) throws IOException {
        this.fd1 = new FileDescriptor();
        try {
            super.create(b);
        }
        catch (final IOException ex) {
            this.fd1 = null;
            throw ex;
        }
    }
    
    @Override
    protected synchronized void bind(final InetAddress anyLocalBoundAddr, final int n) throws IOException {
        super.bind(anyLocalBoundAddr, n);
        if (anyLocalBoundAddr.isAnyLocalAddress()) {
            this.anyLocalBoundAddr = anyLocalBoundAddr;
        }
    }
    
    @Override
    public Object getOption(final int n) throws SocketException {
        if (this.isClosedOrPending()) {
            throw new SocketException("Socket Closed");
        }
        if (n == 15) {
            if (this.fd != null && this.fd1 != null) {
                return this.anyLocalBoundAddr;
            }
            final InetAddressContainer inetAddressContainer = new InetAddressContainer();
            this.socketGetOption(n, inetAddressContainer);
            return inetAddressContainer.addr;
        }
        else {
            if (n == 4 && this.exclusiveBind) {
                return this.isReuseAddress;
            }
            return super.getOption(n);
        }
    }
    
    @Override
    void socketBind(final InetAddress inetAddress, final int n) throws IOException {
        this.socketBind(inetAddress, n, this.exclusiveBind);
    }
    
    @Override
    void socketSetOption(final int n, final boolean isReuseAddress, final Object o) throws SocketException {
        if (n == 4 && this.exclusiveBind) {
            this.isReuseAddress = isReuseAddress;
        }
        else {
            this.socketNativeSetOption(n, isReuseAddress, o);
        }
    }
    
    @Override
    protected void close() throws IOException {
        synchronized (this.fdLock) {
            if (this.fd != null || this.fd1 != null) {
                if (!this.stream) {
                    ResourceManager.afterUdpClose();
                }
                if (this.fdUseCount == 0) {
                    if (this.closePending) {
                        return;
                    }
                    this.closePending = true;
                    this.socketClose();
                    this.fd = null;
                    this.fd1 = null;
                }
                else if (!this.closePending) {
                    this.closePending = true;
                    --this.fdUseCount;
                    this.socketClose();
                }
            }
        }
    }
    
    @Override
    void reset() throws IOException {
        if (this.fd != null || this.fd1 != null) {
            this.socketClose();
        }
        this.fd = null;
        this.fd1 = null;
        super.reset();
    }
    
    @Override
    public boolean isClosedOrPending() {
        synchronized (this.fdLock) {
            return this.closePending || (this.fd == null && this.fd1 == null);
        }
    }
    
    static native void initProto();
    
    @Override
    native void socketCreate(final boolean p0) throws IOException;
    
    @Override
    native void socketConnect(final InetAddress p0, final int p1, final int p2) throws IOException;
    
    native void socketBind(final InetAddress p0, final int p1, final boolean p2) throws IOException;
    
    @Override
    native void socketListen(final int p0) throws IOException;
    
    @Override
    native void socketAccept(final SocketImpl p0) throws IOException;
    
    @Override
    native int socketAvailable() throws IOException;
    
    @Override
    native void socketClose0(final boolean p0) throws IOException;
    
    @Override
    native void socketShutdown(final int p0) throws IOException;
    
    native void socketNativeSetOption(final int p0, final boolean p1, final Object p2) throws SocketException;
    
    @Override
    native int socketGetOption(final int p0, final Object p1) throws SocketException;
    
    @Override
    native void socketSendUrgentData(final int p0) throws IOException;
    
    static {
        initProto();
    }
}
