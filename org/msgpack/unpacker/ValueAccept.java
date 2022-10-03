package org.msgpack.unpacker;

import java.nio.ByteBuffer;
import java.math.BigInteger;
import org.msgpack.type.Value;
import org.msgpack.type.ValueFactory;
import java.io.IOException;
import org.msgpack.packer.Unconverter;

final class ValueAccept extends Accept
{
    private Unconverter uc;
    
    ValueAccept() {
        this.uc = null;
    }
    
    void setUnconverter(final Unconverter uc) throws IOException {
        this.uc = uc;
    }
    
    @Override
    void acceptBoolean(final boolean v) throws IOException {
        this.uc.write(ValueFactory.createBooleanValue(v));
    }
    
    @Override
    void acceptInteger(final byte v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v));
    }
    
    @Override
    void acceptInteger(final short v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v));
    }
    
    @Override
    void acceptInteger(final int v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v));
    }
    
    @Override
    void acceptInteger(final long v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v));
    }
    
    @Override
    void acceptUnsignedInteger(final byte v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v & 0xFF));
    }
    
    @Override
    void acceptUnsignedInteger(final short v) throws IOException {
        this.uc.write(ValueFactory.createIntegerValue(v & 0xFFFF));
    }
    
    @Override
    void acceptUnsignedInteger(final int v) throws IOException {
        if (v < 0) {
            final long value = (v & Integer.MAX_VALUE) + 2147483648L;
            this.uc.write(ValueFactory.createIntegerValue(value));
        }
        else {
            this.uc.write(ValueFactory.createIntegerValue(v));
        }
    }
    
    @Override
    void acceptUnsignedInteger(final long v) throws IOException {
        if (v < 0L) {
            final BigInteger value = BigInteger.valueOf(v + Long.MAX_VALUE + 1L).setBit(63);
            this.uc.write(ValueFactory.createIntegerValue(value));
        }
        else {
            this.uc.write(ValueFactory.createIntegerValue(v));
        }
    }
    
    @Override
    void acceptRaw(final byte[] raw) throws IOException {
        this.uc.write(ValueFactory.createRawValue(raw));
    }
    
    @Override
    void acceptEmptyRaw() throws IOException {
        this.uc.write(ValueFactory.createRawValue());
    }
    
    @Override
    public void refer(final ByteBuffer bb, final boolean gift) throws IOException {
        final byte[] raw = new byte[bb.remaining()];
        bb.get(raw);
        this.uc.write(ValueFactory.createRawValue(raw, true));
    }
    
    @Override
    void acceptArray(final int size) throws IOException {
        this.uc.writeArrayBegin(size);
    }
    
    @Override
    void acceptMap(final int size) throws IOException {
        this.uc.writeMapBegin(size);
    }
    
    @Override
    void acceptNil() throws IOException {
        this.uc.write(ValueFactory.createNilValue());
    }
    
    @Override
    void acceptFloat(final float v) throws IOException {
        this.uc.write(ValueFactory.createFloatValue(v));
    }
    
    @Override
    void acceptDouble(final double v) throws IOException {
        this.uc.write(ValueFactory.createFloatValue(v));
    }
}
