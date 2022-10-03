package org.apache.xmlbeans.impl.soap;

public interface Node extends org.w3c.dom.Node
{
    String getValue();
    
    void setParentElement(final SOAPElement p0) throws SOAPException;
    
    SOAPElement getParentElement();
    
    void detachNode();
    
    void recycleNode();
    
    void setValue(final String p0);
}
