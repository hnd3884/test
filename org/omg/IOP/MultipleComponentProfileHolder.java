package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class MultipleComponentProfileHolder implements Streamable
{
    public TaggedComponent[] value;
    
    public MultipleComponentProfileHolder() {
        this.value = null;
    }
    
    public MultipleComponentProfileHolder(final TaggedComponent[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = MultipleComponentProfileHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        MultipleComponentProfileHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return MultipleComponentProfileHelper.type();
    }
}
