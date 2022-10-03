package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDocumentFilter;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponent;

public interface XPointerSchema extends XMLComponent, XMLDocumentFilter
{
    void setXPointerSchemaName(final String p0);
    
    String getXpointerSchemaName();
    
    void setParent(final Object p0);
    
    Object getParent();
    
    void setXPointerSchemaPointer(final String p0);
    
    String getXPointerSchemaPointer();
    
    boolean isSubResourceIndentified();
    
    void reset();
}
