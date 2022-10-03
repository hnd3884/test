package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFaultText;

public interface AxiomSOAP12FaultReason extends AxiomSOAPFaultReason, AxiomSOAP12Element
{
    void addSOAPText(final SOAPFaultText p0);
    
    Class<? extends CoreNode> coreGetNodeClass();
}
