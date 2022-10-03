package org.tukaani.xz.rangecoder;

import java.io.IOException;

public abstract class RangeDecoder extends RangeCoder
{
    int range;
    int code;
    
    public RangeDecoder() {
        this.range = 0;
        this.code = 0;
    }
    
    public abstract void normalize() throws IOException;
    
    public int decodeBit(final short[] array, final int n) throws IOException {
        this.normalize();
        final short n2 = array[n];
        final int range = (this.range >>> 11) * n2;
        int n3;
        if ((this.code ^ Integer.MIN_VALUE) < (range ^ Integer.MIN_VALUE)) {
            this.range = range;
            array[n] = (short)(n2 + (2048 - n2 >>> 5));
            n3 = 0;
        }
        else {
            this.range -= range;
            this.code -= range;
            array[n] = (short)(n2 - (n2 >>> 5));
            n3 = 1;
        }
        return n3;
    }
    
    public int decodeBitTree(final short[] array) throws IOException {
        int i = 1;
        do {
            i = (i << 1 | this.decodeBit(array, i));
        } while (i < array.length);
        return i - array.length;
    }
    
    public int decodeReverseBitTree(final short[] array) throws IOException {
        int i = 1;
        int n = 0;
        int n2 = 0;
        do {
            final int decodeBit = this.decodeBit(array, i);
            i = (i << 1 | decodeBit);
            n2 |= decodeBit << n++;
        } while (i < array.length);
        return n2;
    }
    
    public int decodeDirectBits(int n) throws IOException {
        int n2 = 0;
        do {
            this.normalize();
            this.range >>>= 1;
            final int n3 = this.code - this.range >>> 31;
            this.code -= (this.range & n3 - 1);
            n2 = (n2 << 1 | 1 - n3);
        } while (--n != 0);
        return n2;
    }
}
