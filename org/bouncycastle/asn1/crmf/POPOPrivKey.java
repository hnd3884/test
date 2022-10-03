package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class POPOPrivKey extends ASN1Object implements ASN1Choice
{
    public static final int thisMessage = 0;
    public static final int subsequentMessage = 1;
    public static final int dhMAC = 2;
    public static final int agreeMAC = 3;
    public static final int encryptedKey = 4;
    private int tagNo;
    private ASN1Encodable obj;
    
    private POPOPrivKey(final ASN1TaggedObject asn1TaggedObject) {
        switch (this.tagNo = asn1TaggedObject.getTagNo()) {
            case 0: {
                this.obj = DERBitString.getInstance(asn1TaggedObject, false);
                break;
            }
            case 1: {
                this.obj = SubsequentMessage.valueOf(ASN1Integer.getInstance(asn1TaggedObject, false).getValue().intValue());
                break;
            }
            case 2: {
                this.obj = DERBitString.getInstance(asn1TaggedObject, false);
                break;
            }
            case 3: {
                this.obj = PKMACValue.getInstance(asn1TaggedObject, false);
                break;
            }
            case 4: {
                this.obj = EnvelopedData.getInstance(asn1TaggedObject, false);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag in POPOPrivKey");
            }
        }
    }
    
    public static POPOPrivKey getInstance(final Object o) {
        if (o instanceof POPOPrivKey) {
            return (POPOPrivKey)o;
        }
        if (o != null) {
            return new POPOPrivKey(ASN1TaggedObject.getInstance(o));
        }
        return null;
    }
    
    public static POPOPrivKey getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1TaggedObject.getInstance(asn1TaggedObject, b));
    }
    
    public POPOPrivKey(final SubsequentMessage obj) {
        this.tagNo = 1;
        this.obj = obj;
    }
    
    public int getType() {
        return this.tagNo;
    }
    
    public ASN1Encodable getValue() {
        return this.obj;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.obj);
    }
}
