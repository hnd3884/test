package org.htmlparser.lexer;

import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.ParserException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.InputStream;

public class InputStreamSource extends Source
{
    public static int BUFFER_SIZE;
    protected transient InputStream mStream;
    protected String mEncoding;
    protected transient InputStreamReader mReader;
    protected char[] mBuffer;
    protected int mLevel;
    protected int mOffset;
    protected int mMark;
    
    public InputStreamSource(final InputStream stream) throws UnsupportedEncodingException {
        this(stream, null, InputStreamSource.BUFFER_SIZE);
    }
    
    public InputStreamSource(final InputStream stream, final String charset) throws UnsupportedEncodingException {
        this(stream, charset, InputStreamSource.BUFFER_SIZE);
    }
    
    public InputStreamSource(InputStream stream, final String charset, final int size) throws UnsupportedEncodingException {
        if (null == stream) {
            stream = new Stream(null);
        }
        else if (!stream.markSupported()) {
            stream = new Stream(stream);
        }
        this.mStream = stream;
        if (null == charset) {
            this.mReader = new InputStreamReader(stream);
            this.mEncoding = this.mReader.getEncoding();
        }
        else {
            this.mEncoding = charset;
            this.mReader = new InputStreamReader(stream, charset);
        }
        this.mBuffer = new char[size];
        this.mLevel = 0;
        this.mOffset = 0;
        this.mMark = -1;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        if (null != this.mStream) {
            final int offset = this.mOffset;
            final char[] buffer = new char[4096];
            while (-1 != this.read(buffer)) {}
            this.mOffset = offset;
        }
        out.defaultWriteObject();
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (null != this.mBuffer) {
            this.mStream = new ByteArrayInputStream(new byte[0]);
        }
    }
    
    public InputStream getStream() {
        return this.mStream;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
    public void setEncoding(final String character_set) throws ParserException {
        final String encoding = this.getEncoding();
        if (!encoding.equalsIgnoreCase(character_set)) {
            final InputStream stream = this.getStream();
            try {
                final char[] buffer = this.mBuffer;
                final int offset = this.mOffset;
                stream.reset();
                try {
                    this.mEncoding = character_set;
                    this.mReader = new InputStreamReader(stream, character_set);
                    this.mBuffer = new char[this.mBuffer.length];
                    this.mLevel = 0;
                    this.mOffset = 0;
                    this.mMark = -1;
                    if (0 != offset) {
                        final char[] new_chars = new char[offset];
                        if (offset != this.read(new_chars)) {
                            throw new ParserException("reset stream failed");
                        }
                        for (int i = 0; i < offset; ++i) {
                            if (new_chars[i] != buffer[i]) {
                                throw new EncodingChangeException("character mismatch (new: " + new_chars[i] + " [0x" + Integer.toString(new_chars[i], 16) + "] != old: " + " [0x" + Integer.toString(buffer[i], 16) + buffer[i] + "]) for encoding change from " + encoding + " to " + character_set + " at character offset " + i);
                            }
                        }
                    }
                }
                catch (final IOException ioe) {
                    throw new ParserException(ioe.getMessage(), ioe);
                }
            }
            catch (final IOException ioe) {
                throw new ParserException("Stream reset failed (" + ioe.getMessage() + "), try wrapping it with a org.htmlparser.lexer.Stream", ioe);
            }
        }
    }
    
    protected void fill(int min) throws IOException {
        if (null != this.mReader) {
            int size = this.mBuffer.length - this.mLevel;
            char[] buffer;
            if (size < min) {
                size = this.mBuffer.length * 2;
                final int read = this.mLevel + min;
                if (size < read) {
                    size = read;
                }
                else {
                    min = size - this.mLevel;
                }
                buffer = new char[size];
            }
            else {
                buffer = this.mBuffer;
                min = size;
            }
            final int read = this.mReader.read(buffer, this.mLevel, min);
            if (-1 == read) {
                this.mReader.close();
                this.mReader = null;
            }
            else {
                if (this.mBuffer != buffer) {
                    System.arraycopy(this.mBuffer, 0, buffer, 0, this.mLevel);
                    this.mBuffer = buffer;
                }
                this.mLevel += read;
            }
        }
    }
    
    public void close() throws IOException {
    }
    
    public int read() throws IOException {
        int ret;
        if (this.mLevel - this.mOffset < 1) {
            if (null == this.mStream) {
                throw new IOException("source is closed");
            }
            this.fill(1);
            if (this.mOffset >= this.mLevel) {
                ret = -1;
            }
            else {
                ret = this.mBuffer[this.mOffset++];
            }
        }
        else {
            ret = this.mBuffer[this.mOffset++];
        }
        return ret;
    }
    
    public int read(final char[] cbuf, final int off, final int len) throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        if (null == cbuf || 0 > off || 0 > len) {
            throw new IOException("illegal argument read (" + ((null == cbuf) ? "null" : "cbuf") + ", " + off + ", " + len + ")");
        }
        if (this.mLevel - this.mOffset < len) {
            this.fill(len - (this.mLevel - this.mOffset));
        }
        int ret;
        if (this.mOffset >= this.mLevel) {
            ret = -1;
        }
        else {
            ret = Math.min(this.mLevel - this.mOffset, len);
            System.arraycopy(this.mBuffer, this.mOffset, cbuf, off, ret);
            this.mOffset += ret;
        }
        return ret;
    }
    
