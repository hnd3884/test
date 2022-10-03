package javapns.communication;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

class ServerTrustingTrustManager implements X509TrustManager
{
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        throw new CertificateException("Client is not trusted.");
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
