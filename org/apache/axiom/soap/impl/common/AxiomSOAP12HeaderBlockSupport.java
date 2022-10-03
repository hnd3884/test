package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12HeaderBlock;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12HeaderBlockSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12HeaderBlockSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12HeaderBlockSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderBlockSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12HeaderBlock$coreGetNodeClass(final AxiomSOAP12HeaderBlock ajc$this_) {
        return AxiomSOAP12HeaderBlock.class;
    }
    
    public static AxiomSOAP12HeaderBlockSupport aspectOf() {
        if (AxiomSOAP12HeaderBlockSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12HeaderBlockSupport", AxiomSOAP12HeaderBlockSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12HeaderBlockSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12HeaderBlockSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12HeaderBlockSupport();
    }
}
