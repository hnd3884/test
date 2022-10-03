package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ByteHolder implements Streamable
{
    public byte value;
    
    public ByteHolder() {
    }
    
    public ByteHolder(final byte value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_octet();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_octet(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_octet);
    }
}
