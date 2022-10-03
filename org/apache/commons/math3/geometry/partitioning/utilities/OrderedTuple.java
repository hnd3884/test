package org.apache.commons.math3.geometry.partitioning.utilities;

import java.util.Arrays;
import org.apache.commons.math3.util.FastMath;

@Deprecated
public class OrderedTuple implements Comparable<OrderedTuple>
{
    private static final long SIGN_MASK = Long.MIN_VALUE;
    private static final long EXPONENT_MASK = 9218868437227405312L;
    private static final long MANTISSA_MASK = 4503599627370495L;
    private static final long IMPLICIT_ONE = 4503599627370496L;
    private double[] components;
    private int offset;
    private int lsb;
    private long[] encoding;
    private boolean posInf;
    private boolean negInf;
    private boolean nan;
    
    public OrderedTuple(final double... components) {
        this.components = components.clone();
        int msb = Integer.MIN_VALUE;
        this.lsb = Integer.MAX_VALUE;
        this.posInf = false;
        this.negInf = false;
        this.nan = false;
        for (int i = 0; i < components.length; ++i) {
            if (Double.isInfinite(components[i])) {
                if (components[i] < 0.0) {
                    this.negInf = true;
                }
                else {
                    this.posInf = true;
                }
            }
            else if (Double.isNaN(components[i])) {
                this.nan = true;
            }
            else {
                final long b = Double.doubleToLongBits(components[i]);
                final long m = mantissa(b);
                if (m != 0L) {
                    final int e = exponent(b);
                    msb = FastMath.max(msb, e + computeMSB(m));
                    this.lsb = FastMath.min(this.lsb, e + computeLSB(m));
                }
            }
        }
        if (this.posInf && this.negInf) {
            this.posInf = false;
            this.negInf = false;
            this.nan = true;
        }
        if (this.lsb <= msb) {
            this.encode(msb + 16);
        }
        else {
            this.encoding = new long[] { 0L };
        }
    }
    
    private void encode(final int minOffset) {
        this.offset = minOffset + 31;
        this.offset -= this.offset % 32;
        if (this.encoding != null && this.encoding.length == 1 && this.encoding[0] == 0L) {
            return;
        }
        final int neededBits = this.offset + 1 - this.lsb;
        final int neededLongs = (neededBits + 62) / 63;
        this.encoding = new long[this.components.length * neededLongs];
        int eIndex = 0;
        int shift = 62;
        long word = 0L;
        int k = this.offset;
        while (eIndex < this.encoding.length) {
            for (int vIndex = 0; vIndex < this.components.length; ++vIndex) {
                if (this.getBit(vIndex, k) != 0) {
                    word |= 1L << shift;
                }
                if (shift-- == 0) {
                    this.encoding[eIndex++] = word;
                    word = 0L;
                    shift = 62;
                }
            }
            --k;
        }
    }
    
    public int compareTo(final OrderedTuple ot) {
        if (this.components.length != ot.components.length) {
            return this.components.length - ot.components.length;
        }
        if (this.nan) {
            return 1;
        }
        if (ot.nan) {
            return -1;
        }
        if (this.negInf || ot.posInf) {
            return -1;
        }
        if (this.posInf || ot.negInf) {
            return 1;
        }
        if (this.offset < ot.offset) {
            this.encode(ot.offset);
        }
        else if (this.offset > ot.offset) {
            ot.encode(this.offset);
        }
        for (int limit = FastMath.min(this.encoding.length, ot.encoding.length), i = 0; i < limit; ++i) {
            if (this.encoding[i] < ot.encoding[i]) {
                return -1;
            }
            if (this.encoding[i] > ot.encoding[i]) {
                return 1;
            }
        }
        if (this.encoding.length < ot.encoding.length) {
            return -1;
        }
        if (this.encoding.length > ot.encoding.length) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || (other instanceof OrderedTuple && this.compareTo((OrderedTuple)other) == 0);
    }
    
    @Override
    public int hashCode() {
        final int multiplier = 37;
        final int trueHash = 97;
        final int falseHash = 71;
        int hash = Arrays.hashCode(this.components);
        hash = hash * 37 + this.offset;
        hash = hash * 37 + this.lsb;
        hash = hash * 37 + (this.posInf ? 97 : 71);
        hash = hash * 37 + (this.negInf ? 97 : 71);
        hash = hash * 37 + (this.nan ? 97 : 71);
        return hash;
    }
    
    public double[] getComponents() {
        return this.components.clone();
    }
    
    private static long sign(final long bits) {
        return bits & Long.MIN_VALUE;
    }
    
    private static int exponent(final long bits) {
        return (int)((bits & 0x7FF0000000000000L) >> 52) - 1075;
    }
    
    private static long mantissa(final long bits) {
        return ((bits & 0x7FF0000000000000L) == 0x0L) ? ((bits & 0xFFFFFFFFFFFFFL) << 1) : (0x10000000000000L | (bits & 0xFFFFFFFFFFFFFL));
    }
    
    private static int computeMSB(final long l) {
        long ll = l;
        long mask = 4294967295L;
        int scale = 32;
        int msb = 0;
        while (scale != 0) {
            if ((ll & mask) != ll) {
                msb |= scale;
                ll >>= scale;
            }
            scale >>= 1;
            mask >>= scale;
        }
        return msb;
    }
    
    private static int computeLSB(final long l) {
        long ll = l;
        long mask = -4294967296L;
        int scale = 32;
        int lsb = 0;
        while (scale != 0) {
            if ((ll & mask) == ll) {
                lsb |= scale;
                ll >>= scale;
            }
            scale >>= 1;
            mask >>= scale;
        }
        return lsb;
    }
    
    private int getBit(final int i, final int k) {
        final long bits = Double.doubleToLongBits(this.components[i]);
        final int e = exponent(bits);
        if (k < e || k > this.offset) {
            return 0;
        }
        if (k == this.offset) {
            return (sign(bits) == 0L) ? 1 : 0;
        }
        if (k > e + 52) {
            return (sign(bits) != 0L) ? 1 : 0;
        }
        final long m = (sign(bits) == 0L) ? mantissa(bits) : (-mantissa(bits));
        return (int)(m >> k - e & 0x1L);
    }
}
