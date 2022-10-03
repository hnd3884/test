package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.zip.Deflater;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class IDATOutputStream extends ImageOutputStreamImpl
{
    private static byte[] chunkType;
    private ImageOutputStream stream;
    private int chunkLength;
    private long startPos;
    private CRC crc;
    Deflater def;
    byte[] buf;
    private int bytesRemaining;
    
    public IDATOutputStream(final ImageOutputStream stream, final int chunkLength) throws IOException {
        this.crc = new CRC();
        this.def = new Deflater(9);
        this.buf = new byte[512];
        this.stream = stream;
        this.chunkLength = chunkLength;
        this.startChunk();
    }
    
    private void startChunk() throws IOException {
        this.crc.reset();
        this.startPos = this.stream.getStreamPosition();
        this.stream.writeInt(-1);
        this.crc.update(IDATOutputStream.chunkType, 0, 4);
        this.stream.write(IDATOutputStream.chunkType, 0, 4);
        this.bytesRemaining = this.chunkLength;
    }
    
    private void finishChunk() throws IOException {
        this.stream.writeInt(this.crc.getValue());
        final long streamPosition = this.stream.getStreamPosition();
        this.stream.seek(this.startPos);
        this.stream.writeInt((int)(streamPosition - this.startPos) - 12);
        this.stream.seek(streamPosition);
        this.stream.flushBefore(streamPosition);
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
        if (n2 == 0) {
            return;
        }
        if (!this.def.finished()) {
            this.def.setInput(array, n, n2);
            while (!this.def.needsInput()) {
                this.deflate();
            }
        }
    }
    
    public void deflate() throws IOException {
        int i = this.def.deflate(this.buf, 0, this.buf.length);
        int n = 0;
        while (i > 0) {
            if (this.bytesRemaining == 0) {
                this.finishChunk();
                this.startChunk();
            }
            final int min = Math.min(i, this.bytesRemaining);
            this.crc.update(this.buf, n, min);
            this.stream.write(this.buf, n, min);
            n += min;
            i -= min;
            this.bytesRemaining -= min;
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.write(new byte[] { (byte)n }, 0, 1);
    }
    
    public void finish() throws IOException {
        try {
            if (!this.def.finished()) {
                this.def.finish();
                while (!this.def.finished()) {
                    this.deflate();
                }
            }
            this.finishChunk();
        }
        finally {
            this.def.end();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
    
    static {
        IDATOutputStream.chunkType = new byte[] { 73, 68, 65, 84 };
    }
}
