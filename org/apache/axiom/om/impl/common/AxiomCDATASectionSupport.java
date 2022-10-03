package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomCDATASection;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomCDATASectionSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomCDATASectionSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomCDATASectionSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCDATASectionSupport$org_apache_axiom_om_impl_intf_AxiomCDATASection$getType(final AxiomCDATASection ajc$this_) {
        return 12;
    }
    
    public static AxiomCDATASectionSupport aspectOf() {
        if (AxiomCDATASectionSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomCDATASectionSupport", AxiomCDATASectionSupport.ajc$initFailureCause);
        }
        return AxiomCDATASectionSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomCDATASectionSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomCDATASectionSupport();
    }
}
