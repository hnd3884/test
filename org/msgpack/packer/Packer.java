package org.msgpack.packer;

import org.msgpack.type.Value;
import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.io.IOException;
import java.io.Flushable;
import java.io.Closeable;

public interface Packer extends Closeable, Flushable
{
    Packer write(final boolean p0) throws IOException;
    
    Packer write(final byte p0) throws IOException;
    
    Packer write(final short p0) throws IOException;
    
    Packer write(final int p0) throws IOException;
    
    Packer write(final long p0) throws IOException;
    
    Packer write(final float p0) throws IOException;
    
    Packer write(final double p0) throws IOException;
    
    Packer write(final Boolean p0) throws IOException;
    
    Packer write(final Byte p0) throws IOException;
    
    Packer write(final Short p0) throws IOException;
    
    Packer write(final Integer p0) throws IOException;
    
    Packer write(final Long p0) throws IOException;
    
    Packer write(final Float p0) throws IOException;
    
    Packer write(final Double p0) throws IOException;
    
    Packer write(final BigInteger p0) throws IOException;
    
    Packer write(final byte[] p0) throws IOException;
    
    Packer write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    Packer write(final ByteBuffer p0) throws IOException;
    
    Packer write(final String p0) throws IOException;
    
    Packer write(final Value p0) throws IOException;
    
    Packer write(final Object p0) throws IOException;
    
    Packer writeNil() throws IOException;
    
    Packer writeArrayBegin(final int p0) throws IOException;
    
    Packer writeArrayEnd(final boolean p0) throws IOException;
    
    Packer writeArrayEnd() throws IOException;
    
    Packer writeMapBegin(final int p0) throws IOException;
    
    Packer writeMapEnd(final boolean p0) throws IOException;
    
    Packer writeMapEnd() throws IOException;
}
