package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;

public interface XMLDTDSource
{
    void setDTDHandler(final XMLDTDHandler p0);
    
    XMLDTDHandler getDTDHandler();
}
