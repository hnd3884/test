package org.bouncycastle.crypto.engines;

public final class CAST6Engine extends CAST5Engine
{
    protected static final int ROUNDS = 12;
    protected static final int BLOCK_SIZE = 16;
    protected int[] _Kr;
    protected int[] _Km;
    protected int[] _Tr;
    protected int[] _Tm;
    private int[] _workingKey;
    
    public CAST6Engine() {
        this._Kr = new int[48];
        this._Km = new int[48];
        this._Tr = new int[192];
        this._Tm = new int[192];
        this._workingKey = new int[8];
    }
    
    @Override
    public String getAlgorithmName() {
        return "CAST6";
    }
    
    @Override
    public void reset() {
    }
    
    @Override
    public int getBlockSize() {
        return 16;
    }
    
    @Override
    protected void setKey(final byte[] array) {
        int n = 1518500249;
        final int n2 = 1859775393;
        int n3 = 19;
        final int n4 = 17;
        for (int i = 0; i < 24; ++i) {
            for (int j = 0; j < 8; ++j) {
                this._Tm[i * 8 + j] = n;
                n += n2;
                this._Tr[i * 8 + j] = n3;
                n3 = (n3 + n4 & 0x1F);
            }
        }
        final byte[] array2 = new byte[64];
        System.arraycopy(array, 0, array2, 0, array.length);
        for (int k = 0; k < 8; ++k) {
            this._workingKey[k] = this.BytesTo32bits(array2, k * 4);
        }
        for (int l = 0; l < 12; ++l) {
            final int n5 = l * 2 * 8;
            final int[] workingKey = this._workingKey;
            final int n6 = 6;
            workingKey[n6] ^= this.F1(this._workingKey[7], this._Tm[n5], this._Tr[n5]);
            final int[] workingKey2 = this._workingKey;
            final int n7 = 5;
            workingKey2[n7] ^= this.F2(this._workingKey[6], this._Tm[n5 + 1], this._Tr[n5 + 1]);
            final int[] workingKey3 = this._workingKey;
            final int n8 = 4;
            workingKey3[n8] ^= this.F3(this._workingKey[5], this._Tm[n5 + 2], this._Tr[n5 + 2]);
            final int[] workingKey4 = this._workingKey;
            final int n9 = 3;
            workingKey4[n9] ^= this.F1(this._workingKey[4], this._Tm[n5 + 3], this._Tr[n5 + 3]);
            final int[] workingKey5 = this._workingKey;
            final int n10 = 2;
            workingKey5[n10] ^= this.F2(this._workingKey[3], this._Tm[n5 + 4], this._Tr[n5 + 4]);
            final int[] workingKey6 = this._workingKey;
            final int n11 = 1;
            workingKey6[n11] ^= this.F3(this._workingKey[2], this._Tm[n5 + 5], this._Tr[n5 + 5]);
            final int[] workingKey7 = this._workingKey;
            final int n12 = 0;
            workingKey7[n12] ^= this.F1(this._workingKey[1], this._Tm[n5 + 6], this._Tr[n5 + 6]);
            final int[] workingKey8 = this._workingKey;
            final int n13 = 7;
            workingKey8[n13] ^= this.F2(this._workingKey[0], this._Tm[n5 + 7], this._Tr[n5 + 7]);
            final int n14 = (l * 2 + 1) * 8;
            final int[] workingKey9 = this._workingKey;
            final int n15 = 6;
            workingKey9[n15] ^= this.F1(this._workingKey[7], this._Tm[n14], this._Tr[n14]);
            final int[] workingKey10 = this._workingKey;
            final int n16 = 5;
            workingKey10[n16] ^= this.F2(this._workingKey[6], this._Tm[n14 + 1], this._Tr[n14 + 1]);
            final int[] workingKey11 = this._workingKey;
            final int n17 = 4;
            workingKey11[n17] ^= this.F3(this._workingKey[5], this._Tm[n14 + 2], this._Tr[n14 + 2]);
            final int[] workingKey12 = this._workingKey;
            final int n18 = 3;
            workingKey12[n18] ^= this.F1(this._workingKey[4], this._Tm[n14 + 3], this._Tr[n14 + 3]);
            final int[] workingKey13 = this._workingKey;
            final int n19 = 2;
            workingKey13[n19] ^= this.F2(this._workingKey[3], this._Tm[n14 + 4], this._Tr[n14 + 4]);
            final int[] workingKey14 = this._workingKey;
            final int n20 = 1;
            workingKey14[n20] ^= this.F3(this._workingKey[2], this._Tm[n14 + 5], this._Tr[n14 + 5]);
            final int[] workingKey15 = this._workingKey;
            final int n21 = 0;
            workingKey15[n21] ^= this.F1(this._workingKey[1], this._Tm[n14 + 6], this._Tr[n14 + 6]);
            final int[] workingKey16 = this._workingKey;
            final int n22 = 7;
            workingKey16[n22] ^= this.F2(this._workingKey[0], this._Tm[n14 + 7], this._Tr[n14 + 7]);
            this._Kr[l * 4] = (this._workingKey[0] & 0x1F);
            this._Kr[l * 4 + 1] = (this._workingKey[2] & 0x1F);
            this._Kr[l * 4 + 2] = (this._workingKey[4] & 0x1F);
            this._Kr[l * 4 + 3] = (this._workingKey[6] & 0x1F);
            this._Km[l * 4] = this._workingKey[7];
            this._Km[l * 4 + 1] = this._workingKey[5];
            this._Km[l * 4 + 2] = this._workingKey[3];
            this._Km[l * 4 + 3] = this._workingKey[1];
        }
    }
    
