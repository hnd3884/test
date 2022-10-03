package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP12Fault extends AxiomSOAPFault, AxiomSOAP12Element
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    SOAPFaultCode getCode();
    
    SOAPFaultDetail getDetail();
    
    SOAPFaultNode getNode();
    
    SOAPFaultReason getReason();
    
    SOAPFaultRole getRole();
    
    void setCode(final SOAPFaultCode p0) throws SOAPProcessingException;
    
    void setDetail(final SOAPFaultDetail p0) throws SOAPProcessingException;
    
    void setNode(final SOAPFaultNode p0) throws SOAPProcessingException;
    
    void setReason(final SOAPFaultReason p0) throws SOAPProcessingException;
    
    void setRole(final SOAPFaultRole p0) throws SOAPProcessingException;
}
