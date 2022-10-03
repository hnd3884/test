package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Header;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12HeaderSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12HeaderSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12HeaderSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Header$coreGetNodeClass(final AxiomSOAP12Header ajc$this_) {
        return AxiomSOAP12Header.class;
    }
    
    public static AxiomSOAP12HeaderSupport aspectOf() {
        if (AxiomSOAP12HeaderSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderSupport", AxiomSOAP12HeaderSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12HeaderSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12HeaderSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12HeaderSupport();
    }
}
