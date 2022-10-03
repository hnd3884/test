package com.turo.pushy.apns;

import java.util.Objects;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;
import com.turo.pushy.apns.util.DateAsTimeSinceEpochTypeAdapter;
import com.google.gson.GsonBuilder;
import java.util.Iterator;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2Error;
import java.util.UUID;
import java.util.Date;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.util.concurrent.TimeUnit;
import io.netty.channel.ChannelFuture;
import io.netty.handler.timeout.IdleStateEvent;
import com.eatthepath.uuid.FastUUID;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.PromiseCombiner;
import java.nio.charset.StandardCharsets;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import org.slf4j.Logger;
import com.google.gson.Gson;
import java.io.IOException;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.Map;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2ConnectionHandler;

class ApnsClientHandler extends Http2ConnectionHandler implements Http2FrameListener, Http2Connection.Listener
{
    private final Map<Integer, PushNotificationPromise> unattachedResponsePromisesByStreamId;
    private final Http2Connection.PropertyKey responseHeadersPropertyKey;
    private final Http2Connection.PropertyKey responsePromisePropertyKey;
    private final Http2Connection.PropertyKey streamErrorCausePropertyKey;
    private final String authority;
    private final long pingTimeoutMillis;
    private ScheduledFuture<?> pingTimeoutFuture;
    private Throwable connectionErrorCause;
    private static final String APNS_PATH_PREFIX = "/3/device/";
    private static final AsciiString APNS_EXPIRATION_HEADER;
    private static final AsciiString APNS_TOPIC_HEADER;
    private static final AsciiString APNS_PRIORITY_HEADER;
    private static final AsciiString APNS_COLLAPSE_ID_HEADER;
    private static final AsciiString APNS_ID_HEADER;
    private static final AsciiString APNS_PUSH_TYPE_HEADER;
    private static final int INITIAL_PAYLOAD_BUFFER_CAPACITY = 4096;
    private static final IOException STREAMS_EXHAUSTED_EXCEPTION;
    private static final IOException STREAM_CLOSED_BEFORE_REPLY_EXCEPTION;
    private static final Gson GSON;
    private static final Logger log;
    
    ApnsClientHandler(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings, final String authority, final long idlePingIntervalMillis) {
        super(decoder, encoder, initialSettings);
        this.unattachedResponsePromisesByStreamId = (Map<Integer, PushNotificationPromise>)new IntObjectHashMap();
        this.authority = authority;
        this.responseHeadersPropertyKey = this.connection().newKey();
        this.responsePromisePropertyKey = this.connection().newKey();
        this.streamErrorCausePropertyKey = this.connection().newKey();
        this.connection().addListener((Http2Connection.Listener)this);
        this.pingTimeoutMillis = idlePingIntervalMillis / 2L;
    }
    
    public void write(final ChannelHandlerContext context, final Object message, final ChannelPromise writePromise) {
        if (message instanceof PushNotificationPromise) {
            this.writePushNotification(context, (PushNotificationPromise)message, writePromise);
        }
        else {
            ApnsClientHandler.log.error("Unexpected object in pipeline: {}", message);
            context.write(message, writePromise);
        }
    }
    
    void retryPushNotificationFromStream(final ChannelHandlerContext context, final int streamId) {
        final Http2Stream stream = this.connection().stream(streamId);
        final PushNotificationPromise responsePromise = (PushNotificationPromise)stream.removeProperty(this.responsePromisePropertyKey);
        final ChannelPromise writePromise = context.channel().newPromise();
        this.writePushNotification(context, responsePromise, writePromise);
    }
    
