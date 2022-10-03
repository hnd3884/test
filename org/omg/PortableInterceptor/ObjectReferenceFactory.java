package org.omg.PortableInterceptor;

import org.omg.CORBA.Object;
import org.omg.CORBA.portable.ValueBase;

public interface ObjectReferenceFactory extends ValueBase
{
    org.omg.CORBA.Object make_object(final String p0, final byte[] p1);
}
