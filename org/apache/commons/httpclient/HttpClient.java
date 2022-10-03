package org.apache.commons.httpclient;

import java.security.Provider;
import java.security.Security;
import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.commons.httpclient.params.HttpParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.logging.Log;

public class HttpClient
{
    private static final Log LOG;
    private HttpConnectionManager httpConnectionManager;
    private HttpState state;
    private HttpClientParams params;
    private HostConfiguration hostConfiguration;
    
    public HttpClient() {
        this(new HttpClientParams());
    }
    
    public HttpClient(final HttpClientParams params) {
        this.state = new HttpState();
        this.params = null;
        this.hostConfiguration = new HostConfiguration();
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
        this.httpConnectionManager = null;
        final Class clazz = params.getConnectionManagerClass();
        if (clazz != null) {
            try {
                this.httpConnectionManager = clazz.newInstance();
            }
            catch (final Exception e) {
                HttpClient.LOG.warn((Object)"Error instantiating connection manager class, defaulting to SimpleHttpConnectionManager", (Throwable)e);
            }
        }
        if (this.httpConnectionManager == null) {
            this.httpConnectionManager = new SimpleHttpConnectionManager();
        }
        if (this.httpConnectionManager != null) {
            this.httpConnectionManager.getParams().setDefaults(this.params);
        }
    }
    
    public HttpClient(final HttpClientParams params, final HttpConnectionManager httpConnectionManager) {
        this.state = new HttpState();
        this.params = null;
        this.hostConfiguration = new HostConfiguration();
        if (httpConnectionManager == null) {
            throw new IllegalArgumentException("httpConnectionManager cannot be null");
        }
        if (params == null) {
            throw new IllegalArgumentException("Params may not be null");
        }
        this.params = params;
        this.httpConnectionManager = httpConnectionManager;
        this.httpConnectionManager.getParams().setDefaults(this.params);
    }
    
    public HttpClient(final HttpConnectionManager httpConnectionManager) {
        this(new HttpClientParams(), httpConnectionManager);
    }
    
    public synchronized HttpState getState() {
        return this.state;
    }
    
    public synchronized void setState(final HttpState state) {
        this.state = state;
    }
    
    public synchronized void setStrictMode(final boolean strictMode) {
        if (strictMode) {
            this.params.makeStrict();
        }
        else {
            this.params.makeLenient();
        }
    }
    
    public synchronized boolean isStrictMode() {
        return false;
    }
    
    public synchronized void setTimeout(final int newTimeoutInMilliseconds) {
        this.params.setSoTimeout(newTimeoutInMilliseconds);
    }
    
    public synchronized void setHttpConnectionFactoryTimeout(final long timeout) {
        this.params.setConnectionManagerTimeout(timeout);
    }
    
    public synchronized void setConnectionTimeout(final int newTimeoutInMilliseconds) {
        this.httpConnectionManager.getParams().setConnectionTimeout(newTimeoutInMilliseconds);
    }
    
    public int executeMethod(final HttpMethod method) throws IOException, HttpException {
        HttpClient.LOG.trace((Object)"enter HttpClient.executeMethod(HttpMethod)");
        return this.executeMethod(null, method, null);
    }
    
    public int executeMethod(final HostConfiguration hostConfiguration, final HttpMethod method) throws IOException, HttpException {
        HttpClient.LOG.trace((Object)"enter HttpClient.executeMethod(HostConfiguration,HttpMethod)");
        return this.executeMethod(hostConfiguration, method, null);
    }
    
    public int executeMethod(HostConfiguration hostconfig, final HttpMethod method, final HttpState state) throws IOException, HttpException {
        HttpClient.LOG.trace((Object)"enter HttpClient.executeMethod(HostConfiguration,HttpMethod,HttpState)");
        if (method == null) {
            throw new IllegalArgumentException("HttpMethod parameter may not be null");
        }
        final HostConfiguration defaulthostconfig = this.getHostConfiguration();
        if (hostconfig == null) {
            hostconfig = defaulthostconfig;
        }
        final URI uri = method.getURI();
        if (hostconfig == defaulthostconfig || uri.isAbsoluteURI()) {
            hostconfig = (HostConfiguration)hostconfig.clone();
            if (uri.isAbsoluteURI()) {
                hostconfig.setHost(uri);
            }
        }
        final HttpMethodDirector methodDirector = new HttpMethodDirector(this.getHttpConnectionManager(), hostconfig, this.params, (state == null) ? this.getState() : state);
        methodDirector.executeMethod(method);
        return method.getStatusCode();
    }
    
    public String getHost() {
        return this.hostConfiguration.getHost();
    }
    
    public int getPort() {
        return this.hostConfiguration.getPort();
    }
    
    public synchronized HostConfiguration getHostConfiguration() {
        return this.hostConfiguration;
    }
    
    public synchronized void setHostConfiguration(final HostConfiguration hostConfiguration) {
        this.hostConfiguration = hostConfiguration;
    }
    
    public synchronized HttpConnectionManager getHttpConnectionManager() {
        return this.httpConnectionManager;
    }
    
    public synchronized void setHttpConnectionManager(final HttpConnectionManager httpConnectionManager) {
        this.httpConnectionManager = httpConnectionManager;
        if (this.httpConnectionManager != null) {
            this.httpConnectionManager.getParams().setDefaults(this.params);
        }
    }
    
    public HttpClientParams getParams() {
        return this.params;
    }
    
    public void setParams(final HttpClientParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    static {
        LOG = LogFactory.getLog(HttpClient.class);
        if (HttpClient.LOG.isDebugEnabled()) {
            try {
                HttpClient.LOG.debug((Object)("Java version: " + System.getProperty("java.version")));
                HttpClient.LOG.debug((Object)("Java vendor: " + System.getProperty("java.vendor")));
                HttpClient.LOG.debug((Object)("Java class path: " + System.getProperty("java.class.path")));
                HttpClient.LOG.debug((Object)("Operating system name: " + System.getProperty("os.name")));
                HttpClient.LOG.debug((Object)("Operating system architecture: " + System.getProperty("os.arch")));
                HttpClient.LOG.debug((Object)("Operating system version: " + System.getProperty("os.version")));
                final Provider[] providers = Security.getProviders();
                for (int i = 0; i < providers.length; ++i) {
                    final Provider provider = providers[i];
                    HttpClient.LOG.debug((Object)(provider.getName() + " " + provider.getVersion() + ": " + provider.getInfo()));
                }
            }
            catch (final SecurityException ex) {}
        }
    }
}
