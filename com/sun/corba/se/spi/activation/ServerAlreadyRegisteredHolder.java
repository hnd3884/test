package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyRegisteredHolder implements Streamable
{
    public ServerAlreadyRegistered value;
    
    public ServerAlreadyRegisteredHolder() {
        this.value = null;
    }
    
    public ServerAlreadyRegisteredHolder(final ServerAlreadyRegistered value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerAlreadyRegisteredHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerAlreadyRegisteredHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerAlreadyRegisteredHelper.type();
    }
}
