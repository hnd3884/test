package org.jscep.client;

import java.util.concurrent.atomic.AtomicBoolean;
import java.security.cert.X509Certificate;
import javax.security.auth.callback.Callback;

public final class CertificateVerificationCallback implements Callback
{
    private final X509Certificate caCertificate;
    private final AtomicBoolean verified;
    
    public CertificateVerificationCallback(final X509Certificate caCertificate) {
        this.verified = new AtomicBoolean(false);
        this.caCertificate = caCertificate;
    }
    
    public X509Certificate getCertificate() {
        return this.caCertificate;
    }
    
    public boolean isVerified() {
        return this.verified.get();
    }
    
    public void setVerified(final boolean verified) {
        this.verified.set(verified);
    }
}
