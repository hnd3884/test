package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12FaultRole;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12FaultRoleSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12FaultRoleSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12FaultRoleSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12FaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12FaultRole$coreGetNodeClass(final AxiomSOAP12FaultRole ajc$this_) {
        return AxiomSOAP12FaultRole.class;
    }
    
    public static AxiomSOAP12FaultRoleSupport aspectOf() {
        if (AxiomSOAP12FaultRoleSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12FaultRoleSupport", AxiomSOAP12FaultRoleSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12FaultRoleSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12FaultRoleSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12FaultRoleSupport();
    }
}
