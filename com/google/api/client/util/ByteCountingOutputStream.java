package com.google.api.client.util;

import java.io.IOException;
import java.io.OutputStream;

final class ByteCountingOutputStream extends OutputStream
{
    long count;
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.count += len;
    }
    
    @Override
    public void write(final int b) throws IOException {
        ++this.count;
    }
}
