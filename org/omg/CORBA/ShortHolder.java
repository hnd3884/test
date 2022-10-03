package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ShortHolder implements Streamable
{
    public short value;
    
    public ShortHolder() {
    }
    
    public ShortHolder(final short value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_short();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_short(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_short);
    }
}
