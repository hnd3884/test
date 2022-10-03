package org.msgpack.io;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

public final class LinkedBufferOutput extends BufferedOutput
{
    private LinkedList<Link> link;
    private int size;
    
    public LinkedBufferOutput(final int bufferSize) {
        super(bufferSize);
        this.link = new LinkedList<Link>();
    }
    
    public byte[] toByteArray() {
        final byte[] bytes = new byte[this.size + this.filled];
        int off = 0;
        for (final Link l : this.link) {
            System.arraycopy(l.buffer, l.offset, bytes, off, l.size);
            off += l.size;
        }
        if (this.filled > 0) {
            System.arraycopy(this.buffer, 0, bytes, off, this.filled);
        }
        return bytes;
    }
    
    public int getSize() {
        return this.size + this.filled;
    }
    
    @Override
    protected boolean flushBuffer(final byte[] b, final int off, final int len) {
        this.link.add(new Link(b, off, len));
        this.size += len;
        return false;
    }
    
    public void clear() {
        this.link.clear();
        this.size = 0;
        this.filled = 0;
    }
    
    @Override
    public void close() {
    }
    
    private static final class Link
    {
        final byte[] buffer;
        final int offset;
        final int size;
        
        Link(final byte[] buffer, final int offset, final int size) {
            this.buffer = buffer;
            this.offset = offset;
            this.size = size;
        }
    }
}
