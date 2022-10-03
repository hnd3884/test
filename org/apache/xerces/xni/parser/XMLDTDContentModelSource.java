package org.apache.xerces.xni.parser;

import org.apache.xerces.xni.XMLDTDContentModelHandler;

public interface XMLDTDContentModelSource
{
    void setDTDContentModelHandler(final XMLDTDContentModelHandler p0);
    
    XMLDTDContentModelHandler getDTDContentModelHandler();
}
