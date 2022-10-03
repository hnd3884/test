package com.sun.corba.se.spi.activation.LocatorPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ServerLocationPerORBHolder implements Streamable
{
    public ServerLocationPerORB value;
    
    public ServerLocationPerORBHolder() {
        this.value = null;
    }
    
    public ServerLocationPerORBHolder(final ServerLocationPerORB value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ServerLocationPerORBHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ServerLocationPerORBHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ServerLocationPerORBHelper.type();
    }
}
