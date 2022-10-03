package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.crypto.Digest;

public class GMSSRootSig
{
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private byte[] privateKeyOTS;
    private byte[] hash;
    private byte[] sign;
    private int w;
    private GMSSRandom gmssRandom;
    private int messagesize;
    private int k;
    private int r;
    private int test;
    private int counter;
    private int ii;
    private long test8;
    private long big8;
    private int steps;
    private int checksum;
    private int height;
    private byte[] seed;
    
    public GMSSRootSig(final Digest messDigestOTS, final byte[][] array, final int[] array2) {
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.counter = array2[0];
        this.test = array2[1];
        this.ii = array2[2];
        this.r = array2[3];
        this.steps = array2[4];
        this.keysize = array2[5];
        this.height = array2[6];
        this.w = array2[7];
        this.checksum = array2[8];
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.k = (1 << this.w) - 1;
        this.messagesize = (int)Math.ceil((this.mdsize << 3) / (double)this.w);
        this.privateKeyOTS = array[0];
        this.seed = array[1];
        this.hash = array[2];
        this.sign = array[3];
        this.test8 = ((long)(array[4][0] & 0xFF) | (long)(array[4][1] & 0xFF) << 8 | (long)(array[4][2] & 0xFF) << 16 | (long)(array[4][3] & 0xFF) << 24 | (long)(array[4][4] & 0xFF) << 32 | (long)(array[4][5] & 0xFF) << 40 | (long)(array[4][6] & 0xFF) << 48 | (long)(array[4][7] & 0xFF) << 56);
        this.big8 = ((long)(array[4][8] & 0xFF) | (long)(array[4][9] & 0xFF) << 8 | (long)(array[4][10] & 0xFF) << 16 | (long)(array[4][11] & 0xFF) << 24 | (long)(array[4][12] & 0xFF) << 32 | (long)(array[4][13] & 0xFF) << 40 | (long)(array[4][14] & 0xFF) << 48 | (long)(array[4][15] & 0xFF) << 56);
    }
    
    public GMSSRootSig(final Digest messDigestOTS, final int w, final int height) {
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        this.w = w;
        this.height = height;
        this.k = (1 << w) - 1;
        this.messagesize = (int)Math.ceil((this.mdsize << 3) / (double)w);
    }
    
    public void initSign(final byte[] array, final byte[] array2) {
        this.hash = new byte[this.mdsize];
        this.messDigestOTS.update(array2, 0, array2.length);
        this.hash = new byte[this.messDigestOTS.getDigestSize()];
        this.messDigestOTS.doFinal(this.hash, 0);
        final byte[] array3 = new byte[this.mdsize];
        System.arraycopy(this.hash, 0, array3, 0, this.mdsize);
        int n = 0;
        final int log = this.getLog((this.messagesize << this.w) + 1);
        if (8 % this.w == 0) {
            final int n2 = 8 / this.w;
            for (int i = 0; i < this.mdsize; ++i) {
                for (int j = 0; j < n2; ++j) {
                    n += (array3[i] & this.k);
                    array3[i] >>>= (byte)this.w;
                }
            }
            this.checksum = (this.messagesize << this.w) - n;
            int checksum = this.checksum;
            for (int k = 0; k < log; k += this.w) {
                n += (checksum & this.k);
                checksum >>>= this.w;
            }
        }
        else if (this.w < 8) {
            int n3 = 0;
            for (int n4 = this.mdsize / this.w, l = 0; l < n4; ++l) {
                long n5 = 0L;
                for (int n6 = 0; n6 < this.w; ++n6) {
                    n5 ^= (array3[n3] & 0xFF) << (n6 << 3);
                    ++n3;
                }
                for (int n7 = 0; n7 < 8; ++n7) {
                    n += (int)(n5 & (long)this.k);
                    n5 >>>= this.w;
                }
            }
            final int n8 = this.mdsize % this.w;
            long n9 = 0L;
            for (int n10 = 0; n10 < n8; ++n10) {
                n9 ^= (array3[n3] & 0xFF) << (n10 << 3);
                ++n3;
            }
            for (int n11 = n8 << 3, n12 = 0; n12 < n11; n12 += this.w) {
                n += (int)(n9 & (long)this.k);
                n9 >>>= this.w;
            }
            this.checksum = (this.messagesize << this.w) - n;
            int checksum2 = this.checksum;
            for (int n13 = 0; n13 < log; n13 += this.w) {
                n += (checksum2 & this.k);
                checksum2 >>>= this.w;
            }
        }
        else if (this.w < 57) {
            int n14 = 0;
            while (n14 <= (this.mdsize << 3) - this.w) {
                final int n15 = n14 >>> 3;
                final int n16 = n14 % 8;
                n14 += this.w;
                final int n17 = n14 + 7 >>> 3;
                long n18 = 0L;
                int n19 = 0;
                for (int n20 = n15; n20 < n17; ++n20) {
                    n18 ^= (array3[n20] & 0xFF) << (n19 << 3);
                    ++n19;
                }
                n += (int)(n18 >>> n16 & (long)this.k);
            }
            final int n21 = n14 >>> 3;
            if (n21 < this.mdsize) {
                final int n22 = n14 % 8;
                long n23 = 0L;
                int n24 = 0;
                for (int n25 = n21; n25 < this.mdsize; ++n25) {
                    n23 ^= (array3[n25] & 0xFF) << (n24 << 3);
                    ++n24;
                }
                n += (int)(n23 >>> n22 & (long)this.k);
            }
            this.checksum = (this.messagesize << this.w) - n;
            int checksum3 = this.checksum;
            for (int n26 = 0; n26 < log; n26 += this.w) {
                n += (checksum3 & this.k);
                checksum3 >>>= this.w;
            }
        }
        this.keysize = this.messagesize + (int)Math.ceil(log / (double)this.w);
        this.steps = (int)Math.ceil((this.keysize + n) / (double)(1 << this.height));
        this.sign = new byte[this.keysize * this.mdsize];
        this.counter = 0;
        this.test = 0;
        this.ii = 0;
        this.test8 = 0L;
        this.r = 0;
        this.privateKeyOTS = new byte[this.mdsize];
        System.arraycopy(array, 0, this.seed = new byte[this.mdsize], 0, this.mdsize);
    }
    
