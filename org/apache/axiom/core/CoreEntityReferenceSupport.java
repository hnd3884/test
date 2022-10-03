package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreEntityReferenceSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreEntityReferenceSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreEntityReferenceSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$name(final CoreEntityReference ajc$this_) {
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$replacementText(final CoreEntityReference ajc$this_) {
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetNodeType(final CoreEntityReference ajc$this_) {
        return NodeType.ENTITY_REFERENCE;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetName(final CoreEntityReference ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$name();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreSetName(final CoreEntityReference ajc$this_, final String name) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$name(name);
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreGetReplacementText(final CoreEntityReference ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$replacementText();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$coreSetReplacementText(final CoreEntityReference ajc$this_, final String replacementText) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$replacementText(replacementText);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreEntityReferenceSupport$org_apache_axiom_core_CoreEntityReference$init(final CoreEntityReference ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreEntityReference o = (CoreEntityReference)other;
        ajc$this_.coreSetName(o.coreGetName());
        ajc$this_.coreSetReplacementText(o.coreGetReplacementText());
    }
    
    public static CoreEntityReferenceSupport aspectOf() {
        if (CoreEntityReferenceSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreEntityReferenceSupport", CoreEntityReferenceSupport.ajc$initFailureCause);
        }
        return CoreEntityReferenceSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreEntityReferenceSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreEntityReferenceSupport();
    }
}
