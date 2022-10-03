package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class Gost2814789KeyWrapParameters extends ASN1Object
{
    private final ASN1ObjectIdentifier encryptionParamSet;
    private final byte[] ukm;
    
    private Gost2814789KeyWrapParameters(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() == 2) {
            this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
            this.ukm = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(1)).getOctets();
        }
        else {
            if (asn1Sequence.size() != 1) {
                throw new IllegalArgumentException("unknown sequence length: " + asn1Sequence.size());
            }
            this.encryptionParamSet = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
            this.ukm = null;
        }
    }
    
    public static Gost2814789KeyWrapParameters getInstance(final Object o) {
        if (o instanceof Gost2814789KeyWrapParameters) {
            return (Gost2814789KeyWrapParameters)o;
        }
        if (o != null) {
            return new Gost2814789KeyWrapParameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Gost2814789KeyWrapParameters(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this(asn1ObjectIdentifier, null);
    }
    
    public Gost2814789KeyWrapParameters(final ASN1ObjectIdentifier encryptionParamSet, final byte[] array) {
        this.encryptionParamSet = encryptionParamSet;
        this.ukm = Arrays.clone(array);
    }
    
    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }
    
    public byte[] getUkm() {
        return this.ukm;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.encryptionParamSet);
        if (this.ukm != null) {
            asn1EncodableVector.add(new DEROctetString(this.ukm));
        }
        return new DERSequence(asn1EncodableVector);
    }
}
