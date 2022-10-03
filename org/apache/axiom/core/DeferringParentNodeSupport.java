package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class DeferringParentNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ DeferringParentNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            DeferringParentNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$builder(final DeferringParentNode ajc$this_) {
    }
    
    public static OMXMLParserWrapper ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(final DeferringParentNode ajc$this_) {
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$forceExpand(ajc$this_);
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$builder();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$coreSetBuilder(final DeferringParentNode ajc$this_, final OMXMLParserWrapper builder) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$builder(builder);
        CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreSetState(ajc$this_, (builder != null) ? 1 : 0);
    }
    
    public static DeferringParentNodeSupport aspectOf() {
        if (DeferringParentNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_DeferringParentNodeSupport", DeferringParentNodeSupport.ajc$initFailureCause);
        }
        return DeferringParentNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return DeferringParentNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new DeferringParentNodeSupport();
    }
}
