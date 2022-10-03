package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP12HeaderBlock extends AxiomSOAPHeaderBlock, AxiomSOAP12Element
{
    Class<? extends CoreNode> coreGetNodeClass();
}
