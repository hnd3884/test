package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

@Deprecated
public final class PrincipalHolder implements Streamable
{
    public Principal value;
    
    public PrincipalHolder() {
    }
    
    public PrincipalHolder(final Principal value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_Principal();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_Principal(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_Principal);
    }
}
