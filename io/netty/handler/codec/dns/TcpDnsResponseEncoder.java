package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToMessageEncoder;

@ChannelHandler.Sharable
public final class TcpDnsResponseEncoder extends MessageToMessageEncoder<DnsResponse>
{
    private final DnsRecordEncoder encoder;
    
    public TcpDnsResponseEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }
    
    public TcpDnsResponseEncoder(final DnsRecordEncoder encoder) {
        this.encoder = ObjectUtil.checkNotNull(encoder, "encoder");
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final DnsResponse response, final List<Object> out) throws Exception {
        final ByteBuf buf = ctx.alloc().ioBuffer(1024);
        buf.writerIndex(buf.writerIndex() + 2);
        DnsMessageUtil.encodeDnsResponse(this.encoder, response, buf);
        buf.setShort(0, buf.readableBytes() - 2);
        out.add(buf);
    }
}
