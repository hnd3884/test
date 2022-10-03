package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.TargetEtcChain;

public class TargetChain
{
    private final TargetEtcChain certs;
    
    public TargetChain(final TargetEtcChain certs) {
        this.certs = certs;
    }
    
    public TargetEtcChain toASN1Structure() {
        return this.certs;
    }
}
