package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreLeafNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreLeafNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreLeafNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreLeafNodeSupport$org_apache_axiom_core_CoreLeafNode$cloneChildrenIfNecessary(final CoreLeafNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode clone) {
    }
    
    public static CoreLeafNodeSupport aspectOf() {
        if (CoreLeafNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreLeafNodeSupport", CoreLeafNodeSupport.ajc$initFailureCause);
        }
        return CoreLeafNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreLeafNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreLeafNodeSupport();
    }
}
