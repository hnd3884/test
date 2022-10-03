package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.soap.SOAP12Constants;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultDetail;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultRole;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultReason;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultCode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Fault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultCode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultSupport
{
    private static final Class<?>[] sequence;
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultSupport ajc$perSingletonInstance;
    
    static {
        try {
            sequence = new Class[] { SOAPFaultCode.class, SOAPFaultReason.class, SOAPFaultNode.class, SOAPFaultRole.class, SOAPFaultDetail.class };
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$coreGetNodeClass(final AxiomSOAP12Fault ajc$this_) {
        return AxiomSOAP12Fault.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setCode(final AxiomSOAP12Fault ajc$this_, final SOAPFaultCode soapFaultCode) throws SOAPProcessingException {
        if (!(soapFaultCode instanceof AxiomSOAP12FaultCode)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Fault Code. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultSupport.sequence, 0, (OMNode)soapFaultCode);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setReason(final AxiomSOAP12Fault ajc$this_, final SOAPFaultReason reason) throws SOAPProcessingException {
        if (!(reason instanceof AxiomSOAP12FaultReason)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Fault Reason. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultSupport.sequence, 1, (OMNode)reason);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setNode(final AxiomSOAP12Fault ajc$this_, final SOAPFaultNode node) throws SOAPProcessingException {
        if (!(node instanceof AxiomSOAP12FaultNode)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Fault Node. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultSupport.sequence, 2, (OMNode)node);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setRole(final AxiomSOAP12Fault ajc$this_, final SOAPFaultRole role) throws SOAPProcessingException {
        if (!(role instanceof AxiomSOAP12FaultRole)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Fault Role. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultSupport.sequence, 3, (OMNode)role);
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$setDetail(final AxiomSOAP12Fault ajc$this_, final SOAPFaultDetail detail) throws SOAPProcessingException {
        if (!(detail instanceof AxiomSOAP12FaultDetail)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP Fault Detail. But received some other implementation");
        }
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$insertChild(ajc$this_, AxiomSOAP12FaultSupport.sequence, 4, (OMNode)detail);
    }
    
    public static SOAPFaultCode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getCode(final AxiomSOAP12Fault ajc$this_) {
        return (SOAPFaultCode)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP12Constants.QNAME_FAULT_CODE);
    }
    
    public static SOAPFaultReason ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getReason(final AxiomSOAP12Fault ajc$this_) {
        return (SOAPFaultReason)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP12Constants.QNAME_FAULT_REASON);
    }
    
    public static SOAPFaultNode ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getNode(final AxiomSOAP12Fault ajc$this_) {
        return (SOAPFaultNode)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP12Constants.QNAME_FAULT_NODE);
    }
    
    public static SOAPFaultRole ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getRole(final AxiomSOAP12Fault ajc$this_) {
        return (SOAPFaultRole)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP12Constants.QNAME_FAULT_ROLE);
    }
    
    public static SOAPFaultDetail ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Fault$getDetail(final AxiomSOAP12Fault ajc$this_) {
        return (SOAPFaultDetail)AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$getFirstChildWithName(ajc$this_, SOAP12Constants.QNAME_FAULT_DETAIL);
    }
    
    public static AxiomSOAP12FaultSupport aspectOf() {
        if (AxiomSOAP12FaultSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSupport", AxiomSOAP12FaultSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultSupport();
    }
}
