package org.apache.lucene.store;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Closeable;

public class OutputStreamDataOutput extends DataOutput implements Closeable
{
    private final OutputStream os;
    
    public OutputStreamDataOutput(final OutputStream os) {
        this.os = os;
    }
    
    @Override
    public void writeByte(final byte b) throws IOException {
        this.os.write(b);
    }
    
    @Override
    public void writeBytes(final byte[] b, final int offset, final int length) throws IOException {
        this.os.write(b, offset, length);
    }
    
    @Override
    public void close() throws IOException {
        this.os.close();
    }
}
