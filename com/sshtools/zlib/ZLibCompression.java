package com.sshtools.zlib;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import com.maverick.ssh.compression.SshCompression;

public class ZLibCompression implements SshCompression
{
    ByteArrayOutputStream b;
    ByteArrayOutputStream f;
    private ZStream d;
    private byte[] e;
    private byte[] c;
    
    public ZLibCompression() {
        this.b = new ByteArrayOutputStream(65535);
        this.f = new ByteArrayOutputStream(65535);
        this.e = new byte[65535];
        this.c = new byte[65535];
        this.d = new ZStream();
    }
    
    public String getAlgorithm() {
        return "zlib";
    }
    
    public void init(final int n, final int n2) {
        if (n == 1) {
            this.d.deflateInit(n2);
        }
        else if (n == 0) {
            this.d.inflateInit();
        }
    }
    
    public byte[] compress(final byte[] next_in, final int next_in_index, final int n) throws IOException {
        this.b.reset();
        this.d.next_in = next_in;
        this.d.next_in_index = next_in_index;
        this.d.avail_in = n - next_in_index;
        do {
            this.d.next_out = this.c;
            this.d.next_out_index = 0;
            this.d.avail_out = 65535;
            final int deflate = this.d.deflate(1);
            switch (deflate) {
                case 0: {
                    this.b.write(this.c, 0, 65535 - this.d.avail_out);
                    continue;
                }
                default: {
                    throw new IOException("compress: deflate returnd " + deflate);
                }
            }
        } while (this.d.avail_out == 0);
        return this.b.toByteArray();
    }
    
    public byte[] uncompress(final byte[] next_in, final int next_in_index, final int avail_in) throws IOException {
        this.f.reset();
        this.d.next_in = next_in;
        this.d.next_in_index = next_in_index;
        this.d.avail_in = avail_in;
        while (true) {
            this.d.next_out = this.e;
            this.d.next_out_index = 0;
            this.d.avail_out = 65535;
            final int inflate = this.d.inflate(1);
            switch (inflate) {
                case 0: {
                    this.f.write(this.e, 0, 65535 - this.d.avail_out);
                    continue;
                }
                case -5: {
                    return this.f.toByteArray();
                }
                default: {
                    throw new IOException("uncompress: inflate returnd " + inflate);
                }
            }
        }
    }
}
