package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.ConnectionResetException;
import java.io.OutputStream;
import java.io.InputStream;
import sun.net.NetHooks;
import java.io.IOException;
import java.io.FileDescriptor;
import sun.net.ResourceManager;

abstract class AbstractPlainSocketImpl extends SocketImpl
{
    int timeout;
    private int trafficClass;
    private boolean shut_rd;
    private boolean shut_wr;
    private SocketInputStream socketInputStream;
    private SocketOutputStream socketOutputStream;
    protected int fdUseCount;
    protected final Object fdLock;
    protected boolean closePending;
    private int CONNECTION_NOT_RESET;
    private int CONNECTION_RESET_PENDING;
    private int CONNECTION_RESET;
    private int resetState;
    private final Object resetLock;
    protected boolean stream;
    public static final int SHUT_RD = 0;
    public static final int SHUT_WR = 1;
    
    AbstractPlainSocketImpl() {
        this.shut_rd = false;
        this.shut_wr = false;
        this.socketInputStream = null;
        this.socketOutputStream = null;
        this.fdUseCount = 0;
        this.fdLock = new Object();
        this.closePending = false;
        this.CONNECTION_NOT_RESET = 0;
        this.CONNECTION_RESET_PENDING = 1;
        this.CONNECTION_RESET = 2;
        this.resetLock = new Object();
    }
    
    @Override
    protected synchronized void create(final boolean stream) throws IOException {
        Label_0058: {
            if (!(this.stream = stream)) {
                ResourceManager.beforeUdpCreate();
                this.fd = new FileDescriptor();
                try {
                    this.socketCreate(false);
                    break Label_0058;
                }
                catch (final IOException ex) {
                    ResourceManager.afterUdpClose();
                    this.fd = null;
                    throw ex;
                }
            }
            this.fd = new FileDescriptor();
            this.socketCreate(true);
        }
        if (this.socket != null) {
            this.socket.setCreated();
        }
        if (this.serverSocket != null) {
            this.serverSocket.setCreated();
        }
    }
    
