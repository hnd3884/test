package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP11ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP11EnvelopeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP11Envelope;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;

public class SOAP11EnvelopeImpl extends SOAPEnvelopeImpl implements AxiomSOAP11Envelope
{
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP11EnvelopeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Envelope$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP11ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Element$getSOAPHelper(this);
    }
    
    public final void internalCheckChild(final OMNode child) {
        AxiomSOAP11EnvelopeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP11EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP11Envelope$internalCheckChild(this, child);
    }
}
