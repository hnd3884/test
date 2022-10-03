package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHolder implements Streamable
{
    public Server value;
    
    public ServerHolder() {
        this.value = null;
    }
    
    public ServerHolder(final Server value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerHelper.type();
    }
}
