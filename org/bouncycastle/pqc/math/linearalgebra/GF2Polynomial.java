package org.bouncycastle.pqc.math.linearalgebra;

import java.math.BigInteger;
import java.util.Random;

public class GF2Polynomial
{
    private int len;
    private int blocks;
    private int[] value;
    private static Random rand;
    private static final boolean[] parity;
    private static final short[] squaringTable;
    private static final int[] bitMask;
    private static final int[] reverseRightMask;
    
    public GF2Polynomial(final int n) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
    }
    
    public GF2Polynomial(final int n, final Random random) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
        this.randomize(random);
    }
    
    public GF2Polynomial(final int n, final String s) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
        if (s.equalsIgnoreCase("ZERO")) {
            this.assignZero();
        }
        else if (s.equalsIgnoreCase("ONE")) {
            this.assignOne();
        }
        else if (s.equalsIgnoreCase("RANDOM")) {
            this.randomize();
        }
        else if (s.equalsIgnoreCase("X")) {
            this.assignX();
        }
        else {
            if (!s.equalsIgnoreCase("ALL")) {
                throw new IllegalArgumentException("Error: GF2Polynomial was called using " + s + " as value!");
            }
            this.assignAll();
        }
    }
    
    public GF2Polynomial(final int n, final int[] array) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
        System.arraycopy(array, 0, this.value, 0, Math.min(this.blocks, array.length));
        this.zeroUnusedBits();
    }
    
    public GF2Polynomial(final int n, final byte[] array) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
        final int min = Math.min((array.length - 1 >> 2) + 1, this.blocks);
        for (int i = 0; i < min - 1; ++i) {
            final int n2 = array.length - (i << 2) - 1;
            this.value[i] = (array[n2] & 0xFF);
            final int[] value = this.value;
            final int n3 = i;
            value[n3] |= (array[n2 - 1] << 8 & 0xFF00);
            final int[] value2 = this.value;
            final int n4 = i;
            value2[n4] |= (array[n2 - 2] << 16 & 0xFF0000);
            final int[] value3 = this.value;
            final int n5 = i;
            value3[n5] |= (array[n2 - 3] << 24 & 0xFF000000);
        }
        final int n6 = min - 1;
        final int n7 = array.length - (n6 << 2) - 1;
        this.value[n6] = (array[n7] & 0xFF);
        if (n7 > 0) {
            final int[] value4 = this.value;
            final int n8 = n6;
            value4[n8] |= (array[n7 - 1] << 8 & 0xFF00);
        }
        if (n7 > 1) {
            final int[] value5 = this.value;
            final int n9 = n6;
            value5[n9] |= (array[n7 - 2] << 16 & 0xFF0000);
        }
        if (n7 > 2) {
            final int[] value6 = this.value;
            final int n10 = n6;
            value6[n10] |= (array[n7 - 3] << 24 & 0xFF000000);
        }
        this.zeroUnusedBits();
        this.reduceN();
    }
    
    public GF2Polynomial(final int n, final BigInteger bigInteger) {
        int len = n;
        if (len < 1) {
            len = 1;
        }
        this.blocks = (len - 1 >> 5) + 1;
        this.value = new int[this.blocks];
        this.len = len;
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray[0] == 0) {
            final byte[] array = new byte[byteArray.length - 1];
            System.arraycopy(byteArray, 1, array, 0, array.length);
            byteArray = array;
        }
        final int n2 = byteArray.length & 0x3;
        final int n3 = (byteArray.length - 1 >> 2) + 1;
        for (int i = 0; i < n2; ++i) {
            final int[] value = this.value;
            final int n4 = n3 - 1;
            value[n4] |= (byteArray[i] & 0xFF) << (n2 - 1 - i << 3);
        }
        for (int j = 0; j <= byteArray.length - 4 >> 2; ++j) {
            final int n5 = byteArray.length - 1 - (j << 2);
            this.value[j] = (byteArray[n5] & 0xFF);
            final int[] value2 = this.value;
            final int n6 = j;
            value2[n6] |= (byteArray[n5 - 1] << 8 & 0xFF00);
            final int[] value3 = this.value;
            final int n7 = j;
            value3[n7] |= (byteArray[n5 - 2] << 16 & 0xFF0000);
            final int[] value4 = this.value;
            final int n8 = j;
            value4[n8] |= (byteArray[n5 - 3] << 24 & 0xFF000000);
        }
        if ((this.len & 0x1F) != 0x0) {
            final int[] value5 = this.value;
            final int n9 = this.blocks - 1;
            value5[n9] &= GF2Polynomial.reverseRightMask[this.len & 0x1F];
        }
        this.reduceN();
    }
    
    public GF2Polynomial(final GF2Polynomial gf2Polynomial) {
        this.len = gf2Polynomial.len;
        this.blocks = gf2Polynomial.blocks;
        this.value = IntUtils.clone(gf2Polynomial.value);
    }
    
    public Object clone() {
        return new GF2Polynomial(this);
    }
    
    public int getLength() {
        return this.len;
    }
    
    public int[] toIntegerArray() {
        final int[] array = new int[this.blocks];
        System.arraycopy(this.value, 0, array, 0, this.blocks);
        return array;
    }
    
    public String toString(final int n) {
        final char[] array = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        final String[] array2 = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" };
        String s = new String();
        if (n == 16) {
            for (int i = this.blocks - 1; i >= 0; --i) {
                s = s + array[this.value[i] >>> 28 & 0xF] + array[this.value[i] >>> 24 & 0xF] + array[this.value[i] >>> 20 & 0xF] + array[this.value[i] >>> 16 & 0xF] + array[this.value[i] >>> 12 & 0xF] + array[this.value[i] >>> 8 & 0xF] + array[this.value[i] >>> 4 & 0xF] + array[this.value[i] & 0xF] + " ";
            }
        }
        else {
            for (int j = this.blocks - 1; j >= 0; --j) {
                s = s + array2[this.value[j] >>> 28 & 0xF] + array2[this.value[j] >>> 24 & 0xF] + array2[this.value[j] >>> 20 & 0xF] + array2[this.value[j] >>> 16 & 0xF] + array2[this.value[j] >>> 12 & 0xF] + array2[this.value[j] >>> 8 & 0xF] + array2[this.value[j] >>> 4 & 0xF] + array2[this.value[j] & 0xF] + " ";
            }
        }
        return s;
    }
    
    public byte[] toByteArray() {
        final int n = (this.len - 1 >> 3) + 1;
        final int n2 = n & 0x3;
        final byte[] array = new byte[n];
        for (int i = 0; i < n >> 2; ++i) {
            final int n3 = n - (i << 2) - 1;
            array[n3] = (byte)(this.value[i] & 0xFF);
            array[n3 - 1] = (byte)((this.value[i] & 0xFF00) >>> 8);
            array[n3 - 2] = (byte)((this.value[i] & 0xFF0000) >>> 16);
            array[n3 - 3] = (byte)((this.value[i] & 0xFF000000) >>> 24);
        }
        for (int j = 0; j < n2; ++j) {
            final int n4 = n2 - j - 1 << 3;
            array[j] = (byte)((this.value[this.blocks - 1] & 255 << n4) >>> n4);
        }
        return array;
    }
    
    public BigInteger toFlexiBigInt() {
        if (this.len == 0 || this.isZero()) {
            return new BigInteger(0, new byte[0]);
        }
        return new BigInteger(1, this.toByteArray());
    }
    
    public void assignOne() {
        for (int i = 1; i < this.blocks; ++i) {
            this.value[i] = 0;
        }
        this.value[0] = 1;
    }
    
    public void assignX() {
        for (int i = 1; i < this.blocks; ++i) {
            this.value[i] = 0;
        }
        this.value[0] = 2;
    }
    
    public void assignAll() {
        for (int i = 0; i < this.blocks; ++i) {
            this.value[i] = -1;
        }
        this.zeroUnusedBits();
    }
    
    public void assignZero() {
        for (int i = 0; i < this.blocks; ++i) {
            this.value[i] = 0;
        }
    }
    
    public void randomize() {
        for (int i = 0; i < this.blocks; ++i) {
            this.value[i] = GF2Polynomial.rand.nextInt();
        }
        this.zeroUnusedBits();
    }
    
    public void randomize(final Random random) {
        for (int i = 0; i < this.blocks; ++i) {
            this.value[i] = random.nextInt();
        }
        this.zeroUnusedBits();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null || !(o instanceof GF2Polynomial)) {
            return false;
        }
        final GF2Polynomial gf2Polynomial = (GF2Polynomial)o;
        if (this.len != gf2Polynomial.len) {
            return false;
        }
        for (int i = 0; i < this.blocks; ++i) {
            if (this.value[i] != gf2Polynomial.value[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        return this.len + this.value.hashCode();
    }
    
    public boolean isZero() {
        if (this.len == 0) {
            return true;
        }
        for (int i = 0; i < this.blocks; ++i) {
            if (this.value[i] != 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isOne() {
        for (int i = 1; i < this.blocks; ++i) {
            if (this.value[i] != 0) {
                return false;
            }
        }
        return this.value[0] == 1;
    }
    
    public void addToThis(final GF2Polynomial gf2Polynomial) {
        this.expandN(gf2Polynomial.len);
        this.xorThisBy(gf2Polynomial);
    }
    
    public GF2Polynomial add(final GF2Polynomial gf2Polynomial) {
        return this.xor(gf2Polynomial);
    }
    
    public void subtractFromThis(final GF2Polynomial gf2Polynomial) {
        this.expandN(gf2Polynomial.len);
        this.xorThisBy(gf2Polynomial);
    }
    
    public GF2Polynomial subtract(final GF2Polynomial gf2Polynomial) {
        return this.xor(gf2Polynomial);
    }
    
    public void increaseThis() {
        this.xorBit(0);
    }
    
    public GF2Polynomial increase() {
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this);
        gf2Polynomial.increaseThis();
        return gf2Polynomial;
    }
    
    public GF2Polynomial multiplyClassic(final GF2Polynomial gf2Polynomial) {
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(Math.max(this.len, gf2Polynomial.len) << 1);
        final GF2Polynomial[] array = new GF2Polynomial[32];
        array[0] = new GF2Polynomial(this);
        for (int i = 1; i <= 31; ++i) {
            array[i] = array[i - 1].shiftLeft();
        }
        for (int j = 0; j < gf2Polynomial.blocks; ++j) {
            for (int k = 0; k <= 31; ++k) {
                if ((gf2Polynomial.value[j] & GF2Polynomial.bitMask[k]) != 0x0) {
                    gf2Polynomial2.xorThisBy(array[k]);
                }
            }
            for (int l = 0; l <= 31; ++l) {
                array[l].shiftBlocksLeft();
            }
        }
        return gf2Polynomial2;
    }
    
    public GF2Polynomial multiply(final GF2Polynomial gf2Polynomial) {
        final int max = Math.max(this.len, gf2Polynomial.len);
        this.expandN(max);
        gf2Polynomial.expandN(max);
        return this.karaMult(gf2Polynomial);
    }
    
    private GF2Polynomial karaMult(final GF2Polynomial gf2Polynomial) {
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.len << 1);
        if (this.len <= 32) {
            gf2Polynomial2.value = mult32(this.value[0], gf2Polynomial.value[0]);
            return gf2Polynomial2;
        }
        if (this.len <= 64) {
            gf2Polynomial2.value = mult64(this.value, gf2Polynomial.value);
            return gf2Polynomial2;
        }
        if (this.len <= 128) {
            gf2Polynomial2.value = mult128(this.value, gf2Polynomial.value);
            return gf2Polynomial2;
        }
        if (this.len <= 256) {
            gf2Polynomial2.value = mult256(this.value, gf2Polynomial.value);
            return gf2Polynomial2;
        }
        if (this.len <= 512) {
            gf2Polynomial2.value = mult512(this.value, gf2Polynomial.value);
            return gf2Polynomial2;
        }
        final int n = GF2Polynomial.bitMask[IntegerFunctions.floorLog(this.len - 1)];
        final GF2Polynomial lower = this.lower((n - 1 >> 5) + 1);
        final GF2Polynomial upper = this.upper((n - 1 >> 5) + 1);
        final GF2Polynomial lower2 = gf2Polynomial.lower((n - 1 >> 5) + 1);
        final GF2Polynomial upper2 = gf2Polynomial.upper((n - 1 >> 5) + 1);
        final GF2Polynomial karaMult = upper.karaMult(upper2);
        final GF2Polynomial karaMult2 = lower.karaMult(lower2);
        lower.addToThis(upper);
        lower2.addToThis(upper2);
        final GF2Polynomial karaMult3 = lower.karaMult(lower2);
        gf2Polynomial2.shiftLeftAddThis(karaMult, n << 1);
        gf2Polynomial2.shiftLeftAddThis(karaMult, n);
        gf2Polynomial2.shiftLeftAddThis(karaMult3, n);
        gf2Polynomial2.shiftLeftAddThis(karaMult2, n);
        gf2Polynomial2.addToThis(karaMult2);
        return gf2Polynomial2;
    }
    
    private static int[] mult512(final int[] array, final int[] array2) {
        final int[] array3 = new int[32];
        final int[] array4 = new int[8];
        System.arraycopy(array, 0, array4, 0, Math.min(8, array.length));
        final int[] array5 = new int[8];
        if (array.length > 8) {
            System.arraycopy(array, 8, array5, 0, Math.min(8, array.length - 8));
        }
        final int[] array6 = new int[8];
        System.arraycopy(array2, 0, array6, 0, Math.min(8, array2.length));
        final int[] array7 = new int[8];
        if (array2.length > 8) {
            System.arraycopy(array2, 8, array7, 0, Math.min(8, array2.length - 8));
        }
        final int[] mult256 = mult256(array5, array7);
        final int[] array8 = array3;
        final int n = 31;
        array8[n] ^= mult256[15];
        final int[] array9 = array3;
        final int n2 = 30;
        array9[n2] ^= mult256[14];
        final int[] array10 = array3;
        final int n3 = 29;
        array10[n3] ^= mult256[13];
        final int[] array11 = array3;
        final int n4 = 28;
        array11[n4] ^= mult256[12];
        final int[] array12 = array3;
        final int n5 = 27;
        array12[n5] ^= mult256[11];
        final int[] array13 = array3;
        final int n6 = 26;
        array13[n6] ^= mult256[10];
        final int[] array14 = array3;
        final int n7 = 25;
        array14[n7] ^= mult256[9];
        final int[] array15 = array3;
        final int n8 = 24;
        array15[n8] ^= mult256[8];
        final int[] array16 = array3;
        final int n9 = 23;
        array16[n9] ^= (mult256[7] ^ mult256[15]);
        final int[] array17 = array3;
        final int n10 = 22;
        array17[n10] ^= (mult256[6] ^ mult256[14]);
        final int[] array18 = array3;
        final int n11 = 21;
        array18[n11] ^= (mult256[5] ^ mult256[13]);
        final int[] array19 = array3;
        final int n12 = 20;
        array19[n12] ^= (mult256[4] ^ mult256[12]);
        final int[] array20 = array3;
        final int n13 = 19;
        array20[n13] ^= (mult256[3] ^ mult256[11]);
        final int[] array21 = array3;
        final int n14 = 18;
        array21[n14] ^= (mult256[2] ^ mult256[10]);
        final int[] array22 = array3;
        final int n15 = 17;
        array22[n15] ^= (mult256[1] ^ mult256[9]);
        final int[] array23 = array3;
        final int n16 = 16;
        array23[n16] ^= (mult256[0] ^ mult256[8]);
        final int[] array24 = array3;
        final int n17 = 15;
        array24[n17] ^= mult256[7];
        final int[] array25 = array3;
        final int n18 = 14;
        array25[n18] ^= mult256[6];
        final int[] array26 = array3;
        final int n19 = 13;
        array26[n19] ^= mult256[5];
        final int[] array27 = array3;
        final int n20 = 12;
        array27[n20] ^= mult256[4];
        final int[] array28 = array3;
        final int n21 = 11;
        array28[n21] ^= mult256[3];
        final int[] array29 = array3;
        final int n22 = 10;
        array29[n22] ^= mult256[2];
        final int[] array30 = array3;
        final int n23 = 9;
        array30[n23] ^= mult256[1];
        final int[] array31 = array3;
        final int n24 = 8;
        array31[n24] ^= mult256[0];
        final int[] array32 = array5;
        final int n25 = 0;
        array32[n25] ^= array4[0];
        final int[] array33 = array5;
        final int n26 = 1;
        array33[n26] ^= array4[1];
        final int[] array34 = array5;
        final int n27 = 2;
        array34[n27] ^= array4[2];
        final int[] array35 = array5;
        final int n28 = 3;
        array35[n28] ^= array4[3];
        final int[] array36 = array5;
        final int n29 = 4;
        array36[n29] ^= array4[4];
        final int[] array37 = array5;
        final int n30 = 5;
        array37[n30] ^= array4[5];
        final int[] array38 = array5;
        final int n31 = 6;
        array38[n31] ^= array4[6];
        final int[] array39 = array5;
        final int n32 = 7;
        array39[n32] ^= array4[7];
        final int[] array40 = array7;
        final int n33 = 0;
        array40[n33] ^= array6[0];
        final int[] array41 = array7;
        final int n34 = 1;
        array41[n34] ^= array6[1];
        final int[] array42 = array7;
        final int n35 = 2;
        array42[n35] ^= array6[2];
        final int[] array43 = array7;
        final int n36 = 3;
        array43[n36] ^= array6[3];
        final int[] array44 = array7;
        final int n37 = 4;
        array44[n37] ^= array6[4];
        final int[] array45 = array7;
        final int n38 = 5;
        array45[n38] ^= array6[5];
        final int[] array46 = array7;
        final int n39 = 6;
        array46[n39] ^= array6[6];
        final int[] array47 = array7;
        final int n40 = 7;
        array47[n40] ^= array6[7];
        final int[] mult257 = mult256(array5, array7);
        final int[] array48 = array3;
        final int n41 = 23;
        array48[n41] ^= mult257[15];
        final int[] array49 = array3;
        final int n42 = 22;
        array49[n42] ^= mult257[14];
        final int[] array50 = array3;
        final int n43 = 21;
        array50[n43] ^= mult257[13];
        final int[] array51 = array3;
        final int n44 = 20;
        array51[n44] ^= mult257[12];
        final int[] array52 = array3;
        final int n45 = 19;
        array52[n45] ^= mult257[11];
        final int[] array53 = array3;
        final int n46 = 18;
        array53[n46] ^= mult257[10];
        final int[] array54 = array3;
        final int n47 = 17;
        array54[n47] ^= mult257[9];
        final int[] array55 = array3;
        final int n48 = 16;
        array55[n48] ^= mult257[8];
        final int[] array56 = array3;
        final int n49 = 15;
        array56[n49] ^= mult257[7];
        final int[] array57 = array3;
        final int n50 = 14;
        array57[n50] ^= mult257[6];
        final int[] array58 = array3;
        final int n51 = 13;
        array58[n51] ^= mult257[5];
        final int[] array59 = array3;
        final int n52 = 12;
        array59[n52] ^= mult257[4];
        final int[] array60 = array3;
        final int n53 = 11;
        array60[n53] ^= mult257[3];
        final int[] array61 = array3;
        final int n54 = 10;
        array61[n54] ^= mult257[2];
        final int[] array62 = array3;
        final int n55 = 9;
        array62[n55] ^= mult257[1];
        final int[] array63 = array3;
        final int n56 = 8;
        array63[n56] ^= mult257[0];
        final int[] mult258 = mult256(array4, array6);
        final int[] array64 = array3;
        final int n57 = 23;
        array64[n57] ^= mult258[15];
        final int[] array65 = array3;
        final int n58 = 22;
        array65[n58] ^= mult258[14];
        final int[] array66 = array3;
        final int n59 = 21;
        array66[n59] ^= mult258[13];
        final int[] array67 = array3;
        final int n60 = 20;
        array67[n60] ^= mult258[12];
        final int[] array68 = array3;
        final int n61 = 19;
        array68[n61] ^= mult258[11];
        final int[] array69 = array3;
        final int n62 = 18;
        array69[n62] ^= mult258[10];
        final int[] array70 = array3;
        final int n63 = 17;
        array70[n63] ^= mult258[9];
        final int[] array71 = array3;
        final int n64 = 16;
        array71[n64] ^= mult258[8];
        final int[] array72 = array3;
        final int n65 = 15;
        array72[n65] ^= (mult258[7] ^ mult258[15]);
        final int[] array73 = array3;
        final int n66 = 14;
        array73[n66] ^= (mult258[6] ^ mult258[14]);
        final int[] array74 = array3;
        final int n67 = 13;
        array74[n67] ^= (mult258[5] ^ mult258[13]);
        final int[] array75 = array3;
        final int n68 = 12;
        array75[n68] ^= (mult258[4] ^ mult258[12]);
        final int[] array76 = array3;
        final int n69 = 11;
        array76[n69] ^= (mult258[3] ^ mult258[11]);
        final int[] array77 = array3;
        final int n70 = 10;
        array77[n70] ^= (mult258[2] ^ mult258[10]);
        final int[] array78 = array3;
        final int n71 = 9;
        array78[n71] ^= (mult258[1] ^ mult258[9]);
        final int[] array79 = array3;
        final int n72 = 8;
        array79[n72] ^= (mult258[0] ^ mult258[8]);
        final int[] array80 = array3;
        final int n73 = 7;
        array80[n73] ^= mult258[7];
        final int[] array81 = array3;
        final int n74 = 6;
        array81[n74] ^= mult258[6];
        final int[] array82 = array3;
        final int n75 = 5;
        array82[n75] ^= mult258[5];
        final int[] array83 = array3;
        final int n76 = 4;
        array83[n76] ^= mult258[4];
        final int[] array84 = array3;
        final int n77 = 3;
        array84[n77] ^= mult258[3];
        final int[] array85 = array3;
        final int n78 = 2;
        array85[n78] ^= mult258[2];
        final int[] array86 = array3;
        final int n79 = 1;
        array86[n79] ^= mult258[1];
        final int[] array87 = array3;
        final int n80 = 0;
        array87[n80] ^= mult258[0];
        return array3;
    }
    
    private static int[] mult256(final int[] array, final int[] array2) {
        final int[] array3 = new int[16];
        final int[] array4 = new int[4];
        System.arraycopy(array, 0, array4, 0, Math.min(4, array.length));
        final int[] array5 = new int[4];
        if (array.length > 4) {
            System.arraycopy(array, 4, array5, 0, Math.min(4, array.length - 4));
        }
        final int[] array6 = new int[4];
        System.arraycopy(array2, 0, array6, 0, Math.min(4, array2.length));
        final int[] array7 = new int[4];
        if (array2.length > 4) {
            System.arraycopy(array2, 4, array7, 0, Math.min(4, array2.length - 4));
        }
        if (array5[3] == 0 && array5[2] == 0 && array7[3] == 0 && array7[2] == 0) {
            if (array5[1] == 0 && array7[1] == 0) {
                if (array5[0] != 0 || array7[0] != 0) {
                    final int[] mult32 = mult32(array5[0], array7[0]);
                    final int[] array8 = array3;
                    final int n = 9;
                    array8[n] ^= mult32[1];
                    final int[] array9 = array3;
                    final int n2 = 8;
                    array9[n2] ^= mult32[0];
                    final int[] array10 = array3;
                    final int n3 = 5;
                    array10[n3] ^= mult32[1];
                    final int[] array11 = array3;
                    final int n4 = 4;
                    array11[n4] ^= mult32[0];
                }
            }
            else {
                final int[] mult33 = mult64(array5, array7);
                final int[] array12 = array3;
                final int n5 = 11;
                array12[n5] ^= mult33[3];
                final int[] array13 = array3;
                final int n6 = 10;
                array13[n6] ^= mult33[2];
                final int[] array14 = array3;
                final int n7 = 9;
                array14[n7] ^= mult33[1];
                final int[] array15 = array3;
                final int n8 = 8;
                array15[n8] ^= mult33[0];
                final int[] array16 = array3;
                final int n9 = 7;
                array16[n9] ^= mult33[3];
                final int[] array17 = array3;
                final int n10 = 6;
                array17[n10] ^= mult33[2];
                final int[] array18 = array3;
                final int n11 = 5;
                array18[n11] ^= mult33[1];
                final int[] array19 = array3;
                final int n12 = 4;
                array19[n12] ^= mult33[0];
            }
        }
        else {
            final int[] mult34 = mult128(array5, array7);
            final int[] array20 = array3;
            final int n13 = 15;
            array20[n13] ^= mult34[7];
            final int[] array21 = array3;
            final int n14 = 14;
            array21[n14] ^= mult34[6];
            final int[] array22 = array3;
            final int n15 = 13;
            array22[n15] ^= mult34[5];
            final int[] array23 = array3;
            final int n16 = 12;
            array23[n16] ^= mult34[4];
            final int[] array24 = array3;
            final int n17 = 11;
            array24[n17] ^= (mult34[3] ^ mult34[7]);
            final int[] array25 = array3;
            final int n18 = 10;
            array25[n18] ^= (mult34[2] ^ mult34[6]);
            final int[] array26 = array3;
            final int n19 = 9;
            array26[n19] ^= (mult34[1] ^ mult34[5]);
            final int[] array27 = array3;
            final int n20 = 8;
            array27[n20] ^= (mult34[0] ^ mult34[4]);
            final int[] array28 = array3;
            final int n21 = 7;
            array28[n21] ^= mult34[3];
            final int[] array29 = array3;
            final int n22 = 6;
            array29[n22] ^= mult34[2];
            final int[] array30 = array3;
            final int n23 = 5;
            array30[n23] ^= mult34[1];
            final int[] array31 = array3;
            final int n24 = 4;
            array31[n24] ^= mult34[0];
        }
        final int[] array32 = array5;
        final int n25 = 0;
        array32[n25] ^= array4[0];
        final int[] array33 = array5;
        final int n26 = 1;
        array33[n26] ^= array4[1];
        final int[] array34 = array5;
        final int n27 = 2;
        array34[n27] ^= array4[2];
        final int[] array35 = array5;
        final int n28 = 3;
        array35[n28] ^= array4[3];
        final int[] array36 = array7;
        final int n29 = 0;
        array36[n29] ^= array6[0];
        final int[] array37 = array7;
        final int n30 = 1;
        array37[n30] ^= array6[1];
        final int[] array38 = array7;
        final int n31 = 2;
        array38[n31] ^= array6[2];
        final int[] array39 = array7;
        final int n32 = 3;
        array39[n32] ^= array6[3];
        final int[] mult35 = mult128(array5, array7);
        final int[] array40 = array3;
        final int n33 = 11;
        array40[n33] ^= mult35[7];
        final int[] array41 = array3;
        final int n34 = 10;
        array41[n34] ^= mult35[6];
        final int[] array42 = array3;
        final int n35 = 9;
        array42[n35] ^= mult35[5];
        final int[] array43 = array3;
        final int n36 = 8;
        array43[n36] ^= mult35[4];
        final int[] array44 = array3;
        final int n37 = 7;
        array44[n37] ^= mult35[3];
        final int[] array45 = array3;
        final int n38 = 6;
        array45[n38] ^= mult35[2];
        final int[] array46 = array3;
        final int n39 = 5;
        array46[n39] ^= mult35[1];
        final int[] array47 = array3;
        final int n40 = 4;
        array47[n40] ^= mult35[0];
        final int[] mult36 = mult128(array4, array6);
        final int[] array48 = array3;
        final int n41 = 11;
        array48[n41] ^= mult36[7];
        final int[] array49 = array3;
        final int n42 = 10;
        array49[n42] ^= mult36[6];
        final int[] array50 = array3;
        final int n43 = 9;
        array50[n43] ^= mult36[5];
        final int[] array51 = array3;
        final int n44 = 8;
        array51[n44] ^= mult36[4];
        final int[] array52 = array3;
        final int n45 = 7;
        array52[n45] ^= (mult36[3] ^ mult36[7]);
        final int[] array53 = array3;
        final int n46 = 6;
        array53[n46] ^= (mult36[2] ^ mult36[6]);
        final int[] array54 = array3;
        final int n47 = 5;
        array54[n47] ^= (mult36[1] ^ mult36[5]);
        final int[] array55 = array3;
        final int n48 = 4;
        array55[n48] ^= (mult36[0] ^ mult36[4]);
        final int[] array56 = array3;
        final int n49 = 3;
        array56[n49] ^= mult36[3];
        final int[] array57 = array3;
        final int n50 = 2;
        array57[n50] ^= mult36[2];
        final int[] array58 = array3;
        final int n51 = 1;
        array58[n51] ^= mult36[1];
        final int[] array59 = array3;
        final int n52 = 0;
        array59[n52] ^= mult36[0];
        return array3;
    }
    
    private static int[] mult128(final int[] array, final int[] array2) {
        final int[] array3 = new int[8];
        final int[] array4 = new int[2];
        System.arraycopy(array, 0, array4, 0, Math.min(2, array.length));
        final int[] array5 = new int[2];
        if (array.length > 2) {
            System.arraycopy(array, 2, array5, 0, Math.min(2, array.length - 2));
        }
        final int[] array6 = new int[2];
        System.arraycopy(array2, 0, array6, 0, Math.min(2, array2.length));
        final int[] array7 = new int[2];
        if (array2.length > 2) {
            System.arraycopy(array2, 2, array7, 0, Math.min(2, array2.length - 2));
        }
        if (array5[1] == 0 && array7[1] == 0) {
            if (array5[0] != 0 || array7[0] != 0) {
                final int[] mult32 = mult32(array5[0], array7[0]);
                final int[] array8 = array3;
                final int n = 5;
                array8[n] ^= mult32[1];
                final int[] array9 = array3;
                final int n2 = 4;
                array9[n2] ^= mult32[0];
                final int[] array10 = array3;
                final int n3 = 3;
                array10[n3] ^= mult32[1];
                final int[] array11 = array3;
                final int n4 = 2;
                array11[n4] ^= mult32[0];
            }
        }
        else {
            final int[] mult33 = mult64(array5, array7);
            final int[] array12 = array3;
            final int n5 = 7;
            array12[n5] ^= mult33[3];
            final int[] array13 = array3;
            final int n6 = 6;
            array13[n6] ^= mult33[2];
            final int[] array14 = array3;
            final int n7 = 5;
            array14[n7] ^= (mult33[1] ^ mult33[3]);
            final int[] array15 = array3;
            final int n8 = 4;
            array15[n8] ^= (mult33[0] ^ mult33[2]);
            final int[] array16 = array3;
            final int n9 = 3;
            array16[n9] ^= mult33[1];
            final int[] array17 = array3;
            final int n10 = 2;
            array17[n10] ^= mult33[0];
        }
        final int[] array18 = array5;
        final int n11 = 0;
        array18[n11] ^= array4[0];
        final int[] array19 = array5;
        final int n12 = 1;
        array19[n12] ^= array4[1];
        final int[] array20 = array7;
        final int n13 = 0;
        array20[n13] ^= array6[0];
        final int[] array21 = array7;
        final int n14 = 1;
        array21[n14] ^= array6[1];
        if (array5[1] == 0 && array7[1] == 0) {
            final int[] mult34 = mult32(array5[0], array7[0]);
            final int[] array22 = array3;
            final int n15 = 3;
            array22[n15] ^= mult34[1];
            final int[] array23 = array3;
            final int n16 = 2;
            array23[n16] ^= mult34[0];
        }
        else {
            final int[] mult35 = mult64(array5, array7);
            final int[] array24 = array3;
            final int n17 = 5;
            array24[n17] ^= mult35[3];
            final int[] array25 = array3;
            final int n18 = 4;
            array25[n18] ^= mult35[2];
            final int[] array26 = array3;
            final int n19 = 3;
            array26[n19] ^= mult35[1];
            final int[] array27 = array3;
            final int n20 = 2;
            array27[n20] ^= mult35[0];
        }
        if (array4[1] == 0 && array6[1] == 0) {
            final int[] mult36 = mult32(array4[0], array6[0]);
            final int[] array28 = array3;
            final int n21 = 3;
            array28[n21] ^= mult36[1];
            final int[] array29 = array3;
            final int n22 = 2;
            array29[n22] ^= mult36[0];
            final int[] array30 = array3;
            final int n23 = 1;
            array30[n23] ^= mult36[1];
            final int[] array31 = array3;
            final int n24 = 0;
            array31[n24] ^= mult36[0];
        }
        else {
            final int[] mult37 = mult64(array4, array6);
            final int[] array32 = array3;
            final int n25 = 5;
            array32[n25] ^= mult37[3];
            final int[] array33 = array3;
            final int n26 = 4;
            array33[n26] ^= mult37[2];
            final int[] array34 = array3;
            final int n27 = 3;
            array34[n27] ^= (mult37[1] ^ mult37[3]);
            final int[] array35 = array3;
            final int n28 = 2;
            array35[n28] ^= (mult37[0] ^ mult37[2]);
            final int[] array36 = array3;
            final int n29 = 1;
            array36[n29] ^= mult37[1];
            final int[] array37 = array3;
            final int n30 = 0;
            array37[n30] ^= mult37[0];
        }
        return array3;
    }
    
    private static int[] mult64(final int[] array, final int[] array2) {
        final int[] array3 = new int[4];
        final int n = array[0];
        int n2 = 0;
        if (array.length > 1) {
            n2 = array[1];
        }
        final int n3 = array2[0];
        int n4 = 0;
        if (array2.length > 1) {
            n4 = array2[1];
        }
        if (n2 != 0 || n4 != 0) {
            final int[] mult32 = mult32(n2, n4);
            final int[] array4 = array3;
            final int n5 = 3;
            array4[n5] ^= mult32[1];
            final int[] array5 = array3;
            final int n6 = 2;
            array5[n6] ^= (mult32[0] ^ mult32[1]);
            final int[] array6 = array3;
            final int n7 = 1;
            array6[n7] ^= mult32[0];
        }
        final int[] mult33 = mult32(n ^ n2, n3 ^ n4);
        final int[] array7 = array3;
        final int n8 = 2;
        array7[n8] ^= mult33[1];
        final int[] array8 = array3;
        final int n9 = 1;
        array8[n9] ^= mult33[0];
        final int[] mult34 = mult32(n, n3);
        final int[] array9 = array3;
        final int n10 = 2;
        array9[n10] ^= mult34[1];
        final int[] array10 = array3;
        final int n11 = 1;
        array10[n11] ^= (mult34[0] ^ mult34[1]);
        final int[] array11 = array3;
        final int n12 = 0;
        array11[n12] ^= mult34[0];
        return array3;
    }
    
    private static int[] mult32(final int n, final int n2) {
        final int[] array = new int[2];
        if (n == 0 || n2 == 0) {
            return array;
        }
        long n3 = (long)n2 & 0xFFFFFFFFL;
        long n4 = 0L;
        for (int i = 1; i <= 32; ++i) {
            if ((n & GF2Polynomial.bitMask[i - 1]) != 0x0) {
                n4 ^= n3;
            }
            n3 <<= 1;
        }
        array[1] = (int)(n4 >>> 32);
        array[0] = (int)(n4 & 0xFFFFFFFFL);
        return array;
    }
    
    private GF2Polynomial upper(final int n) {
        final int min = Math.min(n, this.blocks - n);
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(min << 5);
        if (this.blocks >= n) {
            System.arraycopy(this.value, n, gf2Polynomial.value, 0, min);
        }
        return gf2Polynomial;
    }
    
    private GF2Polynomial lower(final int n) {
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(n << 5);
        System.arraycopy(this.value, 0, gf2Polynomial.value, 0, Math.min(n, this.blocks));
        return gf2Polynomial;
    }
    
    public GF2Polynomial remainder(final GF2Polynomial gf2Polynomial) throws RuntimeException {
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this);
        final GF2Polynomial gf2Polynomial3 = new GF2Polynomial(gf2Polynomial);
        if (gf2Polynomial3.isZero()) {
            throw new RuntimeException();
        }
        gf2Polynomial2.reduceN();
        gf2Polynomial3.reduceN();
        if (gf2Polynomial2.len < gf2Polynomial3.len) {
            return gf2Polynomial2;
        }
        for (int i = gf2Polynomial2.len - gf2Polynomial3.len; i >= 0; i = gf2Polynomial2.len - gf2Polynomial3.len) {
            gf2Polynomial2.subtractFromThis(gf2Polynomial3.shiftLeft(i));
            gf2Polynomial2.reduceN();
        }
        return gf2Polynomial2;
    }
    
    public GF2Polynomial quotient(final GF2Polynomial gf2Polynomial) throws RuntimeException {
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.len);
        final GF2Polynomial gf2Polynomial3 = new GF2Polynomial(this);
        final GF2Polynomial gf2Polynomial4 = new GF2Polynomial(gf2Polynomial);
        if (gf2Polynomial4.isZero()) {
            throw new RuntimeException();
        }
        gf2Polynomial3.reduceN();
        gf2Polynomial4.reduceN();
        if (gf2Polynomial3.len < gf2Polynomial4.len) {
            return new GF2Polynomial(0);
        }
        int i = gf2Polynomial3.len - gf2Polynomial4.len;
        gf2Polynomial2.expandN(i + 1);
        while (i >= 0) {
            gf2Polynomial3.subtractFromThis(gf2Polynomial4.shiftLeft(i));
            gf2Polynomial3.reduceN();
            gf2Polynomial2.xorBit(i);
            i = gf2Polynomial3.len - gf2Polynomial4.len;
        }
        return gf2Polynomial2;
    }
    
    public GF2Polynomial[] divide(final GF2Polynomial gf2Polynomial) throws RuntimeException {
        final GF2Polynomial[] array = new GF2Polynomial[2];
        final GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this.len);
        final GF2Polynomial gf2Polynomial3 = new GF2Polynomial(this);
        final GF2Polynomial gf2Polynomial4 = new GF2Polynomial(gf2Polynomial);
        if (gf2Polynomial4.isZero()) {
            throw new RuntimeException();
        }
        gf2Polynomial3.reduceN();
        gf2Polynomial4.reduceN();
        if (gf2Polynomial3.len < gf2Polynomial4.len) {
            array[0] = new GF2Polynomial(0);
            array[1] = gf2Polynomial3;
            return array;
        }
        int i = gf2Polynomial3.len - gf2Polynomial4.len;
        gf2Polynomial2.expandN(i + 1);
        while (i >= 0) {
            gf2Polynomial3.subtractFromThis(gf2Polynomial4.shiftLeft(i));
            gf2Polynomial3.reduceN();
            gf2Polynomial2.xorBit(i);
            i = gf2Polynomial3.len - gf2Polynomial4.len;
        }
        array[0] = gf2Polynomial2;
        array[1] = gf2Polynomial3;
        return array;
    }
    
    public GF2Polynomial gcd(final GF2Polynomial gf2Polynomial) throws RuntimeException {
        if (this.isZero() && gf2Polynomial.isZero()) {
            throw new ArithmeticException("Both operands of gcd equal zero.");
        }
        if (this.isZero()) {
            return new GF2Polynomial(gf2Polynomial);
        }
        if (gf2Polynomial.isZero()) {
            return new GF2Polynomial(this);
        }
        GF2Polynomial gf2Polynomial2 = new GF2Polynomial(this);
        GF2Polynomial remainder;
        for (GF2Polynomial gf2Polynomial3 = new GF2Polynomial(gf2Polynomial); !gf2Polynomial3.isZero(); gf2Polynomial3 = remainder) {
            remainder = gf2Polynomial2.remainder(gf2Polynomial3);
            gf2Polynomial2 = gf2Polynomial3;
        }
        return gf2Polynomial2;
    }
    
    public boolean isIrreducible() {
        if (this.isZero()) {
            return false;
        }
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this);
        gf2Polynomial.reduceN();
        final int n = gf2Polynomial.len - 1;
        GF2Polynomial remainder = new GF2Polynomial(gf2Polynomial.len, "X");
        for (int i = 1; i <= n >> 1; ++i) {
            remainder.squareThisPreCalc();
            remainder = remainder.remainder(gf2Polynomial);
            final GF2Polynomial add = remainder.add(new GF2Polynomial(32, "X"));
            if (add.isZero()) {
                return false;
            }
            if (!gf2Polynomial.gcd(add).isOne()) {
                return false;
            }
        }
        return true;
    }
    
    void reduceTrinomial(final int len, final int n) {
        final int n2 = len >>> 5;
        final int n3 = 32 - (len & 0x1F);
        final int n4 = len - n >>> 5;
        final int n5 = 32 - (len - n & 0x1F);
        final int n6 = (len << 1) - 2 >>> 5;
        final int n7 = n2;
        for (int i = n6; i > n7; --i) {
            final long n8 = (long)this.value[i] & 0xFFFFFFFFL;
            final int[] value = this.value;
            final int n9 = i - n2 - 1;
            value[n9] ^= (int)(n8 << n3);
            final int[] value2 = this.value;
            final int n10 = i - n2;
            value2[n10] = (int)((long)value2[n10] ^ n8 >>> 32 - n3);
            final int[] value3 = this.value;
            final int n11 = i - n4 - 1;
            value3[n11] ^= (int)(n8 << n5);
            final int[] value4 = this.value;
            final int n12 = i - n4;
            value4[n12] = (int)((long)value4[n12] ^ n8 >>> 32 - n5);
            this.value[i] = 0;
        }
        final long n13 = (long)this.value[n7] & 0xFFFFFFFFL & 4294967295L << (len & 0x1F);
        final int[] value5 = this.value;
        final int n14 = 0;
        value5[n14] = (int)((long)value5[n14] ^ n13 >>> 32 - n3);
        if (n7 - n4 - 1 >= 0) {
            final int[] value6 = this.value;
            final int n15 = n7 - n4 - 1;
            value6[n15] ^= (int)(n13 << n5);
        }
        final int[] value7 = this.value;
        final int n16 = n7 - n4;
        value7[n16] = (int)((long)value7[n16] ^ n13 >>> 32 - n5);
        final int[] value8 = this.value;
        final int n17 = n7;
        value8[n17] &= GF2Polynomial.reverseRightMask[len & 0x1F];
        this.blocks = (len - 1 >>> 5) + 1;
        this.len = len;
    }
    
    void reducePentanomial(final int len, final int[] array) {
        final int n = len >>> 5;
        final int n2 = 32 - (len & 0x1F);
        final int n3 = len - array[0] >>> 5;
        final int n4 = 32 - (len - array[0] & 0x1F);
        final int n5 = len - array[1] >>> 5;
        final int n6 = 32 - (len - array[1] & 0x1F);
        final int n7 = len - array[2] >>> 5;
        final int n8 = 32 - (len - array[2] & 0x1F);
        final int n9 = (len << 1) - 2 >>> 5;
        final int n10 = n;
        for (int i = n9; i > n10; --i) {
            final long n11 = (long)this.value[i] & 0xFFFFFFFFL;
            final int[] value = this.value;
            final int n12 = i - n - 1;
            value[n12] ^= (int)(n11 << n2);
            final int[] value2 = this.value;
            final int n13 = i - n;
            value2[n13] = (int)((long)value2[n13] ^ n11 >>> 32 - n2);
            final int[] value3 = this.value;
            final int n14 = i - n3 - 1;
            value3[n14] ^= (int)(n11 << n4);
            final int[] value4 = this.value;
            final int n15 = i - n3;
            value4[n15] = (int)((long)value4[n15] ^ n11 >>> 32 - n4);
            final int[] value5 = this.value;
            final int n16 = i - n5 - 1;
            value5[n16] ^= (int)(n11 << n6);
            final int[] value6 = this.value;
            final int n17 = i - n5;
            value6[n17] = (int)((long)value6[n17] ^ n11 >>> 32 - n6);
            final int[] value7 = this.value;
            final int n18 = i - n7 - 1;
            value7[n18] ^= (int)(n11 << n8);
            final int[] value8 = this.value;
            final int n19 = i - n7;
            value8[n19] = (int)((long)value8[n19] ^ n11 >>> 32 - n8);
            this.value[i] = 0;
        }
        final long n20 = (long)this.value[n10] & 0xFFFFFFFFL & 4294967295L << (len & 0x1F);
        final int[] value9 = this.value;
        final int n21 = 0;
        value9[n21] = (int)((long)value9[n21] ^ n20 >>> 32 - n2);
        if (n10 - n3 - 1 >= 0) {
            final int[] value10 = this.value;
            final int n22 = n10 - n3 - 1;
            value10[n22] ^= (int)(n20 << n4);
        }
        final int[] value11 = this.value;
        final int n23 = n10 - n3;
        value11[n23] = (int)((long)value11[n23] ^ n20 >>> 32 - n4);
        if (n10 - n5 - 1 >= 0) {
            final int[] value12 = this.value;
            final int n24 = n10 - n5 - 1;
            value12[n24] ^= (int)(n20 << n6);
        }
        final int[] value13 = this.value;
        final int n25 = n10 - n5;
        value13[n25] = (int)((long)value13[n25] ^ n20 >>> 32 - n6);
        if (n10 - n7 - 1 >= 0) {
            final int[] value14 = this.value;
            final int n26 = n10 - n7 - 1;
            value14[n26] ^= (int)(n20 << n8);
        }
        final int[] value15 = this.value;
        final int n27 = n10 - n7;
        value15[n27] = (int)((long)value15[n27] ^ n20 >>> 32 - n8);
        final int[] value16 = this.value;
        final int n28 = n10;
        value16[n28] &= GF2Polynomial.reverseRightMask[len & 0x1F];
        this.blocks = (len - 1 >>> 5) + 1;
        this.len = len;
    }
    
    public void reduceN() {
        int n;
        for (n = this.blocks - 1; this.value[n] == 0 && n > 0; --n) {}
        int i;
        int n2;
        for (i = this.value[n], n2 = 0; i != 0; i >>>= 1, ++n2) {}
        this.len = (n << 5) + n2;
        this.blocks = n + 1;
    }
    
    public void expandN(final int len) {
        if (this.len >= len) {
            return;
        }
        this.len = len;
        final int n = (len - 1 >>> 5) + 1;
        if (this.blocks >= n) {
            return;
        }
        if (this.value.length >= n) {
            for (int i = this.blocks; i < n; ++i) {
                this.value[i] = 0;
            }
            this.blocks = n;
            return;
        }
        final int[] value = new int[n];
        System.arraycopy(this.value, 0, value, 0, this.blocks);
        this.blocks = n;
        this.value = null;
        this.value = value;
    }
    
    public void squareThisBitwise() {
        if (this.isZero()) {
            return;
        }
        final int[] value = new int[this.blocks << 1];
        for (int i = this.blocks - 1; i >= 0; --i) {
            int n = this.value[i];
            int n2 = 1;
            for (int j = 0; j < 16; ++j) {
                if ((n & 0x1) != 0x0) {
                    final int[] array = value;
                    final int n3 = i << 1;
                    array[n3] |= n2;
                }
                if ((n & 0x10000) != 0x0) {
                    final int[] array2 = value;
                    final int n4 = (i << 1) + 1;
                    array2[n4] |= n2;
                }
                n2 <<= 2;
                n >>>= 1;
            }
        }
        this.value = null;
        this.value = value;
        this.blocks = value.length;
        this.len = (this.len << 1) - 1;
    }
    
    public void squareThisPreCalc() {
        if (this.isZero()) {
            return;
        }
        if (this.value.length >= this.blocks << 1) {
            for (int i = this.blocks - 1; i >= 0; --i) {
                this.value[(i << 1) + 1] = (GF2Polynomial.squaringTable[(this.value[i] & 0xFF0000) >>> 16] | GF2Polynomial.squaringTable[(this.value[i] & 0xFF000000) >>> 24] << 16);
                this.value[i << 1] = (GF2Polynomial.squaringTable[this.value[i] & 0xFF] | GF2Polynomial.squaringTable[(this.value[i] & 0xFF00) >>> 8] << 16);
            }
            this.blocks <<= 1;
            this.len = (this.len << 1) - 1;
        }
        else {
            final int[] value = new int[this.blocks << 1];
            for (int j = 0; j < this.blocks; ++j) {
                value[j << 1] = (GF2Polynomial.squaringTable[this.value[j] & 0xFF] | GF2Polynomial.squaringTable[(this.value[j] & 0xFF00) >>> 8] << 16);
                value[(j << 1) + 1] = (GF2Polynomial.squaringTable[(this.value[j] & 0xFF0000) >>> 16] | GF2Polynomial.squaringTable[(this.value[j] & 0xFF000000) >>> 24] << 16);
            }
            this.value = null;
            this.value = value;
            this.blocks <<= 1;
            this.len = (this.len << 1) - 1;
        }
    }
    
    public boolean vectorMult(final GF2Polynomial gf2Polynomial) throws RuntimeException {
        boolean b = false;
        if (this.len != gf2Polynomial.len) {
            throw new RuntimeException();
        }
        for (int i = 0; i < this.blocks; ++i) {
            final int n = this.value[i] & gf2Polynomial.value[i];
            b = (b ^ GF2Polynomial.parity[n & 0xFF] ^ GF2Polynomial.parity[n >>> 8 & 0xFF] ^ GF2Polynomial.parity[n >>> 16 & 0xFF] ^ GF2Polynomial.parity[n >>> 24 & 0xFF]);
        }
        return b;
    }
    
    public GF2Polynomial xor(final GF2Polynomial gf2Polynomial) {
        final int min = Math.min(this.blocks, gf2Polynomial.blocks);
        GF2Polynomial gf2Polynomial2;
        if (this.len >= gf2Polynomial.len) {
            gf2Polynomial2 = new GF2Polynomial(this);
            for (int i = 0; i < min; ++i) {
                final int[] value = gf2Polynomial2.value;
                final int n = i;
                value[n] ^= gf2Polynomial.value[i];
            }
        }
        else {
            gf2Polynomial2 = new GF2Polynomial(gf2Polynomial);
            for (int j = 0; j < min; ++j) {
                final int[] value2 = gf2Polynomial2.value;
                final int n2 = j;
                value2[n2] ^= this.value[j];
            }
        }
        gf2Polynomial2.zeroUnusedBits();
        return gf2Polynomial2;
    }
    
    public void xorThisBy(final GF2Polynomial gf2Polynomial) {
        for (int i = 0; i < Math.min(this.blocks, gf2Polynomial.blocks); ++i) {
            final int[] value = this.value;
            final int n = i;
            value[n] ^= gf2Polynomial.value[i];
        }
        this.zeroUnusedBits();
    }
    
    private void zeroUnusedBits() {
        if ((this.len & 0x1F) != 0x0) {
            final int[] value = this.value;
            final int n = this.blocks - 1;
            value[n] &= GF2Polynomial.reverseRightMask[this.len & 0x1F];
        }
    }
    
    public void setBit(final int n) throws RuntimeException {
        if (n < 0 || n > this.len - 1) {
            throw new RuntimeException();
        }
        final int[] value = this.value;
        final int n2 = n >>> 5;
        value[n2] |= GF2Polynomial.bitMask[n & 0x1F];
    }
    
    public int getBit(final int n) {
        if (n < 0) {
            throw new RuntimeException();
        }
        return (n <= this.len - 1 && (this.value[n >>> 5] & GF2Polynomial.bitMask[n & 0x1F]) != 0x0) ? 1 : 0;
    }
    
    public void resetBit(final int n) throws RuntimeException {
        if (n < 0) {
            throw new RuntimeException();
        }
        if (n > this.len - 1) {
            return;
        }
        final int[] value = this.value;
        final int n2 = n >>> 5;
        value[n2] &= ~GF2Polynomial.bitMask[n & 0x1F];
    }
    
    public void xorBit(final int n) throws RuntimeException {
        if (n < 0 || n > this.len - 1) {
            throw new RuntimeException();
        }
        final int[] value = this.value;
        final int n2 = n >>> 5;
        value[n2] ^= GF2Polynomial.bitMask[n & 0x1F];
    }
    
    public boolean testBit(final int n) {
        if (n < 0) {
            throw new RuntimeException();
        }
        return n <= this.len - 1 && (this.value[n >>> 5] & GF2Polynomial.bitMask[n & 0x1F]) != 0x0;
    }
    
    public GF2Polynomial shiftLeft() {
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this.len + 1, this.value);
        for (int i = gf2Polynomial.blocks - 1; i >= 1; --i) {
            final int[] value = gf2Polynomial.value;
            final int n = i;
            value[n] <<= 1;
            final int[] value2 = gf2Polynomial.value;
            final int n2 = i;
            value2[n2] |= gf2Polynomial.value[i - 1] >>> 31;
        }
        final int[] value3 = gf2Polynomial.value;
        final int n3 = 0;
        value3[n3] <<= 1;
        return gf2Polynomial;
    }
    
    public void shiftLeftThis() {
        if ((this.len & 0x1F) == 0x0) {
            ++this.len;
            ++this.blocks;
            if (this.blocks > this.value.length) {
                final int[] value = new int[this.blocks];
                System.arraycopy(this.value, 0, value, 0, this.value.length);
                this.value = null;
                this.value = value;
            }
            for (int i = this.blocks - 1; i >= 1; --i) {
                final int[] value2 = this.value;
                final int n = i;
                value2[n] |= this.value[i - 1] >>> 31;
                final int[] value3 = this.value;
                final int n2 = i - 1;
                value3[n2] <<= 1;
            }
        }
        else {
            ++this.len;
            for (int j = this.blocks - 1; j >= 1; --j) {
                final int[] value4 = this.value;
                final int n3 = j;
                value4[n3] <<= 1;
                final int[] value5 = this.value;
                final int n4 = j;
                value5[n4] |= this.value[j - 1] >>> 31;
            }
            final int[] value6 = this.value;
            final int n5 = 0;
            value6[n5] <<= 1;
        }
    }
    
    public GF2Polynomial shiftLeft(final int n) {
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this.len + n, this.value);
        if (n >= 32) {
            gf2Polynomial.doShiftBlocksLeft(n >>> 5);
        }
        final int n2 = n & 0x1F;
        if (n2 != 0) {
            for (int i = gf2Polynomial.blocks - 1; i >= 1; --i) {
                final int[] value = gf2Polynomial.value;
                final int n3 = i;
                value[n3] <<= n2;
                final int[] value2 = gf2Polynomial.value;
                final int n4 = i;
                value2[n4] |= gf2Polynomial.value[i - 1] >>> 32 - n2;
            }
            final int[] value3 = gf2Polynomial.value;
            final int n5 = 0;
            value3[n5] <<= n2;
        }
        return gf2Polynomial;
    }
    
    public void shiftLeftAddThis(final GF2Polynomial gf2Polynomial, final int n) {
        if (n == 0) {
            this.addToThis(gf2Polynomial);
            return;
        }
        this.expandN(gf2Polynomial.len + n);
        final int n2 = n >>> 5;
        for (int i = gf2Polynomial.blocks - 1; i >= 0; --i) {
            if (i + n2 + 1 < this.blocks && (n & 0x1F) != 0x0) {
                final int[] value = this.value;
                final int n3 = i + n2 + 1;
                value[n3] ^= gf2Polynomial.value[i] >>> 32 - (n & 0x1F);
            }
            final int[] value2 = this.value;
            final int n4 = i + n2;
            value2[n4] ^= gf2Polynomial.value[i] << (n & 0x1F);
        }
    }
    
    void shiftBlocksLeft() {
        ++this.blocks;
        this.len += 32;
        if (this.blocks <= this.value.length) {
            for (int i = this.blocks - 1; i >= 1; --i) {
                this.value[i] = this.value[i - 1];
            }
            this.value[0] = 0;
        }
        else {
            final int[] value = new int[this.blocks];
            System.arraycopy(this.value, 0, value, 1, this.blocks - 1);
            this.value = null;
            this.value = value;
        }
    }
    
    private void doShiftBlocksLeft(final int n) {
        if (this.blocks <= this.value.length) {
            for (int i = this.blocks - 1; i >= n; --i) {
                this.value[i] = this.value[i - n];
            }
            for (int j = 0; j < n; ++j) {
                this.value[j] = 0;
            }
        }
        else {
            final int[] value = new int[this.blocks];
            System.arraycopy(this.value, 0, value, n, this.blocks - n);
            this.value = null;
            this.value = value;
        }
    }
    
    public GF2Polynomial shiftRight() {
        final GF2Polynomial gf2Polynomial = new GF2Polynomial(this.len - 1);
        System.arraycopy(this.value, 0, gf2Polynomial.value, 0, gf2Polynomial.blocks);
        for (int i = 0; i <= gf2Polynomial.blocks - 2; ++i) {
            final int[] value = gf2Polynomial.value;
            final int n = i;
            value[n] >>>= 1;
            final int[] value2 = gf2Polynomial.value;
            final int n2 = i;
            value2[n2] |= gf2Polynomial.value[i + 1] << 31;
        }
        final int[] value3 = gf2Polynomial.value;
        final int n3 = gf2Polynomial.blocks - 1;
        value3[n3] >>>= 1;
        if (gf2Polynomial.blocks < this.blocks) {
            final int[] value4 = gf2Polynomial.value;
            final int n4 = gf2Polynomial.blocks - 1;
            value4[n4] |= this.value[gf2Polynomial.blocks] << 31;
        }
        return gf2Polynomial;
    }
    
    public void shiftRightThis() {
        --this.len;
        this.blocks = (this.len - 1 >>> 5) + 1;
        for (int i = 0; i <= this.blocks - 2; ++i) {
            final int[] value = this.value;
            final int n = i;
            value[n] >>>= 1;
            final int[] value2 = this.value;
            final int n2 = i;
            value2[n2] |= this.value[i + 1] << 31;
        }
        final int[] value3 = this.value;
        final int n3 = this.blocks - 1;
        value3[n3] >>>= 1;
        if ((this.len & 0x1F) == 0x0) {
            final int[] value4 = this.value;
            final int n4 = this.blocks - 1;
            value4[n4] |= this.value[this.blocks] << 31;
        }
    }
    
    static {
        GF2Polynomial.rand = new Random();
        parity = new boolean[] { false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false, false, true, true, false, true, false, false, true, false, true, true, false, true, false, false, true, true, false, false, true, false, true, true, false };
        squaringTable = new short[] { 0, 1, 4, 5, 16, 17, 20, 21, 64, 65, 68, 69, 80, 81, 84, 85, 256, 257, 260, 261, 272, 273, 276, 277, 320, 321, 324, 325, 336, 337, 340, 341, 1024, 1025, 1028, 1029, 1040, 1041, 1044, 1045, 1088, 1089, 1092, 1093, 1104, 1105, 1108, 1109, 1280, 1281, 1284, 1285, 1296, 1297, 1300, 1301, 1344, 1345, 1348, 1349, 1360, 1361, 1364, 1365, 4096, 4097, 4100, 4101, 4112, 4113, 4116, 4117, 4160, 4161, 4164, 4165, 4176, 4177, 4180, 4181, 4352, 4353, 4356, 4357, 4368, 4369, 4372, 4373, 4416, 4417, 4420, 4421, 4432, 4433, 4436, 4437, 5120, 5121, 5124, 5125, 5136, 5137, 5140, 5141, 5184, 5185, 5188, 5189, 5200, 5201, 5204, 5205, 5376, 5377, 5380, 5381, 5392, 5393, 5396, 5397, 5440, 5441, 5444, 5445, 5456, 5457, 5460, 5461, 16384, 16385, 16388, 16389, 16400, 16401, 16404, 16405, 16448, 16449, 16452, 16453, 16464, 16465, 16468, 16469, 16640, 16641, 16644, 16645, 16656, 16657, 16660, 16661, 16704, 16705, 16708, 16709, 16720, 16721, 16724, 16725, 17408, 17409, 17412, 17413, 17424, 17425, 17428, 17429, 17472, 17473, 17476, 17477, 17488, 17489, 17492, 17493, 17664, 17665, 17668, 17669, 17680, 17681, 17684, 17685, 17728, 17729, 17732, 17733, 17744, 17745, 17748, 17749, 20480, 20481, 20484, 20485, 20496, 20497, 20500, 20501, 20544, 20545, 20548, 20549, 20560, 20561, 20564, 20565, 20736, 20737, 20740, 20741, 20752, 20753, 20756, 20757, 20800, 20801, 20804, 20805, 20816, 20817, 20820, 20821, 21504, 21505, 21508, 21509, 21520, 21521, 21524, 21525, 21568, 21569, 21572, 21573, 21584, 21585, 21588, 21589, 21760, 21761, 21764, 21765, 21776, 21777, 21780, 21781, 21824, 21825, 21828, 21829, 21840, 21841, 21844, 21845 };
        bitMask = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 1048576, 2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824, Integer.MIN_VALUE, 0 };
        reverseRightMask = new int[] { 0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 1023, 2047, 4095, 8191, 16383, 32767, 65535, 131071, 262143, 524287, 1048575, 2097151, 4194303, 8388607, 16777215, 33554431, 67108863, 134217727, 268435455, 536870911, 1073741823, Integer.MAX_VALUE, -1 };
    }
}
