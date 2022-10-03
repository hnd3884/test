package org.bouncycastle.x509;

import org.bouncycastle.util.Selector;

public class X509CertPairStoreSelector implements Selector
{
    private X509CertStoreSelector forwardSelector;
    private X509CertStoreSelector reverseSelector;
    private X509CertificatePair certPair;
    
    public X509CertificatePair getCertPair() {
        return this.certPair;
    }
    
    public void setCertPair(final X509CertificatePair certPair) {
        this.certPair = certPair;
    }
    
    public void setForwardSelector(final X509CertStoreSelector forwardSelector) {
        this.forwardSelector = forwardSelector;
    }
    
    public void setReverseSelector(final X509CertStoreSelector reverseSelector) {
        this.reverseSelector = reverseSelector;
    }
    
    public Object clone() {
        final X509CertPairStoreSelector x509CertPairStoreSelector = new X509CertPairStoreSelector();
        x509CertPairStoreSelector.certPair = this.certPair;
        if (this.forwardSelector != null) {
            x509CertPairStoreSelector.setForwardSelector((X509CertStoreSelector)this.forwardSelector.clone());
        }
        if (this.reverseSelector != null) {
            x509CertPairStoreSelector.setReverseSelector((X509CertStoreSelector)this.reverseSelector.clone());
        }
        return x509CertPairStoreSelector;
    }
    
    public boolean match(final Object o) {
        try {
            if (!(o instanceof X509CertificatePair)) {
                return false;
            }
            final X509CertificatePair x509CertificatePair = (X509CertificatePair)o;
            return (this.forwardSelector == null || this.forwardSelector.match((Object)x509CertificatePair.getForward())) && (this.reverseSelector == null || this.reverseSelector.match((Object)x509CertificatePair.getReverse())) && (this.certPair == null || this.certPair.equals(o));
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    public X509CertStoreSelector getForwardSelector() {
        return this.forwardSelector;
    }
    
    public X509CertStoreSelector getReverseSelector() {
        return this.reverseSelector;
    }
}
