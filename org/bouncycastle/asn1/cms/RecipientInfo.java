package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class RecipientInfo extends ASN1Object implements ASN1Choice
{
    ASN1Encodable info;
    
    public RecipientInfo(final KeyTransRecipientInfo info) {
        this.info = info;
    }
    
    public RecipientInfo(final KeyAgreeRecipientInfo keyAgreeRecipientInfo) {
        this.info = new DERTaggedObject(false, 1, keyAgreeRecipientInfo);
    }
    
    public RecipientInfo(final KEKRecipientInfo kekRecipientInfo) {
        this.info = new DERTaggedObject(false, 2, kekRecipientInfo);
    }
    
    public RecipientInfo(final PasswordRecipientInfo passwordRecipientInfo) {
        this.info = new DERTaggedObject(false, 3, passwordRecipientInfo);
    }
    
    public RecipientInfo(final OtherRecipientInfo otherRecipientInfo) {
        this.info = new DERTaggedObject(false, 4, otherRecipientInfo);
    }
    
    public RecipientInfo(final ASN1Primitive info) {
        this.info = info;
    }
    
    public static RecipientInfo getInstance(final Object o) {
        if (o == null || o instanceof RecipientInfo) {
            return (RecipientInfo)o;
        }
        if (o instanceof ASN1Sequence) {
            return new RecipientInfo((ASN1Primitive)o);
        }
        if (o instanceof ASN1TaggedObject) {
            return new RecipientInfo((ASN1Primitive)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public ASN1Integer getVersion() {
        if (!(this.info instanceof ASN1TaggedObject)) {
            return KeyTransRecipientInfo.getInstance(this.info).getVersion();
        }
        final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)this.info;
        switch (asn1TaggedObject.getTagNo()) {
            case 1: {
                return KeyAgreeRecipientInfo.getInstance(asn1TaggedObject, false).getVersion();
            }
            case 2: {
                return this.getKEKInfo(asn1TaggedObject).getVersion();
            }
            case 3: {
                return PasswordRecipientInfo.getInstance(asn1TaggedObject, false).getVersion();
            }
            case 4: {
                return new ASN1Integer(0L);
            }
            default: {
                throw new IllegalStateException("unknown tag");
            }
        }
    }
    
    public boolean isTagged() {
        return this.info instanceof ASN1TaggedObject;
    }
    
    public ASN1Encodable getInfo() {
        if (!(this.info instanceof ASN1TaggedObject)) {
            return KeyTransRecipientInfo.getInstance(this.info);
        }
        final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)this.info;
        switch (asn1TaggedObject.getTagNo()) {
            case 1: {
                return KeyAgreeRecipientInfo.getInstance(asn1TaggedObject, false);
            }
            case 2: {
                return this.getKEKInfo(asn1TaggedObject);
            }
            case 3: {
                return PasswordRecipientInfo.getInstance(asn1TaggedObject, false);
            }
            case 4: {
                return OtherRecipientInfo.getInstance(asn1TaggedObject, false);
            }
            default: {
                throw new IllegalStateException("unknown tag");
            }
        }
    }
    
    private KEKRecipientInfo getKEKInfo(final ASN1TaggedObject asn1TaggedObject) {
        if (asn1TaggedObject.isExplicit()) {
            return KEKRecipientInfo.getInstance(asn1TaggedObject, true);
        }
        return KEKRecipientInfo.getInstance(asn1TaggedObject, false);
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.info.toASN1Primitive();
    }
}
