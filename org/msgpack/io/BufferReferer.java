package org.msgpack.io;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface BufferReferer
{
    void refer(final ByteBuffer p0, final boolean p1) throws IOException;
}
