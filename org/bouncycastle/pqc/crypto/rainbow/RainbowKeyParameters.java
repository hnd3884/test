package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class RainbowKeyParameters extends AsymmetricKeyParameter
{
    private int docLength;
    
    public RainbowKeyParameters(final boolean b, final int docLength) {
        super(b);
        this.docLength = docLength;
    }
    
    public int getDocLength() {
        return this.docLength;
    }
}
