package java.net;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileDescriptor;

class PlainSocketImpl extends AbstractPlainSocketImpl
{
    private AbstractPlainSocketImpl impl;
    private static float version;
    private static boolean preferIPv4Stack;
    private static boolean useDualStackImpl;
    private static String exclBindProp;
    private static boolean exclusiveBind;
    
    PlainSocketImpl() {
        if (PlainSocketImpl.useDualStackImpl) {
            this.impl = new DualStackPlainSocketImpl(PlainSocketImpl.exclusiveBind);
        }
        else {
            this.impl = new TwoStacksPlainSocketImpl(PlainSocketImpl.exclusiveBind);
        }
    }
    
    PlainSocketImpl(final FileDescriptor fileDescriptor) {
        if (PlainSocketImpl.useDualStackImpl) {
            this.impl = new DualStackPlainSocketImpl(fileDescriptor, PlainSocketImpl.exclusiveBind);
        }
        else {
            this.impl = new TwoStacksPlainSocketImpl(fileDescriptor, PlainSocketImpl.exclusiveBind);
        }
    }
    
    @Override
    protected FileDescriptor getFileDescriptor() {
        return this.impl.getFileDescriptor();
    }
    
    @Override
    protected InetAddress getInetAddress() {
        return this.impl.getInetAddress();
    }
    
    @Override
    protected int getPort() {
        return this.impl.getPort();
    }
    
    @Override
    protected int getLocalPort() {
        return this.impl.getLocalPort();
    }
    
    @Override
    void setSocket(final Socket socket) {
        this.impl.setSocket(socket);
    }
    
    @Override
    Socket getSocket() {
        return this.impl.getSocket();
    }
    
    @Override
    void setServerSocket(final ServerSocket serverSocket) {
        this.impl.setServerSocket(serverSocket);
    }
    
    @Override
    ServerSocket getServerSocket() {
        return this.impl.getServerSocket();
    }
    
    @Override
    public String toString() {
        return this.impl.toString();
    }
    
    @Override
    protected synchronized void create(final boolean b) throws IOException {
        this.impl.create(b);
        this.fd = this.impl.fd;
    }
    
    @Override
    protected void connect(final String s, final int n) throws UnknownHostException, IOException {
        this.impl.connect(s, n);
    }
    
    @Override
    protected void connect(final InetAddress inetAddress, final int n) throws IOException {
        this.impl.connect(inetAddress, n);
    }
    
    @Override
    protected void connect(final SocketAddress socketAddress, final int n) throws IOException {
        this.impl.connect(socketAddress, n);
    }
    
    @Override
    public void setOption(final int n, final Object o) throws SocketException {
        this.impl.setOption(n, o);
    }
    
    @Override
    public Object getOption(final int n) throws SocketException {
        return this.impl.getOption(n);
    }
    
    @Override
    synchronized void doConnect(final InetAddress inetAddress, final int n, final int n2) throws IOException {
        this.impl.doConnect(inetAddress, n, n2);
    }
    
    @Override
    protected synchronized void bind(final InetAddress inetAddress, final int n) throws IOException {
        this.impl.bind(inetAddress, n);
    }
    
    @Override
    protected synchronized void accept(final SocketImpl socketImpl) throws IOException {
        if (socketImpl instanceof PlainSocketImpl) {
            final AbstractPlainSocketImpl impl = ((PlainSocketImpl)socketImpl).impl;
            impl.address = new InetAddress();
            impl.fd = new FileDescriptor();
            this.impl.accept(impl);
            socketImpl.fd = impl.fd;
        }
        else {
            this.impl.accept(socketImpl);
        }
    }
    
    @Override
    void setFileDescriptor(final FileDescriptor fileDescriptor) {
        this.impl.setFileDescriptor(fileDescriptor);
    }
    
    @Override
    void setAddress(final InetAddress address) {
        this.impl.setAddress(address);
    }
    
    @Override
    void setPort(final int port) {
        this.impl.setPort(port);
    }
    
    @Override
    void setLocalPort(final int localPort) {
        this.impl.setLocalPort(localPort);
    }
    
    @Override
    protected synchronized InputStream getInputStream() throws IOException {
        return this.impl.getInputStream();
    }
    
    @Override
    void setInputStream(final SocketInputStream inputStream) {
        this.impl.setInputStream(inputStream);
    }
    
