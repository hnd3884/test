package org.openjsse.com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;

@Deprecated
public class Handler extends sun.net.www.protocol.https.Handler
{
    public Handler() {
    }
    
    public Handler(final String proxy, final int port) {
        super(proxy, port);
    }
    
    @Override
    protected URLConnection openConnection(final URL u) throws IOException {
        return this.openConnection(u, null);
    }
    
    @Override
    protected URLConnection openConnection(final URL u, final Proxy p) throws IOException {
        return new HttpsURLConnectionOldImpl(u, p, this);
    }
}
