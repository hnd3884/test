package org.apache.tomcat.util.buf;

import java.io.IOException;

public final class CharChunk extends AbstractChunk implements CharSequence
{
    private static final long serialVersionUID = 1L;
    private char[] buff;
    private transient CharInputChannel in;
    private transient CharOutputChannel out;
    
    public CharChunk() {
        this.in = null;
        this.out = null;
    }
    
    public CharChunk(final int initial) {
        this.in = null;
        this.out = null;
        this.allocate(initial, -1);
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    public void allocate(final int initial, final int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new char[initial];
        }
        this.setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }
    
    public void setChars(final char[] c, final int off, final int len) {
        this.buff = c;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }
    
    public char[] getChars() {
        return this.getBuffer();
    }
    
    public char[] getBuffer() {
        return this.buff;
    }
    
    public void setCharInputChannel(final CharInputChannel in) {
        this.in = in;
    }
    
    public void setCharOutputChannel(final CharOutputChannel out) {
        this.out = out;
    }
    
    public void append(final char c) throws IOException {
        this.makeSpace(1);
        final int limit = this.getLimitInternal();
        if (this.end >= limit) {
            this.flushBuffer();
        }
        this.buff[this.end++] = c;
    }
    
    public void append(final CharChunk src) throws IOException {
        this.append(src.getBuffer(), src.getOffset(), src.getLength());
    }
    
    public void append(final char[] src, final int off, final int len) throws IOException {
        this.makeSpace(len);
        final int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteChars(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
            return;
        }
        if (len + this.end < 2 * limit) {
            final int avail = limit - this.end;
            System.arraycopy(src, off, this.buff, this.end, avail);
            this.end += avail;
            this.flushBuffer();
            System.arraycopy(src, off + avail, this.buff, this.end, len - avail);
            this.end += len - avail;
        }
        else {
            this.flushBuffer();
            this.out.realWriteChars(src, off, len);
        }
    }
    
    public void append(final String s) throws IOException {
        this.append(s, 0, s.length());
    }
    
    public void append(final String s, final int off, final int len) throws IOException {
        if (s == null) {
            return;
        }
        this.makeSpace(len);
        final int limit = this.getLimitInternal();
        int sOff = off;
        final int sEnd = off + len;
        while (sOff < sEnd) {
            final int d = this.min(limit - this.end, sEnd - sOff);
            s.getChars(sOff, sOff + d, this.buff, this.end);
            sOff += d;
            this.end += d;
            if (this.end >= limit) {
                this.flushBuffer();
            }
        }
    }
    
    public int substract() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++];
    }
    
    public int substract(final char[] dest, final int off, final int len) throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        int n;
        if ((n = len) > this.getLength()) {
            n = this.getLength();
        }
        System.arraycopy(this.buff, this.start, dest, off, n);
        this.start += n;
        return n;
    }
    
    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            final int n = this.in.realReadChars();
            if (n < 0) {
                return true;
            }
        }
        return false;
    }
    
    public void flushBuffer() throws IOException {
        if (this.out == null) {
            throw new IOException("Buffer overflow, no sink " + this.getLimit() + " " + this.buff.length);
        }
        this.out.realWriteChars(this.buff, this.start, this.end - this.start);
        this.end = this.start;
    }
    
    public void makeSpace(final int count) {
        char[] tmp = null;
        final int limit = this.getLimitInternal();
        long desiredSize = this.end + count;
        if (desiredSize > limit) {
            desiredSize = limit;
        }
        if (this.buff == null) {
            if (desiredSize < 256L) {
                desiredSize = 256L;
            }
            this.buff = new char[(int)desiredSize];
        }
        if (desiredSize <= this.buff.length) {
            return;
        }
        long newSize;
        if (desiredSize < 2L * this.buff.length) {
            newSize = this.buff.length * 2L;
        }
        else {
            newSize = this.buff.length * 2L + count;
        }
        if (newSize > limit) {
            newSize = limit;
        }
        tmp = new char[(int)newSize];
        System.arraycopy(this.buff, 0, tmp, 0, this.end);
        this.buff = tmp;
        tmp = null;
    }
    
    @Override
    public String toString() {
        if (null == this.buff) {
            return null;
        }
        if (this.end - this.start == 0) {
            return "";
        }
        return StringCache.toString(this);
    }
    
    public String toStringInternal() {
        return new String(this.buff, this.start, this.end - this.start);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof CharChunk && this.equals((CharChunk)obj);
    }
    
    public boolean equals(final String s) {
        final char[] c = this.buff;
        final int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (c[off++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equalsIgnoreCase(final String s) {
        final char[] c = this.buff;
        final int len = this.end - this.start;
        if (c == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(c[off++]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(final CharChunk cc) {
        return this.equals(cc.getChars(), cc.getOffset(), cc.getLength());
    }
    
    public boolean equals(final char[] b2, int off2, final int len2) {
        final char[] b3 = this.buff;
        if (b3 == null && b2 == null) {
            return true;
        }
        int len3 = this.end - this.start;
        if (len3 != len2 || b3 == null || b2 == null) {
            return false;
        }
        int off3 = this.start;
        while (len3-- > 0) {
            if (b3[off3++] != b2[off2++]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean startsWith(final String s) {
        final char[] c = this.buff;
        final int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (c[off++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean startsWithIgnoreCase(final String s, final int pos) {
        final char[] c = this.buff;
        final int len = s.length();
        if (c == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(c[off++]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean endsWith(final String s) {
        final char[] c = this.buff;
        final int len = s.length();
        if (c == null || len > this.end - this.start) {
            return false;
        }
        int off = this.end - len;
        for (int i = 0; i < len; ++i) {
            if (c[off++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected int getBufferElement(final int index) {
        return this.buff[index];
    }
    
    public int indexOf(final char c) {
        return this.indexOf(c, this.start);
    }
    
    public int indexOf(final char c, final int starting) {
        final int ret = indexOf(this.buff, this.start + starting, this.end, c);
        return (ret >= this.start) ? (ret - this.start) : -1;
    }
    
    public static int indexOf(final char[] chars, final int start, final int end, final char s) {
        for (int offset = start; offset < end; ++offset) {
            final char c = chars[offset];
            if (c == s) {
                return offset;
            }
        }
        return -1;
    }
    
    private int min(final int a, final int b) {
        if (a < b) {
            return a;
        }
        return b;
    }
    
    @Override
    public char charAt(final int index) {
        return this.buff[index + this.start];
    }
    
    @Override
    public CharSequence subSequence(final int start, final int end) {
        try {
            final CharChunk result = (CharChunk)this.clone();
            result.setOffset(this.start + start);
            result.setEnd(this.start + end);
            return result;
        }
        catch (final CloneNotSupportedException e) {
            return null;
        }
    }
    
    @Override
    public int length() {
        return this.end - this.start;
    }
    
    @Deprecated
    public void setOptimizedWrite(final boolean optimizedWrite) {
    }
    
    public interface CharOutputChannel
    {
        void realWriteChars(final char[] p0, final int p1, final int p2) throws IOException;
    }
    
    public interface CharInputChannel
    {
        int realReadChars() throws IOException;
    }
}
