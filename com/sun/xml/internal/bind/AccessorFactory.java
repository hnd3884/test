package com.sun.xml.internal.bind;

import java.lang.reflect.Method;
import javax.xml.bind.JAXBException;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;

public interface AccessorFactory
{
    Accessor createFieldAccessor(final Class p0, final Field p1, final boolean p2) throws JAXBException;
    
    Accessor createPropertyAccessor(final Class p0, final Method p1, final Method p2) throws JAXBException;
}
