package org.bouncycastle.asn1.x509;

import org.bouncycastle.util.Integers;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import java.util.Hashtable;
import org.bouncycastle.asn1.ASN1Object;

public class CRLReason extends ASN1Object
{
    @Deprecated
    public static final int UNSPECIFIED = 0;
    @Deprecated
    public static final int KEY_COMPROMISE = 1;
    @Deprecated
    public static final int CA_COMPROMISE = 2;
    @Deprecated
    public static final int AFFILIATION_CHANGED = 3;
    @Deprecated
    public static final int SUPERSEDED = 4;
    @Deprecated
    public static final int CESSATION_OF_OPERATION = 5;
    @Deprecated
    public static final int CERTIFICATE_HOLD = 6;
    @Deprecated
    public static final int REMOVE_FROM_CRL = 8;
    @Deprecated
    public static final int PRIVILEGE_WITHDRAWN = 9;
    @Deprecated
    public static final int AA_COMPROMISE = 10;
    public static final int unspecified = 0;
    public static final int keyCompromise = 1;
    public static final int cACompromise = 2;
    public static final int affiliationChanged = 3;
    public static final int superseded = 4;
    public static final int cessationOfOperation = 5;
    public static final int certificateHold = 6;
    public static final int removeFromCRL = 8;
    public static final int privilegeWithdrawn = 9;
    public static final int aACompromise = 10;
    private static final String[] reasonString;
    private static final Hashtable table;
    private ASN1Enumerated value;
    
    public static CRLReason getInstance(final Object o) {
        if (o instanceof CRLReason) {
            return (CRLReason)o;
        }
        if (o != null) {
            return lookup(ASN1Enumerated.getInstance(o).getValue().intValue());
        }
        return null;
    }
    
    private CRLReason(final int n) {
        this.value = new ASN1Enumerated(n);
    }
    
    @Override
    public String toString() {
        final int intValue = this.getValue().intValue();
        String s;
        if (intValue < 0 || intValue > 10) {
            s = "invalid";
        }
        else {
            s = CRLReason.reasonString[intValue];
        }
        return "CRLReason: " + s;
    }
    
    public BigInteger getValue() {
        return this.value.getValue();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.value;
    }
    
    public static CRLReason lookup(final int n) {
        final Integer value = Integers.valueOf(n);
        if (!CRLReason.table.containsKey(value)) {
            CRLReason.table.put(value, new CRLReason(n));
        }
        return (CRLReason)CRLReason.table.get(value);
    }
    
    static {
        reasonString = new String[] { "unspecified", "keyCompromise", "cACompromise", "affiliationChanged", "superseded", "cessationOfOperation", "certificateHold", "unknown", "removeFromCRL", "privilegeWithdrawn", "aACompromise" };
        table = new Hashtable();
    }
}
