package org.msgpack.unpacker;

import org.msgpack.MessageTypeException;

final class IntAccept extends Accept
{
    int value;
    
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
        if (this.value < -2147483648L || this.value > 2147483647L) {
            throw new MessageTypeException();
        }
        this.value = (int)v;
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
            throw new MessageTypeException();
        }
        this.value = v;
    }
    
    @Override
    void acceptUnsignedInteger(final long v) {
        if (v < 0L || v > 2147483647L) {
            throw new MessageTypeException();
        }
        this.value = (int)v;
    }
}
