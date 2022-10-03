package org.msgpack.unpacker;

final class DoubleAccept extends Accept
{
    double value;
    
    @Override
    void acceptFloat(final float v) {
        this.value = v;
    }
    
    @Override
    void acceptDouble(final double v) {
        this.value = v;
    }
}
