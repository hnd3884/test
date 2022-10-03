package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBPortInfoListHolder implements Streamable
{
    public ORBPortInfo[] value;
    
    public ORBPortInfoListHolder() {
        this.value = null;
    }
    
    public ORBPortInfoListHolder(final ORBPortInfo[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ORBPortInfoListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ORBPortInfoListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORBPortInfoListHelper.type();
    }
}
