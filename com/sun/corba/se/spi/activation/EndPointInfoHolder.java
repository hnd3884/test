package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndPointInfoHolder implements Streamable
{
    public EndPointInfo value;
    
    public EndPointInfoHolder() {
        this.value = null;
    }
    
    public EndPointInfoHolder(final EndPointInfo value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = EndPointInfoHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        EndPointInfoHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return EndPointInfoHelper.type();
    }
}
