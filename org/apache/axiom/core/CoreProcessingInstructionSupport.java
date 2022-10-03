package org.apache.axiom.core;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class CoreProcessingInstructionSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ CoreProcessingInstructionSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            CoreProcessingInstructionSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static void ajc$interFieldInit$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$target(final CoreProcessingInstruction ajc$this_) {
    }
    
    public static NodeType ajc$interMethod$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$coreGetNodeType(final CoreProcessingInstruction ajc$this_) {
        return NodeType.PROCESSING_INSTRUCTION;
    }
    
    public static String ajc$interMethod$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$coreGetTarget(final CoreProcessingInstruction ajc$this_) {
        return ajc$this_.ajc$interFieldGet$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$target();
    }
    
    public static void ajc$interMethod$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$coreSetTarget(final CoreProcessingInstruction ajc$this_, final String target) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$target(target);
    }
    
    public static <T> void ajc$interMethod$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$init(final CoreProcessingInstruction ajc$this_, final ClonePolicy<T> policy, final T options, final CoreNode other) {
        ajc$this_.ajc$interFieldSet$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$target(((CoreProcessingInstruction)other).ajc$interFieldGet$org_apache_axiom_core_CoreProcessingInstructionSupport$org_apache_axiom_core_CoreProcessingInstruction$target());
    }
    
    public static CoreProcessingInstructionSupport aspectOf() {
        if (CoreProcessingInstructionSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_core_CoreProcessingInstructionSupport", CoreProcessingInstructionSupport.ajc$initFailureCause);
        }
        return CoreProcessingInstructionSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return CoreProcessingInstructionSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new CoreProcessingInstructionSupport();
    }
}
