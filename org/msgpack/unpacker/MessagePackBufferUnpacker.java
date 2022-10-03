package org.msgpack.unpacker;

import java.nio.ByteBuffer;
import org.msgpack.io.Input;
import org.msgpack.io.LinkedBufferInput;
import org.msgpack.MessagePack;

public class MessagePackBufferUnpacker extends MessagePackUnpacker implements BufferUnpacker
{
    private static final int DEFAULT_BUFFER_SIZE = 512;
    
    public MessagePackBufferUnpacker(final MessagePack msgpack) {
        this(msgpack, 512);
    }
    
    public MessagePackBufferUnpacker(final MessagePack msgpack, final int bufferSize) {
        super(msgpack, new LinkedBufferInput(bufferSize));
    }
    
    @Override
    public MessagePackBufferUnpacker wrap(final byte[] b) {
        return this.wrap(b, 0, b.length);
    }
    
    @Override
    public MessagePackBufferUnpacker wrap(final byte[] b, final int off, final int len) {
        ((LinkedBufferInput)this.in).clear();
        ((LinkedBufferInput)this.in).feed(b, off, len, true);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker wrap(final ByteBuffer buf) {
        ((LinkedBufferInput)this.in).clear();
        ((LinkedBufferInput)this.in).feed(buf, true);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final byte[] b) {
        ((LinkedBufferInput)this.in).feed(b);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final byte[] b, final boolean reference) {
        ((LinkedBufferInput)this.in).feed(b, reference);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final byte[] b, final int off, final int len) {
        ((LinkedBufferInput)this.in).feed(b, off, len);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final byte[] b, final int off, final int len, final boolean reference) {
        ((LinkedBufferInput)this.in).feed(b, off, len, reference);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final ByteBuffer b) {
        ((LinkedBufferInput)this.in).feed(b);
        return this;
    }
    
    @Override
    public MessagePackBufferUnpacker feed(final ByteBuffer buf, final boolean reference) {
        ((LinkedBufferInput)this.in).feed(buf, reference);
        return this;
    }
    
    @Override
    public int getBufferSize() {
        return ((LinkedBufferInput)this.in).getSize();
    }
    
    @Override
    public void copyReferencedBuffer() {
        ((LinkedBufferInput)this.in).copyReferencedBuffer();
    }
    
    @Override
    public void clear() {
        ((LinkedBufferInput)this.in).clear();
        this.reset();
    }
}
