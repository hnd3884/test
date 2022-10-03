package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultDetail;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultDetailSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultDetailSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultDetailSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultDetailSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultDetail$coreGetNodeClass(final AxiomSOAP12FaultDetail ajc$this_) {
        return AxiomSOAP12FaultDetail.class;
    }
    
    public static AxiomSOAP12FaultDetailSupport aspectOf() {
        if (AxiomSOAP12FaultDetailSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultDetailSupport", AxiomSOAP12FaultDetailSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultDetailSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultDetailSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultDetailSupport();
    }
}
