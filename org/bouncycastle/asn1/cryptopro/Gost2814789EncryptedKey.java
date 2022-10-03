package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class Gost2814789EncryptedKey extends ASN1Object
{
    private final byte[] encryptedKey;
    private final byte[] maskKey;
    private final byte[] macKey;
    
    private Gost2814789EncryptedKey(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() == 2) {
            this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0)).getOctets());
            this.macKey = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1)).getOctets());
            this.maskKey = null;
        }
        else {
            if (asn1Sequence.size() != 3) {
                throw new IllegalArgumentException("unknown sequence length: " + asn1Sequence.size());
            }
            this.encryptedKey = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(0)).getOctets());
            this.maskKey = Arrays.clone(ASN1OctetString.getInstance(ASN1TaggedObject.getInstance(asn1Sequence.getObjectAt(1)), false).getOctets());
            this.macKey = Arrays.clone(ASN1OctetString.getInstance(asn1Sequence.getObjectAt(2)).getOctets());
        }
    }
    
    public static Gost2814789EncryptedKey getInstance(final Object o) {
        if (o instanceof Gost2814789EncryptedKey) {
            return (Gost2814789EncryptedKey)o;
        }
        if (o != null) {
            return new Gost2814789EncryptedKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Gost2814789EncryptedKey(final byte[] array, final byte[] array2) {
        this(array, null, array2);
    }
    
    public Gost2814789EncryptedKey(final byte[] array, final byte[] array2, final byte[] array3) {
        this.encryptedKey = Arrays.clone(array);
        this.maskKey = Arrays.clone(array2);
        this.macKey = Arrays.clone(array3);
    }
    
    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }
    
    public byte[] getMaskKey() {
        return this.maskKey;
    }
    
    public byte[] getMacKey() {
        return this.macKey;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new DEROctetString(this.encryptedKey));
        if (this.maskKey != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, new DEROctetString(this.encryptedKey)));
        }
        asn1EncodableVector.add(new DEROctetString(this.macKey));
        return new DERSequence(asn1EncodableVector);
    }
}
