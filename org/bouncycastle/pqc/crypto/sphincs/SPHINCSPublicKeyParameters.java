package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCSPublicKeyParameters extends AsymmetricKeyParameter
{
    private final byte[] keyData;
    
    public SPHINCSPublicKeyParameters(final byte[] array) {
        super(false);
        this.keyData = Arrays.clone(array);
    }
    
    public byte[] getKeyData() {
        return Arrays.clone(this.keyData);
    }
}
