package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class WinternitzOTSVerify
{
    private Digest messDigestOTS;
    private int w;
    
    public WinternitzOTSVerify(final Digest messDigestOTS, final int w) {
        this.w = w;
        this.messDigestOTS = messDigestOTS;
    }
    
    public int getSignatureLength() {
        final int digestSize = this.messDigestOTS.getDigestSize();
        final int n = ((digestSize << 3) + (this.w - 1)) / this.w;
        return digestSize * (n + (this.getLog((n << this.w) + 1) + this.w - 1) / this.w);
    }
    
    public byte[] Verify(final byte[] array, final byte[] array2) {
        final int digestSize = this.messDigestOTS.getDigestSize();
        final byte[] array3 = new byte[digestSize];
        this.messDigestOTS.update(array, 0, array.length);
        final byte[] array4 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(array4, 0);
        final int n = ((digestSize << 3) + (this.w - 1)) / this.w;
        final int log = this.getLog((n << this.w) + 1);
        final int n2 = digestSize * (n + (log + this.w - 1) / this.w);
        if (n2 != array2.length) {
            return null;
        }
        final byte[] array5 = new byte[n2];
        int n3 = 0;
        int n4 = 0;
        if (8 % this.w == 0) {
            final int n5 = 8 / this.w;
            final int n6 = (1 << this.w) - 1;
            byte[] array6 = new byte[digestSize];
            for (int i = 0; i < array4.length; ++i) {
                for (int j = 0; j < n5; ++j) {
                    int k = array4[i] & n6;
                    n3 += k;
                    System.arraycopy(array2, n4 * digestSize, array6, 0, digestSize);
                    while (k < n6) {
                        this.messDigestOTS.update(array6, 0, array6.length);
                        array6 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(array6, 0);
                        ++k;
                    }
                    System.arraycopy(array6, 0, array5, n4 * digestSize, digestSize);
                    array4[i] >>>= (byte)this.w;
                    ++n4;
                }
            }
            int n7 = (n << this.w) - n3;
            for (int l = 0; l < log; l += this.w) {
                int n8 = n7 & n6;
                System.arraycopy(array2, n4 * digestSize, array6, 0, digestSize);
                while (n8 < n6) {
                    this.messDigestOTS.update(array6, 0, array6.length);
                    array6 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array6, 0);
                    ++n8;
                }
                System.arraycopy(array6, 0, array5, n4 * digestSize, digestSize);
                n7 >>>= this.w;
                ++n4;
            }
        }
        else if (this.w < 8) {
            final int n9 = digestSize / this.w;
            final int n10 = (1 << this.w) - 1;
            byte[] array7 = new byte[digestSize];
            int n11 = 0;
            for (int n12 = 0; n12 < n9; ++n12) {
                long n13 = 0L;
                for (int n14 = 0; n14 < this.w; ++n14) {
                    n13 ^= (array4[n11] & 0xFF) << (n14 << 3);
                    ++n11;
                }
                for (int n15 = 0; n15 < 8; ++n15) {
                    int n16 = (int)(n13 & (long)n10);
                    n3 += n16;
                    System.arraycopy(array2, n4 * digestSize, array7, 0, digestSize);
                    while (n16 < n10) {
                        this.messDigestOTS.update(array7, 0, array7.length);
                        array7 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(array7, 0);
                        ++n16;
                    }
                    System.arraycopy(array7, 0, array5, n4 * digestSize, digestSize);
                    n13 >>>= this.w;
                    ++n4;
                }
            }
            final int n17 = digestSize % this.w;
            long n18 = 0L;
            for (int n19 = 0; n19 < n17; ++n19) {
                n18 ^= (array4[n11] & 0xFF) << (n19 << 3);
                ++n11;
            }
            for (int n20 = n17 << 3, n21 = 0; n21 < n20; n21 += this.w) {
                int n22 = (int)(n18 & (long)n10);
                n3 += n22;
                System.arraycopy(array2, n4 * digestSize, array7, 0, digestSize);
                while (n22 < n10) {
                    this.messDigestOTS.update(array7, 0, array7.length);
                    array7 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array7, 0);
                    ++n22;
                }
                System.arraycopy(array7, 0, array5, n4 * digestSize, digestSize);
                n18 >>>= this.w;
                ++n4;
            }
            int n23 = (n << this.w) - n3;
            for (int n24 = 0; n24 < log; n24 += this.w) {
                int n25 = n23 & n10;
                System.arraycopy(array2, n4 * digestSize, array7, 0, digestSize);
                while (n25 < n10) {
                    this.messDigestOTS.update(array7, 0, array7.length);
                    array7 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array7, 0);
                    ++n25;
                }
                System.arraycopy(array7, 0, array5, n4 * digestSize, digestSize);
                n23 >>>= this.w;
                ++n4;
            }
        }
        else if (this.w < 57) {
            final int n26 = (digestSize << 3) - this.w;
            final int n27 = (1 << this.w) - 1;
            byte[] array8 = new byte[digestSize];
            int n28 = 0;
            while (n28 <= n26) {
                final int n29 = n28 >>> 3;
                final int n30 = n28 % 8;
                n28 += this.w;
                final int n31 = n28 + 7 >>> 3;
                long n32 = 0L;
                int n33 = 0;
                for (int n34 = n29; n34 < n31; ++n34) {
                    n32 ^= (array4[n34] & 0xFF) << (n33 << 3);
                    ++n33;
                }
                long n35 = n32 >>> n30 & (long)n27;
                n3 += (int)n35;
                System.arraycopy(array2, n4 * digestSize, array8, 0, digestSize);
                while (n35 < n27) {
                    this.messDigestOTS.update(array8, 0, array8.length);
                    array8 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array8, 0);
                    ++n35;
                }
                System.arraycopy(array8, 0, array5, n4 * digestSize, digestSize);
                ++n4;
            }
            final int n36 = n28 >>> 3;
            if (n36 < digestSize) {
                final int n37 = n28 % 8;
                long n38 = 0L;
                int n39 = 0;
                for (int n40 = n36; n40 < digestSize; ++n40) {
                    n38 ^= (array4[n40] & 0xFF) << (n39 << 3);
                    ++n39;
                }
                long n41 = n38 >>> n37 & (long)n27;
                n3 += (int)n41;
                System.arraycopy(array2, n4 * digestSize, array8, 0, digestSize);
                while (n41 < n27) {
                    this.messDigestOTS.update(array8, 0, array8.length);
                    array8 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array8, 0);
                    ++n41;
                }
                System.arraycopy(array8, 0, array5, n4 * digestSize, digestSize);
                ++n4;
            }
            int n42 = (n << this.w) - n3;
            for (int n43 = 0; n43 < log; n43 += this.w) {
                long n44 = n42 & n27;
                System.arraycopy(array2, n4 * digestSize, array8, 0, digestSize);
                while (n44 < n27) {
                    this.messDigestOTS.update(array8, 0, array8.length);
                    array8 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array8, 0);
                    ++n44;
                }
                System.arraycopy(array8, 0, array5, n4 * digestSize, digestSize);
                n42 >>>= this.w;
                ++n4;
            }
        }
        final byte[] array9 = new byte[digestSize];
        this.messDigestOTS.update(array5, 0, array5.length);
        final byte[] array10 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(array10, 0);
        return array10;
    }
    
    public int getLog(final int n) {
        int n2 = 1;
        for (int i = 2; i < n; i <<= 1, ++n2) {}
        return n2;
    }
}
