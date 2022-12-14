package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class SecretKeyData extends ASN1Object
{
    private final ASN1ObjectIdentifier keyAlgorithm;
    private final ASN1OctetString keyBytes;
    
    public SecretKeyData(final ASN1ObjectIdentifier keyAlgorithm, final byte[] array) {
        this.keyAlgorithm = keyAlgorithm;
        this.keyBytes = new DEROctetString(Arrays.clone(array));
    }
    
    private SecretKeyData(final ASN1Sequence asn1Sequence) {
        this.keyAlgorithm = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.keyBytes = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static SecretKeyData getInstance(final Object o) {
        if (o instanceof SecretKeyData) {
            return (SecretKeyData)o;
        }
        if (o != null) {
            return new SecretKeyData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public byte[] getKeyBytes() {
        return Arrays.clone(this.keyBytes.getOctets());
    }
    
    public ASN1ObjectIdentifier getKeyAlgorithm() {
        return this.keyAlgorithm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyAlgorithm);
        asn1EncodableVector.add(this.keyBytes);
        return new DERSequence(asn1EncodableVector);
    }
}
