package io.netty.handler.codec.http2;

import java.lang.annotation.Annotation;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.util.internal.ObjectUtil;
import io.netty.channel.ChannelHandler;

@Deprecated
public class Http2MultiplexCodecBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2MultiplexCodec, Http2MultiplexCodecBuilder>
{
    private Http2FrameWriter frameWriter;
    final ChannelHandler childHandler;
    private ChannelHandler upgradeStreamHandler;
    
    Http2MultiplexCodecBuilder(final boolean server, final ChannelHandler childHandler) {
        this.server(server);
        this.childHandler = checkSharable(ObjectUtil.checkNotNull(childHandler, "childHandler"));
        this.gracefulShutdownTimeoutMillis(0L);
    }
    
    private static ChannelHandler checkSharable(final ChannelHandler handler) {
        if (handler instanceof ChannelHandlerAdapter && !((ChannelHandlerAdapter)handler).isSharable() && !handler.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
            throw new IllegalArgumentException("The handler must be Sharable");
        }
        return handler;
    }
    
    Http2MultiplexCodecBuilder frameWriter(final Http2FrameWriter frameWriter) {
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        return this;
    }
    
    public static Http2MultiplexCodecBuilder forClient(final ChannelHandler childHandler) {
        return new Http2MultiplexCodecBuilder(false, childHandler);
    }
    
    public static Http2MultiplexCodecBuilder forServer(final ChannelHandler childHandler) {
        return new Http2MultiplexCodecBuilder(true, childHandler);
    }
    
    public Http2MultiplexCodecBuilder withUpgradeStreamHandler(final ChannelHandler upgradeStreamHandler) {
        if (this.isServer()) {
            throw new IllegalArgumentException("Server codecs don't use an extra handler for the upgrade stream");
        }
        this.upgradeStreamHandler = upgradeStreamHandler;
        return this;
    }
    
    public Http2Settings initialSettings() {
        return super.initialSettings();
    }
    
    public Http2MultiplexCodecBuilder initialSettings(final Http2Settings settings) {
        return super.initialSettings(settings);
    }
    
    public long gracefulShutdownTimeoutMillis() {
        return super.gracefulShutdownTimeoutMillis();
    }
    
    public Http2MultiplexCodecBuilder gracefulShutdownTimeoutMillis(final long gracefulShutdownTimeoutMillis) {
        return super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    }
    
    public boolean isServer() {
        return super.isServer();
    }
    
    public int maxReservedStreams() {
        return super.maxReservedStreams();
    }
    
    public Http2MultiplexCodecBuilder maxReservedStreams(final int maxReservedStreams) {
        return super.maxReservedStreams(maxReservedStreams);
    }
    
    public boolean isValidateHeaders() {
        return super.isValidateHeaders();
    }
    
    public Http2MultiplexCodecBuilder validateHeaders(final boolean validateHeaders) {
        return super.validateHeaders(validateHeaders);
    }
    
    public Http2FrameLogger frameLogger() {
        return super.frameLogger();
    }
    
    public Http2MultiplexCodecBuilder frameLogger(final Http2FrameLogger frameLogger) {
        return super.frameLogger(frameLogger);
    }
    
    public boolean encoderEnforceMaxConcurrentStreams() {
        return super.encoderEnforceMaxConcurrentStreams();
    }
    
    public Http2MultiplexCodecBuilder encoderEnforceMaxConcurrentStreams(final boolean encoderEnforceMaxConcurrentStreams) {
        return super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }
    
    public int encoderEnforceMaxQueuedControlFrames() {
        return super.encoderEnforceMaxQueuedControlFrames();
    }
    
    public Http2MultiplexCodecBuilder encoderEnforceMaxQueuedControlFrames(final int maxQueuedControlFrames) {
        return super.encoderEnforceMaxQueuedControlFrames(maxQueuedControlFrames);
    }
    
    public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
        return super.headerSensitivityDetector();
    }
    
    public Http2MultiplexCodecBuilder headerSensitivityDetector(final Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return super.headerSensitivityDetector(headerSensitivityDetector);
    }
    
    public Http2MultiplexCodecBuilder encoderIgnoreMaxHeaderListSize(final boolean ignoreMaxHeaderListSize) {
        return super.encoderIgnoreMaxHeaderListSize(ignoreMaxHeaderListSize);
    }
    
    @Deprecated
    public Http2MultiplexCodecBuilder initialHuffmanDecodeCapacity(final int initialHuffmanDecodeCapacity) {
        return super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }
    
    public Http2MultiplexCodecBuilder autoAckSettingsFrame(final boolean autoAckSettings) {
        return super.autoAckSettingsFrame(autoAckSettings);
    }
    
    public Http2MultiplexCodecBuilder autoAckPingFrame(final boolean autoAckPingFrame) {
        return super.autoAckPingFrame(autoAckPingFrame);
    }
    
    public Http2MultiplexCodecBuilder decoupleCloseAndGoAway(final boolean decoupleCloseAndGoAway) {
        return super.decoupleCloseAndGoAway(decoupleCloseAndGoAway);
    }
    
    public int decoderEnforceMaxConsecutiveEmptyDataFrames() {
        return super.decoderEnforceMaxConsecutiveEmptyDataFrames();
    }
    
    public Http2MultiplexCodecBuilder decoderEnforceMaxConsecutiveEmptyDataFrames(final int maxConsecutiveEmptyFrames) {
        return super.decoderEnforceMaxConsecutiveEmptyDataFrames(maxConsecutiveEmptyFrames);
    }
    
    public Http2MultiplexCodec build() {
        Http2FrameWriter frameWriter = this.frameWriter;
        if (frameWriter != null) {
            final DefaultHttp2Connection connection = new DefaultHttp2Connection(this.isServer(), this.maxReservedStreams());
            final Long maxHeaderListSize = this.initialSettings().maxHeaderListSize();
            Http2FrameReader frameReader = new DefaultHttp2FrameReader((maxHeaderListSize == null) ? new DefaultHttp2HeadersDecoder(this.isValidateHeaders()) : new DefaultHttp2HeadersDecoder(this.isValidateHeaders(), maxHeaderListSize));
            if (this.frameLogger() != null) {
                frameWriter = new Http2OutboundFrameLogger(frameWriter, this.frameLogger());
                frameReader = new Http2InboundFrameLogger(frameReader, this.frameLogger());
            }
            Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, frameWriter);
            if (this.encoderEnforceMaxConcurrentStreams()) {
                encoder = new StreamBufferingEncoder(encoder);
            }
            Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, frameReader, this.promisedRequestVerifier(), this.isAutoAckSettingsFrame(), this.isAutoAckPingFrame());
            final int maxConsecutiveEmptyDataFrames = this.decoderEnforceMaxConsecutiveEmptyDataFrames();
            if (maxConsecutiveEmptyDataFrames > 0) {
                decoder = new Http2EmptyDataFrameConnectionDecoder(decoder, maxConsecutiveEmptyDataFrames);
            }
            return this.build(decoder, encoder, this.initialSettings());
        }
        return super.build();
    }
    
    @Override
    protected Http2MultiplexCodec build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
        final Http2MultiplexCodec codec = new Http2MultiplexCodec(encoder, decoder, initialSettings, this.childHandler, this.upgradeStreamHandler, this.decoupleCloseAndGoAway());
        codec.gracefulShutdownTimeoutMillis(this.gracefulShutdownTimeoutMillis());
        return codec;
    }
}
