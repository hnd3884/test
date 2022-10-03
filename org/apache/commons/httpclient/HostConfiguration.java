package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.LangUtils;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.params.HostParams;
import java.net.InetAddress;

public class HostConfiguration implements Cloneable
{
    public static final HostConfiguration ANY_HOST_CONFIGURATION;
    private HttpHost host;
    private ProxyHost proxyHost;
    private InetAddress localAddress;
    private HostParams params;
    
    public HostConfiguration() {
        this.host = null;
        this.proxyHost = null;
        this.localAddress = null;
        this.params = new HostParams();
    }
    
    public HostConfiguration(final HostConfiguration hostConfiguration) {
        this.host = null;
        this.proxyHost = null;
        this.localAddress = null;
        this.params = new HostParams();
        this.init(hostConfiguration);
    }
    
    private void init(final HostConfiguration hostConfiguration) {
        synchronized (hostConfiguration) {
            try {
                if (hostConfiguration.host != null) {
                    this.host = (HttpHost)hostConfiguration.host.clone();
                }
                else {
                    this.host = null;
                }
                if (hostConfiguration.proxyHost != null) {
                    this.proxyHost = (ProxyHost)hostConfiguration.proxyHost.clone();
                }
                else {
                    this.proxyHost = null;
                }
                this.localAddress = hostConfiguration.getLocalAddress();
                this.params = (HostParams)hostConfiguration.getParams().clone();
            }
            catch (final CloneNotSupportedException e) {
                throw new IllegalArgumentException("Host configuration could not be cloned");
            }
        }
    }
    
    public Object clone() {
        HostConfiguration copy;
        try {
            copy = (HostConfiguration)super.clone();
        }
        catch (final CloneNotSupportedException e) {
            throw new IllegalArgumentException("Host configuration could not be cloned");
        }
        copy.init(this);
        return copy;
    }
    
    public synchronized String toString() {
        boolean appendComma = false;
        final StringBuffer b = new StringBuffer(50);
        b.append("HostConfiguration[");
        if (this.host != null) {
            appendComma = true;
            b.append("host=").append(this.host);
        }
        if (this.proxyHost != null) {
            if (appendComma) {
                b.append(", ");
            }
            else {
                appendComma = true;
            }
            b.append("proxyHost=").append(this.proxyHost);
        }
        if (this.localAddress != null) {
            if (appendComma) {
                b.append(", ");
            }
            else {
                appendComma = true;
            }
            b.append("localAddress=").append(this.localAddress);
            if (appendComma) {
                b.append(", ");
            }
            else {
                appendComma = true;
            }
            b.append("params=").append(this.params);
        }
        b.append("]");
        return b.toString();
    }
    
    public synchronized boolean hostEquals(final HttpConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (this.host == null) {
            return false;
        }
        if (!this.host.getHostName().equalsIgnoreCase(connection.getHost())) {
            return false;
        }
        if (this.host.getPort() != connection.getPort()) {
            return false;
        }
        if (!this.host.getProtocol().equals(connection.getProtocol())) {
            return false;
        }
        if (this.localAddress != null) {
            if (!this.localAddress.equals(connection.getLocalAddress())) {
                return false;
            }
        }
        else if (connection.getLocalAddress() != null) {
            return false;
        }
        return true;
    }
    
    public synchronized boolean proxyEquals(final HttpConnection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection may not be null");
        }
        if (this.proxyHost != null) {
            return this.proxyHost.getHostName().equalsIgnoreCase(connection.getProxyHost()) && this.proxyHost.getPort() == connection.getProxyPort();
        }
        return connection.getProxyHost() == null;
    }
    
    public synchronized boolean isHostSet() {
        return this.host != null;
    }
    
    public synchronized void setHost(final HttpHost host) {
        this.host = host;
    }
    
    public synchronized void setHost(final String host, final int port, final String protocol) {
        this.host = new HttpHost(host, port, Protocol.getProtocol(protocol));
    }
    
    public synchronized void setHost(final String host, final String virtualHost, final int port, final Protocol protocol) {
        this.setHost(host, port, protocol);
        this.params.setVirtualHost(virtualHost);
    }
    
    public synchronized void setHost(final String host, final int port, final Protocol protocol) {
        if (host == null) {
            throw new IllegalArgumentException("host must not be null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("protocol must not be null");
        }
        this.host = new HttpHost(host, port, protocol);
    }
    
    public synchronized void setHost(final String host, final int port) {
        this.setHost(host, port, Protocol.getProtocol("http"));
    }
    
    public synchronized void setHost(final String host) {
        final Protocol defaultProtocol = Protocol.getProtocol("http");
        this.setHost(host, defaultProtocol.getDefaultPort(), defaultProtocol);
    }
    
    public synchronized void setHost(final URI uri) {
        try {
            this.setHost(uri.getHost(), uri.getPort(), uri.getScheme());
        }
        catch (final URIException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }
    
    public synchronized String getHostURL() {
        if (this.host == null) {
            throw new IllegalStateException("Host must be set to create a host URL");
        }
        return this.host.toURI();
    }
    
    public synchronized String getHost() {
        if (this.host != null) {
            return this.host.getHostName();
        }
        return null;
    }
    
    public synchronized String getVirtualHost() {
        return this.params.getVirtualHost();
    }
    
    public synchronized int getPort() {
        if (this.host != null) {
            return this.host.getPort();
        }
        return -1;
    }
    
    public synchronized Protocol getProtocol() {
        if (this.host != null) {
            return this.host.getProtocol();
        }
        return null;
    }
    
    public synchronized boolean isProxySet() {
        return this.proxyHost != null;
    }
    
    public synchronized void setProxyHost(final ProxyHost proxyHost) {
        this.proxyHost = proxyHost;
    }
    
    public synchronized void setProxy(final String proxyHost, final int proxyPort) {
        this.proxyHost = new ProxyHost(proxyHost, proxyPort);
    }
    
    public synchronized String getProxyHost() {
        if (this.proxyHost != null) {
            return this.proxyHost.getHostName();
        }
        return null;
    }
    
    public synchronized int getProxyPort() {
        if (this.proxyHost != null) {
            return this.proxyHost.getPort();
        }
        return -1;
    }
    
    public synchronized void setLocalAddress(final InetAddress localAddress) {
        this.localAddress = localAddress;
    }
    
    public synchronized InetAddress getLocalAddress() {
        return this.localAddress;
    }
    
    public HostParams getParams() {
        return this.params;
    }
    
    public void setParams(final HostParams params) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        this.params = params;
    }
    
    public synchronized boolean equals(final Object o) {
        if (!(o instanceof HostConfiguration)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        final HostConfiguration that = (HostConfiguration)o;
        return LangUtils.equals(this.host, that.host) && LangUtils.equals(this.proxyHost, that.proxyHost) && LangUtils.equals(this.localAddress, that.localAddress);
    }
    
    public synchronized int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.host);
        hash = LangUtils.hashCode(hash, this.proxyHost);
        hash = LangUtils.hashCode(hash, this.localAddress);
        return hash;
    }
    
    static {
        ANY_HOST_CONFIGURATION = new HostConfiguration();
    }
}
