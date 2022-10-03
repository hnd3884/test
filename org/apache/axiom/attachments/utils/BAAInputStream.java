package org.apache.axiom.attachments.utils;

import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.io.InputStream;

public class BAAInputStream extends InputStream
{
    ArrayList data;
    static final int BUFFER_SIZE = 4096;
    int i;
    int size;
    int currIndex;
    int totalIndex;
    int mark;
    byte[] currBuffer;
    byte[] read_byte;
    
    public BAAInputStream(final ArrayList data, final int size) {
        this.data = new ArrayList();
        this.mark = 0;
        this.currBuffer = null;
        this.read_byte = new byte[1];
        this.data = data;
        this.size = size;
        this.i = 0;
        this.currIndex = 0;
        this.totalIndex = 0;
        this.currBuffer = data.get(0);
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.read(this.read_byte);
        if (read < 0) {
            return -1;
        }
        return this.read_byte[0] & 0xFF;
    }
    
    @Override
    public int available() throws IOException {
        return this.size - this.totalIndex;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.mark = this.totalIndex;
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public int read(final byte[] b, int off, final int len) throws IOException {
        int total = 0;
        if (this.totalIndex >= this.size) {
            return -1;
        }
        while (total < len && this.totalIndex < this.size) {
            int copy = Math.min(len - total, 4096 - this.currIndex);
            copy = Math.min(copy, this.size - this.totalIndex);
            System.arraycopy(this.currBuffer, this.currIndex, b, off, copy);
            total += copy;
            this.currIndex += copy;
            this.totalIndex += copy;
            off += copy;
            if (this.currIndex >= 4096) {
                if (this.i + 1 < this.data.size()) {
                    this.currBuffer = this.data.get(this.i + 1);
                    ++this.i;
                    this.currIndex = 0;
                }
                else {
                    this.currBuffer = null;
                    this.currIndex = 4096;
                }
            }
        }
        return total;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.i = this.mark / 4096;
        this.currIndex = this.mark - this.i * 4096;
        this.currBuffer = this.data.get(this.i);
        this.totalIndex = this.mark;
    }
    
    public void writeTo(final OutputStream os) throws IOException {
        if (this.data != null) {
            final int numBuffers = this.data.size();
            for (int j = 0; j < numBuffers - 1; ++j) {
                os.write(this.data.get(j), 0, 4096);
            }
            if (numBuffers > 0) {
                final int writeLimit = this.size - (numBuffers - 1) * 4096;
                os.write(this.data.get(numBuffers - 1), 0, writeLimit);
            }
        }
    }
}
