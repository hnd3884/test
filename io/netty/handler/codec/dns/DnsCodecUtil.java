package io.netty.handler.codec.dns;

import io.netty.util.CharsetUtil;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ByteBuf;

final class DnsCodecUtil
{
    private DnsCodecUtil() {
    }
    
    static void encodeDomainName(final String name, final ByteBuf buf) {
        if (".".equals(name)) {
            buf.writeByte(0);
            return;
        }
        final String[] split;
        final String[] labels = split = name.split("\\.");
        for (final String label : split) {
            final int labelLen = label.length();
            if (labelLen == 0) {
                break;
            }
            buf.writeByte(labelLen);
            ByteBufUtil.writeAscii(buf, label);
        }
        buf.writeByte(0);
    }
    
    static String decodeDomainName(final ByteBuf in) {
        int position = -1;
        int checked = 0;
        final int end = in.writerIndex();
        final int readable = in.readableBytes();
        if (readable == 0) {
            return ".";
        }
        final StringBuilder name = new StringBuilder(readable << 1);
        while (in.isReadable()) {
            final int len = in.readUnsignedByte();
            final boolean pointer = (len & 0xC0) == 0xC0;
            if (pointer) {
                if (position == -1) {
                    position = in.readerIndex() + 1;
                }
                if (!in.isReadable()) {
                    throw new CorruptedFrameException("truncated pointer in a name");
                }
                final int next = (len & 0x3F) << 8 | in.readUnsignedByte();
                if (next >= end) {
                    throw new CorruptedFrameException("name has an out-of-range pointer");
                }
                in.readerIndex(next);
                checked += 2;
                if (checked >= end) {
                    throw new CorruptedFrameException("name contains a loop.");
                }
                continue;
            }
            else {
                if (len == 0) {
                    break;
                }
                if (!in.isReadable(len)) {
                    throw new CorruptedFrameException("truncated label in a name");
                }
                name.append(in.toString(in.readerIndex(), len, CharsetUtil.UTF_8)).append('.');
                in.skipBytes(len);
            }
        }
        if (position != -1) {
            in.readerIndex(position);
        }
        if (name.length() == 0) {
            return ".";
        }
        if (name.charAt(name.length() - 1) != '.') {
            name.append('.');
        }
        return name.toString();
    }
    
    static ByteBuf decompressDomainName(final ByteBuf compression) {
        final String domainName = decodeDomainName(compression);
        final ByteBuf result = compression.alloc().buffer(domainName.length() << 1);
        encodeDomainName(domainName, result);
        return result;
    }
}
