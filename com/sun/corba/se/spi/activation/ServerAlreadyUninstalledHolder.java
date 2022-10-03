package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyUninstalledHolder implements Streamable
{
    public ServerAlreadyUninstalled value;
    
    public ServerAlreadyUninstalledHolder() {
        this.value = null;
    }
    
    public ServerAlreadyUninstalledHolder(final ServerAlreadyUninstalled value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerAlreadyUninstalledHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerAlreadyUninstalledHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerAlreadyUninstalledHelper.type();
    }
}
