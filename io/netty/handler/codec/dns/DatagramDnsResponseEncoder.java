package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.MessageToMessageEncoder;

@ChannelHandler.Sharable
public class DatagramDnsResponseEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsResponse, InetSocketAddress>>
{
    private final DnsRecordEncoder recordEncoder;
    
    public DatagramDnsResponseEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }
    
    public DatagramDnsResponseEncoder(final DnsRecordEncoder recordEncoder) {
        this.recordEncoder = ObjectUtil.checkNotNull(recordEncoder, "recordEncoder");
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final AddressedEnvelope<DnsResponse, InetSocketAddress> in, final List<Object> out) throws Exception {
        final InetSocketAddress recipient = in.recipient();
        final DnsResponse response = in.content();
        final ByteBuf buf = this.allocateBuffer(ctx, in);
        DnsMessageUtil.encodeDnsResponse(this.recordEncoder, response, buf);
        out.add(new DatagramPacket(buf, recipient, null));
    }
    
    protected ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final AddressedEnvelope<DnsResponse, InetSocketAddress> msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }
}
