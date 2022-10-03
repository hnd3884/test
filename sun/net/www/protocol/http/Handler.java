package sun.net.www.protocol.http;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    protected String proxy;
    protected int proxyPort;
    
    @Override
    protected int getDefaultPort() {
        return 80;
    }
    
    public Handler() {
        this.proxy = null;
        this.proxyPort = -1;
    }
    
    public Handler(final String proxy, final int proxyPort) {
        this.proxy = proxy;
        this.proxyPort = proxyPort;
    }
    
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        return this.openConnection(url, null);
    }
    
    @Override
    protected URLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        return new HttpURLConnection(url, proxy, this);
    }
}
