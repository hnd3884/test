package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CAST5CBCParameters extends ASN1Object
{
    ASN1Integer keyLength;
    ASN1OctetString iv;
    
    public static CAST5CBCParameters getInstance(final Object o) {
        if (o instanceof CAST5CBCParameters) {
            return (CAST5CBCParameters)o;
        }
        if (o != null) {
            return new CAST5CBCParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CAST5CBCParameters(final byte[] array, final int n) {
        this.iv = new DEROctetString(Arrays.clone(array));
        this.keyLength = new ASN1Integer(n);
    }
    
    public CAST5CBCParameters(final ASN1Sequence asn1Sequence) {
        this.iv = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        this.keyLength = (ASN1Integer)asn1Sequence.getObjectAt(1);
    }
    
    public byte[] getIV() {
        return Arrays.clone(this.iv.getOctets());
    }
    
    public int getKeyLength() {
        return this.keyLength.getValue().intValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.iv);
        asn1EncodableVector.add(this.keyLength);
        return new DERSequence(asn1EncodableVector);
    }
}
