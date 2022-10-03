package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreMixedContentContainerSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreMixedContentContainerSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreMixedContentContainerSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Object ajc$interMethod$org_apache_axiom_core_CoreMixedContentContainerSupport$org_apache_axiom_core_CoreMixedContentContainer$coreGetCharacterData(final CoreMixedContentContainer ajc$this_, final ElementAction elementAction) {
        return CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$internalGetCharacterData(ajc$this_, elementAction);
    }
    
    public static CoreMixedContentContainerSupport aspectOf() {
        if (CoreMixedContentContainerSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreMixedContentContainerSupport", CoreMixedContentContainerSupport.ajc$initFailureCause);
        }
        return CoreMixedContentContainerSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreMixedContentContainerSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreMixedContentContainerSupport();
    }
}