    private void writePushNotification(final ChannelHandlerContext context, final PushNotificationPromise responsePromise, final ChannelPromise writePromise) {
        if (context.channel().isActive()) {
            final int streamId = this.connection().local().incrementAndGetNextStreamId();
            if (streamId > 0) {
                this.unattachedResponsePromisesByStreamId.put(streamId, responsePromise);
                final ApnsPushNotification pushNotification = responsePromise.getPushNotification();
                final Http2Headers headers = this.getHeadersForPushNotification(pushNotification, streamId);
                final ChannelPromise headersPromise = context.newPromise();
                this.encoder().writeHeaders(context, streamId, headers, 0, false, headersPromise);
                ApnsClientHandler.log.trace("Wrote headers on stream {}: {}", (Object)streamId, (Object)headers);
                final ByteBuf payloadBuffer = context.alloc().ioBuffer(4096);
                payloadBuffer.writeBytes(pushNotification.getPayload().getBytes(StandardCharsets.UTF_8));
                final ChannelPromise dataPromise = context.newPromise();
                this.encoder().writeData(context, streamId, payloadBuffer, 0, true, dataPromise);
                ApnsClientHandler.log.trace("Wrote payload on stream {}: {}", (Object)streamId, (Object)pushNotification.getPayload());
                final PromiseCombiner promiseCombiner = new PromiseCombiner();
                promiseCombiner.addAll(new Future[] { (Future)headersPromise, (Future)dataPromise });
                promiseCombiner.finish((Promise)writePromise);
                writePromise.addListener((GenericFutureListener)new GenericFutureListener<ChannelPromise>() {
                    public void operationComplete(final ChannelPromise future) {
                        if (!future.isSuccess()) {
                            ApnsClientHandler.log.trace("Failed to write push notification on stream {}.", (Object)streamId, (Object)future.cause());
                            responsePromise.tryFailure(future.cause());
                        }
                    }
                });
            }
            else {
                writePromise.tryFailure((Throwable)ApnsClientHandler.STREAMS_EXHAUSTED_EXCEPTION);
                context.channel().close();
            }
        }
        else {
            writePromise.tryFailure((Throwable)ApnsClientHandler.STREAM_CLOSED_BEFORE_REPLY_EXCEPTION);
        }
    }
    
    protected Http2Headers getHeadersForPushNotification(final ApnsPushNotification pushNotification, final int streamId) {
        final Http2Headers headers = (Http2Headers)new DefaultHttp2Headers().method((CharSequence)HttpMethod.POST.asciiName()).authority((CharSequence)this.authority).path((CharSequence)("/3/device/" + pushNotification.getToken())).scheme((CharSequence)HttpScheme.HTTPS.name()).addInt((Object)ApnsClientHandler.APNS_EXPIRATION_HEADER, (pushNotification.getExpiration() == null) ? 0 : ((int)(pushNotification.getExpiration().getTime() / 1000L)));
        if (pushNotification.getCollapseId() != null) {
            headers.add((Object)ApnsClientHandler.APNS_COLLAPSE_ID_HEADER, (Object)pushNotification.getCollapseId());
        }
        if (pushNotification.getPriority() != null) {
            headers.addInt((Object)ApnsClientHandler.APNS_PRIORITY_HEADER, pushNotification.getPriority().getCode());
        }
        if (pushNotification.getPushType() != null) {
            headers.add((Object)ApnsClientHandler.APNS_PUSH_TYPE_HEADER, (Object)pushNotification.getPushType().getHeaderValue());
        }
        if (pushNotification.getTopic() != null) {
            headers.add((Object)ApnsClientHandler.APNS_TOPIC_HEADER, (Object)pushNotification.getTopic());
        }
        if (pushNotification.getApnsId() != null) {
            headers.add((Object)ApnsClientHandler.APNS_ID_HEADER, (Object)FastUUID.toString(pushNotification.getApnsId()));
        }
        return headers;
    }
    
