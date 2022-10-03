package com.sun.corba.se.spi.activation.InitialNameServicePackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NameAlreadyBoundHolder implements Streamable
{
    public NameAlreadyBound value;
    
    public NameAlreadyBoundHolder() {
        this.value = null;
    }
    
    public NameAlreadyBoundHolder(final NameAlreadyBound value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NameAlreadyBoundHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NameAlreadyBoundHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NameAlreadyBoundHelper.type();
    }
}
