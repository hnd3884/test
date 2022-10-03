package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class DESEngine implements BlockCipher
{
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey;
    private static final short[] bytebit;
    private static final int[] bigbyte;
    private static final byte[] pc1;
    private static final byte[] totrot;
    private static final byte[] pc2;
    private static final int[] SP1;
    private static final int[] SP2;
    private static final int[] SP3;
    private static final int[] SP4;
    private static final int[] SP5;
    private static final int[] SP6;
    private static final int[] SP7;
    private static final int[] SP8;
    
    public DESEngine() {
        this.workingKey = null;
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DES init - " + cipherParameters.getClass().getName());
        }
        if (((KeyParameter)cipherParameters).getKey().length > 8) {
            throw new IllegalArgumentException("DES key too long - should be 8 bytes");
        }
        this.workingKey = this.generateWorkingKey(b, ((KeyParameter)cipherParameters).getKey());
    }
    
    public String getAlgorithmName() {
        return "DES";
    }
    
    public int getBlockSize() {
        return 8;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (this.workingKey == null) {
            throw new IllegalStateException("DES engine not initialised");
        }
        if (n + 8 > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > array2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        this.desFunc(this.workingKey, array, n, array2, n2);
        return 8;
    }
    
    public void reset() {
    }
    
    protected int[] generateWorkingKey(final boolean b, final byte[] array) {
        final int[] array2 = new int[32];
        final boolean[] array3 = new boolean[56];
        final boolean[] array4 = new boolean[56];
        for (int i = 0; i < 56; ++i) {
            final byte b2 = DESEngine.pc1[i];
            array3[i] = ((array[b2 >>> 3] & DESEngine.bytebit[b2 & 0x7]) != 0x0);
        }
        for (int j = 0; j < 16; ++j) {
            int n;
            if (b) {
                n = j << 1;
            }
            else {
                n = 15 - j << 1;
            }
            final int n2 = n + 1;
            array2[n] = (array2[n2] = 0);
            for (byte b3 = 0; b3 < 28; ++b3) {
                final int n3 = b3 + DESEngine.totrot[j];
                if (n3 < 28) {
                    array4[b3] = array3[n3];
                }
                else {
                    array4[b3] = array3[n3 - 28];
                }
            }
            for (byte b4 = 28; b4 < 56; ++b4) {
                final int n4 = b4 + DESEngine.totrot[j];
                if (n4 < 56) {
                    array4[b4] = array3[n4];
                }
                else {
                    array4[b4] = array3[n4 - 28];
                }
            }
            for (int k = 0; k < 24; ++k) {
                if (array4[DESEngine.pc2[k]]) {
                    final int[] array5 = array2;
                    final int n5 = n;
                    array5[n5] |= DESEngine.bigbyte[k];
                }
                if (array4[DESEngine.pc2[k + 24]]) {
                    final int[] array6 = array2;
                    final int n6 = n2;
                    array6[n6] |= DESEngine.bigbyte[k];
                }
            }
        }
        for (int l = 0; l != 32; l += 2) {
            final int n7 = array2[l];
            final int n8 = array2[l + 1];
            array2[l] = ((n7 & 0xFC0000) << 6 | (n7 & 0xFC0) << 10 | (n8 & 0xFC0000) >>> 10 | (n8 & 0xFC0) >>> 6);
            array2[l + 1] = ((n7 & 0x3F000) << 12 | (n7 & 0x3F) << 16 | (n8 & 0x3F000) >>> 4 | (n8 & 0x3F));
        }
        return array2;
    }
    
    protected void desFunc(final int[] array, final byte[] array2, final int n, final byte[] array3, final int n2) {
        final int n3 = (array2[n + 0] & 0xFF) << 24 | (array2[n + 1] & 0xFF) << 16 | (array2[n + 2] & 0xFF) << 8 | (array2[n + 3] & 0xFF);
        final int n4 = (array2[n + 4] & 0xFF) << 24 | (array2[n + 5] & 0xFF) << 16 | (array2[n + 6] & 0xFF) << 8 | (array2[n + 7] & 0xFF);
        final int n5 = (n3 >>> 4 ^ n4) & 0xF0F0F0F;
        final int n6 = n4 ^ n5;
        final int n7 = n3 ^ n5 << 4;
        final int n8 = (n7 >>> 16 ^ n6) & 0xFFFF;
        final int n9 = n6 ^ n8;
        final int n10 = n7 ^ n8 << 16;
        final int n11 = (n9 >>> 2 ^ n10) & 0x33333333;
        final int n12 = n10 ^ n11;
        final int n13 = n9 ^ n11 << 2;
        final int n14 = (n13 >>> 8 ^ n12) & 0xFF00FF;
        final int n15 = n12 ^ n14;
        final int n16 = n13 ^ n14 << 8;
        final int n17 = (n16 << 1 | (n16 >>> 31 & 0x1)) & -1;
        final int n18 = (n15 ^ n17) & 0xAAAAAAAA;
        final int n19 = n15 ^ n18;
        int n20 = n17 ^ n18;
        int n21 = (n19 << 1 | (n19 >>> 31 & 0x1)) & -1;
        for (int i = 0; i < 8; ++i) {
            final int n22 = (n20 << 28 | n20 >>> 4) ^ array[i * 4 + 0];
            final int n23 = DESEngine.SP7[n22 & 0x3F] | DESEngine.SP5[n22 >>> 8 & 0x3F] | DESEngine.SP3[n22 >>> 16 & 0x3F] | DESEngine.SP1[n22 >>> 24 & 0x3F];
            final int n24 = n20 ^ array[i * 4 + 1];
            n21 ^= (n23 | DESEngine.SP8[n24 & 0x3F] | DESEngine.SP6[n24 >>> 8 & 0x3F] | DESEngine.SP4[n24 >>> 16 & 0x3F] | DESEngine.SP2[n24 >>> 24 & 0x3F]);
            final int n25 = (n21 << 28 | n21 >>> 4) ^ array[i * 4 + 2];
            final int n26 = DESEngine.SP7[n25 & 0x3F] | DESEngine.SP5[n25 >>> 8 & 0x3F] | DESEngine.SP3[n25 >>> 16 & 0x3F] | DESEngine.SP1[n25 >>> 24 & 0x3F];
            final int n27 = n21 ^ array[i * 4 + 3];
            n20 ^= (n26 | DESEngine.SP8[n27 & 0x3F] | DESEngine.SP6[n27 >>> 8 & 0x3F] | DESEngine.SP4[n27 >>> 16 & 0x3F] | DESEngine.SP2[n27 >>> 24 & 0x3F]);
        }
        final int n28 = n20 << 31 | n20 >>> 1;
        final int n29 = (n21 ^ n28) & 0xAAAAAAAA;
        final int n30 = n21 ^ n29;
        final int n31 = n28 ^ n29;
        final int n32 = n30 << 31 | n30 >>> 1;
        final int n33 = (n32 >>> 8 ^ n31) & 0xFF00FF;
        final int n34 = n31 ^ n33;
        final int n35 = n32 ^ n33 << 8;
        final int n36 = (n35 >>> 2 ^ n34) & 0x33333333;
        final int n37 = n34 ^ n36;
        final int n38 = n35 ^ n36 << 2;
        final int n39 = (n37 >>> 16 ^ n38) & 0xFFFF;
        final int n40 = n38 ^ n39;
        final int n41 = n37 ^ n39 << 16;
        final int n42 = (n41 >>> 4 ^ n40) & 0xF0F0F0F;
        final int n43 = n40 ^ n42;
        final int n44 = n41 ^ n42 << 4;
        array3[n2 + 0] = (byte)(n44 >>> 24 & 0xFF);
        array3[n2 + 1] = (byte)(n44 >>> 16 & 0xFF);
        array3[n2 + 2] = (byte)(n44 >>> 8 & 0xFF);
        array3[n2 + 3] = (byte)(n44 & 0xFF);
        array3[n2 + 4] = (byte)(n43 >>> 24 & 0xFF);
        array3[n2 + 5] = (byte)(n43 >>> 16 & 0xFF);
        array3[n2 + 6] = (byte)(n43 >>> 8 & 0xFF);
        array3[n2 + 7] = (byte)(n43 & 0xFF);
    }
    
    static {
        bytebit = new short[] { 128, 64, 32, 16, 8, 4, 2, 1 };
        bigbyte = new int[] { 8388608, 4194304, 2097152, 1048576, 524288, 262144, 131072, 65536, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8, 4, 2, 1 };
        pc1 = new byte[] { 56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3 };
        totrot = new byte[] { 1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28 };
        pc2 = new byte[] { 13, 16, 10, 23, 0, 4, 2, 27, 14, 5, 20, 9, 22, 18, 11, 3, 25, 7, 15, 6, 26, 19, 12, 1, 40, 51, 30, 36, 46, 54, 29, 39, 50, 44, 32, 47, 43, 48, 38, 55, 33, 52, 45, 41, 49, 35, 28, 31 };
        SP1 = new int[] { 16843776, 0, 65536, 16843780, 16842756, 66564, 4, 65536, 1024, 16843776, 16843780, 1024, 16778244, 16842756, 16777216, 4, 1028, 16778240, 16778240, 66560, 66560, 16842752, 16842752, 16778244, 65540, 16777220, 16777220, 65540, 0, 1028, 66564, 16777216, 65536, 16843780, 4, 16842752, 16843776, 16777216, 16777216, 1024, 16842756, 65536, 66560, 16777220, 1024, 4, 16778244, 66564, 16843780, 65540, 16842752, 16778244, 16777220, 1028, 66564, 16843776, 1028, 16778240, 16778240, 0, 65540, 66560, 0, 16842756 };
        SP2 = new int[] { -2146402272, -2147450880, 32768, 1081376, 1048576, 32, -2146435040, -2147450848, -2147483616, -2146402272, -2146402304, Integer.MIN_VALUE, -2147450880, 1048576, 32, -2146435040, 1081344, 1048608, -2147450848, 0, Integer.MIN_VALUE, 32768, 1081376, -2146435072, 1048608, -2147483616, 0, 1081344, 32800, -2146402304, -2146435072, 32800, 0, 1081376, -2146435040, 1048576, -2147450848, -2146435072, -2146402304, 32768, -2146435072, -2147450880, 32, -2146402272, 1081376, 32, 32768, Integer.MIN_VALUE, 32800, -2146402304, 1048576, -2147483616, 1048608, -2147450848, -2147483616, 1048608, 1081344, 0, -2147450880, 32800, Integer.MIN_VALUE, -2146435040, -2146402272, 1081344 };
        SP3 = new int[] { 520, 134349312, 0, 134348808, 134218240, 0, 131592, 134218240, 131080, 134217736, 134217736, 131072, 134349320, 131080, 134348800, 520, 134217728, 8, 134349312, 512, 131584, 134348800, 134348808, 131592, 134218248, 131584, 131072, 134218248, 8, 134349320, 512, 134217728, 134349312, 134217728, 131080, 520, 131072, 134349312, 134218240, 0, 512, 131080, 134349320, 134218240, 134217736, 512, 0, 134348808, 134218248, 131072, 134217728, 134349320, 8, 131592, 131584, 134217736, 134348800, 134218248, 520, 134348800, 131592, 8, 134348808, 131584 };
        SP4 = new int[] { 8396801, 8321, 8321, 128, 8396928, 8388737, 8388609, 8193, 0, 8396800, 8396800, 8396929, 129, 0, 8388736, 8388609, 1, 8192, 8388608, 8396801, 128, 8388608, 8193, 8320, 8388737, 1, 8320, 8388736, 8192, 8396928, 8396929, 129, 8388736, 8388609, 8396800, 8396929, 129, 0, 0, 8396800, 8320, 8388736, 8388737, 1, 8396801, 8321, 8321, 128, 8396929, 129, 1, 8192, 8388609, 8193, 8396928, 8388737, 8193, 8320, 8388608, 8396801, 128, 8388608, 8192, 8396928 };
        SP5 = new int[] { 256, 34078976, 34078720, 1107296512, 524288, 256, 1073741824, 34078720, 1074266368, 524288, 33554688, 1074266368, 1107296512, 1107820544, 524544, 1073741824, 33554432, 1074266112, 1074266112, 0, 1073742080, 1107820800, 1107820800, 33554688, 1107820544, 1073742080, 0, 1107296256, 34078976, 33554432, 1107296256, 524544, 524288, 1107296512, 256, 33554432, 1073741824, 34078720, 1107296512, 1074266368, 33554688, 1073741824, 1107820544, 34078976, 1074266368, 256, 33554432, 1107820544, 1107820800, 524544, 1107296256, 1107820800, 34078720, 0, 1074266112, 1107296256, 524544, 33554688, 1073742080, 524288, 0, 1074266112, 34078976, 1073742080 };
        SP6 = new int[] { 536870928, 541065216, 16384, 541081616, 541065216, 16, 541081616, 4194304, 536887296, 4210704, 4194304, 536870928, 4194320, 536887296, 536870912, 16400, 0, 4194320, 536887312, 16384, 4210688, 536887312, 16, 541065232, 541065232, 0, 4210704, 541081600, 16400, 4210688, 541081600, 536870912, 536887296, 16, 541065232, 4210688, 541081616, 4194304, 16400, 536870928, 4194304, 536887296, 536870912, 16400, 536870928, 541081616, 4210688, 541065216, 4210704, 541081600, 0, 541065232, 16, 16384, 541065216, 4210704, 16384, 4194320, 536887312, 0, 541081600, 536870912, 4194320, 536887312 };
        SP7 = new int[] { 2097152, 69206018, 67110914, 0, 2048, 67110914, 2099202, 69208064, 69208066, 2097152, 0, 67108866, 2, 67108864, 69206018, 2050, 67110912, 2099202, 2097154, 67110912, 67108866, 69206016, 69208064, 2097154, 69206016, 2048, 2050, 69208066, 2099200, 2, 67108864, 2099200, 67108864, 2099200, 2097152, 67110914, 67110914, 69206018, 69206018, 2, 2097154, 67108864, 67110912, 2097152, 69208064, 2050, 2099202, 69208064, 2050, 67108866, 69208066, 69206016, 2099200, 0, 2, 69208066, 0, 2099202, 69206016, 2048, 67108866, 67110912, 2048, 2097154 };
        SP8 = new int[] { 268439616, 4096, 262144, 268701760, 268435456, 268439616, 64, 268435456, 262208, 268697600, 268701760, 266240, 268701696, 266304, 4096, 64, 268697600, 268435520, 268439552, 4160, 266240, 262208, 268697664, 268701696, 4160, 0, 0, 268697664, 268435520, 268439552, 266304, 262144, 266304, 262144, 268701696, 4096, 64, 268697664, 4096, 266304, 268439552, 64, 268435520, 268697600, 268697664, 268435456, 262144, 268439616, 0, 268701760, 262208, 268435520, 268697600, 268439552, 268439616, 0, 268701760, 266240, 266240, 4160, 4160, 262208, 268435456, 268701696 };
    }
}
