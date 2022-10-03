package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class RepositoryHolder implements Streamable
{
    public Repository value;
    
    public RepositoryHolder() {
        this.value = null;
    }
    
    public RepositoryHolder(final Repository value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = RepositoryHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        RepositoryHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return RepositoryHelper.type();
    }
}
