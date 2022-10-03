package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingTypeHolder implements Streamable
{
    public BindingType value;
    
    public BindingTypeHolder() {
        this.value = null;
    }
    
    public BindingTypeHolder(final BindingType value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = BindingTypeHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        BindingTypeHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return BindingTypeHelper.type();
    }
}
