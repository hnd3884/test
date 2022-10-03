package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11FaultRole;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11FaultRoleSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11FaultRoleSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11FaultRoleSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11FaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11FaultRole$coreGetNodeClass(final AxiomSOAP11FaultRole ajc$this_) {
        return AxiomSOAP11FaultRole.class;
    }
    
    public static AxiomSOAP11FaultRoleSupport aspectOf() {
        if (AxiomSOAP11FaultRoleSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11FaultRoleSupport", AxiomSOAP11FaultRoleSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11FaultRoleSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11FaultRoleSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11FaultRoleSupport();
    }
}
