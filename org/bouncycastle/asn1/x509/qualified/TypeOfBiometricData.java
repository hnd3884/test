package org.bouncycastle.asn1.x509.qualified;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class TypeOfBiometricData extends ASN1Object implements ASN1Choice
{
    public static final int PICTURE = 0;
    public static final int HANDWRITTEN_SIGNATURE = 1;
    ASN1Encodable obj;
    
    public static TypeOfBiometricData getInstance(final Object o) {
        if (o == null || o instanceof TypeOfBiometricData) {
            return (TypeOfBiometricData)o;
        }
        if (o instanceof ASN1Integer) {
            return new TypeOfBiometricData(ASN1Integer.getInstance(o).getValue().intValue());
        }
        if (o instanceof ASN1ObjectIdentifier) {
            return new TypeOfBiometricData(ASN1ObjectIdentifier.getInstance(o));
        }
        throw new IllegalArgumentException("unknown object in getInstance");
    }
    
    public TypeOfBiometricData(final int n) {
        if (n == 0 || n == 1) {
            this.obj = new ASN1Integer(n);
            return;
        }
        throw new IllegalArgumentException("unknow PredefinedBiometricType : " + n);
    }
    
    public TypeOfBiometricData(final ASN1ObjectIdentifier obj) {
        this.obj = obj;
    }
    
    public boolean isPredefined() {
        return this.obj instanceof ASN1Integer;
    }
    
    public int getPredefinedBiometricType() {
        return ((ASN1Integer)this.obj).getValue().intValue();
    }
    
    public ASN1ObjectIdentifier getBiometricDataOid() {
        return (ASN1ObjectIdentifier)this.obj;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.obj.toASN1Primitive();
    }
}
