package org.apache.axiom.soap.impl.builder;

import org.apache.commons.logging.LogFactory;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.soap.SOAP12Version;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAP11Version;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.impl.builder.CustomBuilder;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.soap.SOAPFactory;
import java.io.Closeable;
import org.apache.axiom.om.impl.builder.Detachable;
import org.apache.axiom.om.OMAbstractFactory;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.logging.Log;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.soap.SOAPModelBuilder;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;

public class StAXSOAPModelBuilder extends StAXOMBuilder implements SOAPModelBuilder
{
    private OMMetaFactory metaFactory;
    private SOAPFactoryEx soapFactory;
    private boolean headerPresent;
    private boolean bodyPresent;
    private static final Log log;
    private boolean processingFault;
    private SOAPBuilderHelper builderHelper;
    
    @Deprecated
    public StAXSOAPModelBuilder(final XMLStreamReader parser, final String soapVersion) {
        this(OMAbstractFactory.getMetaFactory(), parser, soapVersion);
    }
    
    @Deprecated
    public StAXSOAPModelBuilder(final OMMetaFactory metaFactory, final XMLStreamReader parser, final String soapVersion) {
        super(metaFactory.getOMFactory(), parser);
        this.headerPresent = false;
        this.bodyPresent = false;
        this.processingFault = false;
        this.metaFactory = metaFactory;
        this.identifySOAPVersion(soapVersion);
    }
    
    @Deprecated
    public StAXSOAPModelBuilder(final XMLStreamReader parser) {
        this(OMAbstractFactory.getMetaFactory(), parser);
    }
    
    public StAXSOAPModelBuilder(final OMMetaFactory metaFactory, final XMLStreamReader parser, final Detachable detachable, final Closeable closeable) {
        super(metaFactory.getOMFactory(), parser, detachable, closeable);
        this.headerPresent = false;
        this.bodyPresent = false;
        this.processingFault = false;
        this.metaFactory = metaFactory;
    }
    
    @Deprecated
    public StAXSOAPModelBuilder(final OMMetaFactory metaFactory, final XMLStreamReader parser) {
        this(metaFactory, parser, null, null);
    }
    
    public StAXSOAPModelBuilder(final XMLStreamReader parser, final SOAPFactory factory, final String soapVersion, final Detachable detachable, final Closeable closeable) {
        super(factory, parser, detachable, closeable);
        this.headerPresent = false;
        this.bodyPresent = false;
        this.processingFault = false;
        this.soapFactory = (SOAPFactoryEx)factory;
        this.identifySOAPVersion(soapVersion);
    }
    
    @Deprecated
    public StAXSOAPModelBuilder(final XMLStreamReader parser, final SOAPFactory factory, final String soapVersion) {
        this(parser, factory, soapVersion, null, null);
    }
    
    protected void identifySOAPVersion(final String soapVersionURIFromTransport) {
        final SOAPEnvelope soapEnvelope = this.getSOAPEnvelope();
        if (soapEnvelope == null) {
            throw new SOAPProcessingException("SOAP Message does not contain an Envelope", "VersionMismatch");
        }
        final OMNamespace envelopeNamespace = soapEnvelope.getNamespace();
        if (soapVersionURIFromTransport != null) {
            final String namespaceName = envelopeNamespace.getNamespaceURI();
            if (!soapVersionURIFromTransport.equals(namespaceName)) {
                throw new SOAPProcessingException("Transport level information does not match with SOAP Message namespace URI", envelopeNamespace.getPrefix() + ":" + "VersionMismatch");
            }
        }
    }
    
    public SOAPEnvelope getSOAPEnvelope() throws OMException {
        return (SOAPEnvelope)this.getDocumentElement();
    }
    
    @Override
    protected OMNode createNextOMElement() {
        OMNode newElement = null;
        if (this.elementLevel == 3 && this.customBuilderForPayload != null && this.target instanceof SOAPBody) {
            newElement = this.createWithCustomBuilder(this.customBuilderForPayload, this.soapFactory);
        }
        if (newElement == null && this.customBuilders != null && this.elementLevel <= this.maxDepthForCustomBuilders) {
            final String namespace = this.parser.getNamespaceURI();
            final String localPart = this.parser.getLocalName();
            final CustomBuilder customBuilder = this.getCustomBuilder(namespace, localPart);
            if (customBuilder != null) {
                newElement = this.createWithCustomBuilder(customBuilder, this.soapFactory);
            }
        }
        if (newElement == null) {
            newElement = this.createOMElement();
        }
        else {
            --this.elementLevel;
        }
        return newElement;
    }
    
