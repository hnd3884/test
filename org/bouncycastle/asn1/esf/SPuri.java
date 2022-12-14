package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERIA5String;

public class SPuri
{
    private DERIA5String uri;
    
    public static SPuri getInstance(final Object o) {
        if (o instanceof SPuri) {
            return (SPuri)o;
        }
        if (o instanceof DERIA5String) {
            return new SPuri(DERIA5String.getInstance(o));
        }
        return null;
    }
    
    public SPuri(final DERIA5String uri) {
        this.uri = uri;
    }
    
    public DERIA5String getUri() {
        return this.uri;
    }
    
    public ASN1Primitive toASN1Primitive() {
        return this.uri.toASN1Primitive();
    }
}
