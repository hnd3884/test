package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreCommentSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreCommentSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreCommentSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreCommentSupport$org_apache_axiom_core_CoreComment$coreGetNodeType(final CoreComment ajc$this_) {
        return NodeType.COMMENT;
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreCommentSupport$org_apache_axiom_core_CoreComment$init(final CoreComment ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
    }
    
    public static CoreCommentSupport aspectOf() {
        if (CoreCommentSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreCommentSupport", CoreCommentSupport.ajc$initFailureCause);
        }
        return CoreCommentSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreCommentSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreCommentSupport();
    }
}
