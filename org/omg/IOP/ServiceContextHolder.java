package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextHolder implements Streamable
{
    public ServiceContext value;
    
    public ServiceContextHolder() {
        this.value = null;
    }
    
    public ServiceContextHolder(final ServiceContext value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServiceContextHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServiceContextHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServiceContextHelper.type();
    }
}
