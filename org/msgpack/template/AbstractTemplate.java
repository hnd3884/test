package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;

public abstract class AbstractTemplate<T> implements Template<T>
{
    @Override
    public void write(final Packer pk, final T v) throws IOException {
        this.write(pk, v, false);
    }
    
    @Override
    public T read(final Unpacker u, final T to) throws IOException {
        return this.read(u, to, false);
    }
}
