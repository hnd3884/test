package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.ExtendedDigest;

public class Blake2sDigest implements ExtendedDigest
{
    private static final int[] blake2s_IV;
    private static final byte[][] blake2s_sigma;
    private static final int ROUNDS = 10;
    private static final int BLOCK_LENGTH_BYTES = 64;
    private int digestLength;
    private int keyLength;
    private byte[] salt;
    private byte[] personalization;
    private byte[] key;
    private byte[] buffer;
    private int bufferPos;
    private int[] internalState;
    private int[] chainValue;
    private int t0;
    private int t1;
    private int f0;
    
    public Blake2sDigest() {
        this(256);
    }
    
    public Blake2sDigest(final Blake2sDigest blake2sDigest) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.bufferPos = blake2sDigest.bufferPos;
        this.buffer = Arrays.clone(blake2sDigest.buffer);
        this.keyLength = blake2sDigest.keyLength;
        this.key = Arrays.clone(blake2sDigest.key);
        this.digestLength = blake2sDigest.digestLength;
        this.chainValue = Arrays.clone(blake2sDigest.chainValue);
        this.personalization = Arrays.clone(blake2sDigest.personalization);
    }
    
    public Blake2sDigest(final int n) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        if (n != 128 && n != 160 && n != 224 && n != 256) {
            throw new IllegalArgumentException("BLAKE2s digest restricted to one of [128, 160, 224, 256]");
        }
        this.buffer = new byte[64];
        this.keyLength = 0;
        this.digestLength = n / 8;
        this.init();
    }
    
    public Blake2sDigest(final byte[] array) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.buffer = new byte[64];
        if (array != null) {
            if (array.length > 32) {
                throw new IllegalArgumentException("Keys > 32 are not supported");
            }
            System.arraycopy(array, 0, this.key = new byte[array.length], 0, array.length);
            this.keyLength = array.length;
            System.arraycopy(array, 0, this.buffer, 0, array.length);
            this.bufferPos = 64;
        }
        this.digestLength = 32;
        this.init();
    }
    
    public Blake2sDigest(final byte[] array, final int digestLength, final byte[] array2, final byte[] array3) {
        this.digestLength = 32;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new int[16];
        this.chainValue = null;
        this.t0 = 0;
        this.t1 = 0;
        this.f0 = 0;
        this.buffer = new byte[64];
        if (digestLength < 1 || digestLength > 32) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 32)");
        }
        this.digestLength = digestLength;
        if (array2 != null) {
            if (array2.length != 8) {
                throw new IllegalArgumentException("Salt length must be exactly 8 bytes");
            }
            System.arraycopy(array2, 0, this.salt = new byte[8], 0, array2.length);
        }
        if (array3 != null) {
            if (array3.length != 8) {
                throw new IllegalArgumentException("Personalization length must be exactly 8 bytes");
            }
            System.arraycopy(array3, 0, this.personalization = new byte[8], 0, array3.length);
        }
        if (array != null) {
            if (array.length > 32) {
                throw new IllegalArgumentException("Keys > 32 bytes are not supported");
            }
            System.arraycopy(array, 0, this.key = new byte[array.length], 0, array.length);
            this.keyLength = array.length;
            System.arraycopy(array, 0, this.buffer, 0, array.length);
            this.bufferPos = 64;
        }
        this.init();
    }
    
    private void init() {
        if (this.chainValue == null) {
            (this.chainValue = new int[8])[0] = (Blake2sDigest.blake2s_IV[0] ^ (this.digestLength | this.keyLength << 8 | 0x1010000));
            this.chainValue[1] = Blake2sDigest.blake2s_IV[1];
            this.chainValue[2] = Blake2sDigest.blake2s_IV[2];
            this.chainValue[3] = Blake2sDigest.blake2s_IV[3];
            this.chainValue[4] = Blake2sDigest.blake2s_IV[4];
            this.chainValue[5] = Blake2sDigest.blake2s_IV[5];
            if (this.salt != null) {
                final int[] chainValue = this.chainValue;
                final int n = 4;
                chainValue[n] ^= this.bytes2int(this.salt, 0);
                final int[] chainValue2 = this.chainValue;
                final int n2 = 5;
                chainValue2[n2] ^= this.bytes2int(this.salt, 4);
            }
            this.chainValue[6] = Blake2sDigest.blake2s_IV[6];
            this.chainValue[7] = Blake2sDigest.blake2s_IV[7];
            if (this.personalization != null) {
                final int[] chainValue3 = this.chainValue;
                final int n3 = 6;
                chainValue3[n3] ^= this.bytes2int(this.personalization, 0);
                final int[] chainValue4 = this.chainValue;
                final int n4 = 7;
                chainValue4[n4] ^= this.bytes2int(this.personalization, 4);
            }
        }
    }
    
    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(Blake2sDigest.blake2s_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = (this.t0 ^ Blake2sDigest.blake2s_IV[4]);
        this.internalState[13] = (this.t1 ^ Blake2sDigest.blake2s_IV[5]);
        this.internalState[14] = (this.f0 ^ Blake2sDigest.blake2s_IV[6]);
        this.internalState[15] = Blake2sDigest.blake2s_IV[7];
    }
    
    public void update(final byte b) {
        if (64 - this.bufferPos == 0) {
            this.t0 += 64;
            if (this.t0 == 0) {
                ++this.t1;
            }
            this.compress(this.buffer, 0);
            Arrays.fill(this.buffer, (byte)0);
            this.buffer[0] = b;
            this.bufferPos = 1;
        }
        else {
            this.buffer[this.bufferPos] = b;
            ++this.bufferPos;
        }
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        if (array == null || n2 == 0) {
            return;
        }
        int n3 = 0;
        if (this.bufferPos != 0) {
            n3 = 64 - this.bufferPos;
            if (n3 >= n2) {
                System.arraycopy(array, n, this.buffer, this.bufferPos, n2);
                this.bufferPos += n2;
                return;
            }
            System.arraycopy(array, n, this.buffer, this.bufferPos, n3);
            this.t0 += 64;
            if (this.t0 == 0) {
                ++this.t1;
            }
            this.compress(this.buffer, 0);
            this.bufferPos = 0;
            Arrays.fill(this.buffer, (byte)0);
        }
        int n4;
        int i;
        for (n4 = n + n2 - 64, i = n + n3; i < n4; i += 64) {
            this.t0 += 64;
            if (this.t0 == 0) {
                ++this.t1;
            }
            this.compress(array, i);
        }
        System.arraycopy(array, i, this.buffer, 0, n + n2 - i);
        this.bufferPos += n + n2 - i;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.f0 = -1;
        this.t0 += this.bufferPos;
        if (this.t0 < 0 && this.bufferPos > -this.t0) {
            ++this.t1;
        }
        this.compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.internalState, 0);
        for (int n2 = 0; n2 < this.chainValue.length && n2 * 4 < this.digestLength; ++n2) {
            final byte[] int2bytes = this.int2bytes(this.chainValue[n2]);
            if (n2 * 4 < this.digestLength - 4) {
                System.arraycopy(int2bytes, 0, array, n + n2 * 4, 4);
            }
            else {
                System.arraycopy(int2bytes, 0, array, n + n2 * 4, this.digestLength - n2 * 4);
            }
        }
        Arrays.fill(this.chainValue, 0);
        this.reset();
        return this.digestLength;
    }
    
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0;
        this.t0 = 0;
        this.t1 = 0;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte)0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 64;
        }
        this.init();
    }
    
    private void compress(final byte[] array, final int n) {
        this.initializeInternalState();
        final int[] array2 = new int[16];
        for (int i = 0; i < 16; ++i) {
            array2[i] = this.bytes2int(array, n + i * 4);
        }
        for (int j = 0; j < 10; ++j) {
            this.G(array2[Blake2sDigest.blake2s_sigma[j][0]], array2[Blake2sDigest.blake2s_sigma[j][1]], 0, 4, 8, 12);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][2]], array2[Blake2sDigest.blake2s_sigma[j][3]], 1, 5, 9, 13);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][4]], array2[Blake2sDigest.blake2s_sigma[j][5]], 2, 6, 10, 14);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][6]], array2[Blake2sDigest.blake2s_sigma[j][7]], 3, 7, 11, 15);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][8]], array2[Blake2sDigest.blake2s_sigma[j][9]], 0, 5, 10, 15);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][10]], array2[Blake2sDigest.blake2s_sigma[j][11]], 1, 6, 11, 12);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][12]], array2[Blake2sDigest.blake2s_sigma[j][13]], 2, 7, 8, 13);
            this.G(array2[Blake2sDigest.blake2s_sigma[j][14]], array2[Blake2sDigest.blake2s_sigma[j][15]], 3, 4, 9, 14);
        }
        for (int k = 0; k < this.chainValue.length; ++k) {
            this.chainValue[k] = (this.chainValue[k] ^ this.internalState[k] ^ this.internalState[k + 8]);
        }
    }
    
    private void G(final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        this.internalState[n3] = this.internalState[n3] + this.internalState[n4] + n;
        this.internalState[n6] = this.rotr32(this.internalState[n6] ^ this.internalState[n3], 16);
        this.internalState[n5] += this.internalState[n6];
        this.internalState[n4] = this.rotr32(this.internalState[n4] ^ this.internalState[n5], 12);
        this.internalState[n3] = this.internalState[n3] + this.internalState[n4] + n2;
        this.internalState[n6] = this.rotr32(this.internalState[n6] ^ this.internalState[n3], 8);
        this.internalState[n5] += this.internalState[n6];
        this.internalState[n4] = this.rotr32(this.internalState[n4] ^ this.internalState[n5], 7);
    }
    
    private int rotr32(final int n, final int n2) {
        return n >>> n2 | n << 32 - n2;
    }
    
    private byte[] int2bytes(final int n) {
        return new byte[] { (byte)n, (byte)(n >> 8), (byte)(n >> 16), (byte)(n >> 24) };
    }
    
    private int bytes2int(final byte[] array, final int n) {
        return (array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF) << 16 | (array[n + 3] & 0xFF) << 24;
    }
    
    public String getAlgorithmName() {
        return "BLAKE2s";
    }
    
    public int getDigestSize() {
        return this.digestLength;
    }
    
    public int getByteLength() {
        return 64;
    }
    
    public void clearKey() {
        if (this.key != null) {
            Arrays.fill(this.key, (byte)0);
            Arrays.fill(this.buffer, (byte)0);
        }
    }
    
    public void clearSalt() {
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte)0);
        }
    }
    
    static {
        blake2s_IV = new int[] { 1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225 };
        blake2s_sigma = new byte[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, { 14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3 }, { 11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4 }, { 7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8 }, { 9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13 }, { 2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9 }, { 12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11 }, { 13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10 }, { 6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5 }, { 10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0 } };
    }
}
