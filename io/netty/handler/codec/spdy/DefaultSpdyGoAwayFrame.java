package io.netty.handler.codec.spdy;

import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;

public class DefaultSpdyGoAwayFrame implements SpdyGoAwayFrame
{
    private int lastGoodStreamId;
    private SpdySessionStatus status;
    
    public DefaultSpdyGoAwayFrame(final int lastGoodStreamId) {
        this(lastGoodStreamId, 0);
    }
    
    public DefaultSpdyGoAwayFrame(final int lastGoodStreamId, final int statusCode) {
        this(lastGoodStreamId, SpdySessionStatus.valueOf(statusCode));
    }
    
    public DefaultSpdyGoAwayFrame(final int lastGoodStreamId, final SpdySessionStatus status) {
        this.setLastGoodStreamId(lastGoodStreamId);
        this.setStatus(status);
    }
    
    @Override
    public int lastGoodStreamId() {
        return this.lastGoodStreamId;
    }
    
    @Override
    public SpdyGoAwayFrame setLastGoodStreamId(final int lastGoodStreamId) {
        ObjectUtil.checkPositiveOrZero(lastGoodStreamId, "lastGoodStreamId");
        this.lastGoodStreamId = lastGoodStreamId;
        return this;
    }
    
    @Override
    public SpdySessionStatus status() {
        return this.status;
    }
    
    @Override
    public SpdyGoAwayFrame setStatus(final SpdySessionStatus status) {
        this.status = status;
        return this;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + StringUtil.NEWLINE + "--> Last-good-stream-ID = " + this.lastGoodStreamId() + StringUtil.NEWLINE + "--> Status: " + this.status();
    }
}
