package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Integer;

public class SubsequentMessage extends ASN1Integer
{
    public static final SubsequentMessage encrCert;
    public static final SubsequentMessage challengeResp;
    
    private SubsequentMessage(final int n) {
        super(n);
    }
    
    public static SubsequentMessage valueOf(final int n) {
        if (n == 0) {
            return SubsequentMessage.encrCert;
        }
        if (n == 1) {
            return SubsequentMessage.challengeResp;
        }
        throw new IllegalArgumentException("unknown value: " + n);
    }
    
    static {
        encrCert = new SubsequentMessage(0);
        challengeResp = new SubsequentMessage(1);
    }
}
