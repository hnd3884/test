package io.netty.handler.codec.redis;

import io.netty.buffer.ByteBuf;

public enum RedisMessageType
{
    INLINE_COMMAND((Byte)null, true), 
    SIMPLE_STRING((Byte)43, true), 
    ERROR((Byte)45, true), 
    INTEGER((Byte)58, true), 
    BULK_STRING((Byte)36, false), 
    ARRAY_HEADER((Byte)42, false);
    
    private final Byte value;
    private final boolean inline;
    
    private RedisMessageType(final Byte value, final boolean inline) {
        this.value = value;
        this.inline = inline;
    }
    
    public int length() {
        return (this.value != null) ? 1 : 0;
    }
    
    public boolean isInline() {
        return this.inline;
    }
    
    public static RedisMessageType readFrom(final ByteBuf in, final boolean decodeInlineCommands) {
        final int initialIndex = in.readerIndex();
        final RedisMessageType type = valueOf(in.readByte());
        if (type == RedisMessageType.INLINE_COMMAND) {
            if (!decodeInlineCommands) {
                throw new RedisCodecException("Decoding of inline commands is disabled");
            }
            in.readerIndex(initialIndex);
        }
        return type;
    }
    
    public void writeTo(final ByteBuf out) {
        if (this.value == null) {
            return;
        }
        out.writeByte(this.value);
    }
    
    private static RedisMessageType valueOf(final byte value) {
        switch (value) {
            case 43: {
                return RedisMessageType.SIMPLE_STRING;
            }
            case 45: {
                return RedisMessageType.ERROR;
            }
            case 58: {
                return RedisMessageType.INTEGER;
            }
            case 36: {
                return RedisMessageType.BULK_STRING;
            }
            case 42: {
                return RedisMessageType.ARRAY_HEADER;
            }
            default: {
                return RedisMessageType.INLINE_COMMAND;
            }
        }
    }
}
