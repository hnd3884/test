package com.turo.pushy.apns.server;

import io.netty.handler.codec.http2.AbstractHttp2ConnectionHandlerBuilder;
import java.util.UUID;
import io.netty.util.AsciiString;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Flags;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2FrameListener;
import io.netty.handler.codec.http2.Http2ConnectionHandler;

class BenchmarkApnsServerHandler extends Http2ConnectionHandler implements Http2FrameListener
{
    private static final Http2Headers SUCCESS_HEADERS;
    
    BenchmarkApnsServerHandler(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
        super(decoder, encoder, initialSettings);
    }
    
    public int onDataRead(final ChannelHandlerContext context, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) {
        if (endOfStream) {
            this.handleEndOfStream(context, streamId);
        }
        return data.readableBytes() + padding;
    }
    
    public void onHeadersRead(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final int padding, final boolean endOfStream) {
        if (endOfStream) {
            this.handleEndOfStream(context, streamId);
        }
    }
    
    private void handleEndOfStream(final ChannelHandlerContext context, final int streamId) {
        this.encoder().writeHeaders(context, streamId, BenchmarkApnsServerHandler.SUCCESS_HEADERS, 0, true, context.channel().newPromise());
    }
    
    public void onHeadersRead(final ChannelHandlerContext context, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endOfStream) {
        this.onHeadersRead(context, streamId, headers, padding, endOfStream);
    }
    
    public void onPriorityRead(final ChannelHandlerContext context, final int streamId, final int streamDependency, final short weight, final boolean exclusive) {
    }
    
    public void onRstStreamRead(final ChannelHandlerContext context, final int streamId, final long errorCode) {
    }
    
    public void onSettingsAckRead(final ChannelHandlerContext context) {
    }
    
    public void onSettingsRead(final ChannelHandlerContext context, final Http2Settings settings) {
    }
    
    public void onPingRead(final ChannelHandlerContext context, final long data) {
    }
    
    public void onPingAckRead(final ChannelHandlerContext context, final long data) {
    }
    
    public void onPushPromiseRead(final ChannelHandlerContext context, final int streamId, final int promisedStreamId, final Http2Headers headers, final int padding) {
    }
    
    public void onGoAwayRead(final ChannelHandlerContext context, final int lastStreamId, final long errorCode, final ByteBuf debugData) {
    }
    
    public void onWindowUpdateRead(final ChannelHandlerContext context, final int streamId, final int windowSizeIncrement) {
    }
    
    public void onUnknownFrame(final ChannelHandlerContext context, final byte frameType, final int streamId, final Http2Flags flags, final ByteBuf payload) {
    }
    
    static {
        SUCCESS_HEADERS = (Http2Headers)new DefaultHttp2Headers().status((CharSequence)HttpResponseStatus.OK.codeAsText()).add((Object)new AsciiString((CharSequence)"apns-id"), (Object)new AsciiString((CharSequence)UUID.randomUUID().toString()));
    }
    
    public static class BenchmarkApnsServerHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<BenchmarkApnsServerHandler, BenchmarkApnsServerHandlerBuilder>
    {
        public BenchmarkApnsServerHandlerBuilder initialSettings(final Http2Settings initialSettings) {
            return (BenchmarkApnsServerHandlerBuilder)super.initialSettings(initialSettings);
        }
        
        public BenchmarkApnsServerHandler build(final Http2ConnectionDecoder decoder, final Http2ConnectionEncoder encoder, final Http2Settings initialSettings) {
            final BenchmarkApnsServerHandler handler = new BenchmarkApnsServerHandler(decoder, encoder, initialSettings);
            this.frameListener((Http2FrameListener)handler);
            return handler;
        }
        
        public BenchmarkApnsServerHandler build() {
            return (BenchmarkApnsServerHandler)super.build();
        }
    }
}
