package org.bouncycastle.cert.selector;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Arrays;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.util.Selector;

public class X509CertificateHolderSelector implements Selector
{
    private byte[] subjectKeyId;
    private X500Name issuer;
    private BigInteger serialNumber;
    
    public X509CertificateHolderSelector(final byte[] array) {
        this(null, null, array);
    }
    
    public X509CertificateHolderSelector(final X500Name x500Name, final BigInteger bigInteger) {
        this(x500Name, bigInteger, null);
    }
    
    public X509CertificateHolderSelector(final X500Name issuer, final BigInteger serialNumber, final byte[] subjectKeyId) {
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.subjectKeyId = subjectKeyId;
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }
    
    public byte[] getSubjectKeyIdentifier() {
        return Arrays.clone(this.subjectKeyId);
    }
    
    @Override
    public int hashCode() {
        int hashCode = Arrays.hashCode(this.subjectKeyId);
        if (this.serialNumber != null) {
            hashCode ^= this.serialNumber.hashCode();
        }
        if (this.issuer != null) {
            hashCode ^= this.issuer.hashCode();
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof X509CertificateHolderSelector)) {
            return false;
        }
        final X509CertificateHolderSelector x509CertificateHolderSelector = (X509CertificateHolderSelector)o;
        return Arrays.areEqual(this.subjectKeyId, x509CertificateHolderSelector.subjectKeyId) && this.equalsObj(this.serialNumber, x509CertificateHolderSelector.serialNumber) && this.equalsObj(this.issuer, x509CertificateHolderSelector.issuer);
    }
    
    private boolean equalsObj(final Object o, final Object o2) {
        return (o != null) ? o.equals(o2) : (o2 == null);
    }
    
    public boolean match(final Object o) {
        if (o instanceof X509CertificateHolder) {
            final X509CertificateHolder x509CertificateHolder = (X509CertificateHolder)o;
            if (this.getSerialNumber() != null) {
                final IssuerAndSerialNumber issuerAndSerialNumber = new IssuerAndSerialNumber(x509CertificateHolder.toASN1Structure());
                return issuerAndSerialNumber.getName().equals((Object)this.issuer) && issuerAndSerialNumber.getSerialNumber().getValue().equals(this.serialNumber);
            }
            if (this.subjectKeyId != null) {
                final Extension extension = x509CertificateHolder.getExtension(Extension.subjectKeyIdentifier);
                if (extension == null) {
                    return Arrays.areEqual(this.subjectKeyId, MSOutlookKeyIdCalculator.calculateKeyId(x509CertificateHolder.getSubjectPublicKeyInfo()));
                }
                return Arrays.areEqual(this.subjectKeyId, ASN1OctetString.getInstance((Object)extension.getParsedValue()).getOctets());
            }
        }
        else if (o instanceof byte[]) {
            return Arrays.areEqual(this.subjectKeyId, (byte[])o);
        }
        return false;
    }
    
    public Object clone() {
        return new X509CertificateHolderSelector(this.issuer, this.serialNumber, this.subjectKeyId);
    }
}
