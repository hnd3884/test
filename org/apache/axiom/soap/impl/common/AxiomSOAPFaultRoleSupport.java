package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.impl.intf.AxiomSOAPFaultRole;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPFaultRoleSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPFaultRoleSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPFaultRoleSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultRole$setRoleValue(final AxiomSOAPFaultRole ajc$this_, final String uri) {
        AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$setText(ajc$this_, uri);
    }
    
    public static String ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPFaultRoleSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPFaultRole$getRoleValue(final AxiomSOAPFaultRole ajc$this_) {
        return AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getText(ajc$this_);
    }
    
    public static AxiomSOAPFaultRoleSupport aspectOf() {
        if (AxiomSOAPFaultRoleSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPFaultRoleSupport", AxiomSOAPFaultRoleSupport.ajc$initFailureCause);
        }
        return AxiomSOAPFaultRoleSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPFaultRoleSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPFaultRoleSupport();
    }
}
