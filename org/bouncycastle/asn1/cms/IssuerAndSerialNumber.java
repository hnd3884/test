package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.X509Name;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class IssuerAndSerialNumber extends ASN1Object
{
    private X500Name name;
    private ASN1Integer serialNumber;
    
    public static IssuerAndSerialNumber getInstance(final Object o) {
        if (o instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber)o;
        }
        if (o != null) {
            return new IssuerAndSerialNumber(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Deprecated
    public IssuerAndSerialNumber(final ASN1Sequence asn1Sequence) {
        this.name = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        this.serialNumber = (ASN1Integer)asn1Sequence.getObjectAt(1);
    }
    
    public IssuerAndSerialNumber(final Certificate certificate) {
        this.name = certificate.getIssuer();
        this.serialNumber = certificate.getSerialNumber();
    }
    
    @Deprecated
    public IssuerAndSerialNumber(final X509CertificateStructure x509CertificateStructure) {
        this.name = x509CertificateStructure.getIssuer();
        this.serialNumber = x509CertificateStructure.getSerialNumber();
    }
    
    public IssuerAndSerialNumber(final X500Name name, final BigInteger bigInteger) {
        this.name = name;
        this.serialNumber = new ASN1Integer(bigInteger);
    }
    
    @Deprecated
    public IssuerAndSerialNumber(final X509Name x509Name, final BigInteger bigInteger) {
        this.name = X500Name.getInstance(x509Name);
        this.serialNumber = new ASN1Integer(bigInteger);
    }
    
    @Deprecated
    public IssuerAndSerialNumber(final X509Name x509Name, final ASN1Integer serialNumber) {
        this.name = X500Name.getInstance(x509Name);
        this.serialNumber = serialNumber;
    }
    
    public X500Name getName() {
        return this.name;
    }
    
    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.name);
        asn1EncodableVector.add(this.serialNumber);
        return new DERSequence(asn1EncodableVector);
    }
}
