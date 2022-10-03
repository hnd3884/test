package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;

public interface AxiomSOAPElement extends AxiomElement
{
    SOAPHelper getSOAPHelper();
    
    void checkParent(final OMElement p0) throws SOAPProcessingException;
    
    OMFactory getOMFactory();
}
