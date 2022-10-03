package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.net.SocketAddress;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public final class TcpDnsResponseDecoder extends LengthFieldBasedFrameDecoder
{
    private final DnsResponseDecoder<SocketAddress> responseDecoder;
    
    public TcpDnsResponseDecoder() {
        this(DnsRecordDecoder.DEFAULT, 65536);
    }
    
    public TcpDnsResponseDecoder(final DnsRecordDecoder recordDecoder, final int maxFrameLength) {
        super(maxFrameLength, 0, 2, 0, 2);
        this.responseDecoder = new DnsResponseDecoder<SocketAddress>(recordDecoder) {
            @Override
            protected DnsResponse newResponse(final SocketAddress sender, final SocketAddress recipient, final int id, final DnsOpCode opCode, final DnsResponseCode responseCode) {
                return new DefaultDnsResponse(id, opCode, responseCode);
            }
        };
    }
    
    @Override
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        final ByteBuf frame = (ByteBuf)super.decode(ctx, in);
        if (frame == null) {
            return null;
        }
        try {
            return this.responseDecoder.decode(ctx.channel().remoteAddress(), ctx.channel().localAddress(), frame.slice());
        }
        finally {
            frame.release();
        }
    }
    
    @Override
    protected ByteBuf extractFrame(final ChannelHandlerContext ctx, final ByteBuf buffer, final int index, final int length) {
        return buffer.copy(index, length);
    }
}
