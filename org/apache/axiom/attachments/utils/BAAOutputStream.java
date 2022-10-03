package org.apache.axiom.attachments.utils;

import org.apache.axiom.ext.io.StreamCopyException;
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.axiom.ext.io.ReadFromSupport;
import java.io.OutputStream;

public class BAAOutputStream extends OutputStream implements ReadFromSupport
{
    ArrayList data;
    static final int BUFFER_SIZE = 4096;
    int index;
    byte[] currBuffer;
    byte[] writeByte;
    
    public BAAOutputStream() {
        this.data = new ArrayList();
        this.index = 0;
        this.currBuffer = null;
        this.writeByte = new byte[1];
        this.addBuffer();
    }
    
    private void addBuffer() {
        this.currBuffer = new byte[4096];
        this.data.add(this.currBuffer);
        this.index = 0;
    }
    
    @Override
    public void write(final byte[] b, int off, final int len) throws IOException {
        int total = 0;
        while (total < len) {
            final int copy = Math.min(len - total, 4096 - this.index);
            System.arraycopy(b, off, this.currBuffer, this.index, copy);
            total += copy;
            this.index += copy;
            off += copy;
            if (this.index >= 4096) {
                this.addBuffer();
            }
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.writeByte[0] = (byte)b;
        this.write(this.writeByte, 0, 1);
    }
    
    public ArrayList buffers() {
        return this.data;
    }
    
    public int length() {
        return 4096 * (this.data.size() - 1) + this.index;
    }
    
    public long receive(final InputStream is, final long maxRead) throws IOException {
        return this.readFrom(is, maxRead);
    }
    
    public long readFrom(final InputStream is, long maxRead) throws StreamCopyException {
        if (maxRead == -1L) {
            maxRead = Long.MAX_VALUE;
        }
        long bytesReceived = 0L;
        boolean done = false;
        while (!done) {
            final int len = (int)Math.min(4096 - this.index, maxRead - bytesReceived);
            int bytesRead;
            try {
                bytesRead = is.read(this.currBuffer, this.index, len);
            }
            catch (final IOException ex) {
                throw new StreamCopyException(1, ex);
            }
            if (bytesRead >= 0) {
                bytesReceived += bytesRead;
                this.index += bytesRead;
                if (this.index >= 4096) {
                    this.addBuffer();
                }
                if (bytesReceived < maxRead) {
                    continue;
                }
                done = true;
            }
            else {
                done = true;
            }
        }
        return bytesReceived;
    }
}
