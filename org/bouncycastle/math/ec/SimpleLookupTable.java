package org.bouncycastle.math.ec;

public class SimpleLookupTable implements ECLookupTable
{
    private final ECPoint[] points;
    
    private static ECPoint[] copy(final ECPoint[] array, final int n, final int n2) {
        final ECPoint[] array2 = new ECPoint[n2];
        for (int i = 0; i < n2; ++i) {
            array2[i] = array[n + i];
        }
        return array2;
    }
    
    public SimpleLookupTable(final ECPoint[] array, final int n, final int n2) {
        this.points = copy(array, n, n2);
    }
    
    public int getSize() {
        return this.points.length;
    }
    
    public ECPoint lookup(final int n) {
        return this.points[n];
    }
}
