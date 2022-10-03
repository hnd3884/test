package org.apache.lucene.analysis.tokenattributes;

import java.io.IOException;
import org.apache.lucene.util.Attribute;
import org.apache.lucene.util.AttributeReflector;
import java.nio.CharBuffer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.AttributeImpl;

public class CharTermAttributeImpl extends AttributeImpl implements CharTermAttribute, TermToBytesRefAttribute, Cloneable
{
    private static int MIN_BUFFER_SIZE;
    private char[] termBuffer;
    private int termLength;
    protected BytesRefBuilder builder;
    
    public CharTermAttributeImpl() {
        this.termBuffer = new char[ArrayUtil.oversize(CharTermAttributeImpl.MIN_BUFFER_SIZE, 2)];
        this.termLength = 0;
        this.builder = new BytesRefBuilder();
    }
    
    @Override
    public final void copyBuffer(final char[] buffer, final int offset, final int length) {
        this.growTermBuffer(length);
        System.arraycopy(buffer, offset, this.termBuffer, 0, length);
        this.termLength = length;
    }
    
    @Override
    public final char[] buffer() {
        return this.termBuffer;
    }
    
    @Override
    public final char[] resizeBuffer(final int newSize) {
        if (this.termBuffer.length < newSize) {
            final char[] newCharBuffer = new char[ArrayUtil.oversize(newSize, 2)];
            System.arraycopy(this.termBuffer, 0, newCharBuffer, 0, this.termBuffer.length);
            this.termBuffer = newCharBuffer;
        }
        return this.termBuffer;
    }
    
    private void growTermBuffer(final int newSize) {
        if (this.termBuffer.length < newSize) {
            this.termBuffer = new char[ArrayUtil.oversize(newSize, 2)];
        }
    }
    
    @Override
    public final CharTermAttribute setLength(final int length) {
        if (length > this.termBuffer.length) {
            throw new IllegalArgumentException("length " + length + " exceeds the size of the termBuffer (" + this.termBuffer.length + ")");
        }
        this.termLength = length;
        return this;
    }
    
    @Override
    public final CharTermAttribute setEmpty() {
        this.termLength = 0;
        return this;
    }
    
    @Override
    public BytesRef getBytesRef() {
        this.builder.copyChars(this.termBuffer, 0, this.termLength);
        return this.builder.get();
    }
    
    @Override
    public final int length() {
        return this.termLength;
    }
    
    @Override
    public final char charAt(final int index) {
        if (index >= this.termLength) {
            throw new IndexOutOfBoundsException();
        }
        return this.termBuffer[index];
    }
    
    @Override
    public final CharSequence subSequence(final int start, final int end) {
        if (start > this.termLength || end > this.termLength) {
            throw new IndexOutOfBoundsException();
        }
        return new String(this.termBuffer, start, end - start);
    }
    
    @Override
    public final CharTermAttribute append(final CharSequence csq) {
        if (csq == null) {
            return this.appendNull();
        }
        return this.append(csq, 0, csq.length());
    }
    
    @Override
    public final CharTermAttribute append(CharSequence csq, int start, final int end) {
        if (csq == null) {
            csq = "null";
        }
        final int len = end - start;
        final int csqlen = csq.length();
        if (len < 0 || start > csqlen || end > csqlen) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return this;
        }
        this.resizeBuffer(this.termLength + len);
        if (len > 4) {
            if (csq instanceof String) {
                ((String)csq).getChars(start, end, this.termBuffer, this.termLength);
            }
            else if (csq instanceof StringBuilder) {
                ((StringBuilder)csq).getChars(start, end, this.termBuffer, this.termLength);
            }
            else if (csq instanceof CharTermAttribute) {
                System.arraycopy(((CharTermAttribute)csq).buffer(), start, this.termBuffer, this.termLength, len);
            }
            else if (csq instanceof CharBuffer && ((CharBuffer)csq).hasArray()) {
                final CharBuffer cb = (CharBuffer)csq;
                System.arraycopy(cb.array(), cb.arrayOffset() + cb.position() + start, this.termBuffer, this.termLength, len);
            }
            else {
                if (!(csq instanceof StringBuffer)) {
                    while (start < end) {
                        this.termBuffer[this.termLength++] = csq.charAt(start++);
                    }
                    return this;
                }
                ((StringBuffer)csq).getChars(start, end, this.termBuffer, this.termLength);
            }
            this.termLength += len;
            return this;
        }
        while (start < end) {
            this.termBuffer[this.termLength++] = csq.charAt(start++);
        }
        return this;
    }
    
    @Override
    public final CharTermAttribute append(final char c) {
        this.resizeBuffer(this.termLength + 1)[this.termLength++] = c;
        return this;
    }
    
    @Override
    public final CharTermAttribute append(final String s) {
        if (s == null) {
            return this.appendNull();
        }
        final int len = s.length();
        s.getChars(0, len, this.resizeBuffer(this.termLength + len), this.termLength);
        this.termLength += len;
        return this;
    }
    
    @Override
    public final CharTermAttribute append(final StringBuilder s) {
        if (s == null) {
            return this.appendNull();
        }
        final int len = s.length();
        s.getChars(0, len, this.resizeBuffer(this.termLength + len), this.termLength);
        this.termLength += len;
        return this;
    }
    
    @Override
    public final CharTermAttribute append(final CharTermAttribute ta) {
        if (ta == null) {
            return this.appendNull();
        }
        final int len = ta.length();
        System.arraycopy(ta.buffer(), 0, this.resizeBuffer(this.termLength + len), this.termLength, len);
        this.termLength += len;
        return this;
    }
    
    private CharTermAttribute appendNull() {
        this.resizeBuffer(this.termLength + 4);
        this.termBuffer[this.termLength++] = 'n';
        this.termBuffer[this.termLength++] = 'u';
        this.termBuffer[this.termLength++] = 'l';
        this.termBuffer[this.termLength++] = 'l';
        return this;
    }
    
    @Override
    public int hashCode() {
        int code = this.termLength;
        code = code * 31 + ArrayUtil.hashCode(this.termBuffer, 0, this.termLength);
        return code;
    }
    
    @Override
    public void clear() {
        this.termLength = 0;
    }
    
    @Override
    public CharTermAttributeImpl clone() {
        final CharTermAttributeImpl t = (CharTermAttributeImpl)super.clone();
        t.termBuffer = new char[this.termLength];
        System.arraycopy(this.termBuffer, 0, t.termBuffer, 0, this.termLength);
        (t.builder = new BytesRefBuilder()).copyBytes(this.builder.get());
        return t;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof CharTermAttributeImpl)) {
            return false;
        }
        final CharTermAttributeImpl o = (CharTermAttributeImpl)other;
        if (this.termLength != o.termLength) {
            return false;
        }
        for (int i = 0; i < this.termLength; ++i) {
            if (this.termBuffer[i] != o.termBuffer[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return new String(this.termBuffer, 0, this.termLength);
    }
    
    @Override
    public void reflectWith(final AttributeReflector reflector) {
        reflector.reflect(CharTermAttribute.class, "term", this.toString());
        reflector.reflect(TermToBytesRefAttribute.class, "bytes", this.getBytesRef());
    }
    
    @Override
    public void copyTo(final AttributeImpl target) {
        final CharTermAttribute t = (CharTermAttribute)target;
        t.copyBuffer(this.termBuffer, 0, this.termLength);
    }
    
    static {
        CharTermAttributeImpl.MIN_BUFFER_SIZE = 10;
    }
}
