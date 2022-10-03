package org.msgpack.util.json;

import java.io.Reader;
import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.BufferUnpacker;

public class JSONBufferUnpacker extends JSONUnpacker implements BufferUnpacker
{
    private static final int DEFAULT_BUFFER_SIZE = 512;
    
    public JSONBufferUnpacker() {
        this(512);
    }
    
    public JSONBufferUnpacker(final int bufferSize) {
        this(new MessagePack(), bufferSize);
    }
    
    public JSONBufferUnpacker(final MessagePack msgpack) {
        this(msgpack, 512);
    }
    
    public JSONBufferUnpacker(final MessagePack msgpack, final int bufferSize) {
        super(msgpack, newEmptyReader());
    }
    
    @Override
    public JSONBufferUnpacker wrap(final byte[] b) {
        return this.wrap(b, 0, b.length);
    }
    
    @Override
    public JSONBufferUnpacker wrap(final byte[] b, final int off, final int len) {
        final ByteArrayInputStream in = new ByteArrayInputStream(b, off, len);
        this.in = new InputStreamReader(in);
        return this;
    }
    
    @Override
    public JSONBufferUnpacker wrap(final ByteBuffer buf) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support wrap(ByteBuffer buf)");
    }
    
    @Override
    public JSONBufferUnpacker feed(final byte[] b) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public JSONBufferUnpacker feed(final byte[] b, final boolean reference) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public JSONBufferUnpacker feed(final byte[] b, final int off, final int len) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public JSONBufferUnpacker feed(final byte[] b, final int off, final int len, final boolean reference) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public JSONBufferUnpacker feed(final ByteBuffer buf) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public JSONBufferUnpacker feed(final ByteBuffer buf, final boolean reference) {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support feed()");
    }
    
    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support getBufferSize()");
    }
    
    @Override
    public void copyReferencedBuffer() {
        throw new UnsupportedOperationException("JSONBufferUnpacker doesn't support copyReferencedBuffer()");
    }
    
    @Override
    public void clear() {
        this.reset();
        this.in = newEmptyReader();
    }
    
    private static Reader newEmptyReader() {
        return new InputStreamReader(new ByteArrayInputStream(new byte[0]));
    }
}
