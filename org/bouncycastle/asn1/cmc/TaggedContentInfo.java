package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.ASN1Object;

public class TaggedContentInfo extends ASN1Object
{
    private final BodyPartID bodyPartID;
    private final ContentInfo contentInfo;
    
    public TaggedContentInfo(final BodyPartID bodyPartID, final ContentInfo contentInfo) {
        this.bodyPartID = bodyPartID;
        this.contentInfo = contentInfo;
    }
    
    private TaggedContentInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(asn1Sequence.getObjectAt(0));
        this.contentInfo = ContentInfo.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static TaggedContentInfo getInstance(final Object o) {
        if (o instanceof TaggedContentInfo) {
            return (TaggedContentInfo)o;
        }
        if (o != null) {
            return new TaggedContentInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static TaggedContentInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.bodyPartID);
        asn1EncodableVector.add(this.contentInfo);
        return new DERSequence(asn1EncodableVector);
    }
    
    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }
    
    public ContentInfo getContentInfo() {
        return this.contentInfo;
    }
}
