package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

public interface SOAPFaultRole extends OMElement
{
    void setRoleValue(final String p0);
    
    String getRoleValue();
}
