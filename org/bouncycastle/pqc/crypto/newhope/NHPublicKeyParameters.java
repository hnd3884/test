package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class NHPublicKeyParameters extends AsymmetricKeyParameter
{
    final byte[] pubData;
    
    public NHPublicKeyParameters(final byte[] array) {
        super(false);
        this.pubData = Arrays.clone(array);
    }
    
    public byte[] getPubData() {
        return Arrays.clone(this.pubData);
    }
}
