package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class KeyAgreeRecipientIdentifier extends ASN1Object implements ASN1Choice
{
    private IssuerAndSerialNumber issuerSerial;
    private RecipientKeyIdentifier rKeyID;
    
    public static KeyAgreeRecipientIdentifier getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static KeyAgreeRecipientIdentifier getInstance(final Object o) {
        if (o == null || o instanceof KeyAgreeRecipientIdentifier) {
            return (KeyAgreeRecipientIdentifier)o;
        }
        if (o instanceof ASN1Sequence) {
            return new KeyAgreeRecipientIdentifier(IssuerAndSerialNumber.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject && ((ASN1TaggedObject)o).getTagNo() == 0) {
            return new KeyAgreeRecipientIdentifier(RecipientKeyIdentifier.getInstance((ASN1TaggedObject)o, false));
        }
        throw new IllegalArgumentException("Invalid KeyAgreeRecipientIdentifier: " + o.getClass().getName());
    }
    
    public KeyAgreeRecipientIdentifier(final IssuerAndSerialNumber issuerSerial) {
        this.issuerSerial = issuerSerial;
        this.rKeyID = null;
    }
    
    public KeyAgreeRecipientIdentifier(final RecipientKeyIdentifier rKeyID) {
        this.issuerSerial = null;
        this.rKeyID = rKeyID;
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        return this.issuerSerial;
    }
    
    public RecipientKeyIdentifier getRKeyID() {
        return this.rKeyID;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.issuerSerial != null) {
            return this.issuerSerial.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.rKeyID);
    }
}
