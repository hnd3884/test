package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

final class Http2EmptyDataFrameListener extends Http2FrameListenerDecorator
{
    private final int maxConsecutiveEmptyFrames;
    private boolean violationDetected;
    private int emptyDataFrames;
    
    Http2EmptyDataFrameListener(final Http2FrameListener listener, final int maxConsecutiveEmptyFrames) {
        super(listener);
        this.maxConsecutiveEmptyFrames = ObjectUtil.checkPositive(maxConsecutiveEmptyFrames, "maxConsecutiveEmptyFrames");
    }
    
    @Override
    public int onDataRead(final ChannelHandlerContext ctx, final int streamId, final ByteBuf data, final int padding, final boolean endOfStream) throws Http2Exception {
        if (endOfStream || data.isReadable()) {
            this.emptyDataFrames = 0;
        }
        else if (this.emptyDataFrames++ == this.maxConsecutiveEmptyFrames && !this.violationDetected) {
            this.violationDetected = true;
            throw Http2Exception.connectionError(Http2Error.ENHANCE_YOUR_CALM, "Maximum number %d of empty data frames without end_of_stream flag received", this.maxConsecutiveEmptyFrames);
        }
        return super.onDataRead(ctx, streamId, data, padding, endOfStream);
    }
    
    @Override
    public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int padding, final boolean endStream) throws Http2Exception {
        this.emptyDataFrames = 0;
        super.onHeadersRead(ctx, streamId, headers, padding, endStream);
    }
    
    @Override
    public void onHeadersRead(final ChannelHandlerContext ctx, final int streamId, final Http2Headers headers, final int streamDependency, final short weight, final boolean exclusive, final int padding, final boolean endStream) throws Http2Exception {
        this.emptyDataFrames = 0;
        super.onHeadersRead(ctx, streamId, headers, streamDependency, weight, exclusive, padding, endStream);
    }
}
