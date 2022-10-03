package org.msgpack.unpacker;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.msgpack.MessageTypeException;
import org.msgpack.io.BufferReferer;

abstract class Accept implements BufferReferer
{
    void acceptBoolean(final boolean v) throws IOException {
        throw new MessageTypeException("Unexpected boolean value");
    }
    
    void acceptInteger(final byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptInteger(final short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptInteger(final int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptInteger(final long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptUnsignedInteger(final byte v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptUnsignedInteger(final short v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptUnsignedInteger(final int v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptUnsignedInteger(final long v) throws IOException {
        throw new MessageTypeException("Unexpected integer value");
    }
    
    void acceptRaw(final byte[] raw) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }
    
    void acceptEmptyRaw() throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }
    
    void acceptArray(final int size) throws IOException {
        throw new MessageTypeException("Unexpected array value");
    }
    
    void acceptMap(final int size) throws IOException {
        throw new MessageTypeException("Unexpected map value");
    }
    
    void acceptNil() throws IOException {
        throw new MessageTypeException("Unexpected nil value");
    }
    
    void acceptFloat(final float v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }
    
    void acceptDouble(final double v) throws IOException {
        throw new MessageTypeException("Unexpected float value");
    }
    
    @Override
    public void refer(final ByteBuffer bb, final boolean gift) throws IOException {
        throw new MessageTypeException("Unexpected raw value");
    }
}
