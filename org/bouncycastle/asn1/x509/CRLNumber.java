package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Object;

public class CRLNumber extends ASN1Object
{
    private BigInteger number;
    
    public CRLNumber(final BigInteger number) {
        this.number = number;
    }
    
    public BigInteger getCRLNumber() {
        return this.number;
    }
    
    @Override
    public String toString() {
        return "CRLNumber: " + this.getCRLNumber();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.number);
    }
    
    public static CRLNumber getInstance(final Object o) {
        if (o instanceof CRLNumber) {
            return (CRLNumber)o;
        }
        if (o != null) {
            return new CRLNumber(ASN1Integer.getInstance(o).getValue());
        }
        return null;
    }
}
