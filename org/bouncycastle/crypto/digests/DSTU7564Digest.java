package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.ExtendedDigest;

public class DSTU7564Digest implements ExtendedDigest, Memoable
{
    private static final int NB_512 = 8;
    private static final int NB_1024 = 16;
    private static final int NR_512 = 10;
    private static final int NR_1024 = 14;
    private int hashSize;
    private int blockSize;
    private int columns;
    private int rounds;
    private long[] state;
    private long[] tempState1;
    private long[] tempState2;
    private long inputBlocks;
    private int bufOff;
    private byte[] buf;
    private static final byte[] S0;
    private static final byte[] S1;
    private static final byte[] S2;
    private static final byte[] S3;
    
    public DSTU7564Digest(final DSTU7564Digest dstu7564Digest) {
        this.copyIn(dstu7564Digest);
    }
    
    private void copyIn(final DSTU7564Digest dstu7564Digest) {
        this.hashSize = dstu7564Digest.hashSize;
        this.blockSize = dstu7564Digest.blockSize;
        this.rounds = dstu7564Digest.rounds;
        if (this.columns > 0 && this.columns == dstu7564Digest.columns) {
            System.arraycopy(dstu7564Digest.state, 0, this.state, 0, this.columns);
            System.arraycopy(dstu7564Digest.buf, 0, this.buf, 0, this.blockSize);
        }
        else {
            this.columns = dstu7564Digest.columns;
            this.state = Arrays.clone(dstu7564Digest.state);
            this.tempState1 = new long[this.columns];
            this.tempState2 = new long[this.columns];
            this.buf = Arrays.clone(dstu7564Digest.buf);
        }
        this.inputBlocks = dstu7564Digest.inputBlocks;
        this.bufOff = dstu7564Digest.bufOff;
    }
    
    public DSTU7564Digest(final int n) {
        if (n == 256 || n == 384 || n == 512) {
            this.hashSize = n >>> 3;
            if (n > 256) {
                this.columns = 16;
                this.rounds = 14;
            }
            else {
                this.columns = 8;
                this.rounds = 10;
            }
            this.blockSize = this.columns << 3;
            (this.state = new long[this.columns])[0] = this.blockSize;
            this.tempState1 = new long[this.columns];
            this.tempState2 = new long[this.columns];
            this.buf = new byte[this.blockSize];
            return;
        }
        throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
    }
    
    public String getAlgorithmName() {
        return "DSTU7564";
    }
    
    public int getDigestSize() {
        return this.hashSize;
    }
    
    public int getByteLength() {
        return this.blockSize;
    }
    
    public void update(final byte b) {
        this.buf[this.bufOff++] = b;
        if (this.bufOff == this.blockSize) {
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
            ++this.inputBlocks;
        }
    }
    
    public void update(final byte[] array, int n, int i) {
        while (this.bufOff != 0 && i > 0) {
            this.update(array[n++]);
            --i;
        }
        if (i > 0) {
            while (i >= this.blockSize) {
                this.processBlock(array, n);
                n += this.blockSize;
                i -= this.blockSize;
                ++this.inputBlocks;
            }
            while (i > 0) {
                this.update(array[n++]);
                --i;
            }
        }
    }
    
    public int doFinal(final byte[] array, int n) {
        final int bufOff = this.bufOff;
        this.buf[this.bufOff++] = -128;
        final int n2 = this.blockSize - 12;
        if (this.bufOff > n2) {
            while (this.bufOff < this.blockSize) {
                this.buf[this.bufOff++] = 0;
            }
            this.bufOff = 0;
            this.processBlock(this.buf, 0);
        }
        while (this.bufOff < n2) {
            this.buf[this.bufOff++] = 0;
        }
        final long n3 = (this.inputBlocks & 0xFFFFFFFFL) * this.blockSize + bufOff << 3;
        Pack.intToLittleEndian((int)n3, this.buf, this.bufOff);
        this.bufOff += 4;
        Pack.longToLittleEndian((int)((n3 >>> 32) + ((this.inputBlocks >>> 32) * this.blockSize << 3)), this.buf, this.bufOff);
        this.processBlock(this.buf, 0);
        System.arraycopy(this.state, 0, this.tempState1, 0, this.columns);
        this.P(this.tempState1);
        for (int i = 0; i < this.columns; ++i) {
            final long[] state = this.state;
            final int n4 = i;
            state[n4] ^= this.tempState1[i];
        }
        for (int j = this.columns - (this.hashSize >>> 3); j < this.columns; ++j) {
            Pack.longToLittleEndian(this.state[j], array, n);
            n += 8;
        }
        this.reset();
        return this.hashSize;
    }
    
