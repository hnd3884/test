package org.bouncycastle.pqc.crypto.gmss;

import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.pqc.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.crypto.Digest;

public class GMSSLeaf
{
    private Digest messDigestOTS;
    private int mdsize;
    private int keysize;
    private GMSSRandom gmssRandom;
    private byte[] leaf;
    private byte[] concHashs;
    private int i;
    private int j;
    private int two_power_w;
    private int w;
    private int steps;
    private byte[] seed;
    byte[] privateKeyOTS;
    
    public GMSSLeaf(final Digest messDigestOTS, final byte[][] array, final int[] array2) {
        this.i = array2[0];
        this.j = array2[1];
        this.steps = array2[2];
        this.w = array2[3];
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        final int n = (int)Math.ceil((this.mdsize << 3) / (double)this.w);
        this.keysize = n + (int)Math.ceil(this.getLog((n << this.w) + 1) / (double)this.w);
        this.two_power_w = 1 << this.w;
        this.privateKeyOTS = array[0];
        this.seed = array[1];
        this.concHashs = array[2];
        this.leaf = array[3];
    }
    
    GMSSLeaf(final Digest messDigestOTS, final int w, final int n) {
        this.w = w;
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        final int n2 = (int)Math.ceil((this.mdsize << 3) / (double)w);
        this.keysize = n2 + (int)Math.ceil(this.getLog((n2 << w) + 1) / (double)w);
        this.two_power_w = 1 << w;
        this.steps = (int)Math.ceil((((1 << w) - 1) * this.keysize + 1 + this.keysize) / (double)n);
        this.seed = new byte[this.mdsize];
        this.leaf = new byte[this.mdsize];
        this.privateKeyOTS = new byte[this.mdsize];
        this.concHashs = new byte[this.mdsize * this.keysize];
    }
    
    public GMSSLeaf(final Digest messDigestOTS, final int w, final int n, final byte[] array) {
        this.w = w;
        this.messDigestOTS = messDigestOTS;
        this.gmssRandom = new GMSSRandom(this.messDigestOTS);
        this.mdsize = this.messDigestOTS.getDigestSize();
        final int n2 = (int)Math.ceil((this.mdsize << 3) / (double)w);
        this.keysize = n2 + (int)Math.ceil(this.getLog((n2 << w) + 1) / (double)w);
        this.two_power_w = 1 << w;
        this.steps = (int)Math.ceil((((1 << w) - 1) * this.keysize + 1 + this.keysize) / (double)n);
        this.seed = new byte[this.mdsize];
        this.leaf = new byte[this.mdsize];
        this.privateKeyOTS = new byte[this.mdsize];
        this.concHashs = new byte[this.mdsize * this.keysize];
        this.initLeafCalc(array);
    }
    
    private GMSSLeaf(final GMSSLeaf gmssLeaf) {
        this.messDigestOTS = gmssLeaf.messDigestOTS;
        this.mdsize = gmssLeaf.mdsize;
        this.keysize = gmssLeaf.keysize;
        this.gmssRandom = gmssLeaf.gmssRandom;
        this.leaf = Arrays.clone(gmssLeaf.leaf);
        this.concHashs = Arrays.clone(gmssLeaf.concHashs);
        this.i = gmssLeaf.i;
        this.j = gmssLeaf.j;
        this.two_power_w = gmssLeaf.two_power_w;
        this.w = gmssLeaf.w;
        this.steps = gmssLeaf.steps;
        this.seed = Arrays.clone(gmssLeaf.seed);
        this.privateKeyOTS = Arrays.clone(gmssLeaf.privateKeyOTS);
    }
    
    void initLeafCalc(final byte[] array) {
        this.i = 0;
        this.j = 0;
        final byte[] array2 = new byte[this.mdsize];
        System.arraycopy(array, 0, array2, 0, this.seed.length);
        this.seed = this.gmssRandom.nextSeed(array2);
    }
    
    GMSSLeaf nextLeaf() {
        final GMSSLeaf gmssLeaf = new GMSSLeaf(this);
        gmssLeaf.updateLeafCalc();
        return gmssLeaf;
    }
    
    private void updateLeafCalc() {
        final byte[] privateKeyOTS = new byte[this.messDigestOTS.getDigestSize()];
        for (int i = 0; i < this.steps + 10000; ++i) {
            if (this.i == this.keysize && this.j == this.two_power_w - 1) {
                this.messDigestOTS.update(this.concHashs, 0, this.concHashs.length);
                this.leaf = new byte[this.messDigestOTS.getDigestSize()];
                this.messDigestOTS.doFinal(this.leaf, 0);
                return;
            }
            if (this.i == 0 || this.j == this.two_power_w - 1) {
                ++this.i;
                this.j = 0;
                this.privateKeyOTS = this.gmssRandom.nextSeed(this.seed);
            }
            else {
                this.messDigestOTS.update(this.privateKeyOTS, 0, this.privateKeyOTS.length);
                this.privateKeyOTS = privateKeyOTS;
                this.messDigestOTS.doFinal(this.privateKeyOTS, 0);
                ++this.j;
                if (this.j == this.two_power_w - 1) {
                    System.arraycopy(this.privateKeyOTS, 0, this.concHashs, this.mdsize * (this.i - 1), this.mdsize);
                }
            }
        }
        throw new IllegalStateException("unable to updateLeaf in steps: " + this.steps + " " + this.i + " " + this.j);
    }
    
    public byte[] getLeaf() {
        return Arrays.clone(this.leaf);
    }
    
    private int getLog(final int n) {
        int n2 = 1;
        for (int i = 2; i < n; i <<= 1, ++n2) {}
        return n2;
    }
    
    public byte[][] getStatByte() {
        final byte[][] array = { new byte[this.mdsize], new byte[this.mdsize], new byte[this.mdsize * this.keysize], new byte[this.mdsize] };
        array[0] = this.privateKeyOTS;
        array[1] = this.seed;
        array[2] = this.concHashs;
        array[3] = this.leaf;
        return array;
    }
    
    public int[] getStatInt() {
        return new int[] { this.i, this.j, this.steps, this.w };
    }
    
    @Override
    public String toString() {
        String string = "";
        for (int i = 0; i < 4; ++i) {
            string = string + this.getStatInt()[i] + " ";
        }
        String s = string + " " + this.mdsize + " " + this.keysize + " " + this.two_power_w + " ";
        final byte[][] statByte = this.getStatByte();
        for (int j = 0; j < 4; ++j) {
            if (statByte[j] != null) {
                s = s + new String(Hex.encode(statByte[j])) + " ";
            }
            else {
                s += "null ";
            }
        }
        return s;
    }
}
