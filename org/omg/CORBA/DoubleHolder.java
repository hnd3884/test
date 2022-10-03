package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class DoubleHolder implements Streamable
{
    public double value;
    
    public DoubleHolder() {
    }
    
    public DoubleHolder(final double value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_double();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_double(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_double);
    }
}
