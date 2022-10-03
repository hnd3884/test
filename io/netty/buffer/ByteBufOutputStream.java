package io.netty.buffer;

import io.netty.util.CharsetUtil;
import java.io.IOException;
import io.netty.util.internal.ObjectUtil;
import java.io.DataOutputStream;
import java.io.DataOutput;
import java.io.OutputStream;

public class ByteBufOutputStream extends OutputStream implements DataOutput
{
    private final ByteBuf buffer;
    private final int startIndex;
    private DataOutputStream utf8out;
    private boolean closed;
    
    public ByteBufOutputStream(final ByteBuf buffer) {
        this.buffer = ObjectUtil.checkNotNull(buffer, "buffer");
        this.startIndex = buffer.writerIndex();
    }
    
    public int writtenBytes() {
        return this.buffer.writerIndex() - this.startIndex;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return;
        }
        this.buffer.writeBytes(b, off, len);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.buffer.writeBytes(b);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.buffer.writeByte(b);
    }
    
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        this.buffer.writeBoolean(v);
    }
    
    @Override
    public void writeByte(final int v) throws IOException {
        this.buffer.writeByte(v);
    }
    
    @Override
    public void writeBytes(final String s) throws IOException {
        this.buffer.writeCharSequence(s, CharsetUtil.US_ASCII);
    }
    
    @Override
    public void writeChar(final int v) throws IOException {
        this.buffer.writeChar(v);
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
        for (int len = s.length(), i = 0; i < len; ++i) {
            this.buffer.writeChar(s.charAt(i));
        }
    }
    
    @Override
    public void writeDouble(final double v) throws IOException {
        this.buffer.writeDouble(v);
    }
    
    @Override
    public void writeFloat(final float v) throws IOException {
        this.buffer.writeFloat(v);
    }
    
    @Override
    public void writeInt(final int v) throws IOException {
        this.buffer.writeInt(v);
    }
    
    @Override
    public void writeLong(final long v) throws IOException {
        this.buffer.writeLong(v);
    }
    
    @Override
    public void writeShort(final int v) throws IOException {
        this.buffer.writeShort((short)v);
    }
    
    @Override
    public void writeUTF(final String s) throws IOException {
        DataOutputStream out = this.utf8out;
        if (out == null) {
            if (this.closed) {
                throw new IOException("The stream is closed");
            }
            out = (this.utf8out = new DataOutputStream(this));
        }
        out.writeUTF(s);
    }
    
    public ByteBuf buffer() {
        return this.buffer;
    }
    
    @Override
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        this.closed = true;
        try {
            super.close();
        }
        finally {
            if (this.utf8out != null) {
                this.utf8out.close();
            }
        }
    }
}
