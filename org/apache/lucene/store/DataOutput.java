package org.apache.lucene.store;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BitUtil;
import java.io.IOException;

public abstract class DataOutput
{
    private static int COPY_BUFFER_SIZE;
    private byte[] copyBuffer;
    
    public abstract void writeByte(final byte p0) throws IOException;
    
    public void writeBytes(final byte[] b, final int length) throws IOException {
        this.writeBytes(b, 0, length);
    }
    
    public abstract void writeBytes(final byte[] p0, final int p1, final int p2) throws IOException;
    
    public void writeInt(final int i) throws IOException {
        this.writeByte((byte)(i >> 24));
        this.writeByte((byte)(i >> 16));
        this.writeByte((byte)(i >> 8));
        this.writeByte((byte)i);
    }
    
    public void writeShort(final short i) throws IOException {
        this.writeByte((byte)(i >> 8));
        this.writeByte((byte)i);
    }
    
    public final void writeVInt(int i) throws IOException {
        while ((i & 0xFFFFFF80) != 0x0) {
            this.writeByte((byte)((i & 0x7F) | 0x80));
            i >>>= 7;
        }
        this.writeByte((byte)i);
    }
    
    public final void writeZInt(final int i) throws IOException {
        this.writeVInt(BitUtil.zigZagEncode(i));
    }
    
    public void writeLong(final long i) throws IOException {
        this.writeInt((int)(i >> 32));
        this.writeInt((int)i);
    }
    
    public final void writeVLong(final long i) throws IOException {
        if (i < 0L) {
            throw new IllegalArgumentException("cannot write negative vLong (got: " + i + ")");
        }
        this.writeSignedVLong(i);
    }
    
    private void writeSignedVLong(long i) throws IOException {
        while ((i & 0xFFFFFFFFFFFFFF80L) != 0x0L) {
            this.writeByte((byte)((i & 0x7FL) | 0x80L));
            i >>>= 7;
        }
        this.writeByte((byte)i);
    }
    
    public final void writeZLong(final long i) throws IOException {
        this.writeSignedVLong(BitUtil.zigZagEncode(i));
    }
    
    public void writeString(final String s) throws IOException {
        final BytesRef utf8Result = new BytesRef(s);
        this.writeVInt(utf8Result.length);
        this.writeBytes(utf8Result.bytes, utf8Result.offset, utf8Result.length);
    }
    
    public void copyBytes(final DataInput input, final long numBytes) throws IOException {
        assert numBytes >= 0L : "numBytes=" + numBytes;
        long left = numBytes;
        if (this.copyBuffer == null) {
            this.copyBuffer = new byte[DataOutput.COPY_BUFFER_SIZE];
        }
        while (left > 0L) {
            int toCopy;
            if (left > DataOutput.COPY_BUFFER_SIZE) {
                toCopy = DataOutput.COPY_BUFFER_SIZE;
            }
            else {
                toCopy = (int)left;
            }
            input.readBytes(this.copyBuffer, 0, toCopy);
            this.writeBytes(this.copyBuffer, 0, toCopy);
            left -= toCopy;
        }
    }
    
    @Deprecated
    public void writeStringStringMap(final Map<String, String> map) throws IOException {
        if (map == null) {
            this.writeInt(0);
        }
        else {
            this.writeInt(map.size());
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                this.writeString(entry.getKey());
                this.writeString(entry.getValue());
            }
        }
    }
    
    public void writeMapOfStrings(final Map<String, String> map) throws IOException {
        this.writeVInt(map.size());
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            this.writeString(entry.getKey());
            this.writeString(entry.getValue());
        }
    }
    
    @Deprecated
    public void writeStringSet(final Set<String> set) throws IOException {
        if (set == null) {
            this.writeInt(0);
        }
        else {
            this.writeInt(set.size());
            for (final String value : set) {
                this.writeString(value);
            }
        }
    }
    
    public void writeSetOfStrings(final Set<String> set) throws IOException {
        this.writeVInt(set.size());
        for (final String value : set) {
            this.writeString(value);
        }
    }
    
    static {
        DataOutput.COPY_BUFFER_SIZE = 16384;
    }
}
