package org.apache.commons.httpclient;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.params.HttpParams;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;

public class SimpleHttpConnectionManager implements HttpConnectionManager
{
    private static final Log LOG;
    private static final String MISUSE_MESSAGE = "SimpleHttpConnectionManager being used incorrectly.  Be sure that HttpMethod.releaseConnection() is always called and that only one thread and/or method is using this connection manager at a time.";
    protected HttpConnection httpConnection;
    private HttpConnectionManagerParams params;
    private long idleStartTime;
    private volatile boolean inUse;
    private boolean alwaysClose;
    
    static void finishLastResponse(final HttpConnection conn) {
        final InputStream lastResponse = conn.getLastResponseInputStream();
        if (lastResponse != null) {
            conn.setLastResponseInputStream(null);
            try {
                lastResponse.close();
            }
            catch (final IOException ioe) {
                conn.close();
            }
        }
    }
    
    public SimpleHttpConnectionManager(final boolean alwaysClose) {
        this.params = new HttpConnectionManagerParams();
        this.idleStartTime = Long.MAX_VALUE;
        this.inUse = false;
        this.alwaysClose = false;
        this.alwaysClose = alwaysClose;
    }
    
    public SimpleHttpConnectionManager() {
        this.params = new HttpConnectionManagerParams();
        this.idleStartTime = Long.MAX_VALUE;
        this.inUse = false;
        this.alwaysClose = false;
    }
    
    public HttpConnection getConnection(final HostConfiguration hostConfiguration) {
        return this.getConnection(hostConfiguration, 0L);
    }
    
    public boolean isConnectionStaleCheckingEnabled() {
        return this.params.isStaleCheckingEnabled();
    }
    
    public void setConnectionStaleCheckingEnabled(final boolean connectionStaleCheckingEnabled) {
        this.params.setStaleCheckingEnabled(connectionStaleCheckingEnabled);
    }
    
    public HttpConnection getConnectionWithTimeout(final HostConfiguration hostConfiguration, final long timeout) {
        if (this.httpConnection == null) {
            (this.httpConnection = new HttpConnection(hostConfiguration)).setHttpConnectionManager(this);
            this.httpConnection.getParams().setDefaults(this.params);
        }
        else if (!hostConfiguration.hostEquals(this.httpConnection) || !hostConfiguration.proxyEquals(this.httpConnection)) {
            if (this.httpConnection.isOpen()) {
                this.httpConnection.close();
            }
            this.httpConnection.setHost(hostConfiguration.getHost());
            this.httpConnection.setPort(hostConfiguration.getPort());
            this.httpConnection.setProtocol(hostConfiguration.getProtocol());
            this.httpConnection.setLocalAddress(hostConfiguration.getLocalAddress());
            this.httpConnection.setProxyHost(hostConfiguration.getProxyHost());
            this.httpConnection.setProxyPort(hostConfiguration.getProxyPort());
        }
        else {
            finishLastResponse(this.httpConnection);
        }
        this.idleStartTime = Long.MAX_VALUE;
        if (this.inUse) {
            SimpleHttpConnectionManager.LOG.warn((Object)"SimpleHttpConnectionManager being used incorrectly.  Be sure that HttpMethod.releaseConnection() is always called and that only one thread and/or method is using this connection manager at a time.");
        }
        this.inUse = true;
        return this.httpConnection;
    }
    
    public HttpConnection getConnection(final HostConfiguration hostConfiguration, final long timeout) {
        return this.getConnectionWithTimeout(hostConfiguration, timeout);
    }
    
    public void releaseConnection(final HttpConnection conn) {
        if (conn != this.httpConnection) {
            throw new IllegalStateException("Unexpected release of an unknown connection.");
        }
        if (this.alwaysClose) {
            this.httpConnection.close();
        }
        else {
            finishLastResponse(this.httpConnection);
        }
        this.inUse = false;
        this.idleStartTime = System.currentTimeMillis();
    }
    
    public HttpConnectionManagerParams getParams() {
        return this.params;
    }
    
    public void setParams(final HttpConnectionManagerParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    public void closeIdleConnections(final long idleTimeout) {
        final long maxIdleTime = System.currentTimeMillis() - idleTimeout;
        if (this.idleStartTime <= maxIdleTime) {
            this.httpConnection.close();
        }
    }
    
    public void shutdown() {
        this.httpConnection.close();
    }
    
    static {
        LOG = LogFactory.getLog(SimpleHttpConnectionManager.class);
    }
}
