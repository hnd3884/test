package io.netty.handler.codec.dns;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import io.netty.channel.ChannelHandlerContext;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

@ChannelHandler.Sharable
public class DatagramDnsResponseDecoder extends MessageToMessageDecoder<DatagramPacket>
{
    private final DnsResponseDecoder<InetSocketAddress> responseDecoder;
    
    public DatagramDnsResponseDecoder() {
        this(DnsRecordDecoder.DEFAULT);
    }
    
    public DatagramDnsResponseDecoder(final DnsRecordDecoder recordDecoder) {
        this.responseDecoder = new DnsResponseDecoder<InetSocketAddress>(recordDecoder) {
            @Override
            protected DnsResponse newResponse(final InetSocketAddress sender, final InetSocketAddress recipient, final int id, final DnsOpCode opCode, final DnsResponseCode responseCode) {
                return new DatagramDnsResponse(sender, recipient, id, opCode, responseCode);
            }
        };
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final DatagramPacket packet, final List<Object> out) throws Exception {
        try {
            out.add(this.decodeResponse(ctx, packet));
        }
        catch (final IndexOutOfBoundsException e) {
            throw new CorruptedFrameException("Unable to decode response", e);
        }
    }
    
    protected DnsResponse decodeResponse(final ChannelHandlerContext ctx, final DatagramPacket packet) throws Exception {
        return this.responseDecoder.decode(((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).sender(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)packet).recipient(), ((DefaultAddressedEnvelope<ByteBuf, A>)packet).content());
    }
}
