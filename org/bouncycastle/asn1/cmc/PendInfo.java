package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;

public class PendInfo extends ASN1Object
{
    private final byte[] pendToken;
    private final ASN1GeneralizedTime pendTime;
    
    public PendInfo(final byte[] array, final ASN1GeneralizedTime pendTime) {
        this.pendToken = Arrays.clone(array);
        this.pendTime = pendTime;
    }
    
    private PendInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pendToken = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0)).getOctets());
        this.pendTime = ASN1GeneralizedTime.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static PendInfo getInstance(final Object o) {
        if (o instanceof PendInfo) {
            return (PendInfo)o;
        }
        if (o != null) {
            return new PendInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DEROctetString(this.pendToken));
        asn1EncodableVector.add(this.pendTime);
        return new DERSequence(asn1EncodableVector);
    }
    
    public byte[] getPendToken() {
        return this.pendToken;
    }
    
    public ASN1GeneralizedTime getPendTime() {
        return this.pendTime;
    }
}
