package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class SubImageInputStream extends ImageInputStreamImpl
{
    ImageInputStream stream;
    long startingPos;
    int startingLength;
    int length;
    
    public SubImageInputStream(final ImageInputStream stream, final int n) throws IOException {
        this.stream = stream;
        this.startingPos = stream.getStreamPosition();
        this.length = n;
        this.startingLength = n;
    }
    
    @Override
    public int read() throws IOException {
        if (this.length == 0) {
            return -1;
        }
        --this.length;
        return this.stream.read();
    }
    
    @Override
    public int read(final byte[] array, final int n, int min) throws IOException {
        if (this.length == 0) {
            return -1;
        }
        min = Math.min(min, this.length);
        final int read = this.stream.read(array, n, min);
        this.length -= read;
        return read;
    }
    
    @Override
    public long length() {
        return this.startingLength;
    }
    
    @Override
    public void seek(final long streamPos) throws IOException {
        this.stream.seek(streamPos - this.startingPos);
        this.streamPos = streamPos;
    }
    
    @Override
    protected void finalize() throws Throwable {
    }
}
