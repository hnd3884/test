package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class AnyHolder implements Streamable
{
    public Any value;
    
    public AnyHolder() {
    }
    
    public AnyHolder(final Any value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_any();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_any(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_any);
    }
}
