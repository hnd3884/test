package org.bouncycastle.crypto.params;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DerivationParameters;

public final class KDFFeedbackParameters implements DerivationParameters
{
    private static final int UNUSED_R = -1;
    private final byte[] ki;
    private final byte[] iv;
    private final boolean useCounter;
    private final int r;
    private final byte[] fixedInputData;
    
    private KDFFeedbackParameters(final byte[] array, final byte[] array2, final byte[] array3, final int r, final boolean useCounter) {
        if (array == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(array);
        if (array3 == null) {
            this.fixedInputData = new byte[0];
        }
        else {
            this.fixedInputData = Arrays.clone(array3);
        }
        this.r = r;
        if (array2 == null) {
            this.iv = new byte[0];
        }
        else {
            this.iv = Arrays.clone(array2);
        }
        this.useCounter = useCounter;
    }
    
    public static KDFFeedbackParameters createWithCounter(final byte[] array, final byte[] array2, final byte[] array3, final int n) {
        if (n != 8 && n != 16 && n != 24 && n != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        return new KDFFeedbackParameters(array, array2, array3, n, true);
    }
    
    public static KDFFeedbackParameters createWithoutCounter(final byte[] array, final byte[] array2, final byte[] array3) {
        return new KDFFeedbackParameters(array, array2, array3, -1, false);
    }
    
    public byte[] getKI() {
        return this.ki;
    }
    
    public byte[] getIV() {
        return this.iv;
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
