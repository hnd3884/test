package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class CertStatus extends ASN1Object implements ASN1Choice
{
    private int tagNo;
    private ASN1Encodable value;
    
    public CertStatus() {
        this.tagNo = 0;
        this.value = DERNull.INSTANCE;
    }
    
    public CertStatus(final RevokedInfo value) {
        this.tagNo = 1;
        this.value = value;
    }
    
    public CertStatus(final int tagNo, final ASN1Encodable value) {
        this.tagNo = tagNo;
        this.value = value;
    }
    
    private CertStatus(final ASN1TaggedObject asn1TaggedObject) {
        this.tagNo = asn1TaggedObject.getTagNo();
        switch (asn1TaggedObject.getTagNo()) {
            case 0: {
                this.value = DERNull.INSTANCE;
                break;
            }
            case 1: {
                this.value = RevokedInfo.getInstance(asn1TaggedObject, false);
                break;
            }
            case 2: {
                this.value = DERNull.INSTANCE;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown tag encountered: " + asn1TaggedObject.getTagNo());
            }
        }
    }
    
    public static CertStatus getInstance(final Object o) {
        if (o == null || o instanceof CertStatus) {
            return (CertStatus)o;
        }
        if (o instanceof ASN1TaggedObject) {
            return new CertStatus((ASN1TaggedObject)o);
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }
    
    public static CertStatus getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public int getTagNo() {
        return this.tagNo;
    }
    
    public ASN1Encodable getStatus() {
        return this.value;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(false, this.tagNo, this.value);
    }
}
