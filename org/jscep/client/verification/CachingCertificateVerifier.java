package org.jscep.client.verification;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.security.cert.Certificate;
import java.util.Map;

public final class CachingCertificateVerifier implements CertificateVerifier
{
    private final Map<Certificate, Boolean> verificationAnswers;
    private final CertificateVerifier delegate;
    
    public CachingCertificateVerifier(final CertificateVerifier delegate) {
        this.delegate = delegate;
        this.verificationAnswers = new HashMap<Certificate, Boolean>();
    }
    
    @Override
    public synchronized boolean verify(final X509Certificate cert) {
        if (this.verificationAnswers.containsKey(cert)) {
            return this.verificationAnswers.get(cert);
        }
        final boolean answer = this.delegate.verify(cert);
        this.verificationAnswers.put(cert, answer);
        return answer;
    }
}
