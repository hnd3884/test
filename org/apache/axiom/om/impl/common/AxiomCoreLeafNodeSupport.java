package org.apache.axiom.om.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.intf.AxiomCoreLeafNode;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomCoreLeafNodeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomCoreLeafNodeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomCoreLeafNodeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMXMLParserWrapper ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$getBuilder(final AxiomCoreLeafNode ajc$this_) {
        return null;
    }
    
    public static boolean ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$isComplete(final AxiomCoreLeafNode ajc$this_) {
        return true;
    }
    
    public static void ajc$interMethod$org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport$org_apache_axiom_om_impl_intf_AxiomCoreLeafNode$build(final AxiomCoreLeafNode ajc$this_) {
    }
    
    public static AxiomCoreLeafNodeSupport aspectOf() {
        if (AxiomCoreLeafNodeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_om_impl_common_AxiomCoreLeafNodeSupport", AxiomCoreLeafNodeSupport.ajc$initFailureCause);
        }
        return AxiomCoreLeafNodeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomCoreLeafNodeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomCoreLeafNodeSupport();
    }
}
