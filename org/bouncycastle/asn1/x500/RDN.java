package org.bouncycastle.asn1.x500;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1Object;

public class RDN extends ASN1Object
{
    private ASN1Set values;
    
    private RDN(final ASN1Set values) {
        this.values = values;
    }
    
    public static RDN getInstance(final Object o) {
        if (o instanceof RDN) {
            return (RDN)o;
        }
        if (o != null) {
            return new RDN(ASN1Set.getInstance(o));
        }
        return null;
    }
    
    public RDN(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(asn1ObjectIdentifier);
        asn1EncodableVector.add(asn1Encodable);
        this.values = new DERSet(new DERSequence(asn1EncodableVector));
    }
    
    public RDN(final AttributeTypeAndValue attributeTypeAndValue) {
        this.values = new DERSet(attributeTypeAndValue);
    }
    
    public RDN(final AttributeTypeAndValue[] array) {
        this.values = new DERSet(array);
    }
    
    public boolean isMultiValued() {
        return this.values.size() > 1;
    }
    
    public int size() {
        return this.values.size();
    }
    
    public AttributeTypeAndValue getFirst() {
        if (this.values.size() == 0) {
            return null;
        }
        return AttributeTypeAndValue.getInstance(this.values.getObjectAt(0));
    }
    
    public AttributeTypeAndValue[] getTypesAndValues() {
        final AttributeTypeAndValue[] array = new AttributeTypeAndValue[this.values.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = AttributeTypeAndValue.getInstance(this.values.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.values;
    }
}
