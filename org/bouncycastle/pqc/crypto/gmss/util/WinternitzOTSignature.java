package org.bouncycastle.pqc.crypto.gmss.util;

import org.bouncycastle.crypto.Digest;

public class WinternitzOTSignature
{
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private byte[][] privateKeyOTS;
    private int w;
    private GMSSRandom gmssRandom;
    private int messagesize;
    private int checksumsize;
    
    public WinternitzOTSignature(final byte[] array, final Digest messDigestOTS, final int w) {
        this.w = w;
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.messagesize = (int)Math.ceil((this.mdsize << 3) / (double)w);
        this.checksumsize = this.getLog((this.messagesize << w) + 1);
        this.keysize = this.messagesize + (int)Math.ceil(this.checksumsize / (double)w);
        this.privateKeyOTS = new byte[this.keysize][this.mdsize];
        final byte[] array2 = new byte[this.mdsize];
        System.arraycopy(array, 0, array2, 0, array2.length);
        for (int i = 0; i < this.keysize; ++i) {
            this.privateKeyOTS[i] = this.gmssRandom.nextSeed(array2);
        }
    }
    
    public byte[][] getPrivateKey() {
        return this.privateKeyOTS;
    }
    
    public byte[] getPublicKey() {
        final byte[] array = new byte[this.keysize * this.mdsize];
        final byte[] array2 = new byte[this.mdsize];
        final int n = 1 << this.w;
        for (int i = 0; i < this.keysize; ++i) {
            this.messDigestOTS.update(this.privateKeyOTS[i], 0, this.privateKeyOTS[i].length);
            byte[] array3 = new byte[this.messDigestOTS.getDigestSize()];
            this.messDigestOTS.doFinal(array3, 0);
            for (int j = 2; j < n; ++j) {
                this.messDigestOTS.update(array3, 0, array3.length);
                array3 = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(array3, 0);
            }
            System.arraycopy(array3, 0, array, this.mdsize * i, this.mdsize);
        }
        this.messDigestOTS.update(array, 0, array.length);
        final byte[] array4 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(array4, 0);
        return array4;
    }
    
