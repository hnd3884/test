package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreCharacterDataContainingParentNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreCharacterDataContainingParentNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreCharacterDataContainingParentNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Object ajc$interMethod$org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport$org_apache_axiom_core_CoreCharacterDataContainingParentNode$coreGetCharacterData(final CoreCharacterDataContainingParentNode ajc$this_) {
        final Object characterData = CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$internalGetCharacterData(ajc$this_, ElementAction.RETURN_NULL);
        if (characterData == null) {
            throw new IllegalStateException();
        }
        return characterData;
    }
    
    public static CoreCharacterDataContainingParentNodeSupport aspectOf() {
        if (CoreCharacterDataContainingParentNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreCharacterDataContainingParentNodeSupport", CoreCharacterDataContainingParentNodeSupport.ajc$initFailureCause);
        }
        return CoreCharacterDataContainingParentNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreCharacterDataContainingParentNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreCharacterDataContainingParentNodeSupport();
    }
}
