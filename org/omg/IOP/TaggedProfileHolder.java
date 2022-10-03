package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class TaggedProfileHolder implements Streamable
{
    public TaggedProfile value;
    
    public TaggedProfileHolder() {
        this.value = null;
    }
    
    public TaggedProfileHolder(final TaggedProfile value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = TaggedProfileHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        TaggedProfileHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return TaggedProfileHelper.type();
    }
}
