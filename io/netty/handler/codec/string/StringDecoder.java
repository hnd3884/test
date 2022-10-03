package io.netty.handler.codec.string;

import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import java.nio.charset.Charset;
import io.netty.channel.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.MessageToMessageDecoder;

@ChannelHandler.Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf>
{
    private final Charset charset;
    
    public StringDecoder() {
        this(Charset.defaultCharset());
    }
    
    public StringDecoder(final Charset charset) {
        this.charset = ObjectUtil.checkNotNull(charset, "charset");
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(msg.toString(this.charset));
    }
}
