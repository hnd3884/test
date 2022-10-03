package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class LongHolder implements Streamable
{
    public long value;
    
    public LongHolder() {
    }
    
    public LongHolder(final long value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_longlong();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_longlong(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_longlong);
    }
}
