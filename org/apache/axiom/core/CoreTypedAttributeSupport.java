package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreTypedAttributeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreTypedAttributeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreTypedAttributeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$type(final CoreTypedAttribute ajc$this_) {
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$coreGetType(final CoreTypedAttribute ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$type();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$coreSetType(final CoreTypedAttribute ajc$this_, final String type) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$type(type);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreTypedAttributeSupport$org_apache_axiom_core_CoreTypedAttribute$init(final CoreTypedAttribute ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreTypedAttribute o = (CoreTypedAttribute)other;
        ajc$this_.initName(o);
        ajc$this_.coreSetType(o.coreGetType());
    }
    
    public static CoreTypedAttributeSupport aspectOf() {
        if (CoreTypedAttributeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreTypedAttributeSupport", CoreTypedAttributeSupport.ajc$initFailureCause);
        }
        return CoreTypedAttributeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreTypedAttributeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreTypedAttributeSupport();
    }
}
