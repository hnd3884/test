package com.unboundid.util.ssl;

import java.security.cert.CertificateException;
import java.util.Date;
import java.security.cert.X509Certificate;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import javax.net.ssl.X509TrustManager;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TrustAllTrustManager implements X509TrustManager, Serializable
{
    private static final X509Certificate[] NO_CERTIFICATES;
    private static final long serialVersionUID = -1295254056169520318L;
    private final boolean examineValidityDates;
    
    public TrustAllTrustManager() {
        this.examineValidityDates = false;
    }
    
    public TrustAllTrustManager(final boolean examineValidityDates) {
        this.examineValidityDates = examineValidityDates;
    }
    
    public boolean examineValidityDates() {
        return this.examineValidityDates;
    }
    
    @Override
    public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (this.examineValidityDates) {
            final Date currentDate = new Date();
            for (final X509Certificate c : chain) {
                c.checkValidity(currentDate);
            }
        }
    }
    
    @Override
    public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        if (this.examineValidityDates) {
            final Date currentDate = new Date();
            for (final X509Certificate c : chain) {
                c.checkValidity(currentDate);
            }
        }
    }
    
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return TrustAllTrustManager.NO_CERTIFICATES;
    }
    
    static {
        NO_CERTIFICATES = new X509Certificate[0];
    }
}
