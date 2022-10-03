package org.apache.commons.httpclient;

import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import java.net.Socket;
import java.io.IOException;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.params.HttpClientParams;

public class ProxyClient
{
    private HttpState state;
    private HttpClientParams params;
    private HostConfiguration hostConfiguration;
    
    public ProxyClient() {
        this(new HttpClientParams());
    }
    
    public ProxyClient(final HttpClientParams params) {
        this.state = new HttpState();
        this.params = null;
        this.hostConfiguration = new HostConfiguration();
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
    }
    
    public synchronized HttpState getState() {
        return this.state;
    }
    
    public synchronized void setState(final HttpState state) {
        this.state = state;
    }
    
    public synchronized HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }
    
    public synchronized void setHostConfiguration(final HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }
    
    public synchronized HttpClientParams getParams() {
        return this.params;
    }
    
    public synchronized void setParams(final HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    public ConnectResponse connect() throws IOException, HttpException {
        final HostConfiguration hostconf = this.getHostConfiguration();
        if (hostconf.getProxyHost() == null) {
            throw new IllegalStateException("proxy host must be configured");
        }
        if (hostconf.getHost() == null) {
            throw new IllegalStateException("destination host must be configured");
        }
        if (hostconf.getProtocol().isSecure()) {
            throw new IllegalStateException("secure protocol socket factory may not be used");
        }
        final ConnectMethod method = new ConnectMethod(this.getHostConfiguration());
        method.getParams().setDefaults(this.getParams());
        final DummyConnectionManager connectionManager = new DummyConnectionManager();
        connectionManager.setConnectionParams(this.getParams());
        final HttpMethodDirector director = new HttpMethodDirector(connectionManager, hostconf, this.getParams(), this.getState());
        director.executeMethod(method);
        final ConnectResponse response = new ConnectResponse();
        response.setConnectMethod(method);
        if (method.getStatusCode() == 200) {
            response.setSocket(connectionManager.getConnection().getSocket());
        }
        else {
            connectionManager.getConnection().close();
        }
        return response;
    }
    
    public static class ConnectResponse
    {
        private ConnectMethod connectMethod;
        private Socket socket;
        
        private ConnectResponse() {
        }
        
        public ConnectMethod getConnectMethod() {
            return this.connectMethod;
        }
        
        private void setConnectMethod(final ConnectMethod connectMethod) {
            this.connectMethod = connectMethod;
        }
        
        public Socket getSocket() {
            return this.socket;
        }
        
        private void setSocket(final Socket socket) {
            this.socket = socket;
        }
    }
    
    static class DummyConnectionManager implements HttpConnectionManager
    {
        private HttpConnection httpConnection;
        private HttpParams connectionParams;
        
        public void closeIdleConnections(final long idleTimeout) {
        }
        
        public HttpConnection getConnection() {
            return this.httpConnection;
        }
        
        public void setConnectionParams(final HttpParams httpParams) {
            this.connectionParams = httpParams;
        }
        
        public HttpConnection getConnectionWithTimeout(final HostConfiguration hostConfiguration, final long timeout) {
            (this.httpConnection = new HttpConnection(hostConfiguration)).setHttpConnectionManager(this);
            this.httpConnection.getParams().setDefaults(this.connectionParams);
            return this.httpConnection;
        }
        
        public HttpConnection getConnection(final HostConfiguration hostConfiguration, final long timeout) throws HttpException {
            return this.getConnectionWithTimeout(hostConfiguration, timeout);
        }
        
        public HttpConnection getConnection(final HostConfiguration hostConfiguration) {
            return this.getConnectionWithTimeout(hostConfiguration, -1L);
        }
        
        public void releaseConnection(final HttpConnection conn) {
        }
        
        public HttpConnectionManagerParams getParams() {
            return null;
        }
        
        public void setParams(final HttpConnectionManagerParams params) {
        }
    }
}
