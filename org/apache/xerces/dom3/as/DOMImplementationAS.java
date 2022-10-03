package org.apache.xerces.dom3.as;

public interface DOMImplementationAS
{
    ASModel createAS(final boolean p0);
    
    DOMASBuilder createDOMASBuilder();
    
    DOMASWriter createDOMASWriter();
}
