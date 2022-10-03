package io.netty.handler.codec.http2;

public final class DefaultHttp2PriorityFrame extends AbstractHttp2StreamFrame implements Http2PriorityFrame
{
    private final int streamDependency;
    private final short weight;
    private final boolean exclusive;
    
    public DefaultHttp2PriorityFrame(final int streamDependency, final short weight, final boolean exclusive) {
        this.streamDependency = streamDependency;
        this.weight = weight;
        this.exclusive = exclusive;
    }
    
    @Override
    public int streamDependency() {
        return this.streamDependency;
    }
    
    @Override
    public short weight() {
        return this.weight;
    }
    
    @Override
    public boolean exclusive() {
        return this.exclusive;
    }
    
    @Override
    public DefaultHttp2PriorityFrame stream(final Http2FrameStream stream) {
        super.stream(stream);
        return this;
    }
    
    @Override
    public String name() {
        return "PRIORITY_FRAME";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DefaultHttp2PriorityFrame)) {
            return false;
        }
        final DefaultHttp2PriorityFrame other = (DefaultHttp2PriorityFrame)o;
        final boolean same = super.equals(other);
        return same && this.streamDependency == other.streamDependency && this.weight == other.weight && this.exclusive == other.exclusive;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.streamDependency;
        hash = hash * 31 + this.weight;
        hash = hash * 31 + (this.exclusive ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return "DefaultHttp2PriorityFrame(stream=" + this.stream() + ", streamDependency=" + this.streamDependency + ", weight=" + this.weight + ", exclusive=" + this.exclusive + ')';
    }
}
