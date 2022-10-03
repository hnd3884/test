package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class LocatorHolder implements Streamable
{
    public Locator value;
    
    public LocatorHolder() {
        this.value = null;
    }
    
    public LocatorHolder(final Locator value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = LocatorHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        LocatorHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return LocatorHelper.type();
    }
}
