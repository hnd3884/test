package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;

public interface XMLErrorHandler
{
    void warning(final String p0, final String p1, final XMLParseException p2) throws XNIException;
    
    void error(final String p0, final String p1, final XMLParseException p2) throws XNIException;
    
    void fatalError(final String p0, final String p1, final XMLParseException p2) throws XNIException;
}
