package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPEnvelope;

public interface AxiomSOAPEnvelope extends AxiomSOAPElement, SOAPEnvelope
{
    void internalCheckChild(final OMNode p0);
}
