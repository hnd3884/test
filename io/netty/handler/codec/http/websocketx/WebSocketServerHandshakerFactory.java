package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelFuture;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.internal.ObjectUtil;

public class WebSocketServerHandshakerFactory
{
    private final String webSocketURL;
    private final String subprotocols;
    private final WebSocketDecoderConfig decoderConfig;
    
    public WebSocketServerHandshakerFactory(final String webSocketURL, final String subprotocols, final boolean allowExtensions) {
        this(webSocketURL, subprotocols, allowExtensions, 65536);
    }
    
    public WebSocketServerHandshakerFactory(final String webSocketURL, final String subprotocols, final boolean allowExtensions, final int maxFramePayloadLength) {
        this(webSocketURL, subprotocols, allowExtensions, maxFramePayloadLength, false);
    }
    
    public WebSocketServerHandshakerFactory(final String webSocketURL, final String subprotocols, final boolean allowExtensions, final int maxFramePayloadLength, final boolean allowMaskMismatch) {
        this(webSocketURL, subprotocols, WebSocketDecoderConfig.newBuilder().allowExtensions(allowExtensions).maxFramePayloadLength(maxFramePayloadLength).allowMaskMismatch(allowMaskMismatch).build());
    }
    
    public WebSocketServerHandshakerFactory(final String webSocketURL, final String subprotocols, final WebSocketDecoderConfig decoderConfig) {
        this.webSocketURL = webSocketURL;
        this.subprotocols = subprotocols;
        this.decoderConfig = ObjectUtil.checkNotNull(decoderConfig, "decoderConfig");
    }
    
    public WebSocketServerHandshaker newHandshaker(final HttpRequest req) {
        final CharSequence version = req.headers().get(HttpHeaderNames.SEC_WEBSOCKET_VERSION);
        if (version == null) {
            return new WebSocketServerHandshaker00(this.webSocketURL, this.subprotocols, this.decoderConfig);
        }
        if (version.equals(WebSocketVersion.V13.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker13(this.webSocketURL, this.subprotocols, this.decoderConfig);
        }
        if (version.equals(WebSocketVersion.V08.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker08(this.webSocketURL, this.subprotocols, this.decoderConfig);
        }
        if (version.equals(WebSocketVersion.V07.toHttpHeaderValue())) {
            return new WebSocketServerHandshaker07(this.webSocketURL, this.subprotocols, this.decoderConfig);
        }
        return null;
    }
    
    @Deprecated
    public static void sendUnsupportedWebSocketVersionResponse(final Channel channel) {
        sendUnsupportedVersionResponse(channel);
    }
    
    public static ChannelFuture sendUnsupportedVersionResponse(final Channel channel) {
        return sendUnsupportedVersionResponse(channel, channel.newPromise());
    }
    
    public static ChannelFuture sendUnsupportedVersionResponse(final Channel channel, final ChannelPromise promise) {
        final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UPGRADE_REQUIRED, channel.alloc().buffer(0));
        res.headers().set(HttpHeaderNames.SEC_WEBSOCKET_VERSION, WebSocketVersion.V13.toHttpHeaderValue());
        HttpUtil.setContentLength(res, 0L);
        return channel.writeAndFlush(res, promise);
    }
}
