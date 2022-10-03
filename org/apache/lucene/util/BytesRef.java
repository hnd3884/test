package org.apache.lucene.util;

import java.util.Arrays;
import java.util.Comparator;

public final class BytesRef implements Comparable<BytesRef>, Cloneable
{
    public static final byte[] EMPTY_BYTES;
    public byte[] bytes;
    public int offset;
    public int length;
    private static final Comparator<BytesRef> utf8SortedAsUnicodeSortOrder;
    @Deprecated
    private static final Comparator<BytesRef> utf8SortedAsUTF16SortOrder;
    
    public BytesRef() {
        this(BytesRef.EMPTY_BYTES);
    }
    
    public BytesRef(final byte[] bytes, final int offset, final int length) {
        this.bytes = bytes;
        this.offset = offset;
        this.length = length;
        assert this.isValid();
    }
    
    public BytesRef(final byte[] bytes) {
        this(bytes, 0, bytes.length);
    }
    
    public BytesRef(final int capacity) {
        this.bytes = new byte[capacity];
    }
    
    public BytesRef(final CharSequence text) {
        this(new byte[3 * text.length()]);
        this.length = UnicodeUtil.UTF16toUTF8(text, 0, text.length(), this.bytes);
    }
    
    public boolean bytesEquals(final BytesRef other) {
        assert other != null;
        if (this.length == other.length) {
            int otherUpto = other.offset;
            final byte[] otherBytes = other.bytes;
            for (int end = this.offset + this.length, upto = this.offset; upto < end; ++upto, ++otherUpto) {
                if (this.bytes[upto] != otherBytes[otherUpto]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    public BytesRef clone() {
        return new BytesRef(this.bytes, this.offset, this.length);
    }
    
    @Override
    public int hashCode() {
        return StringHelper.murmurhash3_x86_32(this, StringHelper.GOOD_FAST_HASH_SEED);
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof BytesRef && this.bytesEquals((BytesRef)other);
    }
    
    public String utf8ToString() {
        final char[] ref = new char[this.length];
        final int len = UnicodeUtil.UTF8toUTF16(this.bytes, this.offset, this.length, ref);
        return new String(ref, 0, len);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int end = this.offset + this.length, i = this.offset; i < end; ++i) {
            if (i > this.offset) {
                sb.append(' ');
            }
            sb.append(Integer.toHexString(this.bytes[i] & 0xFF));
        }
        sb.append(']');
        return sb.toString();
    }
    
    @Override
    public int compareTo(final BytesRef other) {
        return BytesRef.utf8SortedAsUnicodeSortOrder.compare(this, other);
    }
    
    public static Comparator<BytesRef> getUTF8SortedAsUnicodeComparator() {
        return BytesRef.utf8SortedAsUnicodeSortOrder;
    }
    
    @Deprecated
    public static Comparator<BytesRef> getUTF8SortedAsUTF16Comparator() {
        return BytesRef.utf8SortedAsUTF16SortOrder;
    }
    
    public static BytesRef deepCopyOf(final BytesRef other) {
        final BytesRef copy = new BytesRef();
        copy.bytes = Arrays.copyOfRange(other.bytes, other.offset, other.offset + other.length);
        copy.offset = 0;
        copy.length = other.length;
        return copy;
    }
    
    public boolean isValid() {
        if (this.bytes == null) {
            throw new IllegalStateException("bytes is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.bytes.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",bytes.length=" + this.bytes.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.bytes.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",bytes.length=" + this.bytes.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.bytes.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",bytes.length=" + this.bytes.length);
        }
        return true;
    }
    
    static {
        EMPTY_BYTES = new byte[0];
        utf8SortedAsUnicodeSortOrder = new UTF8SortedAsUnicodeComparator();
        utf8SortedAsUTF16SortOrder = new UTF8SortedAsUTF16Comparator();
    }
    
    private static class UTF8SortedAsUnicodeComparator implements Comparator<BytesRef>
    {
        @Override
        public int compare(final BytesRef a, final BytesRef b) {
            final byte[] aBytes = a.bytes;
            int aUpto = a.offset;
            final byte[] bBytes = b.bytes;
            int bUpto = b.offset;
            final int aStop = aUpto + Math.min(a.length, b.length);
            while (aUpto < aStop) {
                final int aByte = aBytes[aUpto++] & 0xFF;
                final int bByte = bBytes[bUpto++] & 0xFF;
                final int diff = aByte - bByte;
                if (diff != 0) {
                    return diff;
                }
            }
            return a.length - b.length;
        }
    }
    
    @Deprecated
    private static class UTF8SortedAsUTF16Comparator implements Comparator<BytesRef>
    {
        @Override
        public int compare(final BytesRef a, final BytesRef b) {
            final byte[] aBytes = a.bytes;
            int aUpto = a.offset;
            final byte[] bBytes = b.bytes;
            int bUpto = b.offset;
            int aStop;
            if (a.length < b.length) {
                aStop = aUpto + a.length;
            }
            else {
                aStop = aUpto + b.length;
            }
            while (aUpto < aStop) {
                int aByte = aBytes[aUpto++] & 0xFF;
                int bByte = bBytes[bUpto++] & 0xFF;
                if (aByte != bByte) {
                    if (aByte >= 238 && bByte >= 238) {
                        if ((aByte & 0xFE) == 0xEE) {
                            aByte += 14;
                        }
                        if ((bByte & 0xFE) == 0xEE) {
                            bByte += 14;
                        }
                    }
                    return aByte - bByte;
                }
            }
            return a.length - b.length;
        }
    }
}
