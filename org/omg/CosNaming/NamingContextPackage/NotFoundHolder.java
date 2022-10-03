package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NotFoundHolder implements Streamable
{
    public NotFound value;
    
    public NotFoundHolder() {
        this.value = null;
    }
    
    public NotFoundHolder(final NotFound value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NotFoundHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NotFoundHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NotFoundHelper.type();
    }
}
