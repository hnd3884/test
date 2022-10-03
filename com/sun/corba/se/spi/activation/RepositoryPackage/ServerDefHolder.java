package com.sun.corba.se.spi.activation.RepositoryPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerDefHolder implements Streamable
{
    public ServerDef value;
    
    public ServerDefHolder() {
        this.value = null;
    }
    
    public ServerDefHolder(final ServerDef value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerDefHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerDefHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerDefHelper.type();
    }
}
