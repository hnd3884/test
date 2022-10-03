package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DerivationParameters;

public final class KDFDoublePipelineIterationParameters implements DerivationParameters
{
    private static final int UNUSED_R = 32;
    private final byte[] ki;
    private final boolean useCounter;
    private final int r;
    private final byte[] fixedInputData;
    
    private KDFDoublePipelineIterationParameters(final byte[] array, final byte[] array2, final int r, final boolean useCounter) {
        if (array == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(array);
        if (array2 == null) {
            this.fixedInputData = new byte[0];
        }
        else {
            this.fixedInputData = Arrays.clone(array2);
        }
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        this.r = r;
        this.useCounter = useCounter;
    }
    
    public static KDFDoublePipelineIterationParameters createWithCounter(final byte[] array, final byte[] array2, final int n) {
        return new KDFDoublePipelineIterationParameters(array, array2, n, true);
    }
    
    public static KDFDoublePipelineIterationParameters createWithoutCounter(final byte[] array, final byte[] array2) {
        return new KDFDoublePipelineIterationParameters(array, array2, 32, false);
    }
    
    public byte[] getKI() {
        return this.ki;
    }
    
    public boolean useCounter() {
        return this.useCounter;
    }
    
    public int getR() {
        return this.r;
    }
    
    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputData);
    }
}
