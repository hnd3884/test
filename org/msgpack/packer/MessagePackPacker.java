package org.msgpack.packer;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import org.msgpack.MessageTypeException;
import java.math.BigInteger;
import java.io.IOException;
import org.msgpack.io.StreamOutput;
import java.io.OutputStream;
import org.msgpack.MessagePack;
import org.msgpack.io.Output;

public class MessagePackPacker extends AbstractPacker
{
    protected final Output out;
    private PackerStack stack;
    
    public MessagePackPacker(final MessagePack msgpack, final OutputStream stream) {
        this(msgpack, new StreamOutput(stream));
    }
    
    protected MessagePackPacker(final MessagePack msgpack, final Output out) {
        super(msgpack);
        this.stack = new PackerStack();
        this.out = out;
    }
    
    @Override
    protected void writeByte(final byte d) throws IOException {
        if (d < -32) {
            this.out.writeByteAndByte((byte)(-48), d);
        }
        else {
            this.out.writeByte(d);
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeShort(final short d) throws IOException {
        if (d < -32) {
            if (d < -128) {
                this.out.writeByteAndShort((byte)(-47), d);
            }
            else {
                this.out.writeByteAndByte((byte)(-48), (byte)d);
            }
        }
        else if (d < 128) {
            this.out.writeByte((byte)d);
        }
        else if (d < 256) {
            this.out.writeByteAndByte((byte)(-52), (byte)d);
        }
        else {
            this.out.writeByteAndShort((byte)(-51), d);
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeInt(final int d) throws IOException {
        if (d < -32) {
            if (d < -32768) {
                this.out.writeByteAndInt((byte)(-46), d);
            }
            else if (d < -128) {
                this.out.writeByteAndShort((byte)(-47), (short)d);
            }
            else {
                this.out.writeByteAndByte((byte)(-48), (byte)d);
            }
        }
        else if (d < 128) {
            this.out.writeByte((byte)d);
        }
        else if (d < 256) {
            this.out.writeByteAndByte((byte)(-52), (byte)d);
        }
        else if (d < 65536) {
            this.out.writeByteAndShort((byte)(-51), (short)d);
        }
        else {
            this.out.writeByteAndInt((byte)(-50), d);
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeLong(final long d) throws IOException {
        if (d < -32L) {
            if (d < -32768L) {
                if (d < -2147483648L) {
                    this.out.writeByteAndLong((byte)(-45), d);
                }
                else {
                    this.out.writeByteAndInt((byte)(-46), (int)d);
                }
            }
            else if (d < -128L) {
                this.out.writeByteAndShort((byte)(-47), (short)d);
            }
            else {
                this.out.writeByteAndByte((byte)(-48), (byte)d);
            }
        }
        else if (d < 128L) {
            this.out.writeByte((byte)d);
        }
        else if (d < 65536L) {
            if (d < 256L) {
                this.out.writeByteAndByte((byte)(-52), (byte)d);
            }
            else {
                this.out.writeByteAndShort((byte)(-51), (short)d);
            }
        }
        else if (d < 4294967296L) {
            this.out.writeByteAndInt((byte)(-50), (int)d);
        }
        else {
            this.out.writeByteAndLong((byte)(-49), d);
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeBigInteger(final BigInteger d) throws IOException {
        if (d.bitLength() <= 63) {
            this.writeLong(d.longValue());
            this.stack.reduceCount();
        }
        else {
            if (d.bitLength() != 64 || d.signum() != 1) {
                throw new MessageTypeException("MessagePack can't serialize BigInteger larger than (2^64)-1");
            }
            this.out.writeByteAndLong((byte)(-49), d.longValue());
            this.stack.reduceCount();
        }
    }
    
    @Override
    protected void writeFloat(final float d) throws IOException {
        this.out.writeByteAndFloat((byte)(-54), d);
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeDouble(final double d) throws IOException {
        this.out.writeByteAndDouble((byte)(-53), d);
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeBoolean(final boolean d) throws IOException {
        if (d) {
            this.out.writeByte((byte)(-61));
        }
        else {
            this.out.writeByte((byte)(-62));
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeByteArray(final byte[] b, final int off, final int len) throws IOException {
        if (len < 32) {
            this.out.writeByte((byte)(0xA0 | len));
        }
        else if (len < 65536) {
            this.out.writeByteAndShort((byte)(-38), (short)len);
        }
        else {
            this.out.writeByteAndInt((byte)(-37), len);
        }
        this.out.write(b, off, len);
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeByteBuffer(final ByteBuffer bb) throws IOException {
        final int len = bb.remaining();
        if (len < 32) {
            this.out.writeByte((byte)(0xA0 | len));
        }
        else if (len < 65536) {
            this.out.writeByteAndShort((byte)(-38), (short)len);
        }
        else {
            this.out.writeByteAndInt((byte)(-37), len);
        }
        final int pos = bb.position();
        try {
            this.out.write(bb);
        }
        finally {
            bb.position(pos);
        }
        this.stack.reduceCount();
    }
    
    @Override
    protected void writeString(final String s) throws IOException {
        byte[] b;
        try {
            b = s.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new MessageTypeException(ex);
        }
        this.writeByteArray(b, 0, b.length);
        this.stack.reduceCount();
    }
    
    @Override
    public Packer writeNil() throws IOException {
        this.out.writeByte((byte)(-64));
        this.stack.reduceCount();
        return this;
    }
    
    @Override
    public Packer writeArrayBegin(final int size) throws IOException {
        if (size < 16) {
            this.out.writeByte((byte)(0x90 | size));
        }
        else if (size < 65536) {
            this.out.writeByteAndShort((byte)(-36), (short)size);
        }
        else {
            this.out.writeByteAndInt((byte)(-35), size);
        }
        this.stack.reduceCount();
        this.stack.pushArray(size);
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
                throw new MessageTypeException("writeArrayEnd(check=true) is called but the array is not end: " + remain);
            }
            for (int i = 0; i < remain; ++i) {
                this.writeNil();
            }
        }
        this.stack.pop();
        return this;
    }
    
    @Override
    public Packer writeMapBegin(final int size) throws IOException {
        if (size < 16) {
            this.out.writeByte((byte)(0x80 | size));
        }
        else if (size < 65536) {
            this.out.writeByteAndShort((byte)(-34), (short)size);
        }
        else {
            this.out.writeByteAndInt((byte)(-33), size);
        }
        this.stack.reduceCount();
        this.stack.pushMap(size);
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
                throw new MessageTypeException("writeMapEnd(check=true) is called but the map is not end: " + remain);
            }
            for (int i = 0; i < remain; ++i) {
                this.writeNil();
            }
        }
        this.stack.pop();
        return this;
    }
    
    public void reset() {
        this.stack.clear();
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
