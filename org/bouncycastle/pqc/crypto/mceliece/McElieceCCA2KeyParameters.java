package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class McElieceCCA2KeyParameters extends AsymmetricKeyParameter
{
    private String params;
    
    public McElieceCCA2KeyParameters(final boolean b, final String params) {
        super(b);
        this.params = params;
    }
    
    public String getDigest() {
        return this.params;
    }
}
