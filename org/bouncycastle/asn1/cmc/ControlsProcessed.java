package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class ControlsProcessed extends ASN1Object
{
    private final ASN1Sequence bodyPartReferences;
    
    public ControlsProcessed(final BodyPartReference bodyPartReference) {
        this.bodyPartReferences = new DERSequence(bodyPartReference);
    }
    
    public ControlsProcessed(final BodyPartReference[] array) {
        this.bodyPartReferences = new DERSequence(array);
    }
    
    public static ControlsProcessed getInstance(final Object o) {
        if (o instanceof ControlsProcessed) {
            return (ControlsProcessed)o;
        }
        if (o != null) {
            return new ControlsProcessed(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private ControlsProcessed(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 1) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.bodyPartReferences = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(0));
    }
    
    public BodyPartReference[] getBodyList() {
        final BodyPartReference[] array = new BodyPartReference[this.bodyPartReferences.size()];
        for (int i = 0; i != this.bodyPartReferences.size(); ++i) {
            array[i] = BodyPartReference.getInstance(this.bodyPartReferences.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.bodyPartReferences);
    }
}
