package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerAlreadyInstalledHolder implements Streamable
{
    public ServerAlreadyInstalled value;
    
    public ServerAlreadyInstalledHolder() {
        this.value = null;
    }
    
    public ServerAlreadyInstalledHolder(final ServerAlreadyInstalled value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerAlreadyInstalledHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerAlreadyInstalledHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerAlreadyInstalledHelper.type();
    }
}
