package com.sun.xml.internal.ws.api.addressing;

import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import java.util.HashMap;
import javax.xml.transform.TransformerException;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.io.StringWriter;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.HeaderList;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import javax.xml.transform.Source;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import java.util.ArrayList;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import javax.xml.bind.JAXBContext;
import javax.xml.ws.Dispatch;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.addressing.EndpointReferenceUtil;
import com.sun.xml.internal.ws.spi.ProviderImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.XMLFilterImpl;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import java.util.Iterator;
import javax.xml.stream.XMLStreamWriter;
import com.sun.xml.internal.ws.util.DOMUtil;
import java.util.Collection;
import org.w3c.dom.Element;
import java.util.List;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import java.net.URI;
import java.net.URL;
import javax.xml.stream.XMLStreamReader;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import javax.xml.transform.Result;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import javax.xml.ws.EndpointReference;
import java.util.Map;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLExtension;

public final class WSEndpointReference implements WSDLExtension
{
    private final XMLStreamBuffer infoset;
    private final AddressingVersion version;
    @NotNull
    private Header[] referenceParameters;
    @NotNull
    private String address;
    @NotNull
    private QName rootElement;
    private static final OutboundReferenceParameterHeader[] EMPTY_ARRAY;
    private Map<QName, EPRExtension> rootEprExtensions;
    
    public WSEndpointReference(final EndpointReference epr, final AddressingVersion version) {
        try {
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            epr.writeTo(new XMLStreamBufferResult(xsb));
            this.infoset = xsb;
            this.version = version;
            this.rootElement = new QName("EndpointReference", version.nsUri);
            this.parse();
        }
        catch (final XMLStreamException e) {
            throw new WebServiceException(ClientMessages.FAILED_TO_PARSE_EPR(epr), e);
        }
    }
    
    public WSEndpointReference(final EndpointReference epr) {
        this(epr, AddressingVersion.fromSpecClass(epr.getClass()));
    }
    
