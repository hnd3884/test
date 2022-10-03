package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class DigestedData extends ASN1Object
{
    private ASN1Integer version;
    private AlgorithmIdentifier digestAlgorithm;
    private ContentInfo encapContentInfo;
    private ASN1OctetString digest;
    
    public DigestedData(final AlgorithmIdentifier digestAlgorithm, final ContentInfo encapContentInfo, final byte[] array) {
        this.version = new ASN1Integer(0L);
        this.digestAlgorithm = digestAlgorithm;
        this.encapContentInfo = encapContentInfo;
        this.digest = new DEROctetString(array);
    }
    
    private DigestedData(final ASN1Sequence asn1Sequence) {
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(0);
        this.digestAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.encapContentInfo = ContentInfo.getInstance(asn1Sequence.getObjectAt(2));
        this.digest = ASN1OctetString.getInstance(asn1Sequence.getObjectAt(3));
    }
    
    public static DigestedData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static DigestedData getInstance(final Object o) {
        if (o instanceof DigestedData) {
            return (DigestedData)o;
        }
        if (o != null) {
            return new DigestedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public AlgorithmIdentifier getDigestAlgorithm() {
        return this.digestAlgorithm;
    }
    
    public ContentInfo getEncapContentInfo() {
        return this.encapContentInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.digestAlgorithm);
        asn1EncodableVector.add(this.encapContentInfo);
        asn1EncodableVector.add(this.digest);
        return new BERSequence(asn1EncodableVector);
    }
    
    public byte[] getDigest() {
        return this.digest.getOctets();
    }
}
