package org.tanukisoftware.wrapper;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class WrapperProcessInputStream extends InputStream
{
    private long m_ptr;
    private boolean m_closed;
    private ByteArrayInputStream m_bais;
    private volatile boolean m_read;
    
    private WrapperProcessInputStream() {
    }
    
    private native int nativeRead(final boolean p0);
    
    private native void nativeClose();
    
    private native int nativeRead2(final byte[] p0, final int p1, final int p2, final boolean p3);
    
    public void close() throws IOException {
        synchronized (this) {
            if (!this.m_closed) {
                if (WrapperManager.isNativeLibraryOk()) {
                    this.nativeClose();
                }
                this.m_closed = true;
            }
        }
    }
    
    public boolean markSupported() {
        return false;
    }
    
    public boolean ready() {
        synchronized (this) {
            return !this.m_closed || (this.m_bais != null && this.m_bais.available() > 0);
        }
    }
    
    public int read() throws IOException {
        synchronized (this) {
            this.m_read = true;
            if (!this.m_closed && WrapperManager.isNativeLibraryOk()) {
                return this.nativeRead(true);
            }
            if (this.m_bais != null) {
                return this.m_bais.read();
            }
            throw new IOException(WrapperManager.getRes().getString("Stream is closed."));
        }
    }
    
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        synchronized (this) {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }
            if (!this.ready()) {
                return -1;
            }
            this.m_read = true;
            if (!this.m_closed && WrapperManager.isNativeLibraryOk()) {
                int c = this.nativeRead2(b, off, len, true);
                if (c == -1) {
                    c = this.nativeRead2(b, off, len, false);
                }
            }
            else {
                if (this.m_bais == null) {
                    throw new IOException(WrapperManager.getRes().getString("Stream is closed."));
                }
                final int c = this.m_bais.read(b, off, len);
            }
            final int n;
            return (n == 0) ? -1 : n;
        }
    }
    
    private void readAndCloseOpenFDs() {
        if (this.m_read) {
            return;
        }
        synchronized (this) {
            if (this.m_closed || !WrapperManager.isNativeLibraryOk()) {
                return;
            }
            try {
                byte[] buffer = new byte[1024];
                int count = 0;
                int msg;
                while ((msg = this.nativeRead(false)) != -1) {
                    if (count >= buffer.length) {
                        final byte[] temp = new byte[buffer.length + 1024];
                        System.arraycopy(buffer, 0, temp, 0, buffer.length);
                        buffer = temp;
                    }
                    buffer[count++] = (byte)msg;
                }
                this.m_bais = new ByteArrayInputStream(buffer, 0, count);
                this.close();
            }
            catch (final IOException ioe) {
                System.out.println(WrapperManager.getRes().getString("WrapperProcessStream encountered a ReadError: "));
                ioe.printStackTrace();
            }
        }
    }
}
