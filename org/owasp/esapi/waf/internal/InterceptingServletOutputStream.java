package org.owasp.esapi.waf.internal;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import javax.servlet.ServletOutputStream;

public class InterceptingServletOutputStream extends ServletOutputStream
{
    private static final int FLUSH_BLOCK_SIZE = 1024;
    private ServletOutputStream os;
    private boolean buffering;
    private boolean committed;
    private boolean closed;
    private RandomAccessFile out;
    
    public InterceptingServletOutputStream(final ServletOutputStream os, final boolean buffered) throws FileNotFoundException, IOException {
        this.os = os;
        this.buffering = buffered;
        this.committed = false;
        this.closed = false;
        final File tempFile = File.createTempFile("oew", ".hop");
        this.out = new RandomAccessFile(tempFile, "rw");
        tempFile.deleteOnExit();
    }
    
    public void reset() throws IOException {
        this.out.setLength(0L);
    }
    
    public byte[] getResponseBytes() throws IOException {
        final byte[] buffer = new byte[(int)this.out.length()];
        this.out.seek(0L);
        this.out.read(buffer, 0, (int)this.out.length());
        this.out.seek(this.out.length());
        return buffer;
    }
    
    public void setResponseBytes(final byte[] responseBytes) throws IOException {
        if (!this.buffering && this.out.length() > 0L) {
            throw new IOException("Already committed response because not currently buffering");
        }
        this.out.setLength(0L);
        this.out.write(responseBytes);
    }
    
    public void write(final int i) throws IOException {
        if (!this.buffering) {
            this.os.write(i);
        }
        this.out.write(i);
    }
    
    public void write(final byte[] b) throws IOException {
        if (!this.buffering) {
            this.os.write(b, 0, b.length);
        }
        this.out.write(b, 0, b.length);
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (!this.buffering) {
            this.os.write(b, off, len);
        }
        this.out.write(b, off, len);
    }
    
    public void flush() throws IOException {
        if (this.buffering) {
            synchronized (this.out) {
                this.out.seek(0L);
                final byte[] buff = new byte[1024];
                int amountToWrite;
                for (int i = 0; i < this.out.length(); i += amountToWrite) {
                    final long currentPos = this.out.getFilePointer();
                    final long totalSize = this.out.length();
                    amountToWrite = 1024;
                    if (totalSize - currentPos < 1024L) {
                        amountToWrite = (int)(totalSize - currentPos);
                    }
                    this.out.read(buff, 0, amountToWrite);
                    this.os.write(buff, 0, amountToWrite);
                }
                this.out.setLength(0L);
            }
        }
    }
    
    public void commit() throws IOException {
        if (!this.buffering) {
            return;
        }
        this.flush();
        this.committed = true;
    }
    
    public void close() throws IOException {
        if (!this.buffering) {
            this.os.close();
        }
        this.closed = true;
    }
}
