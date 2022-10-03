package org.tukaani.xz.delta;

public class DeltaDecoder extends DeltaCoder
{
    public DeltaDecoder(final int n) {
        super(n);
    }
    
    public void decode(final byte[] array, final int n, final int n2) {
        for (int n3 = n + n2, i = n; i < n3; ++i) {
            final int n4 = i;
            array[n4] += this.history[this.distance + this.pos & 0xFF];
            this.history[this.pos-- & 0xFF] = array[i];
        }
    }
}
