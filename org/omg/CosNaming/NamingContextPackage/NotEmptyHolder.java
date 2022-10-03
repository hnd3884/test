package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotEmptyHolder implements Streamable
{
    public NotEmpty value;
    
    public NotEmptyHolder() {
        this.value = null;
    }
    
    public NotEmptyHolder(final NotEmpty value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NotEmptyHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NotEmptyHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NotEmptyHelper.type();
    }
}
