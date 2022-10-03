package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultValue;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultValueSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultValueSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultValueSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultValueSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultValue$coreGetNodeClass(final AxiomSOAP12FaultValue ajc$this_) {
        return AxiomSOAP12FaultValue.class;
    }
    
    public static AxiomSOAP12FaultValueSupport aspectOf() {
        if (AxiomSOAP12FaultValueSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultValueSupport", AxiomSOAP12FaultValueSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultValueSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultValueSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultValueSupport();
    }
}
