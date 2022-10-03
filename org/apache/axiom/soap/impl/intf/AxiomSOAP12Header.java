package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP12Header extends AxiomSOAPHeader, AxiomSOAP12Element
{
    Class<? extends CoreNode> coreGetNodeClass();
}
