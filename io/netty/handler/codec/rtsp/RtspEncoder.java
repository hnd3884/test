package io.netty.handler.codec.rtsp;

import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.internal.StringUtil;
import io.netty.util.CharsetUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObjectEncoder;

public class RtspEncoder extends HttpObjectEncoder<HttpMessage>
{
    private static final int CRLF_SHORT = 3338;
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && (msg instanceof HttpRequest || msg instanceof HttpResponse);
    }
    
    @Override
    protected void encodeInitialLine(final ByteBuf buf, final HttpMessage message) throws Exception {
        if (message instanceof HttpRequest) {
            final HttpRequest request = (HttpRequest)message;
            ByteBufUtil.copy(request.method().asciiName(), buf);
            buf.writeByte(32);
            buf.writeCharSequence(request.uri(), CharsetUtil.UTF_8);
            buf.writeByte(32);
            buf.writeCharSequence(request.protocolVersion().toString(), CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE(buf, 3338);
        }
        else {
            if (!(message instanceof HttpResponse)) {
                throw new UnsupportedMessageTypeException("Unsupported type " + StringUtil.simpleClassName(message));
            }
            final HttpResponse response = (HttpResponse)message;
            buf.writeCharSequence(response.protocolVersion().toString(), CharsetUtil.US_ASCII);
            buf.writeByte(32);
            ByteBufUtil.copy(response.status().codeAsText(), buf);
            buf.writeByte(32);
            buf.writeCharSequence(response.status().reasonPhrase(), CharsetUtil.US_ASCII);
            ByteBufUtil.writeShortBE(buf, 3338);
        }
    }
}