    public void userEventTriggered(final ChannelHandlerContext context, final Object event) throws Exception {
        if (event instanceof IdleStateEvent) {
            ApnsClientHandler.log.trace("Sending ping due to inactivity.");
            this.encoder().writePing(context, false, System.currentTimeMillis(), context.newPromise()).addListener((GenericFutureListener)new GenericFutureListener<ChannelFuture>() {
                public void operationComplete(final ChannelFuture future) {
                    if (!future.isSuccess()) {
                        ApnsClientHandler.log.debug("Failed to write PING frame.", future.cause());
                        future.channel().close();
                    }
                }
            });
            this.pingTimeoutFuture = (ScheduledFuture<?>)context.channel().eventLoop().schedule((Runnable)new Runnable() {
                @Override
                public void run() {
                    ApnsClientHandler.log.debug("Closing channel due to ping timeout.");
                    context.channel().close();
                }
            }, this.pingTimeoutMillis, TimeUnit.MILLISECONDS);
            this.flush(context);
        }
        super.userEventTriggered(context, event);
    }
    
    public int onDataRead(final ChannelHandlerContext context, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) {
        ApnsClientHandler.log.trace("Received data from APNs gateway on stream {}: {}", (Object)streamId, (Object)data.toString(StandardCharsets.UTF_8));
        final int bytesProcessed = data.readableBytes() + padding;
        if (endOfStream) {
            final Http2Stream stream = this.connection().stream(streamId);
            this.handleEndOfStream(context, this.connection().stream(streamId), (Http2Headers)stream.getProperty(this.responseHeadersPropertyKey), data);
        }
        else {
            ApnsClientHandler.log.error("Gateway sent a DATA frame that was not the end of a stream.");
        }
        return bytesProcessed;
    }
    
    public void onHeadersRead(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endOfStream) {
        this.onHeadersRead(context, streamId, headers, padding, endOfStream);
    }
    
    public void onHeadersRead(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final int padding, final boolean endOfStream) {
        ApnsClientHandler.log.trace("Received headers from APNs gateway on stream {}: {}", (Object)streamId, (Object)headers);
        final Http2Stream stream = this.connection().stream(streamId);
        if (endOfStream) {
            this.handleEndOfStream(context, stream, headers, null);
        }
        else {
            stream.setProperty(this.responseHeadersPropertyKey, (Object)headers);
        }
    }
    
    private void handleEndOfStream(final ChannelHandlerContext context, final Http2Stream stream, final Http2Headers headers, final ByteBuf data) {
        final PushNotificationPromise<ApnsPushNotification, PushNotificationResponse<ApnsPushNotification>> responsePromise = (PushNotificationPromise<ApnsPushNotification, PushNotificationResponse<ApnsPushNotification>>)stream.getProperty(this.responsePromisePropertyKey);
        final ApnsPushNotification pushNotification = responsePromise.getPushNotification();
        final HttpResponseStatus status = HttpResponseStatus.parseLine(headers.status());
        if (HttpResponseStatus.OK.equals((Object)status)) {
            responsePromise.trySuccess((Object)new SimplePushNotificationResponse(responsePromise.getPushNotification(), true, getApnsIdFromHeaders(headers), null, null));
        }
        else if (data != null) {
            final ErrorResponse errorResponse = (ErrorResponse)ApnsClientHandler.GSON.fromJson(data.toString(StandardCharsets.UTF_8), (Class)ErrorResponse.class);
            this.handleErrorResponse(context, stream.id(), headers, pushNotification, errorResponse);
        }
        else {
            ApnsClientHandler.log.warn("Gateway sent an end-of-stream HEADERS frame for an unsuccessful notification.");
        }
    }
    
    protected void handleErrorResponse(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final ApnsPushNotification pushNotification, final ErrorResponse errorResponse) {
        final PushNotificationPromise<ApnsPushNotification, PushNotificationResponse<ApnsPushNotification>> responsePromise = (PushNotificationPromise<ApnsPushNotification, PushNotificationResponse<ApnsPushNotification>>)this.connection().stream(streamId).getProperty(this.responsePromisePropertyKey);
        final HttpResponseStatus status = HttpResponseStatus.parseLine(headers.status());
        responsePromise.trySuccess((Object)new SimplePushNotificationResponse(responsePromise.getPushNotification(), HttpResponseStatus.OK.equals((Object)status), getApnsIdFromHeaders(headers), errorResponse.getReason(), errorResponse.getTimestamp()));
    }
    
