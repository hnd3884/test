package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Envelope;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP11EnvelopeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP11EnvelopeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP11EnvelopeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Envelope$coreGetNodeClass(final AxiomSOAP11Envelope ajc$this_) {
        return AxiomSOAP11Envelope.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Envelope$internalCheckChild(final AxiomSOAP11Envelope ajc$this_, final OMNode child) {
    }
    
    public static AxiomSOAP11EnvelopeSupport aspectOf() {
        if (AxiomSOAP11EnvelopeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP11EnvelopeSupport", AxiomSOAP11EnvelopeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP11EnvelopeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP11EnvelopeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP11EnvelopeSupport();
    }
}
