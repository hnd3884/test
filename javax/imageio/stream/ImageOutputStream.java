package javax.imageio.stream;

import java.io.IOException;
import java.io.DataOutput;

public interface ImageOutputStream extends ImageInputStream, DataOutput
{
    void write(final int p0) throws IOException;
    
    void write(final byte[] p0) throws IOException;
    
    void write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void writeBoolean(final boolean p0) throws IOException;
    
    void writeByte(final int p0) throws IOException;
    
    void writeShort(final int p0) throws IOException;
    
    void writeChar(final int p0) throws IOException;
    
    void writeInt(final int p0) throws IOException;
    
    void writeLong(final long p0) throws IOException;
    
    void writeFloat(final float p0) throws IOException;
    
    void writeDouble(final double p0) throws IOException;
    
    void writeBytes(final String p0) throws IOException;
    
    void writeChars(final String p0) throws IOException;
    
    void writeUTF(final String p0) throws IOException;
    
    void writeShorts(final short[] p0, final int p1, final int p2) throws IOException;
    
    void writeChars(final char[] p0, final int p1, final int p2) throws IOException;
    
    void writeInts(final int[] p0, final int p1, final int p2) throws IOException;
    
    void writeLongs(final long[] p0, final int p1, final int p2) throws IOException;
    
    void writeFloats(final float[] p0, final int p1, final int p2) throws IOException;
    
    void writeDoubles(final double[] p0, final int p1, final int p2) throws IOException;
    
    void writeBit(final int p0) throws IOException;
    
    void writeBits(final long p0, final int p1) throws IOException;
    
    void flushBefore(final long p0) throws IOException;
}
