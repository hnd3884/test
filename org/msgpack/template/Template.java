package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;

public interface Template<T>
{
    void write(final Packer p0, final T p1) throws IOException;
    
    void write(final Packer p0, final T p1, final boolean p2) throws IOException;
    
    T read(final Unpacker p0, final T p1) throws IOException;
    
    T read(final Unpacker p0, final T p1, final boolean p2) throws IOException;
}
