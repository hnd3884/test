package com.sun.org.apache.xml.internal.utils;

import org.w3c.dom.Node;

public interface PrefixResolver
{
    String getNamespaceForPrefix(final String p0);
    
    String getNamespaceForPrefix(final String p0, final Node p1);
    
    String getBaseIdentifier();
    
    boolean handlesNullPrefixes();
}
