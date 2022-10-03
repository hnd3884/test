package org.bouncycastle.asn1.misc;

import org.bouncycastle.asn1.DERIA5String;

public class VerisignCzagExtension extends DERIA5String
{
    public VerisignCzagExtension(final DERIA5String deria5String) {
        super(deria5String.getString());
    }
    
    @Override
    public String toString() {
        return "VerisignCzagExtension: " + this.getString();
    }
}
