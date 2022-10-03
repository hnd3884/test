package com.sun.corba.se.spi.activation;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidORBidHolder implements Streamable
{
    public InvalidORBid value;
    
    public InvalidORBidHolder() {
        this.value = null;
    }
    
    public InvalidORBidHolder(final InvalidORBid value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = InvalidORBidHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        InvalidORBidHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return InvalidORBidHelper.type();
    }
}
