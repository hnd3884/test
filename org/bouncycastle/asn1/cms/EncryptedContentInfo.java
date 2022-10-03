package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedContentInfo extends ASN1Object
{
    private ASN1ObjectIdentifier contentType;
    private AlgorithmIdentifier contentEncryptionAlgorithm;
    private ASN1OctetString encryptedContent;
    
    public EncryptedContentInfo(final ASN1ObjectIdentifier contentType, final AlgorithmIdentifier contentEncryptionAlgorithm, final ASN1OctetString encryptedContent) {
        this.contentType = contentType;
        this.contentEncryptionAlgorithm = contentEncryptionAlgorithm;
        this.encryptedContent = encryptedContent;
    }
    
    private EncryptedContentInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 2) {
            throw new IllegalArgumentException("Truncated Sequence Found");
        }
        this.contentType = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        this.contentEncryptionAlgorithm = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            this.encryptedContent = ASN1OctetString.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(2), false);
        }
    }
    
    public static EncryptedContentInfo getInstance(final Object o) {
        if (o instanceof EncryptedContentInfo) {
            return (EncryptedContentInfo)o;
        }
        if (o != null) {
            return new EncryptedContentInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    public AlgorithmIdentifier getContentEncryptionAlgorithm() {
        return this.contentEncryptionAlgorithm;
    }
    
    public ASN1OctetString getEncryptedContent() {
        return this.encryptedContent;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.contentType);
        asn1EncodableVector.add(this.contentEncryptionAlgorithm);
        if (this.encryptedContent != null) {
            asn1EncodableVector.add(new BERTaggedObject(false, 0, this.encryptedContent));
        }
        return new BERSequence(asn1EncodableVector);
    }
}
