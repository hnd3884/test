package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11ElementSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11ElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11ElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static SOAPHelper ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(final AxiomSOAP11Element ajc$this_) {
        return SOAPHelper.SOAP11;
    }
    
    public static AxiomSOAP11ElementSupport aspectOf() {
        if (AxiomSOAP11ElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport", AxiomSOAP11ElementSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11ElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11ElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11ElementSupport();
    }
}
