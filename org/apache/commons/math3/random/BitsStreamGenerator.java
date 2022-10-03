package org.apache.commons.math3.random;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.util.FastMath;
import java.io.Serializable;

public abstract class BitsStreamGenerator implements RandomGenerator, Serializable
{
    private static final long serialVersionUID = 20130104L;
    private double nextGaussian;
    
    public BitsStreamGenerator() {
        this.nextGaussian = Double.NaN;
    }
    
    public abstract void setSeed(final int p0);
    
    public abstract void setSeed(final int[] p0);
    
    public abstract void setSeed(final long p0);
    
    protected abstract int next(final int p0);
    
    public boolean nextBoolean() {
        return this.next(1) != 0;
    }
    
    public double nextDouble() {
        final long high = (long)this.next(26) << 26;
        final int low = this.next(26);
        return (high | (long)low) * 2.220446049250313E-16;
    }
    
    public float nextFloat() {
        return this.next(23) * 1.1920929E-7f;
    }
    
    public double nextGaussian() {
        double random;
        if (Double.isNaN(this.nextGaussian)) {
            final double x = this.nextDouble();
            final double y = this.nextDouble();
            final double alpha = 6.283185307179586 * x;
            final double r = FastMath.sqrt(-2.0 * FastMath.log(y));
            random = r * FastMath.cos(alpha);
            this.nextGaussian = r * FastMath.sin(alpha);
        }
        else {
            random = this.nextGaussian;
            this.nextGaussian = Double.NaN;
        }
        return random;
    }
    
    public int nextInt() {
        return this.next(32);
    }
    
    public int nextInt(final int n) throws IllegalArgumentException {
        if (n <= 0) {
            throw new NotStrictlyPositiveException(n);
        }
        if ((n & -n) == n) {
            return (int)(n * (long)this.next(31) >> 31);
        }
        int bits;
        int val;
        do {
            bits = this.next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
    
    public long nextLong() {
        final long high = (long)this.next(32) << 32;
        final long low = (long)this.next(32) & 0xFFFFFFFFL;
        return high | low;
    }
    
    public long nextLong(final long n) throws IllegalArgumentException {
        if (n > 0L) {
            long bits;
            long val;
            do {
                bits = (long)this.next(31) << 32;
                bits |= ((long)this.next(32) & 0xFFFFFFFFL);
                val = bits % n;
            } while (bits - val + (n - 1L) < 0L);
            return val;
        }
        throw new NotStrictlyPositiveException(n);
    }
    
    public void clear() {
        this.nextGaussian = Double.NaN;
    }
    
    public void nextBytes(final byte[] bytes) {
        this.nextBytesFill(bytes, 0, bytes.length);
    }
    
    public void nextBytes(final byte[] bytes, final int start, final int len) {
        if (start < 0 || start >= bytes.length) {
            throw new OutOfRangeException(start, 0, bytes.length);
        }
        if (len < 0 || len > bytes.length - start) {
            throw new OutOfRangeException(len, 0, bytes.length - start);
        }
        this.nextBytesFill(bytes, start, len);
    }
    
    private void nextBytesFill(final byte[] bytes, final int start, final int len) {
        int index = start;
        int random;
        for (int indexLoopLimit = index + (len & 0x7FFFFFFC); index < indexLoopLimit; bytes[index++] = (byte)random, bytes[index++] = (byte)(random >>> 8), bytes[index++] = (byte)(random >>> 16), bytes[index++] = (byte)(random >>> 24)) {
            random = this.next(32);
        }
        final int indexLimit = start + len;
        if (index < indexLimit) {
            int random2 = this.next(32);
            while (true) {
                bytes[index++] = (byte)random2;
                if (index >= indexLimit) {
                    break;
                }
                random2 >>>= 8;
            }
        }
    }
}
