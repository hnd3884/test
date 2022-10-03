package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDContentModelHandler;

public interface XMLDTDContentModelSource
{
    void setDTDContentModelHandler(final XMLDTDContentModelHandler p0);
    
    XMLDTDContentModelHandler getDTDContentModelHandler();
}
