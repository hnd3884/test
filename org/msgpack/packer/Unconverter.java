package org.msgpack.packer;

import org.msgpack.MessageTypeException;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.type.ValueFactory;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;

public class Unconverter extends AbstractPacker
{
    private PackerStack stack;
    private Object[] values;
    private Value result;
    
    public Unconverter() {
        this(new MessagePack());
    }
    
    public Unconverter(final MessagePack msgpack) {
        super(msgpack);
        this.stack = new PackerStack();
        this.values = new Object[128];
    }
    
    public Value getResult() {
        return this.result;
    }
    
    public void resetResult() {
        this.result = null;
    }
    
    public void writeBoolean(final boolean v) throws IOException {
        this.put(ValueFactory.createBooleanValue(v));
    }
    
    public void writeByte(final byte v) throws IOException {
        this.put(ValueFactory.createIntegerValue(v));
    }
    
    public void writeShort(final short v) throws IOException {
        this.put(ValueFactory.createIntegerValue(v));
    }
    
    public void writeInt(final int v) throws IOException {
        this.put(ValueFactory.createIntegerValue(v));
    }
    
    public void writeBigInteger(final BigInteger v) throws IOException {
        this.put(ValueFactory.createIntegerValue(v));
    }
    
    public void writeLong(final long v) throws IOException {
        this.put(ValueFactory.createIntegerValue(v));
    }
    
    public void writeFloat(final float v) throws IOException {
        this.put(ValueFactory.createFloatValue(v));
    }
    
    public void writeDouble(final double v) throws IOException {
        this.put(ValueFactory.createFloatValue(v));
    }
    
    public void writeByteArray(final byte[] b, final int off, final int len) throws IOException {
        this.put(ValueFactory.createRawValue(b, off, len));
    }
    
    public void writeByteBuffer(final ByteBuffer bb) throws IOException {
        this.put(ValueFactory.createRawValue(bb));
    }
    
    public void writeString(final String s) throws IOException {
        this.put(ValueFactory.createRawValue(s));
    }
    
    @Override
    public Packer writeNil() throws IOException {
        this.put(ValueFactory.createNilValue());
        return this;
    }
    
    @Override
    public Packer writeArrayBegin(final int size) throws IOException {
        if (size == 0) {
            this.putContainer(ValueFactory.createArrayValue());
            this.stack.pushArray(0);
            this.values[this.stack.getDepth()] = null;
        }
        else {
            final Value[] array = new Value[size];
            this.putContainer(ValueFactory.createArrayValue(array, true));
            this.stack.pushArray(size);
            this.values[this.stack.getDepth()] = array;
        }
        return this;
    }
    
    @Override
    public Packer writeArrayEnd(final boolean check) throws IOException {
        if (!this.stack.topIsArray()) {
            throw new MessageTypeException("writeArrayEnd() is called but writeArrayBegin() is not called");
        }
        final int remain = this.stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException("writeArrayEnd(check=true) is called but the array is not end");
            }
            for (int i = 0; i < remain; ++i) {
                this.writeNil();
            }
        }
        this.stack.pop();
        if (this.stack.getDepth() <= 0) {
            this.result = (Value)this.values[0];
        }
        return this;
    }
    
    @Override
    public Packer writeMapBegin(final int size) throws IOException {
        this.stack.checkCount();
        if (size == 0) {
            this.putContainer(ValueFactory.createMapValue());
            this.stack.pushMap(0);
            this.values[this.stack.getDepth()] = null;
        }
        else {
            final Value[] array = new Value[size * 2];
            this.putContainer(ValueFactory.createMapValue(array, true));
            this.stack.pushMap(size);
            this.values[this.stack.getDepth()] = array;
        }
        return this;
    }
    
    @Override
    public Packer writeMapEnd(final boolean check) throws IOException {
        if (!this.stack.topIsMap()) {
            throw new MessageTypeException("writeMapEnd() is called but writeMapBegin() is not called");
        }
        final int remain = this.stack.getTopCount();
        if (remain > 0) {
            if (check) {
                throw new MessageTypeException("writeMapEnd(check=true) is called but the map is not end");
            }
            for (int i = 0; i < remain; ++i) {
                this.writeNil();
            }
        }
        this.stack.pop();
        if (this.stack.getDepth() <= 0) {
            this.result = (Value)this.values[0];
        }
        return this;
    }
    
    @Override
    public Packer write(final Value v) throws IOException {
        this.put(v);
        return this;
    }
    
    private void put(final Value v) {
        if (this.stack.getDepth() <= 0) {
            this.result = v;
        }
        else {
            this.stack.checkCount();
            final Value[] array = (Value[])this.values[this.stack.getDepth()];
            array[array.length - this.stack.getTopCount()] = v;
            this.stack.reduceCount();
        }
    }
    
    private void putContainer(final Value v) {
        if (this.stack.getDepth() <= 0) {
            this.values[0] = v;
        }
        else {
            this.stack.checkCount();
            final Value[] array = (Value[])this.values[this.stack.getDepth()];
            array[array.length - this.stack.getTopCount()] = v;
            this.stack.reduceCount();
        }
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
    }
}