    @Override
    protected void connect(final String s, final int port) throws UnknownHostException, IOException {
        boolean b = false;
        try {
            final InetAddress byName = InetAddress.getByName(s);
            this.port = port;
            this.connectToAddress(this.address = byName, port, this.timeout);
            b = true;
        }
        finally {
            if (!b) {
                try {
                    this.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    @Override
    protected void connect(final InetAddress address, final int port) throws IOException {
        this.port = port;
        this.address = address;
        try {
            this.connectToAddress(address, port, this.timeout);
        }
        catch (final IOException ex) {
            this.close();
            throw ex;
        }
    }
    
    @Override
    protected void connect(final SocketAddress socketAddress, final int n) throws IOException {
        boolean b = false;
        try {
            if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
                throw new IllegalArgumentException("unsupported address type");
            }
            final InetSocketAddress inetSocketAddress = (InetSocketAddress)socketAddress;
            if (inetSocketAddress.isUnresolved()) {
                throw new UnknownHostException(inetSocketAddress.getHostName());
            }
            this.port = inetSocketAddress.getPort();
            this.connectToAddress(this.address = inetSocketAddress.getAddress(), this.port, n);
            b = true;
        }
        finally {
            if (!b) {
                try {
                    this.close();
                }
                catch (final IOException ex) {}
            }
        }
    }
    
    private void connectToAddress(final InetAddress inetAddress, final int n, final int n2) throws IOException {
        if (inetAddress.isAnyLocalAddress()) {
            this.doConnect(InetAddress.getLocalHost(), n, n2);
        }
        else {
            this.doConnect(inetAddress, n, n2);
        }
    }
    
    @Override
    public void setOption(final int n, final Object o) throws SocketException {
        if (this.isClosedOrPending()) {
            throw new SocketException("Socket Closed");
        }
        boolean b = true;
        switch (n) {
            case 128: {
                if (o == null || (!(o instanceof Integer) && !(o instanceof Boolean))) {
                    throw new SocketException("Bad parameter for option");
                }
                if (o instanceof Boolean) {
                    b = false;
                    break;
                }
                break;
            }
            case 4102: {
                if (o == null || !(o instanceof Integer)) {
                    throw new SocketException("Bad parameter for SO_TIMEOUT");
                }
                final int intValue = (int)o;
                if (intValue < 0) {
                    throw new IllegalArgumentException("timeout < 0");
                }
                this.timeout = intValue;
                break;
            }
            case 3: {
                if (o == null || !(o instanceof Integer)) {
                    throw new SocketException("bad argument for IP_TOS");
                }
                this.trafficClass = (int)o;
                break;
            }
            case 15: {
                throw new SocketException("Cannot re-bind socket");
            }
            case 1: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad parameter for TCP_NODELAY");
                }
                b = (boolean)o;
                break;
            }
            case 4097:
            case 4098: {
                if (o == null || !(o instanceof Integer) || (int)o <= 0) {
                    throw new SocketException("bad parameter for SO_SNDBUF or SO_RCVBUF");
                }
                break;
            }
            case 8: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad parameter for SO_KEEPALIVE");
                }
                b = (boolean)o;
                break;
            }
            case 4099: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad parameter for SO_OOBINLINE");
                }
                b = (boolean)o;
                break;
            }
            case 4: {
                if (o == null || !(o instanceof Boolean)) {
                    throw new SocketException("bad parameter for SO_REUSEADDR");
                }
                b = (boolean)o;
                break;
            }
            default: {
                throw new SocketException("unrecognized TCP option: " + n);
            }
        }
        this.socketSetOption(n, b, o);
    }
    
    @Override
    public Object getOption(final int n) throws SocketException {
        if (this.isClosedOrPending()) {
            throw new SocketException("Socket Closed");
        }
        if (n == 4102) {
            return new Integer(this.timeout);
        }
        switch (n) {
            case 1: {
                return this.socketGetOption(n, null) != -1;
            }
            case 4099: {
                return this.socketGetOption(n, null) != -1;
            }
            case 128: {
                final int socketGetOption = this.socketGetOption(n, null);
                return (socketGetOption == -1) ? Boolean.FALSE : new Integer(socketGetOption);
            }
            case 4: {
                return this.socketGetOption(n, null) != -1;
            }
            case 15: {
                final InetAddressContainer inetAddressContainer = new InetAddressContainer();
                this.socketGetOption(n, inetAddressContainer);
                return inetAddressContainer.addr;
            }
            case 4097:
            case 4098: {
                return new Integer(this.socketGetOption(n, null));
            }
            case 3: {
                try {
                    final int socketGetOption2 = this.socketGetOption(n, null);
                    if (socketGetOption2 == -1) {
                        return this.trafficClass;
                    }
                    return socketGetOption2;
                }
                catch (final SocketException ex) {
                    return this.trafficClass;
                }
            }
            case 8: {
                return this.socketGetOption(n, null) != -1;
            }
            default: {
                return null;
            }
        }
    }
    
    synchronized void doConnect(final InetAddress inetAddress, final int n, final int n2) throws IOException {
        synchronized (this.fdLock) {
            if (!this.closePending && (this.socket == null || !this.socket.isBound())) {
                NetHooks.beforeTcpConnect(this.fd, inetAddress, n);
            }
        }
        try {
            this.acquireFD();
            try {
                this.socketConnect(inetAddress, n, n2);
                synchronized (this.fdLock) {
                    if (this.closePending) {
                        throw new SocketException("Socket closed");
                    }
                }
                if (this.socket != null) {
                    this.socket.setBound();
                    this.socket.setConnected();
                }
            }
            finally {
                this.releaseFD();
            }
        }
        catch (final IOException ex) {
            this.close();
            throw ex;
        }
    }
    
    @Override
    protected synchronized void bind(final InetAddress inetAddress, final int n) throws IOException {
        synchronized (this.fdLock) {
            if (!this.closePending && (this.socket == null || !this.socket.isBound())) {
                NetHooks.beforeTcpBind(this.fd, inetAddress, n);
            }
        }
        this.socketBind(inetAddress, n);
        if (this.socket != null) {
            this.socket.setBound();
        }
        if (this.serverSocket != null) {
            this.serverSocket.setBound();
        }
    }
    
    @Override
    protected synchronized void listen(final int n) throws IOException {
        this.socketListen(n);
    }
    
    @Override
    protected void accept(final SocketImpl socketImpl) throws IOException {
        this.acquireFD();
        try {
            this.socketAccept(socketImpl);
        }
        finally {
            this.releaseFD();
        }
    }
    
    @Override
    protected synchronized InputStream getInputStream() throws IOException {
        synchronized (this.fdLock) {
            if (this.isClosedOrPending()) {
                throw new IOException("Socket Closed");
            }
            if (this.shut_rd) {
                throw new IOException("Socket input is shutdown");
            }
            if (this.socketInputStream == null) {
                this.socketInputStream = new SocketInputStream(this);
            }
        }
        return this.socketInputStream;
    }
    
    void setInputStream(final SocketInputStream socketInputStream) {
        this.socketInputStream = socketInputStream;
    }
    
    @Override
    protected synchronized OutputStream getOutputStream() throws IOException {
        synchronized (this.fdLock) {
            if (this.isClosedOrPending()) {
                throw new IOException("Socket Closed");
            }
            if (this.shut_wr) {
                throw new IOException("Socket output is shutdown");
            }
            if (this.socketOutputStream == null) {
                this.socketOutputStream = new SocketOutputStream(this);
            }
        }
        return this.socketOutputStream;
    }
    
    void setFileDescriptor(final FileDescriptor fd) {
        this.fd = fd;
    }
    
    void setAddress(final InetAddress address) {
        this.address = address;
    }
    
    void setPort(final int port) {
        this.port = port;
    }
    
    void setLocalPort(final int localport) {
        this.localport = localport;
    }
    
    @Override
    protected synchronized int available() throws IOException {
        if (this.isClosedOrPending()) {
            throw new IOException("Stream closed.");
        }
        if (this.isConnectionReset() || this.shut_rd) {
            return 0;
        }
        int n = 0;
        try {
            n = this.socketAvailable();
            if (n == 0 && this.isConnectionResetPending()) {
                this.setConnectionReset();
            }
        }
        catch (final ConnectionResetException ex) {
            this.setConnectionResetPending();
            try {
                n = this.socketAvailable();
                if (n == 0) {
                    this.setConnectionReset();
                }
            }
            catch (final ConnectionResetException ex2) {}
        }
        return n;
    }
    
    @Override
    protected void close() throws IOException {
        synchronized (this.fdLock) {
            if (this.fd != null) {
                if (!this.stream) {
                    ResourceManager.afterUdpClose();
                }
                if (this.fdUseCount == 0) {
                    if (this.closePending) {
                        return;
                    }
                    this.closePending = true;
                    try {
                        this.socketPreClose();
                    }
                    finally {
                        this.socketClose();
                    }
                    this.fd = null;
                }
                else if (!this.closePending) {
                    this.closePending = true;
                    --this.fdUseCount;
                    this.socketPreClose();
                }
            }
        }
    }
    
    @Override
    void reset() throws IOException {
        if (this.fd != null) {
            this.socketClose();
        }
        this.fd = null;
        super.reset();
    }
    
    @Override
    protected void shutdownInput() throws IOException {
        if (this.fd != null) {
            this.socketShutdown(0);
            if (this.socketInputStream != null) {
                this.socketInputStream.setEOF(true);
            }
            this.shut_rd = true;
        }
    }
    
    @Override
    protected void shutdownOutput() throws IOException {
        if (this.fd != null) {
            this.socketShutdown(1);
            this.shut_wr = true;
        }
    }
    
    @Override
    protected boolean supportsUrgentData() {
        return true;
    }
    
    @Override
    protected void sendUrgentData(final int n) throws IOException {
        if (this.fd == null) {
            throw new IOException("Socket Closed");
        }
        this.socketSendUrgentData(n);
    }
    
    @Override
    protected void finalize() throws IOException {
        this.close();
    }
    
    FileDescriptor acquireFD() {
        synchronized (this.fdLock) {
            ++this.fdUseCount;
            return this.fd;
        }
    }
    
    void releaseFD() {
        synchronized (this.fdLock) {
            --this.fdUseCount;
            if (this.fdUseCount == -1 && this.fd != null) {
                try {
                    this.socketClose();
                }
                catch (final IOException ex) {}
                finally {
                    this.fd = null;
                }
            }
        }
    }
    
    public boolean isConnectionReset() {
        synchronized (this.resetLock) {
            return this.resetState == this.CONNECTION_RESET;
        }
    }
    
    public boolean isConnectionResetPending() {
        synchronized (this.resetLock) {
            return this.resetState == this.CONNECTION_RESET_PENDING;
        }
    }
    
    public void setConnectionReset() {
        synchronized (this.resetLock) {
            this.resetState = this.CONNECTION_RESET;
        }
    }
    
    public void setConnectionResetPending() {
        synchronized (this.resetLock) {
            if (this.resetState == this.CONNECTION_NOT_RESET) {
                this.resetState = this.CONNECTION_RESET_PENDING;
            }
        }
    }
    
    public boolean isClosedOrPending() {
        synchronized (this.fdLock) {
            return this.closePending || this.fd == null;
        }
    }
    
    public int getTimeout() {
        return this.timeout;
    }
    
    private void socketPreClose() throws IOException {
        this.socketClose0(true);
    }
    
    protected void socketClose() throws IOException {
        this.socketClose0(false);
    }
    
    abstract void socketCreate(final boolean p0) throws IOException;
    
    abstract void socketConnect(final InetAddress p0, final int p1, final int p2) throws IOException;
    
    abstract void socketBind(final InetAddress p0, final int p1) throws IOException;
    
    abstract void socketListen(final int p0) throws IOException;
    
    abstract void socketAccept(final SocketImpl p0) throws IOException;
    
    abstract int socketAvailable() throws IOException;
    
    abstract void socketClose0(final boolean p0) throws IOException;
    
    abstract void socketShutdown(final int p0) throws IOException;
    
    abstract void socketSetOption(final int p0, final boolean p1, final Object p2) throws SocketException;
    
    abstract int socketGetOption(final int p0, final Object p1) throws SocketException;
    
    abstract void socketSendUrgentData(final int p0) throws IOException;
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("net");
                return null;
            }
        });
    }
}
