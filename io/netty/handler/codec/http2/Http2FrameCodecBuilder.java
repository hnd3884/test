package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public class Http2FrameCodecBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2FrameCodec, Http2FrameCodecBuilder>
{
    private Http2FrameWriter frameWriter;
    
    protected Http2FrameCodecBuilder() {
    }
    
    Http2FrameCodecBuilder(final boolean server) {
        this.server(server);
        this.gracefulShutdownTimeoutMillis(0L);
    }
    
    public static Http2FrameCodecBuilder forClient() {
        return new Http2FrameCodecBuilder(false);
    }
    
    public static Http2FrameCodecBuilder forServer() {
        return new Http2FrameCodecBuilder(true);
    }
    
    Http2FrameCodecBuilder frameWriter(final Http2FrameWriter frameWriter) {
        this.frameWriter = ObjectUtil.checkNotNull(frameWriter, "frameWriter");
        return this;
    }
    
    public Http2Settings initialSettings() {
        return super.initialSettings();
    }
    
    public Http2FrameCodecBuilder initialSettings(final Http2Settings settings) {
        return super.initialSettings(settings);
    }
    
    public long gracefulShutdownTimeoutMillis() {
        return super.gracefulShutdownTimeoutMillis();
    }
    
    public Http2FrameCodecBuilder gracefulShutdownTimeoutMillis(final long gracefulShutdownTimeoutMillis) {
        return super.gracefulShutdownTimeoutMillis(gracefulShutdownTimeoutMillis);
    }
    
    public boolean isServer() {
        return super.isServer();
    }
    
    public int maxReservedStreams() {
        return super.maxReservedStreams();
    }
    
    public Http2FrameCodecBuilder maxReservedStreams(final int maxReservedStreams) {
        return super.maxReservedStreams(maxReservedStreams);
    }
    
    public boolean isValidateHeaders() {
        return super.isValidateHeaders();
    }
    
    public Http2FrameCodecBuilder validateHeaders(final boolean validateHeaders) {
        return super.validateHeaders(validateHeaders);
    }
    
    public Http2FrameLogger frameLogger() {
        return super.frameLogger();
    }
    
    public Http2FrameCodecBuilder frameLogger(final Http2FrameLogger frameLogger) {
        return super.frameLogger(frameLogger);
    }
    
    public boolean encoderEnforceMaxConcurrentStreams() {
        return super.encoderEnforceMaxConcurrentStreams();
    }
    
    public Http2FrameCodecBuilder encoderEnforceMaxConcurrentStreams(final boolean encoderEnforceMaxConcurrentStreams) {
        return super.encoderEnforceMaxConcurrentStreams(encoderEnforceMaxConcurrentStreams);
    }
    
    public int encoderEnforceMaxQueuedControlFrames() {
        return super.encoderEnforceMaxQueuedControlFrames();
    }
    
    public Http2FrameCodecBuilder encoderEnforceMaxQueuedControlFrames(final int maxQueuedControlFrames) {
        return super.encoderEnforceMaxQueuedControlFrames(maxQueuedControlFrames);
    }
    
    public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
        return super.headerSensitivityDetector();
    }
    
    public Http2FrameCodecBuilder headerSensitivityDetector(final Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        return super.headerSensitivityDetector(headerSensitivityDetector);
    }
    
    public Http2FrameCodecBuilder encoderIgnoreMaxHeaderListSize(final boolean ignoreMaxHeaderListSize) {
        return super.encoderIgnoreMaxHeaderListSize(ignoreMaxHeaderListSize);
    }
    
    @Deprecated
    public Http2FrameCodecBuilder initialHuffmanDecodeCapacity(final int initialHuffmanDecodeCapacity) {
        return super.initialHuffmanDecodeCapacity(initialHuffmanDecodeCapacity);
    }
    
    public Http2FrameCodecBuilder autoAckSettingsFrame(final boolean autoAckSettings) {
        return super.autoAckSettingsFrame(autoAckSettings);
    }
    
    public Http2FrameCodecBuilder autoAckPingFrame(final boolean autoAckPingFrame) {
        return super.autoAckPingFrame(autoAckPingFrame);
    }
    
    public Http2FrameCodecBuilder decoupleCloseAndGoAway(final boolean decoupleCloseAndGoAway) {
        return super.decoupleCloseAndGoAway(decoupleCloseAndGoAway);
    }
    
    public int decoderEnforceMaxConsecutiveEmptyDataFrames() {
        return super.decoderEnforceMaxConsecutiveEmptyDataFrames();
    }
    
    public Http2FrameCodecBuilder decoderEnforceMaxConsecutiveEmptyDataFrames(final int maxConsecutiveEmptyFrames) {
        return super.decoderEnforceMaxConsecutiveEmptyDataFrames(maxConsecutiveEmptyFrames);
    }
    
    public Http2FrameCodec build() {
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
    protected Http2FrameCodec build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
        final Http2FrameCodec codec = new Http2FrameCodec(encoder, decoder, initialSettings, this.decoupleCloseAndGoAway());
        codec.gracefulShutdownTimeoutMillis(this.gracefulShutdownTimeoutMillis());
        return codec;
    }
}
