package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class IntHolder implements Streamable
{
    public int value;
    
    public IntHolder() {
    }
    
    public IntHolder(final int value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_long();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_long(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_long);
    }
}
