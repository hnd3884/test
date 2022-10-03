package io.netty.handler.codec.http;

import io.netty.util.CharsetUtil;
import io.netty.util.AsciiString;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ByteBuf;

final class HttpHeadersEncoder
{
    private static final int COLON_AND_SPACE_SHORT = 14880;
    
    private HttpHeadersEncoder() {
    }
    
    static void encoderHeader(final CharSequence name, final CharSequence value, final ByteBuf buf) {
        final int nameLen = name.length();
        final int valueLen = value.length();
        final int entryLen = nameLen + valueLen + 4;
        buf.ensureWritable(entryLen);
        int offset = buf.writerIndex();
        writeAscii(buf, offset, name);
        offset += nameLen;
        ByteBufUtil.setShortBE(buf, offset, 14880);
        offset += 2;
        writeAscii(buf, offset, value);
        offset += valueLen;
        ByteBufUtil.setShortBE(buf, offset, 3338);
        offset += 2;
        buf.writerIndex(offset);
    }
    
    private static void writeAscii(final ByteBuf buf, final int offset, final CharSequence value) {
        if (value instanceof AsciiString) {
            ByteBufUtil.copy((AsciiString)value, 0, buf, offset, value.length());
        }
        else {
            buf.setCharSequence(offset, value, CharsetUtil.US_ASCII);
        }
    }
}
