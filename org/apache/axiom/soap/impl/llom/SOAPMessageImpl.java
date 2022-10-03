package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.core.ClonePolicy;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.core.CoreNode;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.impl.intf.AxiomDocument;
import org.apache.axiom.om.impl.common.AxiomDocumentSupport;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.impl.common.AxiomSOAPMessageSupport;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.impl.intf.AxiomSOAPMessage;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;

public class SOAPMessageImpl extends OMDocumentImpl implements AxiomSOAPMessage
{
    public SOAPFactory factory;
    
    public SOAPMessageImpl() {
        AxiomSOAPMessageSupport.ajc$interFieldInit$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$factory(this);
    }
    
    public SOAPEnvelope getSOAPEnvelope() throws SOAPProcessingException {
        return (SOAPEnvelope)AxiomDocumentSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$getOMDocumentElement(this);
    }
    
    public void setSOAPEnvelope(final SOAPEnvelope envelope) {
        AxiomDocumentSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomDocumentSupport$org_apache_axiom_om_impl_intf_AxiomDocument$setOMDocumentElement(this, (OMElement)envelope);
    }
    
    @Override
    public void checkDocumentElement(final OMElement element) {
        if (!(element instanceof SOAPEnvelope)) {
            throw new OMException("Child not allowed; must be a SOAPEnvelope");
        }
    }
    
    public Class<? extends CoreNode> coreGetNodeClass() {
        return AxiomSOAPMessageSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$coreGetNodeClass(this);
    }
    
    public final OMFactory getOMFactory() {
        return AxiomSOAPMessageSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$getOMFactory(this);
    }
    
    public final <T> void initAncillaryData(final ClonePolicy<T> policy, final T options, final CoreNode other) {
        AxiomSOAPMessageSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initAncillaryData(this, policy, options, other);
    }
    
    public final void initSOAPFactory(final SOAPFactory factory) {
        AxiomSOAPMessageSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$initSOAPFactory(this, factory);
    }
    
    @Override
    public final void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache, final boolean includeXMLDeclaration) throws OutputException {
        AxiomSOAPMessageSupport.ajc$interMethod$org_apache_axiom_soap_impl_common_AxiomSOAPMessageSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPMessage$internalSerialize(this, serializer, format, cache, includeXMLDeclaration);
    }
}
