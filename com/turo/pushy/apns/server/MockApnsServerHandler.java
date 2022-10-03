package com.turo.pushy.apns.server;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Type;
import com.turo.pushy.apns.util.DateAsTimeSinceEpochTypeAdapter;
import java.util.concurrent.TimeUnit;
import com.google.gson.GsonBuilder;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.PromiseCombiner;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.channel.ChannelPromise;
import java.util.Date;
import java.util.UUID;
import com.eatthepath.uuid.FastUUID;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import org.slf4j.Logger;
import com.google.gson.Gson;
import io.netty.util.AsciiString;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2ConnectionHandler;

class MockApnsServerHandler extends Http2ConnectionHandler implements Http2FrameListener
{
    private final PushNotificationHandler pushNotificationHandler;
    private final MockApnsServerListener listener;
    private final Http2Connection.PropertyKey headersPropertyKey;
    private final Http2Connection.PropertyKey payloadPropertyKey;
    private static final AsciiString APNS_ID_HEADER;
    private static final int MAX_CONTENT_LENGTH = 4096;
    private static final Gson GSON;
    private static final Logger log;
    
    MockApnsServerHandler(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings, final PushNotificationHandler pushNotificationHandler, final MockApnsServerListener listener) {
        super(decoder, encoder, initialSettings);
        this.headersPropertyKey = this.connection().newKey();
        this.payloadPropertyKey = this.connection().newKey();
        this.pushNotificationHandler = pushNotificationHandler;
        this.listener = ((listener != null) ? listener : new NoopMockApnsServerListener());
    }
    
    public int onDataRead(final ChannelHandlerContext context, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) {
        final int bytesProcessed = data.readableBytes() + padding;
        final Http2Stream stream = this.connection().stream(streamId);
        if (stream.getProperty(this.payloadPropertyKey) == null) {
            stream.setProperty(this.payloadPropertyKey, (Object)data.alloc().heapBuffer(4096));
        }
        ((ByteBuf)stream.getProperty(this.payloadPropertyKey)).writeBytes(data);
        if (endOfStream) {
            this.handleEndOfStream(context, stream);
        }
        return bytesProcessed;
    }
    
    public void onHeadersRead(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final int padding, final boolean endOfStream) {
        final Http2Stream stream = this.connection().stream(streamId);
        stream.setProperty(this.headersPropertyKey, (Object)headers);
        if (endOfStream) {
            this.handleEndOfStream(context, stream);
        }
    }
    
