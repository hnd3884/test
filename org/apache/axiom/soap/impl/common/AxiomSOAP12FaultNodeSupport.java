package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultNodeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultNode$coreGetNodeClass(final AxiomSOAP12FaultNode ajc$this_) {
        return AxiomSOAP12FaultNode.class;
    }
    
    public static AxiomSOAP12FaultNodeSupport aspectOf() {
        if (AxiomSOAP12FaultNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultNodeSupport", AxiomSOAP12FaultNodeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultNodeSupport();
    }
}
