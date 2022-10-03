package org.bouncycastle.jce.spec;

import org.bouncycastle.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class IESParameterSpec implements AlgorithmParameterSpec
{
    private byte[] derivation;
    private byte[] encoding;
    private int macKeySize;
    private int cipherKeySize;
    private byte[] nonce;
    private boolean usePointCompression;
    
    public IESParameterSpec(final byte[] array, final byte[] array2, final int n) {
        this(array, array2, n, -1, null, false);
    }
    
    public IESParameterSpec(final byte[] array, final byte[] array2, final int n, final int n2, final byte[] array3) {
        this(array, array2, n, n2, array3, false);
    }
    
    public IESParameterSpec(final byte[] array, final byte[] array2, final int macKeySize, final int cipherKeySize, final byte[] array3, final boolean usePointCompression) {
        if (array != null) {
            System.arraycopy(array, 0, this.derivation = new byte[array.length], 0, array.length);
        }
        else {
            this.derivation = null;
        }
        if (array2 != null) {
            System.arraycopy(array2, 0, this.encoding = new byte[array2.length], 0, array2.length);
        }
        else {
            this.encoding = null;
        }
        this.macKeySize = macKeySize;
        this.cipherKeySize = cipherKeySize;
        this.nonce = Arrays.clone(array3);
        this.usePointCompression = usePointCompression;
    }
    
    public byte[] getDerivationV() {
        return Arrays.clone(this.derivation);
    }
    
    public byte[] getEncodingV() {
        return Arrays.clone(this.encoding);
    }
    
    public int getMacKeySize() {
        return this.macKeySize;
    }
    
    public int getCipherKeySize() {
        return this.cipherKeySize;
    }
    
    public byte[] getNonce() {
        return Arrays.clone(this.nonce);
    }
    
    public void setPointCompression(final boolean usePointCompression) {
        this.usePointCompression = usePointCompression;
    }
    
    public boolean getPointCompression() {
        return this.usePointCompression;
    }
}