    private static UUID getApnsIdFromHeaders(final Http2Headers headers) {
        final CharSequence apnsIdSequence = (CharSequence)headers.get((Object)ApnsClientHandler.APNS_ID_HEADER);
        try {
            return (apnsIdSequence != null) ? FastUUID.parseUUID(apnsIdSequence) : null;
        }
        catch (final IllegalArgumentException e) {
            ApnsClientHandler.log.error("Failed to parse `apns-id` header: {}", (Object)apnsIdSequence, (Object)e);
            return null;
        }
    }
    
    public void onPriorityRead(final ChannelHandlerContext ctx, final int streamId, final int streamDependency, final short weight, final boolean exclusive) {
    }
    
    public void onRstStreamRead(final ChannelHandlerContext context, final int streamId, final long errorCode) {
        if (errorCode == Http2Error.REFUSED_STREAM.code()) {
            this.retryPushNotificationFromStream(context, streamId);
        }
    }
    
    public void onSettingsAckRead(final ChannelHandlerContext ctx) {
    }
    
    public void onSettingsRead(final ChannelHandlerContext context, final Http2Settings settings) {
        ApnsClientHandler.log.trace("Received settings from APNs gateway: {}", (Object)settings);
    }
    
    public void onPingRead(final ChannelHandlerContext ctx, final long pingData) {
    }
    
    public void onPingAckRead(final ChannelHandlerContext context, final long pingData) {
        if (this.pingTimeoutFuture != null) {
            ApnsClientHandler.log.trace("Received reply to ping.");
            this.pingTimeoutFuture.cancel(false);
        }
        else {
            ApnsClientHandler.log.error("Received PING ACK, but no corresponding outbound PING found.");
        }
    }
    
    public void onPushPromiseRead(final ChannelHandlerContext ctx, final int streamId, final int promisedStreamId, final Http2Headers headers, final int padding) {
    }
    
    public void onGoAwayRead(final ChannelHandlerContext context, final int lastStreamId, final long errorCode, final ByteBuf debugData) {
        ApnsClientHandler.log.info("Received GOAWAY from APNs server: {}", (Object)debugData.toString(StandardCharsets.UTF_8));
    }
    
    public void onWindowUpdateRead(final ChannelHandlerContext ctx, final int streamId, final int windowSizeIncrement) {
    }
    
    public void onUnknownFrame(final ChannelHandlerContext ctx, final byte frameType, final int streamId, final Http2Flags flags, final ByteBuf payload) {
    }
    
    public void onStreamAdded(final Http2Stream stream) {
        stream.setProperty(this.responsePromisePropertyKey, (Object)this.unattachedResponsePromisesByStreamId.remove(stream.id()));
    }
    
    public void onStreamActive(final Http2Stream stream) {
    }
    
    public void onStreamHalfClosed(final Http2Stream stream) {
    }
    
    public void onStreamClosed(final Http2Stream stream) {
        final Promise<PushNotificationResponse<ApnsPushNotification>> responsePromise = (Promise<PushNotificationResponse<ApnsPushNotification>>)stream.getProperty(this.responsePromisePropertyKey);
        if (responsePromise != null) {
            Throwable cause;
            if (stream.getProperty(this.streamErrorCausePropertyKey) != null) {
                cause = (Throwable)stream.getProperty(this.streamErrorCausePropertyKey);
            }
            else if (this.connectionErrorCause != null) {
                cause = this.connectionErrorCause;
            }
            else {
                cause = ApnsClientHandler.STREAM_CLOSED_BEFORE_REPLY_EXCEPTION;
            }
            responsePromise.tryFailure(cause);
        }
    }
    
    public void onStreamRemoved(final Http2Stream stream) {
        stream.removeProperty(this.responseHeadersPropertyKey);
        stream.removeProperty(this.responsePromisePropertyKey);
    }
    
    public void onGoAwaySent(final int lastStreamId, final long errorCode, final ByteBuf debugData) {
    }
    
