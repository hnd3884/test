package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.LangUtils;
import org.apache.commons.httpclient.protocol.Protocol;

public class HttpHost implements Cloneable
{
    private String hostname;
    private int port;
    private Protocol protocol;
    
    public HttpHost(final String hostname, final int port, final Protocol protocol) {
        this.hostname = null;
        this.port = -1;
        this.protocol = null;
        if (hostname == null) {
            throw new IllegalArgumentException("Host name may not be null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("Protocol may not be null");
        }
        this.hostname = hostname;
        this.protocol = protocol;
        if (port >= 0) {
            this.port = port;
        }
        else {
            this.port = this.protocol.getDefaultPort();
        }
    }
    
    public HttpHost(final String hostname, final int port) {
        this(hostname, port, Protocol.getProtocol("http"));
    }
    
    public HttpHost(final String hostname) {
        this(hostname, -1, Protocol.getProtocol("http"));
    }
    
    public HttpHost(final URI uri) throws URIException {
        this(uri.getHost(), uri.getPort(), Protocol.getProtocol(uri.getScheme()));
    }
    
    public HttpHost(final HttpHost httphost) {
        this.hostname = null;
        this.port = -1;
        this.protocol = null;
        this.init(httphost);
    }
    
    private void init(final HttpHost httphost) {
        this.hostname = httphost.hostname;
        this.port = httphost.port;
        this.protocol = httphost.protocol;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HttpHost copy = (HttpHost)super.clone();
        copy.init(this);
        return copy;
    }
    
    public String getHostName() {
        return this.hostname;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public Protocol getProtocol() {
        return this.protocol;
    }
    
    public String toURI() {
        final StringBuffer buffer = new StringBuffer(50);
        buffer.append(this.protocol.getScheme());
        buffer.append("://");
        buffer.append(this.hostname);
        if (this.port != this.protocol.getDefaultPort()) {
            buffer.append(':');
            buffer.append(this.port);
        }
        return buffer.toString();
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer(50);
        buffer.append(this.toURI());
        return buffer.toString();
    }
    
    public boolean equals(final Object o) {
        if (!(o instanceof HttpHost)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        final HttpHost that = (HttpHost)o;
        return this.hostname.equalsIgnoreCase(that.hostname) && this.port == that.port && this.protocol.equals(that.protocol);
    }
    
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.hostname);
        hash = LangUtils.hashCode(hash, this.port);
        hash = LangUtils.hashCode(hash, this.protocol);
        return hash;
    }
}
