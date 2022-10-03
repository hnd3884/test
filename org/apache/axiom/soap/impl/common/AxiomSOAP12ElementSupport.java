package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12ElementSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12ElementSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12ElementSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static SOAPHelper ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(final AxiomSOAP12Element ajc$this_) {
        return SOAPHelper.SOAP12;
    }
    
    public static AxiomSOAP12ElementSupport aspectOf() {
        if (AxiomSOAP12ElementSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport", AxiomSOAP12ElementSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12ElementSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12ElementSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12ElementSupport();
    }
}
