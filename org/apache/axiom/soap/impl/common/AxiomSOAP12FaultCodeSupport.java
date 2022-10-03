package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultCode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultCodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultCodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultCodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultCodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultCode$coreGetNodeClass(final AxiomSOAP12FaultCode ajc$this_) {
        return AxiomSOAP12FaultCode.class;
    }
    
    public static AxiomSOAP12FaultCodeSupport aspectOf() {
        if (AxiomSOAP12FaultCodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultCodeSupport", AxiomSOAP12FaultCodeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultCodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultCodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultCodeSupport();
    }
}
