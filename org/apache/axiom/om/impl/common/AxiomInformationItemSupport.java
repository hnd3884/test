package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.impl.intf.AxiomInformationItem;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomInformationItemSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomInformationItemSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomInformationItemSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMFactory ajc$interMethod$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$getOMFactory(final AxiomInformationItem ajc$this_) {
        return ajc$this_.getMetaFactory().getOMFactory();
    }
    
    public static OMInformationItem ajc$interMethod$org_apache_axiom_om_impl_common_AxiomInformationItemSupport$org_apache_axiom_om_impl_intf_AxiomInformationItem$clone(final AxiomInformationItem ajc$this_, final OMCloneOptions options) {
        return (OMInformationItem)ajc$this_.coreClone(AxiomSemantics.CLONE_POLICY, options);
    }
    
    public static AxiomInformationItemSupport aspectOf() {
        if (AxiomInformationItemSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomInformationItemSupport", AxiomInformationItemSupport.ajc$initFailureCause);
        }
        return AxiomInformationItemSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomInformationItemSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomInformationItemSupport();
    }
}
