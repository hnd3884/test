package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultReasonSupport;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultReason;
import org.apache.axiom.soap.impl.llom.SOAPFaultReasonImpl;

public class SOAP12FaultReasonImpl extends SOAPFaultReasonImpl implements AxiomSOAP12FaultReason
{
    public SOAPFaultText getFirstSOAPText() {
        return (SOAPFaultText)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(this, SOAP12Constants.QNAME_FAULT_TEXT);
    }
    
    @Override
    public String getText() {
        return this.getFirstSOAPText().getText();
    }
    
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl, got " + parent.getClass());
        }
    }
    
    public final void addSOAPText(final SOAPFaultText soapFaultText) {
        AxiomSOAP12FaultReasonSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultReason$addSOAPText(this, soapFaultText);
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultReasonSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultReason$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
