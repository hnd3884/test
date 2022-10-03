package org.apache.axiom.soap.impl.intf;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPFaultDetail;

public interface AxiomSOAPFaultDetail extends AxiomSOAPElement, SOAPFaultDetail
{
    void addDetailEntry(final OMElement p0);
    
    Iterator getAllDetailEntries();
}
