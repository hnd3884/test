package org.msgpack.util.json;

import org.msgpack.io.Output;
import org.msgpack.io.LinkedBufferOutput;
import org.msgpack.MessagePack;
import org.msgpack.packer.BufferPacker;

public class JSONBufferPacker extends JSONPacker implements BufferPacker
{
    private static final int DEFAULT_BUFFER_SIZE = 512;
    
    public JSONBufferPacker() {
        this(512);
    }
    
    public JSONBufferPacker(final int bufferSize) {
        this(new MessagePack(), bufferSize);
    }
    
    public JSONBufferPacker(final MessagePack msgpack) {
        this(msgpack, 512);
    }
    
    public JSONBufferPacker(final MessagePack msgpack, final int bufferSize) {
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
