package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFaultSubCode;

public interface AxiomSOAP12FaultSubCode extends AxiomSOAP12FaultClassifier, SOAPFaultSubCode
{
    Class<? extends CoreNode> coreGetNodeClass();
}
