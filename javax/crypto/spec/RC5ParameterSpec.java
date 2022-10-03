package javax.crypto.spec;

import java.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;

public class RC5ParameterSpec implements AlgorithmParameterSpec
{
    private byte[] iv;
    private int version;
    private int rounds;
    private int wordSize;
    
    public RC5ParameterSpec(final int version, final int rounds, final int wordSize) {
        this.iv = null;
        this.version = version;
        this.rounds = rounds;
        this.wordSize = wordSize;
    }
    
    public RC5ParameterSpec(final int n, final int n2, final int n3, final byte[] array) {
        this(n, n2, n3, array, 0);
    }
    
    public RC5ParameterSpec(final int version, final int rounds, final int wordSize, final byte[] array, final int n) {
        this.iv = null;
        this.version = version;
        this.rounds = rounds;
        this.wordSize = wordSize;
        if (array == null) {
            throw new IllegalArgumentException("IV missing");
        }
        final int n2 = wordSize / 8 * 2;
        if (array.length - n < n2) {
            throw new IllegalArgumentException("IV too short");
        }
        System.arraycopy(array, n, this.iv = new byte[n2], 0, n2);
    }
    
    public int getVersion() {
        return this.version;
    }
    
    public int getRounds() {
        return this.rounds;
    }
    
    public int getWordSize() {
        return this.wordSize;
    }
    
    public byte[] getIV() {
        return (byte[])((this.iv == null) ? null : ((byte[])this.iv.clone()));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RC5ParameterSpec)) {
            return false;
        }
        final RC5ParameterSpec rc5ParameterSpec = (RC5ParameterSpec)o;
        return this.version == rc5ParameterSpec.version && this.rounds == rc5ParameterSpec.rounds && this.wordSize == rc5ParameterSpec.wordSize && Arrays.equals(this.iv, rc5ParameterSpec.iv);
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.iv != null) {
            for (byte b = 1; b < this.iv.length; ++b) {
                n += this.iv[b] * b;
            }
        }
        return n + (this.version + this.rounds + this.wordSize);
    }
}
