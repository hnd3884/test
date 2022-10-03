package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SPHINCSPrivateKeyParameters extends AsymmetricKeyParameter
{
    private final byte[] keyData;
    
    public SPHINCSPrivateKeyParameters(final byte[] array) {
        super(true);
        this.keyData = Arrays.clone(array);
    }
    
    public byte[] getKeyData() {
        return Arrays.clone(this.keyData);
    }
}
