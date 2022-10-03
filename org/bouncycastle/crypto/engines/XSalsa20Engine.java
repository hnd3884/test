package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class XSalsa20Engine extends Salsa20Engine
{
    @Override
    public String getAlgorithmName() {
        return "XSalsa20";
    }
    
    @Override
    protected int getNonceSize() {
        return 24;
    }
    
    @Override
    protected void setKey(final byte[] array, final byte[] array2) {
        if (array == null) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " doesn't support re-init with null key");
        }
        if (array.length != 32) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires a 256 bit key");
        }
        super.setKey(array, array2);
        Pack.littleEndianToInt(array2, 8, this.engineState, 8, 2);
        final int[] array3 = new int[this.engineState.length];
        Salsa20Engine.salsaCore(20, this.engineState, array3);
        this.engineState[1] = array3[0] - this.engineState[0];
        this.engineState[2] = array3[5] - this.engineState[5];
        this.engineState[3] = array3[10] - this.engineState[10];
        this.engineState[4] = array3[15] - this.engineState[15];
        this.engineState[11] = array3[6] - this.engineState[6];
        this.engineState[12] = array3[7] - this.engineState[7];
        this.engineState[13] = array3[8] - this.engineState[8];
        this.engineState[14] = array3[9] - this.engineState[9];
        Pack.littleEndianToInt(array2, 16, this.engineState, 6, 2);
    }
}
