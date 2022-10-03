package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.intf.AxiomEntityReference;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomEntityReferenceSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomEntityReferenceSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomEntityReferenceSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getType(final AxiomEntityReference ajc$this_) {
        return 9;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$internalSerialize(final AxiomEntityReference ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        serializer.writeEntityRef(ajc$this_.coreGetName());
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getName(final AxiomEntityReference ajc$this_) {
        return ajc$this_.coreGetName();
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$getReplacementText(final AxiomEntityReference ajc$this_) {
        return ajc$this_.coreGetReplacementText();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport$org_apache_axiom_om_impl_intf_AxiomEntityReference$buildWithAttachments(final AxiomEntityReference ajc$this_) {
    }
    
    public static AxiomEntityReferenceSupport aspectOf() {
        if (AxiomEntityReferenceSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomEntityReferenceSupport", AxiomEntityReferenceSupport.ajc$initFailureCause);
        }
        return AxiomEntityReferenceSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomEntityReferenceSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomEntityReferenceSupport();
    }
}
