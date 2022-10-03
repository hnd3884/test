package org.omg.CORBA.portable;

import java.io.Serializable;
import org.omg.CORBA_2_3.portable.InputStream;

public interface ValueFactory
{
    Serializable read_value(final InputStream p0);
}
