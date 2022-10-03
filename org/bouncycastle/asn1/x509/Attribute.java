package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class Attribute extends ASN1Object
{
    private ASN1ObjectIdentifier attrType;
    private ASN1Set attrValues;
    
    public static Attribute getInstance(final Object o) {
        if (o instanceof Attribute) {
            return (Attribute)o;
        }
        if (o != null) {
            return new Attribute(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private Attribute(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.attrType = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.attrValues = ASN1Set.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public Attribute(final ASN1ObjectIdentifier attrType, final ASN1Set attrValues) {
        this.attrType = attrType;
        this.attrValues = attrValues;
    }
    
    public ASN1ObjectIdentifier getAttrType() {
        return new ASN1ObjectIdentifier(this.attrType.getId());
    }
    
    public ASN1Encodable[] getAttributeValues() {
        return this.attrValues.toArray();
    }
    
    public ASN1Set getAttrValues() {
        return this.attrValues;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.attrType);
        asn1EncodableVector.add(this.attrValues);
        return new DERSequence(asn1EncodableVector);
    }
}
