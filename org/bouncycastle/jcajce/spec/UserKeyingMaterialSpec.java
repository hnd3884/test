package org.bouncycastle.jcajce.spec;

import org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class UserKeyingMaterialSpec implements AlgorithmParameterSpec
{
    private final byte[] userKeyingMaterial;
    
    public UserKeyingMaterialSpec(final byte[] array) {
        this.userKeyingMaterial = Arrays.clone(array);
    }
    
    public byte[] getUserKeyingMaterial() {
        return Arrays.clone(this.userKeyingMaterial);
    }
}
