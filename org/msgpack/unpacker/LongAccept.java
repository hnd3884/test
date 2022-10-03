package org.msgpack.unpacker;

import org.msgpack.MessageTypeException;

final class LongAccept extends Accept
{
    long value;
    
    @Override
    void acceptInteger(final byte v) {
        this.value = v;
    }
    
    @Override
    void acceptInteger(final short v) {
        this.value = v;
    }
    
    @Override
    void acceptInteger(final int v) {
        this.value = v;
    }
    
    @Override
    void acceptInteger(final long v) {
        this.value = v;
    }
    
    @Override
    void acceptUnsignedInteger(final byte v) {
        this.value = (v & 0xFF);
    }
    
    @Override
    void acceptUnsignedInteger(final short v) {
        this.value = (v & 0xFFFF);
    }
    
    @Override
    void acceptUnsignedInteger(final int v) {
        if (v < 0) {
            this.value = (v & Integer.MAX_VALUE) + 2147483648L;
        }
        else {
            this.value = v;
        }
    }
    
    @Override
    void acceptUnsignedInteger(final long v) {
        if (v < 0L) {
            throw new MessageTypeException();
        }
        this.value = v;
    }
}
