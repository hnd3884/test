package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class EncryptedKey extends ASN1Object implements ASN1Choice
{
    private EnvelopedData envelopedData;
    private EncryptedValue encryptedValue;
    
    public static EncryptedKey getInstance(final Object o) {
        if (o instanceof EncryptedKey) {
            return (EncryptedKey)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new EncryptedKey(EnvelopedData.getInstance((ASN1TaggedObject)o, false));
        }
        if (o instanceof EncryptedValue) {
            return new EncryptedKey((EncryptedValue)o);
        }
        return new EncryptedKey(EncryptedValue.getInstance(o));
    }
    
    public EncryptedKey(final EnvelopedData envelopedData) {
        this.envelopedData = envelopedData;
    }
    
    public EncryptedKey(final EncryptedValue encryptedValue) {
        this.encryptedValue = encryptedValue;
    }
    
    public boolean isEncryptedValue() {
        return this.encryptedValue != null;
    }
    
    public ASN1Encodable getValue() {
        if (this.encryptedValue != null) {
            return this.encryptedValue;
        }
        return this.envelopedData;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.encryptedValue != null) {
            return this.encryptedValue.toASN1Primitive();
        }
        return new DERTaggedObject(false, 0, this.envelopedData);
    }
}