    @Override
    protected OMElement constructNode(final OMContainer parent, final String elementName) {
        OMElement element;
        if (this.elementLevel == 1) {
            if (!elementName.equals("Envelope")) {
                throw new SOAPProcessingException("First Element must contain the local name, Envelope , but found " + elementName, "");
            }
            final String namespaceURI = this.parser.getNamespaceURI();
            if (this.soapFactory == null) {
                if ("http://www.w3.org/2003/05/soap-envelope".equals(namespaceURI)) {
                    this.soapFactory = (SOAPFactoryEx)this.metaFactory.getSOAP12Factory();
                    StAXSOAPModelBuilder.log.debug((Object)"Starting to process SOAP 1.2 message");
                }
                else {
                    if (!"http://schemas.xmlsoap.org/soap/envelope/".equals(namespaceURI)) {
                        throw new SOAPProcessingException("Only SOAP 1.1 or SOAP 1.2 messages are supported in the system", "VersionMismatch");
                    }
                    this.soapFactory = (SOAPFactoryEx)this.metaFactory.getSOAP11Factory();
                    StAXSOAPModelBuilder.log.debug((Object)"Starting to process SOAP 1.1 message");
                }
            }
            else if (!this.soapFactory.getSoapVersionURI().equals(namespaceURI)) {
                throw new SOAPProcessingException("Invalid SOAP namespace URI. Expected " + this.soapFactory.getSoapVersionURI(), "Sender");
            }
            element = this.soapFactory.createSOAPEnvelope((SOAPMessage)parent, this);
        }
        else if (this.elementLevel == 2) {
            final String elementNS = this.parser.getNamespaceURI();
            if (this.soapFactory.getSoapVersionURI().equals(elementNS)) {
                if (elementName.equals("Header")) {
                    if (this.headerPresent) {
                        throw new SOAPProcessingException("Multiple headers encountered!", this.getSenderFaultCode());
                    }
                    if (this.bodyPresent) {
                        throw new SOAPProcessingException("Header Body wrong order!", this.getSenderFaultCode());
                    }
                    this.headerPresent = true;
                    element = this.soapFactory.createSOAPHeader((SOAPEnvelope)parent, this);
                }
                else {
                    if (!elementName.equals("Body")) {
                        throw new SOAPProcessingException(elementName + " is not supported here.", this.getSenderFaultCode());
                    }
                    if (this.bodyPresent) {
                        throw new SOAPProcessingException("Multiple body elements encountered", this.getSenderFaultCode());
                    }
                    this.bodyPresent = true;
                    element = this.soapFactory.createSOAPBody((SOAPEnvelope)parent, this);
                }
            }
            else {
                if (this.soapFactory.getSOAPVersion() != SOAP11Version.getSingleton() || !this.bodyPresent) {
                    throw new SOAPProcessingException("Disallowed element found inside Envelope : {" + elementNS + "}" + elementName);
                }
                element = this.omfactory.createOMElement(this.parser.getLocalName(), parent, this);
            }
        }
        else {
            if (this.elementLevel == 3 && ((OMElement)parent).getLocalName().equals("Header")) {
                try {
                    element = this.soapFactory.createSOAPHeaderBlock(elementName, (SOAPHeader)parent, this);
                    return element;
                }
                catch (final SOAPProcessingException e) {
                    throw new SOAPProcessingException("Can not create SOAPHeader block", this.getReceiverFaultCode(), e);
                }
            }
            if (this.elementLevel == 3 && ((OMElement)parent).getLocalName().equals("Body") && elementName.equals("Fault") && this.soapFactory.getSoapVersionURI().equals(this.parser.getNamespaceURI())) {
                element = this.soapFactory.createSOAPFault((SOAPBody)parent, this);
                this.processingFault = true;
                if (this.soapFactory.getSOAPVersion() == SOAP12Version.getSingleton()) {
                    this.builderHelper = new SOAP12BuilderHelper(this, (SOAP12FactoryEx)this.soapFactory);
                }
                else if (this.soapFactory.getSOAPVersion() == SOAP11Version.getSingleton()) {
                    this.builderHelper = new SOAP11BuilderHelper(this, this.soapFactory);
                }
            }
            else if (this.elementLevel > 3 && this.processingFault) {
                element = this.builderHelper.handleEvent(this.parser, (OMElement)parent, this.elementLevel);
            }
            else {
                element = this.soapFactory.createOMElement(elementName, parent, this);
            }
        }
        return element;
    }
    
    private String getSenderFaultCode() {
        return this.getSOAPEnvelope().getVersion().getSenderFaultCode().getLocalPart();
    }
    
    private String getReceiverFaultCode() {
        return this.getSOAPEnvelope().getVersion().getReceiverFaultCode().getLocalPart();
    }
    
    @Override
    protected OMDocument createDocument() {
        if (this.soapFactory != null) {
            return this.soapFactory.createSOAPMessage(this);
        }
        return ((OMMetaFactoryEx)this.metaFactory).createSOAPMessage(this);
    }
    
    @Override
    protected OMNode createDTD() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain a Document Type Declaration(DTD)");
    }
    
    @Override
    protected OMNode createPI() throws OMException {
        throw new SOAPProcessingException("SOAP message MUST NOT contain Processing Instructions(PI)");
    }
    
    @Override
    protected OMNode createEntityReference() {
        throw new SOAPProcessingException("A SOAP message cannot contain entity references because it must not have a DTD");
    }
    
    public OMNamespace getEnvelopeNamespace() {
        return this.getSOAPEnvelope().getNamespace();
    }
    
    @Deprecated
    public SOAPMessage getSoapMessage() {
        return this.getSOAPMessage();
    }
    
    public SOAPMessage getSOAPMessage() {
        return (SOAPMessage)this.getDocument();
    }
    
    public SOAPFactory getSOAPFactory() {
        if (this.soapFactory == null) {
            this.getSOAPEnvelope();
        }
        return this.soapFactory;
    }
    
    static {
        log = LogFactory.getLog((Class)StAXSOAPModelBuilder.class);
    }
}
