package org.bouncycastle.asn1;

import org.bouncycastle.util.io.Streams;
import java.io.IOException;
import java.io.EOFException;
import java.io.InputStream;

class DefiniteLengthInputStream extends LimitedInputStream
{
    private static final byte[] EMPTY_BYTES;
    private final int _originalLength;
    private int _remaining;
    
    DefiniteLengthInputStream(final InputStream inputStream, final int n) {
        super(inputStream, n);
        if (n < 0) {
            throw new IllegalArgumentException("negative lengths not allowed");
        }
        this._originalLength = n;
        if ((this._remaining = n) == 0) {
            this.setParentEofDetect(true);
        }
    }
    
    @Override
    int getRemaining() {
        return this._remaining;
    }
    
    @Override
    public int read() throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        final int read = this._in.read();
        if (read < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        if (--this._remaining == 0) {
            this.setParentEofDetect(true);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        if (this._remaining == 0) {
            return -1;
        }
        final int read = this._in.read(array, n, Math.min(n2, this._remaining));
        if (read < 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        if ((this._remaining -= read) == 0) {
            this.setParentEofDetect(true);
        }
        return read;
    }
    
    byte[] toByteArray() throws IOException {
        if (this._remaining == 0) {
            return DefiniteLengthInputStream.EMPTY_BYTES;
        }
        final byte[] array = new byte[this._remaining];
        if ((this._remaining -= Streams.readFully(this._in, array)) != 0) {
            throw new EOFException("DEF length " + this._originalLength + " object truncated by " + this._remaining);
        }
        this.setParentEofDetect(true);
        return array;
    }
    
    static {
        EMPTY_BYTES = new byte[0];
    }
}
