package org.msgpack.template;

import org.msgpack.unpacker.Unpacker;
import java.io.IOException;
import org.msgpack.packer.Packer;

public class NotNullableTemplate<T> extends AbstractTemplate<T>
{
    private Template<T> tmpl;
    
    public NotNullableTemplate(final Template<T> elementTemplate) {
        this.tmpl = elementTemplate;
    }
    
    @Override
    public void write(final Packer pk, final T v, final boolean required) throws IOException {
        this.tmpl.write(pk, v, required);
    }
    
    @Override
    public void write(final Packer pk, final T v) throws IOException {
        this.write(pk, v, true);
    }
    
    @Override
    public T read(final Unpacker u, final T to, final boolean required) throws IOException {
        return this.tmpl.read(u, to, required);
    }
    
    @Override
    public T read(final Unpacker u, final T to) throws IOException {
        return this.read(u, to, true);
    }
}
