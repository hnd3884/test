package com.maverick.crypto.digests;

public class MD5Digest extends GeneralDigest
{
    private int h;
    private int g;
    private int f;
    private int e;
    private int[] j;
    private int i;
    
    public MD5Digest() {
        this.j = new int[16];
        this.reset();
    }
    
    public MD5Digest(final MD5Digest md5Digest) {
        super(md5Digest);
        this.j = new int[16];
        this.h = md5Digest.h;
        this.g = md5Digest.g;
        this.f = md5Digest.f;
        this.e = md5Digest.e;
        System.arraycopy(md5Digest.j, 0, this.j, 0, md5Digest.j.length);
        this.i = md5Digest.i;
    }
    
    public String getAlgorithmName() {
        return "MD5";
    }
    
    public int getDigestSize() {
        return 16;
    }
    
    protected void processWord(final byte[] array, final int n) {
        this.j[this.i++] = ((array[n] & 0xFF) | (array[n + 1] & 0xFF) << 8 | (array[n + 2] & 0xFF) << 16 | (array[n + 3] & 0xFF) << 24);
        if (this.i == 16) {
            this.processBlock();
        }
    }
    
    protected void processLength(final long n) {
        if (this.i > 14) {
            this.processBlock();
        }
        this.j[14] = (int)(n & -1L);
        this.j[15] = (int)(n >>> 32);
    }
    
    private void b(final int n, final byte[] array, final int n2) {
        array[n2] = (byte)n;
        array[n2 + 1] = (byte)(n >>> 8);
        array[n2 + 2] = (byte)(n >>> 16);
        array[n2 + 3] = (byte)(n >>> 24);
    }
    
    public int doFinal(final byte[] array, final int n) {
        this.finish();
        this.b(this.h, array, n);
        this.b(this.g, array, n + 4);
        this.b(this.f, array, n + 8);
        this.b(this.e, array, n + 12);
        this.reset();
        return 16;
    }
    
    public void reset() {
        super.reset();
        this.h = 1732584193;
        this.g = -271733879;
        this.f = -1732584194;
        this.e = 271733878;
        this.i = 0;
        for (int i = 0; i != this.j.length; ++i) {
            this.j[i] = 0;
        }
    }
    
    private int b(final int n, final int n2) {
        return n << n2 | n >>> 32 - n2;
    }
    
    private int b(final int n, final int n2, final int n3) {
        return (n & n2) | (~n & n3);
    }
    
    private int d(final int n, final int n2, final int n3) {
        return (n & n3) | (n2 & ~n3);
    }
    
    private int c(final int n, final int n2, final int n3) {
        return n ^ n2 ^ n3;
    }
    
    private int e(final int n, final int n2, final int n3) {
        return n2 ^ (n | ~n3);
    }
    
