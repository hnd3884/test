package org.bouncycastle.asn1.sec;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class ECPrivateKeyStructure extends ASN1Object
{
    private ASN1Sequence seq;
    
    public ECPrivateKeyStructure(final ASN1Sequence seq) {
        this.seq = seq;
    }
    
    public ECPrivateKeyStructure(final BigInteger bigInteger) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(1L));
        asn1EncodableVector.add(new DEROctetString(unsignedByteArray));
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    public ECPrivateKeyStructure(final BigInteger bigInteger, final ASN1Encodable asn1Encodable) {
        this(bigInteger, null, asn1Encodable);
    }
    
    public ECPrivateKeyStructure(final BigInteger bigInteger, final DERBitString derBitString, final ASN1Encodable asn1Encodable) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(bigInteger);
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(new ASN1Integer(1L));
        asn1EncodableVector.add(new DEROctetString(unsignedByteArray));
        if (asn1Encodable != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 0, asn1Encodable));
        }
        if (derBitString != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 1, derBitString));
        }
        this.seq = new DERSequence(asn1EncodableVector);
    }
    
    public BigInteger getKey() {
        return new BigInteger(1, ((ASN1OctetString)this.seq.getObjectAt(1)).getOctets());
    }
    
    public DERBitString getPublicKey() {
        return (DERBitString)this.getObjectInTag(1);
    }
    
    public ASN1Primitive getParameters() {
        return this.getObjectInTag(0);
    }
    
    private ASN1Primitive getObjectInTag(final int n) {
        final Enumeration objects = this.seq.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable = objects.nextElement();
            if (asn1Encodable instanceof ASN1TaggedObject) {
                final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Encodable;
                if (asn1TaggedObject.getTagNo() == n) {
                    return asn1TaggedObject.getObject().toASN1Primitive();
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
