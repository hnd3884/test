package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DLSet;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Object;

public class Attributes extends ASN1Object
{
    private ASN1Set attributes;
    
    private Attributes(final ASN1Set attributes) {
        this.attributes = attributes;
    }
    
    public Attributes(final ASN1EncodableVector asn1EncodableVector) {
        this.attributes = new DLSet(asn1EncodableVector);
    }
    
    public static Attributes getInstance(final Object o) {
        if (o instanceof Attributes) {
            return (Attributes)o;
        }
        if (o != null) {
            return new Attributes(ASN1Set.getInstance(o));
        }
        return null;
    }
    
    public Attribute[] getAttributes() {
        final Attribute[] array = new Attribute[this.attributes.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = Attribute.getInstance(this.attributes.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.attributes;
    }
}
