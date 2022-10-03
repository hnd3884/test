package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServiceContextListHolder implements Streamable
{
    public ServiceContext[] value;
    
    public ServiceContextListHolder() {
        this.value = null;
    }
    
    public ServiceContextListHolder(final ServiceContext[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServiceContextListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServiceContextListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServiceContextListHelper.type();
    }
}
