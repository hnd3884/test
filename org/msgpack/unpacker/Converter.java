package org.msgpack.unpacker;

import org.msgpack.type.ValueType;
import org.msgpack.packer.Unconverter;
import org.msgpack.type.MapValue;
import org.msgpack.type.ArrayValue;
import java.math.BigInteger;
import org.msgpack.MessageTypeException;
import java.io.IOException;
import java.io.EOFException;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

public class Converter extends AbstractUnpacker
{
    private final UnpackerStack stack;
    private Object[] values;
    protected Value value;
    
    public Converter(final Value value) {
        this(new MessagePack(), value);
    }
    
    public Converter(final MessagePack msgpack, final Value value) {
        super(msgpack);
        this.stack = new UnpackerStack();
        this.values = new Object[128];
        this.value = value;
    }
    
    protected Value nextValue() throws IOException {
        throw new EOFException();
    }
    
    private void ensureValue() throws IOException {
        if (this.value == null) {
            this.value = this.nextValue();
        }
    }
    
    public boolean tryReadNil() throws IOException {
        this.stack.checkCount();
        if (this.getTop().isNilValue()) {
            this.stack.reduceCount();
            if (this.stack.getDepth() == 0) {
                this.value = null;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean trySkipNil() throws IOException {
        this.ensureValue();
        if (this.stack.getDepth() > 0 && this.stack.getTopCount() <= 0) {
            return true;
        }
        if (this.getTop().isNilValue()) {
            this.stack.reduceCount();
            if (this.stack.getDepth() == 0) {
                this.value = null;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void readNil() throws IOException {
        if (!this.getTop().isNilValue()) {
            throw new MessageTypeException("Expected nil but got not nil value");
        }
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        final boolean v = this.getTop().asBooleanValue().getBoolean();
        this.stack.reduceCount();
        return v;
    }
    
    @Override
    public byte readByte() throws IOException {
        final byte v = this.getTop().asIntegerValue().getByte();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public short readShort() throws IOException {
        final short v = this.getTop().asIntegerValue().getShort();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public int readInt() throws IOException {
        final int v = this.getTop().asIntegerValue().getInt();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public long readLong() throws IOException {
        final long v = this.getTop().asIntegerValue().getLong();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public BigInteger readBigInteger() throws IOException {
        final BigInteger v = this.getTop().asIntegerValue().getBigInteger();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public float readFloat() throws IOException {
        final float v = this.getTop().asFloatValue().getFloat();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public double readDouble() throws IOException {
        final double v = this.getTop().asFloatValue().getDouble();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return v;
    }
    
    @Override
    public byte[] readByteArray() throws IOException {
        final byte[] raw = this.getTop().asRawValue().getByteArray();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return raw;
    }
    
    @Override
    public String readString() throws IOException {
        final String str = this.getTop().asRawValue().getString();
        this.stack.reduceCount();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
        return str;
    }
    
    @Override
    public int readArrayBegin() throws IOException {
        final Value v = this.getTop();
        if (!v.isArrayValue()) {
            throw new MessageTypeException("Expected array but got not array value");
        }
        final ArrayValue a = v.asArrayValue();
        this.stack.reduceCount();
        this.stack.pushArray(a.size());
        this.values[this.stack.getDepth()] = a.getElementArray();
        return a.size();
    }
    
    @Override
    public void readArrayEnd(final boolean check) throws IOException {
        if (!this.stack.topIsArray()) {
            throw new MessageTypeException("readArrayEnd() is called but readArrayBegin() is not called");
        }
        final int remain = this.stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException("readArrayEnd(check=true) is called but the array is not end");
            }
            for (int i = 0; i < remain; ++i) {
                this.skip();
            }
        }
        this.stack.pop();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
    }
    
    @Override
    public int readMapBegin() throws IOException {
        final Value v = this.getTop();
        if (!v.isMapValue()) {
            throw new MessageTypeException("Expected map but got not map value");
        }
        final MapValue m = v.asMapValue();
        this.stack.reduceCount();
        this.stack.pushMap(m.size());
        this.values[this.stack.getDepth()] = m.getKeyValueArray();
        return m.size();
    }
    
    @Override
    public void readMapEnd(final boolean check) throws IOException {
        if (!this.stack.topIsMap()) {
            throw new MessageTypeException("readMapEnd() is called but readMapBegin() is not called");
        }
        final int remain = this.stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException("readMapEnd(check=true) is called but the map is not end");
            }
            for (int i = 0; i < remain; ++i) {
                this.skip();
            }
        }
        this.stack.pop();
        if (this.stack.getDepth() == 0) {
            this.value = null;
        }
    }
    
    private Value getTop() throws IOException {
        this.ensureValue();
        this.stack.checkCount();
        if (this.stack.getDepth() == 0) {
            return this.value;
        }
        final Value[] array = (Value[])this.values[this.stack.getDepth()];
        return array[array.length - this.stack.getTopCount()];
    }
    
    @Override
    public Value readValue() throws IOException {
        if (this.stack.getDepth() != 0) {
            return super.readValue();
        }
        if (this.value == null) {
            return this.nextValue();
        }
        final Value v = this.value;
        this.value = null;
        return v;
    }
    
    @Override
    protected void readValue(final Unconverter uc) throws IOException {
        if (uc.getResult() != null) {
            uc.resetResult();
        }
        this.stack.checkCount();
        Value v = this.getTop();
        if (!v.isArrayValue() && !v.isMapValue()) {
            uc.write(v);
            this.stack.reduceCount();
            if (this.stack.getDepth() == 0) {
                this.value = null;
            }
            if (uc.getResult() != null) {
                return;
            }
        }
        while (true) {
            if (this.stack.getDepth() != 0 && this.stack.getTopCount() == 0) {
                if (this.stack.topIsArray()) {
                    uc.writeArrayEnd(true);
                    this.stack.pop();
                }
                else {
                    if (!this.stack.topIsMap()) {
                        throw new RuntimeException("invalid stack");
                    }
                    uc.writeMapEnd(true);
                    this.stack.pop();
                }
                if (this.stack.getDepth() == 0) {
                    this.value = null;
                }
                if (uc.getResult() != null) {
                    return;
                }
                continue;
            }
            else {
                this.stack.checkCount();
                v = this.getTop();
                if (v.isArrayValue()) {
                    final ArrayValue a = v.asArrayValue();
                    uc.writeArrayBegin(a.size());
                    this.stack.reduceCount();
                    this.stack.pushArray(a.size());
                    this.values[this.stack.getDepth()] = a.getElementArray();
                }
                else if (v.isMapValue()) {
                    final MapValue m = v.asMapValue();
                    uc.writeMapBegin(m.size());
                    this.stack.reduceCount();
                    this.stack.pushMap(m.size());
                    this.values[this.stack.getDepth()] = m.getKeyValueArray();
                }
                else {
                    uc.write(v);
                    this.stack.reduceCount();
                }
            }
        }
    }
    
    @Override
    public void skip() throws IOException {
        this.stack.checkCount();
        Value v = this.getTop();
        if (!v.isArrayValue() && !v.isMapValue()) {
            this.stack.reduceCount();
            if (this.stack.getDepth() == 0) {
                this.value = null;
            }
            return;
        }
        final int targetDepth = this.stack.getDepth();
        while (true) {
            if (this.stack.getTopCount() == 0) {
                this.stack.pop();
                if (this.stack.getDepth() == 0) {
                    this.value = null;
                }
                if (this.stack.getDepth() <= targetDepth) {
                    break;
                }
                continue;
            }
            else {
                this.stack.checkCount();
                v = this.getTop();
                if (v.isArrayValue()) {
                    final ArrayValue a = v.asArrayValue();
                    this.stack.reduceCount();
                    this.stack.pushArray(a.size());
                    this.values[this.stack.getDepth()] = a.getElementArray();
                }
                else if (v.isMapValue()) {
                    final MapValue m = v.asMapValue();
                    this.stack.reduceCount();
                    this.stack.pushMap(m.size());
                    this.values[this.stack.getDepth()] = m.getKeyValueArray();
                }
                else {
                    this.stack.reduceCount();
                }
            }
        }
    }
    
    @Override
    public ValueType getNextType() throws IOException {
        return this.getTop().getType();
    }
    
    public void reset() {
        this.stack.clear();
        this.value = null;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public int getReadByteCount() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void setRawSizeLimit(final int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void setArraySizeLimit(final int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void setMapSizeLimit(final int size) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
