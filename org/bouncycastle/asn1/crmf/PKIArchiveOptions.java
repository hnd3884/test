package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class PKIArchiveOptions extends ASN1Object implements ASN1Choice
{
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private ASN1Encodable value;
    
    public static PKIArchiveOptions getInstance(final Object o) {
        if (o == null || o instanceof PKIArchiveOptions) {
            return (PKIArchiveOptions)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new PKIArchiveOptions((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("unknown object: " + o);
    }
    
    private PKIArchiveOptions(final ASN1TaggedObject asn1TaggedObject) {
        switch (asn1TaggedObject.getTagNo()) {
            case 0: {
                this.value = EncryptedKey.getInstance(asn1TaggedObject.getObject());
                break;
            }
            case 1: {
                this.value = ASN1OctetString.getInstance(asn1TaggedObject, false);
                break;
            }
            case 2: {
                this.value = ASN1Boolean.getInstance(asn1TaggedObject, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag number: " + asn1TaggedObject.getTagNo());
            }
        }
    }
    
    public PKIArchiveOptions(final EncryptedKey value) {
        this.value = value;
    }
    
    public PKIArchiveOptions(final ASN1OctetString value) {
        this.value = value;
    }
    
    public PKIArchiveOptions(final boolean b) {
        this.value = ASN1Boolean.getInstance(b);
    }
    
    public int getType() {
        if (this.value instanceof EncryptedKey) {
            return 0;
        }
        if (this.value instanceof ASN1OctetString) {
            return 1;
        }
        return 2;
    }
    
    public ASN1Encodable getValue() {
        return this.value;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.value instanceof EncryptedKey) {
            return new DERTaggedObject(true, 0, this.value);
        }
        if (this.value instanceof ASN1OctetString) {
            return new DERTaggedObject(false, 1, this.value);
        }
        return new DERTaggedObject(false, 2, this.value);
    }
}
