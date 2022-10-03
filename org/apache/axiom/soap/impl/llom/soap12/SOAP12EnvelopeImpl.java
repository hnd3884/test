package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Element;
import org.apache.axiom.soap.impl.common.AxiomSOAP12ElementSupport;
import org.apache.axiom.soap.impl.intf.SOAPHelper;
import org.apache.axiom.soap.impl.common.AxiomSOAP12EnvelopeSupport;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.soap.impl.intf.AxiomSOAP12Envelope;
import org.apache.axiom.soap.impl.llom.SOAPEnvelopeImpl;

public class SOAP12EnvelopeImpl extends SOAPEnvelopeImpl implements AxiomSOAP12Envelope
{
    public final Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAP12EnvelopeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Envelope$coreGetNodeClass(this);
    }
    
    public final SOAPHelper getSOAPHelper() {
        return AxiomSOAP12ElementSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12ElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Element$getSOAPHelper(this);
    }
    
    public final void internalCheckChild(final OMNode child) {
        AxiomSOAP12EnvelopeSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAP12EnvelopeSupport$org_apache_axiom_soap_impl_intf_AxiomSOAP12Envelope$internalCheckChild(this, child);
    }
}
