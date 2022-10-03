package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import org.xml.sax.SAXException;

public interface Receiver
{
    void receive(final UnmarshallingContext.State p0, final Object p1) throws SAXException;
}
