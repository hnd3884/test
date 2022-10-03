package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class InitialNameServiceHolder implements Streamable
{
    public InitialNameService value;
    
    public InitialNameServiceHolder() {
        this.value = null;
    }
    
    public InitialNameServiceHolder(final InitialNameService value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = InitialNameServiceHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        InitialNameServiceHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return InitialNameServiceHelper.type();
    }
}