    public void reset() {
        Arrays.fill(this.state, 0L);
        this.state[0] = this.blockSize;
        this.inputBlocks = 0L;
        this.bufOff = 0;
    }
    
    private void processBlock(final byte[] array, final int n) {
        int n2 = n;
        for (int i = 0; i < this.columns; ++i) {
            final long littleEndianToLong = Pack.littleEndianToLong(array, n2);
            n2 += 8;
            this.tempState1[i] = (this.state[i] ^ littleEndianToLong);
            this.tempState2[i] = littleEndianToLong;
        }
        this.P(this.tempState1);
        this.Q(this.tempState2);
        for (int j = 0; j < this.columns; ++j) {
            final long[] state = this.state;
            final int n3 = j;
            state[n3] ^= (this.tempState1[j] ^ this.tempState2[j]);
        }
    }
    
    private void P(final long[] array) {
        for (int i = 0; i < this.rounds; ++i) {
            long n = i;
            for (int j = 0; j < this.columns; ++j) {
                final int n2 = j;
                array[n2] ^= n;
                n += 16L;
            }
            this.shiftRows(array);
            this.subBytes(array);
            this.mixColumns(array);
        }
    }
    
    private void Q(final long[] array) {
        for (int i = 0; i < this.rounds; ++i) {
            long n = (long)(this.columns - 1 << 4 ^ i) << 56 | 0xF0F0F0F0F0F0F3L;
            for (int j = 0; j < this.columns; ++j) {
                final int n2 = j;
                array[n2] += n;
                n -= 1152921504606846976L;
            }
            this.shiftRows(array);
            this.subBytes(array);
            this.mixColumns(array);
        }
    }
    
    private static long mixColumn(final long n) {
        final long n2 = (n & 0x7F7F7F7F7F7F7F7FL) << 1 ^ ((n & 0x8080808080808080L) >>> 7) * 29L;
        final long n3 = rotate(8, n) ^ n;
        final long n4 = n3 ^ rotate(16, n3) ^ rotate(48, n);
        final long n5 = n4 ^ n ^ n2;
        return n4 ^ rotate(32, (n5 & 0x3F3F3F3F3F3F3F3FL) << 2 ^ ((n5 & 0x8080808080808080L) >>> 6) * 29L ^ ((n5 & 0x4040404040404040L) >>> 6) * 29L) ^ rotate(40, n2) ^ rotate(48, n2);
    }
    
    private void mixColumns(final long[] array) {
        for (int i = 0; i < this.columns; ++i) {
            array[i] = mixColumn(array[i]);
        }
    }
    
    private static long rotate(final int n, final long n2) {
        return n2 >>> n | n2 << -n;
    }
    
