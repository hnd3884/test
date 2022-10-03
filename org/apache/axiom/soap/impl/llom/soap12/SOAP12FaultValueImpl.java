package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultValueSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultValue;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public class SOAP12FaultValueImpl extends SOAPElement implements AxiomSOAP12FaultValue
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultSubCodeImpl) && !(parent instanceof SOAP12FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultSubCodeImpl or SOAP12FaultCodeImpl as parent, got " + parent.getClass());
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultValueSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultValueSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultValue$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
}
