package org.apache.commons.httpclient;

import org.apache.commons.httpclient.protocol.Protocol;

public class ProxyHost extends HttpHost
{
    public ProxyHost(final ProxyHost httpproxy) {
        super(httpproxy);
    }
    
    public ProxyHost(final String hostname, final int port) {
        super(hostname, port, Protocol.getProtocol("http"));
    }
    
    public ProxyHost(final String hostname) {
        this(hostname, -1);
    }
    
    public Object clone() throws CloneNotSupportedException {
        final ProxyHost copy = (ProxyHost)super.clone();
        return copy;
    }
}