    @Override
    protected int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int[] array3 = new int[4];
        this.CAST_Encipher(this.BytesTo32bits(array, n), this.BytesTo32bits(array, n + 4), this.BytesTo32bits(array, n + 8), this.BytesTo32bits(array, n + 12), array3);
        this.Bits32ToBytes(array3[0], array2, n2);
        this.Bits32ToBytes(array3[1], array2, n2 + 4);
        this.Bits32ToBytes(array3[2], array2, n2 + 8);
        this.Bits32ToBytes(array3[3], array2, n2 + 12);
        return 16;
    }
    
    @Override
    protected int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        final int[] array3 = new int[4];
        this.CAST_Decipher(this.BytesTo32bits(array, n), this.BytesTo32bits(array, n + 4), this.BytesTo32bits(array, n + 8), this.BytesTo32bits(array, n + 12), array3);
        this.Bits32ToBytes(array3[0], array2, n2);
        this.Bits32ToBytes(array3[1], array2, n2 + 4);
        this.Bits32ToBytes(array3[2], array2, n2 + 8);
        this.Bits32ToBytes(array3[3], array2, n2 + 12);
        return 16;
    }
    
    protected final void CAST_Encipher(int n, int n2, int n3, int n4, final int[] array) {
        for (int i = 0; i < 6; ++i) {
            final int n5 = i * 4;
            n3 ^= this.F1(n4, this._Km[n5], this._Kr[n5]);
            n2 ^= this.F2(n3, this._Km[n5 + 1], this._Kr[n5 + 1]);
            n ^= this.F3(n2, this._Km[n5 + 2], this._Kr[n5 + 2]);
            n4 ^= this.F1(n, this._Km[n5 + 3], this._Kr[n5 + 3]);
        }
        for (int j = 6; j < 12; ++j) {
            final int n6 = j * 4;
            n4 ^= this.F1(n, this._Km[n6 + 3], this._Kr[n6 + 3]);
            n ^= this.F3(n2, this._Km[n6 + 2], this._Kr[n6 + 2]);
            n2 ^= this.F2(n3, this._Km[n6 + 1], this._Kr[n6 + 1]);
            n3 ^= this.F1(n4, this._Km[n6], this._Kr[n6]);
        }
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
    }
    
    protected final void CAST_Decipher(int n, int n2, int n3, int n4, final int[] array) {
        for (int i = 0; i < 6; ++i) {
            final int n5 = (11 - i) * 4;
            n3 ^= this.F1(n4, this._Km[n5], this._Kr[n5]);
            n2 ^= this.F2(n3, this._Km[n5 + 1], this._Kr[n5 + 1]);
            n ^= this.F3(n2, this._Km[n5 + 2], this._Kr[n5 + 2]);
            n4 ^= this.F1(n, this._Km[n5 + 3], this._Kr[n5 + 3]);
        }
        for (int j = 6; j < 12; ++j) {
            final int n6 = (11 - j) * 4;
            n4 ^= this.F1(n, this._Km[n6 + 3], this._Kr[n6 + 3]);
            n ^= this.F3(n2, this._Km[n6 + 2], this._Kr[n6 + 2]);
            n2 ^= this.F2(n3, this._Km[n6 + 1], this._Kr[n6 + 1]);
            n3 ^= this.F1(n4, this._Km[n6], this._Kr[n6]);
        }
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        array[3] = n4;
    }
}
