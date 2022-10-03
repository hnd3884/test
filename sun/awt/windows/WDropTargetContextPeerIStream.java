package sun.awt.windows;

import java.io.IOException;
import java.io.InputStream;

final class WDropTargetContextPeerIStream extends InputStream
{
    private long istream;
    
    WDropTargetContextPeerIStream(final long istream) throws IOException {
        if (istream == 0L) {
            throw new IOException("No IStream");
        }
        this.istream = istream;
    }
    
    @Override
    public int available() throws IOException {
        if (this.istream == 0L) {
            throw new IOException("No IStream");
        }
        return this.Available(this.istream);
    }
    
    private native int Available(final long p0);
    
    @Override
    public int read() throws IOException {
        if (this.istream == 0L) {
            throw new IOException("No IStream");
        }
        return this.Read(this.istream);
    }
    
    private native int Read(final long p0) throws IOException;
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this.istream == 0L) {
            throw new IOException("No IStream");
        }
        return this.ReadBytes(this.istream, array, n, n2);
    }
    
    private native int ReadBytes(final long p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    @Override
    public void close() throws IOException {
        if (this.istream != 0L) {
            super.close();
            this.Close(this.istream);
            this.istream = 0L;
        }
    }
    
    private native void Close(final long p0);
}
