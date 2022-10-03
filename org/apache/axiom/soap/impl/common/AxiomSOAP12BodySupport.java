package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Body;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12BodySupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12BodySupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12BodySupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12BodySupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Body$coreGetNodeClass(final AxiomSOAP12Body ajc$this_) {
        return AxiomSOAP12Body.class;
    }
    
    public static AxiomSOAP12BodySupport aspectOf() {
        if (AxiomSOAP12BodySupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12BodySupport", AxiomSOAP12BodySupport.ajc$initFailureCause);
        }
        return AxiomSOAP12BodySupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12BodySupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12BodySupport();
    }
}
