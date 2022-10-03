package com.sun.imageio.plugins.png;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class ChunkStream extends ImageOutputStreamImpl
{
    private ImageOutputStream stream;
    private long startPos;
    private CRC crc;
    
    public ChunkStream(final int n, final ImageOutputStream stream) throws IOException {
        this.crc = new CRC();
        this.stream = stream;
        this.startPos = stream.getStreamPosition();
        stream.writeInt(-1);
        this.writeInt(n);
    }
    
    @Override
    public int read() throws IOException {
        throw new RuntimeException("Method not available");
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        throw new RuntimeException("Method not available");
    }
    
    @Override
    public void write(final byte[] array, final int n, final int n2) throws IOException {
        this.crc.update(array, n, n2);
        this.stream.write(array, n, n2);
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.crc.update(n);
        this.stream.write(n);
    }
    
    public void finish() throws IOException {
        this.stream.writeInt(this.crc.getValue());
        final long streamPosition = this.stream.getStreamPosition();
        this.stream.seek(this.startPos);
        this.stream.writeInt((int)(streamPosition - this.startPos) - 12);
        this.stream.seek(streamPosition);
        this.stream.flushBefore(streamPosition);
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
}
