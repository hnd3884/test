package org.bouncycastle.x509;

import java.io.IOException;
import java.util.Collection;
import java.security.cert.CRL;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.x509.extension.X509ExtensionUtil;
import org.bouncycastle.asn1.x509.X509Extensions;
import java.security.cert.X509CRL;
import java.math.BigInteger;
import org.bouncycastle.util.Selector;
import java.security.cert.X509CRLSelector;

public class X509CRLStoreSelector extends X509CRLSelector implements Selector
{
    private boolean deltaCRLIndicator;
    private boolean completeCRLEnabled;
    private BigInteger maxBaseCRLNumber;
    private byte[] issuingDistributionPoint;
    private boolean issuingDistributionPointEnabled;
    private X509AttributeCertificate attrCertChecking;
    
    public X509CRLStoreSelector() {
        this.deltaCRLIndicator = false;
        this.completeCRLEnabled = false;
        this.maxBaseCRLNumber = null;
        this.issuingDistributionPoint = null;
        this.issuingDistributionPointEnabled = false;
    }
    
    public boolean isIssuingDistributionPointEnabled() {
        return this.issuingDistributionPointEnabled;
    }
    
    public void setIssuingDistributionPointEnabled(final boolean issuingDistributionPointEnabled) {
        this.issuingDistributionPointEnabled = issuingDistributionPointEnabled;
    }
    
    public void setAttrCertificateChecking(final X509AttributeCertificate attrCertChecking) {
        this.attrCertChecking = attrCertChecking;
    }
    
    public X509AttributeCertificate getAttrCertificateChecking() {
        return this.attrCertChecking;
    }
    
    public boolean match(final Object o) {
        if (!(o instanceof X509CRL)) {
            return false;
        }
        final X509CRL x509CRL = (X509CRL)o;
        ASN1Integer instance = null;
        try {
            final byte[] extensionValue = x509CRL.getExtensionValue(X509Extensions.DeltaCRLIndicator.getId());
            if (extensionValue != null) {
                instance = ASN1Integer.getInstance(X509ExtensionUtil.fromExtensionValue(extensionValue));
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
            final byte[] extensionValue2 = x509CRL.getExtensionValue(X509Extensions.IssuingDistributionPoint.getId());
            if (this.issuingDistributionPoint == null) {
                if (extensionValue2 != null) {
                    return false;
                }
            }
            else if (!Arrays.areEqual(extensionValue2, this.issuingDistributionPoint)) {
                return false;
            }
        }
        return super.match((CRL)o);
    }
    
    @Override
    public boolean match(final CRL crl) {
        return this.match((Object)crl);
    }
    
    public boolean isDeltaCRLIndicatorEnabled() {
        return this.deltaCRLIndicator;
    }
    
    public void setDeltaCRLIndicatorEnabled(final boolean deltaCRLIndicator) {
        this.deltaCRLIndicator = deltaCRLIndicator;
    }
    
    public static X509CRLStoreSelector getInstance(final X509CRLSelector x509CRLSelector) {
        if (x509CRLSelector == null) {
            throw new IllegalArgumentException("cannot create from null selector");
        }
        final X509CRLStoreSelector x509CRLStoreSelector = new X509CRLStoreSelector();
        x509CRLStoreSelector.setCertificateChecking(x509CRLSelector.getCertificateChecking());
        x509CRLStoreSelector.setDateAndTime(x509CRLSelector.getDateAndTime());
        try {
            x509CRLStoreSelector.setIssuerNames(x509CRLSelector.getIssuerNames());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException(ex.getMessage());
        }
        x509CRLStoreSelector.setIssuers(x509CRLSelector.getIssuers());
        x509CRLStoreSelector.setMaxCRLNumber(x509CRLSelector.getMaxCRL());
        x509CRLStoreSelector.setMinCRLNumber(x509CRLSelector.getMinCRL());
        return x509CRLStoreSelector;
    }
    
    @Override
    public Object clone() {
        final X509CRLStoreSelector instance = getInstance(this);
        instance.deltaCRLIndicator = this.deltaCRLIndicator;
        instance.completeCRLEnabled = this.completeCRLEnabled;
        instance.maxBaseCRLNumber = this.maxBaseCRLNumber;
        instance.attrCertChecking = this.attrCertChecking;
        instance.issuingDistributionPointEnabled = this.issuingDistributionPointEnabled;
        instance.issuingDistributionPoint = Arrays.clone(this.issuingDistributionPoint);
        return instance;
    }
    
    public boolean isCompleteCRLEnabled() {
        return this.completeCRLEnabled;
    }
    
    public void setCompleteCRLEnabled(final boolean completeCRLEnabled) {
        this.completeCRLEnabled = completeCRLEnabled;
    }
    
    public BigInteger getMaxBaseCRLNumber() {
        return this.maxBaseCRLNumber;
    }
    
    public void setMaxBaseCRLNumber(final BigInteger maxBaseCRLNumber) {
        this.maxBaseCRLNumber = maxBaseCRLNumber;
    }
    
    public byte[] getIssuingDistributionPoint() {
        return Arrays.clone(this.issuingDistributionPoint);
    }
    
    public void setIssuingDistributionPoint(final byte[] array) {
        this.issuingDistributionPoint = Arrays.clone(array);
    }
}
