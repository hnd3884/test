package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

public class BEROctetStringGenerator extends BERGenerator
{
    public BEROctetStringGenerator(final OutputStream outputStream) throws IOException {
        super(outputStream);
        this.writeBERHeader(36);
    }
    
    public BEROctetStringGenerator(final OutputStream outputStream, final int n, final boolean b) throws IOException {
        super(outputStream, n, b);
        this.writeBERHeader(36);
    }
    
    public OutputStream getOctetOutputStream() {
        return this.getOctetOutputStream(new byte[1000]);
    }
    
    public OutputStream getOctetOutputStream(final byte[] array) {
        return new BufferedBEROctetStream(array);
    }
    
    private class BufferedBEROctetStream extends OutputStream
    {
        private byte[] _buf;
        private int _off;
        private DEROutputStream _derOut;
        
        BufferedBEROctetStream(final byte[] buf) {
            this._buf = buf;
            this._off = 0;
            this._derOut = new DEROutputStream(BEROctetStringGenerator.this._out);
        }
        
        @Override
        public void write(final int n) throws IOException {
            this._buf[this._off++] = (byte)n;
            if (this._off == this._buf.length) {
                DEROctetString.encode(this._derOut, this._buf);
                this._off = 0;
            }
        }
        
        @Override
        public void write(final byte[] array, int n, int i) throws IOException {
            while (i > 0) {
                final int min = Math.min(i, this._buf.length - this._off);
                System.arraycopy(array, n, this._buf, this._off, min);
                this._off += min;
                if (this._off < this._buf.length) {
                    break;
                }
                DEROctetString.encode(this._derOut, this._buf);
                this._off = 0;
                n += min;
                i -= min;
            }
        }
        
        @Override
        public void close() throws IOException {
            if (this._off != 0) {
                final byte[] array = new byte[this._off];
                System.arraycopy(this._buf, 0, array, 0, this._off);
                DEROctetString.encode(this._derOut, array);
            }
            BEROctetStringGenerator.this.writeBEREnd();
        }
    }
}
