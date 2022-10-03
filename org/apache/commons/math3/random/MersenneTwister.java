package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public class MersenneTwister extends BitsStreamGenerator implements Serializable
{
    private static final long serialVersionUID = 8661194735290153518L;
    private static final int N = 624;
    private static final int M = 397;
    private static final int[] MAG01;
    private int[] mt;
    private int mti;
    
    public MersenneTwister() {
        this.mt = new int[624];
        this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
    }
    
    public MersenneTwister(final int seed) {
        this.mt = new int[624];
        this.setSeed(seed);
    }
    
    public MersenneTwister(final int[] seed) {
        this.mt = new int[624];
        this.setSeed(seed);
    }
    
    public MersenneTwister(final long seed) {
        this.mt = new int[624];
        this.setSeed(seed);
    }
    
    @Override
    public void setSeed(final int seed) {
        long longMT = seed;
        this.mt[0] = (int)longMT;
        this.mti = 1;
        while (this.mti < 624) {
            longMT = (1812433253L * (longMT ^ longMT >> 30) + this.mti & 0xFFFFFFFFL);
            this.mt[this.mti] = (int)longMT;
            ++this.mti;
        }
        this.clear();
    }
    
    @Override
    public void setSeed(final int[] seed) {
        if (seed == null) {
            this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
            return;
        }
        this.setSeed(19650218);
        int i = 1;
        int j = 0;
        for (int k = FastMath.max(624, seed.length); k != 0; --k) {
            final long l0 = ((long)this.mt[i] & 0x7FFFFFFFL) | ((this.mt[i] < 0) ? 2147483648L : 0L);
            final long l2 = ((long)this.mt[i - 1] & 0x7FFFFFFFL) | ((this.mt[i - 1] < 0) ? 2147483648L : 0L);
            final long m = (l0 ^ (l2 ^ l2 >> 30) * 1664525L) + seed[j] + j;
            this.mt[i] = (int)(m & 0xFFFFFFFFL);
            ++i;
            ++j;
            if (i >= 624) {
                this.mt[0] = this.mt[623];
                i = 1;
            }
            if (j >= seed.length) {
                j = 0;
            }
        }
        for (int k = 623; k != 0; --k) {
            final long l0 = ((long)this.mt[i] & 0x7FFFFFFFL) | ((this.mt[i] < 0) ? 2147483648L : 0L);
            final long l2 = ((long)this.mt[i - 1] & 0x7FFFFFFFL) | ((this.mt[i - 1] < 0) ? 2147483648L : 0L);
            final long m = (l0 ^ (l2 ^ l2 >> 30) * 1566083941L) - i;
            this.mt[i] = (int)(m & 0xFFFFFFFFL);
            if (++i >= 624) {
                this.mt[0] = this.mt[623];
                i = 1;
            }
        }
        this.mt[0] = Integer.MIN_VALUE;
        this.clear();
    }
    
    @Override
    public void setSeed(final long seed) {
        this.setSeed(new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFFL) });
    }
    
    @Override
    protected int next(final int bits) {
        if (this.mti >= 624) {
            int mtNext = this.mt[0];
            for (int k = 0; k < 227; ++k) {
                final int mtCurr = mtNext;
                mtNext = this.mt[k + 1];
                final int y = (mtCurr & Integer.MIN_VALUE) | (mtNext & Integer.MAX_VALUE);
                this.mt[k] = (this.mt[k + 397] ^ y >>> 1 ^ MersenneTwister.MAG01[y & 0x1]);
            }
            for (int k = 227; k < 623; ++k) {
                final int mtCurr = mtNext;
                mtNext = this.mt[k + 1];
                final int y = (mtCurr & Integer.MIN_VALUE) | (mtNext & Integer.MAX_VALUE);
                this.mt[k] = (this.mt[k - 227] ^ y >>> 1 ^ MersenneTwister.MAG01[y & 0x1]);
            }
            final int y = (mtNext & Integer.MIN_VALUE) | (this.mt[0] & Integer.MAX_VALUE);
            this.mt[623] = (this.mt[396] ^ y >>> 1 ^ MersenneTwister.MAG01[y & 0x1]);
            this.mti = 0;
        }
        int y = this.mt[this.mti++];
        y ^= y >>> 11;
        y ^= (y << 7 & 0x9D2C5680);
        y ^= (y << 15 & 0xEFC60000);
        y ^= y >>> 18;
        return y >>> 32 - bits;
    }
    
    static {
        MAG01 = new int[] { 0, -1727483681 };
    }
}
