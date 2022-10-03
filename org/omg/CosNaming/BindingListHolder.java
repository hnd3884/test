package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BindingListHolder implements Streamable
{
    public Binding[] value;
    
    public BindingListHolder() {
        this.value = null;
    }
    
    public BindingListHolder(final Binding[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = BindingListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        BindingListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return BindingListHelper.type();
    }
}
