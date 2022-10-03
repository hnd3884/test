package com.sun.net.ssl.internal.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;

public class Handler extends sun.net.www.protocol.https.Handler
{
    public Handler() {
    }
    
    public Handler(final String s, final int n) {
        super(s, n);
    }
    
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        return this.openConnection(url, null);
    }
    
    @Override
    protected URLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        return new HttpsURLConnectionOldImpl(url, proxy, this);
    }
}
