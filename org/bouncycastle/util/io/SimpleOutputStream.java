package org.bouncycastle.util.io;

import java.io.IOException;
import java.io.OutputStream;

public abstract class SimpleOutputStream extends OutputStream
{
    @Override
    public void close() {
    }
    
    @Override
    public void flush() {
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
}
