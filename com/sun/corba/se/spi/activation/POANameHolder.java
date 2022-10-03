package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class POANameHolder implements Streamable
{
    public String[] value;
    
    public POANameHolder() {
        this.value = null;
    }
    
    public POANameHolder(final String[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = POANameHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        POANameHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return POANameHelper.type();
    }
}
