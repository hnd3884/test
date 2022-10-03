package org.msgpack.unpacker;

import java.io.IOException;
import java.nio.ByteBuffer;

final class SkipAccept extends Accept
{
    @Override
    void acceptBoolean(final boolean v) {
    }
    
    @Override
    void acceptInteger(final byte v) {
    }
    
    @Override
    void acceptInteger(final short v) {
    }
    
    @Override
    void acceptInteger(final int v) {
    }
    
    @Override
    void acceptInteger(final long v) {
    }
    
    @Override
    void acceptUnsignedInteger(final byte v) {
    }
    
    @Override
    void acceptUnsignedInteger(final short v) {
    }
    
    @Override
    void acceptUnsignedInteger(final int v) {
    }
    
    @Override
    void acceptUnsignedInteger(final long v) {
    }
    
    @Override
    void acceptRaw(final byte[] raw) {
    }
    
    @Override
    void acceptEmptyRaw() {
    }
    
    @Override
    public void refer(final ByteBuffer bb, final boolean gift) throws IOException {
    }
    
    @Override
    void acceptArray(final int size) {
    }
    
    @Override
    void acceptMap(final int size) {
    }
    
    @Override
    void acceptNil() {
    }
    
    @Override
    void acceptFloat(final float v) {
    }
    
    @Override
    void acceptDouble(final double v) {
    }
}
