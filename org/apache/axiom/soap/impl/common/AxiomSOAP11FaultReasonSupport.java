package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultReason;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11FaultReasonSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11FaultReasonSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11FaultReasonSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultReasonSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultReason$coreGetNodeClass(final AxiomSOAP11FaultReason ajc$this_) {
        return AxiomSOAP11FaultReason.class;
    }
    
    public static AxiomSOAP11FaultReasonSupport aspectOf() {
        if (AxiomSOAP11FaultReasonSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11FaultReasonSupport", AxiomSOAP11FaultReasonSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11FaultReasonSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11FaultReasonSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11FaultReasonSupport();
    }
}
