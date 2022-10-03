package com.sshtools.zlib;

final class b
{
    long b(final long n, final byte[] array, int n2, int i) {
        if (array == null) {
            return 1L;
        }
        long n3 = n & 0xFFFFL;
        long n4 = n >> 16 & 0xFFFFL;
        while (i > 0) {
            int j = (i < 5552) ? i : 5552;
            i -= j;
            while (j >= 16) {
                final long n5 = n3 + (array[n2++] & 0xFF);
                final long n6 = n4 + n5;
                final long n7 = n5 + (array[n2++] & 0xFF);
                final long n8 = n6 + n7;
                final long n9 = n7 + (array[n2++] & 0xFF);
                final long n10 = n8 + n9;
                final long n11 = n9 + (array[n2++] & 0xFF);
                final long n12 = n10 + n11;
                final long n13 = n11 + (array[n2++] & 0xFF);
                final long n14 = n12 + n13;
                final long n15 = n13 + (array[n2++] & 0xFF);
                final long n16 = n14 + n15;
                final long n17 = n15 + (array[n2++] & 0xFF);
                final long n18 = n16 + n17;
                final long n19 = n17 + (array[n2++] & 0xFF);
                final long n20 = n18 + n19;
                final long n21 = n19 + (array[n2++] & 0xFF);
                final long n22 = n20 + n21;
                final long n23 = n21 + (array[n2++] & 0xFF);
                final long n24 = n22 + n23;
                final long n25 = n23 + (array[n2++] & 0xFF);
                final long n26 = n24 + n25;
                final long n27 = n25 + (array[n2++] & 0xFF);
                final long n28 = n26 + n27;
                final long n29 = n27 + (array[n2++] & 0xFF);
                final long n30 = n28 + n29;
                final long n31 = n29 + (array[n2++] & 0xFF);
                final long n32 = n30 + n31;
                final long n33 = n31 + (array[n2++] & 0xFF);
                final long n34 = n32 + n33;
                n3 = n33 + (array[n2++] & 0xFF);
                n4 = n34 + n3;
                j -= 16;
            }
            if (j != 0) {
                do {
                    n3 += (array[n2++] & 0xFF);
                    n4 += n3;
                } while (--j != 0);
            }
            n3 %= 65521L;
            n4 %= 65521L;
        }
        return n4 << 16 | n3;
    }
}
