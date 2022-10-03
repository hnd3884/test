package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class GMSSKeyParameters extends AsymmetricKeyParameter
{
    private GMSSParameters params;
    
    public GMSSKeyParameters(final boolean b, final GMSSParameters params) {
        super(b);
        this.params = params;
    }
    
    public GMSSParameters getParameters() {
        return this.params;
    }
}
