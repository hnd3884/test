package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultCodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultCode;
import org.apache.axiom.soap.impl.llom.SOAPFaultCodeImpl;

public class SOAP11FaultCodeImpl extends SOAPFaultCodeImpl implements AxiomSOAP11FaultCode
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
    
    public final SOAPFaultSubCode getSubCode() {
        return AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getSubCode(this);
    }
    
    public final SOAPFaultValue getValue() {
        return AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getValue(this);
    }
    
    public final QName getValueAsQName() {
        return AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$getValueAsQName(this);
    }
    
    public final void setSubCode(final SOAPFaultSubCode subCode) {
        AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setSubCode(this, subCode);
    }
    
    public final void setValue(final QName value) {
        AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setValue(this, value);
    }
    
    public final void setValue(final SOAPFaultValue value) {
        AxiomSOAP11FaultCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultCode$setValue(this, value);
    }
}
