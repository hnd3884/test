package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1Object;

public class XMSSMTPublicKey extends ASN1Object
{
    private final byte[] publicSeed;
    private final byte[] root;
    
    public XMSSMTPublicKey(final byte[] array, final byte[] array2) {
        this.publicSeed = Arrays.clone(array);
        this.root = Arrays.clone(array2);
    }
    
    private XMSSMTPublicKey(final ASN1Sequence asn1Sequence) {
        if (!ASN1Integer.getInstance(asn1Sequence.getObjectAt(0)).getValue().equals(BigInteger.valueOf(0L))) {
            throw new IllegalArgumentException("unknown version of sequence");
        }
        this.publicSeed = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1)).getOctets());
        this.root = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2)).getOctets());
    }
    
    public static XMSSMTPublicKey getInstance(final Object o) {
        if (o instanceof XMSSMTPublicKey) {
            return (XMSSMTPublicKey)o;
        }
        if (o != null) {
            return new XMSSMTPublicKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public byte[] getPublicSeed() {
        return Arrays.clone(this.publicSeed);
    }
    
    public byte[] getRoot() {
        return Arrays.clone(this.root);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(0L));
        asn1EncodableVector.add(new DEROctetString(this.publicSeed));
        asn1EncodableVector.add(new DEROctetString(this.root));
        return new DERSequence(asn1EncodableVector);
    }
}
