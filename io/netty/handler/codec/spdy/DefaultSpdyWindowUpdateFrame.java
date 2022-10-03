package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultSpdyWindowUpdateFrame implements SpdyWindowUpdateFrame
{
    private int streamId;
    private int deltaWindowSize;
    
    public DefaultSpdyWindowUpdateFrame(final int streamId, final int deltaWindowSize) {
        this.setStreamId(streamId);
        this.setDeltaWindowSize(deltaWindowSize);
    }
    
    @Override
    public int streamId() {
        return this.streamId;
    }
    
    @Override
    public SpdyWindowUpdateFrame setStreamId(final int streamId) {
        ObjectUtil.checkPositiveOrZero(streamId, "streamId");
        this.streamId = streamId;
        return this;
    }
    
    @Override
    public int deltaWindowSize() {
        return this.deltaWindowSize;
    }
    
    @Override
    public SpdyWindowUpdateFrame setDeltaWindowSize(final int deltaWindowSize) {
        ObjectUtil.checkPositive(deltaWindowSize, "deltaWindowSize");
        this.deltaWindowSize = deltaWindowSize;
        return this;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + StringUtil.NEWLINE + "--> Stream-ID = " + this.streamId() + StringUtil.NEWLINE + "--> Delta-Window-Size = " + this.deltaWindowSize();
    }
}