    private void shiftRows(final long[] array) {
        switch (this.columns) {
            case 8: {
                final long n = array[0];
                final long n2 = array[1];
                final long n3 = array[2];
                final long n4 = array[3];
                final long n5 = array[4];
                final long n6 = array[5];
                final long n7 = array[6];
                final long n8 = array[7];
                final long n9 = (n ^ n5) & 0xFFFFFFFF00000000L;
                final long n10 = n ^ n9;
                final long n11 = n5 ^ n9;
                final long n12 = (n2 ^ n6) & 0xFFFFFFFF000000L;
                final long n13 = n2 ^ n12;
                final long n14 = n6 ^ n12;
                final long n15 = (n3 ^ n7) & 0xFFFFFFFF0000L;
                final long n16 = n3 ^ n15;
                final long n17 = n7 ^ n15;
                final long n18 = (n4 ^ n8) & 0xFFFFFFFF00L;
                final long n19 = n4 ^ n18;
                final long n20 = n8 ^ n18;
                final long n21 = (n10 ^ n16) & 0xFFFF0000FFFF0000L;
                final long n22 = n10 ^ n21;
                final long n23 = n16 ^ n21;
                final long n24 = (n13 ^ n19) & 0xFFFF0000FFFF00L;
                final long n25 = n13 ^ n24;
                final long n26 = n19 ^ n24;
                final long n27 = (n11 ^ n17) & 0xFFFF0000FFFF0000L;
                final long n28 = n11 ^ n27;
                final long n29 = n17 ^ n27;
                final long n30 = (n14 ^ n20) & 0xFFFF0000FFFF00L;
                final long n31 = n14 ^ n30;
                final long n32 = n20 ^ n30;
                final long n33 = (n22 ^ n25) & 0xFF00FF00FF00FF00L;
                final long n34 = n22 ^ n33;
                final long n35 = n25 ^ n33;
                final long n36 = (n23 ^ n26) & 0xFF00FF00FF00FF00L;
                final long n37 = n23 ^ n36;
                final long n38 = n26 ^ n36;
                final long n39 = (n28 ^ n31) & 0xFF00FF00FF00FF00L;
                final long n40 = n28 ^ n39;
                final long n41 = n31 ^ n39;
                final long n42 = (n29 ^ n32) & 0xFF00FF00FF00FF00L;
                final long n43 = n29 ^ n42;
                final long n44 = n32 ^ n42;
                array[0] = n34;
                array[1] = n35;
                array[2] = n37;
                array[3] = n38;
                array[4] = n40;
                array[5] = n41;
                array[6] = n43;
                array[7] = n44;
                break;
            }
            case 16: {
                final long n45 = array[0];
                final long n46 = array[1];
                final long n47 = array[2];
                final long n48 = array[3];
                final long n49 = array[4];
                final long n50 = array[5];
                final long n51 = array[6];
                final long n52 = array[7];
                final long n53 = array[8];
                final long n54 = array[9];
                final long n55 = array[10];
                final long n56 = array[11];
                final long n57 = array[12];
                final long n58 = array[13];
                final long n59 = array[14];
                final long n60 = array[15];
                final long n61 = (n45 ^ n53) & 0xFF00000000000000L;
                final long n62 = n45 ^ n61;
                final long n63 = n53 ^ n61;
                final long n64 = (n46 ^ n54) & 0xFF00000000000000L;
                final long n65 = n46 ^ n64;
                final long n66 = n54 ^ n64;
                final long n67 = (n47 ^ n55) & 0xFFFF000000000000L;
                final long n68 = n47 ^ n67;
                final long n69 = n55 ^ n67;
                final long n70 = (n48 ^ n56) & 0xFFFFFF0000000000L;
                final long n71 = n48 ^ n70;
                final long n72 = n56 ^ n70;
                final long n73 = (n49 ^ n57) & 0xFFFFFFFF00000000L;
                final long n74 = n49 ^ n73;
                final long n75 = n57 ^ n73;
                final long n76 = (n50 ^ n58) & 0xFFFFFFFF000000L;
                final long n77 = n50 ^ n76;
                final long n78 = n58 ^ n76;
                final long n79 = (n51 ^ n59) & 0xFFFFFFFFFF0000L;
                final long n80 = n51 ^ n79;
                final long n81 = n59 ^ n79;
                final long n82 = (n52 ^ n60) & 0xFFFFFFFFFFFF00L;
                final long n83 = n52 ^ n82;
                final long n84 = n60 ^ n82;
                final long n85 = (n62 ^ n74) & 0xFFFFFF00000000L;
                final long n86 = n62 ^ n85;
                final long n87 = n74 ^ n85;
                final long n88 = (n65 ^ n77) & 0xFFFFFFFFFF000000L;
                final long n89 = n65 ^ n88;
                final long n90 = n77 ^ n88;
                final long n91 = (n68 ^ n80) & 0xFF00FFFFFFFF0000L;
                final long n92 = n68 ^ n91;
                final long n93 = n80 ^ n91;
                final long n94 = (n71 ^ n83) & 0xFF0000FFFFFFFF00L;
                final long n95 = n71 ^ n94;
                final long n96 = n83 ^ n94;
                final long n97 = (n63 ^ n75) & 0xFFFFFF00000000L;
                final long n98 = n63 ^ n97;
                final long n99 = n75 ^ n97;
                final long n100 = (n66 ^ n78) & 0xFFFFFFFFFF000000L;
                final long n101 = n66 ^ n100;
                final long n102 = n78 ^ n100;
                final long n103 = (n69 ^ n81) & 0xFF00FFFFFFFF0000L;
                final long n104 = n69 ^ n103;
                final long n105 = n81 ^ n103;
                final long n106 = (n72 ^ n84) & 0xFF0000FFFFFFFF00L;
                final long n107 = n72 ^ n106;
                final long n108 = n84 ^ n106;
                final long n109 = (n86 ^ n92) & 0xFFFF0000FFFF0000L;
                final long n110 = n86 ^ n109;
                final long n111 = n92 ^ n109;
                final long n112 = (n89 ^ n95) & 0xFFFF0000FFFF00L;
                final long n113 = n89 ^ n112;
                final long n114 = n95 ^ n112;
                final long n115 = (n87 ^ n93) & 0xFFFF0000FFFF0000L;
                final long n116 = n87 ^ n115;
                final long n117 = n93 ^ n115;
                final long n118 = (n90 ^ n96) & 0xFFFF0000FFFF00L;
                final long n119 = n90 ^ n118;
                final long n120 = n96 ^ n118;
                final long n121 = (n98 ^ n104) & 0xFFFF0000FFFF0000L;
                final long n122 = n98 ^ n121;
                final long n123 = n104 ^ n121;
                final long n124 = (n101 ^ n107) & 0xFFFF0000FFFF00L;
                final long n125 = n101 ^ n124;
                final long n126 = n107 ^ n124;
                final long n127 = (n99 ^ n105) & 0xFFFF0000FFFF0000L;
                final long n128 = n99 ^ n127;
                final long n129 = n105 ^ n127;
                final long n130 = (n102 ^ n108) & 0xFFFF0000FFFF00L;
                final long n131 = n102 ^ n130;
                final long n132 = n108 ^ n130;
                final long n133 = (n110 ^ n113) & 0xFF00FF00FF00FF00L;
                final long n134 = n110 ^ n133;
                final long n135 = n113 ^ n133;
                final long n136 = (n111 ^ n114) & 0xFF00FF00FF00FF00L;
                final long n137 = n111 ^ n136;
                final long n138 = n114 ^ n136;
                final long n139 = (n116 ^ n119) & 0xFF00FF00FF00FF00L;
                final long n140 = n116 ^ n139;
                final long n141 = n119 ^ n139;
                final long n142 = (n117 ^ n120) & 0xFF00FF00FF00FF00L;
                final long n143 = n117 ^ n142;
                final long n144 = n120 ^ n142;
                final long n145 = (n122 ^ n125) & 0xFF00FF00FF00FF00L;
                final long n146 = n122 ^ n145;
                final long n147 = n125 ^ n145;
                final long n148 = (n123 ^ n126) & 0xFF00FF00FF00FF00L;
                final long n149 = n123 ^ n148;
                final long n150 = n126 ^ n148;
                final long n151 = (n128 ^ n131) & 0xFF00FF00FF00FF00L;
                final long n152 = n128 ^ n151;
                final long n153 = n131 ^ n151;
                final long n154 = (n129 ^ n132) & 0xFF00FF00FF00FF00L;
                final long n155 = n129 ^ n154;
                final long n156 = n132 ^ n154;
                array[0] = n134;
                array[1] = n135;
                array[2] = n137;
                array[3] = n138;
                array[4] = n140;
                array[5] = n141;
                array[6] = n143;
                array[7] = n144;
                array[8] = n146;
                array[9] = n147;
                array[10] = n149;
                array[11] = n150;
                array[12] = n152;
                array[13] = n153;
                array[14] = n155;
                array[15] = n156;
                break;
            }
            default: {
                throw new IllegalStateException("unsupported state size: only 512/1024 are allowed");
            }
        }
    }
    
