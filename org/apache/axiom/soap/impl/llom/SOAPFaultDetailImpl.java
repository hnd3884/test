package org.apache.axiom.soap.impl.llom;

import java.util.Iterator;
import org.apache.axiom.soap.impl.common.AxiomSOAPFaultDetailSupport;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultDetail;

public abstract class SOAPFaultDetailImpl extends SOAPElement implements AxiomSOAPFaultDetail
{
    public final void addDetailEntry(final OMElement detailElement) {
        AxiomSOAPFaultDetailSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultDetail$addDetailEntry(this, detailElement);
    }
    
    public final Iterator getAllDetailEntries() {
        return AxiomSOAPFaultDetailSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultDetail$getAllDetailEntries(this);
    }
}
