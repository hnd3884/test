package com.sun.org.apache.xerces.internal.xs;

public interface XSImplementation
{
    StringList getRecognizedVersions();
    
    XSLoader createXSLoader(final StringList p0) throws XSException;
}
