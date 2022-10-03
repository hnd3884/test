package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class NHPrivateKeyParameters extends AsymmetricKeyParameter
{
    final short[] secData;
    
    public NHPrivateKeyParameters(final short[] array) {
        super(true);
        this.secData = Arrays.clone(array);
    }
    
    public short[] getSecData() {
        return Arrays.clone(this.secData);
    }
}
