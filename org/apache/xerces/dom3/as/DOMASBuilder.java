package org.apache.xerces.dom3.as;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

public interface DOMASBuilder extends LSParser
{
    ASModel getAbstractSchema();
    
    void setAbstractSchema(final ASModel p0);
    
    ASModel parseASURI(final String p0) throws DOMASException, Exception;
    
    ASModel parseASInputSource(final LSInput p0) throws DOMASException, Exception;
}
