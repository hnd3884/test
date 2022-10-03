package org.omg.CORBA.portable;

import java.io.Serializable;

public interface BoxedValueHelper
{
    Serializable read_value(final InputStream p0);
    
    void write_value(final OutputStream p0, final Serializable p1);
    
    String get_id();
}
