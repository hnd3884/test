package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class CamelliaLightEngine implements BlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private static final int MASK8 = 255;
    private boolean initialized;
    private boolean _keyis128;
    private int[] subkey;
    private int[] kw;
    private int[] ke;
    private int[] state;
    private static final int[] SIGMA;
    private static final byte[] SBOX1;
    
    private static int rightRotate(final int n, final int n2) {
        return (n >>> n2) + (n << 32 - n2);
    }
    
    private static int leftRotate(final int n, final int n2) {
        return (n << n2) + (n >>> 32 - n2);
    }
    
    private static void roldq(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        array2[0 + n3] = (array[0 + n2] << n | array[1 + n2] >>> 32 - n);
        array2[1 + n3] = (array[1 + n2] << n | array[2 + n2] >>> 32 - n);
        array2[2 + n3] = (array[2 + n2] << n | array[3 + n2] >>> 32 - n);
        array2[3 + n3] = (array[3 + n2] << n | array[0 + n2] >>> 32 - n);
        array[0 + n2] = array2[0 + n3];
        array[1 + n2] = array2[1 + n3];
        array[2 + n2] = array2[2 + n3];
        array[3 + n2] = array2[3 + n3];
    }
    
    private static void decroldq(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        array2[2 + n3] = (array[0 + n2] << n | array[1 + n2] >>> 32 - n);
        array2[3 + n3] = (array[1 + n2] << n | array[2 + n2] >>> 32 - n);
        array2[0 + n3] = (array[2 + n2] << n | array[3 + n2] >>> 32 - n);
        array2[1 + n3] = (array[3 + n2] << n | array[0 + n2] >>> 32 - n);
        array[0 + n2] = array2[2 + n3];
        array[1 + n2] = array2[3 + n3];
        array[2 + n2] = array2[0 + n3];
        array[3 + n2] = array2[1 + n3];
    }
    
    private static void roldqo32(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        array2[0 + n3] = (array[1 + n2] << n - 32 | array[2 + n2] >>> 64 - n);
        array2[1 + n3] = (array[2 + n2] << n - 32 | array[3 + n2] >>> 64 - n);
        array2[2 + n3] = (array[3 + n2] << n - 32 | array[0 + n2] >>> 64 - n);
        array2[3 + n3] = (array[0 + n2] << n - 32 | array[1 + n2] >>> 64 - n);
        array[0 + n2] = array2[0 + n3];
        array[1 + n2] = array2[1 + n3];
        array[2 + n2] = array2[2 + n3];
        array[3 + n2] = array2[3 + n3];
    }
    
    private static void decroldqo32(final int n, final int[] array, final int n2, final int[] array2, final int n3) {
        array2[2 + n3] = (array[1 + n2] << n - 32 | array[2 + n2] >>> 64 - n);
        array2[3 + n3] = (array[2 + n2] << n - 32 | array[3 + n2] >>> 64 - n);
        array2[0 + n3] = (array[3 + n2] << n - 32 | array[0 + n2] >>> 64 - n);
        array2[1 + n3] = (array[0 + n2] << n - 32 | array[1 + n2] >>> 64 - n);
        array[0 + n2] = array2[2 + n3];
        array[1 + n2] = array2[3 + n3];
        array[2 + n2] = array2[0 + n3];
        array[3 + n2] = array2[1 + n3];
    }
    
    private int bytes2int(final byte[] array, final int n) {
        int n2 = 0;
        for (int i = 0; i < 4; ++i) {
            n2 = (n2 << 8) + (array[i + n] & 0xFF);
        }
        return n2;
    }
    
    private void int2bytes(int n, final byte[] array, final int n2) {
        for (int i = 0; i < 4; ++i) {
            array[3 - i + n2] = (byte)n;
            n >>>= 8;
        }
    }
    
    private byte lRot8(final byte b, final int n) {
        return (byte)(b << n | (b & 0xFF) >>> 8 - n);
    }
    
    private int sbox2(final int n) {
        return this.lRot8(CamelliaLightEngine.SBOX1[n], 1) & 0xFF;
    }
    
    private int sbox3(final int n) {
        return this.lRot8(CamelliaLightEngine.SBOX1[n], 7) & 0xFF;
    }
    
    private int sbox4(final int n) {
        return CamelliaLightEngine.SBOX1[this.lRot8((byte)n, 1) & 0xFF] & 0xFF;
    }
    
    private void camelliaF2(final int[] array, final int[] array2, final int n) {
        final int n2 = array[0] ^ array2[0 + n];
        final int n3 = this.sbox4(n2 & 0xFF) | this.sbox3(n2 >>> 8 & 0xFF) << 8 | this.sbox2(n2 >>> 16 & 0xFF) << 16 | (CamelliaLightEngine.SBOX1[n2 >>> 24 & 0xFF] & 0xFF) << 24;
        final int n4 = array[1] ^ array2[1 + n];
        final int leftRotate = leftRotate((CamelliaLightEngine.SBOX1[n4 & 0xFF] & 0xFF) | this.sbox4(n4 >>> 8 & 0xFF) << 8 | this.sbox3(n4 >>> 16 & 0xFF) << 16 | this.sbox2(n4 >>> 24 & 0xFF) << 24, 8);
        final int n5 = n3 ^ leftRotate;
        final int n6 = leftRotate(leftRotate, 8) ^ n5;
        final int n7 = rightRotate(n5, 8) ^ n6;
        final int n8 = 2;
        array[n8] ^= (leftRotate(n6, 16) ^ n7);
        final int n9 = 3;
        array[n9] ^= leftRotate(n7, 8);
        final int n10 = array[2] ^ array2[2 + n];
        final int n11 = this.sbox4(n10 & 0xFF) | this.sbox3(n10 >>> 8 & 0xFF) << 8 | this.sbox2(n10 >>> 16 & 0xFF) << 16 | (CamelliaLightEngine.SBOX1[n10 >>> 24 & 0xFF] & 0xFF) << 24;
        final int n12 = array[3] ^ array2[3 + n];
        final int leftRotate2 = leftRotate((CamelliaLightEngine.SBOX1[n12 & 0xFF] & 0xFF) | this.sbox4(n12 >>> 8 & 0xFF) << 8 | this.sbox3(n12 >>> 16 & 0xFF) << 16 | this.sbox2(n12 >>> 24 & 0xFF) << 24, 8);
        final int n13 = n11 ^ leftRotate2;
        final int n14 = leftRotate(leftRotate2, 8) ^ n13;
        final int n15 = rightRotate(n13, 8) ^ n14;
        final int n16 = 0;
        array[n16] ^= (leftRotate(n14, 16) ^ n15);
        final int n17 = 1;
        array[n17] ^= leftRotate(n15, 8);
    }
    
    private void camelliaFLs(final int[] array, final int[] array2, final int n) {
        final int n2 = 1;
        array[n2] ^= leftRotate(array[0] & array2[0 + n], 1);
        final int n3 = 0;
        array[n3] ^= (array2[1 + n] | array[1]);
        final int n4 = 2;
        array[n4] ^= (array2[3 + n] | array[3]);
        final int n5 = 3;
        array[n5] ^= leftRotate(array2[2 + n] & array[2], 1);
    }
    
    private void setKey(final boolean b, final byte[] array) {
        final int[] array2 = new int[8];
        final int[] array3 = new int[4];
        final int[] array4 = new int[4];
        final int[] array5 = new int[4];
        switch (array.length) {
            case 16: {
                this._keyis128 = true;
                array2[0] = this.bytes2int(array, 0);
                array2[1] = this.bytes2int(array, 4);
                array2[2] = this.bytes2int(array, 8);
                array2[3] = this.bytes2int(array, 12);
                final int[] array6 = array2;
                final int n = 4;
                final int[] array7 = array2;
                final int n2 = 5;
                final int[] array8 = array2;
                final int n3 = 6;
                final int[] array9 = array2;
                final int n4 = 7;
                final int n5 = 0;
                array8[n3] = (array9[n4] = n5);
                array6[n] = (array7[n2] = n5);
                break;
            }
            case 24: {
                array2[0] = this.bytes2int(array, 0);
                array2[1] = this.bytes2int(array, 4);
                array2[2] = this.bytes2int(array, 8);
                array2[3] = this.bytes2int(array, 12);
                array2[4] = this.bytes2int(array, 16);
                array2[5] = this.bytes2int(array, 20);
                array2[6] = ~array2[4];
                array2[7] = ~array2[5];
                this._keyis128 = false;
                break;
            }
            case 32: {
                array2[0] = this.bytes2int(array, 0);
                array2[1] = this.bytes2int(array, 4);
                array2[2] = this.bytes2int(array, 8);
                array2[3] = this.bytes2int(array, 12);
                array2[4] = this.bytes2int(array, 16);
                array2[5] = this.bytes2int(array, 20);
                array2[6] = this.bytes2int(array, 24);
                array2[7] = this.bytes2int(array, 28);
                this._keyis128 = false;
                break;
            }
            default: {
                throw new IllegalArgumentException("key sizes are only 16/24/32 bytes.");
            }
        }
        for (int i = 0; i < 4; ++i) {
            array3[i] = (array2[i] ^ array2[i + 4]);
        }
        this.camelliaF2(array3, CamelliaLightEngine.SIGMA, 0);
        for (int j = 0; j < 4; ++j) {
            final int[] array10 = array3;
            final int n6 = j;
            array10[n6] ^= array2[j];
        }
        this.camelliaF2(array3, CamelliaLightEngine.SIGMA, 4);
        if (this._keyis128) {
            if (b) {
                this.kw[0] = array2[0];
                this.kw[1] = array2[1];
                this.kw[2] = array2[2];
                this.kw[3] = array2[3];
                roldq(15, array2, 0, this.subkey, 4);
                roldq(30, array2, 0, this.subkey, 12);
                roldq(15, array2, 0, array5, 0);
                this.subkey[18] = array5[2];
                this.subkey[19] = array5[3];
                roldq(17, array2, 0, this.ke, 4);
                roldq(17, array2, 0, this.subkey, 24);
                roldq(17, array2, 0, this.subkey, 32);
                this.subkey[0] = array3[0];
                this.subkey[1] = array3[1];
                this.subkey[2] = array3[2];
                this.subkey[3] = array3[3];
                roldq(15, array3, 0, this.subkey, 8);
                roldq(15, array3, 0, this.ke, 0);
                roldq(15, array3, 0, array5, 0);
                this.subkey[16] = array5[0];
                this.subkey[17] = array5[1];
                roldq(15, array3, 0, this.subkey, 20);
                roldqo32(34, array3, 0, this.subkey, 28);
                roldq(17, array3, 0, this.kw, 4);
            }
            else {
                this.kw[4] = array2[0];
                this.kw[5] = array2[1];
                this.kw[6] = array2[2];
                this.kw[7] = array2[3];
                decroldq(15, array2, 0, this.subkey, 28);
                decroldq(30, array2, 0, this.subkey, 20);
                decroldq(15, array2, 0, array5, 0);
                this.subkey[16] = array5[0];
                this.subkey[17] = array5[1];
                decroldq(17, array2, 0, this.ke, 0);
                decroldq(17, array2, 0, this.subkey, 8);
                decroldq(17, array2, 0, this.subkey, 0);
                this.subkey[34] = array3[0];
                this.subkey[35] = array3[1];
                this.subkey[32] = array3[2];
                this.subkey[33] = array3[3];
                decroldq(15, array3, 0, this.subkey, 24);
                decroldq(15, array3, 0, this.ke, 4);
                decroldq(15, array3, 0, array5, 0);
                this.subkey[18] = array5[2];
                this.subkey[19] = array5[3];
                decroldq(15, array3, 0, this.subkey, 12);
                decroldqo32(34, array3, 0, this.subkey, 4);
                roldq(17, array3, 0, this.kw, 0);
            }
        }
        else {
            for (int k = 0; k < 4; ++k) {
                array4[k] = (array3[k] ^ array2[k + 4]);
            }
            this.camelliaF2(array4, CamelliaLightEngine.SIGMA, 8);
            if (b) {
                this.kw[0] = array2[0];
                this.kw[1] = array2[1];
                this.kw[2] = array2[2];
                this.kw[3] = array2[3];
                roldqo32(45, array2, 0, this.subkey, 16);
                roldq(15, array2, 0, this.ke, 4);
                roldq(17, array2, 0, this.subkey, 32);
                roldqo32(34, array2, 0, this.subkey, 44);
                roldq(15, array2, 4, this.subkey, 4);
                roldq(15, array2, 4, this.ke, 0);
                roldq(30, array2, 4, this.subkey, 24);
                roldqo32(34, array2, 4, this.subkey, 36);
                roldq(15, array3, 0, this.subkey, 8);
                roldq(30, array3, 0, this.subkey, 20);
                this.ke[8] = array3[1];
                this.ke[9] = array3[2];
                this.ke[10] = array3[3];
                this.ke[11] = array3[0];
                roldqo32(49, array3, 0, this.subkey, 40);
                this.subkey[0] = array4[0];
                this.subkey[1] = array4[1];
                this.subkey[2] = array4[2];
                this.subkey[3] = array4[3];
                roldq(30, array4, 0, this.subkey, 12);
                roldq(30, array4, 0, this.subkey, 28);
                roldqo32(51, array4, 0, this.kw, 4);
            }
            else {
                this.kw[4] = array2[0];
                this.kw[5] = array2[1];
                this.kw[6] = array2[2];
                this.kw[7] = array2[3];
                decroldqo32(45, array2, 0, this.subkey, 28);
                decroldq(15, array2, 0, this.ke, 4);
                decroldq(17, array2, 0, this.subkey, 12);
                decroldqo32(34, array2, 0, this.subkey, 0);
                decroldq(15, array2, 4, this.subkey, 40);
                decroldq(15, array2, 4, this.ke, 8);
                decroldq(30, array2, 4, this.subkey, 20);
                decroldqo32(34, array2, 4, this.subkey, 8);
                decroldq(15, array3, 0, this.subkey, 36);
                decroldq(30, array3, 0, this.subkey, 24);
                this.ke[2] = array3[1];
                this.ke[3] = array3[2];
                this.ke[0] = array3[3];
                this.ke[1] = array3[0];
                decroldqo32(49, array3, 0, this.subkey, 4);
                this.subkey[46] = array4[0];
                this.subkey[47] = array4[1];
                this.subkey[44] = array4[2];
                this.subkey[45] = array4[3];
                decroldq(30, array4, 0, this.subkey, 32);
                decroldq(30, array4, 0, this.subkey, 16);
                roldqo32(51, array4, 0, this.kw, 0);
            }
        }
    }
    
    private int processBlock128(final byte[] array, final int n, final byte[] array2, final int n2) {
        for (int i = 0; i < 4; ++i) {
            this.state[i] = this.bytes2int(array, n + i * 4);
            final int[] state = this.state;
            final int n3 = i;
            state[n3] ^= this.kw[i];
        }
        this.camelliaF2(this.state, this.subkey, 0);
        this.camelliaF2(this.state, this.subkey, 4);
        this.camelliaF2(this.state, this.subkey, 8);
        this.camelliaFLs(this.state, this.ke, 0);
        this.camelliaF2(this.state, this.subkey, 12);
        this.camelliaF2(this.state, this.subkey, 16);
        this.camelliaF2(this.state, this.subkey, 20);
        this.camelliaFLs(this.state, this.ke, 4);
        this.camelliaF2(this.state, this.subkey, 24);
        this.camelliaF2(this.state, this.subkey, 28);
        this.camelliaF2(this.state, this.subkey, 32);
        final int[] state2 = this.state;
        final int n4 = 2;
        state2[n4] ^= this.kw[4];
        final int[] state3 = this.state;
        final int n5 = 3;
        state3[n5] ^= this.kw[5];
        final int[] state4 = this.state;
        final int n6 = 0;
        state4[n6] ^= this.kw[6];
        final int[] state5 = this.state;
        final int n7 = 1;
        state5[n7] ^= this.kw[7];
        this.int2bytes(this.state[2], array2, n2);
        this.int2bytes(this.state[3], array2, n2 + 4);
        this.int2bytes(this.state[0], array2, n2 + 8);
        this.int2bytes(this.state[1], array2, n2 + 12);
        return 16;
    }
    
    private int processBlock192or256(final byte[] array, final int n, final byte[] array2, final int n2) {
        for (int i = 0; i < 4; ++i) {
            this.state[i] = this.bytes2int(array, n + i * 4);
            final int[] state = this.state;
            final int n3 = i;
            state[n3] ^= this.kw[i];
        }
        this.camelliaF2(this.state, this.subkey, 0);
        this.camelliaF2(this.state, this.subkey, 4);
        this.camelliaF2(this.state, this.subkey, 8);
        this.camelliaFLs(this.state, this.ke, 0);
        this.camelliaF2(this.state, this.subkey, 12);
        this.camelliaF2(this.state, this.subkey, 16);
        this.camelliaF2(this.state, this.subkey, 20);
        this.camelliaFLs(this.state, this.ke, 4);
        this.camelliaF2(this.state, this.subkey, 24);
        this.camelliaF2(this.state, this.subkey, 28);
        this.camelliaF2(this.state, this.subkey, 32);
        this.camelliaFLs(this.state, this.ke, 8);
        this.camelliaF2(this.state, this.subkey, 36);
        this.camelliaF2(this.state, this.subkey, 40);
        this.camelliaF2(this.state, this.subkey, 44);
        final int[] state2 = this.state;
        final int n4 = 2;
        state2[n4] ^= this.kw[4];
        final int[] state3 = this.state;
        final int n5 = 3;
        state3[n5] ^= this.kw[5];
        final int[] state4 = this.state;
        final int n6 = 0;
        state4[n6] ^= this.kw[6];
        final int[] state5 = this.state;
        final int n7 = 1;
        state5[n7] ^= this.kw[7];
        this.int2bytes(this.state[2], array2, n2);
        this.int2bytes(this.state[3], array2, n2 + 4);
        this.int2bytes(this.state[0], array2, n2 + 8);
        this.int2bytes(this.state[1], array2, n2 + 12);
        return 16;
    }
    
    public CamelliaLightEngine() {
        this.subkey = new int[96];
        this.kw = new int[8];
        this.ke = new int[12];
        this.state = new int[4];
    }
    
    public String getAlgorithmName() {
        return "Camellia";
    }
    
    public int getBlockSize() {
        return 16;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("only simple KeyParameter expected.");
        }
        this.setKey(b, ((KeyParameter)cipherParameters).getKey());
        this.initialized = true;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws IllegalStateException {
        if (!this.initialized) {
            throw new IllegalStateException("Camellia is not initialized");
        }
        if (n + 16 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this._keyis128) {
            return this.processBlock128(array, n, array2, n2);
        }
        return this.processBlock192or256(array, n, array2, n2);
    }
    
    public void reset() {
    }
    
    static {
        SIGMA = new int[] { -1600231809, 1003262091, -1233459112, 1286239154, -957401297, -380665154, 1426019237, -237801700, 283453434, -563598051, -1336506174, -1276722691 };
        SBOX1 = new byte[] { 112, -126, 44, -20, -77, 39, -64, -27, -28, -123, 87, 53, -22, 12, -82, 65, 35, -17, 107, -109, 69, 25, -91, 33, -19, 14, 79, 78, 29, 101, -110, -67, -122, -72, -81, -113, 124, -21, 31, -50, 62, 48, -36, 95, 94, -59, 11, 26, -90, -31, 57, -54, -43, 71, 93, 61, -39, 1, 90, -42, 81, 86, 108, 77, -117, 13, -102, 102, -5, -52, -80, 45, 116, 18, 43, 32, -16, -79, -124, -103, -33, 76, -53, -62, 52, 126, 118, 5, 109, -73, -87, 49, -47, 23, 4, -41, 20, 88, 58, 97, -34, 27, 17, 28, 50, 15, -100, 22, 83, 24, -14, 34, -2, 68, -49, -78, -61, -75, 122, -111, 36, 8, -24, -88, 96, -4, 105, 80, -86, -48, -96, 125, -95, -119, 98, -105, 84, 91, 30, -107, -32, -1, 100, -46, 16, -60, 0, 72, -93, -9, 117, -37, -118, 3, -26, -38, 9, 63, -35, -108, -121, 92, -125, 2, -51, 74, -112, 51, 115, 103, -10, -13, -99, 127, -65, -30, 82, -101, -40, 38, -56, 55, -58, 59, -127, -106, 111, 75, 19, -66, 99, 46, -23, 121, -89, -116, -97, 110, -68, -114, 41, -11, -7, -74, 47, -3, -76, 89, 120, -104, 6, 106, -25, 70, 113, -70, -44, 37, -85, 66, -120, -94, -115, -6, 114, 7, -71, 85, -8, -18, -84, 10, 54, 73, 42, 104, 60, 56, -15, -92, 64, 40, -45, 123, -69, -55, 67, -63, 21, -29, -83, -12, 119, -57, -128, -98 };
    }
}
