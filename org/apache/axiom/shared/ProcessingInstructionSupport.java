package org.apache.axiom.shared;

import org.aspectj.lang.NoAspectBoundException;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ProcessingInstructionSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ ProcessingInstructionSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            ProcessingInstructionSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static String ajc$interMethod$org_apache_axiom_shared_ProcessingInstructionSupport$org_apache_axiom_shared_IProcessingInstruction$getTarget(final IProcessingInstruction ajc$this_) {
        return ajc$this_.coreGetTarget();
    }
    
    public static ProcessingInstructionSupport aspectOf() {
        if (ProcessingInstructionSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_shared_ProcessingInstructionSupport", ProcessingInstructionSupport.ajc$initFailureCause);
        }
        return ProcessingInstructionSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return ProcessingInstructionSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new ProcessingInstructionSupport();
    }
}
