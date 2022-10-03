package org.omg.CORBA;

import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.Streamable;

public final class ObjectHolder implements Streamable
{
    public org.omg.CORBA.Object value;
    
    public ObjectHolder() {
    }
    
    public ObjectHolder(final org.omg.CORBA.Object value) {
        this.value = value;
    }
    
    @Override
    public void _read(final InputStream inputStream) {
        this.value = inputStream.read_Object();
    }
    
    @Override
    public void _write(final OutputStream outputStream) {
        outputStream.write_Object(this.value);
    }
    
    @Override
    public TypeCode _type() {
        return ORB.init().get_primitive_tc(TCKind.tk_objref);
    }
}
