package org.bouncycastle.pqc.crypto.mceliece;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class McElieceKeyParameters extends AsymmetricKeyParameter
{
    private McElieceParameters params;
    
    public McElieceKeyParameters(final boolean b, final McElieceParameters params) {
        super(b);
        this.params = params;
    }
    
    public McElieceParameters getParameters() {
        return this.params;
    }
}
