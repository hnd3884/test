package org.msgpack;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;

public interface MessagePackable
{
    void writeTo(final Packer p0) throws IOException;
    
    void readFrom(final Unpacker p0) throws IOException;
}
