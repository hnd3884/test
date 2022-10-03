package org.apache.commons.math3.random;

public class Well44497a extends AbstractWell
{
    private static final long serialVersionUID = -3859207588353972099L;
    private static final int K = 44497;
    private static final int M1 = 23;
    private static final int M2 = 481;
    private static final int M3 = 229;
    
    public Well44497a() {
        super(44497, 23, 481, 229);
    }
    
    public Well44497a(final int seed) {
        super(44497, 23, 481, 229, seed);
    }
    
    public Well44497a(final int[] seed) {
        super(44497, 23, 481, 229, seed);
    }
    
    public Well44497a(final long seed) {
        super(44497, 23, 481, 229, seed);
    }
    
    @Override
    protected int next(final int bits) {
        final int indexRm1 = this.iRm1[this.index];
        final int indexRm2 = this.iRm2[this.index];
        final int v0 = this.v[this.index];
        final int vM1 = this.v[this.i1[this.index]];
        final int vM2 = this.v[this.i2[this.index]];
        final int vM3 = this.v[this.i3[this.index]];
        final int z0 = (0xFFFF8000 & this.v[indexRm1]) ^ (0x7FFF & this.v[indexRm2]);
        final int z2 = v0 ^ v0 << 24 ^ (vM1 ^ vM1 >>> 30);
        final int z3 = vM2 ^ vM2 << 10 ^ vM3 << 26;
        final int z4 = z2 ^ z3;
        final int z2Prime = (z3 << 9 ^ z3 >>> 23) & 0xFBFFFFFF;
        final int z2Second = ((z3 & 0x20000) != 0x0) ? (z2Prime ^ 0xB729FCEC) : z2Prime;
        final int z5 = z0 ^ (z2 ^ z2 >>> 20) ^ z2Second ^ z4;
        this.v[this.index] = z4;
        this.v[indexRm1] = z5;
        final int[] v2 = this.v;
        final int n = indexRm2;
        v2[n] &= 0xFFFF8000;
        this.index = indexRm1;
        return z5 >>> 32 - bits;
    }
}
