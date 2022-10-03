package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;

public interface NamespaceContext
{
    public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
    public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
    
    void pushContext();
    
    void popContext();
    
    boolean declarePrefix(final String p0, final String p1);
    
    String getURI(final String p0);
    
    String getPrefix(final String p0);
    
    int getDeclaredPrefixCount();
    
    String getDeclaredPrefixAt(final int p0);
    
    Enumeration getAllPrefixes();
    
    void reset();
}
