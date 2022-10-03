package java.net;

import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

class HttpConnectSocketImpl extends PlainSocketImpl
{
    private static final String httpURLClazzStr = "sun.net.www.protocol.http.HttpURLConnection";
    private static final String netClientClazzStr = "sun.net.NetworkClient";
    private static final String doTunnelingStr = "doTunneling";
    private static final Field httpField;
    private static final Field serverSocketField;
    private static final Method doTunneling;
    private final String server;
    private InetSocketAddress external_address;
    private HashMap<Integer, Object> optionsMap;
    
    HttpConnectSocketImpl(final String server, final int port) {
        this.optionsMap = new HashMap<Integer, Object>();
        this.server = server;
        this.port = port;
    }
    
    HttpConnectSocketImpl(final Proxy proxy) {
        this.optionsMap = new HashMap<Integer, Object>();
        final SocketAddress address = proxy.address();
        if (!(address instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        final InetSocketAddress inetSocketAddress = (InetSocketAddress)address;
        this.server = inetSocketAddress.getHostString();
        this.port = inetSocketAddress.getPort();
    }
    
    @Override
    protected void connect(final SocketAddress socketAddress, final int n) throws IOException {
        if (socketAddress == null || !(socketAddress instanceof InetSocketAddress)) {
            throw new IllegalArgumentException("Unsupported address type");
        }
        final InetSocketAddress external_address = (InetSocketAddress)socketAddress;
        final String s = external_address.isUnresolved() ? external_address.getHostName() : external_address.getAddress().getHostAddress();
        final int port = external_address.getPort();
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkConnect(s, port);
        }
        final Socket privilegedDoTunnel = this.privilegedDoTunnel("http://" + s + ":" + port, n);
        this.external_address = external_address;
        this.close();
        final AbstractPlainSocketImpl impl = (AbstractPlainSocketImpl)privilegedDoTunnel.impl;
        this.getSocket().impl = impl;
        final Set<Map.Entry<Integer, Object>> entrySet = this.optionsMap.entrySet();
        try {
            for (final Map.Entry entry : entrySet) {
                impl.setOption((int)entry.getKey(), entry.getValue());
            }
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void setOption(final int n, final Object o) throws SocketException {
        super.setOption(n, o);
        if (this.external_address != null) {
            return;
        }
        this.optionsMap.put(n, o);
    }
    
    private Socket privilegedDoTunnel(final String s, final int n) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Socket>)new PrivilegedExceptionAction<Socket>() {
                @Override
                public Socket run() throws IOException {
                    return HttpConnectSocketImpl.this.doTunnel(s, n);
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private Socket doTunnel(final String s, final int connectTimeout) throws IOException {
        final HttpURLConnection httpURLConnection = (HttpURLConnection)new URL(s).openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(this.server, this.port)));
        httpURLConnection.setConnectTimeout(connectTimeout);
        httpURLConnection.setReadTimeout(this.timeout);
        httpURLConnection.connect();
        this.doTunneling(httpURLConnection);
        try {
            return (Socket)HttpConnectSocketImpl.serverSocketField.get(HttpConnectSocketImpl.httpField.get(httpURLConnection));
        }
        catch (final IllegalAccessException ex) {
            throw new InternalError("Should not reach here", ex);
        }
    }
    
    private void doTunneling(final HttpURLConnection httpURLConnection) {
        try {
            HttpConnectSocketImpl.doTunneling.invoke(httpURLConnection, new Object[0]);
        }
        catch (final ReflectiveOperationException ex) {
            throw new InternalError("Should not reach here", ex);
        }
    }
    
    @Override
    protected InetAddress getInetAddress() {
        if (this.external_address != null) {
            return this.external_address.getAddress();
        }
        return super.getInetAddress();
    }
    
    @Override
    protected int getPort() {
        if (this.external_address != null) {
            return this.external_address.getPort();
        }
        return super.getPort();
    }
    
    @Override
    protected int getLocalPort() {
        if (this.socket != null) {
            return super.getLocalPort();
        }
        if (this.external_address != null) {
            return this.external_address.getPort();
        }
        return super.getLocalPort();
    }
    
    static {
        try {
            final Class<?> forName = Class.forName("sun.net.www.protocol.http.HttpURLConnection", true, null);
            httpField = forName.getDeclaredField("http");
            doTunneling = forName.getDeclaredMethod("doTunneling", (Class[])new Class[0]);
            serverSocketField = Class.forName("sun.net.NetworkClient", true, null).getDeclaredField("serverSocket");
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    HttpConnectSocketImpl.httpField.setAccessible(true);
                    HttpConnectSocketImpl.serverSocketField.setAccessible(true);
                    return null;
                }
            });
        }
        catch (final ReflectiveOperationException ex) {
            throw new InternalError("Should not reach here", ex);
        }
    }
}
