package org.apache.lucene.util;

import java.util.Arrays;

public final class LongsRef implements Comparable<LongsRef>, Cloneable
{
    public static final long[] EMPTY_LONGS;
    public long[] longs;
    public int offset;
    public int length;
    
    public LongsRef() {
        this.longs = LongsRef.EMPTY_LONGS;
    }
    
    public LongsRef(final int capacity) {
        this.longs = new long[capacity];
    }
    
    public LongsRef(final long[] longs, final int offset, final int length) {
        this.longs = longs;
        this.offset = offset;
        this.length = length;
        assert this.isValid();
    }
    
    public LongsRef clone() {
        return new LongsRef(this.longs, this.offset, this.length);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        final long end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            result = 31 * result + (int)(this.longs[i] ^ this.longs[i] >>> 32);
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof LongsRef && this.longsEquals((LongsRef)other);
    }
    
    public boolean longsEquals(final LongsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            final long[] otherInts = other.longs;
            final long end = this.offset + this.length;
            for (int upto = this.offset; upto < end; ++upto, ++otherUpto) {
                if (this.longs[upto] != otherInts[otherUpto]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(final LongsRef other) {
        if (this == other) {
            return 0;
        }
        final long[] aInts = this.longs;
        int aUpto = this.offset;
        final long[] bInts = other.longs;
        int bUpto = other.offset;
        final long aStop = aUpto + Math.min(this.length, other.length);
        while (aUpto < aStop) {
            final long aInt = aInts[aUpto++];
            final long bInt = bInts[bUpto++];
            if (aInt > bInt) {
                return 1;
            }
            if (aInt < bInt) {
                return -1;
            }
        }
        return this.length - other.length;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        final long end = this.offset + this.length;
        for (int i = this.offset; i < end; ++i) {
            if (i > this.offset) {
                sb.append(' ');
            }
            sb.append(Long.toHexString(this.longs[i]));
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static LongsRef deepCopyOf(final LongsRef other) {
        return new LongsRef(Arrays.copyOfRange(other.longs, other.offset, other.offset + other.length), 0, other.length);
    }
    
    public boolean isValid() {
        if (this.longs == null) {
            throw new IllegalStateException("longs is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.longs.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",longs.length=" + this.longs.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.longs.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",longs.length=" + this.longs.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.longs.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",longs.length=" + this.longs.length);
        }
        return true;
    }
    
    static {
        EMPTY_LONGS = new long[0];
    }
}
