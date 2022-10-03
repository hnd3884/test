package io.netty.handler.codec.base64;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageEncoder;

@ChannelHandler.Sharable
public class Base64Encoder extends MessageToMessageEncoder<ByteBuf>
{
    private final boolean breakLines;
    private final Base64Dialect dialect;
    
    public Base64Encoder() {
        this(true);
    }
    
    public Base64Encoder(final boolean breakLines) {
        this(breakLines, Base64Dialect.STANDARD);
    }
    
    public Base64Encoder(final boolean breakLines, final Base64Dialect dialect) {
        this.dialect = ObjectUtil.checkNotNull(dialect, "dialect");
        this.breakLines = breakLines;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(Base64.encode(msg, msg.readerIndex(), msg.readableBytes(), this.breakLines, this.dialect));
    }
}
