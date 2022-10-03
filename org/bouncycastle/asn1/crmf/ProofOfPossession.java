package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class ProofOfPossession extends ASN1Object implements ASN1Choice
{
    public static final int TYPE_RA_VERIFIED = 0;
    public static final int TYPE_SIGNING_KEY = 1;
    public static final int TYPE_KEY_ENCIPHERMENT = 2;
    public static final int TYPE_KEY_AGREEMENT = 3;
    private int tagNo;
    private ASN1Encodable obj;
    
    private ProofOfPossession(final ASN1TaggedObject asn1TaggedObject) {
        switch (this.tagNo = asn1TaggedObject.getTagNo()) {
            case 0: {
                this.obj = DERNull.INSTANCE;
                break;
            }
            case 1: {
                this.obj = POPOSigningKey.getInstance(asn1TaggedObject, false);
                break;
            }
            case 2:
            case 3: {
                this.obj = POPOPrivKey.getInstance(asn1TaggedObject, true);
                break;
            }
            default: {
                throw new IllegalArgumentException("unknown tag: " + this.tagNo);
            }
        }
    }
    
    public static ProofOfPossession getInstance(final Object o) {
        if (o == null || o instanceof ProofOfPossession) {
            return (ProofOfPossession)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new ProofOfPossession((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }
    
    public ProofOfPossession() {
        this.tagNo = 0;
        this.obj = DERNull.INSTANCE;
    }
    
    public ProofOfPossession(final POPOSigningKey obj) {
        this.tagNo = 1;
        this.obj = obj;
    }
    
    public ProofOfPossession(final int tagNo, final POPOPrivKey obj) {
        this.tagNo = tagNo;
        this.obj = obj;
    }
    
    public int getType() {
        return this.tagNo;
    }
    
    public ASN1Encodable getObject() {
        return this.obj;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.obj);
    }
}
