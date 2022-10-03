package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ContentInfo extends ASN1Object implements CMSObjectIdentifiers
{
    private ASN1ObjectIdentifier contentType;
    private ASN1Encodable content;
    
    public static ContentInfo getInstance(final Object o) {
        if (o instanceof ContentInfo) {
            return (ContentInfo)o;
        }
        if (o != null) {
            return new ContentInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static ContentInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Deprecated
    public ContentInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 1 || asn1Sequence.size() > 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.contentType = (ASN1ObjectIdentifier)asn1Sequence.getObjectAt(0);
        if (asn1Sequence.size() > 1) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)asn1Sequence.getObjectAt(1);
            if (!asn1TaggedObject.isExplicit() || asn1TaggedObject.getTagNo() != 0) {
                throw new IllegalArgumentException("Bad tag for 'content'");
            }
            this.content = asn1TaggedObject.getObject();
        }
    }
    
    public ContentInfo(final ASN1ObjectIdentifier contentType, final ASN1Encodable content) {
        this.contentType = contentType;
        this.content = content;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    public ASN1Encodable getContent() {
        return this.content;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.contentType);
        if (this.content != null) {
            asn1EncodableVector.add(new BERTaggedObject(0, this.content));
        }
        return new BERSequence(asn1EncodableVector);
    }
}
