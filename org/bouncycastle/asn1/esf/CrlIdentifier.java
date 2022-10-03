package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Object;

public class CrlIdentifier extends ASN1Object
{
    private X500Name crlIssuer;
    private ASN1UTCTime crlIssuedTime;
    private ASN1Integer crlNumber;
    
    public static CrlIdentifier getInstance(final Object o) {
        if (o instanceof CrlIdentifier) {
            return (CrlIdentifier)o;
        }
        if (o != null) {
            return new CrlIdentifier(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private CrlIdentifier(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 2 || asn1Sequence.size() > 3) {
            throw new IllegalArgumentException();
        }
        this.crlIssuer = X500Name.getInstance(asn1Sequence.getObjectAt(0));
        this.crlIssuedTime = ASN1UTCTime.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            this.crlNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(2));
        }
    }
    
    public CrlIdentifier(final X500Name x500Name, final ASN1UTCTime asn1UTCTime) {
        this(x500Name, asn1UTCTime, null);
    }
    
    public CrlIdentifier(final X500Name crlIssuer, final ASN1UTCTime crlIssuedTime, final BigInteger bigInteger) {
        this.crlIssuer = crlIssuer;
        this.crlIssuedTime = crlIssuedTime;
        if (null != bigInteger) {
            this.crlNumber = new ASN1Integer(bigInteger);
        }
    }
    
    public X500Name getCrlIssuer() {
        return this.crlIssuer;
    }
    
    public ASN1UTCTime getCrlIssuedTime() {
        return this.crlIssuedTime;
    }
    
    public BigInteger getCrlNumber() {
        if (null == this.crlNumber) {
            return null;
        }
        return this.crlNumber.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.crlIssuer.toASN1Primitive());
        asn1EncodableVector.add(this.crlIssuedTime);
        if (null != this.crlNumber) {
            asn1EncodableVector.add(this.crlNumber);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
