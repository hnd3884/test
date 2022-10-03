package org.apache.lucene.util;

import java.util.Arrays;

public final class IntsRef implements Comparable<IntsRef>, Cloneable
{
    public static final int[] EMPTY_INTS;
    public int[] ints;
    public int offset;
    public int length;
    
    public IntsRef() {
        this.ints = IntsRef.EMPTY_INTS;
    }
    
    public IntsRef(final int capacity) {
        this.ints = new int[capacity];
    }
    
    public IntsRef(final int[] ints, final int offset, final int length) {
        this.ints = ints;
        this.offset = offset;
        this.length = length;
        assert this.isValid();
    }
    
    public IntsRef clone() {
        return new IntsRef(this.ints, this.offset, this.length);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        for (int end = this.offset + this.length, i = this.offset; i < end; ++i) {
            result = 31 * result + this.ints[i];
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof IntsRef && this.intsEquals((IntsRef)other);
    }
    
    public boolean intsEquals(final IntsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            final int[] otherInts = other.ints;
            for (int end = this.offset + this.length, upto = this.offset; upto < end; ++upto, ++otherUpto) {
                if (this.ints[upto] != otherInts[otherUpto]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(final IntsRef other) {
        if (this == other) {
            return 0;
        }
        final int[] aInts = this.ints;
        int aUpto = this.offset;
        final int[] bInts = other.ints;
        int bUpto = other.offset;
        final int aStop = aUpto + Math.min(this.length, other.length);
        while (aUpto < aStop) {
            final int aInt = aInts[aUpto++];
            final int bInt = bInts[bUpto++];
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
        for (int end = this.offset + this.length, i = this.offset; i < end; ++i) {
            if (i > this.offset) {
                sb.append(' ');
            }
            sb.append(Integer.toHexString(this.ints[i]));
        }
        sb.append(']');
        return sb.toString();
    }
    
    public static IntsRef deepCopyOf(final IntsRef other) {
        return new IntsRef(Arrays.copyOfRange(other.ints, other.offset, other.offset + other.length), 0, other.length);
    }
    
    public boolean isValid() {
        if (this.ints == null) {
            throw new IllegalStateException("ints is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.ints.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",ints.length=" + this.ints.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.ints.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",ints.length=" + this.ints.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.ints.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",ints.length=" + this.ints.length);
        }
        return true;
    }
    
    static {
        EMPTY_INTS = new int[0];
    }
}
