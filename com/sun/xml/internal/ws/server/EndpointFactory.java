package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.XMLEntityResolver;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.WSFeatureList;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.wsdl.writer.WSDLGeneratorExtension;
import com.oracle.webservices.internal.api.databinding.WSDLResolver;
import com.sun.xml.internal.ws.api.databinding.WSDLGenInfo;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.server.AsyncProvider;
import javax.xml.ws.Provider;
import javax.jws.WebService;
import com.sun.xml.internal.ws.model.ReflectAnnotationReader;
import com.sun.xml.internal.ws.util.HandlerAnnotationInfo;
import javax.xml.ws.soap.SOAPBinding;
import com.sun.xml.internal.ws.util.HandlerAnnotationProcessor;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.InputSource;
import java.util.Iterator;
import java.util.Map;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.net.URL;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.util.HashMap;
import com.sun.xml.internal.ws.server.provider.ProviderInvokerTube;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.policy.jaxws.PolicyUtil;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import com.sun.xml.internal.ws.api.policy.PolicyResolverFactory;
import java.util.List;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import javax.xml.ws.WebServiceProvider;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import org.xml.sax.EntityResolver;
import java.util.Collection;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.server.Container;
import javax.xml.namespace.QName;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.server.Invoker;
import java.util.logging.Logger;

public class EndpointFactory
{
    private static final EndpointFactory instance;
    private static final Logger logger;
    
    public static EndpointFactory getInstance() {
        return EndpointFactory.instance;
    }
    
    public static <T> WSEndpoint<T> createEndpoint(final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, final EntityResolver resolver, final boolean isTransportSynchronous) {
        return createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }
    
