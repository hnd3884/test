package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.impl.common.AxiomSOAPFaultRoleSupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultRole;

public abstract class SOAPFaultRoleImpl extends SOAPElement implements AxiomSOAPFaultRole
{
    public final String getRoleValue() {
        return AxiomSOAPFaultRoleSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultRole$getRoleValue(this);
    }
    
    public final void setRoleValue(final String uri) {
        AxiomSOAPFaultRoleSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultRole$setRoleValue(this, uri);
    }
}
