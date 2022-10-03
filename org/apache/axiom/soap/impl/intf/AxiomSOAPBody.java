package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPBody;

public interface AxiomSOAPBody extends AxiomSOAPElement, SOAPBody
{
    SOAPFault addFault(final Exception p0) throws OMException;
}
