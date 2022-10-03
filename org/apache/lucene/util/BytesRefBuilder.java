package org.apache.lucene.util;

import java.util.Arrays;

public class BytesRefBuilder
{
    private final BytesRef ref;
    
    public BytesRefBuilder() {
        this.ref = new BytesRef();
    }
    
    public byte[] bytes() {
        return this.ref.bytes;
    }
    
    public int length() {
        return this.ref.length;
    }
    
    public void setLength(final int length) {
        this.ref.length = length;
    }
    
    public byte byteAt(final int offset) {
        return this.ref.bytes[offset];
    }
    
    public void setByteAt(final int offset, final byte b) {
        this.ref.bytes[offset] = b;
    }
    
    public void grow(final int capacity) {
        this.ref.bytes = ArrayUtil.grow(this.ref.bytes, capacity);
    }
    
    public void append(final byte b) {
        this.grow(this.ref.length + 1);
        this.ref.bytes[this.ref.length++] = b;
    }
    
    public void append(final byte[] b, final int off, final int len) {
        this.grow(this.ref.length + len);
        System.arraycopy(b, off, this.ref.bytes, this.ref.length, len);
        final BytesRef ref = this.ref;
        ref.length += len;
    }
    
    public void append(final BytesRef ref) {
        this.append(ref.bytes, ref.offset, ref.length);
    }
    
    public void append(final BytesRefBuilder builder) {
        this.append(builder.get());
    }
    
    public void clear() {
        this.setLength(0);
    }
    
    public void copyBytes(final byte[] b, final int off, final int len) {
        this.clear();
        this.append(b, off, len);
    }
    
    public void copyBytes(final BytesRef ref) {
        this.clear();
        this.append(ref);
    }
    
    public void copyBytes(final BytesRefBuilder builder) {
        this.clear();
        this.append(builder);
    }
    
    public void copyChars(final CharSequence text) {
        this.copyChars(text, 0, text.length());
    }
    
    public void copyChars(final CharSequence text, final int off, final int len) {
        this.grow(len * 3);
        this.ref.length = UnicodeUtil.UTF16toUTF8(text, off, len, this.ref.bytes);
    }
    
    public void copyChars(final char[] text, final int off, final int len) {
        this.grow(len * 3);
        this.ref.length = UnicodeUtil.UTF16toUTF8(text, off, len, this.ref.bytes);
    }
    
    public BytesRef get() {
        assert this.ref.offset == 0 : "Modifying the offset of the returned ref is illegal";
        return this.ref;
    }
    
    public BytesRef toBytesRef() {
        return new BytesRef(Arrays.copyOf(this.ref.bytes, this.ref.length));
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
