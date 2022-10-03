package org.msgpack.util.json;

import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.io.IOException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.Charset;
import org.msgpack.io.StreamOutput;
import org.msgpack.MessagePack;
import java.io.OutputStream;
import java.nio.charset.CharsetDecoder;
import org.msgpack.packer.PackerStack;
import org.msgpack.io.Output;
import org.msgpack.packer.AbstractPacker;

public class JSONPacker extends AbstractPacker
{
    private static final byte[] NULL;
    private static final byte[] TRUE;
    private static final byte[] FALSE;
    private static final byte COMMA = 44;
    private static final byte COLON = 58;
    private static final byte QUOTE = 34;
    private static final byte LEFT_BR = 91;
    private static final byte RIGHT_BR = 93;
    private static final byte LEFT_WN = 123;
    private static final byte RIGHT_WN = 125;
    private static final byte BACKSLASH = 92;
    private static final byte ZERO = 48;
    private static final int FLAG_FIRST_ELEMENT = 1;
    private static final int FLAG_MAP_KEY = 2;
    private static final int FLAG_MAP_VALUE = 4;
    protected final Output out;
    private int[] flags;
    private PackerStack stack;
    private CharsetDecoder decoder;
    private static final int[] ESCAPE_TABLE;
    private static final byte[] HEX_TABLE;
    
    public JSONPacker(final OutputStream stream) {
        this(new MessagePack(), stream);
    }
    
    public JSONPacker(final MessagePack msgpack, final OutputStream stream) {
        this(msgpack, new StreamOutput(stream));
    }
    
    protected JSONPacker(final MessagePack msgpack, final Output out) {
        super(msgpack);
        this.stack = new PackerStack();
        this.out = out;
        this.stack = new PackerStack();
        this.flags = new int[128];
        this.decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }
    
    @Override
    protected void writeBoolean(final boolean v) throws IOException {
        this.beginElement();
        if (v) {
            this.out.write(JSONPacker.TRUE, 0, JSONPacker.TRUE.length);
        }
        else {
            this.out.write(JSONPacker.FALSE, 0, JSONPacker.FALSE.length);
        }
        this.endElement();
    }
    
