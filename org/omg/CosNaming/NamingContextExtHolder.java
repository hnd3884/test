package org.omg.CosNaming;

import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class NamingContextExtHolder implements Streamable
{
    public NamingContextExt value;
    
    public NamingContextExtHolder() {
        this.value = null;
    }
    
    public NamingContextExtHolder(final NamingContextExt value) {
        this.value = null;
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = NamingContextExtHelper.read(inputStream);
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        NamingContextExtHelper.write(outputStream, this.value);
    }
    
    @Override
    public TypeCode _type() {
        return NamingContextExtHelper.type();
    }
}
