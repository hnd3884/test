package sun.net.www.protocol.ftp;

import java.io.IOException;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URL;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler
{
    @Override
    protected int getDefaultPort() {
        return 21;
    }
    
    @Override
    protected boolean equals(final URL url, final URL url2) {
        final String userInfo = url.getUserInfo();
        final String userInfo2 = url2.getUserInfo();
        return super.equals(url, url2) && ((userInfo != null) ? userInfo.equals(userInfo2) : (userInfo2 == null));
    }
    
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        return this.openConnection(url, null);
    }
    
    @Override
    protected URLConnection openConnection(final URL url, final Proxy proxy) throws IOException {
        return new FtpURLConnection(url, proxy);
    }
}