    public int read(final char[] cbuf) throws IOException {
        return this.read(cbuf, 0, cbuf.length);
    }
    
    public void reset() throws IllegalStateException {
        if (null == this.mStream) {
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
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        this.mMark = this.mOffset;
    }
    
    public boolean ready() throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        return this.mOffset < this.mLevel;
    }
    
    public long skip(final long n) throws IOException, IllegalArgumentException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        if (0L > n) {
            throw new IllegalArgumentException("cannot skip backwards");
        }
        if (this.mLevel - this.mOffset < n) {
            this.fill((int)(n - (this.mLevel - this.mOffset)));
        }
        long ret;
        if (this.mOffset >= this.mLevel) {
            ret = -1L;
        }
        else {
            ret = Math.min(this.mLevel - this.mOffset, n);
            this.mOffset += (int)ret;
        }
        return ret;
    }
    
    public void unread() throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        if (0 < this.mOffset) {
            --this.mOffset;
            return;
        }
        throw new IOException("can't unread no characters");
    }
    
    public char getCharacter(final int offset) throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        if (offset >= this.mBuffer.length) {
            throw new IOException("illegal read ahead");
        }
        final char ret = this.mBuffer[offset];
        return ret;
    }
    
    public void getCharacters(final char[] array, final int offset, final int start, final int end) throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        System.arraycopy(this.mBuffer, start, array, offset, end - start);
    }
    
    public String getString(final int offset, final int length) throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        if (offset + length > this.mBuffer.length) {
            throw new IOException("illegal read ahead");
        }
        final String ret = new String(this.mBuffer, offset, length);
        return ret;
    }
    
    public void getCharacters(final StringBuffer buffer, final int offset, final int length) throws IOException {
        if (null == this.mStream) {
            throw new IOException("source is closed");
        }
        buffer.append(this.mBuffer, offset, length);
    }
    
    public void destroy() throws IOException {
        this.mStream = null;
        if (null != this.mReader) {
            this.mReader.close();
        }
        this.mReader = null;
        this.mBuffer = null;
        this.mLevel = 0;
        this.mOffset = 0;
        this.mMark = -1;
    }
    
    public int offset() {
        int ret;
        if (null == this.mStream) {
            ret = -1;
        }
        else {
            ret = this.mOffset;
        }
        return ret;
    }
    
    public int available() {
        int ret;
        if (null == this.mStream) {
            ret = 0;
        }
        else {
            ret = this.mLevel - this.mOffset;
        }
        return ret;
    }
    
    static {
        InputStreamSource.BUFFER_SIZE = 16384;
    }
}
