package io.netty.resolver.dns;

import io.netty.buffer.ByteBuf;
import java.net.UnknownHostException;
import java.net.IDN;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.dns.DnsRawRecord;
import java.net.InetAddress;
import io.netty.handler.codec.dns.DnsRecord;

final class DnsAddressDecoder
{
    private static final int INADDRSZ4 = 4;
    private static final int INADDRSZ6 = 16;
    
    static InetAddress decodeAddress(final DnsRecord record, final String name, final boolean decodeIdn) {
        if (!(record instanceof DnsRawRecord)) {
            return null;
        }
        final ByteBuf content = ((ByteBufHolder)record).content();
        final int contentLen = content.readableBytes();
        if (contentLen != 4 && contentLen != 16) {
            return null;
        }
        final byte[] addrBytes = new byte[contentLen];
        content.getBytes(content.readerIndex(), addrBytes);
        try {
            return InetAddress.getByAddress(decodeIdn ? IDN.toUnicode(name) : name, addrBytes);
        }
        catch (final UnknownHostException e) {
            throw new Error(e);
        }
    }
    
    private DnsAddressDecoder() {
    }
}
