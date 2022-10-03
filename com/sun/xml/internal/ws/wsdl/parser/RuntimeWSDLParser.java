package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import java.io.InputStream;
import java.io.FilterInputStream;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.WSDLLocator;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartImpl;
import com.sun.xml.internal.ws.model.wsdl.WSDLPartDescriptorImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLDescriptorKind;
import com.sun.xml.internal.ws.model.wsdl.WSDLMessageImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.model.wsdl.WSDLOutputImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.model.wsdl.WSDLInputImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.model.wsdl.WSDLFaultImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.model.wsdl.WSDLOperationImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortTypeImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundFaultImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundOperationImpl;
import com.sun.xml.internal.ws.api.BindingIDFactory;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import javax.jws.soap.SOAPBinding;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.model.wsdl.WSDLBoundPortTypeImpl;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.stream.buffer.AbstractCreatorProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferMark;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortImpl;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.model.wsdl.WSDLServiceImpl;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.resources.WsdlmodelMessages;
import java.io.Closeable;
import com.sun.xml.internal.ws.streaming.TidyXMLStreamReader;
import com.sun.xml.internal.ws.streaming.SourceReaderFactory;
import com.sun.xml.internal.ws.api.wsdl.parser.PolicyWSDLParserExtension;
import com.sun.xml.internal.ws.model.wsdl.WSDLModelImpl;
import java.util.HashMap;
import java.util.HashSet;
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.XMLStreamReader;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.wsdl.parser.ServiceDescriptor;
import com.sun.xml.internal.ws.api.wsdl.parser.MetaDataResolver;
import javax.xml.transform.stream.StreamSource;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.wsdl.parser.MetadataResolverFactory;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import com.sun.xml.internal.ws.resources.ClientMessages;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import javax.xml.ws.Service;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.api.server.Container;
import org.xml.sax.EntityResolver;
import com.sun.istack.internal.NotNull;
import javax.xml.transform.Source;
import com.sun.istack.internal.Nullable;
import java.net.URL;
import java.util.logging.Logger;
import java.util.Map;
import java.util.List;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import java.util.Set;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;

public class RuntimeWSDLParser
{
    private final EditableWSDLModel wsdlDoc;
    private String targetNamespace;
    private final Set<String> importedWSDLs;
    private final XMLEntityResolver resolver;
    private final PolicyResolver policyResolver;
    private final WSDLParserExtension extensionFacade;
    private final WSDLParserExtensionContextImpl context;
    List<WSDLParserExtension> extensions;
    Map<String, String> wsdldef_nsdecl;
    Map<String, String> service_nsdecl;
    Map<String, String> port_nsdecl;
    private static final Logger LOGGER;
    
