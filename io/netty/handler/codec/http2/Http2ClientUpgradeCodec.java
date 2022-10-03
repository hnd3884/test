package io.netty.handler.codec.http2;

import java.util.Collections;
import java.util.Iterator;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.CharsetUtil;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.util.collection.CharObjectMap;
import io.netty.handler.codec.http.FullHttpResponse;
import java.util.Collection;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;
import java.util.List;
import io.netty.handler.codec.http.HttpClientUpgradeHandler;

public class Http2ClientUpgradeCodec implements HttpClientUpgradeHandler.UpgradeCodec
{
    private static final List<CharSequence> UPGRADE_HEADERS;
    private final String handlerName;
    private final Http2ConnectionHandler connectionHandler;
    private final ChannelHandler upgradeToHandler;
    private final ChannelHandler http2MultiplexHandler;
    
    public Http2ClientUpgradeCodec(final Http2FrameCodec frameCodec, final ChannelHandler upgradeToHandler) {
        this(null, frameCodec, upgradeToHandler);
    }
    
    public Http2ClientUpgradeCodec(final String handlerName, final Http2FrameCodec frameCodec, final ChannelHandler upgradeToHandler) {
        this(handlerName, frameCodec, upgradeToHandler, null);
    }
    
    public Http2ClientUpgradeCodec(final Http2ConnectionHandler connectionHandler) {
        this(null, connectionHandler);
    }
    
    public Http2ClientUpgradeCodec(final Http2ConnectionHandler connectionHandler, final Http2MultiplexHandler http2MultiplexHandler) {
        this(null, connectionHandler, http2MultiplexHandler);
    }
    
    public Http2ClientUpgradeCodec(final String handlerName, final Http2ConnectionHandler connectionHandler) {
        this(handlerName, connectionHandler, connectionHandler, null);
    }
    
    public Http2ClientUpgradeCodec(final String handlerName, final Http2ConnectionHandler connectionHandler, final Http2MultiplexHandler http2MultiplexHandler) {
        this(handlerName, connectionHandler, connectionHandler, http2MultiplexHandler);
    }
    
    private Http2ClientUpgradeCodec(final String handlerName, final Http2ConnectionHandler connectionHandler, final ChannelHandler upgradeToHandler, final Http2MultiplexHandler http2MultiplexHandler) {
        this.handlerName = handlerName;
        this.connectionHandler = ObjectUtil.checkNotNull(connectionHandler, "connectionHandler");
        this.upgradeToHandler = ObjectUtil.checkNotNull(upgradeToHandler, "upgradeToHandler");
        this.http2MultiplexHandler = http2MultiplexHandler;
    }
    
    @Override
    public CharSequence protocol() {
        return Http2CodecUtil.HTTP_UPGRADE_PROTOCOL_NAME;
    }
    
    @Override
    public Collection<CharSequence> setUpgradeHeaders(final ChannelHandlerContext ctx, final HttpRequest upgradeRequest) {
        final CharSequence settingsValue = this.getSettingsHeaderValue(ctx);
        upgradeRequest.headers().set(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER, settingsValue);
        return Http2ClientUpgradeCodec.UPGRADE_HEADERS;
    }
    
    @Override
    public void upgradeTo(final ChannelHandlerContext ctx, final FullHttpResponse upgradeResponse) throws Exception {
        try {
            ctx.pipeline().addAfter(ctx.name(), this.handlerName, this.upgradeToHandler);
            if (this.http2MultiplexHandler != null) {
                final String name = ctx.pipeline().context(this.connectionHandler).name();
                ctx.pipeline().addAfter(name, null, this.http2MultiplexHandler);
            }
            this.connectionHandler.onHttpClientUpgrade();
        }
        catch (final Http2Exception e) {
            ctx.fireExceptionCaught((Throwable)e);
            ctx.close();
        }
    }
    
    private CharSequence getSettingsHeaderValue(final ChannelHandlerContext ctx) {
        ByteBuf buf = null;
        ByteBuf encodedBuf = null;
        try {
            final Http2Settings settings = this.connectionHandler.decoder().localSettings();
            final int payloadLength = 6 * settings.size();
            buf = ctx.alloc().buffer(payloadLength);
            for (final CharObjectMap.PrimitiveEntry<Long> entry : settings.entries()) {
                buf.writeChar(entry.key());
                buf.writeInt(entry.value().intValue());
            }
            encodedBuf = Base64.encode(buf, Base64Dialect.URL_SAFE);
            return encodedBuf.toString(CharsetUtil.UTF_8);
        }
        finally {
            ReferenceCountUtil.release(buf);
            ReferenceCountUtil.release(encodedBuf);
        }
    }
    
    static {
        UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
    }
}
