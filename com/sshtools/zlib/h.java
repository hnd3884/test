package com.sshtools.zlib;

final class h
{
    static final int[] c;
    static final int[] g;
    static final int[] f;
    static final int[] d;
    static final int[] b;
    static final int[] e;
    
    static int b(final int[] array, final int n, int n2, final int n3, final int[] array2, final int[] array3, final int[] array4, final int[] array5, final int[] array6, final int[] array7, final int[] array8) {
        final int[] array9 = new int[16];
        final int[] array10 = new int[3];
        final int[] array11 = new int[15];
        final int[] array12 = new int[16];
        int n4 = 0;
        int n5 = n2;
        do {
            final int[] array13 = array9;
            final int n6 = array[n + n4];
            ++array13[n6];
            ++n4;
        } while (--n5 != 0);
        if (array9[0] == n2) {
            array4[0] = -1;
            return array5[0] = 0;
        }
        int n7 = array5[0];
        int i;
        for (i = 1; i <= 15 && array9[i] == 0; ++i) {}
        int j;
        if (n7 < (j = i)) {
            n7 = i;
        }
        int n8;
        for (n8 = 15; n8 != 0 && array9[n8] == 0; --n8) {}
        final int n9;
        if (n7 > (n9 = n8)) {
            n7 = n8;
        }
        array5[0] = n7;
        int n10 = 1 << i;
        while (i < n8) {
            final int n11;
            if ((n11 = n10 - array9[i]) < 0) {
                return -3;
            }
            ++i;
            n10 = n11 << 1;
        }
        final int n12;
        if ((n12 = n10 - array9[n8]) < 0) {
            return -3;
        }
        final int[] array14 = array9;
        final int n13 = n8;
        array14[n13] += n12;
        int n14 = array12[1] = 0;
        int n15 = 1;
        int n16 = 2;
        while (--n8 != 0) {
            n14 = (array12[n16] = n14 + array9[n15]);
            ++n16;
            ++n15;
        }
        int n17 = 0;
        int n18 = 0;
        do {
            final int n19;
            if ((n19 = array[n + n18]) != 0) {
                array8[array12[n19]++] = n17;
            }
            ++n18;
        } while (++n17 < n2);
        n2 = array12[n9];
        int n20 = array12[0] = 0;
        int n21 = 0;
        int n22 = -1;
        int n23 = -n7;
        array11[0] = 0;
        int n24 = 0;
        int n25 = 0;
        while (j <= n9) {
            int n26 = array9[j];
            while (n26-- != 0) {
                while (j > n23 + n7) {
                    ++n22;
                    n23 += n7;
                    final int n27 = n9 - n23;
                    final int n28 = (n27 > n7) ? n7 : n27;
                    int n30;
                    final int n29;
                    if ((n29 = 1 << (n30 = j - n23)) > n26 + 1) {
                        int n31 = n29 - (n26 + 1);
                        int n32 = j;
                        if (n30 < n28) {
                            while (++n30 < n28) {
                                final int n33;
                                if ((n33 = n31 << 1) <= array9[++n32]) {
                                    break;
                                }
                                n31 = n33 - array9[n32];
                            }
                        }
                    }
                    n25 = 1 << n30;
                    if (array7[0] + n25 > 1440) {
                        return -3;
                    }
                    n24 = (array11[n22] = array7[0]);
                    final int n34 = 0;
                    array7[n34] += n25;
                    if (n22 != 0) {
                        array12[n22] = n20;
                        array10[0] = (byte)n30;
                        array10[1] = (byte)n7;
                        final int n35 = n20 >>> n23 - n7;
                        array10[2] = n24 - array11[n22 - 1] - n35;
                        System.arraycopy(array10, 0, array6, (array11[n22 - 1] + n35) * 3, 3);
                    }
                    else {
                        array4[0] = n24;
                    }
                }
                array10[1] = (byte)(j - n23);
                if (n21 >= n2) {
                    array10[0] = 192;
                }
                else if (array8[n21] < n3) {
                    array10[0] = (byte)((array8[n21] < 256) ? 0 : 96);
                    array10[2] = array8[n21++];
                }
                else {
                    array10[0] = (byte)(array3[array8[n21] - n3] + 16 + 64);
                    array10[2] = array2[array8[n21++] - n3];
                }
                for (int n36 = 1 << j - n23, k = n20 >>> n23; k < n25; k += n36) {
                    System.arraycopy(array10, 0, array6, (n24 + k) * 3, 3);
                }
                int n37;
                for (n37 = 1 << j - 1; (n20 & n37) != 0x0; n20 ^= n37, n37 >>>= 1) {}
                n20 ^= n37;
                for (int n38 = (1 << n23) - 1; (n20 & n38) != array12[n22]; --n22, n23 -= n7, n38 = (1 << n23) - 1) {}
            }
            ++j;
        }
        return (n12 != 0 && n9 != 1) ? -5 : 0;
    }
    
