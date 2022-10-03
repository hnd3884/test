package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class Controls extends ASN1Object
{
    private ASN1Sequence content;
    
    private Controls(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static Controls getInstance(final Object o) {
        if (o instanceof Controls) {
            return (Controls)o;
        }
        if (o != null) {
            return new Controls(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public Controls(final AttributeTypeAndValue attributeTypeAndValue) {
        this.content = new DERSequence(attributeTypeAndValue);
    }
    
    public Controls(final AttributeTypeAndValue[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.content = new DERSequence(asn1EncodableVector);
    }
    
    public AttributeTypeAndValue[] toAttributeTypeAndValueArray() {
        final AttributeTypeAndValue[] array = new AttributeTypeAndValue[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = AttributeTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
