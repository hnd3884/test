package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class RecipientIdentifier extends ASN1Object implements ASN1Choice
{
    private ASN1Encodable id;
    
    public RecipientIdentifier(final IssuerAndSerialNumber id) {
        this.id = id;
    }
    
    public RecipientIdentifier(final ASN1OctetString asn1OctetString) {
        this.id = new DERTaggedObject(false, 0, asn1OctetString);
    }
    
    public RecipientIdentifier(final ASN1Primitive id) {
        this.id = id;
    }
    
    public static RecipientIdentifier getInstance(final Object o) {
        if (o == null || o instanceof RecipientIdentifier) {
            return (RecipientIdentifier)o;
        }
        if (o instanceof IssuerAndSerialNumber) {
            return new RecipientIdentifier((IssuerAndSerialNumber)o);
        }
        if (o instanceof ASN1OctetString) {
            return new RecipientIdentifier((ASN1OctetString)o);
        }
        if (o instanceof ASN1Primitive) {
            return new RecipientIdentifier((ASN1Primitive)o);
        }
        throw new IllegalArgumentException("Illegal object in RecipientIdentifier: " + o.getClass().getName());
    }
    
    public boolean isTagged() {
        return this.id instanceof ASN1TaggedObject;
    }
    
    public ASN1Encodable getId() {
        if (this.id instanceof ASN1TaggedObject) {
            return ASN1OctetString.getInstance((ASN1TaggedObject)this.id, false);
        }
        return IssuerAndSerialNumber.getInstance(this.id);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.id.toASN1Primitive();
    }
}
