package com.unboundid.util.ssl;

import java.util.Date;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ValidityDateTrustManager implements X509TrustManager
{
    private static final X509Certificate[] NO_CERTIFICATES;
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        checkCertificateValidity(chain[0]);
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        checkCertificateValidity(chain[0]);
    }
    
    private static void checkCertificateValidity(final X509Certificate c) throws CertificateException {
        final Date currentTime = new Date();
        final Date notBefore = c.getNotBefore();
        final Date notAfter = c.getNotAfter();
        if (currentTime.before(notBefore)) {
            throw new CertificateException(SSLMessages.ERR_VALIDITY_TOO_EARLY.get(c.getSubjectX500Principal().getName("RFC2253"), String.valueOf(notBefore)));
        }
        if (currentTime.after(c.getNotAfter())) {
            throw new CertificateException(SSLMessages.ERR_VALIDITY_TOO_LATE.get(c.getSubjectX500Principal().getName("RFC2253"), String.valueOf(notAfter)));
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ValidityDateTrustManager.NO_CERTIFICATES;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
