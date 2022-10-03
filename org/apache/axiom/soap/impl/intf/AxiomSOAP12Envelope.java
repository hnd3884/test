package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreNode;

public interface AxiomSOAP12Envelope extends AxiomSOAPEnvelope, AxiomSOAP12Element
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    void internalCheckChild(final OMNode p0);
}
