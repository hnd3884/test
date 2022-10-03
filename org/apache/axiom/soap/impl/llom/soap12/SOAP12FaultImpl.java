package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP12FaultSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Fault;
import org.apache.axiom.soap.impl.llom.SOAPFaultImpl;

public class SOAP12FaultImpl extends SOAPFaultImpl implements AxiomSOAP12Fault
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12BodyImpl)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Body as the parent. But received some other implementation");
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$coreGetNodeClass(this);
    }
    
    public final SOAPFaultCode getCode() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getCode(this);
    }
    
    public final SOAPFaultDetail getDetail() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getDetail(this);
    }
    
    public final SOAPFaultNode getNode() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getNode(this);
    }
    
    public final SOAPFaultReason getReason() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getReason(this);
    }
    
    public final SOAPFaultRole getRole() {
        return AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getRole(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
    
    public final void setCode(final SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setCode(this, soapFaultCode);
    }
    
    public final void setDetail(final SOAPFaultDetail detail) throws SOAPProcessingException {
        AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setDetail(this, detail);
    }
    
    public final void setNode(final SOAPFaultNode node) throws SOAPProcessingException {
        AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setNode(this, node);
    }
    
    public final void setReason(final SOAPFaultReason reason) throws SOAPProcessingException {
        AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setReason(this, reason);
    }
    
    public final void setRole(final SOAPFaultRole role) throws SOAPProcessingException {
        AxiomSOAP12FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setRole(this, role);
    }
}
