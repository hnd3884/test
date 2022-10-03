package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextHolder implements Streamable
{
    public NamingContext value;
    
    public NamingContextHolder() {
        this.value = null;
    }
    
    public NamingContextHolder(final NamingContext value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NamingContextHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NamingContextHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NamingContextHelper.type();
    }
}
