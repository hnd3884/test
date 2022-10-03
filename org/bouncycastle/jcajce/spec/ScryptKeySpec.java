package org.bouncycastle.jcajce.spec;

import org.bouncycastle.util.Arrays;
import java.security.spec.KeySpec;

public class ScryptKeySpec implements KeySpec
{
    private final char[] password;
    private final byte[] salt;
    private final int costParameter;
    private final int blockSize;
    private final int parallelizationParameter;
    private final int keySize;
    
    public ScryptKeySpec(final char[] password, final byte[] array, final int costParameter, final int blockSize, final int parallelizationParameter, final int keySize) {
        this.password = password;
        this.salt = Arrays.clone(array);
        this.costParameter = costParameter;
        this.blockSize = blockSize;
        this.parallelizationParameter = parallelizationParameter;
        this.keySize = keySize;
    }
    
    public char[] getPassword() {
        return this.password;
    }
    
    public byte[] getSalt() {
        return Arrays.clone(this.salt);
    }
    
    public int getCostParameter() {
        return this.costParameter;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    public int getParallelizationParameter() {
        return this.parallelizationParameter;
    }
    
    public int getKeyLength() {
        return this.keySize;
    }
}
