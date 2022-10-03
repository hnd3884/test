package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class ISAACRandom extends BitsStreamGenerator implements Serializable
{
    private static final long serialVersionUID = 7288197941165002400L;
    private static final int SIZE_L = 8;
    private static final int SIZE = 256;
    private static final int H_SIZE = 128;
    private static final int MASK = 1020;
    private static final int GLD_RATIO = -1640531527;
    private final int[] rsl;
    private final int[] mem;
    private int count;
    private int isaacA;
    private int isaacB;
    private int isaacC;
    private final int[] arr;
    private int isaacX;
    private int isaacI;
    private int isaacJ;
    
    public ISAACRandom() {
        this.rsl = new int[256];
        this.mem = new int[256];
        this.arr = new int[8];
        this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }
    
    public ISAACRandom(final long seed) {
        this.rsl = new int[256];
        this.mem = new int[256];
        this.arr = new int[8];
        this.setSeed(seed);
    }
    
    public ISAACRandom(final int[] seed) {
        this.rsl = new int[256];
        this.mem = new int[256];
        this.arr = new int[8];
        this.setSeed(seed);
    }
    
    @Override
    public void setSeed(final int seed) {
        this.setSeed(new int[] { seed });
    }
    
    @Override
    public void setSeed(final long seed) {
        this.setSeed(new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFFL) });
    }
    
    @Override
    public void setSeed(final int[] seed) {
        if (seed == null) {
            this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
            return;
        }
        final int seedLen = seed.length;
        final int rslLen = this.rsl.length;
        System.arraycopy(seed, 0, this.rsl, 0, FastMath.min(seedLen, rslLen));
        if (seedLen < rslLen) {
            for (int j = seedLen; j < rslLen; ++j) {
                final long k = this.rsl[j - seedLen];
                this.rsl[j] = (int)(1812433253L * (k ^ k >> 30) + j & 0xFFFFFFFFL);
            }
        }
        this.initState();
    }
    
    @Override
    protected int next(final int bits) {
        if (this.count < 0) {
            this.isaac();
            this.count = 255;
        }
        return this.rsl[this.count--] >>> 32 - bits;
    }
    
    private void isaac() {
        this.isaacI = 0;
        this.isaacJ = 128;
        this.isaacB += ++this.isaacC;
        while (this.isaacI < 128) {
            this.isaac2();
        }
        this.isaacJ = 0;
        while (this.isaacJ < 128) {
            this.isaac2();
        }
    }
    
    private void isaac2() {
        this.isaacX = this.mem[this.isaacI];
        this.isaacA ^= this.isaacA << 13;
        this.isaacA += this.mem[this.isaacJ++];
        this.isaac3();
        this.isaacX = this.mem[this.isaacI];
        this.isaacA ^= this.isaacA >>> 6;
        this.isaacA += this.mem[this.isaacJ++];
        this.isaac3();
        this.isaacX = this.mem[this.isaacI];
        this.isaacA ^= this.isaacA << 2;
        this.isaacA += this.mem[this.isaacJ++];
        this.isaac3();
        this.isaacX = this.mem[this.isaacI];
        this.isaacA ^= this.isaacA >>> 16;
        this.isaacA += this.mem[this.isaacJ++];
        this.isaac3();
    }
    
    private void isaac3() {
        this.mem[this.isaacI] = this.mem[(this.isaacX & 0x3FC) >> 2] + this.isaacA + this.isaacB;
        this.isaacB = this.mem[(this.mem[this.isaacI] >> 8 & 0x3FC) >> 2] + this.isaacX;
        this.rsl[this.isaacI++] = this.isaacB;
    }
    
    private void initState() {
        this.isaacA = 0;
        this.isaacB = 0;
        this.isaacC = 0;
        for (int j = 0; j < this.arr.length; ++j) {
            this.arr[j] = -1640531527;
        }
        for (int j = 0; j < 4; ++j) {
            this.shuffle();
        }
        for (int j = 0; j < 256; j += 8) {
            final int[] arr = this.arr;
            final int n = 0;
            arr[n] += this.rsl[j];
            final int[] arr2 = this.arr;
            final int n2 = 1;
            arr2[n2] += this.rsl[j + 1];
            final int[] arr3 = this.arr;
            final int n3 = 2;
            arr3[n3] += this.rsl[j + 2];
            final int[] arr4 = this.arr;
            final int n4 = 3;
            arr4[n4] += this.rsl[j + 3];
            final int[] arr5 = this.arr;
            final int n5 = 4;
            arr5[n5] += this.rsl[j + 4];
            final int[] arr6 = this.arr;
            final int n6 = 5;
            arr6[n6] += this.rsl[j + 5];
            final int[] arr7 = this.arr;
            final int n7 = 6;
            arr7[n7] += this.rsl[j + 6];
            final int[] arr8 = this.arr;
            final int n8 = 7;
            arr8[n8] += this.rsl[j + 7];
            this.shuffle();
            this.setState(j);
        }
        for (int j = 0; j < 256; j += 8) {
            final int[] arr9 = this.arr;
            final int n9 = 0;
            arr9[n9] += this.mem[j];
            final int[] arr10 = this.arr;
            final int n10 = 1;
            arr10[n10] += this.mem[j + 1];
            final int[] arr11 = this.arr;
            final int n11 = 2;
            arr11[n11] += this.mem[j + 2];
            final int[] arr12 = this.arr;
            final int n12 = 3;
            arr12[n12] += this.mem[j + 3];
            final int[] arr13 = this.arr;
            final int n13 = 4;
            arr13[n13] += this.mem[j + 4];
            final int[] arr14 = this.arr;
            final int n14 = 5;
            arr14[n14] += this.mem[j + 5];
            final int[] arr15 = this.arr;
            final int n15 = 6;
            arr15[n15] += this.mem[j + 6];
            final int[] arr16 = this.arr;
            final int n16 = 7;
            arr16[n16] += this.mem[j + 7];
            this.shuffle();
            this.setState(j);
        }
        this.isaac();
        this.count = 255;
        this.clear();
    }
    
    private void shuffle() {
        final int[] arr = this.arr;
        final int n = 0;
        arr[n] ^= this.arr[1] << 11;
        final int[] arr2 = this.arr;
        final int n2 = 3;
        arr2[n2] += this.arr[0];
        final int[] arr3 = this.arr;
        final int n3 = 1;
        arr3[n3] += this.arr[2];
        final int[] arr4 = this.arr;
        final int n4 = 1;
        arr4[n4] ^= this.arr[2] >>> 2;
        final int[] arr5 = this.arr;
        final int n5 = 4;
        arr5[n5] += this.arr[1];
        final int[] arr6 = this.arr;
        final int n6 = 2;
        arr6[n6] += this.arr[3];
        final int[] arr7 = this.arr;
        final int n7 = 2;
        arr7[n7] ^= this.arr[3] << 8;
        final int[] arr8 = this.arr;
        final int n8 = 5;
        arr8[n8] += this.arr[2];
        final int[] arr9 = this.arr;
        final int n9 = 3;
        arr9[n9] += this.arr[4];
        final int[] arr10 = this.arr;
        final int n10 = 3;
        arr10[n10] ^= this.arr[4] >>> 16;
        final int[] arr11 = this.arr;
        final int n11 = 6;
        arr11[n11] += this.arr[3];
        final int[] arr12 = this.arr;
        final int n12 = 4;
        arr12[n12] += this.arr[5];
        final int[] arr13 = this.arr;
        final int n13 = 4;
        arr13[n13] ^= this.arr[5] << 10;
        final int[] arr14 = this.arr;
        final int n14 = 7;
        arr14[n14] += this.arr[4];
        final int[] arr15 = this.arr;
        final int n15 = 5;
        arr15[n15] += this.arr[6];
        final int[] arr16 = this.arr;
        final int n16 = 5;
        arr16[n16] ^= this.arr[6] >>> 4;
        final int[] arr17 = this.arr;
        final int n17 = 0;
        arr17[n17] += this.arr[5];
        final int[] arr18 = this.arr;
        final int n18 = 6;
        arr18[n18] += this.arr[7];
        final int[] arr19 = this.arr;
        final int n19 = 6;
        arr19[n19] ^= this.arr[7] << 8;
        final int[] arr20 = this.arr;
        final int n20 = 1;
        arr20[n20] += this.arr[6];
        final int[] arr21 = this.arr;
        final int n21 = 7;
        arr21[n21] += this.arr[0];
        final int[] arr22 = this.arr;
        final int n22 = 7;
        arr22[n22] ^= this.arr[0] >>> 9;
        final int[] arr23 = this.arr;
        final int n23 = 2;
        arr23[n23] += this.arr[7];
        final int[] arr24 = this.arr;
        final int n24 = 0;
        arr24[n24] += this.arr[1];
    }
    
    private void setState(final int start) {
        this.mem[start] = this.arr[0];
        this.mem[start + 1] = this.arr[1];
        this.mem[start + 2] = this.arr[2];
        this.mem[start + 3] = this.arr[3];
        this.mem[start + 4] = this.arr[4];
        this.mem[start + 5] = this.arr[5];
        this.mem[start + 6] = this.arr[6];
        this.mem[start + 7] = this.arr[7];
    }
}
