package com.maverick.crypto.asn1.misc;

import com.maverick.crypto.asn1.DERIA5String;

public class VerisignCzagExtension extends DERIA5String
{
    public VerisignCzagExtension(final DERIA5String deria5String) {
        super(deria5String.getString());
    }
    
    public String toString() {
        return "VerisignCzagExtension: " + this.getString();
    }
}
