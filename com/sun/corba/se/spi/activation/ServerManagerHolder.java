package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerManagerHolder implements Streamable
{
    public ServerManager value;
    
    public ServerManagerHolder() {
        this.value = null;
    }
    
    public ServerManagerHolder(final ServerManager value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerManagerHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerManagerHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerManagerHelper.type();
    }
}
