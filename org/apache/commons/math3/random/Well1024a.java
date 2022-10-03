package org.apache.commons.math3.random;

public class Well1024a extends AbstractWell
{
    private static final long serialVersionUID = 5680173464174485492L;
    private static final int K = 1024;
    private static final int M1 = 3;
    private static final int M2 = 24;
    private static final int M3 = 10;
    
    public Well1024a() {
        super(1024, 3, 24, 10);
    }
    
    public Well1024a(final int seed) {
        super(1024, 3, 24, 10, seed);
    }
    
    public Well1024a(final int[] seed) {
        super(1024, 3, 24, 10, seed);
    }
    
    public Well1024a(final long seed) {
        super(1024, 3, 24, 10, seed);
    }
    
    @Override
    protected int next(final int bits) {
        final int indexRm1 = this.iRm1[this.index];
        final int v0 = this.v[this.index];
        final int vM1 = this.v[this.i1[this.index]];
        final int vM2 = this.v[this.i2[this.index]];
        final int vM3 = this.v[this.i3[this.index]];
        final int z0 = this.v[indexRm1];
        final int z2 = v0 ^ (vM1 ^ vM1 >>> 8);
        final int z3 = vM2 ^ vM2 << 19 ^ (vM3 ^ vM3 << 14);
        final int z4 = z2 ^ z3;
        final int z5 = z0 ^ z0 << 11 ^ (z2 ^ z2 << 7) ^ (z3 ^ z3 << 13);
        this.v[this.index] = z4;
        this.v[indexRm1] = z5;
        this.index = indexRm1;
        return z5 >>> 32 - bits;
    }
}
