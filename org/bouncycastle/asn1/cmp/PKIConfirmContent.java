package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;

public class PKIConfirmContent extends ASN1Object
{
    private ASN1Null val;
    
    private PKIConfirmContent(final ASN1Null val) {
        this.val = val;
    }
    
    public static PKIConfirmContent getInstance(final Object o) {
        if (o == null || o instanceof PKIConfirmContent) {
            return (PKIConfirmContent)o;
        }
        if (o instanceof ASN1Null) {
            return new PKIConfirmContent((ASN1Null)o);
        }
        throw new IllegalArgumentException("Invalid object: " + o.getClass().getName());
    }
    
    public PKIConfirmContent() {
        this.val = DERNull.INSTANCE;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.val;
    }
}
