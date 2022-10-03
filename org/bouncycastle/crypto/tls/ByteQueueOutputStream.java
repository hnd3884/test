package org.bouncycastle.crypto.tls;

import java.io.IOException;
import java.io.OutputStream;

public class ByteQueueOutputStream extends OutputStream
{
    private ByteQueue buffer;
    
    public ByteQueueOutputStream() {
        this.buffer = new ByteQueue();
    }
    
    public ByteQueue getBuffer() {
        return this.buffer;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.buffer.addData(new byte[] { (byte)n }, 0, 1);
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.buffer.addData(array, n, n2);
    }
}
