package org.apache.commons.math3.random;

public class Well512a extends AbstractWell
{
    private static final long serialVersionUID = -6104179812103820574L;
    private static final int K = 512;
    private static final int M1 = 13;
    private static final int M2 = 9;
    private static final int M3 = 5;
    
    public Well512a() {
        super(512, 13, 9, 5);
    }
    
    public Well512a(final int seed) {
        super(512, 13, 9, 5, seed);
    }
    
    public Well512a(final int[] seed) {
        super(512, 13, 9, 5, seed);
    }
    
    public Well512a(final long seed) {
        super(512, 13, 9, 5, seed);
    }
    
    @Override
    protected int next(final int bits) {
        final int indexRm1 = this.iRm1[this.index];
        final int vi = this.v[this.index];
        final int vi2 = this.v[this.i1[this.index]];
        final int vi3 = this.v[this.i2[this.index]];
        final int z0 = this.v[indexRm1];
        final int z2 = vi ^ vi << 16 ^ (vi2 ^ vi2 << 15);
        final int z3 = vi3 ^ vi3 >>> 11;
        final int z4 = z2 ^ z3;
        final int z5 = z0 ^ z0 << 2 ^ (z2 ^ z2 << 18) ^ z3 << 28 ^ (z4 ^ (z4 << 5 & 0xDA442D24));
        this.v[this.index] = z4;
        this.v[indexRm1] = z5;
        this.index = indexRm1;
        return z5 >>> 32 - bits;
    }
}
