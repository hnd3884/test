package org.tukaani.xz.delta;

public class DeltaEncoder extends DeltaCoder
{
    public DeltaEncoder(final int n) {
        super(n);
    }
    
    public void encode(final byte[] array, final int n, final int n2, final byte[] array2) {
        for (int i = 0; i < n2; ++i) {
            final byte b = this.history[this.distance + this.pos & 0xFF];
            this.history[this.pos-- & 0xFF] = array[n + i];
            array2[i] = (byte)(array[n + i] - b);
        }
    }
}
