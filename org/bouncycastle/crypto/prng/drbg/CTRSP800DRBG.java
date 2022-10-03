package org.bouncycastle.crypto.prng.drbg;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.prng.EntropySource;

public class CTRSP800DRBG implements SP80090DRBG
{
    private static final long TDEA_RESEED_MAX = 2147483648L;
    private static final long AES_RESEED_MAX = 140737488355328L;
    private static final int TDEA_MAX_BITS_REQUEST = 4096;
    private static final int AES_MAX_BITS_REQUEST = 262144;
    private EntropySource _entropySource;
    private BlockCipher _engine;
    private int _keySizeInBits;
    private int _seedLength;
    private int _securityStrength;
    private byte[] _Key;
    private byte[] _V;
    private long _reseedCounter;
    private boolean _isTDEA;
    private static final byte[] K_BITS;
    
    public CTRSP800DRBG(final BlockCipher engine, final int keySizeInBits, final int securityStrength, final EntropySource entropySource, final byte[] array, final byte[] array2) {
        this._reseedCounter = 0L;
        this._isTDEA = false;
        this._entropySource = entropySource;
        this._engine = engine;
        this._keySizeInBits = keySizeInBits;
        this._securityStrength = securityStrength;
        this._seedLength = keySizeInBits + engine.getBlockSize() * 8;
        this._isTDEA = this.isTDEA(engine);
        if (securityStrength > 256) {
            throw new IllegalArgumentException("Requested security strength is not supported by the derivation function");
        }
        if (this.getMaxSecurityStrength(engine, keySizeInBits) < securityStrength) {
            throw new IllegalArgumentException("Requested security strength is not supported by block cipher and key size");
        }
        if (entropySource.entropySize() < securityStrength) {
            throw new IllegalArgumentException("Not enough entropy for security strength required");
        }
        this.CTR_DRBG_Instantiate_algorithm(this.getEntropy(), array2, array);
    }
    
