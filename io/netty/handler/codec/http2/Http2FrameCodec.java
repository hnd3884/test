package io.netty.handler.codec.http2;

import io.netty.channel.Channel;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.logging.InternalLogger;

public class Http2FrameCodec extends Http2ConnectionHandler
{
    private static final InternalLogger LOG;
    protected final Http2Connection.PropertyKey streamKey;
    private final Http2Connection.PropertyKey upgradeKey;
    private final Integer initialFlowControlWindowSize;
    ChannelHandlerContext ctx;
    private int numBufferedStreams;
    private final IntObjectMap<DefaultHttp2FrameStream> frameStreamToInitializeMap;
    
    Http2FrameCodec(final Http2ConnectionEncoder encoder, final Http2ConnectionDecoder decoder, final Http2Settings initialSettings, final boolean decoupleCloseAndGoAway) {
        super(decoder, encoder, initialSettings, decoupleCloseAndGoAway);
        this.frameStreamToInitializeMap = new IntObjectHashMap<DefaultHttp2FrameStream>(8);
        decoder.frameListener(new FrameListener());
        this.connection().addListener(new ConnectionListener());
        this.connection().remote().flowController().listener(new Http2RemoteFlowControllerListener());
        this.streamKey = this.connection().newKey();
        this.upgradeKey = this.connection().newKey();
        this.initialFlowControlWindowSize = initialSettings.initialWindowSize();
    }
    
    DefaultHttp2FrameStream newStream() {
        return new DefaultHttp2FrameStream();
    }
    
    final void forEachActiveStream(final Http2FrameStreamVisitor streamVisitor) throws Http2Exception {
        assert this.ctx.executor().inEventLoop();
        if (this.connection().numActiveStreams() > 0) {
            this.connection().forEachActiveStream(new Http2StreamVisitor() {
                @Override
                public boolean visit(final Http2Stream stream) {
                    try {
                        return streamVisitor.visit(stream.getProperty(Http2FrameCodec.this.streamKey));
                    }
                    catch (final Throwable cause) {
                        Http2FrameCodec.this.onError(Http2FrameCodec.this.ctx, false, cause);
                        return false;
                    }
                }
            });
        }
    }
    
    int numInitializingStreams() {
        return this.frameStreamToInitializeMap.size();
    }
    
    @Override
    public final void handlerAdded(final ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(this.ctx = ctx);
        this.handlerAdded0(ctx);
        final Http2Connection connection = this.connection();
        if (connection.isServer()) {
            this.tryExpandConnectionFlowControlWindow(connection);
        }
    }
    
    private void tryExpandConnectionFlowControlWindow(final Http2Connection connection) throws Http2Exception {
        if (this.initialFlowControlWindowSize != null) {
            final Http2Stream connectionStream = connection.connectionStream();
            final Http2LocalFlowController localFlowController = connection.local().flowController();
            final int delta = this.initialFlowControlWindowSize - localFlowController.initialWindowSize(connectionStream);
            if (delta > 0) {
                localFlowController.incrementWindowSize(connectionStream, Math.max(delta << 1, delta));
                this.flush(this.ctx);
            }
        }
    }
    
    void handlerAdded0(final ChannelHandlerContext ctx) throws Exception {
    }
    
