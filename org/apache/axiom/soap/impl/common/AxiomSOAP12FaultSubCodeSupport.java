package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultSubCode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultSubCodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultSubCodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultSubCodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSubCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultSubCode$coreGetNodeClass(final AxiomSOAP12FaultSubCode ajc$this_) {
        return AxiomSOAP12FaultSubCode.class;
    }
    
    public static AxiomSOAP12FaultSubCodeSupport aspectOf() {
        if (AxiomSOAP12FaultSubCodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultSubCodeSupport", AxiomSOAP12FaultSubCodeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultSubCodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultSubCodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultSubCodeSupport();
    }
}
