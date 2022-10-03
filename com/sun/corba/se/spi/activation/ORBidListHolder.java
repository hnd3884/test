package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ORBidListHolder implements Streamable
{
    public String[] value;
    
    public ORBidListHolder() {
        this.value = null;
    }
    
    public ORBidListHolder(final String[] value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ORBidListHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ORBidListHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORBidListHelper.type();
    }
}