    public static WSDLModel parse(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, PolicyResolverFactory.create(), extensions);
    }
    
    public static WSDLModel parse(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final Class serviceClass, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, PolicyResolverFactory.create(), extensions);
    }
    
    public static WSDLModel parse(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, @NotNull final PolicyResolver policyResolver, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, Service.class, policyResolver, extensions);
    }
    
    public static WSDLModel parse(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final Class serviceClass, @NotNull final PolicyResolver policyResolver, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        return parse(wsdlLoc, wsdlSource, resolver, isClientSide, container, serviceClass, policyResolver, false, extensions);
    }
    
    public static WSDLModel parse(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final Class serviceClass, @NotNull final PolicyResolver policyResolver, final boolean isUseStreamFromEntityResolverWrapper, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        assert resolver != null;
        final RuntimeWSDLParser wsdlParser = new RuntimeWSDLParser(wsdlSource.getSystemId(), new EntityResolverWrapper(resolver, isUseStreamFromEntityResolverWrapper), isClientSide, container, policyResolver, extensions);
        XMLEntityResolver.Parser parser;
        try {
            parser = wsdlParser.resolveWSDL(wsdlLoc, wsdlSource, serviceClass);
            if (!hasWSDLDefinitions(parser.parser)) {
                throw new XMLStreamException(ClientMessages.RUNTIME_WSDLPARSER_INVALID_WSDL(parser.systemId, WSDLConstants.QNAME_DEFINITIONS, parser.parser.getName(), parser.parser.getLocation()));
            }
        }
        catch (final XMLStreamException e) {
            if (wsdlLoc == null) {
                throw e;
            }
            return tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, e, serviceClass, policyResolver, extensions);
        }
        catch (final IOException e2) {
            if (wsdlLoc == null) {
                throw e2;
            }
            return tryWithMex(wsdlParser, wsdlLoc, resolver, isClientSide, container, e2, serviceClass, policyResolver, extensions);
        }
        wsdlParser.extensionFacade.start(wsdlParser.context);
        wsdlParser.parseWSDL(parser, false);
        wsdlParser.wsdlDoc.freeze();
        wsdlParser.extensionFacade.finished(wsdlParser.context);
        wsdlParser.extensionFacade.postFinished(wsdlParser.context);
        if (wsdlParser.wsdlDoc.getServices().isEmpty()) {
            throw new WebServiceException(ClientMessages.WSDL_CONTAINS_NO_SERVICE(wsdlLoc));
        }
        return wsdlParser.wsdlDoc;
    }
    
    private static WSDLModel tryWithMex(@NotNull final RuntimeWSDLParser wsdlParser, @NotNull final URL wsdlLoc, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final Throwable e, final Class serviceClass, final PolicyResolver policyResolver, final WSDLParserExtension... extensions) throws SAXException, XMLStreamException {
        final ArrayList<Throwable> exceptions = new ArrayList<Throwable>();
        try {
            final WSDLModel wsdlModel = wsdlParser.parseUsingMex(wsdlLoc, resolver, isClientSide, container, serviceClass, policyResolver, extensions);
            if (wsdlModel == null) {
                throw new WebServiceException(ClientMessages.FAILED_TO_PARSE(wsdlLoc.toExternalForm(), e.getMessage()), e);
            }
            return wsdlModel;
        }
        catch (final URISyntaxException e2) {
            exceptions.add(e);
            exceptions.add(e2);
        }
        catch (final IOException e3) {
            exceptions.add(e);
            exceptions.add(e3);
        }
        throw new InaccessibleWSDLException(exceptions);
    }
    
    private WSDLModel parseUsingMex(@NotNull URL wsdlLoc, @NotNull final EntityResolver resolver, final boolean isClientSide, final Container container, final Class serviceClass, final PolicyResolver policyResolver, final WSDLParserExtension[] extensions) throws IOException, SAXException, XMLStreamException, URISyntaxException {
        MetaDataResolver mdResolver = null;
        ServiceDescriptor serviceDescriptor = null;
        RuntimeWSDLParser wsdlParser = null;
        for (final MetadataResolverFactory resolverFactory : ServiceFinder.find(MetadataResolverFactory.class)) {
            mdResolver = resolverFactory.metadataResolver(resolver);
            serviceDescriptor = mdResolver.resolve(wsdlLoc.toURI());
            if (serviceDescriptor != null) {
                break;
            }
        }
        if (serviceDescriptor != null) {
            final List<? extends Source> wsdls = serviceDescriptor.getWSDLs();
            wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new MexEntityResolver(wsdls), isClientSide, container, policyResolver, extensions);
            wsdlParser.extensionFacade.start(wsdlParser.context);
            for (final Source src : wsdls) {
                final String systemId = src.getSystemId();
                final XMLEntityResolver.Parser parser = wsdlParser.resolver.resolveEntity(null, systemId);
                wsdlParser.parseWSDL(parser, false);
            }
        }
        if ((mdResolver == null || serviceDescriptor == null) && (wsdlLoc.getProtocol().equals("http") || wsdlLoc.getProtocol().equals("https")) && wsdlLoc.getQuery() == null) {
            String urlString = wsdlLoc.toExternalForm();
            urlString += "?wsdl";
            wsdlLoc = new URL(urlString);
            wsdlParser = new RuntimeWSDLParser(wsdlLoc.toExternalForm(), new EntityResolverWrapper(resolver), isClientSide, container, policyResolver, extensions);
            wsdlParser.extensionFacade.start(wsdlParser.context);
            final XMLEntityResolver.Parser parser2 = this.resolveWSDL(wsdlLoc, new StreamSource(wsdlLoc.toExternalForm()), serviceClass);
            wsdlParser.parseWSDL(parser2, false);
        }
        if (wsdlParser == null) {
            return null;
        }
        wsdlParser.wsdlDoc.freeze();
        wsdlParser.extensionFacade.finished(wsdlParser.context);
        wsdlParser.extensionFacade.postFinished(wsdlParser.context);
        return wsdlParser.wsdlDoc;
    }
    
    private static boolean hasWSDLDefinitions(final XMLStreamReader reader) {
        XMLStreamReaderUtil.nextElementContent(reader);
        return reader.getName().equals(WSDLConstants.QNAME_DEFINITIONS);
    }
    
    public static WSDLModel parse(final XMLEntityResolver.Parser wsdl, final XMLEntityResolver resolver, final boolean isClientSide, final Container container, final PolicyResolver policyResolver, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        assert resolver != null;
        final RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, policyResolver, extensions);
        parser.extensionFacade.start(parser.context);
        parser.parseWSDL(wsdl, false);
        parser.wsdlDoc.freeze();
        parser.extensionFacade.finished(parser.context);
        parser.extensionFacade.postFinished(parser.context);
        return parser.wsdlDoc;
    }
    
    public static WSDLModel parse(final XMLEntityResolver.Parser wsdl, final XMLEntityResolver resolver, final boolean isClientSide, final Container container, final WSDLParserExtension... extensions) throws IOException, XMLStreamException, SAXException {
        assert resolver != null;
        final RuntimeWSDLParser parser = new RuntimeWSDLParser(wsdl.systemId.toExternalForm(), resolver, isClientSide, container, PolicyResolverFactory.create(), extensions);
        parser.extensionFacade.start(parser.context);
        parser.parseWSDL(wsdl, false);
        parser.wsdlDoc.freeze();
        parser.extensionFacade.finished(parser.context);
        parser.extensionFacade.postFinished(parser.context);
        return parser.wsdlDoc;
    }
    
    private RuntimeWSDLParser(@NotNull final String sourceLocation, final XMLEntityResolver resolver, final boolean isClientSide, final Container container, final PolicyResolver policyResolver, final WSDLParserExtension... extensions) {
        this.importedWSDLs = new HashSet<String>();
        this.wsdldef_nsdecl = new HashMap<String, String>();
        this.service_nsdecl = new HashMap<String, String>();
        this.port_nsdecl = new HashMap<String, String>();
        this.wsdlDoc = ((sourceLocation != null) ? new WSDLModelImpl(sourceLocation) : new WSDLModelImpl());
        this.resolver = resolver;
        this.policyResolver = policyResolver;
        this.extensions = new ArrayList<WSDLParserExtension>();
        this.context = new WSDLParserExtensionContextImpl(this.wsdlDoc, isClientSide, container, policyResolver);
        boolean isPolicyExtensionFound = false;
        for (final WSDLParserExtension e : extensions) {
            if (e instanceof PolicyWSDLParserExtension) {
                isPolicyExtensionFound = true;
            }
            this.register(e);
        }
        if (!isPolicyExtensionFound) {
            this.register(new com.sun.xml.internal.ws.policy.jaxws.PolicyWSDLParserExtension());
        }
        this.register(new MemberSubmissionAddressingWSDLParserExtension());
        this.register(new W3CAddressingWSDLParserExtension());
        this.register(new W3CAddressingMetadataWSDLParserExtension());
        this.extensionFacade = new WSDLParserExtensionFacade((WSDLParserExtension[])this.extensions.toArray(new WSDLParserExtension[0]));
    }
    
    private XMLEntityResolver.Parser resolveWSDL(@Nullable final URL wsdlLoc, @NotNull final Source wsdlSource, final Class serviceClass) throws IOException, SAXException, XMLStreamException {
        final String systemId = wsdlSource.getSystemId();
        XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, systemId);
        if (parser == null && wsdlLoc != null) {
            final String exForm = wsdlLoc.toExternalForm();
            parser = this.resolver.resolveEntity(null, exForm);
            if (parser == null && serviceClass != null) {
                final URL ru = serviceClass.getResource(".");
                if (ru != null) {
                    final String ruExForm = ru.toExternalForm();
                    if (exForm.startsWith(ruExForm)) {
                        parser = this.resolver.resolveEntity(null, exForm.substring(ruExForm.length()));
                    }
                }
            }
        }
        if (parser == null) {
            if (this.isKnownReadableSource(wsdlSource)) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
            }
            else if (wsdlLoc != null) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, createReader(wsdlLoc, serviceClass));
            }
            if (parser == null) {
                parser = new XMLEntityResolver.Parser(wsdlLoc, this.createReader(wsdlSource));
            }
        }
        return parser;
    }
    
    private boolean isKnownReadableSource(final Source wsdlSource) {
        return wsdlSource instanceof StreamSource && (((StreamSource)wsdlSource).getInputStream() != null || ((StreamSource)wsdlSource).getReader() != null);
    }
    
    private XMLStreamReader createReader(@NotNull final Source src) throws XMLStreamException {
        return new TidyXMLStreamReader(SourceReaderFactory.createSourceReader(src, true), null);
    }
    
    private void parseImport(@NotNull final URL wsdlLoc) throws XMLStreamException, IOException, SAXException {
        final String systemId = wsdlLoc.toExternalForm();
        XMLEntityResolver.Parser parser = this.resolver.resolveEntity(null, systemId);
        if (parser == null) {
            parser = new XMLEntityResolver.Parser(wsdlLoc, createReader(wsdlLoc));
        }
        this.parseWSDL(parser, true);
    }
    
    private void parseWSDL(final XMLEntityResolver.Parser parser, final boolean imported) throws XMLStreamException, IOException, SAXException {
        final XMLStreamReader reader = parser.parser;
        try {
            if (parser.systemId != null && !this.importedWSDLs.add(parser.systemId.toExternalForm())) {
                return;
            }
            if (reader.getEventType() == 7) {
                XMLStreamReaderUtil.nextElementContent(reader);
            }
            if (WSDLConstants.QNAME_DEFINITIONS.equals(reader.getName())) {
                readNSDecl(this.wsdldef_nsdecl, reader);
            }
            if (reader.getEventType() != 8 && reader.getName().equals(WSDLConstants.QNAME_SCHEMA) && imported) {
                RuntimeWSDLParser.LOGGER.warning(WsdlmodelMessages.WSDL_IMPORT_SHOULD_BE_WSDL(parser.systemId));
                return;
            }
            final String tns = ParserUtil.getMandatoryNonEmptyAttribute(reader, "targetNamespace");
            final String oldTargetNamespace = this.targetNamespace;
            this.targetNamespace = tns;
            while (XMLStreamReaderUtil.nextElementContent(reader) != 2 && reader.getEventType() != 8) {
                final QName name = reader.getName();
                if (WSDLConstants.QNAME_IMPORT.equals(name)) {
                    this.parseImport(parser.systemId, reader);
                }
                else if (WSDLConstants.QNAME_MESSAGE.equals(name)) {
                    this.parseMessage(reader);
                }
                else if (WSDLConstants.QNAME_PORT_TYPE.equals(name)) {
                    this.parsePortType(reader);
                }
                else if (WSDLConstants.QNAME_BINDING.equals(name)) {
                    this.parseBinding(reader);
                }
                else if (WSDLConstants.QNAME_SERVICE.equals(name)) {
                    this.parseService(reader);
                }
                else {
                    this.extensionFacade.definitionsElements(reader);
                }
            }
            this.targetNamespace = oldTargetNamespace;
        }
        finally {
            this.wsdldef_nsdecl = new HashMap<String, String>();
            reader.close();
        }
    }
    
    private void parseService(final XMLStreamReader reader) {
        this.service_nsdecl.putAll(this.wsdldef_nsdecl);
        readNSDecl(this.service_nsdecl, reader);
        final String serviceName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final EditableWSDLService service = new WSDLServiceImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, serviceName));
        this.extensionFacade.serviceAttributes(service, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (WSDLConstants.QNAME_PORT.equals(name)) {
                this.parsePort(reader, service);
                if (reader.getEventType() == 2) {
                    continue;
                }
                XMLStreamReaderUtil.next(reader);
            }
            else {
                this.extensionFacade.serviceElements(service, reader);
            }
        }
        this.wsdlDoc.addService(service);
        this.service_nsdecl = new HashMap<String, String>();
    }
    
    private void parsePort(final XMLStreamReader reader, final EditableWSDLService service) {
        this.port_nsdecl.putAll(this.service_nsdecl);
        readNSDecl(this.port_nsdecl, reader);
        final String portName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final String binding = ParserUtil.getMandatoryNonEmptyAttribute(reader, "binding");
        final QName bindingName = ParserUtil.getQName(reader, binding);
        final QName portQName = new QName(service.getName().getNamespaceURI(), portName);
        final EditableWSDLPort port = new WSDLPortImpl(reader, service, portQName, bindingName);
        this.extensionFacade.portAttributes(port, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (SOAPConstants.QNAME_ADDRESS.equals(name) || SOAPConstants.QNAME_SOAP12ADDRESS.equals(name)) {
                final String location = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
                if (location != null) {
                    try {
                        port.setAddress(new EndpointAddress(location));
                    }
                    catch (final URISyntaxException ex) {}
                }
                XMLStreamReaderUtil.next(reader);
            }
            else {
                if (AddressingVersion.W3C.nsUri.equals(name.getNamespaceURI()) && "EndpointReference".equals(name.getLocalPart())) {
                    try {
                        final StreamReaderBufferCreator creator = new StreamReaderBufferCreator(new MutableXMLStreamBuffer());
                        final XMLStreamBuffer eprbuffer = new XMLStreamBufferMark(this.port_nsdecl, creator);
                        creator.createElementFragment(reader, false);
                        final WSEndpointReference wsepr = new WSEndpointReference(eprbuffer, AddressingVersion.W3C);
                        port.setEPR(wsepr);
                        if (reader.getEventType() == 2 && reader.getName().equals(WSDLConstants.QNAME_PORT)) {
                            break;
                        }
                        continue;
                    }
                    catch (final XMLStreamException e) {
                        throw new WebServiceException(e);
                    }
                }
                this.extensionFacade.portElements(port, reader);
            }
        }
        if (port.getAddress() == null) {
            try {
                port.setAddress(new EndpointAddress(""));
            }
            catch (final URISyntaxException ex2) {}
        }
        service.put(portQName, port);
        this.port_nsdecl = new HashMap<String, String>();
    }
    
    private void parseBinding(final XMLStreamReader reader) {
        final String bindingName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "type");
        if (bindingName == null || portTypeName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        final EditableWSDLBoundPortType binding = new WSDLBoundPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, bindingName), ParserUtil.getQName(reader, portTypeName));
        this.extensionFacade.bindingAttributes(binding, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (WSDLConstants.NS_SOAP_BINDING.equals(name)) {
                final String transport = reader.getAttributeValue(null, "transport");
                binding.setBindingId(createBindingId(transport, SOAPVersion.SOAP_11));
                final String style = reader.getAttributeValue(null, "style");
                if (style != null && style.equals("rpc")) {
                    binding.setStyle(SOAPBinding.Style.RPC);
                }
                else {
                    binding.setStyle(SOAPBinding.Style.DOCUMENT);
                }
                goToEnd(reader);
            }
            else if (WSDLConstants.NS_SOAP12_BINDING.equals(name)) {
                final String transport = reader.getAttributeValue(null, "transport");
                binding.setBindingId(createBindingId(transport, SOAPVersion.SOAP_12));
                final String style = reader.getAttributeValue(null, "style");
                if (style != null && style.equals("rpc")) {
                    binding.setStyle(SOAPBinding.Style.RPC);
                }
                else {
                    binding.setStyle(SOAPBinding.Style.DOCUMENT);
                }
                goToEnd(reader);
            }
            else if (WSDLConstants.QNAME_OPERATION.equals(name)) {
                this.parseBindingOperation(reader, binding);
            }
            else {
                this.extensionFacade.bindingElements(binding, reader);
            }
        }
    }
    
    private static BindingID createBindingId(final String transport, final SOAPVersion soapVersion) {
        if (!transport.equals("http://schemas.xmlsoap.org/soap/http")) {
            for (final BindingIDFactory f : ServiceFinder.find(BindingIDFactory.class)) {
                final BindingID bindingId = f.create(transport, soapVersion);
                if (bindingId != null) {
                    return bindingId;
                }
            }
        }
        return soapVersion.equals(SOAPVersion.SOAP_11) ? BindingID.SOAP11_HTTP : BindingID.SOAP12_HTTP;
    }
    
    private void parseBindingOperation(final XMLStreamReader reader, final EditableWSDLBoundPortType binding) {
        final String bindingOpName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (bindingOpName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        final QName opName = new QName(binding.getPortTypeName().getNamespaceURI(), bindingOpName);
        final EditableWSDLBoundOperation bindingOp = new WSDLBoundOperationImpl(reader, binding, opName);
        binding.put(opName, bindingOp);
        this.extensionFacade.bindingOperationAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            String style = null;
            if (WSDLConstants.QNAME_INPUT.equals(name)) {
                this.parseInputBinding(reader, bindingOp);
            }
            else if (WSDLConstants.QNAME_OUTPUT.equals(name)) {
                this.parseOutputBinding(reader, bindingOp);
            }
            else if (WSDLConstants.QNAME_FAULT.equals(name)) {
                this.parseFaultBinding(reader, bindingOp);
            }
            else if (SOAPConstants.QNAME_OPERATION.equals(name) || SOAPConstants.QNAME_SOAP12OPERATION.equals(name)) {
                style = reader.getAttributeValue(null, "style");
                final String soapAction = reader.getAttributeValue(null, "soapAction");
                if (soapAction != null) {
                    bindingOp.setSoapAction(soapAction);
                }
                goToEnd(reader);
            }
            else {
                this.extensionFacade.bindingOperationElements(bindingOp, reader);
            }
            if (style != null) {
                if (style.equals("rpc")) {
                    bindingOp.setStyle(SOAPBinding.Style.RPC);
                }
                else {
                    bindingOp.setStyle(SOAPBinding.Style.DOCUMENT);
                }
            }
            else {
                bindingOp.setStyle(binding.getStyle());
            }
        }
    }
    
    private void parseInputBinding(final XMLStreamReader reader, final EditableWSDLBoundOperation bindingOp) {
        boolean bodyFound = false;
        this.extensionFacade.bindingOperationInputAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
                bodyFound = true;
                bindingOp.setInputExplicitBodyParts(parseSOAPBodyBinding(reader, bindingOp, BindingMode.INPUT));
                goToEnd(reader);
            }
            else if (SOAPConstants.QNAME_HEADER.equals(name) || SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
                parseSOAPHeaderBinding(reader, bindingOp.getInputParts());
            }
            else if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                parseMimeMultipartBinding(reader, bindingOp, BindingMode.INPUT);
            }
            else {
                this.extensionFacade.bindingOperationInputElements(bindingOp, reader);
            }
        }
    }
    
    private void parseOutputBinding(final XMLStreamReader reader, final EditableWSDLBoundOperation bindingOp) {
        boolean bodyFound = false;
        this.extensionFacade.bindingOperationOutputAttributes(bindingOp, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if ((SOAPConstants.QNAME_BODY.equals(name) || SOAPConstants.QNAME_SOAP12BODY.equals(name)) && !bodyFound) {
                bodyFound = true;
                bindingOp.setOutputExplicitBodyParts(parseSOAPBodyBinding(reader, bindingOp, BindingMode.OUTPUT));
                goToEnd(reader);
            }
            else if (SOAPConstants.QNAME_HEADER.equals(name) || SOAPConstants.QNAME_SOAP12HEADER.equals(name)) {
                parseSOAPHeaderBinding(reader, bindingOp.getOutputParts());
            }
            else if (MIMEConstants.QNAME_MULTIPART_RELATED.equals(name)) {
                parseMimeMultipartBinding(reader, bindingOp, BindingMode.OUTPUT);
            }
            else {
                this.extensionFacade.bindingOperationOutputElements(bindingOp, reader);
            }
        }
    }
    
    private void parseFaultBinding(final XMLStreamReader reader, final EditableWSDLBoundOperation bindingOp) {
        final String faultName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final EditableWSDLBoundFault wsdlBoundFault = new WSDLBoundFaultImpl(reader, faultName, bindingOp);
        bindingOp.addFault(wsdlBoundFault);
        this.extensionFacade.bindingOperationFaultAttributes(wsdlBoundFault, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.bindingOperationFaultElements(wsdlBoundFault, reader);
        }
    }
    
    private static boolean parseSOAPBodyBinding(final XMLStreamReader reader, final EditableWSDLBoundOperation op, final BindingMode mode) {
        final String namespace = reader.getAttributeValue(null, "namespace");
        if (mode == BindingMode.INPUT) {
            op.setRequestNamespace(namespace);
            return parseSOAPBodyBinding(reader, op.getInputParts());
        }
        op.setResponseNamespace(namespace);
        return parseSOAPBodyBinding(reader, op.getOutputParts());
    }
    
    private static boolean parseSOAPBodyBinding(final XMLStreamReader reader, final Map<String, ParameterBinding> parts) {
        final String partsString = reader.getAttributeValue(null, "parts");
        if (partsString != null) {
            final List<String> partsList = XmlUtil.parseTokenList(partsString);
            if (partsList.isEmpty()) {
                parts.put(" ", ParameterBinding.BODY);
            }
            else {
                for (final String part : partsList) {
                    parts.put(part, ParameterBinding.BODY);
                }
            }
            return true;
        }
        return false;
    }
    
    private static void parseSOAPHeaderBinding(final XMLStreamReader reader, final Map<String, ParameterBinding> parts) {
        final String part = reader.getAttributeValue(null, "part");
        if (part == null || part.equals("")) {
            return;
        }
        parts.put(part, ParameterBinding.HEADER);
        goToEnd(reader);
    }
    
    private static void parseMimeMultipartBinding(final XMLStreamReader reader, final EditableWSDLBoundOperation op, final BindingMode mode) {
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (MIMEConstants.QNAME_PART.equals(name)) {
                parseMIMEPart(reader, op, mode);
            }
            else {
                XMLStreamReaderUtil.skipElement(reader);
            }
        }
    }
    
    private static void parseMIMEPart(final XMLStreamReader reader, final EditableWSDLBoundOperation op, final BindingMode mode) {
        boolean bodyFound = false;
        Map<String, ParameterBinding> parts = null;
        if (mode == BindingMode.INPUT) {
            parts = op.getInputParts();
        }
        else if (mode == BindingMode.OUTPUT) {
            parts = op.getOutputParts();
        }
        else if (mode == BindingMode.FAULT) {
            parts = op.getFaultParts();
        }
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (SOAPConstants.QNAME_BODY.equals(name) && !bodyFound) {
                bodyFound = true;
                parseSOAPBodyBinding(reader, op, mode);
                XMLStreamReaderUtil.next(reader);
            }
            else if (SOAPConstants.QNAME_HEADER.equals(name)) {
                bodyFound = true;
                parseSOAPHeaderBinding(reader, parts);
                XMLStreamReaderUtil.next(reader);
            }
            else if (MIMEConstants.QNAME_CONTENT.equals(name)) {
                final String part = reader.getAttributeValue(null, "part");
                final String type = reader.getAttributeValue(null, "type");
                if (part == null || type == null) {
                    XMLStreamReaderUtil.skipElement(reader);
                }
                else {
                    final ParameterBinding sb = ParameterBinding.createAttachment(type);
                    if (parts != null && sb != null && part != null) {
                        parts.put(part, sb);
                    }
                    XMLStreamReaderUtil.next(reader);
                }
            }
            else {
                XMLStreamReaderUtil.skipElement(reader);
            }
        }
    }
    
    protected void parseImport(@Nullable final URL baseURL, final XMLStreamReader reader) throws IOException, SAXException, XMLStreamException {
        final String importLocation = ParserUtil.getMandatoryNonEmptyAttribute(reader, "location");
        URL importURL;
        if (baseURL != null) {
            importURL = new URL(baseURL, importLocation);
        }
        else {
            importURL = new URL(importLocation);
        }
        this.parseImport(importURL);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            XMLStreamReaderUtil.skipElement(reader);
        }
    }
    
    private void parsePortType(final XMLStreamReader reader) {
        final String portTypeName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (portTypeName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        final EditableWSDLPortType portType = new WSDLPortTypeImpl(reader, this.wsdlDoc, new QName(this.targetNamespace, portTypeName));
        this.extensionFacade.portTypeAttributes(portType, reader);
        this.wsdlDoc.addPortType(portType);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (WSDLConstants.QNAME_OPERATION.equals(name)) {
                this.parsePortTypeOperation(reader, portType);
            }
            else {
                this.extensionFacade.portTypeElements(portType, reader);
            }
        }
    }
    
    private void parsePortTypeOperation(final XMLStreamReader reader, final EditableWSDLPortType portType) {
        final String operationName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        if (operationName == null) {
            XMLStreamReaderUtil.skipElement(reader);
            return;
        }
        final QName operationQName = new QName(portType.getName().getNamespaceURI(), operationName);
        final EditableWSDLOperation operation = new WSDLOperationImpl(reader, portType, operationQName);
        this.extensionFacade.portTypeOperationAttributes(operation, reader);
        final String parameterOrder = ParserUtil.getAttribute(reader, "parameterOrder");
        operation.setParameterOrder(parameterOrder);
        portType.put(operationName, operation);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (name.equals(WSDLConstants.QNAME_INPUT)) {
                this.parsePortTypeOperationInput(reader, operation);
            }
            else if (name.equals(WSDLConstants.QNAME_OUTPUT)) {
                this.parsePortTypeOperationOutput(reader, operation);
            }
            else if (name.equals(WSDLConstants.QNAME_FAULT)) {
                this.parsePortTypeOperationFault(reader, operation);
            }
            else {
                this.extensionFacade.portTypeOperationElements(operation, reader);
            }
        }
    }
    
    private void parsePortTypeOperationFault(final XMLStreamReader reader, final EditableWSDLOperation operation) {
        final String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
        final QName msgName = ParserUtil.getQName(reader, msg);
        final String name = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final EditableWSDLFault fault = new WSDLFaultImpl(reader, name, msgName, operation);
        operation.addFault(fault);
        this.extensionFacade.portTypeOperationFaultAttributes(fault, reader);
        this.extensionFacade.portTypeOperationFault(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationFaultElements(fault, reader);
        }
    }
    
    private void parsePortTypeOperationInput(final XMLStreamReader reader, final EditableWSDLOperation operation) {
        final String msg = ParserUtil.getMandatoryNonEmptyAttribute(reader, "message");
        final QName msgName = ParserUtil.getQName(reader, msg);
        final String name = ParserUtil.getAttribute(reader, "name");
        final EditableWSDLInput input = new WSDLInputImpl(reader, name, msgName, operation);
        operation.setInput(input);
        this.extensionFacade.portTypeOperationInputAttributes(input, reader);
        this.extensionFacade.portTypeOperationInput(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationInputElements(input, reader);
        }
    }
    
    private void parsePortTypeOperationOutput(final XMLStreamReader reader, final EditableWSDLOperation operation) {
        final String msg = ParserUtil.getAttribute(reader, "message");
        final QName msgName = ParserUtil.getQName(reader, msg);
        final String name = ParserUtil.getAttribute(reader, "name");
        final EditableWSDLOutput output = new WSDLOutputImpl(reader, name, msgName, operation);
        operation.setOutput(output);
        this.extensionFacade.portTypeOperationOutputAttributes(output, reader);
        this.extensionFacade.portTypeOperationOutput(operation, reader);
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            this.extensionFacade.portTypeOperationOutputElements(output, reader);
        }
    }
    
    private void parseMessage(final XMLStreamReader reader) {
        final String msgName = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
        final EditableWSDLMessage msg = new WSDLMessageImpl(reader, new QName(this.targetNamespace, msgName));
        this.extensionFacade.messageAttributes(msg, reader);
        final int partIndex = 0;
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            final QName name = reader.getName();
            if (WSDLConstants.QNAME_PART.equals(name)) {
                final String part = ParserUtil.getMandatoryNonEmptyAttribute(reader, "name");
                String desc = null;
                final int index = reader.getAttributeCount();
                WSDLDescriptorKind kind = WSDLDescriptorKind.ELEMENT;
                for (int i = 0; i < index; ++i) {
                    final QName descName = reader.getAttributeName(i);
                    if (descName.getLocalPart().equals("element")) {
                        kind = WSDLDescriptorKind.ELEMENT;
                    }
                    else if (descName.getLocalPart().equals("type")) {
                        kind = WSDLDescriptorKind.TYPE;
                    }
                    if (descName.getLocalPart().equals("element") || descName.getLocalPart().equals("type")) {
                        desc = reader.getAttributeValue(i);
                        break;
                    }
                }
                if (desc != null) {
                    final EditableWSDLPart wsdlPart = new WSDLPartImpl(reader, part, partIndex, new WSDLPartDescriptorImpl(reader, ParserUtil.getQName(reader, desc), kind));
                    msg.add(wsdlPart);
                }
                if (reader.getEventType() == 2) {
                    continue;
                }
                goToEnd(reader);
            }
            else {
                this.extensionFacade.messageElements(msg, reader);
            }
        }
        this.wsdlDoc.addMessage(msg);
        if (reader.getEventType() != 2) {
            goToEnd(reader);
        }
    }
    
    private static void goToEnd(final XMLStreamReader reader) {
        while (XMLStreamReaderUtil.nextElementContent(reader) != 2) {
            XMLStreamReaderUtil.skipElement(reader);
        }
    }
    
    private static XMLStreamReader createReader(final URL wsdlLoc) throws IOException, XMLStreamException {
        return createReader(wsdlLoc, null);
    }
    
    private static XMLStreamReader createReader(URL wsdlLoc, final Class<Service> serviceClass) throws IOException, XMLStreamException {
        InputStream stream;
        try {
            stream = wsdlLoc.openStream();
        }
        catch (final IOException io) {
            if (serviceClass != null) {
                final WSDLLocator locator = ContainerResolver.getInstance().getContainer().getSPI(WSDLLocator.class);
                if (locator != null) {
                    final String exForm = wsdlLoc.toExternalForm();
                    final URL ru = serviceClass.getResource(".");
                    String loc = wsdlLoc.getPath();
                    if (ru != null) {
                        final String ruExForm = ru.toExternalForm();
                        if (exForm.startsWith(ruExForm)) {
                            loc = exForm.substring(ruExForm.length());
                        }
                    }
                    wsdlLoc = locator.locateWSDL(serviceClass, loc);
                    if (wsdlLoc != null) {
                        stream = new FilterInputStream(wsdlLoc.openStream()) {
                            boolean closed;
                            
                            @Override
                            public void close() throws IOException {
                                if (!this.closed) {
                                    this.closed = true;
                                    final byte[] buf = new byte[8192];
                                    while (this.read(buf) != -1) {}
                                    super.close();
                                }
                            }
                        };
                        return new TidyXMLStreamReader(XMLStreamReaderFactory.create(wsdlLoc.toExternalForm(), stream, false), stream);
                    }
                }
            }
            throw io;
        }
        return new TidyXMLStreamReader(XMLStreamReaderFactory.create(wsdlLoc.toExternalForm(), stream, false), stream);
    }
    
    private void register(final WSDLParserExtension e) {
        this.extensions.add(new FoolProofParserExtension(e));
    }
    
    private static void readNSDecl(final Map<String, String> ns_map, final XMLStreamReader reader) {
        if (reader.getNamespaceCount() > 0) {
            for (int i = 0; i < reader.getNamespaceCount(); ++i) {
                ns_map.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
            }
        }
    }
    
    static {
        LOGGER = Logger.getLogger(RuntimeWSDLParser.class.getName());
    }
    
    private enum BindingMode
    {
        INPUT, 
        OUTPUT, 
        FAULT;
    }
}
