package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.impl.common.AxiomSOAPFaultSupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFault;

public abstract class SOAPFaultImpl extends SOAPElement implements AxiomSOAPFault
{
    public final Exception getException() {
        return AxiomSOAPFaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFault$getException(this);
    }
    
    public final void setException(final Exception e) {
        AxiomSOAPFaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFault$setException(this, e);
    }
}
