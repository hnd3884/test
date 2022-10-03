package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreCharacterDataNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreCharacterDataNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreCharacterDataNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data(final CoreCharacterDataNode ajc$this_) {
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreGetNodeType(final CoreCharacterDataNode ajc$this_) {
        return NodeType.CHARACTER_DATA;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreIsIgnorable(final CoreCharacterDataNode ajc$this_) {
        return CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$getFlag(ajc$this_, 16);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetIgnorable(final CoreCharacterDataNode ajc$this_, final boolean ignorable) {
        CoreNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreNodeSupport$org_apache_axiom_core_CoreNode$setFlag(ajc$this_, 16, ignorable);
    }
    
    public static Object ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreGetCharacterData(final CoreCharacterDataNode ajc$this_) {
        return (ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data() == null) ? "" : ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetCharacterData(final CoreCharacterDataNode ajc$this_, final Object data) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data(data);
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$coreSetCharacterData(final CoreCharacterDataNode ajc$this_, final Object data, final Semantics semantics) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data(data);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$init(final CoreCharacterDataNode ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        final CoreCharacterDataNode o = (CoreCharacterDataNode)other;
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data((o.ajc$interFieldGet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data() instanceof CharacterData) ? ((CharacterData)o.ajc$interFieldGet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data()).clone(policy, options) : o.ajc$interFieldGet$org_apache_axiom_core_CoreCharacterDataNodeSupport$org_apache_axiom_core_CoreCharacterDataNode$data());
        ajc$this_.coreSetIgnorable(o.coreIsIgnorable());
    }
    
    public static CoreCharacterDataNodeSupport aspectOf() {
        if (CoreCharacterDataNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreCharacterDataNodeSupport", CoreCharacterDataNodeSupport.ajc$initFailureCause);
        }
        return CoreCharacterDataNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreCharacterDataNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreCharacterDataNodeSupport();
    }
}
