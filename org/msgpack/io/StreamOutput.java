package org.msgpack.io;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class StreamOutput implements Output
{
    private DataOutputStream out;
    
    public StreamOutput(final OutputStream out) {
        this.out = new DataOutputStream(out);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
    }
    
    @Override
    public void write(final ByteBuffer bb) throws IOException {
        if (bb.hasArray()) {
            final byte[] array = bb.array();
            final int offset = bb.arrayOffset();
            this.out.write(array, offset, bb.remaining());
            bb.position(bb.limit());
        }
        else {
            final byte[] buf = new byte[bb.remaining()];
            bb.get(buf);
            this.out.write(buf);
        }
    }
    
    @Override
    public void writeByte(final byte v) throws IOException {
        this.out.write(v);
    }
    
    @Override
    public void writeShort(final short v) throws IOException {
        this.out.writeShort(v);
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.out.writeInt(v);
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.out.writeLong(v);
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.out.writeFloat(v);
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.out.writeDouble(v);
    }
    
    @Override
    public void writeByteAndByte(final byte b, final byte v) throws IOException {
        this.out.write(b);
        this.out.write(v);
    }
    
    @Override
    public void writeByteAndShort(final byte b, final short v) throws IOException {
        this.out.write(b);
        this.out.writeShort(v);
    }
    
    @Override
    public void writeByteAndInt(final byte b, final int v) throws IOException {
        this.out.write(b);
        this.out.writeInt(v);
    }
    
    @Override
    public void writeByteAndLong(final byte b, final long v) throws IOException {
        this.out.write(b);
        this.out.writeLong(v);
    }
    
    @Override
    public void writeByteAndFloat(final byte b, final float v) throws IOException {
        this.out.write(b);
        this.out.writeFloat(v);
    }
    
    @Override
    public void writeByteAndDouble(final byte b, final double v) throws IOException {
        this.out.write(b);
        this.out.writeDouble(v);
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
