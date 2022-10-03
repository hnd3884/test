package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Object;

public class SubjectPublicKeyInfo extends ASN1Object
{
    private AlgorithmIdentifier algId;
    private DERBitString keyData;
    
    public static SubjectPublicKeyInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static SubjectPublicKeyInfo getInstance(final Object o) {
        if (o instanceof SubjectPublicKeyInfo) {
            return (SubjectPublicKeyInfo)o;
        }
        if (o != null) {
            return new SubjectPublicKeyInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public SubjectPublicKeyInfo(final AlgorithmIdentifier algId, final ASN1Encodable asn1Encodable) throws IOException {
        this.keyData = new DERBitString(asn1Encodable);
        this.algId = algId;
    }
    
    public SubjectPublicKeyInfo(final AlgorithmIdentifier algId, final byte[] array) {
        this.keyData = new DERBitString(array);
        this.algId = algId;
    }
    
    @Deprecated
    public SubjectPublicKeyInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        final Enumeration objects = asn1Sequence.getObjects();
        this.algId = AlgorithmIdentifier.getInstance(objects.nextElement());
        this.keyData = DERBitString.getInstance(objects.nextElement());
    }
    
    public AlgorithmIdentifier getAlgorithm() {
        return this.algId;
    }
    
    @Deprecated
    public AlgorithmIdentifier getAlgorithmId() {
        return this.algId;
    }
    
    public ASN1Primitive parsePublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.keyData.getOctets());
    }
    
    @Deprecated
    public ASN1Primitive getPublicKey() throws IOException {
        return ASN1Primitive.fromByteArray(this.keyData.getOctets());
    }
    
    public DERBitString getPublicKeyData() {
        return this.keyData;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.algId);
        asn1EncodableVector.add(this.keyData);
        return new DERSequence(asn1EncodableVector);
    }
}
