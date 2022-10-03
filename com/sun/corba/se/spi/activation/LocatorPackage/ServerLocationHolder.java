package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationHolder implements Streamable
{
    public ServerLocation value;
    
    public ServerLocationHolder() {
        this.value = null;
    }
    
    public ServerLocationHolder(final ServerLocation value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerLocationHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerLocationHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerLocationHelper.type();
    }
}
