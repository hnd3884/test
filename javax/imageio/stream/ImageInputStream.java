package javax.imageio.stream;

import java.io.IOException;
import java.nio.ByteOrder;
import java.io.Closeable;
import java.io.DataInput;

public interface ImageInputStream extends DataInput, Closeable
{
    void setByteOrder(final ByteOrder p0);
    
    ByteOrder getByteOrder();
    
    int read() throws IOException;
    
    int read(final byte[] p0) throws IOException;
    
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void readBytes(final IIOByteBuffer p0, final int p1) throws IOException;
    
    boolean readBoolean() throws IOException;
    
    byte readByte() throws IOException;
    
    int readUnsignedByte() throws IOException;
    
    short readShort() throws IOException;
    
    int readUnsignedShort() throws IOException;
    
    char readChar() throws IOException;
    
    int readInt() throws IOException;
    
    long readUnsignedInt() throws IOException;
    
    long readLong() throws IOException;
    
    float readFloat() throws IOException;
    
    double readDouble() throws IOException;
    
    String readLine() throws IOException;
    
    String readUTF() throws IOException;
    
    void readFully(final byte[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final byte[] p0) throws IOException;
    
    void readFully(final short[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final char[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final int[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final long[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final float[] p0, final int p1, final int p2) throws IOException;
    
    void readFully(final double[] p0, final int p1, final int p2) throws IOException;
    
    long getStreamPosition() throws IOException;
    
    int getBitOffset() throws IOException;
    
    void setBitOffset(final int p0) throws IOException;
    
    int readBit() throws IOException;
    
    long readBits(final int p0) throws IOException;
    
    long length() throws IOException;
    
    int skipBytes(final int p0) throws IOException;
    
    long skipBytes(final long p0) throws IOException;
    
    void seek(final long p0) throws IOException;
    
    void mark();
    
    void reset() throws IOException;
    
    void flushBefore(final long p0) throws IOException;
    
    void flush() throws IOException;
    
    long getFlushedPosition();
    
    boolean isCached();
    
    boolean isCachedMemory();
    
    boolean isCachedFile();
    
    void close() throws IOException;
}
