package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class ChaChaEngine extends Salsa20Engine
{
    public ChaChaEngine() {
    }
    
    public ChaChaEngine(final int n) {
        super(n);
    }
    
    @Override
    public String getAlgorithmName() {
        return "ChaCha" + this.rounds;
    }
    
    @Override
    protected void advanceCounter(final long n) {
        final int n2 = (int)(n >>> 32);
        final int n3 = (int)n;
        if (n2 > 0) {
            final int[] engineState = this.engineState;
            final int n4 = 13;
            engineState[n4] += n2;
        }
        final int n5 = this.engineState[12];
        final int[] engineState2 = this.engineState;
        final int n6 = 12;
        engineState2[n6] += n3;
        if (n5 != 0 && this.engineState[12] < n5) {
            final int[] engineState3 = this.engineState;
            final int n7 = 13;
            ++engineState3[n7];
        }
    }
    
    @Override
    protected void advanceCounter() {
        if (++this.engineState[12] == 0) {
            final int[] engineState = this.engineState;
            final int n = 13;
            ++engineState[n];
        }
    }
    
    @Override
    protected void retreatCounter(final long n) {
        final int n2 = (int)(n >>> 32);
        final int n3 = (int)n;
        if (n2 != 0) {
            if (((long)this.engineState[13] & 0xFFFFFFFFL) < ((long)n2 & 0xFFFFFFFFL)) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            final int[] engineState = this.engineState;
            final int n4 = 13;
            engineState[n4] -= n2;
        }
        if (((long)this.engineState[12] & 0xFFFFFFFFL) >= ((long)n3 & 0xFFFFFFFFL)) {
            final int[] engineState2 = this.engineState;
            final int n5 = 12;
            engineState2[n5] -= n3;
        }
        else {
            if (this.engineState[13] == 0) {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
            final int[] engineState3 = this.engineState;
            final int n6 = 13;
            --engineState3[n6];
            final int[] engineState4 = this.engineState;
            final int n7 = 12;
            engineState4[n7] -= n3;
        }
    }
    
    @Override
    protected void retreatCounter() {
        if (this.engineState[12] == 0 && this.engineState[13] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        final int[] engineState = this.engineState;
        final int n = 12;
        if (--engineState[n] == -1) {
            final int[] engineState2 = this.engineState;
            final int n2 = 13;
            --engineState2[n2];
        }
    }
    
    @Override
    protected long getCounter() {
        return (long)this.engineState[13] << 32 | ((long)this.engineState[12] & 0xFFFFFFFFL);
    }
    
    @Override
    protected void resetCounter() {
        this.engineState[12] = (this.engineState[13] = 0);
    }
    
    @Override
    protected void setKey(final byte[] array, final byte[] array2) {
        if (array != null) {
            if (array.length != 16 && array.length != 32) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            this.packTauOrSigma(array.length, this.engineState, 0);
            Pack.littleEndianToInt(array, 0, this.engineState, 4, 4);
            Pack.littleEndianToInt(array, array.length - 16, this.engineState, 8, 4);
        }
        Pack.littleEndianToInt(array2, 0, this.engineState, 14, 2);
    }
    
    @Override
    protected void generateKeyStream(final byte[] array) {
        chachaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, array, 0);
    }
    
    public static void chachaCore(final int n, final int[] array, final int[] array2) {
        if (array.length != 16) {
            throw new IllegalArgumentException();
        }
        if (array2.length != 16) {
            throw new IllegalArgumentException();
        }
        if (n % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int n2 = array[0];
        int n3 = array[1];
        int n4 = array[2];
        int n5 = array[3];
        int rotl = array[4];
        int rotl2 = array[5];
        int rotl3 = array[6];
        int rotl4 = array[7];
        int n6 = array[8];
        int n7 = array[9];
        int n8 = array[10];
        int n9 = array[11];
        int rotl5 = array[12];
        int rotl6 = array[13];
        int rotl7 = array[14];
        int rotl8 = array[15];
        for (int i = n; i > 0; i -= 2) {
            final int n10 = n2 + rotl;
            final int rotl9 = Salsa20Engine.rotl(rotl5 ^ n10, 16);
            final int n11 = n6 + rotl9;
            final int rotl10 = Salsa20Engine.rotl(rotl ^ n11, 12);
            final int n12 = n10 + rotl10;
            final int rotl11 = Salsa20Engine.rotl(rotl9 ^ n12, 8);
            final int n13 = n11 + rotl11;
            final int rotl12 = Salsa20Engine.rotl(rotl10 ^ n13, 7);
            final int n14 = n3 + rotl2;
            final int rotl13 = Salsa20Engine.rotl(rotl6 ^ n14, 16);
            final int n15 = n7 + rotl13;
            final int rotl14 = Salsa20Engine.rotl(rotl2 ^ n15, 12);
            final int n16 = n14 + rotl14;
            final int rotl15 = Salsa20Engine.rotl(rotl13 ^ n16, 8);
            final int n17 = n15 + rotl15;
            final int rotl16 = Salsa20Engine.rotl(rotl14 ^ n17, 7);
            final int n18 = n4 + rotl3;
            final int rotl17 = Salsa20Engine.rotl(rotl7 ^ n18, 16);
            final int n19 = n8 + rotl17;
            final int rotl18 = Salsa20Engine.rotl(rotl3 ^ n19, 12);
            final int n20 = n18 + rotl18;
            final int rotl19 = Salsa20Engine.rotl(rotl17 ^ n20, 8);
            final int n21 = n19 + rotl19;
            final int rotl20 = Salsa20Engine.rotl(rotl18 ^ n21, 7);
            final int n22 = n5 + rotl4;
            final int rotl21 = Salsa20Engine.rotl(rotl8 ^ n22, 16);
            final int n23 = n9 + rotl21;
            final int rotl22 = Salsa20Engine.rotl(rotl4 ^ n23, 12);
            final int n24 = n22 + rotl22;
            final int rotl23 = Salsa20Engine.rotl(rotl21 ^ n24, 8);
            final int n25 = n23 + rotl23;
            final int rotl24 = Salsa20Engine.rotl(rotl22 ^ n25, 7);
            final int n26 = n12 + rotl16;
            final int rotl25 = Salsa20Engine.rotl(rotl23 ^ n26, 16);
            final int n27 = n21 + rotl25;
            final int rotl26 = Salsa20Engine.rotl(rotl16 ^ n27, 12);
            n2 = n26 + rotl26;
            rotl8 = Salsa20Engine.rotl(rotl25 ^ n2, 8);
            n8 = n27 + rotl8;
            rotl2 = Salsa20Engine.rotl(rotl26 ^ n8, 7);
            final int n28 = n16 + rotl20;
            final int rotl27 = Salsa20Engine.rotl(rotl11 ^ n28, 16);
            final int n29 = n25 + rotl27;
            final int rotl28 = Salsa20Engine.rotl(rotl20 ^ n29, 12);
            n3 = n28 + rotl28;
            rotl5 = Salsa20Engine.rotl(rotl27 ^ n3, 8);
            n9 = n29 + rotl5;
            rotl3 = Salsa20Engine.rotl(rotl28 ^ n9, 7);
            final int n30 = n20 + rotl24;
            final int rotl29 = Salsa20Engine.rotl(rotl15 ^ n30, 16);
            final int n31 = n13 + rotl29;
            final int rotl30 = Salsa20Engine.rotl(rotl24 ^ n31, 12);
            n4 = n30 + rotl30;
            rotl6 = Salsa20Engine.rotl(rotl29 ^ n4, 8);
            n6 = n31 + rotl6;
            rotl4 = Salsa20Engine.rotl(rotl30 ^ n6, 7);
            final int n32 = n24 + rotl12;
            final int rotl31 = Salsa20Engine.rotl(rotl19 ^ n32, 16);
            final int n33 = n17 + rotl31;
            final int rotl32 = Salsa20Engine.rotl(rotl12 ^ n33, 12);
            n5 = n32 + rotl32;
            rotl7 = Salsa20Engine.rotl(rotl31 ^ n5, 8);
            n7 = n33 + rotl7;
            rotl = Salsa20Engine.rotl(rotl32 ^ n7, 7);
        }
        array2[0] = n2 + array[0];
        array2[1] = n3 + array[1];
        array2[2] = n4 + array[2];
        array2[3] = n5 + array[3];
        array2[4] = rotl + array[4];
        array2[5] = rotl2 + array[5];
        array2[6] = rotl3 + array[6];
        array2[7] = rotl4 + array[7];
        array2[8] = n6 + array[8];
        array2[9] = n7 + array[9];
        array2[10] = n8 + array[10];
        array2[11] = n9 + array[11];
        array2[12] = rotl5 + array[12];
        array2[13] = rotl6 + array[13];
        array2[14] = rotl7 + array[14];
        array2[15] = rotl8 + array[15];
    }
}
