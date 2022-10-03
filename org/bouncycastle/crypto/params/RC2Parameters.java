package org.bouncycastle.crypto.params;

public class RC2Parameters extends KeyParameter
{
    private int bits;
    
    public RC2Parameters(final byte[] array) {
        this(array, (array.length > 128) ? 1024 : (array.length * 8));
    }
    
    public RC2Parameters(final byte[] array, final int bits) {
        super(array);
        this.bits = bits;
    }
    
    public int getEffectiveKeyBits() {
        return this.bits;
    }
}
