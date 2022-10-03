package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreNSAwareAttributeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreNSAwareAttributeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreNSAwareAttributeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreNSAwareAttributeSupport$org_apache_axiom_core_CoreNSAwareAttribute$coreGetNodeType(final CoreNSAwareAttribute ajc$this_) {
        return NodeType.NS_AWARE_ATTRIBUTE;
    }
    
    public static CoreNSAwareAttributeSupport aspectOf() {
        if (CoreNSAwareAttributeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreNSAwareAttributeSupport", CoreNSAwareAttributeSupport.ajc$initFailureCause);
        }
        return CoreNSAwareAttributeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreNSAwareAttributeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreNSAwareAttributeSupport();
    }
}
