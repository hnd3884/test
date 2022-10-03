package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class CharHolder implements Streamable
{
    public char value;
    
    public CharHolder() {
    }
    
    public CharHolder(final char value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_char();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_char(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_char);
    }
}