    public byte[] getSignature(final byte[] array) {
        final byte[] array2 = new byte[this.keysize * this.mdsize];
        final byte[] array3 = new byte[this.mdsize];
        int n = 0;
        int n2 = 0;
        this.messDigestOTS.update(array, 0, array.length);
        final byte[] array4 = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(array4, 0);
        if (8 % this.w == 0) {
            final int n3 = 8 / this.w;
            final int n4 = (1 << this.w) - 1;
            byte[] array5 = new byte[this.mdsize];
            for (int i = 0; i < array4.length; ++i) {
                for (int j = 0; j < n3; ++j) {
                    int k = array4[i] & n4;
                    n2 += k;
                    System.arraycopy(this.privateKeyOTS[n], 0, array5, 0, this.mdsize);
                    while (k > 0) {
                        this.messDigestOTS.update(array5, 0, array5.length);
                        array5 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(array5, 0);
                        --k;
                    }
                    System.arraycopy(array5, 0, array2, n * this.mdsize, this.mdsize);
                    array4[i] >>>= (byte)this.w;
                    ++n;
                }
            }
            int n5 = (this.messagesize << this.w) - n2;
            for (int l = 0; l < this.checksumsize; l += this.w) {
                int n6 = n5 & n4;
                System.arraycopy(this.privateKeyOTS[n], 0, array5, 0, this.mdsize);
                while (n6 > 0) {
                    this.messDigestOTS.update(array5, 0, array5.length);
                    array5 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array5, 0);
                    --n6;
                }
                System.arraycopy(array5, 0, array2, n * this.mdsize, this.mdsize);
                n5 >>>= this.w;
                ++n;
            }
        }
        else if (this.w < 8) {
            final int n7 = this.mdsize / this.w;
            final int n8 = (1 << this.w) - 1;
            byte[] array6 = new byte[this.mdsize];
            int n9 = 0;
            for (int n10 = 0; n10 < n7; ++n10) {
                long n11 = 0L;
                for (int n12 = 0; n12 < this.w; ++n12) {
                    n11 ^= (array4[n9] & 0xFF) << (n12 << 3);
                    ++n9;
                }
                for (int n13 = 0; n13 < 8; ++n13) {
                    int n14 = (int)(n11 & (long)n8);
                    n2 += n14;
                    System.arraycopy(this.privateKeyOTS[n], 0, array6, 0, this.mdsize);
                    while (n14 > 0) {
                        this.messDigestOTS.update(array6, 0, array6.length);
                        array6 = new byte[this.messDigestOTS.getDigestSize()];
                        this.messDigestOTS.doFinal(array6, 0);
                        --n14;
                    }
                    System.arraycopy(array6, 0, array2, n * this.mdsize, this.mdsize);
                    n11 >>>= this.w;
                    ++n;
                }
            }
            final int n15 = this.mdsize % this.w;
            long n16 = 0L;
            for (int n17 = 0; n17 < n15; ++n17) {
                n16 ^= (array4[n9] & 0xFF) << (n17 << 3);
                ++n9;
            }
            for (int n18 = n15 << 3, n19 = 0; n19 < n18; n19 += this.w) {
                int n20 = (int)(n16 & (long)n8);
                n2 += n20;
                System.arraycopy(this.privateKeyOTS[n], 0, array6, 0, this.mdsize);
                while (n20 > 0) {
                    this.messDigestOTS.update(array6, 0, array6.length);
                    array6 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array6, 0);
                    --n20;
                }
                System.arraycopy(array6, 0, array2, n * this.mdsize, this.mdsize);
                n16 >>>= this.w;
                ++n;
            }
            int n21 = (this.messagesize << this.w) - n2;
            for (int n22 = 0; n22 < this.checksumsize; n22 += this.w) {
                int n23 = n21 & n8;
                System.arraycopy(this.privateKeyOTS[n], 0, array6, 0, this.mdsize);
                while (n23 > 0) {
                    this.messDigestOTS.update(array6, 0, array6.length);
                    array6 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array6, 0);
                    --n23;
                }
                System.arraycopy(array6, 0, array2, n * this.mdsize, this.mdsize);
                n21 >>>= this.w;
                ++n;
            }
        }
        else if (this.w < 57) {
            final int n24 = (this.mdsize << 3) - this.w;
            final int n25 = (1 << this.w) - 1;
            byte[] array7 = new byte[this.mdsize];
            int n26 = 0;
            while (n26 <= n24) {
                final int n27 = n26 >>> 3;
                final int n28 = n26 % 8;
                n26 += this.w;
                final int n29 = n26 + 7 >>> 3;
                long n30 = 0L;
                int n31 = 0;
                for (int n32 = n27; n32 < n29; ++n32) {
                    n30 ^= (array4[n32] & 0xFF) << (n31 << 3);
                    ++n31;
                }
                long n33 = n30 >>> n28 & (long)n25;
                n2 += (int)n33;
                System.arraycopy(this.privateKeyOTS[n], 0, array7, 0, this.mdsize);
                while (n33 > 0L) {
                    this.messDigestOTS.update(array7, 0, array7.length);
                    array7 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array7, 0);
                    --n33;
                }
                System.arraycopy(array7, 0, array2, n * this.mdsize, this.mdsize);
                ++n;
            }
            final int n34 = n26 >>> 3;
            if (n34 < this.mdsize) {
                final int n35 = n26 % 8;
                long n36 = 0L;
                int n37 = 0;
                for (int n38 = n34; n38 < this.mdsize; ++n38) {
                    n36 ^= (array4[n38] & 0xFF) << (n37 << 3);
                    ++n37;
                }
                long n39 = n36 >>> n35 & (long)n25;
                n2 += (int)n39;
                System.arraycopy(this.privateKeyOTS[n], 0, array7, 0, this.mdsize);
                while (n39 > 0L) {
                    this.messDigestOTS.update(array7, 0, array7.length);
                    array7 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array7, 0);
                    --n39;
                }
                System.arraycopy(array7, 0, array2, n * this.mdsize, this.mdsize);
                ++n;
            }
            int n40 = (this.messagesize << this.w) - n2;
            for (int n41 = 0; n41 < this.checksumsize; n41 += this.w) {
                long n42 = n40 & n25;
                System.arraycopy(this.privateKeyOTS[n], 0, array7, 0, this.mdsize);
                while (n42 > 0L) {
                    this.messDigestOTS.update(array7, 0, array7.length);
                    array7 = new byte[this.messDigestOTS.getDigestSize()];
                    this.messDigestOTS.doFinal(array7, 0);
                    --n42;
                }
                System.arraycopy(array7, 0, array2, n * this.mdsize, this.mdsize);
                n40 >>>= this.w;
                ++n;
            }
        }
        return array2;
    }
    
    public int getLog(final int n) {
        int n2 = 1;
        for (int i = 2; i < n; i <<= 1, ++n2) {}
        return n2;
    }
}
