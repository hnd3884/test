package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameHolder implements Streamable
{
    public NameComponent[] value;
    
    public NameHolder() {
        this.value = null;
    }
    
    public NameHolder(final NameComponent[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NameHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NameHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NameHelper.type();
    }
}
