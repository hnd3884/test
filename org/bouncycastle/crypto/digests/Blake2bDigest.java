package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.ExtendedDigest;

public class Blake2bDigest implements ExtendedDigest
{
    private static final long[] blake2b_IV;
    private static final byte[][] blake2b_sigma;
    private static int rOUNDS;
    private static final int BLOCK_LENGTH_BYTES = 128;
    private int digestLength;
    private int keyLength;
    private byte[] salt;
    private byte[] personalization;
    private byte[] key;
    private byte[] buffer;
    private int bufferPos;
    private long[] internalState;
    private long[] chainValue;
    private long t0;
    private long t1;
    private long f0;
    
    public Blake2bDigest() {
        this(512);
    }
    
    public Blake2bDigest(final Blake2bDigest blake2bDigest) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.bufferPos = blake2bDigest.bufferPos;
        this.buffer = Arrays.clone(blake2bDigest.buffer);
        this.keyLength = blake2bDigest.keyLength;
        this.key = Arrays.clone(blake2bDigest.key);
        this.digestLength = blake2bDigest.digestLength;
        this.chainValue = Arrays.clone(blake2bDigest.chainValue);
        this.personalization = Arrays.clone(blake2bDigest.personalization);
        this.salt = Arrays.clone(blake2bDigest.salt);
        this.t0 = blake2bDigest.t0;
        this.t1 = blake2bDigest.t1;
        this.f0 = blake2bDigest.f0;
    }
    
    public Blake2bDigest(final int n) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        if (n != 160 && n != 256 && n != 384 && n != 512) {
            throw new IllegalArgumentException("Blake2b digest restricted to one of [160, 256, 384, 512]");
        }
        this.buffer = new byte[128];
        this.keyLength = 0;
        this.digestLength = n / 8;
        this.init();
    }
    
    public Blake2bDigest(final byte[] array) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.buffer = new byte[128];
        if (array != null) {
            System.arraycopy(array, 0, this.key = new byte[array.length], 0, array.length);
            if (array.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = array.length;
            System.arraycopy(array, 0, this.buffer, 0, array.length);
            this.bufferPos = 128;
        }
        this.digestLength = 64;
        this.init();
    }
    
    public Blake2bDigest(final byte[] array, final int digestLength, final byte[] array2, final byte[] array3) {
        this.digestLength = 64;
        this.keyLength = 0;
        this.salt = null;
        this.personalization = null;
        this.key = null;
        this.buffer = null;
        this.bufferPos = 0;
        this.internalState = new long[16];
        this.chainValue = null;
        this.t0 = 0L;
        this.t1 = 0L;
        this.f0 = 0L;
        this.buffer = new byte[128];
        if (digestLength < 1 || digestLength > 64) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 64)");
        }
        this.digestLength = digestLength;
        if (array2 != null) {
            if (array2.length != 16) {
                throw new IllegalArgumentException("salt length must be exactly 16 bytes");
            }
            System.arraycopy(array2, 0, this.salt = new byte[16], 0, array2.length);
        }
        if (array3 != null) {
            if (array3.length != 16) {
                throw new IllegalArgumentException("personalization length must be exactly 16 bytes");
            }
            System.arraycopy(array3, 0, this.personalization = new byte[16], 0, array3.length);
        }
        if (array != null) {
            System.arraycopy(array, 0, this.key = new byte[array.length], 0, array.length);
            if (array.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = array.length;
            System.arraycopy(array, 0, this.buffer, 0, array.length);
            this.bufferPos = 128;
        }
        this.init();
    }
    
    private void init() {
        if (this.chainValue == null) {
            (this.chainValue = new long[8])[0] = (Blake2bDigest.blake2b_IV[0] ^ (long)(this.digestLength | this.keyLength << 8 | 0x1010000));
            this.chainValue[1] = Blake2bDigest.blake2b_IV[1];
            this.chainValue[2] = Blake2bDigest.blake2b_IV[2];
            this.chainValue[3] = Blake2bDigest.blake2b_IV[3];
            this.chainValue[4] = Blake2bDigest.blake2b_IV[4];
            this.chainValue[5] = Blake2bDigest.blake2b_IV[5];
            if (this.salt != null) {
                final long[] chainValue = this.chainValue;
                final int n = 4;
                chainValue[n] ^= this.bytes2long(this.salt, 0);
                final long[] chainValue2 = this.chainValue;
                final int n2 = 5;
                chainValue2[n2] ^= this.bytes2long(this.salt, 8);
            }
            this.chainValue[6] = Blake2bDigest.blake2b_IV[6];
            this.chainValue[7] = Blake2bDigest.blake2b_IV[7];
            if (this.personalization != null) {
                final long[] chainValue3 = this.chainValue;
                final int n3 = 6;
                chainValue3[n3] ^= this.bytes2long(this.personalization, 0);
                final long[] chainValue4 = this.chainValue;
                final int n4 = 7;
                chainValue4[n4] ^= this.bytes2long(this.personalization, 8);
            }
        }
    }
    
    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(Blake2bDigest.blake2b_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = (this.t0 ^ Blake2bDigest.blake2b_IV[4]);
        this.internalState[13] = (this.t1 ^ Blake2bDigest.blake2b_IV[5]);
        this.internalState[14] = (this.f0 ^ Blake2bDigest.blake2b_IV[6]);
        this.internalState[15] = Blake2bDigest.blake2b_IV[7];
    }
    
    public void update(final byte b) {
        if (128 - this.bufferPos == 0) {
            this.t0 += 128L;
            if (this.t0 == 0L) {
                ++this.t1;
            }
            this.compress(this.buffer, 0);
            Arrays.fill(this.buffer, (byte)0);
            this.buffer[0] = b;
            this.bufferPos = 1;
            return;
        }
        this.buffer[this.bufferPos] = b;
        ++this.bufferPos;
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        if (array == null || n2 == 0) {
            return;
        }
        int n3 = 0;
        if (this.bufferPos != 0) {
            n3 = 128 - this.bufferPos;
            if (n3 >= n2) {
                System.arraycopy(array, n, this.buffer, this.bufferPos, n2);
                this.bufferPos += n2;
                return;
            }
            System.arraycopy(array, n, this.buffer, this.bufferPos, n3);
            this.t0 += 128L;
            if (this.t0 == 0L) {
                ++this.t1;
            }
            this.compress(this.buffer, 0);
            this.bufferPos = 0;
            Arrays.fill(this.buffer, (byte)0);
        }
        int n4;
        int i;
        for (n4 = n + n2 - 128, i = n + n3; i < n4; i += 128) {
            this.t0 += 128L;
            if (this.t0 == 0L) {
                ++this.t1;
            }
            this.compress(array, i);
        }
        System.arraycopy(array, i, this.buffer, 0, n + n2 - i);
        this.bufferPos += n + n2 - i;
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.f0 = -1L;
        this.t0 += this.bufferPos;
        if (this.bufferPos > 0 && this.t0 == 0L) {
            ++this.t1;
        }
        this.compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.internalState, 0L);
        for (int n2 = 0; n2 < this.chainValue.length && n2 * 8 < this.digestLength; ++n2) {
            final byte[] long2bytes = this.long2bytes(this.chainValue[n2]);
            if (n2 * 8 < this.digestLength - 8) {
                System.arraycopy(long2bytes, 0, array, n + n2 * 8, 8);
            }
            else {
                System.arraycopy(long2bytes, 0, array, n + n2 * 8, this.digestLength - n2 * 8);
            }
        }
        Arrays.fill(this.chainValue, 0L);
        this.reset();
        return this.digestLength;
    }
    
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0L;
        this.t0 = 0L;
        this.t1 = 0L;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte)0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 128;
        }
        this.init();
    }
    
    private void compress(final byte[] array, final int n) {
        this.initializeInternalState();
        final long[] array2 = new long[16];
        for (int i = 0; i < 16; ++i) {
            array2[i] = this.bytes2long(array, n + i * 8);
        }
        for (int j = 0; j < Blake2bDigest.rOUNDS; ++j) {
            this.G(array2[Blake2bDigest.blake2b_sigma[j][0]], array2[Blake2bDigest.blake2b_sigma[j][1]], 0, 4, 8, 12);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][2]], array2[Blake2bDigest.blake2b_sigma[j][3]], 1, 5, 9, 13);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][4]], array2[Blake2bDigest.blake2b_sigma[j][5]], 2, 6, 10, 14);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][6]], array2[Blake2bDigest.blake2b_sigma[j][7]], 3, 7, 11, 15);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][8]], array2[Blake2bDigest.blake2b_sigma[j][9]], 0, 5, 10, 15);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][10]], array2[Blake2bDigest.blake2b_sigma[j][11]], 1, 6, 11, 12);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][12]], array2[Blake2bDigest.blake2b_sigma[j][13]], 2, 7, 8, 13);
            this.G(array2[Blake2bDigest.blake2b_sigma[j][14]], array2[Blake2bDigest.blake2b_sigma[j][15]], 3, 4, 9, 14);
        }
        for (int k = 0; k < this.chainValue.length; ++k) {
            this.chainValue[k] = (this.chainValue[k] ^ this.internalState[k] ^ this.internalState[k + 8]);
        }
    }
    
    private void G(final long n, final long n2, final int n3, final int n4, final int n5, final int n6) {
        this.internalState[n3] = this.internalState[n3] + this.internalState[n4] + n;
        this.internalState[n6] = this.rotr64(this.internalState[n6] ^ this.internalState[n3], 32);
        this.internalState[n5] += this.internalState[n6];
        this.internalState[n4] = this.rotr64(this.internalState[n4] ^ this.internalState[n5], 24);
        this.internalState[n3] = this.internalState[n3] + this.internalState[n4] + n2;
        this.internalState[n6] = this.rotr64(this.internalState[n6] ^ this.internalState[n3], 16);
        this.internalState[n5] += this.internalState[n6];
        this.internalState[n4] = this.rotr64(this.internalState[n4] ^ this.internalState[n5], 63);
    }
    
    private long rotr64(final long n, final int n2) {
        return n >>> n2 | n << 64 - n2;
    }
    
    private final byte[] long2bytes(final long n) {
        return new byte[] { (byte)n, (byte)(n >> 8), (byte)(n >> 16), (byte)(n >> 24), (byte)(n >> 32), (byte)(n >> 40), (byte)(n >> 48), (byte)(n >> 56) };
    }
    
    private final long bytes2long(final byte[] array, final int n) {
        return ((long)array[n] & 0xFFL) | ((long)array[n + 1] & 0xFFL) << 8 | ((long)array[n + 2] & 0xFFL) << 16 | ((long)array[n + 3] & 0xFFL) << 24 | ((long)array[n + 4] & 0xFFL) << 32 | ((long)array[n + 5] & 0xFFL) << 40 | ((long)array[n + 6] & 0xFFL) << 48 | ((long)array[n + 7] & 0xFFL) << 56;
    }
    
    public String getAlgorithmName() {
        return "Blake2b";
    }
    
    public int getDigestSize() {
        return this.digestLength;
    }
    
    public int getByteLength() {
        return 128;
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
        blake2b_IV = new long[] { 7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L };
        blake2b_sigma = new byte[][] { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, { 14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3 }, { 11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4 }, { 7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8 }, { 9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13 }, { 2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9 }, { 12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11 }, { 13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10 }, { 6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5 }, { 10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0 }, { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 }, { 14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3 } };
        Blake2bDigest.rOUNDS = 12;
    }
}
