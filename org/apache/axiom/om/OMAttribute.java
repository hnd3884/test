package org.apache.axiom.om;

public interface OMAttribute extends OMNamedInformationItem
{
    String getAttributeValue();
    
    void setAttributeValue(final String p0);
    
    String getAttributeType();
    
    void setAttributeType(final String p0);
    
    @Deprecated
    void setOMNamespace(final OMNamespace p0);
    
    OMElement getOwner();
}
