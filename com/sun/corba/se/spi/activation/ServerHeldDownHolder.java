package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerHeldDownHolder implements Streamable
{
    public ServerHeldDown value;
    
    public ServerHeldDownHolder() {
        this.value = null;
    }
    
    public ServerHeldDownHolder(final ServerHeldDown value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerHeldDownHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerHeldDownHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerHeldDownHelper.type();
    }
}
