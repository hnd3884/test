package org.apache.axiom.soap.impl.intf;

import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP11FaultCode extends AxiomSOAPFaultCode, AxiomSOAP11Element
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    SOAPFaultSubCode getSubCode();
    
    SOAPFaultValue getValue();
    
    QName getValueAsQName();
    
    void setSubCode(final SOAPFaultSubCode p0);
    
    void setValue(final QName p0);
    
    void setValue(final SOAPFaultValue p0);
}
