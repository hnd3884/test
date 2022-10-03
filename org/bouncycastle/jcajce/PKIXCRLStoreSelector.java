package org.bouncycastle.jcajce;

import java.security.cert.CertStoreException;
import java.util.Collection;
import java.security.cert.CertStore;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import java.security.cert.X509CRL;
import java.math.BigInteger;
import java.security.cert.CRLSelector;
import org.bouncycastle.util.Selector;
import java.security.cert.CRL;

public class PKIXCRLStoreSelector<T extends CRL> implements Selector<T>
{
    private final CRLSelector baseSelector;
    private final boolean deltaCRLIndicator;
    private final boolean completeCRLEnabled;
    private final BigInteger maxBaseCRLNumber;
    private final byte[] issuingDistributionPoint;
    private final boolean issuingDistributionPointEnabled;
    
    private PKIXCRLStoreSelector(final Builder builder) {
        this.baseSelector = builder.baseSelector;
        this.deltaCRLIndicator = builder.deltaCRLIndicator;
        this.completeCRLEnabled = builder.completeCRLEnabled;
        this.maxBaseCRLNumber = builder.maxBaseCRLNumber;
        this.issuingDistributionPoint = builder.issuingDistributionPoint;
        this.issuingDistributionPointEnabled = builder.issuingDistributionPointEnabled;
    }
    
    public boolean isIssuingDistributionPointEnabled() {
        return this.issuingDistributionPointEnabled;
    }
    
    public boolean match(final CRL crl) {
        if (!(crl instanceof X509CRL)) {
            return this.baseSelector.match(crl);
        }
        final X509CRL x509CRL = (X509CRL)crl;
        ASN1Integer instance = null;
        try {
            final byte[] extensionValue = x509CRL.getExtensionValue(Extension.deltaCRLIndicator.getId());
            if (extensionValue != null) {
                instance = ASN1Integer.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets());
            }
        }
        catch (final Exception ex) {
            return false;
        }
        if (this.isDeltaCRLIndicatorEnabled() && instance == null) {
            return false;
        }
        if (this.isCompleteCRLEnabled() && instance != null) {
            return false;
        }
        if (instance != null && this.maxBaseCRLNumber != null && instance.getPositiveValue().compareTo(this.maxBaseCRLNumber) == 1) {
            return false;
        }
        if (this.issuingDistributionPointEnabled) {
            final byte[] extensionValue2 = x509CRL.getExtensionValue(Extension.issuingDistributionPoint.getId());
            if (this.issuingDistributionPoint == null) {
                if (extensionValue2 != null) {
                    return false;
                }
            }
            else if (!Arrays.areEqual(extensionValue2, this.issuingDistributionPoint)) {
                return false;
            }
        }
        return this.baseSelector.match(crl);
    }
    
    public boolean isDeltaCRLIndicatorEnabled() {
        return this.deltaCRLIndicator;
    }
    
    public Object clone() {
        return this;
    }
    
    public boolean isCompleteCRLEnabled() {
        return this.completeCRLEnabled;
    }
    
    public BigInteger getMaxBaseCRLNumber() {
        return this.maxBaseCRLNumber;
    }
    
    public byte[] getIssuingDistributionPoint() {
        return Arrays.clone(this.issuingDistributionPoint);
    }
    
    public X509Certificate getCertificateChecking() {
        if (this.baseSelector instanceof X509CRLSelector) {
            return ((X509CRLSelector)this.baseSelector).getCertificateChecking();
        }
        return null;
    }
    
    public static Collection<? extends CRL> getCRLs(final PKIXCRLStoreSelector pkixcrlStoreSelector, final CertStore certStore) throws CertStoreException {
        return certStore.getCRLs(new SelectorClone(pkixcrlStoreSelector));
    }
    
    public static class Builder
    {
        private final CRLSelector baseSelector;
        private boolean deltaCRLIndicator;
        private boolean completeCRLEnabled;
        private BigInteger maxBaseCRLNumber;
        private byte[] issuingDistributionPoint;
        private boolean issuingDistributionPointEnabled;
        
        public Builder(final CRLSelector crlSelector) {
            this.deltaCRLIndicator = false;
            this.completeCRLEnabled = false;
            this.maxBaseCRLNumber = null;
            this.issuingDistributionPoint = null;
            this.issuingDistributionPointEnabled = false;
            this.baseSelector = (CRLSelector)crlSelector.clone();
        }
        
        public Builder setCompleteCRLEnabled(final boolean completeCRLEnabled) {
            this.completeCRLEnabled = completeCRLEnabled;
            return this;
        }
        
        public Builder setDeltaCRLIndicatorEnabled(final boolean deltaCRLIndicator) {
            this.deltaCRLIndicator = deltaCRLIndicator;
            return this;
        }
        
        public void setMaxBaseCRLNumber(final BigInteger maxBaseCRLNumber) {
            this.maxBaseCRLNumber = maxBaseCRLNumber;
        }
        
        public void setIssuingDistributionPointEnabled(final boolean issuingDistributionPointEnabled) {
            this.issuingDistributionPointEnabled = issuingDistributionPointEnabled;
        }
        
        public void setIssuingDistributionPoint(final byte[] array) {
            this.issuingDistributionPoint = Arrays.clone(array);
        }
        
        public PKIXCRLStoreSelector<? extends CRL> build() {
            return new PKIXCRLStoreSelector<CRL>(this, null);
        }
    }
    
    private static class SelectorClone extends X509CRLSelector
    {
        private final PKIXCRLStoreSelector selector;
        
        SelectorClone(final PKIXCRLStoreSelector selector) {
            this.selector = selector;
            if (selector.baseSelector instanceof X509CRLSelector) {
                final X509CRLSelector x509CRLSelector = (X509CRLSelector)selector.baseSelector;
                this.setCertificateChecking(x509CRLSelector.getCertificateChecking());
                this.setDateAndTime(x509CRLSelector.getDateAndTime());
                this.setIssuers(x509CRLSelector.getIssuers());
                this.setMinCRLNumber(x509CRLSelector.getMinCRL());
                this.setMaxCRLNumber(x509CRLSelector.getMaxCRL());
            }
        }
        
        @Override
        public boolean match(final CRL crl) {
            return (this.selector == null) ? (crl != null) : this.selector.match(crl);
        }
    }
}
