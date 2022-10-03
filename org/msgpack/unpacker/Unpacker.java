package org.msgpack.unpacker;

import org.msgpack.type.ValueType;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import org.msgpack.template.Template;
import java.io.IOException;
import java.io.Closeable;
import org.msgpack.type.Value;

public interface Unpacker extends Iterable<Value>, Closeable
{
     <T> T read(final Class<T> p0) throws IOException;
    
     <T> T read(final T p0) throws IOException;
    
     <T> T read(final Template<T> p0) throws IOException;
    
     <T> T read(final T p0, final Template<T> p1) throws IOException;
    
    void skip() throws IOException;
    
    int readArrayBegin() throws IOException;
    
    void readArrayEnd(final boolean p0) throws IOException;
    
    void readArrayEnd() throws IOException;
    
    int readMapBegin() throws IOException;
    
    void readMapEnd(final boolean p0) throws IOException;
    
    void readMapEnd() throws IOException;
    
    void readNil() throws IOException;
    
    boolean trySkipNil() throws IOException;
    
    boolean readBoolean() throws IOException;
    
    byte readByte() throws IOException;
    
    short readShort() throws IOException;
    
    int readInt() throws IOException;
    
    long readLong() throws IOException;
    
    BigInteger readBigInteger() throws IOException;
    
    float readFloat() throws IOException;
    
    double readDouble() throws IOException;
    
    byte[] readByteArray() throws IOException;
    
    ByteBuffer readByteBuffer() throws IOException;
    
    String readString() throws IOException;
    
    Value readValue() throws IOException;
    
    ValueType getNextType() throws IOException;
    
    UnpackerIterator iterator();
    
    int getReadByteCount();
    
    void resetReadByteCount();
    
    void setRawSizeLimit(final int p0);
    
    void setArraySizeLimit(final int p0);
    
    void setMapSizeLimit(final int p0);
}