    @Override
    protected synchronized OutputStream getOutputStream() throws IOException {
        return this.impl.getOutputStream();
    }
    
    @Override
    protected void close() throws IOException {
        try {
            this.impl.close();
        }
        finally {
            this.fd = null;
        }
    }
    
    @Override
    void reset() throws IOException {
        try {
            this.impl.reset();
        }
        finally {
            this.fd = null;
        }
    }
    
    @Override
    protected void shutdownInput() throws IOException {
        this.impl.shutdownInput();
    }
    
    @Override
    protected void shutdownOutput() throws IOException {
        this.impl.shutdownOutput();
    }
    
    @Override
    protected void sendUrgentData(final int n) throws IOException {
        this.impl.sendUrgentData(n);
    }
    
    @Override
    FileDescriptor acquireFD() {
        return this.impl.acquireFD();
    }
    
    @Override
    void releaseFD() {
        this.impl.releaseFD();
    }
    
    @Override
    public boolean isConnectionReset() {
        return this.impl.isConnectionReset();
    }
    
    @Override
    public boolean isConnectionResetPending() {
        return this.impl.isConnectionResetPending();
    }
    
    @Override
    public void setConnectionReset() {
        this.impl.setConnectionReset();
    }
    
    @Override
    public void setConnectionResetPending() {
        this.impl.setConnectionResetPending();
    }
    
    @Override
    public boolean isClosedOrPending() {
        return this.impl.isClosedOrPending();
    }
    
    @Override
    public int getTimeout() {
        return this.impl.getTimeout();
    }
    
    @Override
    void socketCreate(final boolean b) throws IOException {
        this.impl.socketCreate(b);
    }
    
    @Override
    void socketConnect(final InetAddress inetAddress, final int n, final int n2) throws IOException {
        this.impl.socketConnect(inetAddress, n, n2);
    }
    
    @Override
    void socketBind(final InetAddress inetAddress, final int n) throws IOException {
        this.impl.socketBind(inetAddress, n);
    }
    
    @Override
    void socketListen(final int n) throws IOException {
        this.impl.socketListen(n);
    }
    
    @Override
    void socketAccept(final SocketImpl socketImpl) throws IOException {
        this.impl.socketAccept(socketImpl);
    }
    
    @Override
    int socketAvailable() throws IOException {
        return this.impl.socketAvailable();
    }
    
    @Override
    void socketClose0(final boolean b) throws IOException {
        this.impl.socketClose0(b);
    }
    
    @Override
    void socketShutdown(final int n) throws IOException {
        this.impl.socketShutdown(n);
    }
    
    @Override
    void socketSetOption(final int n, final boolean b, final Object o) throws SocketException {
        this.impl.socketSetOption(n, b, o);
    }
    
    @Override
    int socketGetOption(final int n, final Object o) throws SocketException {
        return this.impl.socketGetOption(n, o);
    }
    
    @Override
    void socketSendUrgentData(final int n) throws IOException {
        this.impl.socketSendUrgentData(n);
    }
    
    static {
        PlainSocketImpl.preferIPv4Stack = false;
        PlainSocketImpl.useDualStackImpl = false;
        PlainSocketImpl.exclusiveBind = true;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                PlainSocketImpl.version = 0.0f;
                try {
                    PlainSocketImpl.version = Float.parseFloat(System.getProperties().getProperty("os.version"));
                    PlainSocketImpl.preferIPv4Stack = Boolean.parseBoolean(System.getProperties().getProperty("java.net.preferIPv4Stack"));
                    PlainSocketImpl.exclBindProp = System.getProperty("sun.net.useExclusiveBind");
                }
                catch (final NumberFormatException ex) {
                    assert false : ex;
                }
                return null;
            }
        });
        if (PlainSocketImpl.version >= 6.0 && !PlainSocketImpl.preferIPv4Stack) {
            PlainSocketImpl.useDualStackImpl = true;
        }
        if (PlainSocketImpl.exclBindProp != null) {
            PlainSocketImpl.exclusiveBind = (PlainSocketImpl.exclBindProp.length() == 0 || Boolean.parseBoolean(PlainSocketImpl.exclBindProp));
        }
        else if (PlainSocketImpl.version < 6.0) {
            PlainSocketImpl.exclusiveBind = false;
        }
    }
}
