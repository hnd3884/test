package com.sun.org.apache.xerces.internal.xs;

public interface PSVIProvider
{
    ElementPSVI getElementPSVI();
    
    AttributePSVI getAttributePSVI(final int p0);
    
    AttributePSVI getAttributePSVIByName(final String p0, final String p1);
}
