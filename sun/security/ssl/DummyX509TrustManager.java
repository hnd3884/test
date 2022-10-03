package sun.security.ssl;

import javax.net.ssl.SSLEngine;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

final class DummyX509TrustManager extends X509ExtendedTrustManager implements X509TrustManager
{
    static final X509TrustManager INSTANCE;
    
    private DummyX509TrustManager() {
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation avaiable");
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation available");
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation available");
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final Socket socket) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation available");
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation available");
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] array, final String s, final SSLEngine sslEngine) throws CertificateException {
        throw new CertificateException("No X509TrustManager implementation available");
    }
    
    static {
        INSTANCE = new DummyX509TrustManager();
    }
}
