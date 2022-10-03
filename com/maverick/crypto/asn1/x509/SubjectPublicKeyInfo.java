package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DEREncodableVector;
import com.maverick.crypto.asn1.DERSequence;
import com.maverick.crypto.asn1.ASN1EncodableVector;
import java.io.IOException;
import java.io.InputStream;
import com.maverick.crypto.asn1.DERInputStream;
import java.io.ByteArrayInputStream;
import com.maverick.crypto.asn1.DERObject;
import java.util.Enumeration;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERBitString;
import com.maverick.crypto.asn1.DEREncodable;

public class SubjectPublicKeyInfo implements DEREncodable
{
    private AlgorithmIdentifier y;
    private DERBitString z;
    
    public static SubjectPublicKeyInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static SubjectPublicKeyInfo getInstance(final Object o) {
        if (o instanceof SubjectPublicKeyInfo) {
            return (SubjectPublicKeyInfo)o;
        }
        if (o instanceof ASN1Sequence) {
            return new SubjectPublicKeyInfo((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public SubjectPublicKeyInfo(final AlgorithmIdentifier y, final DEREncodable derEncodable) {
        this.z = new DERBitString(derEncodable);
        this.y = y;
    }
    
    public SubjectPublicKeyInfo(final AlgorithmIdentifier y, final byte[] array) {
        this.z = new DERBitString(array);
        this.y = y;
    }
    
    public SubjectPublicKeyInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.y = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.z = (DERBitString)objects.nextElement();
    }
    
    public AlgorithmIdentifier getAlgorithmId() {
        return this.y;
    }
    
    public DERObject getPublicKey() throws IOException {
        return new DERInputStream(new ByteArrayInputStream(this.z.getBytes())).readObject();
    }
    
    public DERBitString getPublicKeyData() {
        return this.z;
    }
    
    public DERObject getDERObject() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.y);
        asn1EncodableVector.add(this.z);
        return new DERSequence(asn1EncodableVector);
    }
}
