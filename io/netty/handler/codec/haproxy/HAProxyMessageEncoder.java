package io.netty.handler.codec.haproxy;

import java.util.List;
import io.netty.util.NetUtil;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.MessageToByteEncoder;

@ChannelHandler.Sharable
public final class HAProxyMessageEncoder extends MessageToByteEncoder<HAProxyMessage>
{
    private static final int V2_VERSION_BITMASK = 32;
    static final int UNIX_ADDRESS_BYTES_LENGTH = 108;
    static final int TOTAL_UNIX_ADDRESS_BYTES_LENGTH = 216;
    public static final HAProxyMessageEncoder INSTANCE;
    
    private HAProxyMessageEncoder() {
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final HAProxyMessage msg, final ByteBuf out) throws Exception {
        switch (msg.protocolVersion()) {
            case V1: {
                encodeV1(msg, out);
                break;
            }
            case V2: {
                encodeV2(msg, out);
                break;
            }
            default: {
                throw new HAProxyProtocolException("Unsupported version: " + msg.protocolVersion());
            }
        }
    }
    
    private static void encodeV1(final HAProxyMessage msg, final ByteBuf out) {
        out.writeBytes(HAProxyConstants.TEXT_PREFIX);
        out.writeByte(32);
        out.writeCharSequence(msg.proxiedProtocol().name(), CharsetUtil.US_ASCII);
        out.writeByte(32);
        out.writeCharSequence(msg.sourceAddress(), CharsetUtil.US_ASCII);
        out.writeByte(32);
        out.writeCharSequence(msg.destinationAddress(), CharsetUtil.US_ASCII);
        out.writeByte(32);
        out.writeCharSequence(String.valueOf(msg.sourcePort()), CharsetUtil.US_ASCII);
        out.writeByte(32);
        out.writeCharSequence(String.valueOf(msg.destinationPort()), CharsetUtil.US_ASCII);
        out.writeByte(13);
        out.writeByte(10);
    }
    
    private static void encodeV2(final HAProxyMessage msg, final ByteBuf out) {
        out.writeBytes(HAProxyConstants.BINARY_PREFIX);
        out.writeByte(0x20 | msg.command().byteValue());
        out.writeByte(msg.proxiedProtocol().byteValue());
        switch (msg.proxiedProtocol().addressFamily()) {
            case AF_IPv4:
            case AF_IPv6: {
                final byte[] srcAddrBytes = NetUtil.createByteArrayFromIpAddressString(msg.sourceAddress());
                final byte[] dstAddrBytes = NetUtil.createByteArrayFromIpAddressString(msg.destinationAddress());
                out.writeShort(srcAddrBytes.length + dstAddrBytes.length + 4 + msg.tlvNumBytes());
                out.writeBytes(srcAddrBytes);
                out.writeBytes(dstAddrBytes);
                out.writeShort(msg.sourcePort());
                out.writeShort(msg.destinationPort());
                encodeTlvs(msg.tlvs(), out);
                break;
            }
            case AF_UNIX: {
                out.writeShort(216 + msg.tlvNumBytes());
                final int srcAddrBytesWritten = out.writeCharSequence(msg.sourceAddress(), CharsetUtil.US_ASCII);
                out.writeZero(108 - srcAddrBytesWritten);
                final int dstAddrBytesWritten = out.writeCharSequence(msg.destinationAddress(), CharsetUtil.US_ASCII);
                out.writeZero(108 - dstAddrBytesWritten);
                encodeTlvs(msg.tlvs(), out);
                break;
            }
            case AF_UNSPEC: {
                out.writeShort(0);
                break;
            }
            default: {
                throw new HAProxyProtocolException("unexpected addrFamily");
            }
        }
    }
    
    private static void encodeTlv(final HAProxyTLV haProxyTLV, final ByteBuf out) {
        if (haProxyTLV instanceof HAProxySSLTLV) {
            final HAProxySSLTLV ssltlv = (HAProxySSLTLV)haProxyTLV;
            out.writeByte(haProxyTLV.typeByteValue());
            out.writeShort(ssltlv.contentNumBytes());
            out.writeByte(ssltlv.client());
            out.writeInt(ssltlv.verify());
            encodeTlvs(ssltlv.encapsulatedTLVs(), out);
        }
        else {
            out.writeByte(haProxyTLV.typeByteValue());
            final ByteBuf value = haProxyTLV.content();
            final int readableBytes = value.readableBytes();
            out.writeShort(readableBytes);
            out.writeBytes(value.readSlice(readableBytes));
        }
    }
    
    private static void encodeTlvs(final List<HAProxyTLV> haProxyTLVs, final ByteBuf out) {
        for (int i = 0; i < haProxyTLVs.size(); ++i) {
            encodeTlv(haProxyTLVs.get(i), out);
        }
    }
    
    static {
        INSTANCE = new HAProxyMessageEncoder();
    }
}
