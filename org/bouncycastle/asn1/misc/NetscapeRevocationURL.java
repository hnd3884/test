package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERIA5String;

public class NetscapeRevocationURL extends DERIA5String
{
    public NetscapeRevocationURL(final DERIA5String deria5String) {
        super(deria5String.getString());
    }
    
    @Override
    public String toString() {
        return "NetscapeRevocationURL: " + this.getString();
    }
}
