package org.msgpack.unpacker;

import java.nio.ByteBuffer;

public interface BufferUnpacker extends Unpacker
{
    BufferUnpacker wrap(final byte[] p0);
    
    BufferUnpacker wrap(final byte[] p0, final int p1, final int p2);
    
    BufferUnpacker wrap(final ByteBuffer p0);
    
    BufferUnpacker feed(final byte[] p0);
    
    BufferUnpacker feed(final byte[] p0, final boolean p1);
    
    BufferUnpacker feed(final byte[] p0, final int p1, final int p2);
    
    BufferUnpacker feed(final byte[] p0, final int p1, final int p2, final boolean p3);
    
    BufferUnpacker feed(final ByteBuffer p0);
    
    BufferUnpacker feed(final ByteBuffer p0, final boolean p1);
    
    int getBufferSize();
    
    void copyReferencedBuffer();
    
    void clear();
}
