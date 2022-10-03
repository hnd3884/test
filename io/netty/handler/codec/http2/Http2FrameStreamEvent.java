package io.netty.handler.codec.http2;

public final class Http2FrameStreamEvent
{
    private final Http2FrameStream stream;
    private final Type type;
    
    private Http2FrameStreamEvent(final Http2FrameStream stream, final Type type) {
        this.stream = stream;
        this.type = type;
    }
    
    public Http2FrameStream stream() {
        return this.stream;
    }
    
    public Type type() {
        return this.type;
    }
    
    static Http2FrameStreamEvent stateChanged(final Http2FrameStream stream) {
        return new Http2FrameStreamEvent(stream, Type.State);
    }
    
    static Http2FrameStreamEvent writabilityChanged(final Http2FrameStream stream) {
        return new Http2FrameStreamEvent(stream, Type.Writability);
    }
    
    enum Type
    {
        State, 
        Writability;
    }
}