    public WSEndpointReference(final XMLStreamBuffer infoset, final AddressingVersion version) {
        try {
            this.infoset = infoset;
            this.version = version;
            this.rootElement = new QName("EndpointReference", version.nsUri);
            this.parse();
        }
        catch (final XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public WSEndpointReference(final InputStream infoset, final AddressingVersion version) throws XMLStreamException {
        this(XMLStreamReaderFactory.create(null, infoset, false), version);
    }
    
    public WSEndpointReference(final XMLStreamReader in, final AddressingVersion version) throws XMLStreamException {
        this(XMLStreamBuffer.createNewBufferFromXMLStreamReader(in), version);
    }
    
    public WSEndpointReference(final URL address, final AddressingVersion version) {
        this(address.toExternalForm(), version);
    }
    
    public WSEndpointReference(final URI address, final AddressingVersion version) {
        this(address.toString(), version);
    }
    
    public WSEndpointReference(final String address, final AddressingVersion version) {
        this.infoset = createBufferFromAddress(address, version);
        this.version = version;
        this.address = address;
        this.rootElement = new QName("EndpointReference", version.nsUri);
        this.referenceParameters = WSEndpointReference.EMPTY_ARRAY;
    }
    
    private static XMLStreamBuffer createBufferFromAddress(final String address, final AddressingVersion version) {
        try {
            final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
            final StreamWriterBufferCreator w = new StreamWriterBufferCreator(xsb);
            w.writeStartDocument();
            w.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            w.writeNamespace(version.getPrefix(), version.nsUri);
            w.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
            w.writeCharacters(address);
            w.writeEndElement();
            w.writeEndElement();
            w.writeEndDocument();
            w.close();
            return xsb;
        }
        catch (final XMLStreamException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    public WSEndpointReference(@NotNull final AddressingVersion version, @NotNull final String address, @Nullable final QName service, @Nullable final QName port, @Nullable final QName portType, @Nullable final List<Element> metadata, @Nullable final String wsdlAddress, @Nullable final List<Element> referenceParameters) {
        this(version, address, service, port, portType, metadata, wsdlAddress, null, referenceParameters, null, null);
    }
    
    public WSEndpointReference(@NotNull final AddressingVersion version, @NotNull final String address, @Nullable final QName service, @Nullable final QName port, @Nullable final QName portType, @Nullable final List<Element> metadata, @Nullable final String wsdlAddress, @Nullable final List<Element> referenceParameters, @Nullable final Collection<EPRExtension> extns, @Nullable final Map<QName, String> attributes) {
        this(createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, null, extns, attributes), version);
    }
    
    public WSEndpointReference(@NotNull final AddressingVersion version, @NotNull final String address, @Nullable final QName service, @Nullable final QName port, @Nullable final QName portType, @Nullable final List<Element> metadata, @Nullable final String wsdlAddress, @Nullable final String wsdlTargetNamepsace, @Nullable final List<Element> referenceParameters, @Nullable final List<Element> elements, @Nullable final Map<QName, String> attributes) {
        this(createBufferFromData(version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamepsace, elements, attributes), version);
    }
    
    private static XMLStreamBuffer createBufferFromData(final AddressingVersion version, final String address, final List<Element> referenceParameters, final QName service, final QName port, final QName portType, final List<Element> metadata, final String wsdlAddress, final String wsdlTargetNamespace, @Nullable final List<Element> elements, @Nullable final Map<QName, String> attributes) {
        final StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        try {
            writer.writeStartDocument();
            writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            writer.writeNamespace(version.getPrefix(), version.nsUri);
            writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
            if (elements != null) {
                for (final Element e : elements) {
                    DOMUtil.serializeNode(e, writer);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            return writer.getXMLStreamBuffer();
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
    }
    
    private static XMLStreamBuffer createBufferFromData(final AddressingVersion version, final String address, final List<Element> referenceParameters, final QName service, final QName port, final QName portType, final List<Element> metadata, final String wsdlAddress, final String wsdlTargetNamespace, @Nullable final Collection<EPRExtension> extns, @Nullable final Map<QName, String> attributes) {
        final StreamWriterBufferCreator writer = new StreamWriterBufferCreator();
        try {
            writer.writeStartDocument();
            writer.writeStartElement(version.getPrefix(), "EndpointReference", version.nsUri);
            writer.writeNamespace(version.getPrefix(), version.nsUri);
            writePartialEPRInfoset(writer, version, address, referenceParameters, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace, attributes);
            if (extns != null) {
                for (final EPRExtension e : extns) {
                    final XMLStreamReaderToXMLStreamWriter c = new XMLStreamReaderToXMLStreamWriter();
                    final XMLStreamReader r = e.readAsXMLStreamReader();
                    c.bridge(r, writer);
                    XMLStreamReaderFactory.recycle(r);
                }
            }
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            return writer.getXMLStreamBuffer();
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
    }
    
    private static void writePartialEPRInfoset(final StreamWriterBufferCreator writer, final AddressingVersion version, final String address, final List<Element> referenceParameters, final QName service, final QName port, final QName portType, final List<Element> metadata, final String wsdlAddress, final String wsdlTargetNamespace, @Nullable final Map<QName, String> attributes) throws XMLStreamException {
        if (attributes != null) {
            for (final Map.Entry<QName, String> entry : attributes.entrySet()) {
                final QName qname = entry.getKey();
                writer.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), entry.getValue());
            }
        }
        writer.writeStartElement(version.getPrefix(), version.eprType.address, version.nsUri);
        writer.writeCharacters(address);
        writer.writeEndElement();
        if (referenceParameters != null && referenceParameters.size() > 0) {
            writer.writeStartElement(version.getPrefix(), version.eprType.referenceParameters, version.nsUri);
            for (final Element e : referenceParameters) {
                DOMUtil.serializeNode(e, writer);
            }
            writer.writeEndElement();
        }
        switch (version) {
            case W3C: {
                writeW3CMetaData(writer, service, port, portType, metadata, wsdlAddress, wsdlTargetNamespace);
                break;
            }
            case MEMBER: {
                writeMSMetaData(writer, service, port, portType, metadata);
                if (wsdlAddress != null) {
                    writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI());
                    writer.writeStartElement(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI());
                    writer.writeAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
                    writeWsdl(writer, service, wsdlAddress);
                    writer.writeEndElement();
                    writer.writeEndElement();
                    break;
                }
                break;
            }
        }
    }
    
    private static boolean isEmty(final QName qname) {
        return qname == null || qname.toString().trim().length() == 0;
    }
    
    private static void writeW3CMetaData(final StreamWriterBufferCreator writer, final QName service, final QName port, final QName portType, final List<Element> metadata, final String wsdlAddress, final String wsdlTargetNamespace) throws XMLStreamException {
        if (isEmty(service) && isEmty(port) && isEmty(portType) && metadata == null) {
            return;
        }
        writer.writeStartElement(AddressingVersion.W3C.getPrefix(), AddressingVersion.W3C.eprType.wsdlMetadata.getLocalPart(), AddressingVersion.W3C.nsUri);
        writer.writeNamespace(AddressingVersion.W3C.getWsdlPrefix(), AddressingVersion.W3C.wsdlNsUri);
        if (wsdlAddress != null) {
            writeWsdliLocation(writer, service, wsdlAddress, wsdlTargetNamespace);
        }
        if (portType != null) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.portTypeName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            String portTypePrefix = portType.getPrefix();
            if (portTypePrefix == null || portTypePrefix.equals("")) {
                portTypePrefix = "wsns";
            }
            writer.writeNamespace(portTypePrefix, portType.getNamespaceURI());
            writer.writeCharacters(portTypePrefix + ":" + portType.getLocalPart());
            writer.writeEndElement();
        }
        if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
            writer.writeStartElement("wsam", AddressingVersion.W3C.eprType.serviceName, "http://www.w3.org/2007/05/addressing/metadata");
            writer.writeNamespace("wsam", "http://www.w3.org/2007/05/addressing/metadata");
            String servicePrefix = service.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
                servicePrefix = "wsns";
            }
            writer.writeNamespace(servicePrefix, service.getNamespaceURI());
            if (port != null) {
                writer.writeAttribute(AddressingVersion.W3C.eprType.portName, port.getLocalPart());
            }
            writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
            writer.writeEndElement();
        }
        if (metadata != null) {
            for (final Element e : metadata) {
                DOMUtil.serializeNode(e, writer);
            }
        }
        writer.writeEndElement();
    }
    
    private static void writeWsdliLocation(final StreamWriterBufferCreator writer, final QName service, final String wsdlAddress, final String wsdlTargetNamespace) throws XMLStreamException {
        String wsdliLocation = "";
        if (wsdlTargetNamespace != null) {
            wsdliLocation = wsdlTargetNamespace + " ";
        }
        else {
            if (service == null) {
                throw new WebServiceException("WSDL target Namespace cannot be resolved");
            }
            wsdliLocation = service.getNamespaceURI() + " ";
        }
        wsdliLocation += wsdlAddress;
        writer.writeNamespace("wsdli", "http://www.w3.org/ns/wsdl-instance");
        writer.writeAttribute("wsdli", "http://www.w3.org/ns/wsdl-instance", "wsdlLocation", wsdliLocation);
    }
    
    private static void writeMSMetaData(final StreamWriterBufferCreator writer, final QName service, final QName port, final QName portType, final List<Element> metadata) throws XMLStreamException {
        if (portType != null) {
            writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.portTypeName, AddressingVersion.MEMBER.nsUri);
            String portTypePrefix = portType.getPrefix();
            if (portTypePrefix == null || portTypePrefix.equals("")) {
                portTypePrefix = "wsns";
            }
            writer.writeNamespace(portTypePrefix, portType.getNamespaceURI());
            writer.writeCharacters(portTypePrefix + ":" + portType.getLocalPart());
            writer.writeEndElement();
        }
        if (service != null && !service.getNamespaceURI().equals("") && !service.getLocalPart().equals("")) {
            writer.writeStartElement(AddressingVersion.MEMBER.getPrefix(), AddressingVersion.MEMBER.eprType.serviceName, AddressingVersion.MEMBER.nsUri);
            String servicePrefix = service.getPrefix();
            if (servicePrefix == null || servicePrefix.equals("")) {
                servicePrefix = "wsns";
            }
            writer.writeNamespace(servicePrefix, service.getNamespaceURI());
            if (port != null) {
                writer.writeAttribute(AddressingVersion.MEMBER.eprType.portName, port.getLocalPart());
            }
            writer.writeCharacters(servicePrefix + ":" + service.getLocalPart());
            writer.writeEndElement();
        }
    }
    
