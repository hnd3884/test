package com.maverick.util;

import java.io.InterruptedIOException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class DynamicBuffer
{
    protected static final int DEFAULT_BUFFER_SIZE = 32768;
    protected byte[] buf;
    protected int writepos;
    protected int readpos;
    protected InputStream in;
    protected OutputStream out;
    private boolean b;
    private int c;
    
    public DynamicBuffer() {
        this.writepos = 0;
        this.readpos = 0;
        this.b = false;
        this.c = 5000;
        this.buf = new byte[32768];
        this.in = new _c();
        this.out = new _b();
    }
    
    public InputStream getInputStream() {
        return this.in;
    }
    
    public OutputStream getOutputStream() {
        return this.out;
    }
    
    private synchronized void b(final int n) {
        if (n > this.buf.length - this.writepos) {
            System.arraycopy(this.buf, this.readpos, this.buf, 0, this.writepos - this.readpos);
            this.writepos -= this.readpos;
            this.readpos = 0;
        }
        if (n > this.buf.length - this.writepos) {
            final byte[] buf = new byte[this.buf.length + 32768];
            System.arraycopy(this.buf, 0, buf, 0, this.writepos - this.readpos);
            this.buf = buf;
        }
    }
    
    protected synchronized int available() {
        return (this.writepos - this.readpos > 0) ? (this.writepos - this.readpos) : (this.b ? -1 : 0);
    }
    
    private synchronized void b() throws InterruptedException {
        if (!this.b) {
            while (this.readpos >= this.writepos && !this.b) {
                this.wait(this.c);
            }
        }
    }
    
    public synchronized void close() {
        if (!this.b) {
            this.b = true;
            this.notifyAll();
        }
    }
    
    protected synchronized void write(final int n) throws IOException {
        if (this.b) {
            throw new IOException("The buffer is closed");
        }
        this.b(1);
        this.buf[this.writepos] = (byte)n;
        ++this.writepos;
        this.notifyAll();
    }
    
    protected synchronized void write(final byte[] array, final int n, final int n2) throws IOException {
        if (this.b) {
            throw new IOException("The buffer is closed");
        }
        this.b(n2);
        System.arraycopy(array, n, this.buf, this.writepos, n2);
        this.writepos += n2;
        this.notifyAll();
    }
    
    public void setBlockInterrupt(final int c) {
        this.c = c;
    }
    
    protected synchronized int read() throws IOException {
        try {
            this.b();
        }
        catch (final InterruptedException ex) {
            throw new InterruptedIOException("The blocking operation was interrupted");
        }
        if (this.b && this.available() <= 0) {
            return -1;
        }
        return this.buf[this.readpos++];
    }
    
    protected synchronized int read(final byte[] array, final int n, final int n2) throws IOException {
        try {
            this.b();
        }
        catch (final InterruptedException ex) {
            throw new InterruptedIOException("The blocking operation was interrupted");
        }
        if (this.b && this.available() <= 0) {
            return -1;
        }
        final int n3 = (n2 > this.writepos - this.readpos) ? (this.writepos - this.readpos) : n2;
        System.arraycopy(this.buf, this.readpos, array, n, n3);
        this.readpos += n3;
        return n3;
    }
    
    protected synchronized void flush() throws IOException {
        this.notifyAll();
    }
    
    class _b extends OutputStream
    {
        public void write(final int n) throws IOException {
            DynamicBuffer.this.write(n);
        }
        
        public void write(final byte[] array, final int n, final int n2) throws IOException {
            DynamicBuffer.this.write(array, n, n2);
        }
        
        public void flush() throws IOException {
            DynamicBuffer.this.flush();
        }
        
        public void close() {
            DynamicBuffer.this.close();
        }
    }
    
    class _c extends InputStream
    {
        public int read() throws IOException {
            return DynamicBuffer.this.read();
        }
        
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            return DynamicBuffer.this.read(array, n, n2);
        }
        
        public int available() {
            return DynamicBuffer.this.available();
        }
        
        public void close() {
            DynamicBuffer.this.close();
        }
    }
}