    public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endOfStream) {
        this.onHeadersRead(ctx, streamId, headers, padding, endOfStream);
    }
    
    public void onPriorityRead(final ChannelHandlerContext ctx, final int streamId, final int streamDependency, final short weight, final boolean exclusive) {
    }
    
    public void onRstStreamRead(final ChannelHandlerContext ctx, final int streamId, final long errorCode) {
    }
    
    public void onSettingsAckRead(final ChannelHandlerContext ctx) {
    }
    
    public void onSettingsRead(final ChannelHandlerContext ctx, final Http2Settings settings) {
    }
    
    public void onPingRead(final ChannelHandlerContext ctx, final long l) {
    }
    
    public void onPingAckRead(final ChannelHandlerContext ctx, final long l) {
    }
    
    public void onPushPromiseRead(final ChannelHandlerContext ctx, final int streamId, final int promisedStreamId, final Http2Headers headers, final int padding) {
    }
    
    public void onGoAwayRead(final ChannelHandlerContext ctx, final int lastStreamId, final long errorCode, final ByteBuf debugData) {
    }
    
    public void onWindowUpdateRead(final ChannelHandlerContext ctx, final int streamId, final int windowSizeIncrement) {
    }
    
    public void onUnknownFrame(final ChannelHandlerContext ctx, final byte frameType, final int streamId, final Http2Flags flags, final ByteBuf payload) {
    }
    
    private void handleEndOfStream(final ChannelHandlerContext context, final Http2Stream stream) {
        final Http2Headers headers = (Http2Headers)stream.getProperty(this.headersPropertyKey);
        final ByteBuf payload = (ByteBuf)stream.getProperty(this.payloadPropertyKey);
        final ChannelPromise writePromise = context.newPromise();
        final CharSequence apnsIdSequence = (CharSequence)headers.get((Object)MockApnsServerHandler.APNS_ID_HEADER);
        UUID apnsIdFromHeaders;
        try {
            apnsIdFromHeaders = ((apnsIdSequence != null) ? FastUUID.parseUUID(apnsIdSequence) : UUID.randomUUID());
        }
        catch (final IllegalArgumentException e) {
            MockApnsServerHandler.log.error("Failed to parse `apns-id` header: {}", (Object)apnsIdSequence, (Object)e);
            apnsIdFromHeaders = UUID.randomUUID();
        }
        final UUID apnsId = apnsIdFromHeaders;
        try {
            this.pushNotificationHandler.handlePushNotification(headers, payload);
            this.write(context, new AcceptNotificationResponse(stream.id(), apnsId), writePromise);
            this.listener.handlePushNotificationAccepted(headers, payload);
        }
        catch (final RejectedNotificationException e2) {
            final Date deviceTokenExpirationTimestamp = (e2 instanceof UnregisteredDeviceTokenException) ? ((UnregisteredDeviceTokenException)e2).getDeviceTokenExpirationTimestamp() : null;
            this.write(context, new RejectNotificationResponse(stream.id(), apnsId, e2.getRejectionReason(), deviceTokenExpirationTimestamp), writePromise);
            this.listener.handlePushNotificationRejected(headers, payload, e2.getRejectionReason(), deviceTokenExpirationTimestamp);
        }
        catch (final Exception e3) {
            this.write(context, new RejectNotificationResponse(stream.id(), apnsId, RejectionReason.INTERNAL_SERVER_ERROR, null), writePromise);
            this.listener.handlePushNotificationRejected(headers, payload, RejectionReason.INTERNAL_SERVER_ERROR, null);
        }
        finally {
            if (stream.getProperty(this.payloadPropertyKey) != null) {
                ((ByteBuf)stream.getProperty(this.payloadPropertyKey)).release();
            }
            this.flush(context);
        }
    }
    
    public void write(final ChannelHandlerContext context, final Object message, final ChannelPromise writePromise) {
        if (message instanceof AcceptNotificationResponse) {
            final AcceptNotificationResponse acceptNotificationResponse = (AcceptNotificationResponse)message;
            final Http2Headers headers = (Http2Headers)new DefaultHttp2Headers().status((CharSequence)HttpResponseStatus.OK.codeAsText()).add((Object)MockApnsServerHandler.APNS_ID_HEADER, (Object)FastUUID.toString(acceptNotificationResponse.getApnsId()));
            this.encoder().writeHeaders(context, acceptNotificationResponse.getStreamId(), headers, 0, true, writePromise);
            MockApnsServerHandler.log.trace("Accepted push notification on stream {}", (Object)acceptNotificationResponse.getStreamId());
        }
        else if (message instanceof RejectNotificationResponse) {
            final RejectNotificationResponse rejectNotificationResponse = (RejectNotificationResponse)message;
            final Http2Headers headers = (Http2Headers)((Http2Headers)new DefaultHttp2Headers().status((CharSequence)rejectNotificationResponse.getErrorReason().getHttpResponseStatus().codeAsText()).add((Object)HttpHeaderNames.CONTENT_TYPE, (Object)"application/json")).add((Object)MockApnsServerHandler.APNS_ID_HEADER, (Object)FastUUID.toString(rejectNotificationResponse.getApnsId()));
            final ErrorPayload errorPayload = new ErrorPayload(rejectNotificationResponse.getErrorReason().getReasonText(), rejectNotificationResponse.getTimestamp());
            final byte[] payloadBytes = MockApnsServerHandler.GSON.toJson((Object)errorPayload).getBytes();
            final ChannelPromise headersPromise = context.newPromise();
            this.encoder().writeHeaders(context, rejectNotificationResponse.getStreamId(), headers, 0, false, headersPromise);
            final ChannelPromise dataPromise = context.newPromise();
            this.encoder().writeData(context, rejectNotificationResponse.getStreamId(), Unpooled.wrappedBuffer(payloadBytes), 0, true, dataPromise);
            final PromiseCombiner promiseCombiner = new PromiseCombiner();
            promiseCombiner.addAll(new Future[] { (Future)headersPromise, (Future)dataPromise });
            promiseCombiner.finish((Promise)writePromise);
            MockApnsServerHandler.log.trace("Rejected push notification on stream {}: {}", (Object)rejectNotificationResponse.getStreamId(), (Object)rejectNotificationResponse.getErrorReason());
        }
        else {
            context.write(message, writePromise);
        }
    }
    
    static {
        APNS_ID_HEADER = new AsciiString((CharSequence)"apns-id");
        GSON = new GsonBuilder().registerTypeAdapter((Type)Date.class, (Object)new DateAsTimeSinceEpochTypeAdapter(TimeUnit.MILLISECONDS)).create();
        log = LoggerFactory.getLogger((Class)MockApnsServerHandler.class);
    }
    
    public static class MockApnsServerHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<MockApnsServerHandler, MockApnsServerHandlerBuilder>
    {
        private PushNotificationHandler pushNotificationHandler;
        private MockApnsServerListener listener;
        
        MockApnsServerHandlerBuilder pushNotificationHandler(final PushNotificationHandler pushNotificationHandler) {
            this.pushNotificationHandler = pushNotificationHandler;
            return this;
        }
        
        MockApnsServerHandlerBuilder listener(final MockApnsServerListener listener) {
            this.listener = listener;
            return this;
        }
        
        public MockApnsServerHandlerBuilder initialSettings(final Http2Settings initialSettings) {
            return (MockApnsServerHandlerBuilder)super.initialSettings(initialSettings);
        }
        
        public MockApnsServerHandler build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
            final MockApnsServerHandler handler = new MockApnsServerHandler(decoder, encoder, initialSettings, this.pushNotificationHandler, this.listener);
            this.frameListener((Http2FrameListener)handler);
            return handler;
        }
        
        public MockApnsServerHandler build() {
            return (MockApnsServerHandler)super.build();
        }
    }
    
    private abstract static class ApnsResponse
    {
        private final int streamId;
        private final UUID apnsId;
        
        private ApnsResponse(final int streamId, final UUID apnsId) {
            this.streamId = streamId;
            this.apnsId = apnsId;
        }
        
        int getStreamId() {
            return this.streamId;
        }
        
        UUID getApnsId() {
            return this.apnsId;
        }
    }
    
    private static class AcceptNotificationResponse extends ApnsResponse
    {
        private AcceptNotificationResponse(final int streamId, final UUID apnsId) {
            super(streamId, apnsId);
        }
    }
    
    private static class RejectNotificationResponse extends ApnsResponse
    {
        private final RejectionReason errorReason;
        private final Date timestamp;
        
        RejectNotificationResponse(final int streamId, final UUID apnsId, final RejectionReason errorReason, final Date timestamp) {
            super(streamId, apnsId);
            this.errorReason = errorReason;
            this.timestamp = timestamp;
        }
        
        RejectionReason getErrorReason() {
            return this.errorReason;
        }
        
        Date getTimestamp() {
            return this.timestamp;
        }
    }
    
    private static class ErrorPayload
    {
        private final String reason;
        private final Date timestamp;
        
        ErrorPayload(final String reason, final Date timestamp) {
            this.reason = reason;
            this.timestamp = timestamp;
        }
    }
    
    private static final class NoopMockApnsServerListener implements MockApnsServerListener
    {
        @Override
        public void handlePushNotificationAccepted(final Http2Headers headers, final ByteBuf payload) {
        }
        
        @Override
        public void handlePushNotificationRejected(final Http2Headers headers, final ByteBuf payload, final RejectionReason rejectionReason, final Date deviceTokenExpirationTimestamp) {
        }
    }
}
