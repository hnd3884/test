package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP11FaultDetail extends AxiomSOAPFaultDetail, AxiomSOAP11Element
{
    Class<? extends CoreNode> coreGetNodeClass();
}
