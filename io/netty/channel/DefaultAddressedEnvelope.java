package io.netty.channel;

import io.netty.util.internal.StringUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope<M, A extends SocketAddress> implements AddressedEnvelope<M, A>
{
    private final M message;
    private final A sender;
    private final A recipient;
    
    public DefaultAddressedEnvelope(final M message, final A recipient, final A sender) {
        ObjectUtil.checkNotNull(message, "message");
        if (recipient == null && sender == null) {
            throw new NullPointerException("recipient and sender");
        }
        this.message = message;
        this.sender = sender;
        this.recipient = recipient;
    }
    
    public DefaultAddressedEnvelope(final M message, final A recipient) {
        this(message, recipient, null);
    }
    
    @Override
    public M content() {
        return this.message;
    }
    
    @Override
    public A sender() {
        return this.sender;
    }
    
    @Override
    public A recipient() {
        return this.recipient;
    }
    
    @Override
    public int refCnt() {
        if (this.message instanceof ReferenceCounted) {
            return ((ReferenceCounted)this.message).refCnt();
        }
        return 1;
    }
    
    @Override
    public AddressedEnvelope<M, A> retain() {
        ReferenceCountUtil.retain(this.message);
        return this;
    }
    
    @Override
    public AddressedEnvelope<M, A> retain(final int increment) {
        ReferenceCountUtil.retain(this.message, increment);
        return this;
    }
    
    @Override
    public boolean release() {
        return ReferenceCountUtil.release(this.message);
    }
    
    @Override
    public boolean release(final int decrement) {
        return ReferenceCountUtil.release(this.message, decrement);
    }
    
    @Override
    public AddressedEnvelope<M, A> touch() {
        ReferenceCountUtil.touch(this.message);
        return this;
    }
    
    @Override
    public AddressedEnvelope<M, A> touch(final Object hint) {
        ReferenceCountUtil.touch(this.message, hint);
        return this;
    }
    
    @Override
    public String toString() {
        if (this.sender != null) {
            return StringUtil.simpleClassName(this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')';
        }
        return StringUtil.simpleClassName(this) + "(=> " + this.recipient + ", " + this.message + ')';
    }
}
