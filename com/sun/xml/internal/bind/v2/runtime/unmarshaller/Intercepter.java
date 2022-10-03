package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Intercepter
{
    Object intercept(final UnmarshallingContext.State p0, final Object p1) throws SAXException;
}
