package org.apache.xerces.xs;

import org.w3c.dom.ls.LSInput;

public interface XSImplementation
{
    StringList getRecognizedVersions();
    
    XSLoader createXSLoader(final StringList p0) throws XSException;
    
    StringList createStringList(final String[] p0);
    
    LSInputList createLSInputList(final LSInput[] p0);
}
