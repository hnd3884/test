package io.netty.handler.codec.http2;

import java.util.Collections;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;
import java.nio.CharBuffer;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collection;
import io.netty.channel.ChannelHandler;
import java.util.List;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;

public class Http2ServerUpgradeCodec implements HttpServerUpgradeHandler.UpgradeCodec
{
    private static final InternalLogger logger;
    private static final List<CharSequence> REQUIRED_UPGRADE_HEADERS;
    private static final ChannelHandler[] EMPTY_HANDLERS;
    private final String handlerName;
    private final Http2ConnectionHandler connectionHandler;
    private final ChannelHandler[] handlers;
    private final Http2FrameReader frameReader;
    private Http2Settings settings;
    
    public Http2ServerUpgradeCodec(final Http2ConnectionHandler connectionHandler) {
        this(null, connectionHandler, Http2ServerUpgradeCodec.EMPTY_HANDLERS);
    }
    
    public Http2ServerUpgradeCodec(final Http2MultiplexCodec http2Codec) {
        this(null, http2Codec, Http2ServerUpgradeCodec.EMPTY_HANDLERS);
    }
    
    public Http2ServerUpgradeCodec(final String handlerName, final Http2ConnectionHandler connectionHandler) {
        this(handlerName, connectionHandler, Http2ServerUpgradeCodec.EMPTY_HANDLERS);
    }
    
    public Http2ServerUpgradeCodec(final String handlerName, final Http2MultiplexCodec http2Codec) {
        this(handlerName, http2Codec, Http2ServerUpgradeCodec.EMPTY_HANDLERS);
    }
    
    public Http2ServerUpgradeCodec(final Http2FrameCodec http2Codec, final ChannelHandler... handlers) {
        this(null, http2Codec, handlers);
    }
    
    private Http2ServerUpgradeCodec(final String handlerName, final Http2ConnectionHandler connectionHandler, final ChannelHandler... handlers) {
        this.handlerName = handlerName;
        this.connectionHandler = connectionHandler;
        this.handlers = handlers;
        this.frameReader = new DefaultHttp2FrameReader();
    }
    
    @Override
    public Collection<CharSequence> requiredUpgradeHeaders() {
        return Http2ServerUpgradeCodec.REQUIRED_UPGRADE_HEADERS;
    }
    
    @Override
    public boolean prepareUpgradeResponse(final ChannelHandlerContext ctx, final FullHttpRequest upgradeRequest, final HttpHeaders headers) {
        try {
            final List<String> upgradeHeaders = upgradeRequest.headers().getAll(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
            if (upgradeHeaders.size() != 1) {
                throw new IllegalArgumentException("There must be 1 and only 1 " + (Object)Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER + " header.");
            }
            this.settings = this.decodeSettingsHeader(ctx, upgradeHeaders.get(0));
            return true;
        }
        catch (final Throwable cause) {
            Http2ServerUpgradeCodec.logger.info("Error during upgrade to HTTP/2", cause);
            return false;
        }
    }
    
    @Override
    public void upgradeTo(final ChannelHandlerContext ctx, final FullHttpRequest upgradeRequest) {
        try {
            ctx.pipeline().addAfter(ctx.name(), this.handlerName, this.connectionHandler);
            if (this.handlers != null) {
                final String name = ctx.pipeline().context(this.connectionHandler).name();
                for (int i = this.handlers.length - 1; i >= 0; --i) {
                    ctx.pipeline().addAfter(name, null, this.handlers[i]);
                }
            }
            this.connectionHandler.onHttpServerUpgrade(this.settings);
        }
        catch (final Http2Exception e) {
            ctx.fireExceptionCaught((Throwable)e);
            ctx.close();
        }
    }
    
    private Http2Settings decodeSettingsHeader(final ChannelHandlerContext ctx, final CharSequence settingsHeader) throws Http2Exception {
        final ByteBuf header = ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(settingsHeader), CharsetUtil.UTF_8);
        try {
            final ByteBuf payload = Base64.decode(header, Base64Dialect.URL_SAFE);
            final ByteBuf frame = createSettingsFrame(ctx, payload);
            return this.decodeSettings(ctx, frame);
        }
        finally {
            header.release();
        }
    }
    
    private Http2Settings decodeSettings(final ChannelHandlerContext ctx, final ByteBuf frame) throws Http2Exception {
        try {
            final Http2Settings decodedSettings = new Http2Settings();
            this.frameReader.readFrame(ctx, frame, new Http2FrameAdapter() {
                @Override
                public void onSettingsRead(final ChannelHandlerContext ctx, final Http2Settings settings) {
                    decodedSettings.copyFrom(settings);
                }
            });
            return decodedSettings;
        }
        finally {
            frame.release();
        }
    }
    
    private static ByteBuf createSettingsFrame(final ChannelHandlerContext ctx, final ByteBuf payload) {
        final ByteBuf frame = ctx.alloc().buffer(9 + payload.readableBytes());
        Http2CodecUtil.writeFrameHeader(frame, payload.readableBytes(), (byte)4, new Http2Flags(), 0);
        frame.writeBytes(payload);
        payload.release();
        return frame;
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(Http2ServerUpgradeCodec.class);
        REQUIRED_UPGRADE_HEADERS = Collections.singletonList(Http2CodecUtil.HTTP_UPGRADE_SETTINGS_HEADER);
        EMPTY_HANDLERS = new ChannelHandler[0];
    }
}