    @Override
    protected void writeByte(final byte v) throws IOException {
        this.beginElement();
        final byte[] b = Byte.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeShort(final short v) throws IOException {
        this.beginElement();
        final byte[] b = Short.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeInt(final int v) throws IOException {
        this.beginElement();
        final byte[] b = Integer.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeLong(final long v) throws IOException {
        this.beginElement();
        final byte[] b = Long.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeBigInteger(final BigInteger v) throws IOException {
        this.beginElement();
        final byte[] b = v.toString().getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeFloat(final float v) throws IOException {
        this.beginElement();
        final Float r = v;
        if (r.isInfinite() || r.isNaN()) {
            throw new IOException("JSONPacker doesn't support NaN and infinite float value");
        }
        final byte[] b = Float.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeDouble(final double v) throws IOException {
        this.beginElement();
        final Double r = v;
        if (r.isInfinite() || r.isNaN()) {
            throw new IOException("JSONPacker doesn't support NaN and infinite float value");
        }
        final byte[] b = Double.toString(v).getBytes();
        this.out.write(b, 0, b.length);
        this.endElement();
    }
    
    @Override
    protected void writeByteArray(final byte[] b, final int off, final int len) throws IOException {
        this.beginStringElement();
        this.out.writeByte((byte)34);
        this.escape(this.out, b, off, len);
        this.out.writeByte((byte)34);
        this.endElement();
    }
    
    @Override
    protected void writeByteBuffer(final ByteBuffer bb) throws IOException {
        this.beginStringElement();
        this.out.writeByte((byte)34);
        final int pos = bb.position();
        try {
            this.escape(this.out, bb);
        }
        finally {
            bb.position(pos);
        }
        this.out.writeByte((byte)34);
        this.endElement();
    }
    
    @Override
    protected void writeString(final String s) throws IOException {
        this.beginStringElement();
        this.out.writeByte((byte)34);
        escape(this.out, s);
        this.out.writeByte((byte)34);
        this.endElement();
    }
    
    @Override
    public Packer writeNil() throws IOException {
        this.beginElement();
        this.out.write(JSONPacker.NULL, 0, JSONPacker.NULL.length);
        this.endElement();
        return this;
    }
    
    @Override
    public Packer writeArrayBegin(final int size) throws IOException {
        this.beginElement();
        this.out.writeByte((byte)91);
        this.endElement();
        this.stack.pushArray(size);
        this.flags[this.stack.getDepth()] = 1;
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
        this.out.writeByte((byte)93);
        return this;
    }
    
    @Override
    public Packer writeMapBegin(final int size) throws IOException {
        this.beginElement();
        this.out.writeByte((byte)123);
        this.endElement();
        this.stack.pushMap(size);
        this.flags[this.stack.getDepth()] = 3;
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
        this.out.writeByte((byte)125);
        return this;
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    public void reset() {
        this.stack.clear();
    }
    
    private void beginElement() throws IOException {
        final int flag = this.flags[this.stack.getDepth()];
        if ((flag & 0x2) != 0x0) {
            throw new IOException("Key of a map must be a string in JSON");
        }
        this.beginStringElement();
    }
    
    private void beginStringElement() throws IOException {
        final int flag = this.flags[this.stack.getDepth()];
        if ((flag & 0x4) != 0x0) {
            this.out.writeByte((byte)58);
        }
        else if (this.stack.getDepth() > 0 && (flag & 0x1) == 0x0) {
            this.out.writeByte((byte)44);
        }
    }
    
    private void endElement() throws IOException {
        int flag = this.flags[this.stack.getDepth()];
        if ((flag & 0x2) != 0x0) {
            flag &= 0xFFFFFFFD;
            flag |= 0x4;
        }
        else if ((flag & 0x4) != 0x0) {
            flag &= 0xFFFFFFFB;
            flag |= 0x2;
        }
        flag &= 0xFFFFFFFE;
        this.flags[this.stack.getDepth()] = flag;
        this.stack.reduceCount();
    }
    
    private void escape(final Output out, final byte[] b, final int off, final int len) throws IOException {
        this.escape(out, ByteBuffer.wrap(b, off, len));
    }
    
    private void escape(final Output out, final ByteBuffer bb) throws IOException {
        final String str = this.decoder.decode(bb).toString();
        escape(out, str);
    }
    
    private static void escape(final Output out, final String s) throws IOException {
        final byte[] tmp = { 92, 117, 0, 0, 0, 0 };
        final char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; ++i) {
            final int ch = chars[i];
            if (ch <= 127) {
                final int e = JSONPacker.ESCAPE_TABLE[ch];
                if (e == 0) {
                    tmp[2] = (byte)ch;
                    out.write(tmp, 2, 1);
                }
                else if (e > 0) {
                    tmp[2] = 92;
                    tmp[3] = (byte)e;
                    out.write(tmp, 2, 2);
                }
                else {
                    tmp[3] = (tmp[2] = 48);
                    tmp[4] = JSONPacker.HEX_TABLE[ch >> 4];
                    tmp[5] = JSONPacker.HEX_TABLE[ch & 0xF];
                    out.write(tmp, 0, 6);
                }
            }
            else if (ch <= 2047) {
                tmp[2] = (byte)(0xC0 | ch >> 6);
                tmp[3] = (byte)(0x80 | (ch & 0x3F));
                out.write(tmp, 2, 2);
            }
            else if (ch >= 55296 && ch <= 57343) {
                tmp[2] = JSONPacker.HEX_TABLE[ch >> 12 & 0xF];
                tmp[3] = JSONPacker.HEX_TABLE[ch >> 8 & 0xF];
                tmp[4] = JSONPacker.HEX_TABLE[ch >> 4 & 0xF];
                tmp[5] = JSONPacker.HEX_TABLE[ch & 0xF];
                out.write(tmp, 0, 6);
            }
            else {
                tmp[2] = (byte)(0xE0 | ch >> 12);
                tmp[3] = (byte)(0x80 | (ch >> 6 & 0x3F));
                tmp[4] = (byte)(0x80 | (ch & 0x3F));
                out.write(tmp, 2, 3);
            }
        }
    }
    
    static {
        NULL = new byte[] { 110, 117, 108, 108 };
        TRUE = new byte[] { 116, 114, 117, 101 };
        FALSE = new byte[] { 102, 97, 108, 115, 101 };
        ESCAPE_TABLE = new int[128];
        for (int i = 0; i < 32; ++i) {
            JSONPacker.ESCAPE_TABLE[i] = -1;
        }
        JSONPacker.ESCAPE_TABLE[34] = 34;
        JSONPacker.ESCAPE_TABLE[92] = 92;
        JSONPacker.ESCAPE_TABLE[8] = 98;
        JSONPacker.ESCAPE_TABLE[9] = 116;
        JSONPacker.ESCAPE_TABLE[12] = 102;
        JSONPacker.ESCAPE_TABLE[10] = 110;
        JSONPacker.ESCAPE_TABLE[13] = 114;
        final char[] hex = "0123456789ABCDEF".toCharArray();
        HEX_TABLE = new byte[hex.length];
        for (int j = 0; j < hex.length; ++j) {
            JSONPacker.HEX_TABLE[j] = (byte)hex[j];
        }
    }
}
