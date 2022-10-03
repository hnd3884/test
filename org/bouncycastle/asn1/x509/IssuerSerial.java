package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class IssuerSerial extends ASN1Object
{
    GeneralNames issuer;
    ASN1Integer serial;
    DERBitString issuerUID;
    
    public static IssuerSerial getInstance(final Object o) {
        if (o instanceof IssuerSerial) {
            return (IssuerSerial)o;
        }
        if (o != null) {
            return new IssuerSerial(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static IssuerSerial getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    private IssuerSerial(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2 && asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.issuer = GeneralNames.getInstance(asn1Sequence.getObjectAt(0));
        this.serial = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() == 3) {
            this.issuerUID = DERBitString.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public IssuerSerial(final X500Name x500Name, final BigInteger bigInteger) {
        this(new GeneralNames(new GeneralName(x500Name)), new ASN1Integer(bigInteger));
    }
    
    public IssuerSerial(final GeneralNames generalNames, final BigInteger bigInteger) {
        this(generalNames, new ASN1Integer(bigInteger));
    }
    
    public IssuerSerial(final GeneralNames issuer, final ASN1Integer serial) {
        this.issuer = issuer;
        this.serial = serial;
    }
    
    public GeneralNames getIssuer() {
        return this.issuer;
    }
    
    public ASN1Integer getSerial() {
        return this.serial;
    }
    
    public DERBitString getIssuerUID() {
        return this.issuerUID;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.issuer);
        asn1EncodableVector.add(this.serial);
        if (this.issuerUID != null) {
            asn1EncodableVector.add(this.issuerUID);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
