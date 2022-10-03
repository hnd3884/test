package org.omg.CORBA.portable;

import org.omg.CORBA.TypeCode;

public interface Streamable
{
    void _read(final InputStream p0);
    
    void _write(final OutputStream p0);
    
    TypeCode _type();
}
