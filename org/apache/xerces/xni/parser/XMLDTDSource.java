package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLDTDHandler;

public interface XMLDTDSource
{
    void setDTDHandler(final XMLDTDHandler p0);
    
    XMLDTDHandler getDTDHandler();
}
