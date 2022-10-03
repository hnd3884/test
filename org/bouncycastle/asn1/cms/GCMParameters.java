package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class GCMParameters extends ASN1Object
{
    private byte[] nonce;
    private int icvLen;
    
    public static GCMParameters getInstance(final Object o) {
        if (o instanceof GCMParameters) {
            return (GCMParameters)o;
        }
        if (o != null) {
            return new GCMParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private GCMParameters(final ASN1Sequence asn1Sequence) {
        this.nonce = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0)).getOctets();
        if (asn1Sequence.size() == 2) {
            this.icvLen = ASN1Integer.getInstance(asn1Sequence.getObjectAt(1)).getValue().intValue();
        }
        else {
            this.icvLen = 12;
        }
    }
    
    public GCMParameters(final byte[] array, final int icvLen) {
        this.nonce = Arrays.clone(array);
        this.icvLen = icvLen;
    }
    
    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }
    
    public int getIcvLen() {
        return this.icvLen;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DEROctetString(this.nonce));
        if (this.icvLen != 12) {
            asn1EncodableVector.add(new ASN1Integer(this.icvLen));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
