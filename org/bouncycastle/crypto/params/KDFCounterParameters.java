package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DerivationParameters;

public final class KDFCounterParameters implements DerivationParameters
{
    private byte[] ki;
    private byte[] fixedInputDataCounterPrefix;
    private byte[] fixedInputDataCounterSuffix;
    private int r;
    
    public KDFCounterParameters(final byte[] array, final byte[] array2, final int n) {
        this(array, null, array2, n);
    }
    
    public KDFCounterParameters(final byte[] array, final byte[] array2, final byte[] array3, final int r) {
        if (array == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(array);
        if (array2 == null) {
            this.fixedInputDataCounterPrefix = new byte[0];
        }
        else {
            this.fixedInputDataCounterPrefix = Arrays.clone(array2);
        }
        if (array3 == null) {
            this.fixedInputDataCounterSuffix = new byte[0];
        }
        else {
            this.fixedInputDataCounterSuffix = Arrays.clone(array3);
        }
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        this.r = r;
    }
    
    public byte[] getKI() {
        return this.ki;
    }
    
    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }
    
    public byte[] getFixedInputDataCounterPrefix() {
        return Arrays.clone(this.fixedInputDataCounterPrefix);
    }
    
    public byte[] getFixedInputDataCounterSuffix() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }
    
    public int getR() {
        return this.r;
    }
}
