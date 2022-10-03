package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.soap.SOAPFault;

public interface AxiomSOAPFault extends AxiomSOAPElement, SOAPFault
{
    Exception getException();
    
    void setException(final Exception p0);
}
