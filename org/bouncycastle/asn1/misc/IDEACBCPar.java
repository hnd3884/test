package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class IDEACBCPar extends ASN1Object
{
    ASN1OctetString iv;
    
    public static IDEACBCPar getInstance(final Object o) {
        if (o instanceof IDEACBCPar) {
            return (IDEACBCPar)o;
        }
        if (o != null) {
            return new IDEACBCPar(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public IDEACBCPar(final byte[] array) {
        this.iv = new DEROctetString(array);
    }
    
    public IDEACBCPar(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() == 1) {
            this.iv = (ASN1OctetString)asn1Sequence.getObjectAt(0);
        }
        else {
            this.iv = null;
        }
    }
    
    public byte[] getIV() {
        if (this.iv != null) {
            return Arrays.clone(this.iv.getOctets());
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.iv != null) {
            asn1EncodableVector.add(this.iv);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
