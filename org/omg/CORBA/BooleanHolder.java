package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class BooleanHolder implements Streamable
{
    public boolean value;
    
    public BooleanHolder() {
    }
    
    public BooleanHolder(final boolean value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_boolean();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_boolean(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_boolean);
    }
}
