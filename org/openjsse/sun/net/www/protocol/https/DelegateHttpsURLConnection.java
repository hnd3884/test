package org.openjsse.sun.net.www.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Proxy;
import sun.net.www.protocol.http.Handler;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection
{
    public HttpsURLConnection httpsURLConnection;
    
    DelegateHttpsURLConnection(final URL url, final Handler handler, final HttpsURLConnection httpsURLConnection) throws IOException {
        this(url, null, handler, httpsURLConnection);
    }
    
    DelegateHttpsURLConnection(final URL url, final Proxy p, final Handler handler, final HttpsURLConnection httpsURLConnection) throws IOException {
        super(url, p, handler);
        this.httpsURLConnection = httpsURLConnection;
    }
    
    @Override
    protected SSLSocketFactory getSSLSocketFactory() {
        return this.httpsURLConnection.getSSLSocketFactory();
    }
    
    @Override
    protected HostnameVerifier getHostnameVerifier() {
        return this.httpsURLConnection.getHostnameVerifier();
    }
    
    protected void dispose() throws Throwable {
        super.finalize();
    }
}
