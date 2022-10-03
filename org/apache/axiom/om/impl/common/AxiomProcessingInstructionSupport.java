package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.Semantics;
import org.apache.axiom.om.impl.intf.AxiomProcessingInstruction;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomProcessingInstructionSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomProcessingInstructionSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomProcessingInstructionSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$getType(final AxiomProcessingInstruction ajc$this_) {
        return 3;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$setTarget(final AxiomProcessingInstruction ajc$this_, final String target) {
        ajc$this_.coreSetTarget(target);
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$getValue(final AxiomProcessingInstruction ajc$this_) {
        return ajc$this_.coreGetCharacterData().toString();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$setValue(final AxiomProcessingInstruction ajc$this_, final String value) {
        ajc$this_.coreSetCharacterData(value, AxiomSemantics.INSTANCE);
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$internalSerialize(final AxiomProcessingInstruction ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        serializer.writeProcessingInstruction(String.valueOf(ajc$this_.coreGetTarget()) + " ", ajc$this_.coreGetCharacterData().toString());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport$org_apache_axiom_om_impl_intf_AxiomProcessingInstruction$buildWithAttachments(final AxiomProcessingInstruction ajc$this_) {
    }
    
    public static AxiomProcessingInstructionSupport aspectOf() {
        if (AxiomProcessingInstructionSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomProcessingInstructionSupport", AxiomProcessingInstructionSupport.ajc$initFailureCause);
        }
        return AxiomProcessingInstructionSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomProcessingInstructionSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomProcessingInstructionSupport();
    }
}
