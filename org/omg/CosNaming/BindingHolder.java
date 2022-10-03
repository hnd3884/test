package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingHolder implements Streamable
{
    public Binding value;
    
    public BindingHolder() {
        this.value = null;
    }
    
    public BindingHolder(final Binding value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = BindingHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        BindingHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return BindingHelper.type();
    }
}
