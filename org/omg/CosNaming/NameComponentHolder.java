package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameComponentHolder implements Streamable
{
    public NameComponent value;
    
    public NameComponentHolder() {
        this.value = null;
    }
    
    public NameComponentHolder(final NameComponent value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NameComponentHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NameComponentHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NameComponentHelper.type();
    }
}
