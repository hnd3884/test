package org.tukaani.xz.rangecoder;

import java.io.IOException;
import java.io.OutputStream;

public final class RangeEncoderToStream extends RangeEncoder
{
    private final OutputStream out;
    
    public RangeEncoderToStream(final OutputStream out) {
        this.out = out;
        this.reset();
    }
    
    @Override
    void writeByte(final int n) throws IOException {
        this.out.write(n);
    }
}
