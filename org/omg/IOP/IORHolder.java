package org.omg.IOP;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class IORHolder implements Streamable
{
    public IOR value;
    
    public IORHolder() {
        this.value = null;
    }
    
    public IORHolder(final IOR value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = IORHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        IORHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return IORHelper.type();
    }
}
