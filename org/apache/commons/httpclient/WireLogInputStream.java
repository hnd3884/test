package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class WireLogInputStream extends FilterInputStream
{
    private InputStream in;
    private Wire wire;
    
    public WireLogInputStream(final InputStream in, final Wire wire) {
        super(in);
        this.in = in;
        this.wire = wire;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int l = this.in.read(b, off, len);
        if (l > 0) {
            this.wire.input(b, off, l);
        }
        return l;
    }
    
    public int read() throws IOException {
        final int l = this.in.read();
        if (l > 0) {
            this.wire.input(l);
        }
        return l;
    }
    
    public int read(final byte[] b) throws IOException {
        final int l = this.in.read(b);
        if (l > 0) {
            this.wire.input(b, 0, l);
        }
        return l;
    }
}
