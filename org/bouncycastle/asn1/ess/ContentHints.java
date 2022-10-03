package org.bouncycastle.asn1.ess;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Object;

public class ContentHints extends ASN1Object
{
    private DERUTF8String contentDescription;
    private ASN1ObjectIdentifier contentType;
    
    public static ContentHints getInstance(final Object o) {
        if (o instanceof ContentHints) {
            return (ContentHints)o;
        }
        if (o != null) {
            return new ContentHints(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ContentHints(final ASN1Sequence asn1Sequence) {
        final ASN1Encodable object = asn1Sequence.getObjectAt(0);
        if (object.toASN1Primitive() instanceof DERUTF8String) {
            this.contentDescription = DERUTF8String.getInstance(object);
            this.contentType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        }
        else {
            this.contentType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        }
    }
    
    public ContentHints(final ASN1ObjectIdentifier contentType) {
        this.contentType = contentType;
        this.contentDescription = null;
    }
    
    public ContentHints(final ASN1ObjectIdentifier contentType, final DERUTF8String contentDescription) {
        this.contentType = contentType;
        this.contentDescription = contentDescription;
    }
    
    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }
    
    public DERUTF8String getContentDescription() {
        return this.contentDescription;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.contentDescription != null) {
            asn1EncodableVector.add(this.contentDescription);
        }
        asn1EncodableVector.add(this.contentType);
        return new DERSequence(asn1EncodableVector);
    }
}
