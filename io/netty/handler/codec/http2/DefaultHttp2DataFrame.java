package io.netty.handler.codec.http2;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.internal.StringUtil;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;

public final class DefaultHttp2DataFrame extends AbstractHttp2StreamFrame implements Http2DataFrame
{
    private final ByteBuf content;
    private final boolean endStream;
    private final int padding;
    private final int initialFlowControlledBytes;
    
    public DefaultHttp2DataFrame(final ByteBuf content) {
        this(content, false);
    }
    
    public DefaultHttp2DataFrame(final boolean endStream) {
        this(Unpooled.EMPTY_BUFFER, endStream);
    }
    
    public DefaultHttp2DataFrame(final ByteBuf content, final boolean endStream) {
        this(content, endStream, 0);
    }
    
    public DefaultHttp2DataFrame(final ByteBuf content, final boolean endStream, final int padding) {
        this.content = ObjectUtil.checkNotNull(content, "content");
        this.endStream = endStream;
        Http2CodecUtil.verifyPadding(padding);
        this.padding = padding;
        if (this.content().readableBytes() + (long)padding > 2147483647L) {
            throw new IllegalArgumentException("content + padding must be <= Integer.MAX_VALUE");
        }
        this.initialFlowControlledBytes = this.content().readableBytes() + padding;
    }
    
    @Override
    public DefaultHttp2DataFrame stream(final Http2FrameStream stream) {
        super.stream(stream);
        return this;
    }
    
    @Override
    public String name() {
        return "DATA";
    }
    
    @Override
    public boolean isEndStream() {
        return this.endStream;
    }
    
    @Override
    public int padding() {
        return this.padding;
    }
    
    @Override
    public ByteBuf content() {
        return ByteBufUtil.ensureAccessible(this.content);
    }
    
    @Override
    public int initialFlowControlledBytes() {
        return this.initialFlowControlledBytes;
    }
    
    @Override
    public DefaultHttp2DataFrame copy() {
        return this.replace(this.content().copy());
    }
    
    @Override
    public DefaultHttp2DataFrame duplicate() {
        return this.replace(this.content().duplicate());
    }
    
    @Override
    public DefaultHttp2DataFrame retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }
    
    @Override
    public DefaultHttp2DataFrame replace(final ByteBuf content) {
        return new DefaultHttp2DataFrame(content, this.endStream, this.padding);
    }
    
    @Override
    public int refCnt() {
        return this.content.refCnt();
    }
    
    @Override
    public boolean release() {
        return this.content.release();
    }
    
    @Override
    public boolean release(final int decrement) {
        return this.content.release(decrement);
    }
    
    @Override
    public DefaultHttp2DataFrame retain() {
        this.content.retain();
        return this;
    }
    
    @Override
    public DefaultHttp2DataFrame retain(final int increment) {
        this.content.retain(increment);
        return this;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(stream=" + this.stream() + ", content=" + this.content + ", endStream=" + this.endStream + ", padding=" + this.padding + ')';
    }
    
    @Override
    public DefaultHttp2DataFrame touch() {
        this.content.touch();
        return this;
    }
    
    @Override
    public DefaultHttp2DataFrame touch(final Object hint) {
        this.content.touch(hint);
        return this;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DefaultHttp2DataFrame)) {
            return false;
        }
        final DefaultHttp2DataFrame other = (DefaultHttp2DataFrame)o;
        return super.equals(other) && this.content.equals(other.content()) && this.endStream == other.endStream && this.padding == other.padding;
    }
    
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = hash * 31 + this.content.hashCode();
        hash = hash * 31 + (this.endStream ? 0 : 1);
        hash = hash * 31 + this.padding;
        return hash;
    }
}
