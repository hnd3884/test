package io.netty.handler.codec.http2;

import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpScheme;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class InboundHttpToHttp2Adapter extends ChannelInboundHandlerAdapter
{
    private final Http2Connection connection;
    private final Http2FrameListener listener;
    
    public InboundHttpToHttp2Adapter(final Http2Connection connection, final Http2FrameListener listener) {
        this.connection = connection;
        this.listener = listener;
    }
    
    private static int getStreamId(final Http2Connection connection, final HttpHeaders httpHeaders) {
        return httpHeaders.getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), connection.remote().incrementAndGetNextStreamId());
    }
    
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        if (msg instanceof FullHttpMessage) {
            handle(ctx, this.connection, this.listener, (FullHttpMessage)msg);
        }
        else {
            super.channelRead(ctx, msg);
        }
    }
    
    static void handle(final ChannelHandlerContext ctx, final Http2Connection connection, final Http2FrameListener listener, final FullHttpMessage message) throws Http2Exception {
        try {
            final int streamId = getStreamId(connection, message.headers());
            Http2Stream stream = connection.stream(streamId);
            if (stream == null) {
                stream = connection.remote().createStream(streamId, false);
            }
            message.headers().set(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTP.name());
            final Http2Headers messageHeaders = HttpConversionUtil.toHttp2Headers(message, true);
            final boolean hasContent = message.content().isReadable();
            final boolean hasTrailers = !message.trailingHeaders().isEmpty();
            listener.onHeadersRead(ctx, streamId, messageHeaders, 0, !hasContent && !hasTrailers);
            if (hasContent) {
                listener.onDataRead(ctx, streamId, message.content(), 0, !hasTrailers);
            }
            if (hasTrailers) {
                final Http2Headers headers = HttpConversionUtil.toHttp2Headers(message.trailingHeaders(), true);
                listener.onHeadersRead(ctx, streamId, headers, 0, true);
            }
            stream.closeRemoteSide();
        }
        finally {
            message.release();
        }
    }
}
