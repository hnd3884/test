package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerIdsHolder implements Streamable
{
    public int[] value;
    
    public ServerIdsHolder() {
        this.value = null;
    }
    
    public ServerIdsHolder(final int[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerIdsHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerIdsHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerIdsHelper.type();
    }
}
