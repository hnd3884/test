package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class AlreadyBoundHolder implements Streamable
{
    public AlreadyBound value;
    
    public AlreadyBoundHolder() {
        this.value = null;
    }
    
    public AlreadyBoundHolder(final AlreadyBound value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = AlreadyBoundHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        AlreadyBoundHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return AlreadyBoundHelper.type();
    }
}
