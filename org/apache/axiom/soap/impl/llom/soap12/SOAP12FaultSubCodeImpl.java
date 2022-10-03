package org.apache.axiom.soap.impl.llom.soap12;

import javax.xml.namespace.QName;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultClassifier;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultClassifierSupport;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultSubCodeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultSubCode;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public class SOAP12FaultSubCodeImpl extends SOAPElement implements AxiomSOAP12FaultSubCode
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultSubCodeImpl) && !(parent instanceof SOAP12FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP FaultSubCode or SOAP FaultCodeValue as the parent. But received some other implementation");
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultSubCodeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSubCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultSubCode$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
    
    public final SOAPFaultSubCode getSubCode() {
        return AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getSubCode(this);
    }
    
    public final SOAPFaultValue getValue() {
        return AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getValue(this);
    }
    
    public final QName getValueAsQName() {
        return AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$getValueAsQName(this);
    }
    
    public final void setSubCode(final SOAPFaultSubCode subCode) {
        AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setSubCode(this, subCode);
    }
    
    public final void setValue(final QName value) {
        AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setValue(this, value);
    }
    
    public final void setValue(final SOAPFaultValue value) {
        AxiomSOAP12FaultClassifierSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultClassifierSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultClassifier$setValue(this, value);
    }
}