    protected void processBlock() {
        final int h = this.h;
        final int g = this.g;
        final int f = this.f;
        final int e = this.e;
        final int n = this.b(h + this.b(g, f, e) + this.j[0] - 680876936, 7) + g;
        final int n2 = this.b(e + this.b(n, g, f) + this.j[1] - 389564586, 12) + n;
        final int n3 = this.b(f + this.b(n2, n, g) + this.j[2] + 606105819, 17) + n2;
        final int n4 = this.b(g + this.b(n3, n2, n) + this.j[3] - 1044525330, 22) + n3;
        final int n5 = this.b(n + this.b(n4, n3, n2) + this.j[4] - 176418897, 7) + n4;
        final int n6 = this.b(n2 + this.b(n5, n4, n3) + this.j[5] + 1200080426, 12) + n5;
        final int n7 = this.b(n3 + this.b(n6, n5, n4) + this.j[6] - 1473231341, 17) + n6;
        final int n8 = this.b(n4 + this.b(n7, n6, n5) + this.j[7] - 45705983, 22) + n7;
        final int n9 = this.b(n5 + this.b(n8, n7, n6) + this.j[8] + 1770035416, 7) + n8;
        final int n10 = this.b(n6 + this.b(n9, n8, n7) + this.j[9] - 1958414417, 12) + n9;
        final int n11 = this.b(n7 + this.b(n10, n9, n8) + this.j[10] - 42063, 17) + n10;
        final int n12 = this.b(n8 + this.b(n11, n10, n9) + this.j[11] - 1990404162, 22) + n11;
        final int n13 = this.b(n9 + this.b(n12, n11, n10) + this.j[12] + 1804603682, 7) + n12;
        final int n14 = this.b(n10 + this.b(n13, n12, n11) + this.j[13] - 40341101, 12) + n13;
        final int n15 = this.b(n11 + this.b(n14, n13, n12) + this.j[14] - 1502002290, 17) + n14;
        final int n16 = this.b(n12 + this.b(n15, n14, n13) + this.j[15] + 1236535329, 22) + n15;
        final int n17 = this.b(n13 + this.d(n16, n15, n14) + this.j[1] - 165796510, 5) + n16;
        final int n18 = this.b(n14 + this.d(n17, n16, n15) + this.j[6] - 1069501632, 9) + n17;
        final int n19 = this.b(n15 + this.d(n18, n17, n16) + this.j[11] + 643717713, 14) + n18;
        final int n20 = this.b(n16 + this.d(n19, n18, n17) + this.j[0] - 373897302, 20) + n19;
        final int n21 = this.b(n17 + this.d(n20, n19, n18) + this.j[5] - 701558691, 5) + n20;
        final int n22 = this.b(n18 + this.d(n21, n20, n19) + this.j[10] + 38016083, 9) + n21;
        final int n23 = this.b(n19 + this.d(n22, n21, n20) + this.j[15] - 660478335, 14) + n22;
        final int n24 = this.b(n20 + this.d(n23, n22, n21) + this.j[4] - 405537848, 20) + n23;
        final int n25 = this.b(n21 + this.d(n24, n23, n22) + this.j[9] + 568446438, 5) + n24;
        final int n26 = this.b(n22 + this.d(n25, n24, n23) + this.j[14] - 1019803690, 9) + n25;
        final int n27 = this.b(n23 + this.d(n26, n25, n24) + this.j[3] - 187363961, 14) + n26;
        final int n28 = this.b(n24 + this.d(n27, n26, n25) + this.j[8] + 1163531501, 20) + n27;
        final int n29 = this.b(n25 + this.d(n28, n27, n26) + this.j[13] - 1444681467, 5) + n28;
        final int n30 = this.b(n26 + this.d(n29, n28, n27) + this.j[2] - 51403784, 9) + n29;
        final int n31 = this.b(n27 + this.d(n30, n29, n28) + this.j[7] + 1735328473, 14) + n30;
        final int n32 = this.b(n28 + this.d(n31, n30, n29) + this.j[12] - 1926607734, 20) + n31;
        final int n33 = this.b(n29 + this.c(n32, n31, n30) + this.j[5] - 378558, 4) + n32;
        final int n34 = this.b(n30 + this.c(n33, n32, n31) + this.j[8] - 2022574463, 11) + n33;
        final int n35 = this.b(n31 + this.c(n34, n33, n32) + this.j[11] + 1839030562, 16) + n34;
        final int n36 = this.b(n32 + this.c(n35, n34, n33) + this.j[14] - 35309556, 23) + n35;
        final int n37 = this.b(n33 + this.c(n36, n35, n34) + this.j[1] - 1530992060, 4) + n36;
        final int n38 = this.b(n34 + this.c(n37, n36, n35) + this.j[4] + 1272893353, 11) + n37;
        final int n39 = this.b(n35 + this.c(n38, n37, n36) + this.j[7] - 155497632, 16) + n38;
        final int n40 = this.b(n36 + this.c(n39, n38, n37) + this.j[10] - 1094730640, 23) + n39;
        final int n41 = this.b(n37 + this.c(n40, n39, n38) + this.j[13] + 681279174, 4) + n40;
        final int n42 = this.b(n38 + this.c(n41, n40, n39) + this.j[0] - 358537222, 11) + n41;
        final int n43 = this.b(n39 + this.c(n42, n41, n40) + this.j[3] - 722521979, 16) + n42;
        final int n44 = this.b(n40 + this.c(n43, n42, n41) + this.j[6] + 76029189, 23) + n43;
        final int n45 = this.b(n41 + this.c(n44, n43, n42) + this.j[9] - 640364487, 4) + n44;
        final int n46 = this.b(n42 + this.c(n45, n44, n43) + this.j[12] - 421815835, 11) + n45;
        final int n47 = this.b(n43 + this.c(n46, n45, n44) + this.j[15] + 530742520, 16) + n46;
        final int n48 = this.b(n44 + this.c(n47, n46, n45) + this.j[2] - 995338651, 23) + n47;
        final int n49 = this.b(n45 + this.e(n48, n47, n46) + this.j[0] - 198630844, 6) + n48;
        final int n50 = this.b(n46 + this.e(n49, n48, n47) + this.j[7] + 1126891415, 10) + n49;
        final int n51 = this.b(n47 + this.e(n50, n49, n48) + this.j[14] - 1416354905, 15) + n50;
        final int n52 = this.b(n48 + this.e(n51, n50, n49) + this.j[5] - 57434055, 21) + n51;
        final int n53 = this.b(n49 + this.e(n52, n51, n50) + this.j[12] + 1700485571, 6) + n52;
        final int n54 = this.b(n50 + this.e(n53, n52, n51) + this.j[3] - 1894986606, 10) + n53;
        final int n55 = this.b(n51 + this.e(n54, n53, n52) + this.j[10] - 1051523, 15) + n54;
        final int n56 = this.b(n52 + this.e(n55, n54, n53) + this.j[1] - 2054922799, 21) + n55;
        final int n57 = this.b(n53 + this.e(n56, n55, n54) + this.j[8] + 1873313359, 6) + n56;
        final int n58 = this.b(n54 + this.e(n57, n56, n55) + this.j[15] - 30611744, 10) + n57;
        final int n59 = this.b(n55 + this.e(n58, n57, n56) + this.j[6] - 1560198380, 15) + n58;
        final int n60 = this.b(n56 + this.e(n59, n58, n57) + this.j[13] + 1309151649, 21) + n59;
        final int n61 = this.b(n57 + this.e(n60, n59, n58) + this.j[4] - 145523070, 6) + n60;
        final int n62 = this.b(n58 + this.e(n61, n60, n59) + this.j[11] - 1120210379, 10) + n61;
        final int n63 = this.b(n59 + this.e(n62, n61, n60) + this.j[2] + 718787259, 15) + n62;
        final int n64 = this.b(n60 + this.e(n63, n62, n61) + this.j[9] - 343485551, 21) + n63;
        this.h += n61;
        this.g += n64;
        this.f += n63;
        this.e += n62;
        this.i = 0;
        for (int i = 0; i != this.j.length; ++i) {
            this.j[i] = 0;
        }
    }
}
