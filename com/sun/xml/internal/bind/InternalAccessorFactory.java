package com.sun.xml.internal.bind;

import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;

public interface InternalAccessorFactory extends AccessorFactory
{
    Accessor createFieldAccessor(final Class p0, final Field p1, final boolean p2, final boolean p3) throws JAXBException;
}
