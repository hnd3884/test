package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP11Envelope extends AxiomSOAPEnvelope, AxiomSOAP11Element
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    void internalCheckChild(final OMNode p0);
}
