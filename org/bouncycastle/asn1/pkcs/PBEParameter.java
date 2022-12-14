package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class PBEParameter extends ASN1Object
{
    ASN1Integer iterations;
    ASN1OctetString salt;
    
    public PBEParameter(final byte[] array, final int n) {
        if (array.length != 8) {
            throw new IllegalArgumentException("salt length must be 8");
        }
        this.salt = new DEROctetString(array);
        this.iterations = new ASN1Integer(n);
    }
    
    private PBEParameter(final ASN1Sequence asn1Sequence) {
        this.salt = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        this.iterations = (ASN1Integer)asn1Sequence.getObjectAt(1);
    }
    
    public static PBEParameter getInstance(final Object o) {
        if (o instanceof PBEParameter) {
            return (PBEParameter)o;
        }
        if (o != null) {
            return new PBEParameter(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getIterationCount() {
        return this.iterations.getValue();
    }
    
    public byte[] getSalt() {
        return this.salt.getOctets();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.salt);
        asn1EncodableVector.add(this.iterations);
        return new DERSequence(asn1EncodableVector);
    }
}
