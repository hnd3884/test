package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1Object;

public class ServiceType extends ASN1Object
{
    public static final ServiceType CPD;
    public static final ServiceType VSD;
    public static final ServiceType VPKC;
    public static final ServiceType CCPD;
    private ASN1Enumerated value;
    
    public ServiceType(final int n) {
        this.value = new ASN1Enumerated(n);
    }
    
    private ServiceType(final ASN1Enumerated value) {
        this.value = value;
    }
    
    public static ServiceType getInstance(final Object o) {
        if (o instanceof ServiceType) {
            return (ServiceType)o;
        }
        if (o != null) {
            return new ServiceType(ASN1Enumerated.getInstance(o));
        }
        return null;
    }
    
    public static ServiceType getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Enumerated.getInstance(asn1TaggedObject, b));
    }
    
    public BigInteger getValue() {
        return this.value.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
    
    @Override
    public String toString() {
        final int intValue = this.value.getValue().intValue();
        return "" + intValue + ((intValue == ServiceType.CPD.getValue().intValue()) ? "(CPD)" : ((intValue == ServiceType.VSD.getValue().intValue()) ? "(VSD)" : ((intValue == ServiceType.VPKC.getValue().intValue()) ? "(VPKC)" : ((intValue == ServiceType.CCPD.getValue().intValue()) ? "(CCPD)" : "?"))));
    }
    
    static {
        CPD = new ServiceType(1);
        VSD = new ServiceType(2);
        VPKC = new ServiceType(3);
        CCPD = new ServiceType(4);
    }
}
