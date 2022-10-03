package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.soap.SOAP11Constants;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultDetail;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultRole;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultReason;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultCode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Fault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultCode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11FaultSupport
{
    private static final Class<?>[] sequence;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11FaultSupport ajc$perSingletonInstance;
    
    static {
        try {
            sequence = new Class[] { SOAPFaultCode.class, SOAPFaultReason.class, SOAPFaultRole.class, SOAPFaultDetail.class };
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11FaultSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$coreGetNodeClass(final AxiomSOAP11Fault ajc$this_) {
        return AxiomSOAP11Fault.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setCode(final AxiomSOAP11Fault ajc$this_, final SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        if (!(soapFaultCode instanceof AxiomSOAP11FaultCode)) {
            throw new SOAPProcessingException("Expecting SOAP 1.1 implementation of SOAP Fault Code. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP11FaultSupport.sequence, 0, (OMNode)soapFaultCode);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setReason(final AxiomSOAP11Fault ajc$this_, final SOAPFaultReason reason) throws SOAPProcessingException {
        if (!(reason instanceof AxiomSOAP11FaultReason)) {
            throw new SOAPProcessingException("Expecting SOAP 1.1 implementation of SOAP Fault Reason. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP11FaultSupport.sequence, 1, (OMNode)reason);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setNode(final AxiomSOAP11Fault ajc$this_, final SOAPFaultNode node) throws SOAPProcessingException {
        throw new UnsupportedOperationException("SOAP 1.1 has no SOAP Fault Node");
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setRole(final AxiomSOAP11Fault ajc$this_, final SOAPFaultRole role) throws SOAPProcessingException {
        if (!(role instanceof AxiomSOAP11FaultRole)) {
            throw new SOAPProcessingException("Expecting SOAP 1.1 implementation of SOAP Fault Role. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP11FaultSupport.sequence, 2, (OMNode)role);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$setDetail(final AxiomSOAP11Fault ajc$this_, final SOAPFaultDetail detail) throws SOAPProcessingException {
        if (!(detail instanceof AxiomSOAP11FaultDetail)) {
            throw new SOAPProcessingException("Expecting SOAP 1.1 implementation of SOAP Fault Detail. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP11FaultSupport.sequence, 3, (OMNode)detail);
    }
    
    public static SOAPFaultCode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getCode(final AxiomSOAP11Fault ajc$this_) {
        return (SOAPFaultCode)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP11Constants.QNAME_FAULT_CODE);
    }
    
    public static SOAPFaultReason ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getReason(final AxiomSOAP11Fault ajc$this_) {
        return (SOAPFaultReason)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP11Constants.QNAME_FAULT_REASON);
    }
    
    public static SOAPFaultNode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getNode(final AxiomSOAP11Fault ajc$this_) {
        return null;
    }
    
    public static SOAPFaultRole ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getRole(final AxiomSOAP11Fault ajc$this_) {
        return (SOAPFaultRole)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP11Constants.QNAME_FAULT_ROLE);
    }
    
    public static SOAPFaultDetail ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Fault$getDetail(final AxiomSOAP11Fault ajc$this_) {
        return (SOAPFaultDetail)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP11Constants.QNAME_FAULT_DETAIL);
    }
    
    public static AxiomSOAP11FaultSupport aspectOf() {
        if (AxiomSOAP11FaultSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11FaultSupport", AxiomSOAP11FaultSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11FaultSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11FaultSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11FaultSupport();
    }
}
