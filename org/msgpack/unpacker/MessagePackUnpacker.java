package org.msgpack.unpacker;

import org.msgpack.type.ValueType;
import org.msgpack.packer.Unconverter;
import java.math.BigInteger;
import org.msgpack.MessageTypeException;
import java.io.EOFException;
import org.msgpack.io.BufferReferer;
import java.io.IOException;
import org.msgpack.io.StreamInput;
import java.io.InputStream;
import org.msgpack.MessagePack;
import org.msgpack.io.Input;

public class MessagePackUnpacker extends AbstractUnpacker
{
    private static final byte REQUIRE_TO_READ_HEAD = -58;
    protected final Input in;
    private final UnpackerStack stack;
    private byte headByte;
    private byte[] raw;
    private int rawFilled;
    private final IntAccept intAccept;
    private final LongAccept longAccept;
    private final BigIntegerAccept bigIntegerAccept;
    private final DoubleAccept doubleAccept;
    private final ByteArrayAccept byteArrayAccept;
    private final StringAccept stringAccept;
    private final ArrayAccept arrayAccept;
    private final MapAccept mapAccept;
    private final ValueAccept valueAccept;
    private final SkipAccept skipAccept;
    
    public MessagePackUnpacker(final MessagePack msgpack, final InputStream stream) {
        this(msgpack, new StreamInput(stream));
    }
    
    protected MessagePackUnpacker(final MessagePack msgpack, final Input in) {
        super(msgpack);
        this.stack = new UnpackerStack();
        this.headByte = -58;
        this.intAccept = new IntAccept();
        this.longAccept = new LongAccept();
        this.bigIntegerAccept = new BigIntegerAccept();
        this.doubleAccept = new DoubleAccept();
        this.byteArrayAccept = new ByteArrayAccept();
        this.stringAccept = new StringAccept();
        this.arrayAccept = new ArrayAccept();
        this.mapAccept = new MapAccept();
        this.valueAccept = new ValueAccept();
        this.skipAccept = new SkipAccept();
        this.in = in;
    }
    
    private byte getHeadByte() throws IOException {
        byte b = this.headByte;
        if (b == -58) {
            final byte byte1 = this.in.readByte();
            this.headByte = byte1;
            b = byte1;
        }
        return b;
    }
    
    final void readOne(final Accept a) throws IOException {
        this.stack.checkCount();
        if (this.readOneWithoutStack(a)) {
            this.stack.reduceCount();
        }
    }
    
    final boolean readOneWithoutStack(final Accept a) throws IOException {
        if (this.raw != null) {
            this.readRawBodyCont();
            a.acceptRaw(this.raw);
            this.raw = null;
            this.headByte = -58;
            return true;
        }
        final int b = this.getHeadByte();
        if ((b & 0x80) == 0x0) {
            a.acceptInteger(b);
            this.headByte = -58;
            return true;
        }
        if ((b & 0xE0) == 0xE0) {
            a.acceptInteger(b);
            this.headByte = -58;
            return true;
        }
        if ((b & 0xE0) == 0xA0) {
            final int count = b & 0x1F;
            if (count == 0) {
                a.acceptEmptyRaw();
                this.headByte = -58;
                return true;
            }
            if (!this.tryReferRawBody(a, count)) {
                this.readRawBody(count);
                a.acceptRaw(this.raw);
                this.raw = null;
            }
            this.headByte = -58;
            return true;
        }
        else {
            if ((b & 0xF0) == 0x90) {
                final int count = b & 0xF;
                a.acceptArray(count);
                this.stack.reduceCount();
                this.stack.pushArray(count);
                this.headByte = -58;
                return false;
            }
            if ((b & 0xF0) == 0x80) {
                final int count = b & 0xF;
                a.acceptMap(count);
                this.stack.reduceCount();
                this.stack.pushMap(count);
                this.headByte = -58;
                return false;
            }
            return this.readOneWithoutStackLarge(a, b);
        }
    }
    
