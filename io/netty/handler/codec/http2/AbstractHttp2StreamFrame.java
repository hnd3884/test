package io.netty.handler.codec.http2;

public abstract class AbstractHttp2StreamFrame implements Http2StreamFrame
{
    private Http2FrameStream stream;
    
    @Override
    public AbstractHttp2StreamFrame stream(final Http2FrameStream stream) {
        this.stream = stream;
        return this;
    }
    
    @Override
    public Http2FrameStream stream() {
        return this.stream;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Http2StreamFrame)) {
            return false;
        }
        final Http2StreamFrame other = (Http2StreamFrame)o;
        return this.stream == other.stream() || (this.stream != null && this.stream.equals(other.stream()));
    }
    
    @Override
    public int hashCode() {
        final Http2FrameStream stream = this.stream;
        if (stream == null) {
            return super.hashCode();
        }
        return stream.hashCode();
    }
}