    public static <T> WSEndpoint<T> createEndpoint(final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, final EntityResolver resolver, final boolean isTransportSynchronous, final boolean isStandard) {
        EndpointFactory factory = (container != null) ? container.getSPI(EndpointFactory.class) : null;
        if (factory == null) {
            factory = getInstance();
        }
        return (WSEndpoint<T>)factory.create((Class<Object>)implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
    }
    
    public <T> WSEndpoint<T> create(final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, final EntityResolver resolver, final boolean isTransportSynchronous) {
        return this.create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }
    
    public <T> WSEndpoint<T> create(final Class<T> implType, final boolean processHandlerAnnotation, @Nullable Invoker invoker, @Nullable QName serviceName, @Nullable QName portName, @Nullable Container container, @Nullable WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, final EntityResolver resolver, final boolean isTransportSynchronous, final boolean isStandard) {
        if (implType == null) {
            throw new IllegalArgumentException();
        }
        final MetadataReader metadataReader = getExternalMetadatReader(implType, binding);
        if (isStandard) {
            verifyImplementorClass(implType, metadataReader);
        }
        if (invoker == null) {
            invoker = InstanceResolver.createDefault(implType).createInvoker();
        }
        final List<SDDocumentSource> md = new ArrayList<SDDocumentSource>();
        if (metadata != null) {
            md.addAll(metadata);
        }
        if (primaryWsdl != null && !md.contains(primaryWsdl)) {
            md.add(primaryWsdl);
        }
        if (container == null) {
            container = ContainerResolver.getInstance().getContainer();
        }
        if (serviceName == null) {
            serviceName = getDefaultServiceName(implType, metadataReader);
        }
        if (portName == null) {
            portName = getDefaultPortName(serviceName, implType, metadataReader);
        }
        final String serviceNS = serviceName.getNamespaceURI();
        final String portNS = portName.getNamespaceURI();
        if (!serviceNS.equals(portNS)) {
            throw new ServerRtException("wrong.tns.for.port", new Object[] { portNS, serviceNS });
        }
        if (binding == null) {
            binding = BindingImpl.create(BindingID.parse(implType));
        }
        if (isStandard && primaryWsdl != null) {
            verifyPrimaryWSDL(primaryWsdl, serviceName);
        }
        QName portTypeName = null;
        if (isStandard && implType.getAnnotation(WebServiceProvider.class) == null) {
            portTypeName = RuntimeModeler.getPortTypeName(implType, metadataReader);
        }
        List<SDDocumentImpl> docList = categoriseMetadata(md, serviceName, portTypeName);
        SDDocumentImpl primaryDoc = (primaryWsdl != null) ? SDDocumentImpl.create(primaryWsdl, serviceName, portTypeName) : findPrimary(docList);
        WSDLPort wsdlPort = null;
        AbstractSEIModelImpl seiModel = null;
        if (primaryDoc != null) {
            wsdlPort = getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
        }
        final WebServiceFeatureList features = ((BindingImpl)binding).getFeatures();
        if (isStandard) {
            features.parseAnnotations(implType);
        }
        PolicyMap policyMap = null;
        EndpointAwareTube terminal;
        if (this.isUseProviderTube(implType, isStandard)) {
            Iterable<WebServiceFeature> configFtrs;
            if (wsdlPort != null) {
                policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
                configFtrs = wsdlPort.getFeatures();
            }
            else {
                policyMap = PolicyResolverFactory.create().resolve(new PolicyResolver.ServerContext(null, container, implType, false, new PolicyMapMutator[0]));
                configFtrs = PolicyUtil.getPortScopedFeatures(policyMap, serviceName, portName);
            }
            features.mergeFeatures(configFtrs, true);
            terminal = this.createProviderInvokerTube(implType, binding, invoker, container);
        }
        else {
            seiModel = createSEIModel(wsdlPort, implType, serviceName, portName, binding, primaryDoc);
            if (binding instanceof SOAPBindingImpl) {
                ((SOAPBindingImpl)binding).setPortKnownHeaders(((SOAPSEIModel)seiModel).getKnownHeaders());
            }
            if (primaryDoc == null) {
                primaryDoc = generateWSDL(binding, seiModel, docList, container, implType);
                wsdlPort = getWSDLPort(primaryDoc, docList, serviceName, portName, container, resolver);
                seiModel.freeze(wsdlPort);
            }
            policyMap = wsdlPort.getOwner().getParent().getPolicyMap();
            features.mergeFeatures(wsdlPort.getFeatures(), true);
            terminal = this.createSEIInvokerTube(seiModel, invoker, binding);
        }
        if (processHandlerAnnotation) {
            processHandlerAnnotation(binding, implType, serviceName, portName);
        }
        if (primaryDoc != null) {
            docList = findMetadataClosure(primaryDoc, docList, resolver);
        }
        final ServiceDefinitionImpl serviceDefiniton = (primaryDoc != null) ? new ServiceDefinitionImpl(docList, primaryDoc) : null;
        return this.create(serviceName, portName, binding, container, seiModel, wsdlPort, implType, serviceDefiniton, terminal, isTransportSynchronous, policyMap);
    }
    
    protected <T> WSEndpoint<T> create(final QName serviceName, final QName portName, final WSBinding binding, final Container container, final SEIModel seiModel, final WSDLPort wsdlPort, final Class<T> implType, final ServiceDefinitionImpl serviceDefinition, final EndpointAwareTube terminal, final boolean isTransportSynchronous, final PolicyMap policyMap) {
        return new WSEndpointImpl<T>(serviceName, portName, binding, container, seiModel, wsdlPort, implType, serviceDefinition, terminal, isTransportSynchronous, policyMap);
    }
    
    protected boolean isUseProviderTube(final Class<?> implType, final boolean isStandard) {
        return !isStandard || implType.getAnnotation(WebServiceProvider.class) != null;
    }
    
    protected EndpointAwareTube createSEIInvokerTube(final AbstractSEIModelImpl seiModel, final Invoker invoker, final WSBinding binding) {
        return new SEIInvokerTube(seiModel, invoker, binding);
    }
    
    protected <T> EndpointAwareTube createProviderInvokerTube(final Class<T> implType, final WSBinding binding, final Invoker invoker, final Container container) {
        return ProviderInvokerTube.create(implType, binding, invoker, container);
    }
    
    private static List<SDDocumentImpl> findMetadataClosure(final SDDocumentImpl primaryDoc, final List<SDDocumentImpl> docList, final EntityResolver resolver) {
        final Map<String, SDDocumentImpl> oldMap = new HashMap<String, SDDocumentImpl>();
        for (final SDDocumentImpl doc : docList) {
            oldMap.put(doc.getSystemId().toString(), doc);
        }
        final Map<String, SDDocumentImpl> newMap = new HashMap<String, SDDocumentImpl>();
        newMap.put(primaryDoc.getSystemId().toString(), primaryDoc);
        final List<String> remaining = new ArrayList<String>();
        remaining.addAll(primaryDoc.getImports());
        while (!remaining.isEmpty()) {
            final String url = remaining.remove(0);
            SDDocumentImpl doc2 = oldMap.get(url);
            if (doc2 == null && resolver != null) {
                try {
                    final InputSource source = resolver.resolveEntity(null, url);
                    if (source != null) {
                        final MutableXMLStreamBuffer xsb = new MutableXMLStreamBuffer();
                        final XMLStreamReader reader = XmlUtil.newXMLInputFactory(true).createXMLStreamReader(source.getByteStream());
                        xsb.createFromXMLStreamReader(reader);
                        final SDDocumentSource sdocSource = SDDocumentSource.create(new URL(url), xsb);
                        doc2 = SDDocumentImpl.create(sdocSource, null, null);
                    }
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (doc2 != null && !newMap.containsKey(url)) {
                newMap.put(url, doc2);
                remaining.addAll(doc2.getImports());
            }
        }
        final List<SDDocumentImpl> newMetadata = new ArrayList<SDDocumentImpl>();
        newMetadata.addAll(newMap.values());
        return newMetadata;
    }
    
    private static <T> void processHandlerAnnotation(final WSBinding binding, final Class<T> implType, final QName serviceName, final QName portName) {
        final HandlerAnnotationInfo chainInfo = HandlerAnnotationProcessor.buildHandlerInfo(implType, serviceName, portName, binding);
        if (chainInfo != null) {
            binding.setHandlerChain(chainInfo.getHandlers());
            if (binding instanceof SOAPBinding) {
                ((SOAPBinding)binding).setRoles(chainInfo.getRoles());
            }
        }
    }
    
    public static boolean verifyImplementorClass(final Class<?> clz) {
        return verifyImplementorClass(clz, null);
    }
    
    public static boolean verifyImplementorClass(final Class<?> clz, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        final WebServiceProvider wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, clz);
        final WebService ws = metadataReader.getAnnotation(WebService.class, clz);
        if (wsProvider == null && ws == null) {
            throw new IllegalArgumentException(clz + " has neither @WebService nor @WebServiceProvider annotation");
        }
        if (wsProvider != null && ws != null) {
            throw new IllegalArgumentException(clz + " has both @WebService and @WebServiceProvider annotations");
        }
        if (wsProvider == null) {
            return false;
        }
        if (Provider.class.isAssignableFrom(clz) || AsyncProvider.class.isAssignableFrom(clz)) {
            return true;
        }
        throw new IllegalArgumentException(clz + " doesn't implement Provider or AsyncProvider interface");
    }
    
    private static AbstractSEIModelImpl createSEIModel(final WSDLPort wsdlPort, final Class<?> implType, @NotNull final QName serviceName, @NotNull final QName portName, final WSBinding binding, final SDDocumentSource primaryWsdl) {
        final DatabindingFactory fac = DatabindingFactory.newInstance();
        final DatabindingConfig config = new DatabindingConfig();
        config.setEndpointClass(implType);
        config.getMappingInfo().setServiceName(serviceName);
        config.setWsdlPort(wsdlPort);
        config.setWSBinding(binding);
        config.setClassLoader(implType.getClassLoader());
        config.getMappingInfo().setPortName(portName);
        if (primaryWsdl != null) {
            config.setWsdlURL(primaryWsdl.getSystemId());
        }
        config.setMetadataReader(getExternalMetadatReader(implType, binding));
        final DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
        return (AbstractSEIModelImpl)rt.getModel();
    }
    
    public static MetadataReader getExternalMetadatReader(final Class<?> implType, final WSBinding binding) {
        final ExternalMetadataFeature ef = binding.getFeature(ExternalMetadataFeature.class);
        if (ef != null) {
            return ef.getMetadataReader(implType.getClassLoader(), false);
        }
        return null;
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class<?> implType) {
        return getDefaultServiceName(implType, null);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class<?> implType, final MetadataReader metadataReader) {
        return getDefaultServiceName(implType, true, metadataReader);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class<?> implType, final boolean isStandard) {
        return getDefaultServiceName(implType, isStandard, null);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class<?> implType, final boolean isStandard, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        final WebServiceProvider wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, implType);
        QName serviceName;
        if (wsProvider != null) {
            final String tns = wsProvider.targetNamespace();
            final String local = wsProvider.serviceName();
            serviceName = new QName(tns, local);
        }
        else {
            serviceName = RuntimeModeler.getServiceName(implType, metadataReader, isStandard);
        }
        assert serviceName != null;
        return serviceName;
    }
    
    @NotNull
    public static QName getDefaultPortName(final QName serviceName, final Class<?> implType) {
        return getDefaultPortName(serviceName, implType, null);
    }
    
    @NotNull
    public static QName getDefaultPortName(final QName serviceName, final Class<?> implType, final MetadataReader metadataReader) {
        return getDefaultPortName(serviceName, implType, true, metadataReader);
    }
    
    @NotNull
    public static QName getDefaultPortName(final QName serviceName, final Class<?> implType, final boolean isStandard) {
        return getDefaultPortName(serviceName, implType, isStandard, null);
    }
    
    @NotNull
    public static QName getDefaultPortName(final QName serviceName, final Class<?> implType, final boolean isStandard, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        final WebServiceProvider wsProvider = metadataReader.getAnnotation(WebServiceProvider.class, implType);
        QName portName;
        if (wsProvider != null) {
            final String tns = wsProvider.targetNamespace();
            final String local = wsProvider.portName();
            portName = new QName(tns, local);
        }
        else {
            portName = RuntimeModeler.getPortName(implType, metadataReader, serviceName.getNamespaceURI(), isStandard);
        }
        assert portName != null;
        return portName;
    }
    
    @Nullable
    public static String getWsdlLocation(final Class<?> implType) {
        return getWsdlLocation(implType, new ReflectAnnotationReader());
    }
    
    @Nullable
    public static String getWsdlLocation(final Class<?> implType, MetadataReader metadataReader) {
        if (metadataReader == null) {
            metadataReader = new ReflectAnnotationReader();
        }
        final WebService ws = metadataReader.getAnnotation(WebService.class, implType);
        if (ws != null) {
            return nullIfEmpty(ws.wsdlLocation());
        }
        final WebServiceProvider wsProvider = implType.getAnnotation(WebServiceProvider.class);
        assert wsProvider != null;
        return nullIfEmpty(wsProvider.wsdlLocation());
    }
    
    private static String nullIfEmpty(String string) {
        if (string.length() < 1) {
            string = null;
        }
        return string;
    }
    
    private static SDDocumentImpl generateWSDL(final WSBinding binding, final AbstractSEIModelImpl seiModel, final List<SDDocumentImpl> docs, final Container container, final Class implType) {
        final BindingID bindingId = binding.getBindingId();
        if (!bindingId.canGenerateWSDL()) {
            throw new ServerRtException("can.not.generate.wsdl", new Object[] { bindingId });
        }
        if (bindingId.toString().equals("http://java.sun.com/xml/ns/jaxws/2003/05/soap/bindings/HTTP/")) {
            final String msg = ServerMessages.GENERATE_NON_STANDARD_WSDL();
            EndpointFactory.logger.warning(msg);
        }
        final WSDLGenResolver wsdlResolver = new WSDLGenResolver(docs, seiModel.getServiceQName(), seiModel.getPortTypeName());
        final WSDLGenInfo wsdlGenInfo = new WSDLGenInfo();
        wsdlGenInfo.setWsdlResolver(wsdlResolver);
        wsdlGenInfo.setContainer(container);
        wsdlGenInfo.setExtensions(ServiceFinder.find(WSDLGeneratorExtension.class).toArray());
        wsdlGenInfo.setInlineSchemas(false);
        wsdlGenInfo.setSecureXmlProcessingDisabled(isSecureXmlProcessingDisabled(binding.getFeatures()));
        seiModel.getDatabinding().generateWSDL(wsdlGenInfo);
        return wsdlResolver.updateDocs();
    }
    
    private static boolean isSecureXmlProcessingDisabled(final WSFeatureList featureList) {
        return false;
    }
    
    private static List<SDDocumentImpl> categoriseMetadata(final List<SDDocumentSource> src, final QName serviceName, final QName portTypeName) {
        final List<SDDocumentImpl> r = new ArrayList<SDDocumentImpl>(src.size());
        for (final SDDocumentSource doc : src) {
            r.add(SDDocumentImpl.create(doc, serviceName, portTypeName));
        }
        return r;
    }
    
    private static void verifyPrimaryWSDL(@NotNull final SDDocumentSource primaryWsdl, @NotNull final QName serviceName) {
        final SDDocumentImpl primaryDoc = SDDocumentImpl.create(primaryWsdl, serviceName, null);
        if (!(primaryDoc instanceof SDDocument.WSDL)) {
            throw new WebServiceException(primaryWsdl.getSystemId() + " is not a WSDL. But it is passed as a primary WSDL");
        }
        final SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)primaryDoc;
        if (wsdlDoc.hasService()) {
            return;
        }
        if (wsdlDoc.getAllServices().isEmpty()) {
            throw new WebServiceException("Not a primary WSDL=" + primaryWsdl.getSystemId() + " since it doesn't have Service " + serviceName);
        }
        throw new WebServiceException("WSDL " + primaryDoc.getSystemId() + " has the following services " + wsdlDoc.getAllServices() + " but not " + serviceName + ". Maybe you forgot to specify a serviceName and/or targetNamespace in @WebService/@WebServiceProvider?");
    }
    
