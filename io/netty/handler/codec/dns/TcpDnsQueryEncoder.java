package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public final class TcpDnsQueryEncoder extends MessageToByteEncoder<DnsQuery>
{
    private final DnsQueryEncoder encoder;
    
    public TcpDnsQueryEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }
    
    public TcpDnsQueryEncoder(final DnsRecordEncoder recordEncoder) {
        this.encoder = new DnsQueryEncoder(recordEncoder);
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final DnsQuery msg, final ByteBuf out) throws Exception {
        out.writerIndex(out.writerIndex() + 2);
        this.encoder.encode(msg, out);
        out.setShort(0, out.readableBytes() - 2);
    }
    
    @Override
    protected ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final DnsQuery msg, final boolean preferDirect) {
        if (preferDirect) {
            return ctx.alloc().ioBuffer(1024);
        }
        return ctx.alloc().heapBuffer(1024);
    }
}
