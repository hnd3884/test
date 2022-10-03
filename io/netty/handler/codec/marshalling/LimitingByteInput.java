package io.netty.handler.codec.marshalling;

import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import org.jboss.marshalling.ByteInput;

class LimitingByteInput implements ByteInput
{
    private static final TooBigObjectException EXCEPTION;
    private final ByteInput input;
    private final long limit;
    private long read;
    
    LimitingByteInput(final ByteInput input, final long limit) {
        this.input = input;
        this.limit = ObjectUtil.checkPositive(limit, "limit");
    }
    
    public void close() throws IOException {
    }
    
    public int available() throws IOException {
        return this.readable(this.input.available());
    }
    
    public int read() throws IOException {
        final int readable = this.readable(1);
        if (readable > 0) {
            final int b = this.input.read();
            ++this.read;
            return b;
        }
        throw LimitingByteInput.EXCEPTION;
    }
    
    public int read(final byte[] array) throws IOException {
        return this.read(array, 0, array.length);
    }
    
    public int read(final byte[] array, final int offset, final int length) throws IOException {
        final int readable = this.readable(length);
        if (readable > 0) {
            final int i = this.input.read(array, offset, readable);
            this.read += i;
            return i;
        }
        throw LimitingByteInput.EXCEPTION;
    }
    
    public long skip(final long bytes) throws IOException {
        final int readable = this.readable((int)bytes);
        if (readable > 0) {
            final long i = this.input.skip((long)readable);
            this.read += i;
            return i;
        }
        throw LimitingByteInput.EXCEPTION;
    }
    
    private int readable(final int length) {
        return (int)Math.min(length, this.limit - this.read);
    }
    
    static {
        EXCEPTION = new TooBigObjectException();
    }
    
    static final class TooBigObjectException extends IOException
    {
        private static final long serialVersionUID = 1L;
    }
}
