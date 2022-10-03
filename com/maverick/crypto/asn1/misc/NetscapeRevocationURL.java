package com.maverick.crypto.asn1.misc;

import com.maverick.crypto.asn1.DERIA5String;

public class NetscapeRevocationURL extends DERIA5String
{
    public NetscapeRevocationURL(final DERIA5String deria5String) {
        super(deria5String.getString());
    }
    
    public String toString() {
        return "NetscapeRevocationURL: " + this.getString();
    }
}
