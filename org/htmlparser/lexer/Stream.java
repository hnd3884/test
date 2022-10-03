package org.htmlparser.lexer;

import java.io.IOException;
import java.io.InputStream;

public class Stream extends InputStream implements Runnable
{
    public int fills;
    public int reallocations;
    public int synchronous;
    protected static final int BUFFER_SIZE = 4096;
    protected static final int EOF = -1;
    protected volatile InputStream mIn;
    public volatile byte[] mBuffer;
    public volatile int mLevel;
    protected int mOffset;
    protected int mContentLength;
    protected int mMark;
    
    public Stream(final InputStream in) {
        this(in, 0);
    }
    
    public Stream(final InputStream in, final int bytes) {
        this.fills = 0;
        this.reallocations = 0;
        this.synchronous = 0;
        this.mIn = in;
        this.mBuffer = null;
        this.mLevel = 0;
        this.mOffset = 0;
        this.mContentLength = ((bytes < 0) ? 0 : bytes);
        this.mMark = -1;
    }
    
    protected synchronized boolean fill(final boolean force) throws IOException {
        boolean ret = false;
        if (null != this.mIn) {
            if (!force) {
                if (0 != this.available()) {
                    return true;
                }
                ++this.synchronous;
            }
            byte[] buffer;
            int size;
            if (0 == this.mContentLength) {
                if (null == this.mBuffer) {
                    this.mBuffer = new byte[Math.max(4096, this.mIn.available())];
                    buffer = this.mBuffer;
                }
                else if (this.mBuffer.length - this.mLevel < 2048) {
                    buffer = new byte[Math.max(this.mBuffer.length * 2, this.mBuffer.length + this.mIn.available())];
                }
                else {
                    buffer = this.mBuffer;
                }
                size = buffer.length - this.mLevel;
            }
            else {
                size = this.mContentLength - this.mLevel;
                if (null == this.mBuffer) {
                    this.mBuffer = new byte[size];
                }
                buffer = this.mBuffer;
            }
            final int read = this.mIn.read(buffer, this.mLevel, size);
            if (-1 == read) {
                this.mIn.close();
                this.mIn = null;
            }
            else {
                if (this.mBuffer != buffer) {
                    System.arraycopy(this.mBuffer, 0, buffer, 0, this.mLevel);
                    this.mBuffer = buffer;
                    ++this.reallocations;
                }
                this.mLevel += read;
                if (0 != this.mContentLength && this.mLevel == this.mContentLength) {
                    this.mIn.close();
                    this.mIn = null;
                }
                ret = true;
                ++this.fills;
            }
        }
        return ret;
    }
    
    public void run() {
        boolean filled;
        do {
            try {
                filled = this.fill(true);
            }
            catch (final IOException ioe) {
                ioe.printStackTrace();
                filled = false;
            }
        } while (filled);
    }
    
    public int read() throws IOException {
        if (0 == this.mLevel - this.mOffset) {
            this.fill(false);
        }
        int ret;
        if (0 != this.mLevel - this.mOffset) {
            ret = (this.mBuffer[this.mOffset++] & 0xFF);
        }
        else {
            ret = -1;
        }
        return ret;
    }
    
    public int available() throws IOException {
        return this.mLevel - this.mOffset;
    }
    
    public synchronized void close() throws IOException {
        if (null != this.mIn) {
            this.mIn.close();
            this.mIn = null;
        }
        this.mBuffer = null;
        this.mLevel = 0;
        this.mOffset = 0;
        this.mContentLength = 0;
        this.mMark = -1;
    }
    
    public void reset() throws IOException {
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
    
    public void mark(final int readlimit) {
        this.mMark = this.mOffset;
    }
}
