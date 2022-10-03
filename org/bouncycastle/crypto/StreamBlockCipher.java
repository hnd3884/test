package org.bouncycastle.crypto;

public abstract class StreamBlockCipher implements BlockCipher, StreamCipher
{
    private final BlockCipher cipher;
    
    protected StreamBlockCipher(final BlockCipher cipher) {
        this.cipher = cipher;
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public final byte returnByte(final byte b) {
        return this.calculateByte(b);
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too small");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = n, n4 = n + n2, n5 = n3; i < n4; array2[n5++] = this.calculateByte(array[i++])) {}
        return n2;
    }
    
    protected abstract byte calculateByte(final byte p0);
}
