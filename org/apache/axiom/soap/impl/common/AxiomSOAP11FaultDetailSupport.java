package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultDetail;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11FaultDetailSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11FaultDetailSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11FaultDetailSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultDetail$coreGetNodeClass(final AxiomSOAP11FaultDetail ajc$this_) {
        return AxiomSOAP11FaultDetail.class;
    }
    
    public static AxiomSOAP11FaultDetailSupport aspectOf() {
        if (AxiomSOAP11FaultDetailSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11FaultDetailSupport", AxiomSOAP11FaultDetailSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11FaultDetailSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11FaultDetailSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11FaultDetailSupport();
    }
}
