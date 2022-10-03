package com.sun.java.browser.dom;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

public interface DOMAccessor
{
    Document getDocument(final Object p0) throws DOMException;
    
    DOMImplementation getDOMImplementation();
}
