package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerNotActiveHolder implements Streamable
{
    public ServerNotActive value;
    
    public ServerNotActiveHolder() {
        this.value = null;
    }
    
    public ServerNotActiveHolder(final ServerNotActive value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerNotActiveHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerNotActiveHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerNotActiveHelper.type();
    }
}
