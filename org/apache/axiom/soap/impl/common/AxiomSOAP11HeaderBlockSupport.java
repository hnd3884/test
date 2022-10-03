package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11HeaderBlock;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11HeaderBlockSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11HeaderBlockSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11HeaderBlockSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11HeaderBlock$coreGetNodeClass(final AxiomSOAP11HeaderBlock ajc$this_) {
        return AxiomSOAP11HeaderBlock.class;
    }
    
    public static AxiomSOAP11HeaderBlockSupport aspectOf() {
        if (AxiomSOAP11HeaderBlockSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11HeaderBlockSupport", AxiomSOAP11HeaderBlockSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11HeaderBlockSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11HeaderBlockSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11HeaderBlockSupport();
    }
}
