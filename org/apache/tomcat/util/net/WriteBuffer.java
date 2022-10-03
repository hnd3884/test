package org.apache.tomcat.util.net;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.buf.ByteBufferHolder;
import java.util.concurrent.LinkedBlockingDeque;

public class WriteBuffer
{
    private final int bufferSize;
    private final LinkedBlockingDeque<ByteBufferHolder> buffers;
    
    public WriteBuffer(final int bufferSize) {
        this.buffers = new LinkedBlockingDeque<ByteBufferHolder>();
        this.bufferSize = bufferSize;
    }
    
    void add(final byte[] buf, final int offset, final int length) {
        final ByteBufferHolder holder = this.getByteBufferHolder(length);
        holder.getBuf().put(buf, offset, length);
    }
    
    public void add(final ByteBuffer from) {
        final ByteBufferHolder holder = this.getByteBufferHolder(from.remaining());
        holder.getBuf().put(from);
    }
    
    private ByteBufferHolder getByteBufferHolder(final int capacity) {
        ByteBufferHolder holder = this.buffers.peekLast();
        if (holder == null || holder.isFlipped() || holder.getBuf().remaining() < capacity) {
            final ByteBuffer buffer = ByteBuffer.allocate(Math.max(this.bufferSize, capacity));
            holder = new ByteBufferHolder(buffer, false);
            this.buffers.add(holder);
        }
        return holder;
    }
    
    public boolean isEmpty() {
        return this.buffers.isEmpty();
    }
    
    ByteBuffer[] toArray(final ByteBuffer... prefixes) {
        final List<ByteBuffer> result = new ArrayList<ByteBuffer>();
        for (final ByteBuffer prefix : prefixes) {
            if (prefix.hasRemaining()) {
                result.add(prefix);
            }
        }
        for (final ByteBufferHolder buffer : this.buffers) {
            buffer.flip();
            result.add(buffer.getBuf());
        }
        this.buffers.clear();
        return result.toArray(new ByteBuffer[0]);
    }
    
    boolean write(final SocketWrapperBase<?> socketWrapper, final boolean blocking) throws IOException {
        final Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            final ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            if (blocking) {
                socketWrapper.writeBlocking(buffer.getBuf());
            }
            else {
                socketWrapper.writeNonBlockingInternal(buffer.getBuf());
            }
            if (buffer.getBuf().remaining() == 0) {
                bufIter.remove();
            }
            else {
                dataLeft = true;
            }
        }
        return dataLeft;
    }
    
    public boolean write(final Sink sink, final boolean blocking) throws IOException {
        final Iterator<ByteBufferHolder> bufIter = this.buffers.iterator();
        boolean dataLeft = false;
        while (!dataLeft && bufIter.hasNext()) {
            final ByteBufferHolder buffer = bufIter.next();
            buffer.flip();
            dataLeft = sink.writeFromBuffer(buffer.getBuf(), blocking);
            if (!dataLeft) {
                bufIter.remove();
            }
        }
        return dataLeft;
    }
    
    public interface Sink
    {
        boolean writeFromBuffer(final ByteBuffer p0, final boolean p1) throws IOException;
    }
}
