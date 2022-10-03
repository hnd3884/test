package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class CannotProceedHolder implements Streamable
{
    public CannotProceed value;
    
    public CannotProceedHolder() {
        this.value = null;
    }
    
    public CannotProceedHolder(final CannotProceed value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = CannotProceedHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        CannotProceedHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return CannotProceedHelper.type();
    }
}
