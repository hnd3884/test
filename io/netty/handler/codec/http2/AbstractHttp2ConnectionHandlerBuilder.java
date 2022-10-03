package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public abstract class AbstractHttp2ConnectionHandlerBuilder<T extends Http2ConnectionHandler, B extends AbstractHttp2ConnectionHandlerBuilder<T, B>>
{
    private static final Http2HeadersEncoder.SensitivityDetector DEFAULT_HEADER_SENSITIVITY_DETECTOR;
    private Http2Settings initialSettings;
    private Http2FrameListener frameListener;
    private long gracefulShutdownTimeoutMillis;
    private boolean decoupleCloseAndGoAway;
    private Boolean isServer;
    private Integer maxReservedStreams;
    private Http2Connection connection;
    private Http2ConnectionDecoder decoder;
    private Http2ConnectionEncoder encoder;
    private Boolean validateHeaders;
    private Http2FrameLogger frameLogger;
    private Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector;
    private Boolean encoderEnforceMaxConcurrentStreams;
    private Boolean encoderIgnoreMaxHeaderListSize;
    private Http2PromisedRequestVerifier promisedRequestVerifier;
    private boolean autoAckSettingsFrame;
    private boolean autoAckPingFrame;
    private int maxQueuedControlFrames;
    private int maxConsecutiveEmptyFrames;
    
    public AbstractHttp2ConnectionHandlerBuilder() {
        this.initialSettings = Http2Settings.defaultSettings();
        this.gracefulShutdownTimeoutMillis = Http2CodecUtil.DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS;
        this.promisedRequestVerifier = Http2PromisedRequestVerifier.ALWAYS_VERIFY;
        this.autoAckSettingsFrame = true;
        this.autoAckPingFrame = true;
        this.maxQueuedControlFrames = 10000;
        this.maxConsecutiveEmptyFrames = 2;
    }
    
    protected Http2Settings initialSettings() {
        return this.initialSettings;
    }
    
    protected B initialSettings(final Http2Settings settings) {
        this.initialSettings = ObjectUtil.checkNotNull(settings, "settings");
        return this.self();
    }
    
    protected Http2FrameListener frameListener() {
        return this.frameListener;
    }
    
    protected B frameListener(final Http2FrameListener frameListener) {
        this.frameListener = ObjectUtil.checkNotNull(frameListener, "frameListener");
        return this.self();
    }
    
    protected long gracefulShutdownTimeoutMillis() {
        return this.gracefulShutdownTimeoutMillis;
    }
    
    protected B gracefulShutdownTimeoutMillis(final long gracefulShutdownTimeoutMillis) {
        if (gracefulShutdownTimeoutMillis < -1L) {
            throw new IllegalArgumentException("gracefulShutdownTimeoutMillis: " + gracefulShutdownTimeoutMillis + " (expected: -1 for indefinite or >= 0)");
        }
        this.gracefulShutdownTimeoutMillis = gracefulShutdownTimeoutMillis;
        return this.self();
    }
    
    protected boolean isServer() {
        return this.isServer == null || this.isServer;
    }
    
    protected B server(final boolean isServer) {
        enforceConstraint("server", "connection", this.connection);
        enforceConstraint("server", "codec", this.decoder);
        enforceConstraint("server", "codec", this.encoder);
        this.isServer = isServer;
        return this.self();
    }
    
    protected int maxReservedStreams() {
        return (this.maxReservedStreams != null) ? this.maxReservedStreams : 100;
    }
    
    protected B maxReservedStreams(final int maxReservedStreams) {
        enforceConstraint("server", "connection", this.connection);
        enforceConstraint("server", "codec", this.decoder);
        enforceConstraint("server", "codec", this.encoder);
        this.maxReservedStreams = ObjectUtil.checkPositiveOrZero(maxReservedStreams, "maxReservedStreams");
        return this.self();
    }
    
    protected Http2Connection connection() {
        return this.connection;
    }
    
    protected B connection(final Http2Connection connection) {
        enforceConstraint("connection", "maxReservedStreams", this.maxReservedStreams);
        enforceConstraint("connection", "server", this.isServer);
        enforceConstraint("connection", "codec", this.decoder);
        enforceConstraint("connection", "codec", this.encoder);
        this.connection = ObjectUtil.checkNotNull(connection, "connection");
        return this.self();
    }
    
    protected Http2ConnectionDecoder decoder() {
        return this.decoder;
    }
    
    protected Http2ConnectionEncoder encoder() {
        return this.encoder;
    }
    
    protected B codec(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder) {
        enforceConstraint("codec", "server", this.isServer);
        enforceConstraint("codec", "maxReservedStreams", this.maxReservedStreams);
        enforceConstraint("codec", "connection", this.connection);
        enforceConstraint("codec", "frameLogger", this.frameLogger);
        enforceConstraint("codec", "validateHeaders", this.validateHeaders);
        enforceConstraint("codec", "headerSensitivityDetector", this.headerSensitivityDetector);
        enforceConstraint("codec", "encoderEnforceMaxConcurrentStreams", this.encoderEnforceMaxConcurrentStreams);
        ObjectUtil.checkNotNull(decoder, "decoder");
        ObjectUtil.checkNotNull(encoder, "encoder");
        if (decoder.connection() != encoder.connection()) {
            throw new IllegalArgumentException("The specified encoder and decoder have different connections.");
        }
        this.decoder = decoder;
        this.encoder = encoder;
        return this.self();
    }
    
    protected boolean isValidateHeaders() {
        return this.validateHeaders == null || this.validateHeaders;
    }
    
    protected B validateHeaders(final boolean validateHeaders) {
        this.enforceNonCodecConstraints("validateHeaders");
        this.validateHeaders = validateHeaders;
        return this.self();
    }
    
    protected Http2FrameLogger frameLogger() {
        return this.frameLogger;
    }
    
    protected B frameLogger(final Http2FrameLogger frameLogger) {
        this.enforceNonCodecConstraints("frameLogger");
        this.frameLogger = ObjectUtil.checkNotNull(frameLogger, "frameLogger");
        return this.self();
    }
    
    protected boolean encoderEnforceMaxConcurrentStreams() {
        return this.encoderEnforceMaxConcurrentStreams != null && this.encoderEnforceMaxConcurrentStreams;
    }
    
    protected B encoderEnforceMaxConcurrentStreams(final boolean encoderEnforceMaxConcurrentStreams) {
        this.enforceNonCodecConstraints("encoderEnforceMaxConcurrentStreams");
        this.encoderEnforceMaxConcurrentStreams = encoderEnforceMaxConcurrentStreams;
        return this.self();
    }
    
    protected int encoderEnforceMaxQueuedControlFrames() {
        return this.maxQueuedControlFrames;
    }
    
    protected B encoderEnforceMaxQueuedControlFrames(final int maxQueuedControlFrames) {
        this.enforceNonCodecConstraints("encoderEnforceMaxQueuedControlFrames");
        this.maxQueuedControlFrames = ObjectUtil.checkPositiveOrZero(maxQueuedControlFrames, "maxQueuedControlFrames");
        return this.self();
    }
    
    protected Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
        return (this.headerSensitivityDetector != null) ? this.headerSensitivityDetector : AbstractHttp2ConnectionHandlerBuilder.DEFAULT_HEADER_SENSITIVITY_DETECTOR;
    }
    
    protected B headerSensitivityDetector(final Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector) {
        this.enforceNonCodecConstraints("headerSensitivityDetector");
        this.headerSensitivityDetector = ObjectUtil.checkNotNull(headerSensitivityDetector, "headerSensitivityDetector");
        return this.self();
    }
    
    protected B encoderIgnoreMaxHeaderListSize(final boolean ignoreMaxHeaderListSize) {
        this.enforceNonCodecConstraints("encoderIgnoreMaxHeaderListSize");
        this.encoderIgnoreMaxHeaderListSize = ignoreMaxHeaderListSize;
        return this.self();
    }
    
    @Deprecated
    protected B initialHuffmanDecodeCapacity(final int initialHuffmanDecodeCapacity) {
        return this.self();
    }
    
    protected B promisedRequestVerifier(final Http2PromisedRequestVerifier promisedRequestVerifier) {
        this.enforceNonCodecConstraints("promisedRequestVerifier");
        this.promisedRequestVerifier = ObjectUtil.checkNotNull(promisedRequestVerifier, "promisedRequestVerifier");
        return this.self();
    }
    
    protected Http2PromisedRequestVerifier promisedRequestVerifier() {
        return this.promisedRequestVerifier;
    }
    
    protected int decoderEnforceMaxConsecutiveEmptyDataFrames() {
        return this.maxConsecutiveEmptyFrames;
    }
    
    protected B decoderEnforceMaxConsecutiveEmptyDataFrames(final int maxConsecutiveEmptyFrames) {
        this.enforceNonCodecConstraints("maxConsecutiveEmptyFrames");
        this.maxConsecutiveEmptyFrames = ObjectUtil.checkPositiveOrZero(maxConsecutiveEmptyFrames, "maxConsecutiveEmptyFrames");
        return this.self();
    }
    
    protected B autoAckSettingsFrame(final boolean autoAckSettings) {
        this.enforceNonCodecConstraints("autoAckSettingsFrame");
        this.autoAckSettingsFrame = autoAckSettings;
        return this.self();
    }
    
    protected boolean isAutoAckSettingsFrame() {
        return this.autoAckSettingsFrame;
    }
    
    protected B autoAckPingFrame(final boolean autoAckPingFrame) {
        this.enforceNonCodecConstraints("autoAckPingFrame");
        this.autoAckPingFrame = autoAckPingFrame;
        return this.self();
    }
    
    protected boolean isAutoAckPingFrame() {
        return this.autoAckPingFrame;
    }
    
    protected B decoupleCloseAndGoAway(final boolean decoupleCloseAndGoAway) {
        this.decoupleCloseAndGoAway = decoupleCloseAndGoAway;
        return this.self();
    }
    
    protected boolean decoupleCloseAndGoAway() {
        return this.decoupleCloseAndGoAway;
    }
    
    protected T build() {
        if (this.encoder == null) {
            Http2Connection connection = this.connection;
            if (connection == null) {
                connection = new DefaultHttp2Connection(this.isServer(), this.maxReservedStreams());
            }
            return this.buildFromConnection(connection);
        }
        assert this.decoder != null;
        return this.buildFromCodec(this.decoder, this.encoder);
    }
    
    private T buildFromConnection(final Http2Connection connection) {
        final Long maxHeaderListSize = this.initialSettings.maxHeaderListSize();
        Http2FrameReader reader = new DefaultHttp2FrameReader(new DefaultHttp2HeadersDecoder(this.isValidateHeaders(), (maxHeaderListSize == null) ? 8192L : maxHeaderListSize, -1));
        Http2FrameWriter writer = (this.encoderIgnoreMaxHeaderListSize == null) ? new DefaultHttp2FrameWriter(this.headerSensitivityDetector()) : new DefaultHttp2FrameWriter(this.headerSensitivityDetector(), this.encoderIgnoreMaxHeaderListSize);
        if (this.frameLogger != null) {
            reader = new Http2InboundFrameLogger(reader, this.frameLogger);
            writer = new Http2OutboundFrameLogger(writer, this.frameLogger);
        }
        Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(connection, writer);
        final boolean encoderEnforceMaxConcurrentStreams = this.encoderEnforceMaxConcurrentStreams();
        if (this.maxQueuedControlFrames != 0) {
            encoder = new Http2ControlFrameLimitEncoder(encoder, this.maxQueuedControlFrames);
        }
        if (encoderEnforceMaxConcurrentStreams) {
            if (connection.isServer()) {
                encoder.close();
                reader.close();
                throw new IllegalArgumentException("encoderEnforceMaxConcurrentStreams: " + encoderEnforceMaxConcurrentStreams + " not supported for server");
            }
            encoder = new StreamBufferingEncoder(encoder);
        }
        final DefaultHttp2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(connection, encoder, reader, this.promisedRequestVerifier(), this.isAutoAckSettingsFrame(), this.isAutoAckPingFrame());
        return this.buildFromCodec(decoder, encoder);
    }
    
    private T buildFromCodec(Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder) {
        final int maxConsecutiveEmptyDataFrames = this.decoderEnforceMaxConsecutiveEmptyDataFrames();
        if (maxConsecutiveEmptyDataFrames > 0) {
            decoder = new Http2EmptyDataFrameConnectionDecoder(decoder, maxConsecutiveEmptyDataFrames);
        }
        T handler;
        try {
            handler = this.build(decoder, encoder, this.initialSettings);
        }
        catch (final Throwable t) {
            encoder.close();
            decoder.close();
            throw new IllegalStateException("failed to build an Http2ConnectionHandler", t);
        }
        handler.gracefulShutdownTimeoutMillis(this.gracefulShutdownTimeoutMillis);
        if (handler.decoder().frameListener() == null) {
            handler.decoder().frameListener(this.frameListener);
        }
        return handler;
    }
    
    protected abstract T build(final Http2ConnectionDecoder p0, final Http2ConnectionEncoder p1, final Http2Settings p2) throws Exception;
    
    protected final B self() {
        return (B)this;
    }
    
    private void enforceNonCodecConstraints(final String rejected) {
        enforceConstraint(rejected, "server/connection", this.decoder);
        enforceConstraint(rejected, "server/connection", this.encoder);
    }
    
    private static void enforceConstraint(final String methodName, final String rejectorName, final Object value) {
        if (value != null) {
            throw new IllegalStateException(methodName + "() cannot be called because " + rejectorName + "() has been called already.");
        }
    }
    
    static {
        DEFAULT_HEADER_SENSITIVITY_DETECTOR = Http2HeadersEncoder.NEVER_SENSITIVE;
    }
}
