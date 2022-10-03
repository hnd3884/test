package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBAlreadyRegisteredHolder implements Streamable
{
    public ORBAlreadyRegistered value;
    
    public ORBAlreadyRegisteredHolder() {
        this.value = null;
    }
    
    public ORBAlreadyRegisteredHolder(final ORBAlreadyRegistered value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ORBAlreadyRegisteredHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ORBAlreadyRegisteredHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORBAlreadyRegisteredHelper.type();
    }
}
