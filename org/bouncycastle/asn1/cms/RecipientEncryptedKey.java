package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class RecipientEncryptedKey extends ASN1Object
{
    private KeyAgreeRecipientIdentifier identifier;
    private ASN1OctetString encryptedKey;
    
    private RecipientEncryptedKey(final ASN1Sequence asn1Sequence) {
        this.identifier = KeyAgreeRecipientIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.encryptedKey = (ASN1OctetString)asn1Sequence.getObjectAt(1);
    }
    
    public static RecipientEncryptedKey getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static RecipientEncryptedKey getInstance(final Object o) {
        if (o instanceof RecipientEncryptedKey) {
            return (RecipientEncryptedKey)o;
        }
        if (o != null) {
            return new RecipientEncryptedKey(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RecipientEncryptedKey(final KeyAgreeRecipientIdentifier identifier, final ASN1OctetString encryptedKey) {
        this.identifier = identifier;
        this.encryptedKey = encryptedKey;
    }
    
    public KeyAgreeRecipientIdentifier getIdentifier() {
        return this.identifier;
    }
    
    public ASN1OctetString getEncryptedKey() {
        return this.encryptedKey;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.identifier);
        asn1EncodableVector.add(this.encryptedKey);
        return new DERSequence(asn1EncodableVector);
    }
}
