package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class FloatHolder implements Streamable
{
    public float value;
    
    public FloatHolder() {
    }
    
    public FloatHolder(final float value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_float();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_float(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_float);
    }
}
