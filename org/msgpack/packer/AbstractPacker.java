package org.msgpack.packer;

import org.msgpack.type.Value;
import org.msgpack.template.Template;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.MessagePack;

public abstract class AbstractPacker implements Packer
{
    protected MessagePack msgpack;
    
    protected AbstractPacker(final MessagePack msgpack) {
        this.msgpack = msgpack;
    }
    
    @Override
    public Packer write(final boolean o) throws IOException {
        this.writeBoolean(o);
        return this;
    }
    
    @Override
    public Packer write(final byte o) throws IOException {
        this.writeByte(o);
        return this;
    }
    
    @Override
    public Packer write(final short o) throws IOException {
        this.writeShort(o);
        return this;
    }
    
    @Override
    public Packer write(final int o) throws IOException {
        this.writeInt(o);
        return this;
    }
    
    @Override
    public Packer write(final long o) throws IOException {
        this.writeLong(o);
        return this;
    }
    
    @Override
    public Packer write(final float o) throws IOException {
        this.writeFloat(o);
        return this;
    }
    
    @Override
    public Packer write(final double o) throws IOException {
        this.writeDouble(o);
        return this;
    }
    
    @Override
    public Packer write(final Boolean o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeBoolean(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Byte o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeByte(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Short o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeShort(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Integer o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeInt(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Long o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeLong(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final BigInteger o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeBigInteger(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Float o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeFloat(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Double o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeDouble(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final byte[] o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeByteArray(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final byte[] o, final int off, final int len) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeByteArray(o, off, len);
        }
        return this;
    }
    
    @Override
    public Packer write(final ByteBuffer o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeByteBuffer(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final String o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            this.writeString(o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Object o) throws IOException {
        if (o == null) {
            this.writeNil();
        }
        else {
            final Template tmpl = this.msgpack.lookup(o.getClass());
            tmpl.write(this, o);
        }
        return this;
    }
    
    @Override
    public Packer write(final Value v) throws IOException {
        if (v == null) {
            this.writeNil();
        }
        else {
            v.writeTo(this);
        }
        return this;
    }
    
    @Override
    public Packer writeArrayEnd() throws IOException {
        this.writeArrayEnd(true);
        return this;
    }
    
    @Override
    public Packer writeMapEnd() throws IOException {
        this.writeMapEnd(true);
        return this;
    }
    
    @Override
    public void close() throws IOException {
    }
    
    protected abstract void writeBoolean(final boolean p0) throws IOException;
    
    protected abstract void writeByte(final byte p0) throws IOException;
    
    protected abstract void writeShort(final short p0) throws IOException;
    
    protected abstract void writeInt(final int p0) throws IOException;
    
    protected abstract void writeLong(final long p0) throws IOException;
    
    protected abstract void writeBigInteger(final BigInteger p0) throws IOException;
    
    protected abstract void writeFloat(final float p0) throws IOException;
    
    protected abstract void writeDouble(final double p0) throws IOException;
    
    protected void writeByteArray(final byte[] b) throws IOException {
        this.writeByteArray(b, 0, b.length);
    }
    
    protected abstract void writeByteArray(final byte[] p0, final int p1, final int p2) throws IOException;
    
    protected abstract void writeByteBuffer(final ByteBuffer p0) throws IOException;
    
    protected abstract void writeString(final String p0) throws IOException;
}
