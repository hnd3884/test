package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoHolder implements Streamable
{
    public ORBPortInfo value;
    
    public ORBPortInfoHolder() {
        this.value = null;
    }
    
    public ORBPortInfoHolder(final ORBPortInfo value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ORBPortInfoHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ORBPortInfoHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORBPortInfoHelper.type();
    }
}
