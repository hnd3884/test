package com.sun.xml.internal.bind;

import java.util.concurrent.Callable;
import org.xml.sax.SAXException;
import javax.xml.bind.ValidationEventHandler;

public abstract class IDResolver
{
    public void startDocument(final ValidationEventHandler eventHandler) throws SAXException {
    }
    
    public void endDocument() throws SAXException {
    }
    
    public abstract void bind(final String p0, final Object p1) throws SAXException;
    
    public abstract Callable<?> resolve(final String p0, final Class p1) throws SAXException;
}