    public boolean updateSign() {
        for (int i = 0; i < this.steps; ++i) {
            if (this.counter < this.keysize) {
                this.oneStep();
            }
            if (this.counter == this.keysize) {
                return true;
            }
        }
        return false;
    }
    
    public byte[] getSig() {
        return this.sign;
    }
    
    private void oneStep() {
        if (8 % this.w == 0) {
            if (this.test == 0) {
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
                if (this.ii < this.mdsize) {
                    this.test = (this.hash[this.ii] & this.k);
                    this.hash[this.ii] >>>= (byte)this.w;
                }
                else {
                    this.test = (this.checksum & this.k);
                    this.checksum >>>= this.w;
                }
            }
            else if (this.test > 0) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                --this.test;
            }
            if (this.test == 0) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                ++this.counter;
                if (this.counter % (8 / this.w) == 0) {
                    ++this.ii;
                }
            }
        }
        else if (this.w < 8) {
            if (this.test == 0) {
                if (this.counter % 8 == 0 && this.ii < this.mdsize) {
                    this.big8 = 0L;
                    if (this.counter < this.mdsize / this.w << 3) {
                        for (int i = 0; i < this.w; ++i) {
                            this.big8 ^= (this.hash[this.ii] & 0xFF) << (i << 3);
                            ++this.ii;
                        }
                    }
                    else {
                        for (int j = 0; j < this.mdsize % this.w; ++j) {
                            this.big8 ^= (this.hash[this.ii] & 0xFF) << (j << 3);
                            ++this.ii;
                        }
                    }
                }
                if (this.counter == this.messagesize) {
                    this.big8 = this.checksum;
                }
                this.test = (int)(this.big8 & (long)this.k);
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            }
            else if (this.test > 0) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                --this.test;
            }
            if (this.test == 0) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                this.big8 >>>= this.w;
                ++this.counter;
            }
        }
        else if (this.w < 57) {
            if (this.test8 == 0L) {
                this.big8 = 0L;
                this.ii = 0;
                final int n = this.r % 8;
                final int n2 = this.r >>> 3;
                if (n2 < this.mdsize) {
                    int mdsize;
                    if (this.r <= (this.mdsize << 3) - this.w) {
                        this.r += this.w;
                        mdsize = this.r + 7 >>> 3;
                    }
                    else {
                        mdsize = this.mdsize;
                        this.r += this.w;
                    }
                    for (int k = n2; k < mdsize; ++k) {
                        this.big8 ^= (this.hash[k] & 0xFF) << (this.ii << 3);
                        ++this.ii;
                    }
                    this.big8 >>>= n;
                    this.test8 = (this.big8 & (long)this.k);
                }
                else {
                    this.test8 = (this.checksum & this.k);
                    this.checksum >>>= this.w;
                }
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            }
            else if (this.test8 > 0L) {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                --this.test8;
            }
            if (this.test8 == 0L) {
                System.arraycopy(this.privateKeyOTS, 0, this.sign, this.counter * this.mdsize, this.mdsize);
                ++this.counter;
            }
        }
    }
    
    public int getLog(final int n) {
        int n2 = 1;
        for (int i = 2; i < n; i <<= 1, ++n2) {}
        return n2;
    }
    
    public byte[][] getStatByte() {
        final byte[][] array = new byte[5][this.mdsize];
        array[0] = this.privateKeyOTS;
        array[1] = this.seed;
        array[2] = this.hash;
        array[3] = this.sign;
        array[4] = this.getStatLong();
        return array;
    }
    
    public int[] getStatInt() {
        return new int[] { this.counter, this.test, this.ii, this.r, this.steps, this.keysize, this.height, this.w, this.checksum };
    }
    
    public byte[] getStatLong() {
        return new byte[] { (byte)(this.test8 & 0xFFL), (byte)(this.test8 >> 8 & 0xFFL), (byte)(this.test8 >> 16 & 0xFFL), (byte)(this.test8 >> 24 & 0xFFL), (byte)(this.test8 >> 32 & 0xFFL), (byte)(this.test8 >> 40 & 0xFFL), (byte)(this.test8 >> 48 & 0xFFL), (byte)(this.test8 >> 56 & 0xFFL), (byte)(this.big8 & 0xFFL), (byte)(this.big8 >> 8 & 0xFFL), (byte)(this.big8 >> 16 & 0xFFL), (byte)(this.big8 >> 24 & 0xFFL), (byte)(this.big8 >> 32 & 0xFFL), (byte)(this.big8 >> 40 & 0xFFL), (byte)(this.big8 >> 48 & 0xFFL), (byte)(this.big8 >> 56 & 0xFFL) };
    }
    
    @Override
    public String toString() {
        String s = "" + this.big8 + "  ";
        final int[] array = new int[9];
        final int[] statInt = this.getStatInt();
        final byte[][] array2 = new byte[5][this.mdsize];
        final byte[][] statByte = this.getStatByte();
        for (int i = 0; i < 9; ++i) {
            s = s + statInt[i] + " ";
        }
        for (int j = 0; j < 5; ++j) {
            s = s + new String(Hex.encode(statByte[j])) + " ";
        }
        return s;
    }
}
