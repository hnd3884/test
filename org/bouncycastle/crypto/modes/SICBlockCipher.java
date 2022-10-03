package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class SICBlockCipher extends StreamBlockCipher implements SkippingStreamCipher
{
    private final BlockCipher cipher;
    private final int blockSize;
    private byte[] IV;
    private byte[] counter;
    private byte[] counterOut;
    private int byteCount;
    
    public SICBlockCipher(final BlockCipher cipher) {
        super(cipher);
        this.cipher = cipher;
        this.blockSize = this.cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.counter = new byte[this.blockSize];
        this.counterOut = new byte[this.blockSize];
        this.byteCount = 0;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("CTR/SIC mode requires ParametersWithIV");
        }
        final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        this.IV = Arrays.clone(parametersWithIV.getIV());
        if (this.blockSize < this.IV.length) {
            throw new IllegalArgumentException("CTR/SIC mode requires IV no greater than: " + this.blockSize + " bytes.");
        }
        final int n = (8 > this.blockSize / 2) ? (this.blockSize / 2) : 8;
        if (this.blockSize - this.IV.length > n) {
            throw new IllegalArgumentException("CTR/SIC mode requires IV of at least: " + (this.blockSize - n) + " bytes.");
        }
        if (parametersWithIV.getParameters() != null) {
            this.cipher.init(true, parametersWithIV.getParameters());
        }
        this.reset();
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/SIC";
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.blockSize, array2, n2);
        return this.blockSize;
    }
    
    @Override
    protected byte calculateByte(final byte b) throws DataLengthException, IllegalStateException {
        if (this.byteCount == 0) {
            this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
            return (byte)(this.counterOut[this.byteCount++] ^ b);
        }
        final byte b2 = (byte)(this.counterOut[this.byteCount++] ^ b);
        if (this.byteCount == this.counter.length) {
            this.incrementCounterAt(this.byteCount = 0);
            this.checkCounter();
        }
        return b2;
    }
    
    private void checkCounter() {
        if (this.IV.length < this.blockSize) {
            for (int i = 0; i != this.IV.length; ++i) {
                if (this.counter[i] != this.IV[i]) {
                    throw new IllegalStateException("Counter in CTR/SIC mode out of range.");
                }
            }
        }
    }
    
    private void incrementCounterAt(final int n) {
        int n2 = this.counter.length - n;
        while (--n2 >= 0) {
            final byte[] counter = this.counter;
            final int n3 = n2;
            if (++counter[n3] != 0) {
                break;
            }
        }
    }
    
    private void incrementCounter(final int n) {
        final byte b = this.counter[this.counter.length - 1];
        final byte[] counter = this.counter;
        final int n2 = this.counter.length - 1;
        counter[n2] += (byte)n;
        if (b != 0 && this.counter[this.counter.length - 1] < b) {
            this.incrementCounterAt(1);
        }
    }
    
    private void decrementCounterAt(final int n) {
        int n2 = this.counter.length - n;
        while (--n2 >= 0) {
            final byte[] counter = this.counter;
            final int n3 = n2;
            if (--counter[n3] != -1) {
                return;
            }
        }
    }
    
    private void adjustCounter(final long n) {
        if (n >= 0L) {
            long n3;
            final long n2 = n3 = (n + this.byteCount) / this.blockSize;
            if (n3 > 255L) {
                for (int i = 5; i >= 1; --i) {
                    for (long n4 = 1L << 8 * i; n3 >= n4; n3 -= n4) {
                        this.incrementCounterAt(i);
                    }
                }
            }
            this.incrementCounter((int)n3);
            this.byteCount = (int)(n + this.byteCount - this.blockSize * n2);
        }
        else {
            long n6;
            final long n5 = n6 = (-n - this.byteCount) / this.blockSize;
            if (n6 > 255L) {
                for (int j = 5; j >= 1; --j) {
                    for (long n7 = 1L << 8 * j; n6 > n7; n6 -= n7) {
                        this.decrementCounterAt(j);
                    }
                }
            }
            for (long n8 = 0L; n8 != n6; ++n8) {
                this.decrementCounterAt(0);
            }
            final int n9 = (int)(this.byteCount + n + this.blockSize * n5);
            if (n9 >= 0) {
                this.byteCount = 0;
            }
            else {
                this.decrementCounterAt(0);
                this.byteCount = this.blockSize + n9;
            }
        }
    }
    
    public void reset() {
        Arrays.fill(this.counter, (byte)0);
        System.arraycopy(this.IV, 0, this.counter, 0, this.IV.length);
        this.cipher.reset();
        this.byteCount = 0;
    }
    
    public long skip(final long n) {
        this.adjustCounter(n);
        this.checkCounter();
        this.cipher.processBlock(this.counter, 0, this.counterOut, 0);
        return n;
    }
    
    public long seekTo(final long n) {
        this.reset();
        return this.skip(n);
    }
    
    public long getPosition() {
        final byte[] array = new byte[this.counter.length];
        System.arraycopy(this.counter, 0, array, 0, array.length);
        for (int i = array.length - 1; i >= 1; --i) {
            int n;
            if (i < this.IV.length) {
                n = (array[i] & 0xFF) - (this.IV[i] & 0xFF);
            }
            else {
                n = (array[i] & 0xFF);
            }
            if (n < 0) {
                final byte[] array2 = array;
                final int n2 = i - 1;
                --array2[n2];
                n += 256;
            }
            array[i] = (byte)n;
        }
        return Pack.bigEndianToLong(array, array.length - 8) * this.blockSize + this.byteCount;
    }
}