    @Nullable
    private static SDDocumentImpl findPrimary(@NotNull final List<SDDocumentImpl> docList) {
        SDDocumentImpl primaryDoc = null;
        boolean foundConcrete = false;
        boolean foundAbstract = false;
        for (final SDDocumentImpl doc : docList) {
            if (doc instanceof SDDocument.WSDL) {
                final SDDocument.WSDL wsdlDoc = (SDDocument.WSDL)doc;
                if (wsdlDoc.hasService()) {
                    primaryDoc = doc;
                    if (foundConcrete) {
                        throw new ServerRtException("duplicate.primary.wsdl", new Object[] { doc.getSystemId() });
                    }
                    foundConcrete = true;
                }
                if (!wsdlDoc.hasPortType()) {
                    continue;
                }
                if (foundAbstract) {
                    throw new ServerRtException("duplicate.abstract.wsdl", new Object[] { doc.getSystemId() });
                }
                foundAbstract = true;
            }
        }
        return primaryDoc;
    }
    
    @NotNull
    private static WSDLPort getWSDLPort(final SDDocumentSource primaryWsdl, final List<? extends SDDocumentSource> metadata, @NotNull final QName serviceName, @NotNull final QName portName, final Container container, final EntityResolver resolver) {
        final URL wsdlUrl = primaryWsdl.getSystemId();
        try {
            final WSDLModel wsdlDoc = RuntimeWSDLParser.parse(new XMLEntityResolver.Parser(primaryWsdl), new EntityResolverImpl(metadata, resolver), false, container, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
            if (wsdlDoc.getServices().size() == 0) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_NOSERVICE_IN_WSDLMODEL(wsdlUrl));
            }
            final WSDLService wsdlService = wsdlDoc.getService(serviceName);
            if (wsdlService == null) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICE(serviceName, wsdlUrl));
            }
            final WSDLPort wsdlPort = wsdlService.get(portName);
            if (wsdlPort == null) {
                throw new ServerRtException(ServerMessages.localizableRUNTIME_PARSER_WSDL_INCORRECTSERVICEPORT(serviceName, portName, wsdlUrl));
            }
            return wsdlPort;
        }
        catch (final IOException e) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { wsdlUrl, e });
        }
        catch (final XMLStreamException e2) {
            throw new ServerRtException("runtime.saxparser.exception", new Object[] { e2.getMessage(), e2.getLocation(), e2 });
        }
        catch (final SAXException e3) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { wsdlUrl, e3 });
        }
        catch (final ServiceConfigurationError e4) {
            throw new ServerRtException("runtime.parser.wsdl", new Object[] { wsdlUrl, e4 });
        }
    }
    
    static {
        instance = new EndpointFactory();
        logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
    }
    
    private static final class EntityResolverImpl implements XMLEntityResolver
    {
        private Map<String, SDDocumentSource> metadata;
        private EntityResolver resolver;
        
        public EntityResolverImpl(final List<? extends SDDocumentSource> metadata, final EntityResolver resolver) {
            this.metadata = new HashMap<String, SDDocumentSource>();
            for (final SDDocumentSource doc : metadata) {
                this.metadata.put(doc.getSystemId().toExternalForm(), doc);
            }
            this.resolver = resolver;
        }
        
        @Override
        public Parser resolveEntity(final String publicId, final String systemId) throws IOException, XMLStreamException {
            if (systemId != null) {
                final SDDocumentSource doc = this.metadata.get(systemId);
                if (doc != null) {
                    return new Parser(doc);
                }
            }
            if (this.resolver != null) {
                try {
                    final InputSource source = this.resolver.resolveEntity(publicId, systemId);
                    if (source != null) {
                        final Parser p = new Parser(null, XMLStreamReaderFactory.create(source, true));
                        return p;
                    }
                }
                catch (final SAXException e) {
                    throw new XMLStreamException(e);
                }
            }
            return null;
        }
    }
}
