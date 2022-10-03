package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotRegisteredHolder implements Streamable
{
    public ServerNotRegistered value;
    
    public ServerNotRegisteredHolder() {
        this.value = null;
    }
    
    public ServerNotRegisteredHolder(final ServerNotRegistered value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerNotRegisteredHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerNotRegisteredHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerNotRegisteredHelper.type();
    }
}
