package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.soap.SOAPFaultRole;

public interface AxiomSOAPFaultRole extends AxiomSOAPElement, SOAPFaultRole
{
    String getRoleValue();
    
    void setRoleValue(final String p0);
}
