package org.apache.axiom.soap;

import org.apache.axiom.om.OMElement;

public interface SOAPFaultText extends OMElement
{
    void setLang(final String p0);
    
    String getLang();
}
