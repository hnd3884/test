package io.netty.handler.codec.haproxy;

import io.netty.util.ReferenceCounted;
import io.netty.buffer.ByteBufHolder;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;

public class HAProxyTLV extends DefaultByteBufHolder
{
    private final Type type;
    private final byte typeByteValue;
    
    int totalNumBytes() {
        return 3 + this.contentNumBytes();
    }
    
    int contentNumBytes() {
        return this.content().readableBytes();
    }
    
    public HAProxyTLV(final byte typeByteValue, final ByteBuf content) {
        this(Type.typeForByteValue(typeByteValue), typeByteValue, content);
    }
    
    public HAProxyTLV(final Type type, final ByteBuf content) {
        this(type, Type.byteValueForType(type), content);
    }
    
    HAProxyTLV(final Type type, final byte typeByteValue, final ByteBuf content) {
        super(content);
        this.type = ObjectUtil.checkNotNull(type, "type");
        this.typeByteValue = typeByteValue;
    }
    
    public Type type() {
        return this.type;
    }
    
    public byte typeByteValue() {
        return this.typeByteValue;
    }
    
    @Override
    public HAProxyTLV copy() {
        return this.replace(this.content().copy());
    }
    
    @Override
    public HAProxyTLV duplicate() {
        return this.replace(this.content().duplicate());
    }
    
    @Override
    public HAProxyTLV retainedDuplicate() {
        return this.replace(this.content().retainedDuplicate());
    }
    
    @Override
    public HAProxyTLV replace(final ByteBuf content) {
        return new HAProxyTLV(this.type, this.typeByteValue, content);
    }
    
    @Override
    public HAProxyTLV retain() {
        super.retain();
        return this;
    }
    
    @Override
    public HAProxyTLV retain(final int increment) {
        super.retain(increment);
        return this;
    }
    
    @Override
    public HAProxyTLV touch() {
        super.touch();
        return this;
    }
    
    @Override
    public HAProxyTLV touch(final Object hint) {
        super.touch(hint);
        return this;
    }
    
    @Override
    public String toString() {
        return StringUtil.simpleClassName(this) + "(type: " + this.type() + ", typeByteValue: " + this.typeByteValue() + ", content: " + this.contentToString() + ')';
    }
    
    public enum Type
    {
        PP2_TYPE_ALPN, 
        PP2_TYPE_AUTHORITY, 
        PP2_TYPE_SSL, 
        PP2_TYPE_SSL_VERSION, 
        PP2_TYPE_SSL_CN, 
        PP2_TYPE_NETNS, 
        OTHER;
        
        public static Type typeForByteValue(final byte byteValue) {
            switch (byteValue) {
                case 1: {
                    return Type.PP2_TYPE_ALPN;
                }
                case 2: {
                    return Type.PP2_TYPE_AUTHORITY;
                }
                case 32: {
                    return Type.PP2_TYPE_SSL;
                }
                case 33: {
                    return Type.PP2_TYPE_SSL_VERSION;
                }
                case 34: {
                    return Type.PP2_TYPE_SSL_CN;
                }
                case 48: {
                    return Type.PP2_TYPE_NETNS;
                }
                default: {
                    return Type.OTHER;
                }
            }
        }
        
        public static byte byteValueForType(final Type type) {
            switch (type) {
                case PP2_TYPE_ALPN: {
                    return 1;
                }
                case PP2_TYPE_AUTHORITY: {
                    return 2;
                }
                case PP2_TYPE_SSL: {
                    return 32;
                }
                case PP2_TYPE_SSL_VERSION: {
                    return 33;
                }
                case PP2_TYPE_SSL_CN: {
                    return 34;
                }
                case PP2_TYPE_NETNS: {
                    return 48;
                }
                default: {
                    throw new IllegalArgumentException("unknown type: " + type);
                }
            }
        }
    }
}
