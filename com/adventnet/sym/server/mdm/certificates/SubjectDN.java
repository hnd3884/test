package com.adventnet.sym.server.mdm.certificates;

import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public enum SubjectDN
{
    CN(BCStyle.CN), 
    OU(BCStyle.OU), 
    O(BCStyle.O), 
    L(BCStyle.L), 
    ST(BCStyle.ST), 
    C(BCStyle.C);
    
    ASN1ObjectIdentifier asn1ObjectIdentifier;
    
    private SubjectDN(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        this.asn1ObjectIdentifier = asn1ObjectIdentifier;
    }
    
    public ASN1ObjectIdentifier getAsn1ObjectIdentifier() {
        return this.asn1ObjectIdentifier;
    }
}
