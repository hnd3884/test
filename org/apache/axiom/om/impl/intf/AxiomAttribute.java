package org.apache.axiom.om.impl.intf;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.core.CoreNSAwareAttribute;
import org.apache.axiom.om.OMAttribute;

public interface AxiomAttribute extends OMAttribute, CoreNSAwareAttribute, AxiomNamedInformationItem
{
    String getAttributeType();
    
    String getAttributeValue();
    
    OMElement getOwner();
    
    void setAttributeType(final String p0);
    
    void setAttributeValue(final String p0);
    
    void setNamespace(final OMNamespace p0, final boolean p1);
    
    void setOMNamespace(final OMNamespace p0);
    
    void setSpecified(final boolean p0);
}
