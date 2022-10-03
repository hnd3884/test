package org.bouncycastle.asn1.cryptopro;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class GOST28147Parameters extends ASN1Object
{
    private ASN1OctetString iv;
    private ASN1ObjectIdentifier paramSet;
    
    public static GOST28147Parameters getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static GOST28147Parameters getInstance(final Object o) {
        if (o instanceof GOST28147Parameters) {
            return (GOST28147Parameters)o;
        }
        if (o != null) {
            return new GOST28147Parameters(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public GOST28147Parameters(final byte[] array, final ASN1ObjectIdentifier paramSet) {
        this.iv = new DEROctetString(array);
        this.paramSet = paramSet;
    }
    
    @Deprecated
    public GOST28147Parameters(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.iv = (ASN1OctetString)objects.nextElement();
        this.paramSet = (ASN1ObjectIdentifier)objects.nextElement();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.iv);
        asn1EncodableVector.add(this.paramSet);
        return new DERSequence(asn1EncodableVector);
    }
    
    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.paramSet;
    }
    
    public byte[] getIV() {
        return Arrays.clone(this.iv.getOctets());
    }
}
