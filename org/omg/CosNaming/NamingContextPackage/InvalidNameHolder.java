package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidNameHolder implements Streamable
{
    public InvalidName value;
    
    public InvalidNameHolder() {
        this.value = null;
    }
    
    public InvalidNameHolder(final InvalidName value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = InvalidNameHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        InvalidNameHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return InvalidNameHelper.type();
    }
}
