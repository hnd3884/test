package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

public class RegTokenControl implements Control
{
    private static final ASN1ObjectIdentifier type;
    private final DERUTF8String token;
    
    public RegTokenControl(final DERUTF8String token) {
        this.token = token;
    }
    
    public RegTokenControl(final String s) {
        this.token = new DERUTF8String(s);
    }
    
    public ASN1ObjectIdentifier getType() {
        return RegTokenControl.type;
    }
    
    public ASN1Encodable getValue() {
        return (ASN1Encodable)this.token;
    }
    
    static {
        type = CRMFObjectIdentifiers.id_regCtrl_regToken;
    }
}