    private void CTR_DRBG_Instantiate_algorithm(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] block_Cipher_df = this.Block_Cipher_df(Arrays.concatenate(array, array2, array3), this._seedLength);
        final int blockSize = this._engine.getBlockSize();
        this._Key = new byte[(this._keySizeInBits + 7) / 8];
        this._V = new byte[blockSize];
        this.CTR_DRBG_Update(block_Cipher_df, this._Key, this._V);
        this._reseedCounter = 1L;
    }
    
    private void CTR_DRBG_Update(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] array4 = new byte[array.length];
        final byte[] array5 = new byte[this._engine.getBlockSize()];
        int n = 0;
        final int blockSize = this._engine.getBlockSize();
        this._engine.init(true, new KeyParameter(this.expandKey(array2)));
        while (n * blockSize < array.length) {
            this.addOneTo(array3);
            this._engine.processBlock(array3, 0, array5, 0);
            System.arraycopy(array5, 0, array4, n * blockSize, (array4.length - n * blockSize > blockSize) ? blockSize : (array4.length - n * blockSize));
            ++n;
        }
        this.XOR(array4, array, array4, 0);
        System.arraycopy(array4, 0, array2, 0, array2.length);
        System.arraycopy(array4, array2.length, array3, 0, array3.length);
    }
    
    private void CTR_DRBG_Reseed_algorithm(final byte[] array) {
        this.CTR_DRBG_Update(this.Block_Cipher_df(Arrays.concatenate(this.getEntropy(), array), this._seedLength), this._Key, this._V);
        this._reseedCounter = 1L;
    }
    
    private void XOR(final byte[] array, final byte[] array2, final byte[] array3, final int n) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = (byte)(array2[i] ^ array3[i + n]);
        }
    }
    
    private void addOneTo(final byte[] array) {
        int n = 1;
        for (int i = 1; i <= array.length; ++i) {
            final int n2 = (array[array.length - i] & 0xFF) + n;
            n = ((n2 > 255) ? 1 : 0);
            array[array.length - i] = (byte)n2;
        }
    }
    
    private byte[] getEntropy() {
        final byte[] entropy = this._entropySource.getEntropy();
        if (entropy.length < (this._securityStrength + 7) / 8) {
            throw new IllegalStateException("Insufficient entropy provided by entropy source");
        }
        return entropy;
    }
    
    private byte[] Block_Cipher_df(final byte[] array, final int n) {
        final int blockSize = this._engine.getBlockSize();
        final int length = array.length;
        final int n2 = n / 8;
        final byte[] array2 = new byte[(8 + length + 1 + blockSize - 1) / blockSize * blockSize];
        this.copyIntToByteArray(array2, length, 0);
        this.copyIntToByteArray(array2, n2, 4);
        System.arraycopy(array, 0, array2, 8, length);
        array2[8 + length] = -128;
        final byte[] array3 = new byte[this._keySizeInBits / 8 + blockSize];
        final byte[] array4 = new byte[blockSize];
        final byte[] array5 = new byte[blockSize];
        int n3 = 0;
        final byte[] array6 = new byte[this._keySizeInBits / 8];
        System.arraycopy(CTRSP800DRBG.K_BITS, 0, array6, 0, array6.length);
        while (n3 * blockSize * 8 < this._keySizeInBits + blockSize * 8) {
            this.copyIntToByteArray(array5, n3, 0);
            this.BCC(array4, array6, array5, array2);
            System.arraycopy(array4, 0, array3, n3 * blockSize, (array3.length - n3 * blockSize > blockSize) ? blockSize : (array3.length - n3 * blockSize));
            ++n3;
        }
        final byte[] array7 = new byte[blockSize];
        System.arraycopy(array3, 0, array6, 0, array6.length);
        System.arraycopy(array3, array6.length, array7, 0, array7.length);
        final byte[] array8 = new byte[n / 8];
        int n4 = 0;
        this._engine.init(true, new KeyParameter(this.expandKey(array6)));
        while (n4 * blockSize < array8.length) {
            this._engine.processBlock(array7, 0, array7, 0);
            System.arraycopy(array7, 0, array8, n4 * blockSize, (array8.length - n4 * blockSize > blockSize) ? blockSize : (array8.length - n4 * blockSize));
            ++n4;
        }
        return array8;
    }
    
    private void BCC(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        final int blockSize = this._engine.getBlockSize();
        final byte[] array5 = new byte[blockSize];
        final int n = array4.length / blockSize;
        final byte[] array6 = new byte[blockSize];
        this._engine.init(true, new KeyParameter(this.expandKey(array2)));
        this._engine.processBlock(array3, 0, array5, 0);
        for (int i = 0; i < n; ++i) {
            this.XOR(array6, array5, array4, i * blockSize);
            this._engine.processBlock(array6, 0, array5, 0);
        }
        System.arraycopy(array5, 0, array, 0, array.length);
    }
    
    private void copyIntToByteArray(final byte[] array, final int n, final int n2) {
        array[n2 + 0] = (byte)(n >> 24);
        array[n2 + 1] = (byte)(n >> 16);
        array[n2 + 2] = (byte)(n >> 8);
        array[n2 + 3] = (byte)n;
    }
    
    public int getBlockSize() {
        return this._V.length * 8;
    }
    
    public int generate(final byte[] array, byte[] block_Cipher_df, final boolean b) {
        if (this._isTDEA) {
            if (this._reseedCounter > 2147483648L) {
                return -1;
            }
            if (Utils.isTooLarge(array, 512)) {
                throw new IllegalArgumentException("Number of bits per request limited to 4096");
            }
        }
        else {
            if (this._reseedCounter > 140737488355328L) {
                return -1;
            }
            if (Utils.isTooLarge(array, 32768)) {
                throw new IllegalArgumentException("Number of bits per request limited to 262144");
            }
        }
        if (b) {
            this.CTR_DRBG_Reseed_algorithm(block_Cipher_df);
            block_Cipher_df = null;
        }
        if (block_Cipher_df != null) {
            block_Cipher_df = this.Block_Cipher_df(block_Cipher_df, this._seedLength);
            this.CTR_DRBG_Update(block_Cipher_df, this._Key, this._V);
        }
        else {
            block_Cipher_df = new byte[this._seedLength];
        }
        final byte[] array2 = new byte[this._V.length];
        this._engine.init(true, new KeyParameter(this.expandKey(this._Key)));
        for (int i = 0; i <= array.length / array2.length; ++i) {
            final int n = (array.length - i * array2.length > array2.length) ? array2.length : (array.length - i * this._V.length);
            if (n != 0) {
                this.addOneTo(this._V);
                this._engine.processBlock(this._V, 0, array2, 0);
                System.arraycopy(array2, 0, array, i * array2.length, n);
            }
        }
        this.CTR_DRBG_Update(block_Cipher_df, this._Key, this._V);
        ++this._reseedCounter;
        return array.length * 8;
    }
    
    public void reseed(final byte[] array) {
        this.CTR_DRBG_Reseed_algorithm(array);
    }
    
    private boolean isTDEA(final BlockCipher blockCipher) {
        return blockCipher.getAlgorithmName().equals("DESede") || blockCipher.getAlgorithmName().equals("TDEA");
    }
    
    private int getMaxSecurityStrength(final BlockCipher blockCipher, final int n) {
        if (this.isTDEA(blockCipher) && n == 168) {
            return 112;
        }
        if (blockCipher.getAlgorithmName().equals("AES")) {
            return n;
        }
        return -1;
    }
    
    byte[] expandKey(final byte[] array) {
        if (this._isTDEA) {
            final byte[] array2 = new byte[24];
            this.padKey(array, 0, array2, 0);
            this.padKey(array, 7, array2, 8);
            this.padKey(array, 14, array2, 16);
            return array2;
        }
        return array;
    }
    
    private void padKey(final byte[] array, final int n, final byte[] array2, final int n2) {
        array2[n2 + 0] = (byte)(array[n + 0] & 0xFE);
        array2[n2 + 1] = (byte)(array[n + 0] << 7 | (array[n + 1] & 0xFC) >>> 1);
        array2[n2 + 2] = (byte)(array[n + 1] << 6 | (array[n + 2] & 0xF8) >>> 2);
        array2[n2 + 3] = (byte)(array[n + 2] << 5 | (array[n + 3] & 0xF0) >>> 3);
        array2[n2 + 4] = (byte)(array[n + 3] << 4 | (array[n + 4] & 0xE0) >>> 4);
        array2[n2 + 5] = (byte)(array[n + 4] << 3 | (array[n + 5] & 0xC0) >>> 5);
        array2[n2 + 6] = (byte)(array[n + 5] << 2 | (array[n + 6] & 0x80) >>> 6);
        array2[n2 + 7] = (byte)(array[n + 6] << 1);
        for (int i = n2; i <= n2 + 7; ++i) {
            final byte b = array2[i];
            array2[i] = (byte)((b & 0xFE) | ((b >> 1 ^ b >> 2 ^ b >> 3 ^ b >> 4 ^ b >> 5 ^ b >> 6 ^ b >> 7 ^ 0x1) & 0x1));
        }
    }
    
    static {
        K_BITS = Hex.decode("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");
    }
}
