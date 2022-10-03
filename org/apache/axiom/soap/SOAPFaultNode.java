package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

public interface SOAPFaultNode extends OMElement
{
    @Deprecated
    void setNodeValue(final String p0);
    
    @Deprecated
    String getNodeValue();
    
    void setFaultNodeValue(final String p0);
    
    String getFaultNodeValue();
}
