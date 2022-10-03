package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CompressedData extends ASN1Object
{
    private ASN1Integer version;
    private AlgorithmIdentifier compressionAlgorithm;
    private ContentInfo encapContentInfo;
    
    public CompressedData(final AlgorithmIdentifier compressionAlgorithm, final ContentInfo encapContentInfo) {
        this.version = new ASN1Integer(0L);
        this.compressionAlgorithm = compressionAlgorithm;
        this.encapContentInfo = encapContentInfo;
    }
    
    private CompressedData(final ASN1Sequence asn1Sequence) {
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(0);
        this.compressionAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.encapContentInfo = ContentInfo.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public static CompressedData getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static CompressedData getInstance(final Object o) {
        if (o instanceof CompressedData) {
            return (CompressedData)o;
        }
        if (o != null) {
            return new CompressedData(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public AlgorithmIdentifier getCompressionAlgorithmIdentifier() {
        return this.compressionAlgorithm;
    }
    
    public ContentInfo getEncapContentInfo() {
        return this.encapContentInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.compressionAlgorithm);
        asn1EncodableVector.add(this.encapContentInfo);
        return new BERSequence(asn1EncodableVector);
    }
}
