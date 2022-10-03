package org.apache.axiom.soap.impl.intf;

import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPFaultText;

public interface AxiomSOAP12FaultText extends AxiomSOAP12Element, SOAPFaultText
{
    Class<? extends CoreNode> coreGetNodeClass();
    
    String getLang();
    
    void setLang(final String p0);
}
