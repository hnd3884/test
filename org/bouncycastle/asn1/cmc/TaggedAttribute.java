package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class TaggedAttribute extends ASN1Object
{
    private final BodyPartID bodyPartID;
    private final ASN1ObjectIdentifier attrType;
    private final ASN1Set attrValues;
    
    public static TaggedAttribute getInstance(final Object o) {
        if (o instanceof TaggedAttribute) {
            return (TaggedAttribute)o;
        }
        if (o != null) {
            return new TaggedAttribute(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private TaggedAttribute(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartID = BodyPartID.getInstance(asn1Sequence.getObjectAt(0));
        this.attrType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(1));
        this.attrValues = ASN1Set.getInstance(asn1Sequence.getObjectAt(2));
    }
    
    public TaggedAttribute(final BodyPartID bodyPartID, final ASN1ObjectIdentifier attrType, final ASN1Set attrValues) {
        this.bodyPartID = bodyPartID;
        this.attrType = attrType;
        this.attrValues = attrValues;
    }
    
    public BodyPartID getBodyPartID() {
        return this.bodyPartID;
    }
    
    public ASN1ObjectIdentifier getAttrType() {
        return this.attrType;
    }
    
    public ASN1Set getAttrValues() {
        return this.attrValues;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[] { this.bodyPartID, this.attrType, this.attrValues });
    }
}