    static int b(final int[] array, final int[] array2, final int[] array3, final int[] array4, final ZStream zStream) {
        int b = b(array, 0, 19, 19, null, null, array3, array2, array4, new int[1], new int[19]);
        if (b == -3) {
            zStream.msg = "oversubscribed dynamic bit lengths tree";
        }
        else if (b == -5 || array2[0] == 0) {
            zStream.msg = "incomplete dynamic bit lengths tree";
            b = -3;
        }
        return b;
    }
    
    static int b(final int n, final int n2, final int[] array, final int[] array2, final int[] array3, final int[] array4, final int[] array5, final int[] array6, final ZStream zStream) {
        final int[] array7 = { 0 };
        final int[] array8 = new int[288];
        int b = b(array, 0, n, 257, h.f, h.d, array4, array2, array6, array7, array8);
        if (b != 0 || array2[0] == 0) {
            if (b == -3) {
                zStream.msg = "oversubscribed literal/length tree";
            }
            else if (b != -4) {
                zStream.msg = "incomplete literal/length tree";
                b = -3;
            }
            return b;
        }
        int b2 = b(array, n, n2, 0, h.b, h.e, array5, array3, array6, array7, array8);
        if (b2 != 0 || (array3[0] == 0 && n > 257)) {
            if (b2 == -3) {
                zStream.msg = "oversubscribed distance tree";
            }
            else if (b2 == -5) {
                zStream.msg = "incomplete distance tree";
                b2 = -3;
            }
            else if (b2 != -4) {
                zStream.msg = "empty distance tree with lengths";
                b2 = -3;
            }
            return b2;
        }
        return 0;
    }
    
    static int b(final int[] array, final int[] array2, final int[][] array3, final int[][] array4, final ZStream zStream) {
        array[0] = 9;
        array2[0] = 5;
        array3[0] = h.c;
        array4[0] = h.g;
        return 0;
    }
    
