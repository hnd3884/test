package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultNodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public class SOAP12FaultNodeImpl extends SOAPElement implements AxiomSOAP12FaultNode
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl, got " + parent.getClass());
        }
    }
    
    public void setFaultNodeValue(final String uri) {
        this.setText(uri);
    }
    
    public String getFaultNodeValue() {
        return this.getText();
    }
    
    public void setNodeValue(final String uri) {
        this.setFaultNodeValue(uri);
    }
    
    public String getNodeValue() {
        return this.getFaultNodeValue();
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultNodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultNodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultNode$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
