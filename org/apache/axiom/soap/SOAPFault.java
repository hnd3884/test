package org.apache.axiom.soap;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;

public interface SOAPFault extends OMElement
{
    void setCode(final SOAPFaultCode p0) throws SOAPProcessingException;
    
    SOAPFaultCode getCode();
    
    void setReason(final SOAPFaultReason p0) throws SOAPProcessingException;
    
    SOAPFaultReason getReason();
    
    void setNode(final SOAPFaultNode p0) throws SOAPProcessingException;
    
    SOAPFaultNode getNode();
    
    void setRole(final SOAPFaultRole p0) throws SOAPProcessingException;
    
    SOAPFaultRole getRole();
    
    void setDetail(final SOAPFaultDetail p0) throws SOAPProcessingException;
    
    SOAPFaultDetail getDetail();
    
    Exception getException() throws OMException;
    
    void setException(final Exception p0) throws OMException;
}
