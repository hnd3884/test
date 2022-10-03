package io.netty.handler.codec.compression;

import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import java.nio.ByteBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.internal.ObjectUtil;
import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import io.netty.handler.codec.ByteToMessageDecoder;

public final class BrotliDecoder extends ByteToMessageDecoder
{
    private final int inputBufferSize;
    private DecoderJNI.Wrapper decoder;
    private boolean destroyed;
    
    public BrotliDecoder() {
        this(8192);
    }
    
    public BrotliDecoder(final int inputBufferSize) {
        this.inputBufferSize = ObjectUtil.checkPositive(inputBufferSize, "inputBufferSize");
    }
    
    private ByteBuf pull(final ByteBufAllocator alloc) {
        final ByteBuffer nativeBuffer = this.decoder.pull();
        final ByteBuf copy = alloc.buffer(nativeBuffer.remaining());
        copy.writeBytes(nativeBuffer);
        return copy;
    }
    
    private State decompress(final ByteBuf input, final List<Object> output, final ByteBufAllocator alloc) {
        while (true) {
            switch (this.decoder.getStatus()) {
                case DONE: {
                    return State.DONE;
                }
                case OK: {
                    this.decoder.push(0);
                    continue;
                }
                case NEEDS_MORE_INPUT: {
                    if (this.decoder.hasOutput()) {
                        output.add(this.pull(alloc));
                    }
                    if (!input.isReadable()) {
                        return State.NEEDS_MORE_INPUT;
                    }
                    final ByteBuffer decoderInputBuffer = this.decoder.getInputBuffer();
                    decoderInputBuffer.clear();
                    final int readBytes = readBytes(input, decoderInputBuffer);
                    this.decoder.push(readBytes);
                    continue;
                }
                case NEEDS_MORE_OUTPUT: {
                    output.add(this.pull(alloc));
                    continue;
                }
                default: {
                    return State.ERROR;
                }
            }
        }
    }
    
    private static int readBytes(final ByteBuf in, final ByteBuffer dest) {
        final int limit = Math.min(in.readableBytes(), dest.remaining());
        final ByteBuffer slice = dest.slice();
        slice.limit(limit);
        in.readBytes(slice);
        dest.position(dest.position() + limit);
        return limit;
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        this.decoder = new DecoderJNI.Wrapper(this.inputBufferSize);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        if (this.destroyed) {
            in.skipBytes(in.readableBytes());
            return;
        }
        if (!in.isReadable()) {
            return;
        }
        try {
            final State state = this.decompress(in, out, ctx.alloc());
            if (state == State.DONE) {
                this.destroy();
            }
            else if (state == State.ERROR) {
                throw new DecompressionException("Brotli stream corrupted");
            }
        }
        catch (final Exception e) {
            this.destroy();
            throw e;
        }
    }
    
    private void destroy() {
        if (!this.destroyed) {
            this.destroyed = true;
            this.decoder.destroy();
        }
    }
    
    @Override
    protected void handlerRemoved0(final ChannelHandlerContext ctx) throws Exception {
        try {
            this.destroy();
        }
        finally {
            super.handlerRemoved0(ctx);
        }
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        try {
            this.destroy();
        }
        finally {
            super.channelInactive(ctx);
        }
    }
    
    static {
        try {
            Brotli.ensureAvailability();
        }
        catch (final Throwable throwable) {
            throw new ExceptionInInitializerError(throwable);
        }
    }
    
    private enum State
    {
        DONE, 
        NEEDS_MORE_INPUT, 
        ERROR;
    }
}
