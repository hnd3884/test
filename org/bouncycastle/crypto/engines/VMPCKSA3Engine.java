package org.bouncycastle.crypto.engines;

public class VMPCKSA3Engine extends VMPCEngine
{
    @Override
    public String getAlgorithmName() {
        return "VMPC-KSA3";
    }
    
    @Override
    protected void initKey(final byte[] array, final byte[] array2) {
        this.s = 0;
        this.P = new byte[256];
        for (int i = 0; i < 256; ++i) {
            this.P[i] = (byte)i;
        }
        for (int j = 0; j < 768; ++j) {
            this.s = this.P[this.s + this.P[j & 0xFF] + array[j % array.length] & 0xFF];
            final byte b = this.P[j & 0xFF];
            this.P[j & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b;
        }
        for (int k = 0; k < 768; ++k) {
            this.s = this.P[this.s + this.P[k & 0xFF] + array2[k % array2.length] & 0xFF];
            final byte b2 = this.P[k & 0xFF];
            this.P[k & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b2;
        }
        for (int l = 0; l < 768; ++l) {
            this.s = this.P[this.s + this.P[l & 0xFF] + array[l % array.length] & 0xFF];
            final byte b3 = this.P[l & 0xFF];
            this.P[l & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = b3;
        }
        this.n = 0;
    }
}
