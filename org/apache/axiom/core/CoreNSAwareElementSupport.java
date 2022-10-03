package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreNSAwareElementSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreNSAwareElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreNSAwareElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$coreGetNodeType(final CoreNSAwareElement ajc$this_) {
        return NodeType.NS_AWARE_ELEMENT;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$getImplicitNamespaceURI(final CoreNSAwareElement ajc$this_, final String prefix) {
        return prefix.equals(ajc$this_.coreGetPrefix()) ? ajc$this_.coreGetNamespaceURI() : null;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreNSAwareElementSupport$org_apache_axiom_core_CoreNSAwareElement$getImplicitPrefix(final CoreNSAwareElement ajc$this_, final String namespaceURI) {
        return namespaceURI.equals(ajc$this_.coreGetNamespaceURI()) ? ajc$this_.coreGetPrefix() : null;
    }
    
    public static CoreNSAwareElementSupport aspectOf() {
        if (CoreNSAwareElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreNSAwareElementSupport", CoreNSAwareElementSupport.ajc$initFailureCause);
        }
        return CoreNSAwareElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreNSAwareElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreNSAwareElementSupport();
    }
}
