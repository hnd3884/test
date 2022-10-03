package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.ByteToMessageDecoder;

public abstract class ZlibDecoder extends ByteToMessageDecoder
{
    protected final int maxAllocation;
    
    public ZlibDecoder() {
        this(0);
    }
    
    public ZlibDecoder(final int maxAllocation) {
        this.maxAllocation = ObjectUtil.checkPositiveOrZero(maxAllocation, "maxAllocation");
    }
    
    public abstract boolean isClosed();
    
    protected ByteBuf prepareDecompressBuffer(final ChannelHandlerContext ctx, final ByteBuf buffer, final int preferredSize) {
        if (buffer == null) {
            if (this.maxAllocation == 0) {
                return ctx.alloc().heapBuffer(preferredSize);
            }
            return ctx.alloc().heapBuffer(Math.min(preferredSize, this.maxAllocation), this.maxAllocation);
        }
        else {
            if (buffer.ensureWritable(preferredSize, true) == 1) {
                this.decompressionBufferExhausted(buffer.duplicate());
                buffer.skipBytes(buffer.readableBytes());
                throw new DecompressionException("Decompression buffer has reached maximum size: " + buffer.maxCapacity());
            }
            return buffer;
        }
    }
    
    protected void decompressionBufferExhausted(final ByteBuf buffer) {
    }
}
