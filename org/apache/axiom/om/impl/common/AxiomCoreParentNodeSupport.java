package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomCoreParentNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomCoreParentNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomCoreParentNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomCoreParentNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreParentNode$isComplete(final AxiomCoreParentNode ajc$this_) {
        final int state = ajc$this_.getState();
        return state == 0 || state == 3;
    }
    
    public static AxiomCoreParentNodeSupport aspectOf() {
        if (AxiomCoreParentNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomCoreParentNodeSupport", AxiomCoreParentNodeSupport.ajc$initFailureCause);
        }
        return AxiomCoreParentNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomCoreParentNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomCoreParentNodeSupport();
    }
}
