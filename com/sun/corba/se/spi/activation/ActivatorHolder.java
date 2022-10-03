package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ActivatorHolder implements Streamable
{
    public Activator value;
    
    public ActivatorHolder() {
        this.value = null;
    }
    
    public ActivatorHolder(final Activator value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ActivatorHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ActivatorHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ActivatorHelper.type();
    }
}
