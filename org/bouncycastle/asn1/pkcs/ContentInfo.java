package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.BERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.BERSequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class ContentInfo extends ASN1Object implements PKCSObjectIdentifiers
{
    private ASN1ObjectIdentifier contentType;
    private ASN1Encodable content;
    private boolean isBer;
    
    public static ContentInfo getInstance(final Object o) {
        if (o instanceof ContentInfo) {
            return (ContentInfo)o;
        }
        if (o != null) {
            return new ContentInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ContentInfo(final ASN1Sequence asn1Sequence) {
        this.isBer = true;
        final Enumeration objects = asn1Sequence.getObjects();
        this.contentType = (ASN1ObjectIdentifier)objects.nextElement();
        if (objects.hasMoreElements()) {
            this.content = ((ASN1TaggedObject)objects.nextElement()).getObject();
        }
        this.isBer = (asn1Sequence instanceof BERSequence);
    }
    
    public ContentInfo(final ASN1ObjectIdentifier contentType, final ASN1Encodable content) {
        this.isBer = true;
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
            asn1EncodableVector.add(new BERTaggedObject(true, 0, this.content));
        }
        if (this.isBer) {
            return new BERSequence(asn1EncodableVector);
        }
        return new DLSequence(asn1EncodableVector);
    }
}
