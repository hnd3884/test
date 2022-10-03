package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyActiveHolder implements Streamable
{
    public ServerAlreadyActive value;
    
    public ServerAlreadyActiveHolder() {
        this.value = null;
    }
    
    public ServerAlreadyActiveHolder(final ServerAlreadyActive value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerAlreadyActiveHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerAlreadyActiveHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerAlreadyActiveHelper.type();
    }
}
