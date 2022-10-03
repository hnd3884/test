package org.apache.lucene.store;

import java.io.IOException;
import java.util.zip.Checksum;
import java.util.zip.CheckedOutputStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.util.zip.CRC32;

public class OutputStreamIndexOutput extends IndexOutput
{
    private final CRC32 crc;
    private final BufferedOutputStream os;
    private long bytesWritten;
    private boolean flushedOnClose;
    
    public OutputStreamIndexOutput(final String resourceDescription, final OutputStream out, final int bufferSize) {
        super(resourceDescription);
        this.crc = new CRC32();
        this.bytesWritten = 0L;
        this.flushedOnClose = false;
        this.os = new BufferedOutputStream(new CheckedOutputStream(out, this.crc), bufferSize);
    }
    
    @Override
    public final void writeByte(final byte b) throws IOException {
        this.os.write(b);
        ++this.bytesWritten;
    }
    
    @Override
    public final void writeBytes(final byte[] b, final int offset, final int length) throws IOException {
        this.os.write(b, offset, length);
        this.bytesWritten += length;
    }
    
    @Override
    public void close() throws IOException {
        try (final OutputStream o = this.os) {
            if (!this.flushedOnClose) {
                this.flushedOnClose = true;
                o.flush();
            }
        }
    }
    
    @Override
    public final long getFilePointer() {
        return this.bytesWritten;
    }
    
    @Override
    public final long getChecksum() throws IOException {
        this.os.flush();
        return this.crc.getValue();
    }
}
