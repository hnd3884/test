package com.sun.net.ssl.internal.www.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.Proxy;
import sun.net.www.protocol.http.Handler;
import java.net.URL;
import com.sun.net.ssl.HttpsURLConnection;
import sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection
{
    public HttpsURLConnection httpsURLConnection;
    
    DelegateHttpsURLConnection(final URL url, final Handler handler, final HttpsURLConnection httpsURLConnection) throws IOException {
        this(url, null, handler, httpsURLConnection);
    }
    
    DelegateHttpsURLConnection(final URL url, final Proxy proxy, final Handler handler, final HttpsURLConnection httpsURLConnection) throws IOException {
        super(url, proxy, handler);
        this.httpsURLConnection = httpsURLConnection;
    }
    
    @Override
    protected SSLSocketFactory getSSLSocketFactory() {
        return this.httpsURLConnection.getSSLSocketFactory();
    }
    
    @Override
    protected HostnameVerifier getHostnameVerifier() {
        return new VerifierWrapper(this.httpsURLConnection.getHostnameVerifier());
    }
    
    protected void dispose() throws Throwable {
        super.finalize();
    }
}
