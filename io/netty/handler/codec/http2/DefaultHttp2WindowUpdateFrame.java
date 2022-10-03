package io.netty.handler.codec.http2;

import io.netty.util.internal.StringUtil;

public class DefaultHttp2WindowUpdateFrame extends AbstractHttp2StreamFrame implements Http2WindowUpdateFrame
{
    private final int windowUpdateIncrement;
    
    public DefaultHttp2WindowUpdateFrame(final int windowUpdateIncrement) {
        this.windowUpdateIncrement = windowUpdateIncrement;
    }
    
    @Override
    public DefaultHttp2WindowUpdateFrame stream(final Http2FrameStream stream) {
        super.stream(stream);
        return this;
    }
    
    @Override
    public String name() {
        return "WINDOW_UPDATE";
    }
    
    @Override
    public int windowSizeIncrement() {
        return this.windowUpdateIncrement;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(stream=" + this.stream() + ", windowUpdateIncrement=" + this.windowUpdateIncrement + ')';
    }
}
