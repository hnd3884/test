package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;

public class ISAACEngine implements StreamCipher
{
    private final int sizeL = 8;
    private final int stateArraySize = 256;
    private int[] engineState;
    private int[] results;
    private int a;
    private int b;
    private int c;
    private int index;
    private byte[] keyStream;
    private byte[] workingKey;
    private boolean initialised;
    
    public ISAACEngine() {
        this.engineState = null;
        this.results = null;
        this.a = 0;
        this.b = 0;
        this.c = 0;
        this.index = 0;
        this.keyStream = new byte[1024];
        this.workingKey = null;
        this.initialised = false;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to ISAAC init - " + cipherParameters.getClass().getName());
        }
        this.setKey(((KeyParameter)cipherParameters).getKey());
    }
    
    public byte returnByte(final byte b) {
        if (this.index == 0) {
            this.isaac();
            this.keyStream = Pack.intToBigEndian(this.results);
        }
        final byte b2 = (byte)(this.keyStream[this.index] ^ b);
        this.index = (this.index + 1 & 0x3FF);
        return b2;
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + n2 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            if (this.index == 0) {
                this.isaac();
                this.keyStream = Pack.intToBigEndian(this.results);
            }
            array2[i + n3] = (byte)(this.keyStream[this.index] ^ array[i + n]);
            this.index = (this.index + 1 & 0x3FF);
        }
        return n2;
    }
    
    public String getAlgorithmName() {
        return "ISAAC";
    }
    
    public void reset() {
        this.setKey(this.workingKey);
    }
    
    private void setKey(final byte[] workingKey) {
        this.workingKey = workingKey;
        if (this.engineState == null) {
            this.engineState = new int[256];
        }
        if (this.results == null) {
            this.results = new int[256];
        }
        for (int i = 0; i < 256; ++i) {
            this.engineState[i] = (this.results[i] = 0);
        }
        final int a = 0;
        this.c = a;
        this.b = a;
        this.a = a;
        this.index = 0;
        final byte[] array = new byte[workingKey.length + (workingKey.length & 0x3)];
        System.arraycopy(workingKey, 0, array, 0, workingKey.length);
        for (int j = 0; j < array.length; j += 4) {
            this.results[j >>> 2] = Pack.littleEndianToInt(array, j);
        }
        final int[] array2 = new int[8];
        for (int k = 0; k < 8; ++k) {
            array2[k] = -1640531527;
        }
        for (int l = 0; l < 4; ++l) {
            this.mix(array2);
        }
        for (int n = 0; n < 2; ++n) {
            for (int n2 = 0; n2 < 256; n2 += 8) {
                for (int n3 = 0; n3 < 8; ++n3) {
                    final int[] array3 = array2;
                    final int n4 = n3;
                    array3[n4] += ((n < 1) ? this.results[n2 + n3] : this.engineState[n2 + n3]);
                }
                this.mix(array2);
                for (int n5 = 0; n5 < 8; ++n5) {
                    this.engineState[n2 + n5] = array2[n5];
                }
            }
        }
        this.isaac();
        this.initialised = true;
    }
    
    private void isaac() {
        this.b += ++this.c;
        for (int i = 0; i < 256; ++i) {
            final int n = this.engineState[i];
            switch (i & 0x3) {
                case 0: {
                    this.a ^= this.a << 13;
                    break;
                }
                case 1: {
                    this.a ^= this.a >>> 6;
                    break;
                }
                case 2: {
                    this.a ^= this.a << 2;
                    break;
                }
                case 3: {
                    this.a ^= this.a >>> 16;
                    break;
                }
            }
            this.a += this.engineState[i + 128 & 0xFF];
            this.results[i] = (this.b = this.engineState[(this.engineState[i] = this.engineState[n >>> 2 & 0xFF] + this.a + this.b) >>> 10 & 0xFF] + n);
        }
    }
    
    private void mix(final int[] array) {
        final int n = 0;
        array[n] ^= array[1] << 11;
        final int n2 = 3;
        array[n2] += array[0];
        final int n3 = 1;
        array[n3] += array[2];
        final int n4 = 1;
        array[n4] ^= array[2] >>> 2;
        final int n5 = 4;
        array[n5] += array[1];
        final int n6 = 2;
        array[n6] += array[3];
        final int n7 = 2;
        array[n7] ^= array[3] << 8;
        final int n8 = 5;
        array[n8] += array[2];
        final int n9 = 3;
        array[n9] += array[4];
        final int n10 = 3;
        array[n10] ^= array[4] >>> 16;
        final int n11 = 6;
        array[n11] += array[3];
        final int n12 = 4;
        array[n12] += array[5];
        final int n13 = 4;
        array[n13] ^= array[5] << 10;
        final int n14 = 7;
        array[n14] += array[4];
        final int n15 = 5;
        array[n15] += array[6];
        final int n16 = 5;
        array[n16] ^= array[6] >>> 4;
        final int n17 = 0;
        array[n17] += array[5];
        final int n18 = 6;
        array[n18] += array[7];
        final int n19 = 6;
        array[n19] ^= array[7] << 8;
        final int n20 = 1;
        array[n20] += array[6];
        final int n21 = 7;
        array[n21] += array[0];
        final int n22 = 7;
        array[n22] ^= array[0] >>> 9;
        final int n23 = 2;
        array[n23] += array[7];
        final int n24 = 0;
        array[n24] += array[1];
    }
}
