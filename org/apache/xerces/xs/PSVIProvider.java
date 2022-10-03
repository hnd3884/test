package org.apache.xerces.xs;

public interface PSVIProvider
{
    ElementPSVI getElementPSVI();
    
    AttributePSVI getAttributePSVI(final int p0);
    
    AttributePSVI getAttributePSVIByName(final String p0, final String p1);
}
