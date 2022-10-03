package io.netty.handler.codec;

import java.util.List;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class FixedLengthFrameDecoder extends ByteToMessageDecoder
{
    private final int frameLength;
    
    public FixedLengthFrameDecoder(final int frameLength) {
        ObjectUtil.checkPositive(frameLength, "frameLength");
        this.frameLength = frameLength;
    }
    
    @Override
    protected final void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
        final Object decoded = this.decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
    }
    
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        if (in.readableBytes() < this.frameLength) {
            return null;
        }
        return in.readRetainedSlice(this.frameLength);
    }
}