    private static void writeWsdl(final StreamWriterBufferCreator writer, final QName service, final String wsdlAddress) throws XMLStreamException {
        writer.writeStartElement("wsdl", WSDLConstants.QNAME_DEFINITIONS.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
        writer.writeNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        writer.writeStartElement("wsdl", WSDLConstants.QNAME_IMPORT.getLocalPart(), "http://schemas.xmlsoap.org/wsdl/");
        writer.writeAttribute("namespace", service.getNamespaceURI());
        writer.writeAttribute("location", wsdlAddress);
        writer.writeEndElement();
        writer.writeEndElement();
    }
    
    @Nullable
    public static WSEndpointReference create(@Nullable final EndpointReference epr) {
        if (epr != null) {
            return new WSEndpointReference(epr);
        }
        return null;
    }
    
    @NotNull
    public WSEndpointReference createWithAddress(@NotNull final URI newAddress) {
        return this.createWithAddress(newAddress.toString());
    }
    
    @NotNull
    public WSEndpointReference createWithAddress(@NotNull final URL newAddress) {
        return this.createWithAddress(newAddress.toString());
    }
    
    @NotNull
    public WSEndpointReference createWithAddress(@NotNull final String newAddress) {
        final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
        final XMLFilterImpl filter = new XMLFilterImpl() {
            private boolean inAddress = false;
            
            @Override
            public void startElement(final String uri, final String localName, final String qName, final Attributes atts) throws SAXException {
                if (localName.equals("Address") && uri.equals(WSEndpointReference.this.version.nsUri)) {
                    this.inAddress = true;
                }
                super.startElement(uri, localName, qName, atts);
            }
            
            @Override
            public void characters(final char[] ch, final int start, final int length) throws SAXException {
                if (!this.inAddress) {
                    super.characters(ch, start, length);
                }
            }
            
            @Override
            public void endElement(final String uri, final String localName, final String qName) throws SAXException {
                if (this.inAddress) {
                    super.characters(newAddress.toCharArray(), 0, newAddress.length());
                }
                this.inAddress = false;
                super.endElement(uri, localName, qName);
            }
        };
        filter.setContentHandler(xsb.createFromSAXBufferCreator());
        try {
            this.infoset.writeTo(filter, false);
        }
        catch (final SAXException e) {
            throw new AssertionError((Object)e);
        }
        return new WSEndpointReference(xsb, this.version);
    }
    
    @NotNull
    public EndpointReference toSpec() {
        return ProviderImpl.INSTANCE.readEndpointReference(this.asSource("EndpointReference"));
    }
    
    @NotNull
    public <T extends EndpointReference> T toSpec(final Class<T> clazz) {
        return EndpointReferenceUtil.transform(clazz, this.toSpec());
    }
    
    @NotNull
    public <T> T getPort(@NotNull final Service jaxwsService, @NotNull final Class<T> serviceEndpointInterface, final WebServiceFeature... features) {
        return jaxwsService.getPort(this.toSpec(), serviceEndpointInterface, features);
    }
    
    @NotNull
    public <T> Dispatch<T> createDispatch(@NotNull final Service jaxwsService, @NotNull final Class<T> type, @NotNull final Service.Mode mode, final WebServiceFeature... features) {
        return jaxwsService.createDispatch(this.toSpec(), type, mode, features);
    }
    
    @NotNull
    public Dispatch<Object> createDispatch(@NotNull final Service jaxwsService, @NotNull final JAXBContext context, @NotNull final Service.Mode mode, final WebServiceFeature... features) {
        return jaxwsService.createDispatch(this.toSpec(), context, mode, features);
    }
    
    @NotNull
    public AddressingVersion getVersion() {
        return this.version;
    }
    
    @NotNull
    public String getAddress() {
        return this.address;
    }
    
    public boolean isAnonymous() {
        return this.address.equals(this.version.anonymousUri);
    }
    
    public boolean isNone() {
        return this.address.equals(this.version.noneUri);
    }
    
    private void parse() throws XMLStreamException {
        final StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
        if (xsr.getEventType() == 7) {
            xsr.nextTag();
        }
        assert xsr.getEventType() == 1;
        final String rootLocalName = xsr.getLocalName();
        if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
            throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
        }
        this.rootElement = new QName(xsr.getNamespaceURI(), rootLocalName);
        List<Header> marks = null;
        while (xsr.nextTag() == 1) {
            final String localName = xsr.getLocalName();
            if (this.version.isReferenceParameter(localName)) {
                XMLStreamBuffer mark;
                while ((mark = xsr.nextTagAndMark()) != null) {
                    if (marks == null) {
                        marks = new ArrayList<Header>();
                    }
                    marks.add(this.version.createReferenceParameterHeader(mark, xsr.getNamespaceURI(), xsr.getLocalName()));
                    XMLStreamReaderUtil.skipElement(xsr);
                }
            }
            else if (localName.equals("Address")) {
                if (this.address != null) {
                    throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), AddressingVersion.fault_duplicateAddressInEpr);
                }
                this.address = xsr.getElementText().trim();
            }
            else {
                XMLStreamReaderUtil.skipElement(xsr);
            }
        }
        if (marks == null) {
            this.referenceParameters = WSEndpointReference.EMPTY_ARRAY;
        }
        else {
            this.referenceParameters = marks.toArray(new Header[marks.size()]);
        }
        if (this.address == null) {
            throw new InvalidAddressingHeaderException(new QName(this.version.nsUri, rootLocalName), this.version.fault_missingAddressInEpr);
        }
    }
    
    public XMLStreamReader read(@NotNull final String localName) throws XMLStreamException {
        return new StreamReaderBufferProcessor(this.infoset) {
            @Override
            protected void processElement(final String prefix, final String uri, String _localName, final boolean inScope) {
                if (this._depth == 0) {
                    _localName = localName;
                }
                super.processElement(prefix, uri, _localName, WSEndpointReference.this.isInscope(WSEndpointReference.this.infoset, this._depth));
            }
        };
    }
    
    private boolean isInscope(final XMLStreamBuffer buffer, final int depth) {
        return buffer.getInscopeNamespaces().size() > 0 && depth == 0;
    }
    
    public Source asSource(@NotNull final String localName) {
        return new SAXSource(new SAXBufferProcessorImpl(localName), new InputSource());
    }
    
    public void writeTo(@NotNull final String localName, final ContentHandler contentHandler, final ErrorHandler errorHandler, final boolean fragment) throws SAXException {
        final SAXBufferProcessorImpl p = new SAXBufferProcessorImpl(localName);
        p.setContentHandler(contentHandler);
        p.setErrorHandler(errorHandler);
        p.process(this.infoset, fragment);
    }
    
    public void writeTo(@NotNull final String localName, @NotNull final XMLStreamWriter w) throws XMLStreamException {
        this.infoset.writeToXMLStreamWriter(new XMLStreamWriterFilter(w) {
            private boolean root = true;
            
            @Override
            public void writeStartDocument() throws XMLStreamException {
            }
            
            @Override
            public void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
            }
            
            @Override
            public void writeStartDocument(final String version) throws XMLStreamException {
            }
            
            @Override
            public void writeEndDocument() throws XMLStreamException {
            }
            
            private String override(final String ln) {
                if (this.root) {
                    this.root = false;
                    return localName;
                }
                return ln;
            }
            
            @Override
            public void writeStartElement(final String localName) throws XMLStreamException {
                super.writeStartElement(this.override(localName));
            }
            
            @Override
            public void writeStartElement(final String namespaceURI, final String localName) throws XMLStreamException {
                super.writeStartElement(namespaceURI, this.override(localName));
            }
            
            @Override
            public void writeStartElement(final String prefix, final String localName, final String namespaceURI) throws XMLStreamException {
                super.writeStartElement(prefix, this.override(localName), namespaceURI);
            }
        }, true);
    }
    
    public Header createHeader(final QName rootTagName) {
        return new EPRHeader(rootTagName, this);
    }
    
    @Deprecated
    public void addReferenceParametersToList(final HeaderList outbound) {
        for (final Header header : this.referenceParameters) {
            outbound.add(header);
        }
    }
    
    public void addReferenceParametersToList(final MessageHeaders outbound) {
        for (final Header header : this.referenceParameters) {
            outbound.add(header);
        }
    }
    
    public void addReferenceParameters(final HeaderList headers) {
        if (headers != null) {
            final Header[] hs = new Header[this.referenceParameters.length + headers.size()];
            System.arraycopy(this.referenceParameters, 0, hs, 0, this.referenceParameters.length);
            int i = this.referenceParameters.length;
            for (final Header h : headers) {
                hs[i++] = h;
            }
            this.referenceParameters = hs;
        }
    }
    
    @Override
    public String toString() {
        try {
            final StringWriter sw = new StringWriter();
            XmlUtil.newTransformer().transform(this.asSource("EndpointReference"), new StreamResult(sw));
            return sw.toString();
        }
        catch (final TransformerException e) {
            return e.toString();
        }
    }
    
    @Override
    public QName getName() {
        return this.rootElement;
    }
    
    @Nullable
    public EPRExtension getEPRExtension(final QName extnQName) throws XMLStreamException {
        if (this.rootEprExtensions == null) {
            this.parseEPRExtensions();
        }
        return this.rootEprExtensions.get(extnQName);
    }
    
    @NotNull
    public Collection<EPRExtension> getEPRExtensions() throws XMLStreamException {
        if (this.rootEprExtensions == null) {
            this.parseEPRExtensions();
        }
        return this.rootEprExtensions.values();
    }
    
    private void parseEPRExtensions() throws XMLStreamException {
        this.rootEprExtensions = new HashMap<QName, EPRExtension>();
        final StreamReaderBufferProcessor xsr = this.infoset.readAsXMLStreamReader();
        if (xsr.getEventType() == 7) {
            xsr.nextTag();
        }
        assert xsr.getEventType() == 1;
        if (!xsr.getNamespaceURI().equals(this.version.nsUri)) {
            throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(this.version.nsUri, xsr.getNamespaceURI()));
        }
        XMLStreamBuffer mark;
        while ((mark = xsr.nextTagAndMark()) != null) {
            final String localName = xsr.getLocalName();
            final String ns = xsr.getNamespaceURI();
            if (this.version.nsUri.equals(ns)) {
                XMLStreamReaderUtil.skipElement(xsr);
            }
            else {
                final QName qn = new QName(ns, localName);
                this.rootEprExtensions.put(qn, new WSEPRExtension(mark, qn));
                XMLStreamReaderUtil.skipElement(xsr);
            }
        }
    }
    
    @NotNull
    public Metadata getMetaData() {
        return new Metadata();
    }
    
    static {
        EMPTY_ARRAY = new OutboundReferenceParameterHeader[0];
    }
    
    class SAXBufferProcessorImpl extends SAXBufferProcessor
    {
        private final String rootLocalName;
        private boolean root;
        
        public SAXBufferProcessorImpl(final String rootLocalName) {
            super(WSEndpointReference.this.infoset, false);
            this.root = true;
            this.rootLocalName = rootLocalName;
        }
        
        @Override
        protected void processElement(final String uri, String localName, String qName, final boolean inscope) throws SAXException {
            if (this.root) {
                this.root = false;
                if (qName.equals(localName)) {
                    localName = (qName = this.rootLocalName);
                }
                else {
                    localName = this.rootLocalName;
                    final int idx = qName.indexOf(58);
                    qName = qName.substring(0, idx + 1) + this.rootLocalName;
                }
            }
            super.processElement(uri, localName, qName, inscope);
        }
    }
    
    public abstract static class EPRExtension
    {
        public abstract XMLStreamReader readAsXMLStreamReader() throws XMLStreamException;
        
        public abstract QName getQName();
    }
    
    public class Metadata
    {
        @Nullable
        private QName serviceName;
        @Nullable
        private QName portName;
        @Nullable
        private QName portTypeName;
        @Nullable
        private Source wsdlSource;
        @Nullable
        private String wsdliLocation;
        
        @Nullable
        public QName getServiceName() {
            return this.serviceName;
        }
        
        @Nullable
        public QName getPortName() {
            return this.portName;
        }
        
        @Nullable
        public QName getPortTypeName() {
            return this.portTypeName;
        }
        
        @Nullable
        public Source getWsdlSource() {
            return this.wsdlSource;
        }
        
        @Nullable
        public String getWsdliLocation() {
            return this.wsdliLocation;
        }
        
        private Metadata() {
            try {
                this.parseMetaData();
            }
            catch (final XMLStreamException e) {
                throw new WebServiceException(e);
            }
        }
        
        private void parseMetaData() throws XMLStreamException {
            final StreamReaderBufferProcessor xsr = WSEndpointReference.this.infoset.readAsXMLStreamReader();
            if (xsr.getEventType() == 7) {
                xsr.nextTag();
            }
            assert xsr.getEventType() == 1;
            final String rootElement = xsr.getLocalName();
            if (!xsr.getNamespaceURI().equals(WSEndpointReference.this.version.nsUri)) {
                throw new WebServiceException(AddressingMessages.WRONG_ADDRESSING_VERSION(WSEndpointReference.this.version.nsUri, xsr.getNamespaceURI()));
            }
            if (WSEndpointReference.this.version == AddressingVersion.W3C) {
                do {
                    if (xsr.getLocalName().equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getLocalPart())) {
                        final String wsdlLoc = xsr.getAttributeValue("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
                        if (wsdlLoc != null) {
                            this.wsdliLocation = wsdlLoc.trim();
                        }
                        XMLStreamBuffer mark;
                        while ((mark = xsr.nextTagAndMark()) != null) {
                            final String localName = xsr.getLocalName();
                            final String ns = xsr.getNamespaceURI();
                            if (localName.equals(WSEndpointReference.this.version.eprType.serviceName)) {
                                final String portStr = xsr.getAttributeValue(null, WSEndpointReference.this.version.eprType.portName);
                                if (this.serviceName != null) {
                                    throw new RuntimeException("More than one " + WSEndpointReference.this.version.eprType.serviceName + " element in EPR Metadata");
                                }
                                this.serviceName = this.getElementTextAsQName(xsr);
                                if (this.serviceName == null || portStr == null) {
                                    continue;
                                }
                                this.portName = new QName(this.serviceName.getNamespaceURI(), portStr);
                            }
                            else if (localName.equals(WSEndpointReference.this.version.eprType.portTypeName)) {
                                if (this.portTypeName != null) {
                                    throw new RuntimeException("More than one " + WSEndpointReference.this.version.eprType.portTypeName + " element in EPR Metadata");
                                }
                                this.portTypeName = this.getElementTextAsQName(xsr);
                            }
                            else if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                this.wsdlSource = new XMLStreamBufferSource(mark);
                            }
                            else {
                                XMLStreamReaderUtil.skipElement(xsr);
                            }
                        }
                    }
                    else {
                        if (xsr.getLocalName().equals(rootElement)) {
                            continue;
                        }
                        XMLStreamReaderUtil.skipElement(xsr);
                    }
                } while (XMLStreamReaderUtil.nextElementContent(xsr) == 1);
                if (this.wsdliLocation != null) {
                    String wsdlLocation = this.wsdliLocation.trim();
                    wsdlLocation = wsdlLocation.substring(this.wsdliLocation.lastIndexOf(" "));
                    this.wsdlSource = new StreamSource(wsdlLocation);
                }
            }
            else if (WSEndpointReference.this.version == AddressingVersion.MEMBER) {
                do {
                    String localName = xsr.getLocalName();
                    String ns = xsr.getNamespaceURI();
                    if (localName.equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getLocalPart()) && ns.equals(WSEndpointReference.this.version.eprType.wsdlMetadata.getNamespaceURI())) {
                        while (xsr.nextTag() == 1) {
                            XMLStreamBuffer mark2;
                            while ((mark2 = xsr.nextTagAndMark()) != null) {
                                localName = xsr.getLocalName();
                                ns = xsr.getNamespaceURI();
                                if (ns.equals("http://schemas.xmlsoap.org/wsdl/") && localName.equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())) {
                                    this.wsdlSource = new XMLStreamBufferSource(mark2);
                                }
                                else {
                                    XMLStreamReaderUtil.skipElement(xsr);
                                }
                            }
                        }
                    }
                    else if (localName.equals(WSEndpointReference.this.version.eprType.serviceName)) {
                        final String portStr2 = xsr.getAttributeValue(null, WSEndpointReference.this.version.eprType.portName);
                        this.serviceName = this.getElementTextAsQName(xsr);
                        if (this.serviceName == null || portStr2 == null) {
                            continue;
                        }
                        this.portName = new QName(this.serviceName.getNamespaceURI(), portStr2);
                    }
                    else if (localName.equals(WSEndpointReference.this.version.eprType.portTypeName)) {
                        this.portTypeName = this.getElementTextAsQName(xsr);
                    }
                    else {
                        if (xsr.getLocalName().equals(rootElement)) {
                            continue;
                        }
                        XMLStreamReaderUtil.skipElement(xsr);
                    }
                } while (XMLStreamReaderUtil.nextElementContent(xsr) == 1);
            }
        }
        
        private QName getElementTextAsQName(final StreamReaderBufferProcessor xsr) throws XMLStreamException {
            final String text = xsr.getElementText().trim();
            final String prefix = XmlUtil.getPrefix(text);
            final String name = XmlUtil.getLocalPart(text);
            if (name != null) {
                if (prefix == null) {
                    return new QName(null, name);
                }
                final String ns = xsr.getNamespaceURI(prefix);
                if (ns != null) {
                    return new QName(ns, name, prefix);
                }
            }
            return null;
        }
    }
}
