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

public class PKCS12PBEParams extends ASN1Object
{
    ASN1Integer iterations;
    ASN1OctetString iv;
    
    public PKCS12PBEParams(final byte[] array, final int n) {
        this.iv = new DEROctetString(array);
        this.iterations = new ASN1Integer(n);
    }
    
    private PKCS12PBEParams(final ASN1Sequence asn1Sequence) {
        this.iv = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        this.iterations = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static PKCS12PBEParams getInstance(final Object o) {
        if (o instanceof PKCS12PBEParams) {
            return (PKCS12PBEParams)o;
        }
        if (o != null) {
            return new PKCS12PBEParams(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getIterations() {
        return this.iterations.getValue();
    }
    
    public byte[] getIV() {
        return this.iv.getOctets();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.iv);
        asn1EncodableVector.add(this.iterations);
        return new DERSequence(asn1EncodableVector);
    }
}