    @Override
    public final void userEventTriggered(final ChannelHandlerContext ctx, final Object evt) throws Exception {
        if (evt == Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE) {
            this.tryExpandConnectionFlowControlWindow(this.connection());
            ctx.executor().execute(new Runnable() {
                @Override
                public void run() {
                    ctx.fireUserEventTriggered(evt);
                }
            });
        }
        else if (evt instanceof HttpServerUpgradeHandler.UpgradeEvent) {
            final HttpServerUpgradeHandler.UpgradeEvent upgrade = (HttpServerUpgradeHandler.UpgradeEvent)evt;
            try {
                this.onUpgradeEvent(ctx, upgrade.retain());
                final Http2Stream stream = this.connection().stream(1);
                if (stream.getProperty(this.streamKey) == null) {
                    this.onStreamActive0(stream);
                }
                upgrade.upgradeRequest().headers().setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), 1);
                stream.setProperty(this.upgradeKey, true);
                InboundHttpToHttp2Adapter.handle(ctx, this.connection(), this.decoder().frameListener(), upgrade.upgradeRequest().retain());
            }
            finally {
                upgrade.release();
            }
        }
        else {
            ctx.fireUserEventTriggered(evt);
        }
    }
    
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) {
        if (msg instanceof Http2DataFrame) {
            final Http2DataFrame dataFrame = (Http2DataFrame)msg;
            this.encoder().writeData(ctx, dataFrame.stream().id(), dataFrame.content(), dataFrame.padding(), dataFrame.isEndStream(), promise);
        }
        else if (msg instanceof Http2HeadersFrame) {
            this.writeHeadersFrame(ctx, (Http2HeadersFrame)msg, promise);
        }
        else if (msg instanceof Http2WindowUpdateFrame) {
            final Http2WindowUpdateFrame frame = (Http2WindowUpdateFrame)msg;
            final Http2FrameStream frameStream = frame.stream();
            try {
                if (frameStream == null) {
                    this.increaseInitialConnectionWindow(frame.windowSizeIncrement());
                }
                else {
                    this.consumeBytes(frameStream.id(), frame.windowSizeIncrement());
                }
                promise.setSuccess();
            }
            catch (final Throwable t) {
                promise.setFailure(t);
            }
        }
        else if (msg instanceof Http2ResetFrame) {
            final Http2ResetFrame rstFrame = (Http2ResetFrame)msg;
            final int id = rstFrame.stream().id();
            if (this.connection().streamMayHaveExisted(id)) {
                this.encoder().writeRstStream(ctx, rstFrame.stream().id(), rstFrame.errorCode(), promise);
            }
            else {
                ReferenceCountUtil.release(rstFrame);
                promise.setFailure((Throwable)Http2Exception.streamError(rstFrame.stream().id(), Http2Error.PROTOCOL_ERROR, "Stream never existed", new Object[0]));
            }
        }
        else if (msg instanceof Http2PingFrame) {
            final Http2PingFrame frame2 = (Http2PingFrame)msg;
            this.encoder().writePing(ctx, frame2.ack(), frame2.content(), promise);
        }
        else if (msg instanceof Http2SettingsFrame) {
            this.encoder().writeSettings(ctx, ((Http2SettingsFrame)msg).settings(), promise);
        }
        else if (msg instanceof Http2SettingsAckFrame) {
            this.encoder().writeSettingsAck(ctx, promise);
        }
        else if (msg instanceof Http2GoAwayFrame) {
            this.writeGoAwayFrame(ctx, (Http2GoAwayFrame)msg, promise);
        }
        else if (msg instanceof Http2PushPromiseFrame) {
            final Http2PushPromiseFrame pushPromiseFrame = (Http2PushPromiseFrame)msg;
            this.writePushPromise(ctx, pushPromiseFrame, promise);
        }
        else if (msg instanceof Http2PriorityFrame) {
            final Http2PriorityFrame priorityFrame = (Http2PriorityFrame)msg;
            this.encoder().writePriority(ctx, priorityFrame.stream().id(), priorityFrame.streamDependency(), priorityFrame.weight(), priorityFrame.exclusive(), promise);
        }
        else if (msg instanceof Http2UnknownFrame) {
            final Http2UnknownFrame unknownFrame = (Http2UnknownFrame)msg;
            this.encoder().writeFrame(ctx, unknownFrame.frameType(), unknownFrame.stream().id(), unknownFrame.flags(), unknownFrame.content(), promise);
        }
        else {
            if (msg instanceof Http2Frame) {
                ReferenceCountUtil.release(msg);
                throw new UnsupportedMessageTypeException(msg, (Class<?>[])new Class[0]);
            }
            ctx.write(msg, promise);
        }
    }
    
    private void increaseInitialConnectionWindow(final int deltaBytes) throws Http2Exception {
        this.connection().local().flowController().incrementWindowSize(this.connection().connectionStream(), deltaBytes);
    }
    
    final boolean consumeBytes(final int streamId, final int bytes) throws Http2Exception {
        final Http2Stream stream = this.connection().stream(streamId);
        if (stream != null && streamId == 1) {
            final Boolean upgraded = stream.getProperty(this.upgradeKey);
            if (Boolean.TRUE.equals(upgraded)) {
                return false;
            }
        }
        return this.connection().local().flowController().consumeBytes(stream, bytes);
    }
    
    private void writeGoAwayFrame(final ChannelHandlerContext ctx, final Http2GoAwayFrame frame, final ChannelPromise promise) {
        if (frame.lastStreamId() > -1) {
            frame.release();
            throw new IllegalArgumentException("Last stream id must not be set on GOAWAY frame");
        }
        final int lastStreamCreated = this.connection().remote().lastStreamCreated();
        long lastStreamId = lastStreamCreated + frame.extraStreamIds() * 2L;
        if (lastStreamId > 2147483647L) {
            lastStreamId = 2147483647L;
        }
        this.goAway(ctx, (int)lastStreamId, frame.errorCode(), frame.content(), promise);
    }
    
    private void writeHeadersFrame(final ChannelHandlerContext ctx, final Http2HeadersFrame headersFrame, final ChannelPromise promise) {
        if (Http2CodecUtil.isStreamIdValid(headersFrame.stream().id())) {
            this.encoder().writeHeaders(ctx, headersFrame.stream().id(), headersFrame.headers(), headersFrame.padding(), headersFrame.isEndStream(), promise);
        }
        else if (this.initializeNewStream(ctx, (DefaultHttp2FrameStream)headersFrame.stream(), promise)) {
            final int streamId = headersFrame.stream().id();
            this.encoder().writeHeaders(ctx, streamId, headersFrame.headers(), headersFrame.padding(), headersFrame.isEndStream(), promise);
            if (!promise.isDone()) {
                ++this.numBufferedStreams;
                promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) {
                        Http2FrameCodec.this.numBufferedStreams--;
                        Http2FrameCodec.this.handleHeaderFuture(channelFuture, streamId);
                    }
                });
            }
            else {
                this.handleHeaderFuture(promise, streamId);
            }
        }
    }
    
    private void writePushPromise(final ChannelHandlerContext ctx, final Http2PushPromiseFrame pushPromiseFrame, final ChannelPromise promise) {
        if (Http2CodecUtil.isStreamIdValid(pushPromiseFrame.pushStream().id())) {
            this.encoder().writePushPromise(ctx, pushPromiseFrame.stream().id(), pushPromiseFrame.pushStream().id(), pushPromiseFrame.http2Headers(), pushPromiseFrame.padding(), promise);
        }
        else if (this.initializeNewStream(ctx, (DefaultHttp2FrameStream)pushPromiseFrame.pushStream(), promise)) {
            final int streamId = pushPromiseFrame.stream().id();
            this.encoder().writePushPromise(ctx, streamId, pushPromiseFrame.pushStream().id(), pushPromiseFrame.http2Headers(), pushPromiseFrame.padding(), promise);
            if (promise.isDone()) {
                this.handleHeaderFuture(promise, streamId);
            }
            else {
                ++this.numBufferedStreams;
                promise.addListener((GenericFutureListener<? extends Future<? super Void>>)new ChannelFutureListener() {
                    @Override
                    public void operationComplete(final ChannelFuture channelFuture) {
                        Http2FrameCodec.this.numBufferedStreams--;
                        Http2FrameCodec.this.handleHeaderFuture(channelFuture, streamId);
                    }
                });
            }
        }
    }
    
    private boolean initializeNewStream(final ChannelHandlerContext ctx, final DefaultHttp2FrameStream http2FrameStream, final ChannelPromise promise) {
        final Http2Connection connection = this.connection();
        final int streamId = connection.local().incrementAndGetNextStreamId();
        if (streamId < 0) {
            promise.setFailure((Throwable)new Http2NoMoreStreamIdsException());
            this.onHttp2Frame(ctx, new DefaultHttp2GoAwayFrame(connection.isServer() ? Integer.MAX_VALUE : 2147483646, Http2Error.NO_ERROR.code(), ByteBufUtil.writeAscii(ctx.alloc(), "Stream IDs exhausted on local stream creation")));
            return false;
        }
        http2FrameStream.id = streamId;
        final Object old = this.frameStreamToInitializeMap.put(streamId, http2FrameStream);
        assert old == null;
        return true;
    }
    
    private void handleHeaderFuture(final ChannelFuture channelFuture, final int streamId) {
        if (!channelFuture.isSuccess()) {
            this.frameStreamToInitializeMap.remove(streamId);
        }
    }
    
    private void onStreamActive0(final Http2Stream stream) {
        if (stream.id() != 1 && this.connection().local().isValidStreamId(stream.id())) {
            return;
        }
        final DefaultHttp2FrameStream stream2 = this.newStream().setStreamAndProperty(this.streamKey, stream);
        this.onHttp2StreamStateChanged(this.ctx, stream2);
    }
    
    @Override
    protected void onConnectionError(final ChannelHandlerContext ctx, final boolean outbound, final Throwable cause, final Http2Exception http2Ex) {
        if (!outbound) {
            ctx.fireExceptionCaught(cause);
        }
        super.onConnectionError(ctx, outbound, cause, http2Ex);
    }
    
    @Override
    protected final void onStreamError(final ChannelHandlerContext ctx, final boolean outbound, final Throwable cause, final Http2Exception.StreamException streamException) {
        final int streamId = streamException.streamId();
        final Http2Stream connectionStream = this.connection().stream(streamId);
        if (connectionStream == null) {
            onHttp2UnknownStreamError(ctx, cause, streamException);
            super.onStreamError(ctx, outbound, cause, streamException);
            return;
        }
        final Http2FrameStream stream = connectionStream.getProperty(this.streamKey);
        if (stream == null) {
            Http2FrameCodec.LOG.warn("Stream exception thrown without stream object attached.", cause);
            super.onStreamError(ctx, outbound, cause, streamException);
            return;
        }
        if (!outbound) {
            this.onHttp2FrameStreamException(ctx, new Http2FrameStreamException(stream, streamException.error(), cause));
        }
    }
    
    private static void onHttp2UnknownStreamError(final ChannelHandlerContext ctx, final Throwable cause, final Http2Exception.StreamException streamException) {
        Http2FrameCodec.LOG.log(InternalLogLevel.DEBUG, "Stream exception thrown for unknown stream {}.", (Object)streamException.streamId(), cause);
    }
    
    @Override
    protected final boolean isGracefulShutdownComplete() {
        return super.isGracefulShutdownComplete() && this.numBufferedStreams == 0;
    }
    
    private void onUpgradeEvent(final ChannelHandlerContext ctx, final HttpServerUpgradeHandler.UpgradeEvent evt) {
        ctx.fireUserEventTriggered((Object)evt);
    }
    
    private void onHttp2StreamWritabilityChanged(final ChannelHandlerContext ctx, final DefaultHttp2FrameStream stream, final boolean writable) {
        ctx.fireUserEventTriggered((Object)stream.writabilityChanged);
    }
    
    void onHttp2StreamStateChanged(final ChannelHandlerContext ctx, final DefaultHttp2FrameStream stream) {
        ctx.fireUserEventTriggered((Object)stream.stateChanged);
    }
    
    void onHttp2Frame(final ChannelHandlerContext ctx, final Http2Frame frame) {
        ctx.fireChannelRead((Object)frame);
    }
    
    void onHttp2FrameStreamException(final ChannelHandlerContext ctx, final Http2FrameStreamException cause) {
        ctx.fireExceptionCaught((Throwable)cause);
    }
    
    static {
        LOG = InternalLoggerFactory.getInstance(Http2FrameCodec.class);
    }
    
    private final class ConnectionListener extends Http2ConnectionAdapter
    {
        @Override
        public void onStreamAdded(final Http2Stream stream) {
            final DefaultHttp2FrameStream frameStream = Http2FrameCodec.this.frameStreamToInitializeMap.remove(stream.id());
            if (frameStream != null) {
                frameStream.setStreamAndProperty(Http2FrameCodec.this.streamKey, stream);
            }
        }
        
        @Override
        public void onStreamActive(final Http2Stream stream) {
            Http2FrameCodec.this.onStreamActive0(stream);
        }
        
        @Override
        public void onStreamClosed(final Http2Stream stream) {
            this.onHttp2StreamStateChanged0(stream);
        }
        
        @Override
        public void onStreamHalfClosed(final Http2Stream stream) {
            this.onHttp2StreamStateChanged0(stream);
        }
        
        private void onHttp2StreamStateChanged0(final Http2Stream stream) {
            final DefaultHttp2FrameStream stream2 = stream.getProperty(Http2FrameCodec.this.streamKey);
            if (stream2 != null) {
                Http2FrameCodec.this.onHttp2StreamStateChanged(Http2FrameCodec.this.ctx, stream2);
            }
        }
    }
    
    private final class FrameListener implements Http2FrameListener
    {
        @Override
        public void onUnknownFrame(final ChannelHandlerContext ctx, final byte frameType, final int streamId, final Http2Flags flags, final ByteBuf payload) {
            if (streamId == 0) {
                return;
            }
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2UnknownFrame(frameType, flags, payload).stream(this.requireStream(streamId)).retain());
        }
        
        @Override
        public void onSettingsRead(final ChannelHandlerContext ctx, final Http2Settings settings) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2SettingsFrame(settings));
        }
        
        @Override
        public void onPingRead(final ChannelHandlerContext ctx, final long data) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2PingFrame(data, false));
        }
        
        @Override
        public void onPingAckRead(final ChannelHandlerContext ctx, final long data) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2PingFrame(data, true));
        }
        
        @Override
        public void onRstStreamRead(final ChannelHandlerContext ctx, final int streamId, final long errorCode) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2ResetFrame(errorCode).stream(this.requireStream(streamId)));
        }
        
        @Override
        public void onWindowUpdateRead(final ChannelHandlerContext ctx, final int streamId, final int windowSizeIncrement) {
            if (streamId == 0) {
                return;
            }
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2WindowUpdateFrame(windowSizeIncrement).stream(this.requireStream(streamId)));
        }
        
        @Override
        public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endStream) {
            this.onHeadersRead(ctx, streamId, headers, padding, endStream);
        }
        
        @Override
        public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int padding, final boolean endOfStream) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2HeadersFrame(headers, endOfStream, padding).stream(this.requireStream(streamId)));
        }
        
        @Override
        public int onDataRead(final ChannelHandlerContext ctx, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2DataFrame(data, endOfStream, padding).stream(this.requireStream(streamId)).retain());
            return 0;
        }
        
        @Override
        public void onGoAwayRead(final ChannelHandlerContext ctx, final int lastStreamId, final long errorCode, final ByteBuf debugData) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2GoAwayFrame(lastStreamId, errorCode, debugData).retain());
        }
        
        @Override
        public void onPriorityRead(final ChannelHandlerContext ctx, final int streamId, final int streamDependency, final short weight, final boolean exclusive) {
            final Http2Stream stream = Http2FrameCodec.this.connection().stream(streamId);
            if (stream == null) {
                return;
            }
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2PriorityFrame(streamDependency, weight, exclusive).stream(this.requireStream(streamId)));
        }
        
        @Override
        public void onSettingsAckRead(final ChannelHandlerContext ctx) {
            Http2FrameCodec.this.onHttp2Frame(ctx, Http2SettingsAckFrame.INSTANCE);
        }
        
        @Override
        public void onPushPromiseRead(final ChannelHandlerContext ctx, final int streamId, final int promisedStreamId, final Http2Headers headers, final int padding) {
            Http2FrameCodec.this.onHttp2Frame(ctx, new DefaultHttp2PushPromiseFrame(headers, padding, promisedStreamId).pushStream(new DefaultHttp2FrameStream().setStreamAndProperty(Http2FrameCodec.this.streamKey, Http2FrameCodec.this.connection().stream(promisedStreamId))).stream(this.requireStream(streamId)));
        }
        
        private Http2FrameStream requireStream(final int streamId) {
            final Http2FrameStream stream = Http2FrameCodec.this.connection().stream(streamId).getProperty(Http2FrameCodec.this.streamKey);
            if (stream == null) {
                throw new IllegalStateException("Stream object required for identifier: " + streamId);
            }
            return stream;
        }
    }
    
    private final class Http2RemoteFlowControllerListener implements Http2RemoteFlowController.Listener
    {
        @Override
        public void writabilityChanged(final Http2Stream stream) {
            final DefaultHttp2FrameStream frameStream = stream.getProperty(Http2FrameCodec.this.streamKey);
            if (frameStream == null) {
                return;
            }
            Http2FrameCodec.this.onHttp2StreamWritabilityChanged(Http2FrameCodec.this.ctx, frameStream, Http2FrameCodec.this.connection().remote().flowController().isWritable(stream));
        }
    }
    
    static class DefaultHttp2FrameStream implements Http2FrameStream
    {
        private volatile int id;
        private volatile Http2Stream stream;
        final Http2FrameStreamEvent stateChanged;
        final Http2FrameStreamEvent writabilityChanged;
        Channel attachment;
        
        DefaultHttp2FrameStream() {
            this.id = -1;
            this.stateChanged = Http2FrameStreamEvent.stateChanged(this);
            this.writabilityChanged = Http2FrameStreamEvent.writabilityChanged(this);
        }
        
        DefaultHttp2FrameStream setStreamAndProperty(final Http2Connection.PropertyKey streamKey, final Http2Stream stream) {
            assert stream.id() == this.id;
            (this.stream = stream).setProperty(streamKey, this);
            return this;
        }
        
        @Override
        public int id() {
            final Http2Stream stream = this.stream;
            return (stream == null) ? this.id : stream.id();
        }
        
        @Override
        public Http2Stream.State state() {
            final Http2Stream stream = this.stream;
            return (stream == null) ? Http2Stream.State.IDLE : stream.state();
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.id());
        }
    }
}
