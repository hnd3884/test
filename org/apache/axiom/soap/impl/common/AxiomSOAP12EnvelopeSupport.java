package org.apache.axiom.soap.impl.common;

import org.aspectj.lang.NoAspectBoundException;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Envelope;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class AxiomSOAP12EnvelopeSupport
{
    private static /* synthetic */ Throwable ajc$initFailureCause;
    public static final /* synthetic */ AxiomSOAP12EnvelopeSupport ajc$perSingletonInstance;
    
    static {
        try {
            ajc$postClinit();
        }
        catch (final Throwable ajc$initFailureCause) {
            AxiomSOAP12EnvelopeSupport.ajc$initFailureCause = ajc$initFailureCause;
        }
    }
    
    public static Class<? extends CoreNode> ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Envelope$coreGetNodeClass(final AxiomSOAP12Envelope ajc$this_) {
        return AxiomSOAP12Envelope.class;
    }
    
    public static void ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Envelope$internalCheckChild(final AxiomSOAP12Envelope ajc$this_, final OMNode child) {
        if (child instanceof OMElement && !(child instanceof SOAPHeader) && !(child instanceof SOAPBody)) {
            throw new SOAPProcessingException("SOAP Envelope can not have children other than SOAP Header and Body", "Sender");
        }
    }
    
    public static AxiomSOAP12EnvelopeSupport aspectOf() {
        if (AxiomSOAP12EnvelopeSupport.ajc$perSingletonInstance == null) {
            throw new NoAspectBoundException("org_apache_axiom_soap_impl_common_AxiomSOAP12EnvelopeSupport", AxiomSOAP12EnvelopeSupport.ajc$initFailureCause);
        }
        return AxiomSOAP12EnvelopeSupport.ajc$perSingletonInstance;
    }
    
    public static boolean hasAspect() {
        return AxiomSOAP12EnvelopeSupport.ajc$perSingletonInstance != null;
    }
    
    private static /* synthetic */ void ajc$postClinit() {
        ajc$perSingletonInstance = new AxiomSOAP12EnvelopeSupport();
    }
}
