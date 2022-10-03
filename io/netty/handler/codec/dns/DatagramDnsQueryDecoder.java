package io.netty.handler.codec.dns;

import io.netty.channel.DefaultAddressedEnvelope;
import java.net.InetSocketAddress;
import io.netty.buffer.ByteBuf;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

@ChannelHandler.Sharable
public class DatagramDnsQueryDecoder extends MessageToMessageDecoder<DatagramPacket>
{
    private final DnsRecordDecoder recordDecoder;
    
    public DatagramDnsQueryDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }
    
    public DatagramDnsQueryDecoder(final DnsRecordDecoder recordDecoder) {
        this.recordDecoder = ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final DatagramPacket packet, final List<Object> out) throws Exception {
        final DnsQuery query = DnsMessageUtil.decodeDnsQuery(this.recordDecoder, ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content(), new DnsMessageUtil.DnsQueryFactory() {
            @Override
            public DnsQuery newQuery(final int id, final DnsOpCode dnsOpCode) {
                return new DatagramDnsQuery(((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).sender(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).recipient(), id, dnsOpCode);
            }
        });
        out.add(query);
    }
}
