package io.netty.handler.codec;

import io.netty.util.ByteProcessor;
import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class LineBasedFrameDecoder extends ByteToMessageDecoder
{
    private final int maxLength;
    private final boolean failFast;
    private final boolean stripDelimiter;
    private boolean discarding;
    private int discardedBytes;
    private int offset;
    
    public LineBasedFrameDecoder(final int maxLength) {
        this(maxLength, true, false);
    }
    
    public LineBasedFrameDecoder(final int maxLength, final boolean stripDelimiter, final boolean failFast) {
        this.maxLength = maxLength;
        this.failFast = failFast;
        this.stripDelimiter = stripDelimiter;
    }
    
    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        final Object decoded = this.decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }
    
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf buffer) throws Exception {
        final int eol = this.findEndOfLine(buffer);
        if (this.discarding) {
            if (eol >= 0) {
                final int length = this.discardedBytes + eol - buffer.readerIndex();
                final int delimLength = (buffer.getByte(eol) == 13) ? 2 : 1;
                buffer.readerIndex(eol + delimLength);
                this.discardedBytes = 0;
                this.discarding = false;
                if (!this.failFast) {
                    this.fail(ctx, length);
                }
            }
            else {
                this.discardedBytes += buffer.readableBytes();
                buffer.readerIndex(buffer.writerIndex());
                this.offset = 0;
            }
            return null;
        }
        if (eol < 0) {
            final int length = buffer.readableBytes();
            if (length > this.maxLength) {
                this.discardedBytes = length;
                buffer.readerIndex(buffer.writerIndex());
                this.discarding = true;
                this.offset = 0;
                if (this.failFast) {
                    this.fail(ctx, "over " + this.discardedBytes);
                }
            }
            return null;
        }
        final int length2 = eol - buffer.readerIndex();
        final int delimLength2 = (buffer.getByte(eol) == 13) ? 2 : 1;
        if (length2 > this.maxLength) {
            buffer.readerIndex(eol + delimLength2);
            this.fail(ctx, length2);
            return null;
        }
        ByteBuf frame;
        if (this.stripDelimiter) {
            frame = buffer.readRetainedSlice(length2);
            buffer.skipBytes(delimLength2);
        }
        else {
            frame = buffer.readRetainedSlice(length2 + delimLength2);
        }
        return frame;
    }
    
    private void fail(final ChannelHandlerContext ctx, final int length) {
        this.fail(ctx, String.valueOf(length));
    }
    
    private void fail(final ChannelHandlerContext ctx, final String length) {
        ctx.fireExceptionCaught((Throwable)new TooLongFrameException("frame length (" + length + ") exceeds the allowed maximum (" + this.maxLength + ')'));
    }
    
    private int findEndOfLine(final ByteBuf buffer) {
        final int totalLength = buffer.readableBytes();
        int i = buffer.forEachByte(buffer.readerIndex() + this.offset, totalLength - this.offset, ByteProcessor.FIND_LF);
        if (i >= 0) {
            this.offset = 0;
            if (i > 0 && buffer.getByte(i - 1) == 13) {
                --i;
            }
        }
        else {
            this.offset = totalLength;
        }
        return i;
    }
}
