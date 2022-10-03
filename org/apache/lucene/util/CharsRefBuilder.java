package org.apache.lucene.util;

import java.io.IOException;
import java.util.Arrays;

public class CharsRefBuilder implements Appendable
{
    private static final String NULL_STRING = "null";
    private final CharsRef ref;
    
    public CharsRefBuilder() {
        this.ref = new CharsRef();
    }
    
    public char[] chars() {
        return this.ref.chars;
    }
    
    public int length() {
        return this.ref.length;
    }
    
    public void setLength(final int length) {
        this.ref.length = length;
    }
    
    public char charAt(final int offset) {
        return this.ref.chars[offset];
    }
    
    public void setCharAt(final int offset, final char b) {
        this.ref.chars[offset] = b;
    }
    
    public void clear() {
        this.ref.length = 0;
    }
    
    @Override
    public CharsRefBuilder append(final CharSequence csq) {
        if (csq == null) {
            return this.append((CharSequence)"null");
        }
        return this.append(csq, 0, csq.length());
    }
    
    @Override
    public CharsRefBuilder append(final CharSequence csq, final int start, final int end) {
        if (csq == null) {
            return this.append((CharSequence)"null");
        }
        this.grow(this.ref.length + end - start);
        for (int i = start; i < end; ++i) {
            this.setCharAt(this.ref.length++, csq.charAt(i));
        }
        return this;
    }
    
    @Override
    public CharsRefBuilder append(final char c) {
        this.grow(this.ref.length + 1);
        this.setCharAt(this.ref.length++, c);
        return this;
    }
    
    public void copyChars(final CharsRef other) {
        this.copyChars(other.chars, other.offset, other.length);
    }
    
    public void grow(final int newLength) {
        this.ref.chars = ArrayUtil.grow(this.ref.chars, newLength);
    }
    
    public void copyUTF8Bytes(final byte[] bytes, final int offset, final int length) {
        this.grow(length);
        this.ref.length = UnicodeUtil.UTF8toUTF16(bytes, offset, length, this.ref.chars);
    }
    
    public void copyUTF8Bytes(final BytesRef bytes) {
        this.copyUTF8Bytes(bytes.bytes, bytes.offset, bytes.length);
    }
    
    public void copyChars(final char[] otherChars, final int otherOffset, final int otherLength) {
        this.grow(otherLength);
        System.arraycopy(otherChars, otherOffset, this.ref.chars, 0, otherLength);
        this.ref.length = otherLength;
    }
    
    public void append(final char[] otherChars, final int otherOffset, final int otherLength) {
        final int newLen = this.ref.length + otherLength;
        this.grow(newLen);
        System.arraycopy(otherChars, otherOffset, this.ref.chars, this.ref.length, otherLength);
        this.ref.length = newLen;
    }
    
    public CharsRef get() {
        assert this.ref.offset == 0 : "Modifying the offset of the returned ref is illegal";
        return this.ref;
    }
    
    public CharsRef toCharsRef() {
        return new CharsRef(Arrays.copyOf(this.ref.chars, this.ref.length), 0, this.ref.length);
    }
    
    @Override
    public String toString() {
        return this.get().toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