    public void onGoAwayReceived(final int lastStreamId, final long errorCode, final ByteBuf debugData) {
    }
    
    protected void onStreamError(final ChannelHandlerContext context, final boolean isOutbound, final Throwable cause, final Http2Exception.StreamException streamException) {
        final Http2Stream stream = this.connection().stream(streamException.streamId());
        stream.setProperty(this.streamErrorCausePropertyKey, (Object)streamException);
        super.onStreamError(context, isOutbound, cause, streamException);
    }
    
    protected void onConnectionError(final ChannelHandlerContext context, final boolean isOutbound, final Throwable cause, final Http2Exception http2Exception) {
        this.connectionErrorCause = (Throwable)((http2Exception != null) ? http2Exception : cause);
        super.onConnectionError(context, isOutbound, cause, http2Exception);
    }
    
    public void channelInactive(final ChannelHandlerContext context) throws Exception {
        super.channelInactive(context);
        for (final PushNotificationPromise promise : this.unattachedResponsePromisesByStreamId.values()) {
            promise.tryFailure((Throwable)ApnsClientHandler.STREAM_CLOSED_BEFORE_REPLY_EXCEPTION);
        }
        this.unattachedResponsePromisesByStreamId.clear();
    }
    
    static {
        APNS_EXPIRATION_HEADER = new AsciiString((CharSequence)"apns-expiration");
        APNS_TOPIC_HEADER = new AsciiString((CharSequence)"apns-topic");
        APNS_PRIORITY_HEADER = new AsciiString((CharSequence)"apns-priority");
        APNS_COLLAPSE_ID_HEADER = new AsciiString((CharSequence)"apns-collapse-id");
        APNS_ID_HEADER = new AsciiString((CharSequence)"apns-id");
        APNS_PUSH_TYPE_HEADER = new AsciiString((CharSequence)"apns-push-type");
        STREAMS_EXHAUSTED_EXCEPTION = new IOException("HTTP/2 streams exhausted; closing connection.");
        STREAM_CLOSED_BEFORE_REPLY_EXCEPTION = new IOException("Stream closed before a reply was received");
        GSON = new GsonBuilder().registerTypeAdapter((Type)Date.class, (Object)new DateAsTimeSinceEpochTypeAdapter(TimeUnit.MILLISECONDS)).create();
        log = LoggerFactory.getLogger((Class)ApnsClientHandler.class);
    }
    
    public static class ApnsClientHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<ApnsClientHandler, ApnsClientHandlerBuilder>
    {
        private String authority;
        private long idlePingIntervalMillis;
        
        ApnsClientHandlerBuilder authority(final String authority) {
            this.authority = authority;
            return this;
        }
        
        String authority() {
            return this.authority;
        }
        
        long idlePingIntervalMillis() {
            return this.idlePingIntervalMillis;
        }
        
        ApnsClientHandlerBuilder idlePingIntervalMillis(final long idlePingIntervalMillis) {
            this.idlePingIntervalMillis = idlePingIntervalMillis;
            return this;
        }
        
        public ApnsClientHandlerBuilder frameLogger(final Http2FrameLogger frameLogger) {
            return (ApnsClientHandlerBuilder)super.frameLogger(frameLogger);
        }
        
        public Http2FrameLogger frameLogger() {
            return super.frameLogger();
        }
        
        protected final boolean isServer() {
            return false;
        }
        
        protected boolean encoderEnforceMaxConcurrentStreams() {
            return true;
        }
        
        public ApnsClientHandler build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
            Objects.requireNonNull(this.authority(), "Authority must be set before building an ApnsClientHandler.");
            final ApnsClientHandler handler = new ApnsClientHandler(decoder, encoder, initialSettings, this.authority(), this.idlePingIntervalMillis());
            this.frameListener((Http2FrameListener)handler);
            return handler;
        }
        
        public ApnsClientHandler build() {
            return (ApnsClientHandler)super.build();
        }
    }
}
