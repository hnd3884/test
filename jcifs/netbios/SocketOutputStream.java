package jcifs.netbios;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class SocketOutputStream extends FilterOutputStream
{
    SocketOutputStream(final OutputStream out) {
        super(out);
    }
    
    public synchronized void write(final byte[] b, int off, final int len) throws IOException {
        if (len > 65535) {
            throw new IOException("write too large: " + len);
        }
        if (off < 4) {
            throw new IOException("NetBIOS socket output buffer requires 4 bytes available before off");
        }
        off -= 4;
        b[off + 1] = (b[off + 0] = 0);
        b[off + 2] = (byte)(len >> 8 & 0xFF);
        b[off + 3] = (byte)(len & 0xFF);
        this.out.write(b, off, 4 + len);
    }
}
