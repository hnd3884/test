package io.netty.handler.codec.bytes;

import io.netty.buffer.ByteBufUtil;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf>
{
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(ByteBufUtil.getBytes(msg));
    }
}
