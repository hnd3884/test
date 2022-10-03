package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;
import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.MessageToMessageEncoder;

@ChannelHandler.Sharable
public class DatagramDnsQueryEncoder extends MessageToMessageEncoder<AddressedEnvelope<DnsQuery, InetSocketAddress>>
{
    private final DnsQueryEncoder encoder;
    
    public DatagramDnsQueryEncoder() {
        this(DnsRecordEncoder.DEFAULT);
    }
    
    public DatagramDnsQueryEncoder(final DnsRecordEncoder recordEncoder) {
        this.encoder = new DnsQueryEncoder(recordEncoder);
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final AddressedEnvelope<DnsQuery, InetSocketAddress> in, final List<Object> out) throws Exception {
        final InetSocketAddress recipient = in.recipient();
        final DnsQuery query = in.content();
        final ByteBuf buf = this.allocateBuffer(ctx, in);
        boolean success = false;
        try {
            this.encoder.encode(query, buf);
            success = true;
        }
        finally {
            if (!success) {
                buf.release();
            }
        }
        out.add(new DatagramPacket(buf, recipient, null));
    }
    
    protected ByteBuf allocateBuffer(final ChannelHandlerContext ctx, final AddressedEnvelope<DnsQuery, InetSocketAddress> msg) throws Exception {
        return ctx.alloc().ioBuffer(1024);
    }
}
