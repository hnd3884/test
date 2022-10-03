package io.netty.handler.codec.stomp;

import java.util.Iterator;
import java.util.Map;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class StompSubframeEncoder extends MessageToMessageEncoder<StompSubframe>
{
    @Override
    protected void encode(final ChannelHandlerContext ctx, final StompSubframe msg, final List<Object> out) throws Exception {
        if (msg instanceof StompFrame) {
            final StompFrame stompFrame = (StompFrame)msg;
            final ByteBuf buf = this.encodeFullFrame(stompFrame, ctx);
            out.add(this.convertFullFrame(stompFrame, buf));
        }
        else if (msg instanceof StompHeadersSubframe) {
            final StompHeadersSubframe stompHeadersSubframe = (StompHeadersSubframe)msg;
            final ByteBuf buf = ctx.alloc().buffer(this.headersSubFrameSize(stompHeadersSubframe));
            encodeHeaders(stompHeadersSubframe, buf);
            out.add(this.convertHeadersSubFrame(stompHeadersSubframe, buf));
        }
        else if (msg instanceof StompContentSubframe) {
            final StompContentSubframe stompContentSubframe = (StompContentSubframe)msg;
            final ByteBuf buf = encodeContent(stompContentSubframe, ctx);
            out.add(this.convertContentSubFrame(stompContentSubframe, buf));
        }
    }
    
    protected Object convertFullFrame(final StompFrame original, final ByteBuf encoded) {
        return encoded;
    }
    
    protected Object convertHeadersSubFrame(final StompHeadersSubframe original, final ByteBuf encoded) {
        return encoded;
    }
    
    protected Object convertContentSubFrame(final StompContentSubframe original, final ByteBuf encoded) {
        return encoded;
    }
    
    protected int headersSubFrameSize(final StompHeadersSubframe headersSubframe) {
        final int estimatedSize = headersSubframe.headers().size() * 34 + 48;
        if (estimatedSize < 128) {
            return 128;
        }
        if (estimatedSize < 256) {
            return 256;
        }
        return estimatedSize;
    }
    
    private ByteBuf encodeFullFrame(final StompFrame frame, final ChannelHandlerContext ctx) {
        final int contentReadableBytes = frame.content().readableBytes();
        final ByteBuf buf = ctx.alloc().buffer(this.headersSubFrameSize(frame) + contentReadableBytes);
        encodeHeaders(frame, buf);
        if (contentReadableBytes > 0) {
            buf.writeBytes(frame.content());
        }
        return buf.writeByte(0);
    }
    
    private static void encodeHeaders(final StompHeadersSubframe frame, final ByteBuf buf) {
        ByteBufUtil.writeUtf8(buf, frame.command().toString());
        buf.writeByte(10);
        for (final Map.Entry<CharSequence, CharSequence> entry : frame.headers()) {
            ByteBufUtil.writeUtf8(buf, entry.getKey());
            buf.writeByte(58);
            ByteBufUtil.writeUtf8(buf, entry.getValue());
            buf.writeByte(10);
        }
        buf.writeByte(10);
    }
    
    private static ByteBuf encodeContent(final StompContentSubframe content, final ChannelHandlerContext ctx) {
        if (content instanceof LastStompContentSubframe) {
            final ByteBuf buf = ctx.alloc().buffer(content.content().readableBytes() + 1);
            buf.writeBytes(content.content());
            buf.writeByte(0);
            return buf;
        }
        return content.content().retain();
    }
}
