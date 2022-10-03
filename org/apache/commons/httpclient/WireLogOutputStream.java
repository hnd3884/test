package org.apache.commons.httpclient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class WireLogOutputStream extends FilterOutputStream
{
    private OutputStream out;
    private Wire wire;
    
    public WireLogOutputStream(final OutputStream out, final Wire wire) {
        super(out);
        this.out = out;
        this.wire = wire;
    }
    
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        this.wire.output(b, off, len);
    }
    
    public void write(final int b) throws IOException {
        this.out.write(b);
        this.wire.output(b);
    }
    
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
        this.wire.output(b);
    }
}
