package sun.security.util;

import java.security.spec.AlgorithmParameterSpec;

public class ECKeySizeParameterSpec implements AlgorithmParameterSpec
{
    private int keySize;
    
    public ECKeySizeParameterSpec(final int keySize) {
        this.keySize = keySize;
    }
    
    public int getKeySize() {
        return this.keySize;
    }
}
