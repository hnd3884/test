package io.netty.handler.codec.http2;

import io.netty.util.internal.StringUtil;

public class DefaultHttp2PingFrame implements Http2PingFrame
{
    private final long content;
    private final boolean ack;
    
    public DefaultHttp2PingFrame(final long content) {
        this(content, false);
    }
    
    public DefaultHttp2PingFrame(final long content, final boolean ack) {
        this.content = content;
        this.ack = ack;
    }
    
    @Override
    public boolean ack() {
        return this.ack;
    }
    
    @Override
    public String name() {
        return "PING";
    }
    
    @Override
    public long content() {
        return this.content;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Http2PingFrame)) {
            return false;
        }
        final Http2PingFrame other = (Http2PingFrame)o;
        return this.ack == other.ack() && this.content == other.content();
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + (this.ack ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(content=" + this.content + ", ack=" + this.ack + ')';
    }
}