    private void subBytes(final long[] array) {
        for (int i = 0; i < this.columns; ++i) {
            final long n = array[i];
            final int n2 = (int)n;
            final int n3 = (int)(n >>> 32);
            array[i] = (((long)((DSTU7564Digest.S0[n2 & 0xFF] & 0xFF) | (DSTU7564Digest.S1[n2 >>> 8 & 0xFF] & 0xFF) << 8 | (DSTU7564Digest.S2[n2 >>> 16 & 0xFF] & 0xFF) << 16 | DSTU7564Digest.S3[n2 >>> 24] << 24) & 0xFFFFFFFFL) | (long)((DSTU7564Digest.S0[n3 & 0xFF] & 0xFF) | (DSTU7564Digest.S1[n3 >>> 8 & 0xFF] & 0xFF) << 8 | (DSTU7564Digest.S2[n3 >>> 16 & 0xFF] & 0xFF) << 16 | DSTU7564Digest.S3[n3 >>> 24] << 24) << 32);
        }
    }
    
    public Memoable copy() {
        return new DSTU7564Digest(this);
    }
    
    public void reset(final Memoable memoable) {
        this.copyIn((DSTU7564Digest)memoable);
    }
    
    static {
        S0 = new byte[] { -88, 67, 95, 6, 107, 117, 108, 89, 113, -33, -121, -107, 23, -16, -40, 9, 109, -13, 29, -53, -55, 77, 44, -81, 121, -32, -105, -3, 111, 75, 69, 57, 62, -35, -93, 79, -76, -74, -102, 14, 31, -65, 21, -31, 73, -46, -109, -58, -110, 114, -98, 97, -47, 99, -6, -18, -12, 25, -43, -83, 88, -92, -69, -95, -36, -14, -125, 55, 66, -28, 122, 50, -100, -52, -85, 74, -113, 110, 4, 39, 46, -25, -30, 90, -106, 22, 35, 43, -62, 101, 102, 15, -68, -87, 71, 65, 52, 72, -4, -73, 106, -120, -91, 83, -122, -7, 91, -37, 56, 123, -61, 30, 34, 51, 36, 40, 54, -57, -78, 59, -114, 119, -70, -11, 20, -97, 8, 85, -101, 76, -2, 96, 92, -38, 24, 70, -51, 125, 33, -80, 63, 27, -119, -1, -21, -124, 105, 58, -99, -41, -45, 112, 103, 64, -75, -34, 93, 48, -111, -79, 120, 17, 1, -27, 0, 104, -104, -96, -59, 2, -90, 116, 45, 11, -94, 118, -77, -66, -50, -67, -82, -23, -118, 49, 28, -20, -15, -103, -108, -86, -10, 38, 47, -17, -24, -116, 53, 3, -44, 127, -5, 5, -63, 94, -112, 32, 61, -126, -9, -22, 10, 13, 126, -8, 80, 26, -60, 7, 87, -72, 60, 98, -29, -56, -84, 82, 100, 16, -48, -39, 19, 12, 18, 41, 81, -71, -49, -42, 115, -115, -127, 84, -64, -19, 78, 68, -89, 42, -123, 37, -26, -54, 124, -117, 86, -128 };
        S1 = new byte[] { -50, -69, -21, -110, -22, -53, 19, -63, -23, 58, -42, -78, -46, -112, 23, -8, 66, 21, 86, -76, 101, 28, -120, 67, -59, 92, 54, -70, -11, 87, 103, -115, 49, -10, 100, 88, -98, -12, 34, -86, 117, 15, 2, -79, -33, 109, 115, 77, 124, 38, 46, -9, 8, 93, 68, 62, -97, 20, -56, -82, 84, 16, -40, -68, 26, 107, 105, -13, -67, 51, -85, -6, -47, -101, 104, 78, 22, -107, -111, -18, 76, 99, -114, 91, -52, 60, 25, -95, -127, 73, 123, -39, 111, 55, 96, -54, -25, 43, 72, -3, -106, 69, -4, 65, 18, 13, 121, -27, -119, -116, -29, 32, 48, -36, -73, 108, 74, -75, 63, -105, -44, 98, 45, 6, -92, -91, -125, 95, 42, -38, -55, 0, 126, -94, 85, -65, 17, -43, -100, -49, 14, 10, 61, 81, 125, -109, 27, -2, -60, 71, 9, -122, 11, -113, -99, 106, 7, -71, -80, -104, 24, 50, 113, 75, -17, 59, 112, -96, -28, 64, -1, -61, -87, -26, 120, -7, -117, 70, -128, 30, 56, -31, -72, -88, -32, 12, 35, 118, 29, 37, 36, 5, -15, 110, -108, 40, -102, -124, -24, -93, 79, 119, -45, -123, -30, 82, -14, -126, 80, 122, 47, 116, 83, -77, 97, -81, 57, 53, -34, -51, 31, -103, -84, -83, 114, 44, -35, -48, -121, -66, 94, -90, -20, 4, -58, 3, 52, -5, -37, 89, -74, -62, 1, -16, 90, -19, -89, 102, 33, 127, -118, 39, -57, -64, 41, -41 };
        S2 = new byte[] { -109, -39, -102, -75, -104, 34, 69, -4, -70, 106, -33, 2, -97, -36, 81, 89, 74, 23, 43, -62, -108, -12, -69, -93, 98, -28, 113, -44, -51, 112, 22, -31, 73, 60, -64, -40, 92, -101, -83, -123, 83, -95, 122, -56, 45, -32, -47, 114, -90, 44, -60, -29, 118, 120, -73, -76, 9, 59, 14, 65, 76, -34, -78, -112, 37, -91, -41, 3, 17, 0, -61, 46, -110, -17, 78, 18, -99, 125, -53, 53, 16, -43, 79, -98, 77, -87, 85, -58, -48, 123, 24, -105, -45, 54, -26, 72, 86, -127, -113, 119, -52, -100, -71, -30, -84, -72, 47, 21, -92, 124, -38, 56, 30, 11, 5, -42, 20, 110, 108, 126, 102, -3, -79, -27, 96, -81, 94, 51, -121, -55, -16, 93, 109, 63, -120, -115, -57, -9, 29, -23, -20, -19, -128, 41, 39, -49, -103, -88, 80, 15, 55, 36, 40, 48, -107, -46, 62, 91, 64, -125, -77, 105, 87, 31, 7, 28, -118, -68, 32, -21, -50, -114, -85, -18, 49, -94, 115, -7, -54, 58, 26, -5, 13, -63, -2, -6, -14, 111, -67, -106, -35, 67, 82, -74, 8, -13, -82, -66, 25, -119, 50, 38, -80, -22, 75, 100, -124, -126, 107, -11, 121, -65, 1, 95, 117, 99, 27, 35, 61, 104, 42, 101, -24, -111, -10, -1, 19, 88, -15, 71, 10, 127, -59, -89, -25, 97, 90, 6, 70, 68, 66, 4, -96, -37, 57, -122, 84, -86, -116, 52, 33, -117, -8, 12, 116, 103 };
        S3 = new byte[] { 104, -115, -54, 77, 115, 75, 78, 42, -44, 82, 38, -77, 84, 30, 25, 31, 34, 3, 70, 61, 45, 74, 83, -125, 19, -118, -73, -43, 37, 121, -11, -67, 88, 47, 13, 2, -19, 81, -98, 17, -14, 62, 85, 94, -47, 22, 60, 102, 112, 93, -13, 69, 64, -52, -24, -108, 86, 8, -50, 26, 58, -46, -31, -33, -75, 56, 110, 14, -27, -12, -7, -122, -23, 79, -42, -123, 35, -49, 50, -103, 49, 20, -82, -18, -56, 72, -45, 48, -95, -110, 65, -79, 24, -60, 44, 113, 114, 68, 21, -3, 55, -66, 95, -86, -101, -120, -40, -85, -119, -100, -6, 96, -22, -68, 98, 12, 36, -90, -88, -20, 103, 32, -37, 124, 40, -35, -84, 91, 52, 126, 16, -15, 123, -113, 99, -96, 5, -102, 67, 119, 33, -65, 39, 9, -61, -97, -74, -41, 41, -62, -21, -64, -92, -117, -116, 29, -5, -1, -63, -78, -105, 46, -8, 101, -10, 117, 7, 4, 73, 51, -28, -39, -71, -48, 66, -57, 108, -112, 0, -114, 111, 80, 1, -59, -38, 71, 63, -51, 105, -94, -30, 122, -89, -58, -109, 15, 10, 6, -26, 43, -106, -93, 28, -81, 106, 18, -124, 57, -25, -80, -126, -9, -2, -99, -121, 92, -127, 53, -34, -76, -91, -4, -128, -17, -53, -69, 107, 118, -70, 90, 125, 120, 11, -107, -29, -83, 116, -104, 59, 54, 100, 109, -36, -16, 89, -87, 76, 23, 127, -111, -72, -55, 87, 27, -32, 97 };
    }
}
