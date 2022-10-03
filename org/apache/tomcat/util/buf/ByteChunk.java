package org.apache.tomcat.util.buf;

import java.nio.charset.StandardCharsets;
import java.nio.CharBuffer;
import java.nio.ByteBuffer;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

public final class ByteChunk extends AbstractChunk
{
    private static final long serialVersionUID = 1L;
    public static final Charset DEFAULT_CHARSET;
    private transient Charset charset;
    private byte[] buff;
    private transient ByteInputChannel in;
    private transient ByteOutputChannel out;
    
    public ByteChunk() {
        this.in = null;
        this.out = null;
    }
    
    public ByteChunk(final int initial) {
        this.in = null;
        this.out = null;
        this.allocate(initial, -1);
    }
    
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeUTF(this.getCharset().name());
    }
    
    private void readObject(final ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.charset = Charset.forName(ois.readUTF());
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
    @Override
    public void recycle() {
        super.recycle();
        this.charset = null;
    }
    
    public void allocate(final int initial, final int limit) {
        if (this.buff == null || this.buff.length < initial) {
            this.buff = new byte[initial];
        }
        this.setLimit(limit);
        this.start = 0;
        this.end = 0;
        this.isSet = true;
        this.hasHashCode = false;
    }
    
    public void setBytes(final byte[] b, final int off, final int len) {
        this.buff = b;
        this.start = off;
        this.end = this.start + len;
        this.isSet = true;
        this.hasHashCode = false;
    }
    
    public void setCharset(final Charset charset) {
        this.charset = charset;
    }
    
    public Charset getCharset() {
        if (this.charset == null) {
            this.charset = ByteChunk.DEFAULT_CHARSET;
        }
        return this.charset;
    }
    
    public byte[] getBytes() {
        return this.getBuffer();
    }
    
    public byte[] getBuffer() {
        return this.buff;
    }
    
    public void setByteInputChannel(final ByteInputChannel in) {
        this.in = in;
    }
    
    public void setByteOutputChannel(final ByteOutputChannel out) {
        this.out = out;
    }
    
    public void append(final byte b) throws IOException {
        this.makeSpace(1);
        final int limit = this.getLimitInternal();
        if (this.end >= limit) {
            this.flushBuffer();
        }
        this.buff[this.end++] = b;
    }
    
    public void append(final ByteChunk src) throws IOException {
        this.append(src.getBytes(), src.getStart(), src.getLength());
    }
    
    public void append(final byte[] src, final int off, final int len) throws IOException {
        this.makeSpace(len);
        final int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(src, off, len);
            return;
        }
        if (len <= limit - this.end) {
            System.arraycopy(src, off, this.buff, this.end, len);
            this.end += len;
            return;
        }
        final int avail = limit - this.end;
        System.arraycopy(src, off, this.buff, this.end, avail);
        this.end += avail;
        this.flushBuffer();
        int remain;
        for (remain = len - avail; remain > limit - this.end; remain -= limit - this.end) {
            this.out.realWriteBytes(src, off + len - remain, limit - this.end);
        }
        System.arraycopy(src, off + len - remain, this.buff, this.end, remain);
        this.end += remain;
    }
    
    public void append(final ByteBuffer from) throws IOException {
        final int len = from.remaining();
        this.makeSpace(len);
        final int limit = this.getLimitInternal();
        if (len == limit && this.end == this.start && this.out != null) {
            this.out.realWriteBytes(from);
            from.position(from.limit());
            return;
        }
        if (len <= limit - this.end) {
            from.get(this.buff, this.end, len);
            this.end += len;
            return;
        }
        int avail = limit - this.end;
        from.get(this.buff, this.end, avail);
        this.end += avail;
        this.flushBuffer();
        final int fromLimit = from.limit();
        int remain;
        for (remain = len - avail, avail = limit - this.end; remain >= avail; remain -= avail) {
            from.limit(from.position() + avail);
            this.out.realWriteBytes(from);
            from.position(from.limit());
        }
        from.limit(fromLimit);
        from.get(this.buff, this.end, remain);
        this.end += remain;
    }
    
    public int substract() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++] & 0xFF;
    }
    
    public byte substractB() throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        return this.buff[this.start++];
    }
    
    public int substract(final byte[] dest, final int off, final int len) throws IOException {
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
    
    public int substract(final ByteBuffer to) throws IOException {
        if (this.checkEof()) {
            return -1;
        }
        final int n = Math.min(to.remaining(), this.getLength());
        to.put(this.buff, this.start, n);
        to.limit(to.position());
        to.position(to.position() - n);
        this.start += n;
        return n;
    }
    
    private boolean checkEof() throws IOException {
        if (this.end - this.start == 0) {
            if (this.in == null) {
                return true;
            }
            final int n = this.in.realReadBytes();
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
        this.out.realWriteBytes(this.buff, this.start, this.end - this.start);
        this.end = this.start;
    }
    
    public void makeSpace(final int count) {
        byte[] tmp = null;
        final int limit = this.getLimitInternal();
        long desiredSize = this.end + count;
        if (desiredSize > limit) {
            desiredSize = limit;
        }
        if (this.buff == null) {
            if (desiredSize < 256L) {
                desiredSize = 256L;
            }
            this.buff = new byte[(int)desiredSize];
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
        tmp = new byte[(int)newSize];
        System.arraycopy(this.buff, this.start, tmp, 0, this.end - this.start);
        this.buff = tmp;
        tmp = null;
        this.end -= this.start;
        this.start = 0;
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
        if (this.charset == null) {
            this.charset = ByteChunk.DEFAULT_CHARSET;
        }
        final CharBuffer cb = this.charset.decode(ByteBuffer.wrap(this.buff, this.start, this.end - this.start));
        return new String(cb.array(), cb.arrayOffset(), cb.length());
    }
    
    public long getLong() {
        return Ascii.parseLong(this.buff, this.start, this.end - this.start);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ByteChunk && this.equals((ByteChunk)obj);
    }
    
    public boolean equals(final String s) {
        final byte[] b = this.buff;
        final int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (b[off++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equalsIgnoreCase(final String s) {
        final byte[] b = this.buff;
        final int len = this.end - this.start;
        if (b == null || len != s.length()) {
            return false;
        }
        int off = this.start;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(b[off++]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public boolean equals(final ByteChunk bb) {
        return this.equals(bb.getBytes(), bb.getStart(), bb.getLength());
    }
    
    public boolean equals(final byte[] b2, int off2, final int len2) {
        final byte[] b3 = this.buff;
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
    
    public boolean equals(final CharChunk cc) {
        return this.equals(cc.getChars(), cc.getStart(), cc.getLength());
    }
    
    public boolean equals(final char[] c2, int off2, final int len2) {
        final byte[] b1 = this.buff;
        if (c2 == null && b1 == null) {
            return true;
        }
        if (b1 == null || c2 == null || this.end - this.start != len2) {
            return false;
        }
        int off3 = this.start;
        int len3 = this.end - this.start;
        while (len3-- > 0) {
            if ((char)b1[off3++] != c2[off2++]) {
                return false;
            }
        }
        return true;
    }
    
    public boolean startsWith(final String s, final int pos) {
        final byte[] b = this.buff;
        final int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (b[off++] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    public boolean startsWithIgnoreCase(final String s, final int pos) {
        final byte[] b = this.buff;
        final int len = s.length();
        if (b == null || len + pos > this.end - this.start) {
            return false;
        }
        int off = this.start + pos;
        for (int i = 0; i < len; ++i) {
            if (Ascii.toLower(b[off++]) != Ascii.toLower(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    protected int getBufferElement(final int index) {
        return this.buff[index];
    }
    
    public int indexOf(final char c, final int starting) {
        final int ret = indexOf(this.buff, this.start + starting, this.end, c);
        return (ret >= this.start) ? (ret - this.start) : -1;
    }
    
    public static int indexOf(final byte[] bytes, final int start, final int end, final char s) {
        for (int offset = start; offset < end; ++offset) {
            final byte b = bytes[offset];
            if (b == s) {
                return offset;
            }
        }
        return -1;
    }
    
    public static int findByte(final byte[] bytes, final int start, final int end, final byte b) {
        for (int offset = start; offset < end; ++offset) {
            if (bytes[offset] == b) {
                return offset;
            }
        }
        return -1;
    }
    
    public static int findBytes(final byte[] bytes, final int start, final int end, final byte[] b) {
        for (int offset = start; offset < end; ++offset) {
            for (final byte value : b) {
                if (bytes[offset] == value) {
                    return offset;
                }
            }
        }
        return -1;
    }
    
    public static final byte[] convertToBytes(final String value) {
        final byte[] result = new byte[value.length()];
        for (int i = 0; i < value.length(); ++i) {
            result[i] = (byte)value.charAt(i);
        }
        return result;
    }
    
    static {
        DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;
    }
    
    public interface ByteOutputChannel
    {
        void realWriteBytes(final byte[] p0, final int p1, final int p2) throws IOException;
        
        void realWriteBytes(final ByteBuffer p0) throws IOException;
    }
    
    public interface ByteInputChannel
    {
        int realReadBytes() throws IOException;
    }
}
