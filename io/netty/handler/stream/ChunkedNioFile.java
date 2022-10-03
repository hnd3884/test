package io.netty.handler.stream;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.nio.channels.ClosedChannelException;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.channels.FileChannel;
import io.netty.buffer.ByteBuf;

public class ChunkedNioFile implements ChunkedInput<ByteBuf>
{
    private final FileChannel in;
    private final long startOffset;
    private final long endOffset;
    private final int chunkSize;
    private long offset;
    
    public ChunkedNioFile(final File in) throws IOException {
        this(new RandomAccessFile(in, "r").getChannel());
    }
    
    public ChunkedNioFile(final File in, final int chunkSize) throws IOException {
        this(new RandomAccessFile(in, "r").getChannel(), chunkSize);
    }
    
    public ChunkedNioFile(final FileChannel in) throws IOException {
        this(in, 8192);
    }
    
    public ChunkedNioFile(final FileChannel in, final int chunkSize) throws IOException {
        this(in, 0L, in.size(), chunkSize);
    }
    
    public ChunkedNioFile(final FileChannel in, final long offset, final long length, final int chunkSize) throws IOException {
        ObjectUtil.checkNotNull(in, "in");
        ObjectUtil.checkPositiveOrZero(offset, "offset");
        ObjectUtil.checkPositiveOrZero(length, "length");
        ObjectUtil.checkPositive(chunkSize, "chunkSize");
        if (!in.isOpen()) {
            throw new ClosedChannelException();
        }
        this.in = in;
        this.chunkSize = chunkSize;
        this.startOffset = offset;
        this.offset = offset;
        this.endOffset = offset + length;
    }
    
    public long startOffset() {
        return this.startOffset;
    }
    
    public long endOffset() {
        return this.endOffset;
    }
    
    public long currentOffset() {
        return this.offset;
    }
    
    @Override
    public boolean isEndOfInput() throws Exception {
        return this.offset >= this.endOffset || !this.in.isOpen();
    }
    
    @Override
    public void close() throws Exception {
        this.in.close();
    }
    
    @Deprecated
    @Override
    public ByteBuf readChunk(final ChannelHandlerContext ctx) throws Exception {
        return this.readChunk(ctx.alloc());
    }
    
    @Override
    public ByteBuf readChunk(final ByteBufAllocator allocator) throws Exception {
        final long offset = this.offset;
        if (offset >= this.endOffset) {
            return null;
        }
        final int chunkSize = (int)Math.min(this.chunkSize, this.endOffset - offset);
        final ByteBuf buffer = allocator.buffer(chunkSize);
        boolean release = true;
        try {
            int readBytes = 0;
            do {
                final int localReadBytes = buffer.writeBytes(this.in, offset + readBytes, chunkSize - readBytes);
                if (localReadBytes < 0) {
                    break;
                }
                readBytes += localReadBytes;
            } while (readBytes != chunkSize);
            this.offset += readBytes;
            release = false;
            return buffer;
        }
        finally {
            if (release) {
                buffer.release();
            }
        }
    }
    
    @Override
    public long length() {
        return this.endOffset - this.startOffset;
    }
    
    @Override
    public long progress() {
        return this.offset - this.startOffset;
    }
}
