package sun.awt.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class PNGFilterInputStream extends FilterInputStream
{
    PNGImageDecoder owner;
    public InputStream underlyingInputStream;
    
    public PNGFilterInputStream(final PNGImageDecoder owner, final InputStream inputStream) {
        super(inputStream);
        this.underlyingInputStream = this.in;
        this.owner = owner;
    }
    
    @Override
    public int available() throws IOException {
        return this.owner.limit - this.owner.pos + this.in.available();
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int read() throws IOException {
        if (this.owner.chunkLength <= 0 && !this.owner.getData()) {
            return -1;
        }
        final PNGImageDecoder owner = this.owner;
        --owner.chunkLength;
        return this.owner.inbuf[this.owner.chunkStart++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    @Override
    public int read(final byte[] array, final int n, int chunkLength) throws IOException {
        if (this.owner.chunkLength <= 0 && !this.owner.getData()) {
            return -1;
        }
        if (this.owner.chunkLength < chunkLength) {
            chunkLength = this.owner.chunkLength;
        }
        System.arraycopy(this.owner.inbuf, this.owner.chunkStart, array, n, chunkLength);
        final PNGImageDecoder owner = this.owner;
        owner.chunkLength -= chunkLength;
        final PNGImageDecoder owner2 = this.owner;
        owner2.chunkStart += chunkLength;
        return chunkLength;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        int n2;
        for (n2 = 0; n2 < n && this.read() >= 0; ++n2) {}
        return n2;
    }
}
