package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import java.util.Arrays;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

public class ANTLRInputStream implements CharStream
{
    public static final int READ_BUFFER_SIZE = 1024;
    public static final int INITIAL_BUFFER_SIZE = 1024;
    protected char[] data;
    protected int n;
    protected int p;
    public String name;
    
    public ANTLRInputStream() {
        this.p = 0;
    }
    
    public ANTLRInputStream(final String input) {
        this.p = 0;
        this.data = input.toCharArray();
        this.n = input.length();
    }
    
    public ANTLRInputStream(final char[] data, final int numberOfActualCharsInArray) {
        this.p = 0;
        this.data = data;
        this.n = numberOfActualCharsInArray;
    }
    
    public ANTLRInputStream(final Reader r) throws IOException {
        this(r, 1024, 1024);
    }
    
    public ANTLRInputStream(final Reader r, final int initialSize) throws IOException {
        this(r, initialSize, 1024);
    }
    
    public ANTLRInputStream(final Reader r, final int initialSize, final int readChunkSize) throws IOException {
        this.p = 0;
        this.load(r, initialSize, readChunkSize);
    }
    
    public ANTLRInputStream(final InputStream input) throws IOException {
        this(new InputStreamReader(input), 1024);
    }
    
    public ANTLRInputStream(final InputStream input, final int initialSize) throws IOException {
        this(new InputStreamReader(input), initialSize);
    }
    
    public ANTLRInputStream(final InputStream input, final int initialSize, final int readChunkSize) throws IOException {
        this(new InputStreamReader(input), initialSize, readChunkSize);
    }
    
    public void load(final Reader r, int size, int readChunkSize) throws IOException {
        if (r == null) {
            return;
        }
        if (size <= 0) {
            size = 1024;
        }
        if (readChunkSize <= 0) {
            readChunkSize = 1024;
        }
        try {
            this.data = new char[size];
            int numRead = 0;
            int p = 0;
            do {
                if (p + readChunkSize > this.data.length) {
                    this.data = Arrays.copyOf(this.data, this.data.length * 2);
                }
                numRead = r.read(this.data, p, readChunkSize);
                p += numRead;
            } while (numRead != -1);
            this.n = p + 1;
        }
        finally {
            r.close();
        }
    }
    
    public void reset() {
        this.p = 0;
    }
    
    @Override
    public void consume() {
        if (this.p < this.n) {
            if (this.p < this.n) {
                ++this.p;
            }
            return;
        }
        assert this.LA(1) == -1;
        throw new IllegalStateException("cannot consume EOF");
    }
    
    @Override
    public int LA(int i) {
        if (i == 0) {
            return 0;
        }
        if (i < 0) {
            ++i;
            if (this.p + i - 1 < 0) {
                return -1;
            }
        }
        if (this.p + i - 1 >= this.n) {
            return -1;
        }
        return this.data[this.p + i - 1];
    }
    
    public int LT(final int i) {
        return this.LA(i);
    }
    
    @Override
    public int index() {
        return this.p;
    }
    
    @Override
    public int size() {
        return this.n;
    }
    
    @Override
    public int mark() {
        return -1;
    }
    
    @Override
    public void release(final int marker) {
    }
    
    @Override
    public void seek(int index) {
        if (index <= this.p) {
            this.p = index;
            return;
        }
        index = Math.min(index, this.n);
        while (this.p < index) {
            this.consume();
        }
    }
    
    @Override
    public String getText(final Interval interval) {
        final int start = interval.a;
        int stop = interval.b;
        if (stop >= this.n) {
            stop = this.n - 1;
        }
        final int count = stop - start + 1;
        if (start >= this.n) {
            return "";
        }
        return new String(this.data, start, count);
    }
    
    @Override
    public String getSourceName() {
        if (this.name == null || this.name.isEmpty()) {
            return "<unknown>";
        }
        return this.name;
    }
    
    @Override
    public String toString() {
        return new String(this.data);
    }
}
