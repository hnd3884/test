package org.bouncycastle.util.io;

import org.bouncycastle.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;

public class BufferingOutputStream extends OutputStream
{
    private final OutputStream other;
    private final byte[] buf;
    private int bufOff;
    
    public BufferingOutputStream(final OutputStream other) {
        this.other = other;
        this.buf = new byte[4096];
    }
    
    public BufferingOutputStream(final OutputStream other, final int n) {
        this.other = other;
        this.buf = new byte[n];
    }
    
    @Override
    public void write(final byte[] array, int n, int i) throws IOException {
        if (i < this.buf.length - this.bufOff) {
            System.arraycopy(array, n, this.buf, this.bufOff, i);
            this.bufOff += i;
        }
        else {
            final int n2 = this.buf.length - this.bufOff;
            System.arraycopy(array, n, this.buf, this.bufOff, n2);
            this.bufOff += n2;
            this.flush();
            n += n2;
            for (i -= n2; i >= this.buf.length; i -= this.buf.length) {
                this.other.write(array, n, this.buf.length);
                n += this.buf.length;
            }
            if (i > 0) {
                System.arraycopy(array, n, this.buf, this.bufOff, i);
                this.bufOff += i;
            }
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.buf[this.bufOff++] = (byte)n;
        if (this.bufOff == this.buf.length) {
            this.flush();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.other.write(this.buf, 0, this.bufOff);
        this.bufOff = 0;
        Arrays.fill(this.buf, (byte)0);
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.other.close();
    }
}
