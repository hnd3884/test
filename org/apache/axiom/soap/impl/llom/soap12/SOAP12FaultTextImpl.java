package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultTextSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultText;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public class SOAP12FaultTextImpl extends SOAPElement implements AxiomSOAP12FaultText
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultReasonImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultReasonImpl as parent, got " + parent.getClass());
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultTextSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$coreGetNodeClass(this);
    }
    
    public final String getLang() {
        return AxiomSOAP12FaultTextSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$getLang(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
    
    public final void setLang(final String lang) {
        AxiomSOAP12FaultTextSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultTextSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultText$setLang(this, lang);
    }
}
