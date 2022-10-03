package org.msgpack.packer;

import org.msgpack.io.Output;
import org.msgpack.io.LinkedBufferOutput;
import org.msgpack.MessagePack;

public class MessagePackBufferPacker extends MessagePackPacker implements BufferPacker
{
    private static final int DEFAULT_BUFFER_SIZE = 512;
    
    public MessagePackBufferPacker(final MessagePack msgpack) {
        this(msgpack, 512);
    }
    
    public MessagePackBufferPacker(final MessagePack msgpack, final int bufferSize) {
        super(msgpack, new LinkedBufferOutput(bufferSize));
    }
    
    @Override
    public int getBufferSize() {
        return ((LinkedBufferOutput)this.out).getSize();
    }
    
    @Override
    public byte[] toByteArray() {
        return ((LinkedBufferOutput)this.out).toByteArray();
    }
    
    @Override
    public void clear() {
        this.reset();
        ((LinkedBufferOutput)this.out).clear();
    }
}
