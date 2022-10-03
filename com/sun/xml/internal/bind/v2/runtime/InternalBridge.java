package com.sun.xml.internal.bind.v2.runtime;

import com.sun.xml.internal.bind.api.JAXBRIContext;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;
import java.io.IOException;
import com.sun.xml.internal.bind.api.Bridge;

abstract class InternalBridge<T> extends Bridge<T>
{
    protected InternalBridge(final JAXBContextImpl context) {
        super(context);
    }
    
    @Override
    public JAXBContextImpl getContext() {
        return this.context;
    }
    
    abstract void marshal(final T p0, final XMLSerializer p1) throws IOException, SAXException, XMLStreamException;
}
