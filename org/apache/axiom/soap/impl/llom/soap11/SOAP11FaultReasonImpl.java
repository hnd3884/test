package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultReasonSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultReason;
import org.apache.axiom.soap.impl.llom.SOAPFaultReasonImpl;

public class SOAP11FaultReasonImpl extends SOAPFaultReasonImpl implements AxiomSOAP11FaultReason
{
    public void addSOAPText(final SOAPFaultText soapFaultText) throws SOAPProcessingException {
        throw new UnsupportedOperationException("addSOAPText() not allowed for SOAP 1.1!");
    }
    
    public SOAPFaultText getFirstSOAPText() {
        throw new UnsupportedOperationException("getFirstSOAPText() not allowed for SOAP 1.1!");
    }
    
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11FaultReasonSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultReason$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
}
