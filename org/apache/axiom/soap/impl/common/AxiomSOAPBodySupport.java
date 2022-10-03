package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.impl.intf.AxiomSOAPBody;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAPBodySupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAPBodySupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAPBodySupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static SOAPFault ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPBodySupport$org_apache_axiom_soap_impl_intf_AxiomSOAPBody$addFault(final AxiomSOAPBody ajc$this_, final Exception e) throws OMException {
        return ((SOAPFactory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(ajc$this_)).createSOAPFault((SOAPBody)ajc$this_, e);
    }
    
    public static AxiomSOAPBodySupport aspectOf() {
        if (AxiomSOAPBodySupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAPBodySupport", AxiomSOAPBodySupport.ajc$initFailureCause);
        }
        return AxiomSOAPBodySupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAPBodySupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAPBodySupport();
    }
}
