package org.openjsse.sun.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;

public class Handler extends sun.net.www.protocol.http.Handler
{
    protected String proxy;
    protected int proxyPort;
    
    @Override
    protected int getDefaultPort() {
        return 443;
    }
    
    public Handler() {
        this.proxy = null;
        this.proxyPort = -1;
    }
    
    public Handler(final String proxy, final int port) {
        this.proxy = proxy;
        this.proxyPort = port;
    }
    
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return this.openConnection(u, null);
    }
    
    @Override
    protected URLConnection openConnection(final URL u, final Proxy p) throws IOException {
        return new HttpsURLConnectionImpl(u, p, this);
    }
}
