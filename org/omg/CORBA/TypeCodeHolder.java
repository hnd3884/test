package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class TypeCodeHolder implements Streamable
{
    public TypeCode value;
    
    public TypeCodeHolder() {
    }
    
    public TypeCodeHolder(final TypeCode value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_TypeCode();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_TypeCode(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
    }
}
