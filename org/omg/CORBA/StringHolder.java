package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class StringHolder implements Streamable
{
    public String value;
    
    public StringHolder() {
    }
    
    public StringHolder(final String value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_string();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_string(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_string);
    }
}
