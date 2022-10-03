package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class PKIStatus extends ASN1Object
{
    public static final int GRANTED = 0;
    public static final int GRANTED_WITH_MODS = 1;
    public static final int REJECTION = 2;
    public static final int WAITING = 3;
    public static final int REVOCATION_WARNING = 4;
    public static final int REVOCATION_NOTIFICATION = 5;
    public static final int KEY_UPDATE_WARNING = 6;
    public static final PKIStatus granted;
    public static final PKIStatus grantedWithMods;
    public static final PKIStatus rejection;
    public static final PKIStatus waiting;
    public static final PKIStatus revocationWarning;
    public static final PKIStatus revocationNotification;
    public static final PKIStatus keyUpdateWaiting;
    private ASN1Integer value;
    
    private PKIStatus(final int n) {
        this(new ASN1Integer(n));
    }
    
    private PKIStatus(final ASN1Integer value) {
        this.value = value;
    }
    
    public static PKIStatus getInstance(final Object o) {
        if (o instanceof PKIStatus) {
            return (PKIStatus)o;
        }
        if (o != null) {
            return new PKIStatus(ASN1Integer.getInstance(o));
        }
        return null;
    }
    
    public BigInteger getValue() {
        return this.value.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
    
    static {
        granted = new PKIStatus(0);
        grantedWithMods = new PKIStatus(1);
        rejection = new PKIStatus(2);
        waiting = new PKIStatus(3);
        revocationWarning = new PKIStatus(4);
        revocationNotification = new PKIStatus(5);
        keyUpdateWaiting = new PKIStatus(6);
    }
}
