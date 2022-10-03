package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class EndpointInfoListHolder implements Streamable
{
    public EndPointInfo[] value;
    
    public EndpointInfoListHolder() {
        this.value = null;
    }
    
    public EndpointInfoListHolder(final EndPointInfo[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = EndpointInfoListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        EndpointInfoListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return EndpointInfoListHelper.type();
    }
}
