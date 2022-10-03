package org.omg.CosNaming.NamingContextExtPackage;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class InvalidAddressHolder implements Streamable
{
    public InvalidAddress value;
    
    public InvalidAddressHolder() {
        this.value = null;
    }
    
    public InvalidAddressHolder(final InvalidAddress value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = InvalidAddressHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        InvalidAddressHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return InvalidAddressHelper.type();
    }
}
