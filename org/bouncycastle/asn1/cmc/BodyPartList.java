package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class BodyPartList extends ASN1Object
{
    private final BodyPartID[] bodyPartIDs;
    
    public static BodyPartList getInstance(final Object o) {
        if (o instanceof BodyPartList) {
            return (BodyPartList)o;
        }
        if (o != null) {
            return new BodyPartList(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static BodyPartList getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public BodyPartList(final BodyPartID bodyPartID) {
        this.bodyPartIDs = new BodyPartID[] { bodyPartID };
    }
    
    public BodyPartList(final BodyPartID[] array) {
        this.bodyPartIDs = Utils.clone(array);
    }
    
    private BodyPartList(final ASN1Sequence asn1Sequence) {
        this.bodyPartIDs = Utils.toBodyPartIDArray(asn1Sequence);
    }
    
    public BodyPartID[] getBodyPartIDs() {
        return Utils.clone(this.bodyPartIDs);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.bodyPartIDs);
    }
}
