package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

final class Http2EmptyDataFrameConnectionDecoder extends DecoratingHttp2ConnectionDecoder
{
    private final int maxConsecutiveEmptyFrames;
    
    Http2EmptyDataFrameConnectionDecoder(final Http2ConnectionDecoder delegate, final int maxConsecutiveEmptyFrames) {
        super(delegate);
        this.maxConsecutiveEmptyFrames = ObjectUtil.checkPositive(maxConsecutiveEmptyFrames, "maxConsecutiveEmptyFrames");
    }
    
    @Override
    public void frameListener(final Http2FrameListener listener) {
        if (listener != null) {
            super.frameListener(new Http2EmptyDataFrameListener(listener, this.maxConsecutiveEmptyFrames));
        }
        else {
            super.frameListener(null);
        }
    }
    
    @Override
    public Http2FrameListener frameListener() {
        final Http2FrameListener frameListener = this.frameListener0();
        if (frameListener instanceof Http2EmptyDataFrameListener) {
            return ((Http2EmptyDataFrameListener)frameListener).listener;
        }
        return frameListener;
    }
    
    Http2FrameListener frameListener0() {
        return super.frameListener();
    }
}
