package io.netty.handler.codec.socks;

import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

final class SocksCommonUtils
{
    public static final SocksRequest UNKNOWN_SOCKS_REQUEST;
    public static final SocksResponse UNKNOWN_SOCKS_RESPONSE;
    private static final char ipv6hextetSeparator = ':';
    
    private SocksCommonUtils() {
    }
    
    public static String ipv6toStr(final byte[] src) {
        assert src.length == 16;
        final StringBuilder sb = new StringBuilder(39);
        ipv6toStr(sb, src, 0, 8);
        return sb.toString();
    }
    
    private static void ipv6toStr(final StringBuilder sb, final byte[] src, final int fromHextet, int toHextet) {
        --toHextet;
        int i;
        for (i = fromHextet; i < toHextet; ++i) {
            appendHextet(sb, src, i);
            sb.append(':');
        }
        appendHextet(sb, src, i);
    }
    
    private static void appendHextet(final StringBuilder sb, final byte[] src, final int i) {
        StringUtil.toHexString(sb, src, i << 1, 2);
    }
    
    static String readUsAscii(final ByteBuf buffer, final int length) {
        final String s = buffer.toString(buffer.readerIndex(), length, CharsetUtil.US_ASCII);
        buffer.skipBytes(length);
        return s;
    }
    
    static {
        UNKNOWN_SOCKS_REQUEST = new UnknownSocksRequest();
        UNKNOWN_SOCKS_RESPONSE = new UnknownSocksResponse();
    }
}
