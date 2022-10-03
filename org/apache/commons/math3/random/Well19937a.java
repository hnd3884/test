package org.apache.commons.math3.random;

public class Well19937a extends AbstractWell
{
    private static final long serialVersionUID = -7462102162223815419L;
    private static final int K = 19937;
    private static final int M1 = 70;
    private static final int M2 = 179;
    private static final int M3 = 449;
    
    public Well19937a() {
        super(19937, 70, 179, 449);
    }
    
    public Well19937a(final int seed) {
        super(19937, 70, 179, 449, seed);
    }
    
    public Well19937a(final int[] seed) {
        super(19937, 70, 179, 449, seed);
    }
    
    public Well19937a(final long seed) {
        super(19937, 70, 179, 449, seed);
    }
    
    @Override
    protected int next(final int bits) {
        final int indexRm1 = this.iRm1[this.index];
        final int indexRm2 = this.iRm2[this.index];
        final int v0 = this.v[this.index];
        final int vM1 = this.v[this.i1[this.index]];
        final int vM2 = this.v[this.i2[this.index]];
        final int vM3 = this.v[this.i3[this.index]];
        final int z0 = (Integer.MIN_VALUE & this.v[indexRm1]) ^ (Integer.MAX_VALUE & this.v[indexRm2]);
        final int z2 = v0 ^ v0 << 25 ^ (vM1 ^ vM1 >>> 27);
        final int z3 = vM2 >>> 9 ^ (vM3 ^ vM3 >>> 1);
        final int z4 = z2 ^ z3;
        final int z5 = z0 ^ (z2 ^ z2 << 9) ^ (z3 ^ z3 << 21) ^ (z4 ^ z4 >>> 21);
        this.v[this.index] = z4;
        this.v[indexRm1] = z5;
        final int[] v2 = this.v;
        final int n = indexRm2;
        v2[n] &= Integer.MIN_VALUE;
        this.index = indexRm1;
        return z5 >>> 32 - bits;
    }
}
