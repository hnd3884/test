package com.sun.java.browser.dom;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract class DOMServiceProvider
{
    public abstract boolean canHandle(final Object p0);
    
    public abstract Document getDocument(final Object p0) throws DOMUnsupportedException;
    
    public abstract DOMImplementation getDOMImplementation();
}
