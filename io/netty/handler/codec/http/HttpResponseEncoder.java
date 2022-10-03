package io.netty.handler.codec.http;

import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class HttpResponseEncoder extends HttpObjectEncoder<HttpResponse>
{
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && !(msg instanceof HttpRequest);
    }
    
    @Override
    protected void encodeInitialLine(final ByteBuf buf, final HttpResponse response) throws Exception {
        response.protocolVersion().encode(buf);
        buf.writeByte(32);
        response.status().encode(buf);
        ByteBufUtil.writeShortBE(buf, 3338);
    }
    
    @Override
    protected void sanitizeHeadersBeforeEncode(final HttpResponse msg, final boolean isAlwaysEmpty) {
        if (isAlwaysEmpty) {
            final HttpResponseStatus status = msg.status();
            if (status.codeClass() == HttpStatusClass.INFORMATIONAL || status.code() == HttpResponseStatus.NO_CONTENT.code()) {
                msg.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                msg.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
            }
            else if (status.code() == HttpResponseStatus.RESET_CONTENT.code()) {
                msg.headers().remove(HttpHeaderNames.TRANSFER_ENCODING);
                msg.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, 0);
            }
        }
    }
    
    @Override
    protected boolean isContentAlwaysEmpty(final HttpResponse msg) {
        final HttpResponseStatus status = msg.status();
        if (status.codeClass() == HttpStatusClass.INFORMATIONAL) {
            return status.code() != HttpResponseStatus.SWITCHING_PROTOCOLS.code() || msg.headers().contains(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        }
        return status.code() == HttpResponseStatus.NO_CONTENT.code() || status.code() == HttpResponseStatus.NOT_MODIFIED.code() || status.code() == HttpResponseStatus.RESET_CONTENT.code();
    }
}
