package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import java.io.Serializable;
import org.omg.CORBA.portable.Streamable;

public final class ValueBaseHolder implements Streamable
{
    public Serializable value;
    
    public ValueBaseHolder() {
    }
    
    public ValueBaseHolder(final Serializable value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = ((org.omg.CORBA_2_3.portable.InputStream)inputStream).read_value();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        ((org.omg.CORBA_2_3.portable.OutputStream)outputStream).write_value(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_value);
    }
}
