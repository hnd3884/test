package org.msgpack.io;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.Flushable;
import java.io.Closeable;

public interface Output extends Closeable, Flushable
{
    void write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void write(final ByteBuffer p0) throws IOException;
    
    void writeByte(final byte p0) throws IOException;
    
    void writeShort(final short p0) throws IOException;
    
    void writeInt(final int p0) throws IOException;
    
    void writeLong(final long p0) throws IOException;
    
    void writeFloat(final float p0) throws IOException;
    
    void writeDouble(final double p0) throws IOException;
    
    void writeByteAndByte(final byte p0, final byte p1) throws IOException;
    
    void writeByteAndShort(final byte p0, final short p1) throws IOException;
    
    void writeByteAndInt(final byte p0, final int p1) throws IOException;
    
    void writeByteAndLong(final byte p0, final long p1) throws IOException;
    
    void writeByteAndFloat(final byte p0, final float p1) throws IOException;
    
    void writeByteAndDouble(final byte p0, final double p1) throws IOException;
}
