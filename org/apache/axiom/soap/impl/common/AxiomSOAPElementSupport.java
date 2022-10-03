package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPElementSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static OMFactory ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(final AxiomSOAPElement ajc$this_) {
        return (OMFactory)ajc$this_.getSOAPHelper().getSOAPFactory(ajc$this_.getMetaFactory());
    }
    
    public static AxiomSOAPElementSupport aspectOf() {
        if (AxiomSOAPElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport", AxiomSOAPElementSupport.ajc$initFailureCause);
        }
        return AxiomSOAPElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPElementSupport();
    }
}
