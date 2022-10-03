package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedComponentHolder implements Streamable
{
    public TaggedComponent value;
    
    public TaggedComponentHolder() {
        this.value = null;
    }
    
    public TaggedComponentHolder(final TaggedComponent value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = TaggedComponentHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        TaggedComponentHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return TaggedComponentHelper.type();
    }
}
