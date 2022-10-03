package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class Shacal2Engine implements BlockCipher
{
    private static final int[] K;
    private static final int BLOCK_SIZE = 32;
    private boolean forEncryption;
    private static final int ROUNDS = 64;
    private int[] workingKey;
    
    public Shacal2Engine() {
        this.forEncryption = false;
        this.workingKey = null;
    }
    
    public void reset() {
    }
    
    public String getAlgorithmName() {
        return "Shacal2";
    }
    
    public int getBlockSize() {
        return 32;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("only simple KeyParameter expected.");
        }
        this.forEncryption = forEncryption;
        this.workingKey = new int[64];
        this.setKey(((KeyParameter)cipherParameters).getKey());
    }
    
    public void setKey(final byte[] array) {
        if (array.length == 0 || array.length > 64 || array.length < 16 || array.length % 8 != 0) {
            throw new IllegalArgumentException("Shacal2-key must be 16 - 64 bytes and multiple of 8");
        }
        this.bytes2ints(array, this.workingKey, 0, 0);
        for (int i = 16; i < 64; ++i) {
            this.workingKey[i] = ((this.workingKey[i - 2] >>> 17 | this.workingKey[i - 2] << -17) ^ (this.workingKey[i - 2] >>> 19 | this.workingKey[i - 2] << -19) ^ this.workingKey[i - 2] >>> 10) + this.workingKey[i - 7] + ((this.workingKey[i - 15] >>> 7 | this.workingKey[i - 15] << -7) ^ (this.workingKey[i - 15] >>> 18 | this.workingKey[i - 15] << -18) ^ this.workingKey[i - 15] >>> 3) + this.workingKey[i - 16];
        }
    }
    
    private void encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int[] array3 = new int[8];
        this.byteBlockToInts(array, array3, n, 0);
        for (int i = 0; i < 64; ++i) {
            final int n3 = ((array3[4] >>> 6 | array3[4] << -6) ^ (array3[4] >>> 11 | array3[4] << -11) ^ (array3[4] >>> 25 | array3[4] << -25)) + ((array3[4] & array3[5]) ^ (~array3[4] & array3[6])) + array3[7] + Shacal2Engine.K[i] + this.workingKey[i];
            array3[7] = array3[6];
            array3[6] = array3[5];
            array3[5] = array3[4];
            array3[4] = array3[3] + n3;
            array3[3] = array3[2];
            array3[2] = array3[1];
            array3[1] = array3[0];
            array3[0] = n3 + ((array3[0] >>> 2 | array3[0] << -2) ^ (array3[0] >>> 13 | array3[0] << -13) ^ (array3[0] >>> 22 | array3[0] << -22)) + ((array3[0] & array3[2]) ^ (array3[0] & array3[3]) ^ (array3[2] & array3[3]));
        }
        this.ints2bytes(array3, array2, n2);
    }
    
    private void decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int[] array3 = new int[8];
        this.byteBlockToInts(array, array3, n, 0);
        for (int i = 63; i > -1; --i) {
            final int n3 = array3[0] - ((array3[1] >>> 2 | array3[1] << -2) ^ (array3[1] >>> 13 | array3[1] << -13) ^ (array3[1] >>> 22 | array3[1] << -22)) - ((array3[1] & array3[2]) ^ (array3[1] & array3[3]) ^ (array3[2] & array3[3]));
            array3[0] = array3[1];
            array3[1] = array3[2];
            array3[2] = array3[3];
            array3[3] = array3[4] - n3;
            array3[4] = array3[5];
            array3[5] = array3[6];
            array3[6] = array3[7];
            array3[7] = n3 - Shacal2Engine.K[i] - this.workingKey[i] - ((array3[4] >>> 6 | array3[4] << -6) ^ (array3[4] >>> 11 | array3[4] << -11) ^ (array3[4] >>> 25 | array3[4] << -25)) - ((array3[4] & array3[5]) ^ (~array3[4] & array3[6]));
        }
        this.ints2bytes(array3, array2, n2);
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (this.workingKey == null) {
            throw new IllegalStateException("Shacal2 not initialised");
        }
        if (n + 32 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 32 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            this.encryptBlock(array, n, array2, n2);
        }
        else {
            this.decryptBlock(array, n, array2, n2);
        }
        return 32;
    }
    
    private void byteBlockToInts(final byte[] array, final int[] array2, int n, final int n2) {
        for (int i = n2; i < 8; ++i) {
            array2[i] = ((array[n++] & 0xFF) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF));
        }
    }
    
    private void bytes2ints(final byte[] array, final int[] array2, int n, final int n2) {
        for (int i = n2; i < array.length / 4; ++i) {
            array2[i] = ((array[n++] & 0xFF) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF));
        }
    }
    
    private void ints2bytes(final int[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            array2[n++] = (byte)(array[i] >>> 24);
            array2[n++] = (byte)(array[i] >>> 16);
            array2[n++] = (byte)(array[i] >>> 8);
            array2[n++] = (byte)array[i];
        }
    }
    
    static {
        K = new int[] { 1116352408, 1899447441, -1245643825, -373957723, 961987163, 1508970993, -1841331548, -1424204075, -670586216, 310598401, 607225278, 1426881987, 1925078388, -2132889090, -1680079193, -1046744716, -459576895, -272742522, 264347078, 604807628, 770255983, 1249150122, 1555081692, 1996064986, -1740746414, -1473132947, -1341970488, -1084653625, -958395405, -710438585, 113926993, 338241895, 666307205, 773529912, 1294757372, 1396182291, 1695183700, 1986661051, -2117940946, -1838011259, -1564481375, -1474664885, -1035236496, -949202525, -778901479, -694614492, -200395387, 275423344, 430227734, 506948616, 659060556, 883997877, 958139571, 1322822218, 1537002063, 1747873779, 1955562222, 2024104815, -2067236844, -1933114872, -1866530822, -1538233109, -1090935817, -965641998 };
    }
}
