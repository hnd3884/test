package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.Provider;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;
import org.bouncycastle.cert.X509ContentVerifierProviderBuilder;

public class JcaX509ContentVerifierProviderBuilder implements X509ContentVerifierProviderBuilder
{
    private JcaContentVerifierProviderBuilder builder;
    
    public JcaX509ContentVerifierProviderBuilder() {
        this.builder = new JcaContentVerifierProviderBuilder();
    }
    
    public JcaX509ContentVerifierProviderBuilder setProvider(final Provider provider) {
        this.builder.setProvider(provider);
        return this;
    }
    
    public JcaX509ContentVerifierProviderBuilder setProvider(final String provider) {
        this.builder.setProvider(provider);
        return this;
    }
    
    public ContentVerifierProvider build(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        return this.builder.build(subjectPublicKeyInfo);
    }
    
    public ContentVerifierProvider build(final X509CertificateHolder x509CertificateHolder) throws OperatorCreationException {
        try {
            return this.builder.build(x509CertificateHolder);
        }
        catch (final CertificateException ex) {
            throw new OperatorCreationException("Unable to process certificate: " + ex.getMessage(), ex);
        }
    }
}
