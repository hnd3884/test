package io.netty.handler.codec.memcache.binary;

import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.memcache.MemcacheContent;
import io.netty.handler.codec.memcache.LastMemcacheContent;
import io.netty.handler.codec.memcache.FullMemcacheMessage;
import io.netty.util.ReferenceCounted;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.ByteBuf;

public class DefaultFullBinaryMemcacheResponse extends DefaultBinaryMemcacheResponse implements FullBinaryMemcacheResponse
{
    private final ByteBuf content;
    
    public DefaultFullBinaryMemcacheResponse(final ByteBuf key, final ByteBuf extras) {
        this(key, extras, Unpooled.buffer(0));
    }
    
    public DefaultFullBinaryMemcacheResponse(final ByteBuf key, final ByteBuf extras, final ByteBuf content) {
        super(key, extras);
        this.content = ObjectUtil.checkNotNull(content, "content");
        this.setTotalBodyLength(this.keyLength() + this.extrasLength() + content.readableBytes());
    }
    
    @Override
    public ByteBuf content() {
        return this.content;
    }
    
    @Override
    public FullBinaryMemcacheResponse retain() {
        super.retain();
        return this;
    }
    
    @Override
    public FullBinaryMemcacheResponse retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public FullBinaryMemcacheResponse touch() {
        super.touch();
        return this;
    }
    
    @Override
    public FullBinaryMemcacheResponse touch(final Object hint) {
        super.touch(hint);
        this.content.touch(hint);
        return this;
    }
    
    @Override
    protected void deallocate() {
        super.deallocate();
        this.content.release();
    }
    
    @Override
    public FullBinaryMemcacheResponse copy() {
        ByteBuf key = this.key();
        if (key != null) {
            key = key.copy();
        }
        ByteBuf extras = this.extras();
        if (extras != null) {
            extras = extras.copy();
        }
        return this.newInstance(key, extras, this.content().copy());
    }
    
    @Override
    public FullBinaryMemcacheResponse duplicate() {
        ByteBuf key = this.key();
        if (key != null) {
            key = key.duplicate();
        }
        ByteBuf extras = this.extras();
        if (extras != null) {
            extras = extras.duplicate();
        }
        return this.newInstance(key, extras, this.content().duplicate());
    }
    
    @Override
    public FullBinaryMemcacheResponse retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }
    
    @Override
    public FullBinaryMemcacheResponse replace(final ByteBuf content) {
        ByteBuf key = this.key();
        if (key != null) {
            key = key.retainedDuplicate();
        }
        ByteBuf extras = this.extras();
        if (extras != null) {
            extras = extras.retainedDuplicate();
        }
        return this.newInstance(key, extras, content);
    }
    
    private FullBinaryMemcacheResponse newInstance(final ByteBuf key, final ByteBuf extras, final ByteBuf content) {
        final DefaultFullBinaryMemcacheResponse newInstance = new DefaultFullBinaryMemcacheResponse(key, extras, content);
        this.copyMeta(newInstance);
        return newInstance;
    }
}
