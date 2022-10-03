package org.bouncycastle.jcajce.spec;

import org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class SM2ParameterSpec implements AlgorithmParameterSpec
{
    private byte[] id;
    
    public SM2ParameterSpec(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("id string cannot be null");
        }
        this.id = Arrays.clone(array);
    }
    
    public byte[] getID() {
        return Arrays.clone(this.id);
    }
}
