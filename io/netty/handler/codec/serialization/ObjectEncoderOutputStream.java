package io.netty.handler.codec.serialization;

import java.io.IOException;
import java.io.ObjectOutputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;
import java.io.DataOutputStream;
import java.io.ObjectOutput;
import java.io.OutputStream;

public class ObjectEncoderOutputStream extends OutputStream implements ObjectOutput
{
    private final DataOutputStream out;
    private final int estimatedLength;
    
    public ObjectEncoderOutputStream(final OutputStream out) {
        this(out, 512);
    }
    
    public ObjectEncoderOutputStream(final OutputStream out, final int estimatedLength) {
        ObjectUtil.checkNotNull(out, "out");
        ObjectUtil.checkPositiveOrZero(estimatedLength, "estimatedLength");
        if (out instanceof DataOutputStream) {
            this.out = (DataOutputStream)out;
        }
        else {
            this.out = new DataOutputStream(out);
        }
        this.estimatedLength = estimatedLength;
    }
    
    @Override
    public void writeObject(final Object obj) throws IOException {
        final ByteBuf buf = Unpooled.buffer(this.estimatedLength);
        try {
            final ObjectOutputStream oout = new CompactObjectOutputStream(new ByteBufOutputStream(buf));
            try {
                oout.writeObject(obj);
                oout.flush();
            }
            finally {
                oout.close();
            }
            final int objectSize = buf.readableBytes();
            this.writeInt(objectSize);
            buf.getBytes(0, this, objectSize);
        }
        finally {
            buf.release();
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    public final int size() {
        return this.out.size();
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public final void writeBoolean(final boolean v) throws IOException {
        this.out.writeBoolean(v);
    }
    
    @Override
    public final void writeByte(final int v) throws IOException {
        this.out.writeByte(v);
    }
    
    @Override
    public final void writeBytes(final String s) throws IOException {
        this.out.writeBytes(s);
    }
    
    @Override
    public final void writeChar(final int v) throws IOException {
        this.out.writeChar(v);
    }
    
    @Override
    public final void writeChars(final String s) throws IOException {
        this.out.writeChars(s);
    }
    
    @Override
    public final void writeDouble(final double v) throws IOException {
        this.out.writeDouble(v);
    }
    
    @Override
    public final void writeFloat(final float v) throws IOException {
        this.out.writeFloat(v);
    }
    
    @Override
    public final void writeInt(final int v) throws IOException {
        this.out.writeInt(v);
    }
    
    @Override
    public final void writeLong(final long v) throws IOException {
        this.out.writeLong(v);
    }
    
    @Override
    public final void writeShort(final int v) throws IOException {
        this.out.writeShort(v);
    }
    
    @Override
    public final void writeUTF(final String str) throws IOException {
        this.out.writeUTF(str);
    }
}
