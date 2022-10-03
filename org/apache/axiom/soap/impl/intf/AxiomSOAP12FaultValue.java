package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFaultValue;

public interface AxiomSOAP12FaultValue extends AxiomSOAP12Element, SOAPFaultValue
{
    Class<? extends CoreNode> coreGetNodeClass();
}
