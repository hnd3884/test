package org.apache.lucene.util;

import java.util.Arrays;
import java.util.Comparator;

public final class CharsRef implements Comparable<CharsRef>, CharSequence, Cloneable
{
    public static final char[] EMPTY_CHARS;
    public char[] chars;
    public int offset;
    public int length;
    @Deprecated
    private static final Comparator<CharsRef> utf16SortedAsUTF8SortOrder;
    
    public CharsRef() {
        this(CharsRef.EMPTY_CHARS, 0, 0);
    }
    
    public CharsRef(final int capacity) {
        this.chars = new char[capacity];
    }
    
    public CharsRef(final char[] chars, final int offset, final int length) {
        this.chars = chars;
        this.offset = offset;
        this.length = length;
        assert this.isValid();
    }
    
    public CharsRef(final String string) {
        this.chars = string.toCharArray();
        this.offset = 0;
        this.length = this.chars.length;
    }
    
    public CharsRef clone() {
        return new CharsRef(this.chars, this.offset, this.length);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        for (int end = this.offset + this.length, i = this.offset; i < end; ++i) {
            result = 31 * result + this.chars[i];
        }
        return result;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other != null && other instanceof CharsRef && this.charsEquals((CharsRef)other);
    }
    
    public boolean charsEquals(final CharsRef other) {
        if (this.length == other.length) {
            int otherUpto = other.offset;
            final char[] otherChars = other.chars;
            for (int end = this.offset + this.length, upto = this.offset; upto < end; ++upto, ++otherUpto) {
                if (this.chars[upto] != otherChars[otherUpto]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int compareTo(final CharsRef other) {
        if (this == other) {
            return 0;
        }
        final char[] aChars = this.chars;
        int aUpto = this.offset;
        final char[] bChars = other.chars;
        int bUpto = other.offset;
        final int aStop = aUpto + Math.min(this.length, other.length);
        while (aUpto < aStop) {
            final int aInt = aChars[aUpto++];
            final int bInt = bChars[bUpto++];
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
        return new String(this.chars, this.offset, this.length);
    }
    
    @Override
    public int length() {
        return this.length;
    }
    
    @Override
    public char charAt(final int index) {
        if (index < 0 || index >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        return this.chars[this.offset + index];
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        if (start < 0 || end > this.length || start > end) {
            throw new IndexOutOfBoundsException();
        }
        return new CharsRef(this.chars, this.offset + start, end - start);
    }
    
    @Deprecated
    public static Comparator<CharsRef> getUTF16SortedAsUTF8Comparator() {
        return CharsRef.utf16SortedAsUTF8SortOrder;
    }
    
    public static CharsRef deepCopyOf(final CharsRef other) {
        return new CharsRef(Arrays.copyOfRange(other.chars, other.offset, other.offset + other.length), 0, other.length);
    }
    
    public boolean isValid() {
        if (this.chars == null) {
            throw new IllegalStateException("chars is null");
        }
        if (this.length < 0) {
            throw new IllegalStateException("length is negative: " + this.length);
        }
        if (this.length > this.chars.length) {
            throw new IllegalStateException("length is out of bounds: " + this.length + ",chars.length=" + this.chars.length);
        }
        if (this.offset < 0) {
            throw new IllegalStateException("offset is negative: " + this.offset);
        }
        if (this.offset > this.chars.length) {
            throw new IllegalStateException("offset out of bounds: " + this.offset + ",chars.length=" + this.chars.length);
        }
        if (this.offset + this.length < 0) {
            throw new IllegalStateException("offset+length is negative: offset=" + this.offset + ",length=" + this.length);
        }
        if (this.offset + this.length > this.chars.length) {
            throw new IllegalStateException("offset+length out of bounds: offset=" + this.offset + ",length=" + this.length + ",chars.length=" + this.chars.length);
        }
        return true;
    }
    
    static {
        EMPTY_CHARS = new char[0];
        utf16SortedAsUTF8SortOrder = new UTF16SortedAsUTF8Comparator();
    }
    
    @Deprecated
    private static class UTF16SortedAsUTF8Comparator implements Comparator<CharsRef>
    {
        @Override
        public int compare(final CharsRef a, final CharsRef b) {
            if (a == b) {
                return 0;
            }
            final char[] aChars = a.chars;
            int aUpto = a.offset;
            final char[] bChars = b.chars;
            int bUpto = b.offset;
            final int aStop = aUpto + Math.min(a.length, b.length);
            while (aUpto < aStop) {
                char aChar = aChars[aUpto++];
                char bChar = bChars[bUpto++];
                if (aChar != bChar) {
                    if (aChar >= '\ud800' && bChar >= '\ud800') {
                        if (aChar >= '\ue000') {
                            aChar -= '\u0800';
                        }
                        else {
                            aChar += '\u2000';
                        }
                        if (bChar >= '\ue000') {
                            bChar -= '\u0800';
                        }
                        else {
                            bChar += '\u2000';
                        }
                    }
                    return aChar - bChar;
                }
            }
            return a.length - b.length;
        }
    }
}
