package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.om.OMNamespace;
import javax.xml.namespace.QName;
import org.apache.axiom.om.impl.common.serializer.push.OutputException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.core.DeferringParentNode;
import org.apache.axiom.core.DeferringParentNodeSupport;
import org.apache.axiom.om.OMOutputFormat;
import org.apache.axiom.om.impl.common.serializer.push.Serializer;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.impl.intf.AxiomContainer;
import org.apache.axiom.om.impl.common.AxiomContainerSupport;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.core.CoreParentNode;
import org.apache.axiom.core.CoreParentNodeSupport;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.intf.AxiomElement;
import org.apache.axiom.om.impl.common.AxiomElementSupport;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.impl.intf.AxiomSOAPElement;
import org.apache.axiom.soap.impl.common.AxiomSOAPElementSupport;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPVersion;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMConstants;
import org.apache.axiom.soap.impl.intf.AxiomSOAPEnvelope;

public abstract class SOAPEnvelopeImpl extends SOAPElement implements AxiomSOAPEnvelope, OMConstants
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog((Class)SOAPEnvelopeImpl.class);
    }
    
    public SOAPVersion getVersion() {
        return ((SOAPFactory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this)).getSOAPVersion();
    }
    
    public SOAPHeader getHeader() {
        final OMElement e = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        if (e instanceof SOAPHeader) {
            return (SOAPHeader)e;
        }
        return null;
    }
    
    public SOAPHeader getOrCreateHeader() {
        final SOAPHeader header = this.getHeader();
        return (header != null) ? header : ((SOAPFactory)AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this)).createSOAPHeader((SOAPEnvelope)this);
    }
    
    public void addChild(final OMNode child, final boolean fromBuilder) {
        this.internalCheckChild(child);
        if (child instanceof SOAPHeader) {
            if (CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$getState(this) == 0) {
                final SOAPBody body = this.getBody();
                if (body != null) {
                    body.insertSiblingBefore(child);
                    return;
                }
            }
            else {
                for (OMNode node = (OMNode)CoreParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_CoreParentNodeSupport$org_apache_axiom_core_CoreParentNode$coreGetLastKnownChild(this); node != null; node = node.getPreviousOMSibling()) {
                    if (node instanceof SOAPBody) {
                        node.insertSiblingBefore(child);
                        return;
                    }
                }
            }
        }
        AxiomContainerSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomContainerSupport$org_apache_axiom_om_impl_intf_AxiomContainer$addChild(this, child, fromBuilder);
    }
    
    public SOAPBody getBody() throws OMException {
        final OMElement element = AxiomElementSupport.ajc$interMethodDispatch1$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$getFirstElement(this);
        if (element == null) {
            return null;
        }
        if ("Body".equals(element.getLocalName())) {
            return (SOAPBody)element;
        }
        OMNode node;
        for (node = element.getNextOMSibling(); node != null && node.getType() != 1; node = node.getNextOMSibling()) {}
        if (node == null) {
            return null;
        }
        if ("Body".equals(((OMElement)node).getLocalName())) {
            return (SOAPBody)node;
        }
        throw new OMException("SOAPEnvelope must contain a body element which is either first or second child element of the SOAPEnvelope.");
    }
    
    public void checkParent(final OMElement parent) throws SOAPProcessingException {
    }
    
    public void internalSerialize(final Serializer serializer, final OMOutputFormat format, final boolean cache) throws OutputException {
        if (!format.isIgnoreXMLDeclaration()) {
            final String charSetEncoding = format.getCharSetEncoding();
            final String xmlVersion = format.getXmlVersion();
            serializer.writeStartDocument((charSetEncoding == null) ? "utf-8" : charSetEncoding, (xmlVersion == null) ? "1.0" : xmlVersion);
        }
        AxiomElementSupport.ajc$interMethod$org_apache_axiom_om_impl_common_AxiomElementSupport$org_apache_axiom_om_impl_intf_AxiomElement$internalSerialize(this, serializer, format, cache);
        serializer.writeEndDocument();
        if (!cache) {
            final OMXMLParserWrapper builder = DeferringParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(this);
            if (builder != null && builder instanceof StAXBuilder) {
                try {
                    if (SOAPEnvelopeImpl.log.isDebugEnabled()) {
                        SOAPEnvelopeImpl.log.debug((Object)("closing builder: " + builder));
                    }
                    final StAXBuilder staxBuilder = (StAXBuilder)builder;
                    staxBuilder.close();
                }
                catch (final Exception e) {
                    if (SOAPEnvelopeImpl.log.isDebugEnabled()) {
                        SOAPEnvelopeImpl.log.error((Object)"Could not close builder or parser due to: ", (Throwable)e);
                    }
                }
            }
            else if (SOAPEnvelopeImpl.log.isDebugEnabled()) {
                SOAPEnvelopeImpl.log.debug((Object)"Could not close builder or parser due to:");
                if (builder == null) {
                    SOAPEnvelopeImpl.log.debug((Object)"builder is null");
                }
                if (builder != null && !(builder instanceof StAXBuilder)) {
                    SOAPEnvelopeImpl.log.debug((Object)("builder is not instance of " + StAXBuilder.class.getName()));
                }
            }
        }
    }
    
    public boolean hasFault() {
        final QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null && "Fault".equals(payloadQName.getLocalPart())) {
            final String ns = payloadQName.getNamespaceURI();
            return "http://schemas.xmlsoap.org/soap/envelope/".equals(ns) || "http://www.w3.org/2003/05/soap-envelope".equals(ns);
        }
        final SOAPBody body = this.getBody();
        return body != null && body.hasFault();
    }
    
    public String getSOAPBodyFirstElementLocalName() {
        final QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null) {
            return payloadQName.getLocalPart();
        }
        final SOAPBody body = this.getBody();
        return (body == null) ? null : body.getFirstElementLocalName();
    }
    
    public OMNamespace getSOAPBodyFirstElementNS() {
        final QName payloadQName = this.getPayloadQName_Optimized();
        if (payloadQName != null) {
            return AxiomSOAPElementSupport.ajc$interMethodDispatch1$org_apache_axiom_soap_impl_common_AxiomSOAPElementSupport$org_apache_axiom_soap_impl_intf_AxiomSOAPElement$getOMFactory(this).createOMNamespace(payloadQName.getNamespaceURI(), payloadQName.getPrefix());
        }
        final SOAPBody body = this.getBody();
        return (body == null) ? null : body.getFirstElementNS();
    }
    
    private QName getPayloadQName_Optimized() {
        final OMXMLParserWrapper builder = DeferringParentNodeSupport.ajc$interMethodDispatch1$org_apache_axiom_core_DeferringParentNodeSupport$org_apache_axiom_core_DeferringParentNode$getBuilder(this);
        if (builder instanceof StAXSOAPModelBuilder) {
            try {
                final QName payloadQName = (QName)((StAXSOAPModelBuilder)builder).getReaderProperty("org.apache.axiom.SOAPBodyFirstChildElementQName");
                return payloadQName;
            }
            catch (final Throwable t) {}
        }
        return null;
    }
}
