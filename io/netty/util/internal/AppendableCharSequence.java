package io.netty.util.internal;

import java.io.IOException;
import java.util.Arrays;

public final class AppendableCharSequence implements CharSequence, Appendable
{
    private char[] chars;
    private int pos;
    
    public AppendableCharSequence(final int length) {
        this.chars = new char[ObjectUtil.checkPositive(length, "length")];
    }
    
    private AppendableCharSequence(final char[] chars) {
        this.chars = ObjectUtil.checkNonEmpty(chars, "chars");
        this.pos = chars.length;
    }
    
    public void setLength(final int length) {
        if (length < 0 || length > this.pos) {
            throw new IllegalArgumentException("length: " + length + " (length: >= 0, <= " + this.pos + ')');
        }
        this.pos = length;
    }
    
    @Override
    public int length() {
        return this.pos;
    }
    
    @Override
    public char charAt(final int index) {
        if (index > this.pos) {
            throw new IndexOutOfBoundsException();
        }
        return this.chars[index];
    }
    
    public char charAtUnsafe(final int index) {
        return this.chars[index];
    }
    
    @Override
    public AppendableCharSequence subSequence(final int start, final int end) {
        if (start == end) {
            return new AppendableCharSequence(Math.min(16, this.chars.length));
        }
        return new AppendableCharSequence(Arrays.copyOfRange(this.chars, start, end));
    }
    
    @Override
    public AppendableCharSequence append(final char c) {
        if (this.pos == this.chars.length) {
            final char[] old = this.chars;
            System.arraycopy(old, 0, this.chars = new char[old.length << 1], 0, old.length);
        }
        this.chars[this.pos++] = c;
        return this;
    }
    
    @Override
    public AppendableCharSequence append(final CharSequence csq) {
        return this.append(csq, 0, csq.length());
    }
    
    @Override
    public AppendableCharSequence append(final CharSequence csq, final int start, final int end) {
        if (csq.length() < end) {
            throw new IndexOutOfBoundsException("expected: csq.length() >= (" + end + "),but actual is (" + csq.length() + ")");
        }
        final int length = end - start;
        if (length > this.chars.length - this.pos) {
            this.chars = expand(this.chars, this.pos + length, this.pos);
        }
        if (csq instanceof AppendableCharSequence) {
            final AppendableCharSequence seq = (AppendableCharSequence)csq;
            final char[] src = seq.chars;
            System.arraycopy(src, start, this.chars, this.pos, length);
            this.pos += length;
            return this;
        }
        for (int i = start; i < end; ++i) {
            this.chars[this.pos++] = csq.charAt(i);
        }
        return this;
    }
    
    public void reset() {
        this.pos = 0;
    }
    
    @Override
    public String toString() {
        return new String(this.chars, 0, this.pos);
    }
    
    public String substring(final int start, final int end) {
        final int length = end - start;
        if (start > this.pos || length > this.pos) {
            throw new IndexOutOfBoundsException("expected: start and length <= (" + this.pos + ")");
        }
        return new String(this.chars, start, length);
    }
    
    public String subStringUnsafe(final int start, final int end) {
        return new String(this.chars, start, end - start);
    }
    
    private static char[] expand(final char[] array, final int neededSpace, final int size) {
        int newCapacity = array.length;
        do {
            newCapacity <<= 1;
            if (newCapacity < 0) {
                throw new IllegalStateException();
            }
        } while (neededSpace > newCapacity);
        final char[] newArray = new char[newCapacity];
        System.arraycopy(array, 0, newArray, 0, size);
        return newArray;
    }
}
