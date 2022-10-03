package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.Reader;

public class UnbufferedCharStream implements CharStream
{
    protected char[] data;
    protected int n;
    protected int p;
    protected int numMarkers;
    protected int lastChar;
    protected int lastCharBufferStart;
    protected int currentCharIndex;
    protected Reader input;
    public String name;
    
    public UnbufferedCharStream() {
        this(256);
    }
    
    public UnbufferedCharStream(final int bufferSize) {
        this.p = 0;
        this.numMarkers = 0;
        this.lastChar = -1;
        this.currentCharIndex = 0;
        this.n = 0;
        this.data = new char[bufferSize];
    }
    
    public UnbufferedCharStream(final InputStream input) {
        this(input, 256);
    }
    
    public UnbufferedCharStream(final Reader input) {
        this(input, 256);
    }
    
    public UnbufferedCharStream(final InputStream input, final int bufferSize) {
        this(bufferSize);
        this.input = new InputStreamReader(input);
        this.fill(1);
    }
    
    public UnbufferedCharStream(final Reader input, final int bufferSize) {
        this(bufferSize);
        this.input = input;
        this.fill(1);
    }
    
    @Override
    public void consume() {
        if (this.LA(1) == -1) {
            throw new IllegalStateException("cannot consume EOF");
        }
        this.lastChar = this.data[this.p];
        if (this.p == this.n - 1 && this.numMarkers == 0) {
            this.n = 0;
            this.p = -1;
            this.lastCharBufferStart = this.lastChar;
        }
        ++this.p;
        ++this.currentCharIndex;
        this.sync(1);
    }
    
    protected void sync(final int want) {
        final int need = this.p + want - 1 - this.n + 1;
        if (need > 0) {
            this.fill(need);
        }
    }
    
    protected int fill(final int n) {
        for (int i = 0; i < n; ++i) {
            if (this.n > 0 && this.data[this.n - 1] == '\uffff') {
                return i;
            }
            try {
                final int c = this.nextChar();
                this.add(c);
            }
            catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
        return n;
    }
    
    protected int nextChar() throws IOException {
        return this.input.read();
    }
    
    protected void add(final int c) {
        if (this.n >= this.data.length) {
            this.data = Arrays.copyOf(this.data, this.data.length * 2);
        }
        this.data[this.n++] = (char)c;
    }
    
    @Override
    public int LA(final int i) {
        if (i == -1) {
            return this.lastChar;
        }
        this.sync(i);
        final int index = this.p + i - 1;
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (index >= this.n) {
            return -1;
        }
        final char c = this.data[index];
        if (c == '\uffff') {
            return -1;
        }
        return c;
    }
    
    @Override
    public int mark() {
        if (this.numMarkers == 0) {
            this.lastCharBufferStart = this.lastChar;
        }
        final int mark = -this.numMarkers - 1;
        ++this.numMarkers;
        return mark;
    }
    
    @Override
    public void release(final int marker) {
        final int expectedMark = -this.numMarkers;
        if (marker != expectedMark) {
            throw new IllegalStateException("release() called with an invalid marker.");
        }
        --this.numMarkers;
        if (this.numMarkers == 0 && this.p > 0) {
            System.arraycopy(this.data, this.p, this.data, 0, this.n - this.p);
            this.n -= this.p;
            this.p = 0;
            this.lastCharBufferStart = this.lastChar;
        }
    }
    
    @Override
    public int index() {
        return this.currentCharIndex;
    }
    
    @Override
    public void seek(int index) {
        if (index == this.currentCharIndex) {
            return;
        }
        if (index > this.currentCharIndex) {
            this.sync(index - this.currentCharIndex);
            index = Math.min(index, this.getBufferStartIndex() + this.n - 1);
        }
        final int i = index - this.getBufferStartIndex();
        if (i < 0) {
            throw new IllegalArgumentException("cannot seek to negative index " + index);
        }
        if (i >= this.n) {
            throw new UnsupportedOperationException("seek to index outside buffer: " + index + " not in " + this.getBufferStartIndex() + ".." + (this.getBufferStartIndex() + this.n));
        }
        this.p = i;
        this.currentCharIndex = index;
        if (this.p == 0) {
            this.lastChar = this.lastCharBufferStart;
        }
        else {
            this.lastChar = this.data[this.p - 1];
        }
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
    }
    
    @Override
    public String getSourceName() {
        if (this.name == null || this.name.isEmpty()) {
            return "<unknown>";
        }
        return this.name;
    }
    
    @Override
    public String getText(final Interval interval) {
        if (interval.a < 0 || interval.b < interval.a - 1) {
            throw new IllegalArgumentException("invalid interval");
        }
        final int bufferStartIndex = this.getBufferStartIndex();
        if (this.n > 0 && this.data[this.n - 1] == '\uffff' && interval.a + interval.length() > bufferStartIndex + this.n) {
            throw new IllegalArgumentException("the interval extends past the end of the stream");
        }
        if (interval.a < bufferStartIndex || interval.b >= bufferStartIndex + this.n) {
            throw new UnsupportedOperationException("interval " + interval + " outside buffer: " + bufferStartIndex + ".." + (bufferStartIndex + this.n - 1));
        }
        final int i = interval.a - bufferStartIndex;
        return new String(this.data, i, interval.length());
    }
    
    protected final int getBufferStartIndex() {
        return this.currentCharIndex - this.p;
    }
}
