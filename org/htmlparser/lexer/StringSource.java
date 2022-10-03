package org.htmlparser.lexer;

import java.io.IOException;
import org.htmlparser.util.ParserException;

public class StringSource extends Source
{
    protected String mString;
    protected int mOffset;
    protected String mEncoding;
    protected int mMark;
    
    public StringSource(final String string) {
        this(string, "ISO-8859-1");
    }
    
    public StringSource(final String string, final String character_set) {
        this.mString = ((null == string) ? "" : string);
        this.mOffset = 0;
        this.mEncoding = character_set;
        this.mMark = -1;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
    public void setEncoding(final String character_set) throws ParserException {
        this.mEncoding = character_set;
    }
    
    public void close() throws IOException {
    }
    
    public int read() throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        int ret;
        if (this.mOffset >= this.mString.length()) {
            ret = -1;
        }
        else {
            ret = this.mString.charAt(this.mOffset);
            ++this.mOffset;
        }
        return ret;
    }
    
    public int read(final char[] cbuf, final int off, int len) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        final int length = this.mString.length();
        int ret;
        if (this.mOffset >= length) {
            ret = -1;
        }
        else {
            if (len > length - this.mOffset) {
                len = length - this.mOffset;
            }
            this.mString.getChars(this.mOffset, this.mOffset + len, cbuf, off);
            this.mOffset += len;
            ret = len;
        }
        return ret;
    }
    
    public int read(final char[] cbuf) throws IOException {
        return this.read(cbuf, 0, cbuf.length);
    }
    
    public boolean ready() throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        return this.mOffset < this.mString.length();
    }
    
    public void reset() throws IllegalStateException {
        if (null == this.mString) {
            throw new IllegalStateException("source is closed");
        }
        if (-1 != this.mMark) {
            this.mOffset = this.mMark;
        }
        else {
            this.mOffset = 0;
        }
    }
    
    public boolean markSupported() {
        return true;
    }
    
    public void mark(final int readAheadLimit) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        this.mMark = this.mOffset;
    }
    
    public long skip(long n) throws IOException, IllegalArgumentException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (0L > n) {
            throw new IllegalArgumentException("cannot skip backwards");
        }
        final int length = this.mString.length();
        if (this.mOffset >= length) {
            n = 0L;
        }
        else if (n > length - this.mOffset) {
            n = length - this.mOffset;
        }
        this.mOffset += (int)n;
        final long ret = n;
        return ret;
    }
    
    public void unread() throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (this.mOffset <= 0) {
            throw new IOException("can't unread no characters");
        }
        --this.mOffset;
    }
    
    public char getCharacter(final int offset) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (offset >= this.mOffset) {
            throw new IOException("read beyond current offset");
        }
        final char ret = this.mString.charAt(offset);
        return ret;
    }
    
    public void getCharacters(final char[] array, final int offset, final int start, final int end) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (end > this.mOffset) {
            throw new IOException("read beyond current offset");
        }
        this.mString.getChars(start, end, array, offset);
    }
    
    public String getString(final int offset, final int length) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (offset + length > this.mOffset) {
            throw new IOException("read beyond end of string");
        }
        final String ret = this.mString.substring(offset, offset + length);
        return ret;
    }
    
    public void getCharacters(final StringBuffer buffer, final int offset, final int length) throws IOException {
        if (null == this.mString) {
            throw new IOException("source is closed");
        }
        if (offset + length > this.mOffset) {
            throw new IOException("read beyond end of string");
        }
        buffer.append(this.mString.substring(offset, offset + length));
    }
    
    public void destroy() throws IOException {
        this.mString = null;
    }
    
    public int offset() {
        int ret;
        if (null == this.mString) {
            ret = -1;
        }
        else {
            ret = this.mOffset;
        }
        return ret;
    }
    
    public int available() {
        int ret;
        if (null == this.mString) {
            ret = 0;
        }
        else {
            ret = this.mString.length() - this.mOffset;
        }
        return ret;
    }
}
