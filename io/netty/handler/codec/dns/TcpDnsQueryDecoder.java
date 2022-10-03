package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public final class TcpDnsQueryDecoder extends LengthFieldBasedFrameDecoder
{
    private final DnsRecordDecoder decoder;
    
    public TcpDnsQueryDecoder() {
        this(DnsRecordDecoder.DEFAULT, 65535);
    }
    
    public TcpDnsQueryDecoder(final DnsRecordDecoder decoder, final int maxFrameLength) {
        super(maxFrameLength, 0, 2, 0, 2);
        this.decoder = ObjectUtil.checkNotNull(decoder, "decoder");
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        final ByteBuf frame = (ByteBuf)super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        return DnsMessageUtil.decodeDnsQuery(this.decoder, frame.slice(), new DnsMessageUtil.DnsQueryFactory() {
            @Override
            public DnsQuery newQuery(final int id, final DnsOpCode dnsOpCode) {
                return new DefaultDnsQuery(id, dnsOpCode);
            }
        });
    }
}
