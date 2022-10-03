package org.apache.commons.math3.random;

import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public abstract class AbstractWell extends BitsStreamGenerator implements Serializable
{
    private static final long serialVersionUID = -817701723016583596L;
    protected int index;
    protected final int[] v;
    protected final int[] iRm1;
    protected final int[] iRm2;
    protected final int[] i1;
    protected final int[] i2;
    protected final int[] i3;
    
    protected AbstractWell(final int k, final int m1, final int m2, final int m3) {
        this(k, m1, m2, m3, null);
    }
    
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final int seed) {
        this(k, m1, m2, m3, new int[] { seed });
    }
    
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final int[] seed) {
        final int w = 32;
        final int r = (k + 32 - 1) / 32;
        this.v = new int[r];
        this.index = 0;
        this.iRm1 = new int[r];
        this.iRm2 = new int[r];
        this.i1 = new int[r];
        this.i2 = new int[r];
        this.i3 = new int[r];
        for (int j = 0; j < r; ++j) {
            this.iRm1[j] = (j + r - 1) % r;
            this.iRm2[j] = (j + r - 2) % r;
            this.i1[j] = (j + m1) % r;
            this.i2[j] = (j + m2) % r;
            this.i3[j] = (j + m3) % r;
        }
        this.setSeed(seed);
    }
    
    protected AbstractWell(final int k, final int m1, final int m2, final int m3, final long seed) {
        this(k, m1, m2, m3, new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFFL) });
    }
    
    @Override
    public void setSeed(final int seed) {
        this.setSeed(new int[] { seed });
    }
    
    @Override
    public void setSeed(final int[] seed) {
        if (seed == null) {
            this.setSeed(System.currentTimeMillis() + System.identityHashCode(this));
            return;
        }
        System.arraycopy(seed, 0, this.v, 0, FastMath.min(seed.length, this.v.length));
        if (seed.length < this.v.length) {
            for (int i = seed.length; i < this.v.length; ++i) {
                final long l = this.v[i - seed.length];
                this.v[i] = (int)(1812433253L * (l ^ l >> 30) + i & 0xFFFFFFFFL);
            }
        }
        this.index = 0;
        this.clear();
    }
    
    @Override
    public void setSeed(final long seed) {
        this.setSeed(new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFFL) });
    }
    
    @Override
    protected abstract int next(final int p0);
}