    private boolean readOneWithoutStackLarge(final Accept a, final int b) throws IOException {
        switch (b & 0xFF) {
            case 192: {
                a.acceptNil();
                this.headByte = -58;
                return true;
            }
            case 194: {
                a.acceptBoolean(false);
                this.headByte = -58;
                return true;
            }
            case 195: {
                a.acceptBoolean(true);
                this.headByte = -58;
                return true;
            }
            case 202: {
                a.acceptFloat(this.in.getFloat());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 203: {
                a.acceptDouble(this.in.getDouble());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 204: {
                a.acceptUnsignedInteger(this.in.getByte());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 205: {
                a.acceptUnsignedInteger(this.in.getShort());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 206: {
                a.acceptUnsignedInteger(this.in.getInt());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 207: {
                a.acceptUnsignedInteger(this.in.getLong());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 208: {
                a.acceptInteger(this.in.getByte());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 209: {
                a.acceptInteger(this.in.getShort());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 210: {
                a.acceptInteger(this.in.getInt());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 211: {
                a.acceptInteger(this.in.getLong());
                this.in.advance();
                this.headByte = -58;
                return true;
            }
            case 218: {
                final int count = this.in.getShort() & 0xFFFF;
                if (count == 0) {
                    a.acceptEmptyRaw();
                    this.in.advance();
                    this.headByte = -58;
                    return true;
                }
                if (count >= this.rawSizeLimit) {
                    final String reason = String.format("Size of raw (%d) over limit at %d", count, this.rawSizeLimit);
                    throw new SizeLimitException(reason);
                }
                this.in.advance();
                if (!this.tryReferRawBody(a, count)) {
                    this.readRawBody(count);
                    a.acceptRaw(this.raw);
                    this.raw = null;
                }
                this.headByte = -58;
                return true;
            }
            case 219: {
                final int count = this.in.getInt();
                if (count == 0) {
                    a.acceptEmptyRaw();
                    this.in.advance();
                    this.headByte = -58;
                    return true;
                }
                if (count < 0 || count >= this.rawSizeLimit) {
                    final String reason = String.format("Size of raw (%d) over limit at %d", count, this.rawSizeLimit);
                    throw new SizeLimitException(reason);
                }
                this.in.advance();
                if (!this.tryReferRawBody(a, count)) {
                    this.readRawBody(count);
                    a.acceptRaw(this.raw);
                    this.raw = null;
                }
                this.headByte = -58;
                return true;
            }
            case 220: {
                final int count = this.in.getShort() & 0xFFFF;
                if (count >= this.arraySizeLimit) {
                    final String reason = String.format("Size of array (%d) over limit at %d", count, this.arraySizeLimit);
                    throw new SizeLimitException(reason);
                }
                a.acceptArray(count);
                this.stack.reduceCount();
                this.stack.pushArray(count);
                this.in.advance();
                this.headByte = -58;
                return false;
            }
            case 221: {
                final int count = this.in.getInt();
                if (count < 0 || count >= this.arraySizeLimit) {
                    final String reason = String.format("Size of array (%d) over limit at %d", count, this.arraySizeLimit);
                    throw new SizeLimitException(reason);
                }
                a.acceptArray(count);
                this.stack.reduceCount();
                this.stack.pushArray(count);
                this.in.advance();
                this.headByte = -58;
                return false;
            }
            case 222: {
                final int count = this.in.getShort() & 0xFFFF;
                if (count >= this.mapSizeLimit) {
                    final String reason = String.format("Size of map (%d) over limit at %d", count, this.mapSizeLimit);
                    throw new SizeLimitException(reason);
                }
                a.acceptMap(count);
                this.stack.reduceCount();
                this.stack.pushMap(count);
                this.in.advance();
                this.headByte = -58;
                return false;
            }
            case 223: {
                final int count = this.in.getInt();
                if (count < 0 || count >= this.mapSizeLimit) {
                    final String reason = String.format("Size of map (%d) over limit at %d", count, this.mapSizeLimit);
                    throw new SizeLimitException(reason);
                }
                a.acceptMap(count);
                this.stack.reduceCount();
                this.stack.pushMap(count);
                this.in.advance();
                this.headByte = -58;
                return false;
            }
            default: {
                this.headByte = -58;
                throw new IOException("Invalid byte: " + b);
            }
        }
    }
    
    private boolean tryReferRawBody(final BufferReferer referer, final int size) throws IOException {
        return this.in.tryRefer(referer, size);
    }
    
    private void readRawBody(final int size) throws IOException {
        this.raw = new byte[size];
        this.rawFilled = 0;
        this.readRawBodyCont();
    }
    
    private void readRawBodyCont() throws IOException {
        final int len = this.in.read(this.raw, this.rawFilled, this.raw.length - this.rawFilled);
        this.rawFilled += len;
        if (this.rawFilled < this.raw.length) {
            throw new EOFException();
        }
    }
    
    @Override
    protected boolean tryReadNil() throws IOException {
        this.stack.checkCount();
        final int b = this.getHeadByte() & 0xFF;
        if (b == 192) {
            this.stack.reduceCount();
            this.headByte = -58;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean trySkipNil() throws IOException {
        if (this.stack.getDepth() > 0 && this.stack.getTopCount() <= 0) {
            return true;
        }
        final int b = this.getHeadByte() & 0xFF;
        if (b == 192) {
            this.stack.reduceCount();
            this.headByte = -58;
            return true;
        }
        return false;
    }
    
    @Override
    public void readNil() throws IOException {
        this.stack.checkCount();
        final int b = this.getHeadByte() & 0xFF;
        if (b == 192) {
            this.stack.reduceCount();
            this.headByte = -58;
            return;
        }
        throw new MessageTypeException("Expected nil but got not nil value");
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        this.stack.checkCount();
        final int b = this.getHeadByte() & 0xFF;
        if (b == 194) {
            this.stack.reduceCount();
            this.headByte = -58;
            return false;
        }
        if (b == 195) {
            this.stack.reduceCount();
            this.headByte = -58;
            return true;
        }
        throw new MessageTypeException("Expected Boolean but got not boolean value");
    }
    
    @Override
    public byte readByte() throws IOException {
        this.stack.checkCount();
        this.readOneWithoutStack(this.intAccept);
        final int value = this.intAccept.value;
        if (value < -128 || value > 127) {
            throw new MessageTypeException();
        }
        this.stack.reduceCount();
        return (byte)value;
    }
    
    @Override
    public short readShort() throws IOException {
        this.stack.checkCount();
        this.readOneWithoutStack(this.intAccept);
        final int value = this.intAccept.value;
        if (value < -32768 || value > 32767) {
            throw new MessageTypeException();
        }
        this.stack.reduceCount();
        return (short)value;
    }
    
    @Override
    public int readInt() throws IOException {
        this.readOne(this.intAccept);
        return this.intAccept.value;
    }
    
    @Override
    public long readLong() throws IOException {
        this.readOne(this.longAccept);
        return this.longAccept.value;
    }
    
    @Override
    public BigInteger readBigInteger() throws IOException {
        this.readOne(this.bigIntegerAccept);
        return this.bigIntegerAccept.value;
    }
    
    @Override
    public float readFloat() throws IOException {
        this.readOne(this.doubleAccept);
        return (float)this.doubleAccept.value;
    }
    
    @Override
    public double readDouble() throws IOException {
        this.readOne(this.doubleAccept);
        return this.doubleAccept.value;
    }
    
    @Override
    public byte[] readByteArray() throws IOException {
        this.readOne(this.byteArrayAccept);
        return this.byteArrayAccept.value;
    }
    
    @Override
    public String readString() throws IOException {
        this.readOne(this.stringAccept);
        return this.stringAccept.value;
    }
    
    @Override
    public int readArrayBegin() throws IOException {
        this.readOne(this.arrayAccept);
        return this.arrayAccept.size;
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
    }
    
    @Override
    public int readMapBegin() throws IOException {
        this.readOne(this.mapAccept);
        return this.mapAccept.size;
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
    }
    
    @Override
    protected void readValue(final Unconverter uc) throws IOException {
        if (uc.getResult() != null) {
            uc.resetResult();
        }
        this.valueAccept.setUnconverter(uc);
        this.stack.checkCount();
        if (this.readOneWithoutStack(this.valueAccept)) {
            this.stack.reduceCount();
            if (uc.getResult() != null) {
                return;
            }
        }
        while (true) {
            if (this.stack.getTopCount() == 0) {
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
                if (uc.getResult() != null) {
                    return;
                }
                continue;
            }
            else {
                this.readOne(this.valueAccept);
            }
        }
    }
    
    @Override
    public void skip() throws IOException {
        this.stack.checkCount();
        if (this.readOneWithoutStack(this.skipAccept)) {
            this.stack.reduceCount();
            return;
        }
        final int targetDepth = this.stack.getDepth() - 1;
        while (true) {
            if (this.stack.getTopCount() == 0) {
                this.stack.pop();
                if (this.stack.getDepth() <= targetDepth) {
                    break;
                }
                continue;
            }
            else {
                this.readOne(this.skipAccept);
            }
        }
    }
    
    @Override
    public ValueType getNextType() throws IOException {
        final int b = this.getHeadByte();
        if ((b & 0x80) == 0x0) {
            return ValueType.INTEGER;
        }
        if ((b & 0xE0) == 0xE0) {
            return ValueType.INTEGER;
        }
        if ((b & 0xE0) == 0xA0) {
            return ValueType.RAW;
        }
        if ((b & 0xF0) == 0x90) {
            return ValueType.ARRAY;
        }
        if ((b & 0xF0) == 0x80) {
            return ValueType.MAP;
        }
        switch (b & 0xFF) {
            case 192: {
                return ValueType.NIL;
            }
            case 194:
            case 195: {
                return ValueType.BOOLEAN;
            }
            case 202:
            case 203: {
                return ValueType.FLOAT;
            }
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211: {
                return ValueType.INTEGER;
            }
            case 218:
            case 219: {
                return ValueType.RAW;
            }
            case 220:
            case 221: {
                return ValueType.ARRAY;
            }
            case 222:
            case 223: {
                return ValueType.MAP;
            }
            default: {
                throw new IOException("Invalid byte: " + b);
            }
        }
    }
    
    public void reset() {
        this.raw = null;
        this.stack.clear();
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public int getReadByteCount() {
        return this.in.getReadByteCount();
    }
    
    @Override
    public void resetReadByteCount() {
        this.in.resetReadByteCount();
    }
}
