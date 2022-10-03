package org.bouncycastle.est;

import org.bouncycastle.util.encoders.Base64;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

class CTEBase64InputStream extends InputStream
{
    protected final InputStream src;
    protected final byte[] rawBuf;
    protected final byte[] data;
    protected final OutputStream dataOutputStream;
    protected final Long max;
    protected int rp;
    protected int wp;
    protected boolean end;
    protected long read;
    
    public CTEBase64InputStream(final InputStream src, final Long max) {
        this.rawBuf = new byte[1024];
        this.data = new byte[768];
        this.src = src;
        this.dataOutputStream = new OutputStream() {
            @Override
            public void write(final int n) throws IOException {
                CTEBase64InputStream.this.data[CTEBase64InputStream.this.wp++] = (byte)n;
            }
        };
        this.max = max;
    }
    
    protected int pullFromSrc() throws IOException {
        if (this.read >= this.max) {
            return -1;
        }
        int n = 0;
        int read;
        do {
            read = this.src.read();
            if (read >= 33 || read == 13 || read == 10) {
                if (n >= this.rawBuf.length) {
                    throw new IOException("Content Transfer Encoding, base64 line length > 1024");
                }
                this.rawBuf[n++] = (byte)read;
                ++this.read;
            }
            else {
                if (read < 0) {
                    continue;
                }
                ++this.read;
            }
        } while (read > -1 && n < this.rawBuf.length && read != 10 && this.read < this.max);
        if (n > 0) {
            try {
                Base64.decode(this.rawBuf, 0, n, this.dataOutputStream);
                return this.wp;
            }
            catch (final Exception ex) {
                throw new IOException("Decode Base64 Content-Transfer-Encoding: " + ex);
            }
        }
        if (read == -1) {
            return -1;
        }
        return this.wp;
    }
    
    @Override
    public int read() throws IOException {
        if (this.rp == this.wp) {
            this.rp = 0;
            this.wp = 0;
            final int pullFromSrc = this.pullFromSrc();
            if (pullFromSrc == -1) {
                return pullFromSrc;
            }
        }
        return this.data[this.rp++] & 0xFF;
    }
    
    @Override
    public void close() throws IOException {
        this.src.close();
    }
}
