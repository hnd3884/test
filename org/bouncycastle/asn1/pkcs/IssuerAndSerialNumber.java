package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class IssuerAndSerialNumber extends ASN1Object
{
    X500Name name;
    ASN1Integer certSerialNumber;
    
    public static IssuerAndSerialNumber getInstance(final Object o) {
        if (o instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber)o;
        }
        if (o != null) {
            return new IssuerAndSerialNumber(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private IssuerAndSerialNumber(final ASN1Sequence asn1Sequence) {
        this.name = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        this.certSerialNumber = (ASN1Integer)asn1Sequence.getObjectAt(1);
    }
    
    public IssuerAndSerialNumber(final X509Name x509Name, final BigInteger bigInteger) {
        this.name = X500Name.getInstance(x509Name.toASN1Primitive());
        this.certSerialNumber = new ASN1Integer(bigInteger);
    }
    
    public IssuerAndSerialNumber(final X509Name x509Name, final ASN1Integer certSerialNumber) {
        this.name = X500Name.getInstance(x509Name.toASN1Primitive());
        this.certSerialNumber = certSerialNumber;
    }
    
    public IssuerAndSerialNumber(final X500Name name, final BigInteger bigInteger) {
        this.name = name;
        this.certSerialNumber = new ASN1Integer(bigInteger);
    }
    
    public X500Name getName() {
        return this.name;
    }
    
    public ASN1Integer getCertificateSerialNumber() {
        return this.certSerialNumber;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.name);
        asn1EncodableVector.add(this.certSerialNumber);
        return new DERSequence(asn1EncodableVector);
    }
}
