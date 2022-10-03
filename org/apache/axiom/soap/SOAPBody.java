package org.apache.axiom.soap;

import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;

public interface SOAPBody extends OMElement
{
    SOAPFault addFault(final Exception p0) throws OMException;
    
    boolean hasFault();
    
    SOAPFault getFault();
    
    void addFault(final SOAPFault p0) throws OMException;
    
    OMNamespace getFirstElementNS();
    
    String getFirstElementLocalName();
}
