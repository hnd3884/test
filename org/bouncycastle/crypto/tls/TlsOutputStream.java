package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

class TlsOutputStream extends OutputStream
{
    private byte[] buf;
    private TlsProtocol handler;
    
    TlsOutputStream(final TlsProtocol handler) {
        this.buf = new byte[1];
        this.handler = handler;
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.handler.writeData(array, n, n2);
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.buf[0] = (byte)n;
        this.write(this.buf, 0, 1);
    }
    
    @Override
    public void close() throws IOException {
        this.handler.close();
    }
    
    @Override
    public void flush() throws IOException {
        this.handler.flush();
    }
}
