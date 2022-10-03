package io.netty.handler.codec.compression;

import io.netty.buffer.Unpooled;
import java.nio.ByteBuffer;
import com.github.luben.zstd.Zstd;
import io.netty.handler.codec.EncoderException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToByteEncoder;

public final class ZstdEncoder extends MessageToByteEncoder<ByteBuf>
{
    private final int blockSize;
    private final int compressionLevel;
    private final int maxEncodeSize;
    private ByteBuf buffer;
    
    public ZstdEncoder() {
        this(3, 65536, 33554432);
    }
    
    public ZstdEncoder(final int compressionLevel) {
        this(compressionLevel, 65536, 33554432);
    }
    
    public ZstdEncoder(final int blockSize, final int maxEncodeSize) {
        this(3, blockSize, maxEncodeSize);
    }
    
    public ZstdEncoder(final int compressionLevel, final int blockSize, final int maxEncodeSize) {
        super(true);
        this.compressionLevel = ObjectUtil.checkInRange(compressionLevel, 0, 22, "compressionLevel");
        this.blockSize = ObjectUtil.checkPositive(blockSize, "blockSize");
        this.maxEncodeSize = ObjectUtil.checkPositive(maxEncodeSize, "maxEncodeSize");
    }
    
    @Override
    protected ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final ByteBuf msg, final boolean preferDirect) {
        if (this.buffer == null) {
            throw new IllegalStateException("not added to a pipeline,or has been removed,buffer is null");
        }
        int remaining = msg.readableBytes() + this.buffer.readableBytes();
        if (remaining < 0) {
            throw new EncoderException("too much data to allocate a buffer for compression");
        }
        long bufferSize;
        int curSize;
        for (bufferSize = 0L; remaining > 0; remaining -= curSize, bufferSize += Zstd.compressBound((long)curSize)) {
            curSize = Math.min(this.blockSize, remaining);
        }
        if (bufferSize > this.maxEncodeSize || 0L > bufferSize) {
            throw new EncoderException("requested encode buffer size (" + bufferSize + " bytes) exceeds the maximum allowable size (" + this.maxEncodeSize + " bytes)");
        }
        return ctx.alloc().directBuffer((int)bufferSize);
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf in, final ByteBuf out) {
        if (this.buffer == null) {
            throw new IllegalStateException("not added to a pipeline,or has been removed,buffer is null");
        }
        final ByteBuf buffer = this.buffer;
        int length;
        while ((length = in.readableBytes()) > 0) {
            final int nextChunkSize = Math.min(length, buffer.writableBytes());
            in.readBytes(buffer, nextChunkSize);
            if (!buffer.isWritable()) {
                this.flushBufferedData(out);
            }
        }
    }
    
    private void flushBufferedData(final ByteBuf out) {
        final int flushableBytes = this.buffer.readableBytes();
        if (flushableBytes == 0) {
            return;
        }
        final int bufSize = (int)Zstd.compressBound((long)flushableBytes);
        out.ensureWritable(bufSize);
        final int idx = out.writerIndex();
        int compressedLength;
        try {
            final ByteBuffer outNioBuffer = out.internalNioBuffer(idx, out.writableBytes());
            compressedLength = Zstd.compress(outNioBuffer, this.buffer.internalNioBuffer(this.buffer.readerIndex(), flushableBytes), this.compressionLevel);
        }
        catch (final Exception e) {
            throw new CompressionException(e);
        }
        out.writerIndex(idx + compressedLength);
        this.buffer.clear();
    }
    
    @Override
    public void flush(final ChannelHandlerContext ctx) {
        if (this.buffer != null && this.buffer.isReadable()) {
            final ByteBuf buf = this.allocateBuffer(ctx, Unpooled.EMPTY_BUFFER, this.isPreferDirect());
            this.flushBufferedData(buf);
            ctx.write(buf);
        }
        ctx.flush();
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        (this.buffer = ctx.alloc().directBuffer(this.blockSize)).clear();
    }
    
    @Override
    public void handlerRemoved(final ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        if (this.buffer != null) {
            this.buffer.release();
            this.buffer = null;
        }
    }
}
