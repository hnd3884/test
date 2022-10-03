package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreCDATASectionSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreCDATASectionSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreCDATASectionSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreCDATASectionSupport$org_apache_axiom_core_CoreCDATASection$coreGetNodeType(final CoreCDATASection ajc$this_) {
        return NodeType.CDATA_SECTION;
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreCDATASectionSupport$org_apache_axiom_core_CoreCDATASection$init(final CoreCDATASection ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
    }
    
    public static CoreCDATASectionSupport aspectOf() {
        if (CoreCDATASectionSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreCDATASectionSupport", CoreCDATASectionSupport.ajc$initFailureCause);
        }
        return CoreCDATASectionSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreCDATASectionSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreCDATASectionSupport();
    }
}
