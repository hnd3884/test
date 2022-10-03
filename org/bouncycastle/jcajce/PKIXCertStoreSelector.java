package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.cert.X509CertSelector;
import java.security.cert.CertStoreException;
import java.util.Collection;
import java.security.cert.CertStore;
import java.security.cert.CertSelector;
import org.bouncycastle.util.Selector;
import java.security.cert.Certificate;

public class PKIXCertStoreSelector<T extends Certificate> implements Selector<T>
{
    private final CertSelector baseSelector;
    
    private PKIXCertStoreSelector(final CertSelector baseSelector) {
        this.baseSelector = baseSelector;
    }
    
    public boolean match(final Certificate certificate) {
        return this.baseSelector.match(certificate);
    }
    
    public Object clone() {
        return new PKIXCertStoreSelector(this.baseSelector);
    }
    
    public static Collection<? extends Certificate> getCertificates(final PKIXCertStoreSelector pkixCertStoreSelector, final CertStore certStore) throws CertStoreException {
        return certStore.getCertificates(new SelectorClone(pkixCertStoreSelector));
    }
    
    public static class Builder
    {
        private final CertSelector baseSelector;
        
        public Builder(final CertSelector certSelector) {
            this.baseSelector = (CertSelector)certSelector.clone();
        }
        
        public PKIXCertStoreSelector<? extends Certificate> build() {
            return new PKIXCertStoreSelector<Certificate>(this.baseSelector, null);
        }
    }
    
    private static class SelectorClone extends X509CertSelector
    {
        private final PKIXCertStoreSelector selector;
        
        SelectorClone(final PKIXCertStoreSelector selector) {
            this.selector = selector;
            if (selector.baseSelector instanceof X509CertSelector) {
                final X509CertSelector x509CertSelector = (X509CertSelector)selector.baseSelector;
                this.setAuthorityKeyIdentifier(x509CertSelector.getAuthorityKeyIdentifier());
                this.setBasicConstraints(x509CertSelector.getBasicConstraints());
                this.setCertificate(x509CertSelector.getCertificate());
                this.setCertificateValid(x509CertSelector.getCertificateValid());
                this.setKeyUsage(x509CertSelector.getKeyUsage());
                this.setMatchAllSubjectAltNames(x509CertSelector.getMatchAllSubjectAltNames());
                this.setPrivateKeyValid(x509CertSelector.getPrivateKeyValid());
                this.setSerialNumber(x509CertSelector.getSerialNumber());
                this.setSubjectKeyIdentifier(x509CertSelector.getSubjectKeyIdentifier());
                this.setSubjectPublicKey(x509CertSelector.getSubjectPublicKey());
                try {
                    this.setExtendedKeyUsage(x509CertSelector.getExtendedKeyUsage());
                    this.setIssuer(x509CertSelector.getIssuerAsBytes());
                    this.setNameConstraints(x509CertSelector.getNameConstraints());
                    this.setPathToNames(x509CertSelector.getPathToNames());
                    this.setPolicy(x509CertSelector.getPolicy());
                    this.setSubject(x509CertSelector.getSubjectAsBytes());
                    this.setSubjectAlternativeNames(x509CertSelector.getSubjectAlternativeNames());
                    this.setSubjectPublicKeyAlgID(x509CertSelector.getSubjectPublicKeyAlgID());
                }
                catch (final IOException ex) {
                    throw new IllegalStateException("base selector invalid: " + ex.getMessage(), ex);
                }
            }
        }
        
        @Override
        public boolean match(final Certificate certificate) {
            return (this.selector == null) ? (certificate != null) : this.selector.match(certificate);
        }
    }
}
