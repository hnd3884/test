package org.bouncycastle.crypto.prng;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;

public class FixedSecureRandom extends SecureRandom
{
    private byte[] _data;
    private int _index;
    private int _intPad;
    
    public FixedSecureRandom(final byte[] array) {
        this(false, new byte[][] { array });
    }
    
    public FixedSecureRandom(final byte[][] array) {
        this(false, array);
    }
    
    public FixedSecureRandom(final boolean b, final byte[] array) {
        this(b, new byte[][] { array });
    }
    
    public FixedSecureRandom(final boolean b, final byte[][] array) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i != array.length; ++i) {
            try {
                byteArrayOutputStream.write(array[i]);
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("can't save value array.");
            }
        }
        this._data = byteArrayOutputStream.toByteArray();
        if (b) {
            this._intPad = this._data.length % 4;
        }
    }
    
    @Override
    public void nextBytes(final byte[] array) {
        System.arraycopy(this._data, this._index, array, 0, array.length);
        this._index += array.length;
    }
    
    @Override
    public byte[] generateSeed(final int n) {
        final byte[] array = new byte[n];
        this.nextBytes(array);
        return array;
    }
    
    @Override
    public int nextInt() {
        int n = 0x0 | this.nextValue() << 24 | this.nextValue() << 16;
        if (this._intPad == 2) {
            --this._intPad;
        }
        else {
            n |= this.nextValue() << 8;
        }
        if (this._intPad == 1) {
            --this._intPad;
        }
        else {
            n |= this.nextValue();
        }
        return n;
    }
    
    @Override
    public long nextLong() {
        return 0x0L | (long)this.nextValue() << 56 | (long)this.nextValue() << 48 | (long)this.nextValue() << 40 | (long)this.nextValue() << 32 | (long)this.nextValue() << 24 | (long)this.nextValue() << 16 | (long)this.nextValue() << 8 | (long)this.nextValue();
    }
    
    public boolean isExhausted() {
        return this._index == this._data.length;
    }
    
    private int nextValue() {
        return this._data[this._index++] & 0xFF;
    }
}
