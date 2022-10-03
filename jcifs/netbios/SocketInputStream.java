package jcifs.netbios;

import java.io.IOException;
import java.io.InputStream;

class SocketInputStream extends InputStream
{
    private static final int TMP_BUFFER_SIZE = 256;
    private InputStream in;
    private SessionServicePacket ssp;
    private int tot;
    private int bip;
    private int n;
    private byte[] header;
    private byte[] tmp;
    
    SocketInputStream(final InputStream in) {
        this.in = in;
        this.header = new byte[4];
        this.tmp = new byte[256];
    }
    
    public synchronized int read() throws IOException {
        if (this.read(this.tmp, 0, 1) < 0) {
            return -1;
        }
        return this.tmp[0] & 0xFF;
    }
    
    public synchronized int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    public synchronized int read(final byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        this.tot = 0;
        while (true) {
            if (this.bip > 0) {
                this.n = this.in.read(b, off, Math.min(len, this.bip));
                if (this.n == -1) {
                    return (this.tot > 0) ? this.tot : -1;
                }
                this.tot += this.n;
                off += this.n;
                len -= this.n;
                this.bip -= this.n;
                if (len == 0) {
                    return this.tot;
                }
                continue;
            }
            else {
                switch (SessionServicePacket.readPacketType(this.in, this.header, 0)) {
                    case 133: {
                        continue;
                    }
                    case 0: {
                        this.bip = SessionServicePacket.readLength(this.header, 0);
                        continue;
                    }
                    case -1: {
                        if (this.tot > 0) {
                            return this.tot;
                        }
                        return -1;
                    }
                }
            }
        }
    }
    
    public synchronized long skip(final long numbytes) throws IOException {
        if (numbytes <= 0L) {
            return 0L;
        }
        long n;
        int r;
        for (n = numbytes; n > 0L; n -= r) {
            r = this.read(this.tmp, 0, (int)Math.min(256L, n));
            if (r < 0) {
                break;
            }
        }
        return numbytes - n;
    }
    
    public int available() throws IOException {
        if (this.bip > 0) {
            return this.bip;
        }
        return this.in.available();
    }
    
    public void close() throws IOException {
        this.in.close();
    }
}
