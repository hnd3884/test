package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFaultNode;

public interface AxiomSOAP12FaultNode extends AxiomSOAP12Element, SOAPFaultNode
{
    Class<? extends CoreNode> coreGetNodeClass();
}
