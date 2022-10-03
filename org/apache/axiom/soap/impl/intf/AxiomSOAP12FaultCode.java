package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP12FaultCode extends AxiomSOAPFaultCode, AxiomSOAP12FaultClassifier
{
    Class<? extends CoreNode> coreGetNodeClass();
}
