package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.intf.AxiomCharacterDataNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomCharacterDataNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomCharacterDataNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomCharacterDataNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCharacterDataNodeSupport$org_apache_axiom_om_impl_intf_AxiomCharacterDataNode$getType(final AxiomCharacterDataNode ajc$this_) {
        return ajc$this_.coreIsIgnorable() ? 6 : 4;
    }
    
    public static AxiomCharacterDataNodeSupport aspectOf() {
        if (AxiomCharacterDataNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomCharacterDataNodeSupport", AxiomCharacterDataNodeSupport.ajc$initFailureCause);
        }
        return AxiomCharacterDataNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomCharacterDataNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomCharacterDataNodeSupport();
    }
}
