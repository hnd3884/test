package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Header;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11HeaderSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11HeaderSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11HeaderSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Header$coreGetNodeClass(final AxiomSOAP11Header ajc$this_) {
        return AxiomSOAP11Header.class;
    }
    
    public static AxiomSOAP11HeaderSupport aspectOf() {
        if (AxiomSOAP11HeaderSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderSupport", AxiomSOAP11HeaderSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11HeaderSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11HeaderSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11HeaderSupport();
    }
}