    static {
        c = new int[] { 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, 192, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 160, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, 224, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 144, 83, 7, 59, 0, 8, 120, 0, 8, 56, 0, 9, 208, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, 176, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 240, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 200, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 168, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 232, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, 152, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, 216, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 184, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, 248, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7, 35, 0, 8, 114, 0, 8, 50, 0, 9, 196, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 164, 0, 8, 2, 0, 8, 130, 0, 8, 66, 0, 9, 228, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, 148, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 212, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, 180, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 244, 80, 7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, 204, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 172, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, 236, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 156, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9, 220, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 188, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 252, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0, 8, 113, 0, 8, 49, 0, 9, 194, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, 162, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 226, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, 146, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 210, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 178, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 242, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, 258, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, 202, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 170, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, 234, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, 154, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 218, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 186, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 250, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, 198, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, 166, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, 230, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 150, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 214, 82, 7, 19, 0, 8, 107, 0, 8, 43, 0, 9, 182, 0, 8, 11, 0, 8, 139, 0, 8, 75, 0, 9, 246, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, 206, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 174, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 238, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 158, 84, 7, 99, 0, 8, 127, 0, 8, 63, 0, 9, 222, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 190, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 254, 96, 7, 256, 0, 8, 80, 0, 8, 16, 84, 8, 115, 82, 7, 31, 0, 8, 112, 0, 8, 48, 0, 9, 193, 80, 7, 10, 0, 8, 96, 0, 8, 32, 0, 9, 161, 0, 8, 0, 0, 8, 128, 0, 8, 64, 0, 9, 225, 80, 7, 6, 0, 8, 88, 0, 8, 24, 0, 9, 145, 83, 7, 59, 0, 8, 120, 0, 8, 56, 0, 9, 209, 81, 7, 17, 0, 8, 104, 0, 8, 40, 0, 9, 177, 0, 8, 8, 0, 8, 136, 0, 8, 72, 0, 9, 241, 80, 7, 4, 0, 8, 84, 0, 8, 20, 85, 8, 227, 83, 7, 43, 0, 8, 116, 0, 8, 52, 0, 9, 201, 81, 7, 13, 0, 8, 100, 0, 8, 36, 0, 9, 169, 0, 8, 4, 0, 8, 132, 0, 8, 68, 0, 9, 233, 80, 7, 8, 0, 8, 92, 0, 8, 28, 0, 9, 153, 84, 7, 83, 0, 8, 124, 0, 8, 60, 0, 9, 217, 82, 7, 23, 0, 8, 108, 0, 8, 44, 0, 9, 185, 0, 8, 12, 0, 8, 140, 0, 8, 76, 0, 9, 249, 80, 7, 3, 0, 8, 82, 0, 8, 18, 85, 8, 163, 83, 7, 35, 0, 8, 114, 0, 8, 50, 0, 9, 197, 81, 7, 11, 0, 8, 98, 0, 8, 34, 0, 9, 165, 0, 8, 2, 0, 8, 130, 0, 8, 66, 0, 9, 229, 80, 7, 7, 0, 8, 90, 0, 8, 26, 0, 9, 149, 84, 7, 67, 0, 8, 122, 0, 8, 58, 0, 9, 213, 82, 7, 19, 0, 8, 106, 0, 8, 42, 0, 9, 181, 0, 8, 10, 0, 8, 138, 0, 8, 74, 0, 9, 245, 80, 7, 5, 0, 8, 86, 0, 8, 22, 192, 8, 0, 83, 7, 51, 0, 8, 118, 0, 8, 54, 0, 9, 205, 81, 7, 15, 0, 8, 102, 0, 8, 38, 0, 9, 173, 0, 8, 6, 0, 8, 134, 0, 8, 70, 0, 9, 237, 80, 7, 9, 0, 8, 94, 0, 8, 30, 0, 9, 157, 84, 7, 99, 0, 8, 126, 0, 8, 62, 0, 9, 221, 82, 7, 27, 0, 8, 110, 0, 8, 46, 0, 9, 189, 0, 8, 14, 0, 8, 142, 0, 8, 78, 0, 9, 253, 96, 7, 256, 0, 8, 81, 0, 8, 17, 85, 8, 131, 82, 7, 31, 0, 8, 113, 0, 8, 49, 0, 9, 195, 80, 7, 10, 0, 8, 97, 0, 8, 33, 0, 9, 163, 0, 8, 1, 0, 8, 129, 0, 8, 65, 0, 9, 227, 80, 7, 6, 0, 8, 89, 0, 8, 25, 0, 9, 147, 83, 7, 59, 0, 8, 121, 0, 8, 57, 0, 9, 211, 81, 7, 17, 0, 8, 105, 0, 8, 41, 0, 9, 179, 0, 8, 9, 0, 8, 137, 0, 8, 73, 0, 9, 243, 80, 7, 4, 0, 8, 85, 0, 8, 21, 80, 8, 258, 83, 7, 43, 0, 8, 117, 0, 8, 53, 0, 9, 203, 81, 7, 13, 0, 8, 101, 0, 8, 37, 0, 9, 171, 0, 8, 5, 0, 8, 133, 0, 8, 69, 0, 9, 235, 80, 7, 8, 0, 8, 93, 0, 8, 29, 0, 9, 155, 84, 7, 83, 0, 8, 125, 0, 8, 61, 0, 9, 219, 82, 7, 23, 0, 8, 109, 0, 8, 45, 0, 9, 187, 0, 8, 13, 0, 8, 141, 0, 8, 77, 0, 9, 251, 80, 7, 3, 0, 8, 83, 0, 8, 19, 85, 8, 195, 83, 7, 35, 0, 8, 115, 0, 8, 51, 0, 9, 199, 81, 7, 11, 0, 8, 99, 0, 8, 35, 0, 9, 167, 0, 8, 3, 0, 8, 131, 0, 8, 67, 0, 9, 231, 80, 7, 7, 0, 8, 91, 0, 8, 27, 0, 9, 151, 84, 7, 67, 0, 8, 123, 0, 8, 59, 0, 9, 215, 82, 7, 19, 0, 8, 107, 0, 8, 43, 0, 9, 183, 0, 8, 11, 0, 8, 139, 0, 8, 75, 0, 9, 247, 80, 7, 5, 0, 8, 87, 0, 8, 23, 192, 8, 0, 83, 7, 51, 0, 8, 119, 0, 8, 55, 0, 9, 207, 81, 7, 15, 0, 8, 103, 0, 8, 39, 0, 9, 175, 0, 8, 7, 0, 8, 135, 0, 8, 71, 0, 9, 239, 80, 7, 9, 0, 8, 95, 0, 8, 31, 0, 9, 159, 84, 7, 99, 0, 8, 127, 0, 8, 63, 0, 9, 223, 82, 7, 27, 0, 8, 111, 0, 8, 47, 0, 9, 191, 0, 8, 15, 0, 8, 143, 0, 8, 79, 0, 9, 255 };
        g = new int[] { 80, 5, 1, 87, 5, 257, 83, 5, 17, 91, 5, 4097, 81, 5, 5, 89, 5, 1025, 85, 5, 65, 93, 5, 16385, 80, 5, 3, 88, 5, 513, 84, 5, 33, 92, 5, 8193, 82, 5, 9, 90, 5, 2049, 86, 5, 129, 192, 5, 24577, 80, 5, 2, 87, 5, 385, 83, 5, 25, 91, 5, 6145, 81, 5, 7, 89, 5, 1537, 85, 5, 97, 93, 5, 24577, 80, 5, 4, 88, 5, 769, 84, 5, 49, 92, 5, 12289, 82, 5, 13, 90, 5, 3073, 86, 5, 193, 192, 5, 24577 };
        f = new int[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 13, 15, 17, 19, 23, 27, 31, 35, 43, 51, 59, 67, 83, 99, 115, 131, 163, 195, 227, 258, 0, 0 };
        d = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 0, 112, 112 };
        b = new int[] { 1, 2, 3, 4, 5, 7, 9, 13, 17, 25, 33, 49, 65, 97, 129, 193, 257, 385, 513, 769, 1025, 1537, 2049, 3073, 4097, 6145, 8193, 12289, 16385, 24577 };
        e = new int[] { 0, 0, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13 };
    }
}