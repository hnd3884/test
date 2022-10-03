package org.apache.http.impl.conn;

import java.net.InetAddress;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.HttpResponse;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import javax.net.ssl.SSLSession;
import java.net.Socket;
import org.apache.http.HttpClientConnection;
import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.conn.ManagedHttpClientConnection;

class CPoolProxy implements ManagedHttpClientConnection, HttpContext
{
    private volatile CPoolEntry poolEntry;
    
    CPoolProxy(final CPoolEntry entry) {
        this.poolEntry = entry;
    }
    
    CPoolEntry getPoolEntry() {
        return this.poolEntry;
    }
    
    CPoolEntry detach() {
        final CPoolEntry local = this.poolEntry;
        this.poolEntry = null;
        return local;
    }
    
    ManagedHttpClientConnection getConnection() {
        final CPoolEntry local = this.poolEntry;
        if (local == null) {
            return null;
        }
        return (ManagedHttpClientConnection)local.getConnection();
    }
    
    ManagedHttpClientConnection getValidConnection() {
        final ManagedHttpClientConnection conn = this.getConnection();
        if (conn == null) {
            throw new ConnectionShutdownException();
        }
        return conn;
    }
    
    public void close() throws IOException {
        final CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.closeConnection();
        }
    }
    
    public void shutdown() throws IOException {
        final CPoolEntry local = this.poolEntry;
        if (local != null) {
            local.shutdownConnection();
        }
    }
    
    public boolean isOpen() {
        final CPoolEntry local = this.poolEntry;
        return local != null && !local.isClosed();
    }
    
    public boolean isStale() {
        final HttpClientConnection conn = (HttpClientConnection)this.getConnection();
        return conn == null || conn.isStale();
    }
    
    public void setSocketTimeout(final int timeout) {
        this.getValidConnection().setSocketTimeout(timeout);
    }
    
    public int getSocketTimeout() {
        return this.getValidConnection().getSocketTimeout();
    }
    
    @Override
    public String getId() {
        return this.getValidConnection().getId();
    }
    
    @Override
    public void bind(final Socket socket) throws IOException {
        this.getValidConnection().bind(socket);
    }
    
    @Override
    public Socket getSocket() {
        return this.getValidConnection().getSocket();
    }
    
    @Override
    public SSLSession getSSLSession() {
        return this.getValidConnection().getSSLSession();
    }
    
    public boolean isResponseAvailable(final int timeout) throws IOException {
        return this.getValidConnection().isResponseAvailable(timeout);
    }
    
    public void sendRequestHeader(final HttpRequest request) throws HttpException, IOException {
        this.getValidConnection().sendRequestHeader(request);
    }
    
    public void sendRequestEntity(final HttpEntityEnclosingRequest request) throws HttpException, IOException {
        this.getValidConnection().sendRequestEntity(request);
    }
    
    public HttpResponse receiveResponseHeader() throws HttpException, IOException {
        return this.getValidConnection().receiveResponseHeader();
    }
    
    public void receiveResponseEntity(final HttpResponse response) throws HttpException, IOException {
        this.getValidConnection().receiveResponseEntity(response);
    }
    
    public void flush() throws IOException {
        this.getValidConnection().flush();
    }
    
    public HttpConnectionMetrics getMetrics() {
        return this.getValidConnection().getMetrics();
    }
    
    public InetAddress getLocalAddress() {
        return this.getValidConnection().getLocalAddress();
    }
    
    public int getLocalPort() {
        return this.getValidConnection().getLocalPort();
    }
    
    public InetAddress getRemoteAddress() {
        return this.getValidConnection().getRemoteAddress();
    }
    
    public int getRemotePort() {
        return this.getValidConnection().getRemotePort();
    }
    
    public Object getAttribute(final String id) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        return (conn instanceof HttpContext) ? ((HttpContext)conn).getAttribute(id) : null;
    }
    
    public void setAttribute(final String id, final Object obj) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        if (conn instanceof HttpContext) {
            ((HttpContext)conn).setAttribute(id, obj);
        }
    }
    
    public Object removeAttribute(final String id) {
        final ManagedHttpClientConnection conn = this.getValidConnection();
        return (conn instanceof HttpContext) ? ((HttpContext)conn).removeAttribute(id) : null;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CPoolProxy{");
        final ManagedHttpClientConnection conn = this.getConnection();
        if (conn != null) {
            sb.append(conn);
        }
        else {
            sb.append("detached");
        }
        sb.append('}');
        return sb.toString();
    }
    
    public static HttpClientConnection newProxy(final CPoolEntry poolEntry) {
        return (HttpClientConnection)new CPoolProxy(poolEntry);
    }
    
    private static CPoolProxy getProxy(final HttpClientConnection conn) {
        if (!CPoolProxy.class.isInstance(conn)) {
            throw new IllegalStateException("Unexpected connection proxy class: " + conn.getClass());
        }
        return CPoolProxy.class.cast(conn);
    }
    
    public static CPoolEntry getPoolEntry(final HttpClientConnection proxy) {
        final CPoolEntry entry = getProxy(proxy).getPoolEntry();
        if (entry == null) {
            throw new ConnectionShutdownException();
        }
        return entry;
    }
    
    public static CPoolEntry detach(final HttpClientConnection conn) {
        return getProxy(conn).detach();
    }
}
