package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.impl.common.AxiomSOAP11FaultSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Fault;
import org.apache.axiom.soap.impl.llom.SOAPFaultImpl;

public class SOAP11FaultImpl extends SOAPFaultImpl implements AxiomSOAP11Fault
{
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11BodyImpl)) {
            throw new SOAPProcessingException("Expecting SOAP 1.1 implementation of SOAP Body as the parent. But received some other implementation");
        }
    }
    
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$coreGetNodeClass(this);
    }
    
    public final SOAPFaultCode getCode() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getCode(this);
    }
    
    public final SOAPFaultDetail getDetail() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getDetail(this);
    }
    
    public final SOAPFaultNode getNode() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getNode(this);
    }
    
    public final SOAPFaultReason getReason() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getReason(this);
    }
    
    public final SOAPFaultRole getRole() {
        return AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getRole(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
    
    public final void setCode(final SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setCode(this, soapFaultCode);
    }
    
    public final void setDetail(final SOAPFaultDetail detail) throws SOAPProcessingException {
        AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setDetail(this, detail);
    }
    
    public final void setNode(final SOAPFaultNode node) throws SOAPProcessingException {
        AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setNode(this, node);
    }
    
    public final void setReason(final SOAPFaultReason reason) throws SOAPProcessingException {
        AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setReason(this, reason);
    }
    
    public final void setRole(final SOAPFaultRole role) throws SOAPProcessingException {
        AxiomSOAP11FaultSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setRole(this, role);
    }
}
