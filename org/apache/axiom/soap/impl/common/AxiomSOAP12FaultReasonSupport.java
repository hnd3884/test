package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultReason;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultReasonSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultReasonSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultReasonSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultReason$coreGetNodeClass(final AxiomSOAP12FaultReason ajc$this_) {
        return AxiomSOAP12FaultReason.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultReason$addSOAPText(final AxiomSOAP12FaultReason ajc$this_, final SOAPFaultText soapFaultText) {
        AxiomContainerSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(ajc$this_, (OMNode)soapFaultText);
    }
    
    public static AxiomSOAP12FaultReasonSupport aspectOf() {
        if (AxiomSOAP12FaultReasonSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultReasonSupport", AxiomSOAP12FaultReasonSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultReasonSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultReasonSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultReasonSupport();
    }
}
