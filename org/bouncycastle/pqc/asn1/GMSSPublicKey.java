package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class GMSSPublicKey extends ASN1Object
{
    private ASN1Integer version;
    private byte[] publicKey;
    
    private GMSSPublicKey(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("size of seq = " + asn1Sequence.size());
        }
        this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.publicKey = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1)).getOctets();
    }
    
    public GMSSPublicKey(final byte[] publicKey) {
        this.version = new ASN1Integer(0L);
        this.publicKey = publicKey;
    }
    
    public static GMSSPublicKey getInstance(final Object o) {
        if (o instanceof GMSSPublicKey) {
            return (GMSSPublicKey)o;
        }
        if (o != null) {
            return new GMSSPublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public byte[] getPublicKey() {
        return Arrays.clone(this.publicKey);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(new DEROctetString(this.publicKey));
        return new DERSequence(asn1EncodableVector);
    }
}
