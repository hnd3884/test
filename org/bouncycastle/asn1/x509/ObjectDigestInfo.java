package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;

public class ObjectDigestInfo extends ASN1Object
{
    public static final int publicKey = 0;
    public static final int publicKeyCert = 1;
    public static final int otherObjectDigest = 2;
    ASN1Enumerated digestedObjectType;
    ASN1ObjectIdentifier otherObjectTypeID;
    AlgorithmIdentifier digestAlgorithm;
    DERBitString objectDigest;
    
    public static ObjectDigestInfo getInstance(final Object o) {
        if (o instanceof ObjectDigestInfo) {
            return (ObjectDigestInfo)o;
        }
        if (o != null) {
            return new ObjectDigestInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static ObjectDigestInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public ObjectDigestInfo(final int n, final ASN1ObjectIdentifier otherObjectTypeID, final AlgorithmIdentifier digestAlgorithm, final byte[] array) {
        this.digestedObjectType = new ASN1Enumerated(n);
        if (n == 2) {
            this.otherObjectTypeID = otherObjectTypeID;
        }
        this.digestAlgorithm = digestAlgorithm;
        this.objectDigest = new DERBitString(array);
    }
    
    private ObjectDigestInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() > 4 || asn1Sequence.size() < 3) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.digestedObjectType = ASN1Enumerated.getInstance(asn1Sequence.getObjectAt(0));
        int n = 0;
        if (asn1Sequence.size() == 4) {
            this.otherObjectTypeID = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(1));
            ++n;
        }
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1 + n));
        this.objectDigest = DERBitString.getInstance(asn1Sequence.getObjectAt(2 + n));
    }
    
    public ASN1Enumerated getDigestedObjectType() {
        return this.digestedObjectType;
    }
    
    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        return this.otherObjectTypeID;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }
    
    public DERBitString getObjectDigest() {
        return this.objectDigest;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.digestedObjectType);
        if (this.otherObjectTypeID != null) {
            asn1EncodableVector.add(this.otherObjectTypeID);
        }
        asn1EncodableVector.add(this.digestAlgorithm);
        asn1EncodableVector.add(this.objectDigest);
        return new DERSequence(asn1EncodableVector);
    }
}
