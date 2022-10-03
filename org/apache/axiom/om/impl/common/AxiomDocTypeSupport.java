package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.om.impl.intf.AxiomDocType;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomDocTypeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomDocTypeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomDocTypeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static int ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$getType(final AxiomDocType ajc$this_) {
        return 11;
    }
    
    public static String ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$getRootName(final AxiomDocType ajc$this_) {
        return ajc$this_.coreGetRootName();
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$internalSerialize(final AxiomDocType ajc$this_, final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        serializer.writeDTD(ajc$this_.coreGetRootName(), ajc$this_.coreGetPublicId(), ajc$this_.coreGetSystemId(), ajc$this_.coreGetInternalSubset());
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomDocTypeSupport$org_apache_axiom_om_impl_intf_AxiomDocType$buildWithAttachments(final AxiomDocType ajc$this_) {
    }
    
    public static AxiomDocTypeSupport aspectOf() {
        if (AxiomDocTypeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomDocTypeSupport", AxiomDocTypeSupport.ajc$initFailureCause);
        }
        return AxiomDocTypeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomDocTypeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomDocTypeSupport();
    }
}
