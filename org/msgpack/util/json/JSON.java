package org.msgpack.util.json;

import java.nio.ByteBuffer;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;
import java.io.InputStream;
import org.msgpack.packer.BufferPacker;
import org.msgpack.packer.Packer;
import java.io.OutputStream;
import org.msgpack.MessagePack;

public class JSON extends MessagePack
{
    public JSON() {
    }
    
    public JSON(final MessagePack msgpack) {
        super(msgpack);
    }
    
    @Override
    public Packer createPacker(final OutputStream stream) {
        return new JSONPacker(this, stream);
    }
    
    @Override
    public BufferPacker createBufferPacker() {
        return new JSONBufferPacker(this);
    }
    
    @Override
    public BufferPacker createBufferPacker(final int bufferSize) {
        return new JSONBufferPacker(this, bufferSize);
    }
    
    @Override
    public Unpacker createUnpacker(final InputStream stream) {
        return new JSONUnpacker(this, stream);
    }
    
    @Override
    public BufferUnpacker createBufferUnpacker() {
        return new JSONBufferUnpacker();
    }
    
    @Override
    public BufferUnpacker createBufferUnpacker(final byte[] b) {
        return this.createBufferUnpacker().wrap(b);
    }
    
    @Override
    public BufferUnpacker createBufferUnpacker(final byte[] b, final int off, final int len) {
        return this.createBufferUnpacker().wrap(b, off, len);
    }
    
    @Override
    public BufferUnpacker createBufferUnpacker(final ByteBuffer bb) {
        return this.createBufferUnpacker().wrap(bb);
    }
}
