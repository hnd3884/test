package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NoSuchEndPointHolder implements Streamable
{
    public NoSuchEndPoint value;
    
    public NoSuchEndPointHolder() {
        this.value = null;
    }
    
    public NoSuchEndPointHolder(final NoSuchEndPoint value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NoSuchEndPointHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NoSuchEndPointHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NoSuchEndPointHelper.type();
    }
}
