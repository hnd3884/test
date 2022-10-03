package com.sun.xml.internal.ws.client;

import java.util.concurrent.ThreadFactory;
import com.sun.xml.internal.ws.developer.UsesJAXBContextFeature;
import com.sun.xml.internal.ws.model.SOAPSEIModel;
import com.oracle.webservices.internal.api.databinding.ExternalMetadataFeature;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.db.DatabindingImpl;
import com.sun.xml.internal.ws.api.databinding.DatabindingConfig;
import com.sun.xml.internal.ws.api.databinding.DatabindingFactory;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.Set;
import java.util.HashSet;
import com.sun.xml.internal.ws.client.sei.SEIStub;
import javax.jws.WebService;
import java.security.PermissionCollection;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.lang.reflect.Proxy;
import com.sun.xml.internal.ws.Closeable;
import java.security.Permission;
import java.lang.reflect.InvocationHandler;
import javax.xml.bind.JAXBContext;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import javax.xml.ws.soap.AddressingFeature;
import com.sun.xml.internal.ws.developer.WSBindingProvider;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.pipe.Stubs;
import com.sun.xml.internal.ws.binding.BindingImpl;
import javax.xml.ws.Dispatch;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.model.RuntimeModeler;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import javax.xml.ws.EndpointReference;
import com.sun.xml.internal.ws.resources.ProviderApiMessages;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import org.xml.sax.EntityResolver;
import com.sun.xml.internal.ws.util.ServiceConfigurationError;
import org.xml.sax.SAXException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import com.sun.xml.internal.ws.wsdl.parser.RuntimeWSDLParser;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import java.util.Iterator;
import javax.jws.HandlerChain;
import java.net.MalformedURLException;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import java.util.Collection;
import com.sun.xml.internal.ws.util.JAXWSUtils;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.xml.ws.WebServiceClient;
import com.sun.xml.internal.ws.api.client.ServiceInterceptorFactory;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import javax.xml.ws.handler.HandlerResolver;
import java.util.HashMap;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.WebServiceFeature;
import java.net.URL;
import com.sun.xml.internal.ws.api.client.ServiceInterceptor;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import java.util.concurrent.Executor;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import javax.xml.ws.Service;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.Map;
import com.sun.xml.internal.ws.api.WSService;

public class WSServiceDelegate extends WSService
{
    private final Map<QName, PortInfo> ports;
    @NotNull
    private HandlerConfigurator handlerConfigurator;
    private final Class<? extends Service> serviceClass;
    private final WebServiceFeatureList features;
    @NotNull
    private final QName serviceName;
    private final Map<QName, SEIPortInfo> seiContext;
    private volatile Executor executor;
    @Nullable
    private WSDLService wsdlService;
    private final Container container;
    @NotNull
    final ServiceInterceptor serviceInterceptor;
    private URL wsdlURL;
    protected static final WebServiceFeature[] EMPTY_FEATURES;
    
    protected Map<QName, PortInfo> getQNameToPortInfoMap() {
        return this.ports;
    }
    
    public WSServiceDelegate(final URL wsdlDocumentLocation, final QName serviceName, final Class<? extends Service> serviceClass, final WebServiceFeature... features) {
        this(wsdlDocumentLocation, serviceName, serviceClass, new WebServiceFeatureList(features));
    }
    
    protected WSServiceDelegate(final URL wsdlDocumentLocation, final QName serviceName, final Class<? extends Service> serviceClass, final WebServiceFeatureList features) {
        this((wsdlDocumentLocation == null) ? null : new StreamSource(wsdlDocumentLocation.toExternalForm()), serviceName, serviceClass, features);
        this.wsdlURL = wsdlDocumentLocation;
    }
    
    public WSServiceDelegate(@Nullable final Source wsdl, @NotNull final QName serviceName, @NotNull final Class<? extends Service> serviceClass, final WebServiceFeature... features) {
        this(wsdl, serviceName, serviceClass, new WebServiceFeatureList(features));
    }
    
    protected WSServiceDelegate(@Nullable final Source wsdl, @NotNull final QName serviceName, @NotNull final Class<? extends Service> serviceClass, final WebServiceFeatureList features) {
        this(wsdl, null, serviceName, serviceClass, features);
    }
    
    public WSServiceDelegate(@Nullable final Source wsdl, @Nullable final WSDLService service, @NotNull final QName serviceName, @NotNull final Class<? extends Service> serviceClass, final WebServiceFeature... features) {
        this(wsdl, service, serviceName, serviceClass, new WebServiceFeatureList(features));
    }
    
    public WSServiceDelegate(@Nullable Source wsdl, @Nullable WSDLService service, @NotNull final QName serviceName, @NotNull final Class<? extends Service> serviceClass, final WebServiceFeatureList features) {
        this.ports = new HashMap<QName, PortInfo>();
        this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(null);
        this.seiContext = new HashMap<QName, SEIPortInfo>();
        if (serviceName == null) {
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME_NULL(null));
        }
        this.features = features;
        InitParams initParams = WSServiceDelegate.INIT_PARAMS.get();
        WSServiceDelegate.INIT_PARAMS.set(null);
        if (initParams == null) {
            initParams = WSServiceDelegate.EMPTY_PARAMS;
        }
        this.serviceName = serviceName;
        this.serviceClass = serviceClass;
        Container tContainer = (initParams.getContainer() != null) ? initParams.getContainer() : ContainerResolver.getInstance().getContainer();
        if (tContainer == Container.NONE) {
            tContainer = new ClientContainer();
        }
        this.container = tContainer;
        final ComponentFeature cf = this.features.get(ComponentFeature.class);
        if (cf != null) {
            switch (cf.getTarget()) {
                case SERVICE: {
                    this.getComponents().add(cf.getComponent());
                    break;
                }
                case CONTAINER: {
                    this.container.getComponents().add(cf.getComponent());
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        final ComponentsFeature csf = this.features.get(ComponentsFeature.class);
        if (csf != null) {
            for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                switch (cfi.getTarget()) {
                    case SERVICE: {
                        this.getComponents().add(cfi.getComponent());
                        continue;
                    }
                    case CONTAINER: {
                        this.container.getComponents().add(cfi.getComponent());
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        ServiceInterceptor interceptor = ServiceInterceptorFactory.load(this, Thread.currentThread().getContextClassLoader());
        final ServiceInterceptor si = this.container.getSPI(ServiceInterceptor.class);
        if (si != null) {
            interceptor = ServiceInterceptor.aggregate(interceptor, si);
        }
        this.serviceInterceptor = interceptor;
        Label_0750: {
            if (service == null) {
                if (wsdl == null && serviceClass != Service.class) {
                    final WebServiceClient wsClient = AccessController.doPrivileged((PrivilegedAction<WebServiceClient>)new PrivilegedAction<WebServiceClient>() {
                        @Override
                        public WebServiceClient run() {
                            return serviceClass.getAnnotation(WebServiceClient.class);
                        }
                    });
                    String wsdlLocation = wsClient.wsdlLocation();
                    wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
                    wsdl = new StreamSource(wsdlLocation);
                }
                if (wsdl == null) {
                    break Label_0750;
                }
                try {
                    final URL url = (wsdl.getSystemId() == null) ? null : JAXWSUtils.getEncodedURL(wsdl.getSystemId());
                    final WSDLModel model = this.parseWSDL(url, wsdl, serviceClass);
                    service = model.getService(this.serviceName);
                    if (service == null) {
                        throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(model.getServices().keySet())));
                    }
                    for (final WSDLPort port : service.getPorts()) {
                        this.ports.put(port.getName(), new PortInfo(this, port));
                    }
                    break Label_0750;
                }
                catch (final MalformedURLException e) {
                    throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
                }
            }
            for (final WSDLPort port2 : service.getPorts()) {
                this.ports.put(port2.getName(), new PortInfo(this, port2));
            }
        }
        this.wsdlService = service;
        if (serviceClass != Service.class) {
            final HandlerChain handlerChain = AccessController.doPrivileged((PrivilegedAction<HandlerChain>)new PrivilegedAction<HandlerChain>() {
                @Override
                public HandlerChain run() {
                    return serviceClass.getAnnotation(HandlerChain.class);
                }
            });
            if (handlerChain != null) {
                this.handlerConfigurator = new HandlerConfigurator.AnnotationConfigurator(this);
            }
        }
    }
    
    private WSDLModel parseWSDL(final URL wsdlDocumentLocation, final Source wsdlSource, final Class serviceClass) {
        try {
            return RuntimeWSDLParser.parse(wsdlDocumentLocation, wsdlSource, this.createCatalogResolver(), true, this.getContainer(), serviceClass, (WSDLParserExtension[])ServiceFinder.find(WSDLParserExtension.class).toArray());
        }
        catch (final IOException e) {
            throw new WebServiceException(e);
        }
        catch (final XMLStreamException e2) {
            throw new WebServiceException(e2);
        }
        catch (final SAXException e3) {
            throw new WebServiceException(e3);
        }
        catch (final ServiceConfigurationError e4) {
            throw new WebServiceException(e4);
        }
    }
    
    protected EntityResolver createCatalogResolver() {
        return XmlUtil.createDefaultCatalogResolver();
    }
    
    @Override
    public Executor getExecutor() {
        return this.executor;
    }
    
    @Override
    public void setExecutor(final Executor executor) {
        this.executor = executor;
    }
    
    @Override
    public HandlerResolver getHandlerResolver() {
        return this.handlerConfigurator.getResolver();
    }
    
    final HandlerConfigurator getHandlerConfigurator() {
        return this.handlerConfigurator;
    }
    
    @Override
    public void setHandlerResolver(final HandlerResolver resolver) {
        this.handlerConfigurator = new HandlerConfigurator.HandlerResolverImpl(resolver);
    }
    
    @Override
    public <T> T getPort(final QName portName, final Class<T> portInterface) throws WebServiceException {
        return this.getPort(portName, portInterface, WSServiceDelegate.EMPTY_FEATURES);
    }
    
    @Override
    public <T> T getPort(final QName portName, final Class<T> portInterface, final WebServiceFeature... features) {
        if (portName == null || portInterface == null) {
            throw new IllegalArgumentException();
        }
        WSDLService tWsdlService = this.wsdlService;
        if (tWsdlService == null) {
            tWsdlService = this.getWSDLModelfromSEI(portInterface);
            if (tWsdlService == null) {
                throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
            }
        }
        final WSDLPort portModel = this.getPortModel(tWsdlService, portName);
        return this.getPort(portModel.getEPR(), portName, portInterface, new WebServiceFeatureList(features));
    }
    
    @Override
    public <T> T getPort(final EndpointReference epr, final Class<T> portInterface, final WebServiceFeature... features) {
        return this.getPort(WSEndpointReference.create(epr), portInterface, features);
    }
    
    @Override
    public <T> T getPort(final WSEndpointReference wsepr, final Class<T> portInterface, final WebServiceFeature... features) {
        final WebServiceFeatureList featureList = new WebServiceFeatureList(features);
        final QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(featureList, portInterface.getClassLoader()));
        final QName portName = this.getPortNameFromEPR(wsepr, portTypeName);
        return this.getPort(wsepr, portName, portInterface, featureList);
    }
    
    protected <T> T getPort(final WSEndpointReference wsepr, final QName portName, final Class<T> portInterface, final WebServiceFeatureList features) {
        final ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        final ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                    throw new IllegalArgumentException();
                }
            }
        }
        features.addAll(this.features);
        final SEIPortInfo spi = this.addSEI(portName, portInterface, features);
        return this.createEndpointIFBaseProxy(wsepr, portName, portInterface, features, spi);
    }
    
    @Override
    public <T> T getPort(final Class<T> portInterface, final WebServiceFeature... features) {
        final QName portTypeName = RuntimeModeler.getPortTypeName(portInterface, this.getMetadadaReader(new WebServiceFeatureList(features), portInterface.getClassLoader()));
        WSDLService tmpWsdlService = this.wsdlService;
        if (tmpWsdlService == null) {
            tmpWsdlService = this.getWSDLModelfromSEI(portInterface);
            if (tmpWsdlService == null) {
                throw new WebServiceException(ProviderApiMessages.NO_WSDL_NO_PORT(portInterface.getName()));
            }
        }
        final WSDLPort port = tmpWsdlService.getMatchingPort(portTypeName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
        }
        final QName portName = port.getName();
        return this.getPort(portName, portInterface, features);
    }
    
    @Override
    public <T> T getPort(final Class<T> portInterface) throws WebServiceException {
        return this.getPort(portInterface, WSServiceDelegate.EMPTY_FEATURES);
    }
    
    @Override
    public void addPort(final QName portName, final String bindingId, final String endpointAddress) throws WebServiceException {
        if (!this.ports.containsKey(portName)) {
            final BindingID bid = (bindingId == null) ? BindingID.SOAP11_HTTP : BindingID.parse(bindingId);
            this.ports.put(portName, new PortInfo(this, (endpointAddress == null) ? null : EndpointAddress.create(endpointAddress), portName, bid));
            return;
        }
        throw new WebServiceException(DispatchMessages.DUPLICATE_PORT(portName.toString()));
    }
    
    @Override
    public <T> Dispatch<T> createDispatch(final QName portName, final Class<T> aClass, final Service.Mode mode) throws WebServiceException {
        return this.createDispatch(portName, aClass, mode, WSServiceDelegate.EMPTY_FEATURES);
    }
    
    @Override
    public <T> Dispatch<T> createDispatch(final QName portName, final WSEndpointReference wsepr, final Class<T> aClass, final Service.Mode mode, final WebServiceFeature... features) {
        return this.createDispatch(portName, wsepr, aClass, mode, new WebServiceFeatureList(features));
    }
    
    public <T> Dispatch<T> createDispatch(final QName portName, final WSEndpointReference wsepr, final Class<T> aClass, final Service.Mode mode, final WebServiceFeatureList features) {
        final PortInfo port = this.safeGetPort(portName);
        final ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        final ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                    throw new IllegalArgumentException();
                }
            }
        }
        features.addAll(this.features);
        final BindingImpl binding = port.createBinding(features, null, null);
        binding.setMode(mode);
        final Dispatch<T> dispatch = Stubs.createDispatch(port, this, binding, aClass, mode, wsepr);
        this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
        return dispatch;
    }
    
    @Override
    public <T> Dispatch<T> createDispatch(final QName portName, final Class<T> aClass, final Service.Mode mode, final WebServiceFeature... features) {
        return this.createDispatch(portName, aClass, mode, new WebServiceFeatureList(features));
    }
    
    public <T> Dispatch<T> createDispatch(final QName portName, final Class<T> aClass, final Service.Mode mode, final WebServiceFeatureList features) {
        WSEndpointReference wsepr = null;
        boolean isAddressingEnabled = false;
        AddressingFeature af = features.get(AddressingFeature.class);
        if (af == null) {
            af = this.features.get(AddressingFeature.class);
        }
        if (af != null && af.isEnabled()) {
            isAddressingEnabled = true;
        }
        MemberSubmissionAddressingFeature msa = features.get(MemberSubmissionAddressingFeature.class);
        if (msa == null) {
            msa = this.features.get(MemberSubmissionAddressingFeature.class);
        }
        if (msa != null && msa.isEnabled()) {
            isAddressingEnabled = true;
        }
        if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
            wsepr = this.wsdlService.get(portName).getEPR();
        }
        return this.createDispatch(portName, wsepr, aClass, mode, features);
    }
    
    @Override
    public <T> Dispatch<T> createDispatch(final EndpointReference endpointReference, final Class<T> type, final Service.Mode mode, final WebServiceFeature... features) {
        final WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        final QName portName = this.addPortEpr(wsepr);
        return this.createDispatch(portName, wsepr, type, mode, features);
    }
    
    @NotNull
    public PortInfo safeGetPort(final QName portName) {
        final PortInfo port = this.ports.get(portName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildNameList(this.ports.keySet())));
        }
        return port;
    }
    
    private StringBuilder buildNameList(final Collection<QName> names) {
        final StringBuilder sb = new StringBuilder();
        for (final QName qn : names) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(qn);
        }
        return sb;
    }
    
    public EndpointAddress getEndpointAddress(final QName qName) {
        final PortInfo p = this.ports.get(qName);
        return (p != null) ? p.targetEndpoint : null;
    }
    
    @Override
    public Dispatch<Object> createDispatch(final QName portName, final JAXBContext jaxbContext, final Service.Mode mode) throws WebServiceException {
        return this.createDispatch(portName, jaxbContext, mode, WSServiceDelegate.EMPTY_FEATURES);
    }
    
    @Override
    public Dispatch<Object> createDispatch(final QName portName, final WSEndpointReference wsepr, final JAXBContext jaxbContext, final Service.Mode mode, final WebServiceFeature... features) {
        return this.createDispatch(portName, wsepr, jaxbContext, mode, new WebServiceFeatureList(features));
    }
    
    protected Dispatch<Object> createDispatch(final QName portName, final WSEndpointReference wsepr, final JAXBContext jaxbContext, final Service.Mode mode, final WebServiceFeatureList features) {
        final PortInfo port = this.safeGetPort(portName);
        final ComponentFeature cf = features.get(ComponentFeature.class);
        if (cf != null && !ComponentFeature.Target.STUB.equals(cf.getTarget())) {
            throw new IllegalArgumentException();
        }
        final ComponentsFeature csf = features.get(ComponentsFeature.class);
        if (csf != null) {
            for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                if (!ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                    throw new IllegalArgumentException();
                }
            }
        }
        features.addAll(this.features);
        final BindingImpl binding = port.createBinding(features, null, null);
        binding.setMode(mode);
        final Dispatch<Object> dispatch = Stubs.createJAXBDispatch(port, binding, jaxbContext, mode, wsepr);
        this.serviceInterceptor.postCreateDispatch((WSBindingProvider)dispatch);
        return dispatch;
    }
    
    @NotNull
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public Dispatch<Object> createDispatch(final QName portName, final JAXBContext jaxbContext, final Service.Mode mode, final WebServiceFeature... webServiceFeatures) {
        return this.createDispatch(portName, jaxbContext, mode, new WebServiceFeatureList(webServiceFeatures));
    }
    
    protected Dispatch<Object> createDispatch(final QName portName, final JAXBContext jaxbContext, final Service.Mode mode, final WebServiceFeatureList features) {
        WSEndpointReference wsepr = null;
        boolean isAddressingEnabled = false;
        AddressingFeature af = features.get(AddressingFeature.class);
        if (af == null) {
            af = this.features.get(AddressingFeature.class);
        }
        if (af != null && af.isEnabled()) {
            isAddressingEnabled = true;
        }
        MemberSubmissionAddressingFeature msa = features.get(MemberSubmissionAddressingFeature.class);
        if (msa == null) {
            msa = this.features.get(MemberSubmissionAddressingFeature.class);
        }
        if (msa != null && msa.isEnabled()) {
            isAddressingEnabled = true;
        }
        if (isAddressingEnabled && this.wsdlService != null && this.wsdlService.get(portName) != null) {
            wsepr = this.wsdlService.get(portName).getEPR();
        }
        return this.createDispatch(portName, wsepr, jaxbContext, mode, features);
    }
    
    @Override
    public Dispatch<Object> createDispatch(final EndpointReference endpointReference, final JAXBContext context, final Service.Mode mode, final WebServiceFeature... features) {
        final WSEndpointReference wsepr = new WSEndpointReference(endpointReference);
        final QName portName = this.addPortEpr(wsepr);
        return this.createDispatch(portName, wsepr, context, mode, features);
    }
    
    private QName addPortEpr(final WSEndpointReference wsepr) {
        if (wsepr == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_EPR());
        }
        final QName eprPortName = this.getPortNameFromEPR(wsepr, null);
        final PortInfo portInfo = new PortInfo(this, (wsepr.getAddress() == null) ? null : EndpointAddress.create(wsepr.getAddress()), eprPortName, this.getPortModel(this.wsdlService, eprPortName).getBinding().getBindingId());
        if (!this.ports.containsKey(eprPortName)) {
            this.ports.put(eprPortName, portInfo);
        }
        return eprPortName;
    }
    
    private QName getPortNameFromEPR(@NotNull final WSEndpointReference wsepr, @Nullable final QName portTypeName) {
        final WSEndpointReference.Metadata metadata = wsepr.getMetaData();
        final QName eprServiceName = metadata.getServiceName();
        final QName eprPortName = metadata.getPortName();
        if (eprServiceName != null && !eprServiceName.equals(this.serviceName)) {
            throw new WebServiceException("EndpointReference WSDL ServiceName differs from Service Instance WSDL Service QName.\n The two Service QNames must match");
        }
        if (this.wsdlService == null) {
            final Source eprWsdlSource = metadata.getWsdlSource();
            if (eprWsdlSource == null) {
                throw new WebServiceException(ProviderApiMessages.NULL_WSDL());
            }
            try {
                final WSDLModel eprWsdlMdl = this.parseWSDL(new URL(wsepr.getAddress()), eprWsdlSource, null);
                this.wsdlService = eprWsdlMdl.getService(this.serviceName);
                if (this.wsdlService == null) {
                    throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(eprWsdlMdl.getServices().keySet())));
                }
            }
            catch (final MalformedURLException e) {
                throw new WebServiceException(ClientMessages.INVALID_ADDRESS(wsepr.getAddress()));
            }
        }
        QName portName = eprPortName;
        if (portName == null && portTypeName != null) {
            final WSDLPort port = this.wsdlService.getMatchingPort(portTypeName);
            if (port == null) {
                throw new WebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName));
            }
            portName = port.getName();
        }
        if (portName == null) {
            throw new WebServiceException(ProviderApiMessages.NULL_PORTNAME());
        }
        if (this.wsdlService.get(portName) == null) {
            throw new WebServiceException(ClientMessages.INVALID_EPR_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        return portName;
    }
    
    private <T> T createProxy(final Class<T> portInterface, final InvocationHandler pis) {
        final ClassLoader loader = getDelegatingLoader(portInterface.getClassLoader(), WSServiceDelegate.class.getClassLoader());
        final RuntimePermission perm = new RuntimePermission("accessClassInPackage.com.sun.xml.internal.*");
        final PermissionCollection perms = perm.newPermissionCollection();
        perms.add(perm);
        return AccessController.doPrivileged((PrivilegedAction<T>)new PrivilegedAction<T>() {
            @Override
            public T run() {
                final Object proxy = Proxy.newProxyInstance(loader, new Class[] { portInterface, WSBindingProvider.class, Closeable.class }, pis);
                return portInterface.cast(proxy);
            }
        }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, perms) }));
    }
    
    private WSDLService getWSDLModelfromSEI(final Class sei) {
        final WebService ws = AccessController.doPrivileged((PrivilegedAction<WebService>)new PrivilegedAction<WebService>() {
            @Override
            public WebService run() {
                return sei.getAnnotation(WebService.class);
            }
        });
        if (ws == null || ws.wsdlLocation().equals("")) {
            return null;
        }
        String wsdlLocation = ws.wsdlLocation();
        wsdlLocation = JAXWSUtils.absolutize(JAXWSUtils.getFileOrURLName(wsdlLocation));
        final Source wsdl = new StreamSource(wsdlLocation);
        WSDLService service = null;
        try {
            final URL url = (wsdl.getSystemId() == null) ? null : new URL(wsdl.getSystemId());
            final WSDLModel model = this.parseWSDL(url, wsdl, sei);
            service = model.getService(this.serviceName);
            if (service == null) {
                throw new WebServiceException(ClientMessages.INVALID_SERVICE_NAME(this.serviceName, this.buildNameList(model.getServices().keySet())));
            }
        }
        catch (final MalformedURLException e) {
            throw new WebServiceException(ClientMessages.INVALID_WSDL_URL(wsdl.getSystemId()));
        }
        return service;
    }
    
    @Override
    public QName getServiceName() {
        return this.serviceName;
    }
    
    public Class getServiceClass() {
        return this.serviceClass;
    }
    
    @Override
    public Iterator<QName> getPorts() throws WebServiceException {
        return this.ports.keySet().iterator();
    }
    
    @Override
    public URL getWSDLDocumentLocation() {
        if (this.wsdlService == null) {
            return null;
        }
        try {
            return new URL(this.wsdlService.getParent().getLocation().getSystemId());
        }
        catch (final MalformedURLException e) {
            throw new AssertionError((Object)e);
        }
    }
    
    private <T> T createEndpointIFBaseProxy(@Nullable final WSEndpointReference epr, final QName portName, final Class<T> portInterface, final WebServiceFeatureList webServiceFeatures, final SEIPortInfo eif) {
        if (this.wsdlService == null) {
            throw new WebServiceException(ClientMessages.INVALID_SERVICE_NO_WSDL(this.serviceName));
        }
        if (this.wsdlService.get(portName) == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        final BindingImpl binding = eif.createBinding(webServiceFeatures, portInterface);
        final InvocationHandler pis = this.getStubHandler(binding, eif, epr);
        final T proxy = this.createProxy(portInterface, pis);
        if (this.serviceInterceptor != null) {
            this.serviceInterceptor.postCreateProxy((WSBindingProvider)proxy, portInterface);
        }
        return proxy;
    }
    
    protected InvocationHandler getStubHandler(final BindingImpl binding, final SEIPortInfo eif, @Nullable final WSEndpointReference epr) {
        return new SEIStub(eif, binding, eif.model, epr);
    }
    
    private StringBuilder buildWsdlPortNames() {
        final Set<QName> wsdlPortNames = new HashSet<QName>();
        for (final WSDLPort port : this.wsdlService.getPorts()) {
            wsdlPortNames.add(port.getName());
        }
        return this.buildNameList(wsdlPortNames);
    }
    
    @NotNull
    public WSDLPort getPortModel(final WSDLService wsdlService, final QName portName) {
        final WSDLPort port = wsdlService.get(portName);
        if (port == null) {
            throw new WebServiceException(ClientMessages.INVALID_PORT_NAME(portName, this.buildWsdlPortNames()));
        }
        return port;
    }
    
    private SEIPortInfo addSEI(final QName portName, final Class portInterface, final WebServiceFeatureList features) throws WebServiceException {
        final boolean ownModel = this.useOwnSEIModel(features);
        if (ownModel) {
            return this.createSEIPortInfo(portName, portInterface, features);
        }
        SEIPortInfo spi = this.seiContext.get(portName);
        if (spi == null) {
            spi = this.createSEIPortInfo(portName, portInterface, features);
            this.seiContext.put(spi.portName, spi);
            this.ports.put(spi.portName, spi);
        }
        return spi;
    }
    
    public SEIModel buildRuntimeModel(final QName serviceName, final QName portName, final Class portInterface, final WSDLPort wsdlPort, final WebServiceFeatureList features) {
        final DatabindingFactory fac = DatabindingFactory.newInstance();
        final DatabindingConfig config = new DatabindingConfig();
        config.setContractClass(portInterface);
        config.getMappingInfo().setServiceName(serviceName);
        config.setWsdlPort(wsdlPort);
        config.setFeatures(features);
        config.setClassLoader(portInterface.getClassLoader());
        config.getMappingInfo().setPortName(portName);
        config.setWsdlURL(this.wsdlURL);
        config.setMetadataReader(this.getMetadadaReader(features, portInterface.getClassLoader()));
        final DatabindingImpl rt = (DatabindingImpl)fac.createRuntime(config);
        return rt.getModel();
    }
    
    private MetadataReader getMetadadaReader(final WebServiceFeatureList features, final ClassLoader classLoader) {
        if (features == null) {
            return null;
        }
        final ExternalMetadataFeature ef = features.get(ExternalMetadataFeature.class);
        if (ef != null) {
            return ef.getMetadataReader(classLoader, false);
        }
        return null;
    }
    
    private SEIPortInfo createSEIPortInfo(final QName portName, final Class portInterface, final WebServiceFeatureList features) {
        final WSDLPort wsdlPort = this.getPortModel(this.wsdlService, portName);
        final SEIModel model = this.buildRuntimeModel(this.serviceName, portName, portInterface, wsdlPort, features);
        return new SEIPortInfo(this, portInterface, (SOAPSEIModel)model, wsdlPort);
    }
    
    private boolean useOwnSEIModel(final WebServiceFeatureList features) {
        return features.contains(UsesJAXBContextFeature.class);
    }
    
    public WSDLService getWsdlService() {
        return this.wsdlService;
    }
    
    private static ClassLoader getDelegatingLoader(final ClassLoader loader1, final ClassLoader loader2) {
        if (loader1 == null) {
            return loader2;
        }
        if (loader2 == null) {
            return loader1;
        }
        return new DelegatingLoader(loader1, loader2);
    }
    
    static {
        EMPTY_FEATURES = new WebServiceFeature[0];
    }
    
    static class DaemonThreadFactory implements ThreadFactory
    {
        @Override
        public Thread newThread(final Runnable r) {
            final Thread daemonThread = new Thread(r);
            daemonThread.setDaemon(Boolean.TRUE);
            return daemonThread;
        }
    }
    
    private static final class DelegatingLoader extends ClassLoader
    {
        private final ClassLoader loader;
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = 31 * result + ((this.loader == null) ? 0 : this.loader.hashCode());
            result = 31 * result + ((this.getParent() == null) ? 0 : this.getParent().hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final DelegatingLoader other = (DelegatingLoader)obj;
            if (this.loader == null) {
                if (other.loader != null) {
                    return false;
                }
            }
            else if (!this.loader.equals(other.loader)) {
                return false;
            }
            if (this.getParent() == null) {
                if (other.getParent() != null) {
                    return false;
                }
            }
            else if (!this.getParent().equals(other.getParent())) {
                return false;
            }
            return true;
        }
        
        DelegatingLoader(final ClassLoader loader1, final ClassLoader loader2) {
            super(loader2);
            this.loader = loader1;
        }
        
        @Override
        protected Class findClass(final String name) throws ClassNotFoundException {
            return this.loader.loadClass(name);
        }
        
        @Override
        protected URL findResource(final String name) {
            return this.loader.getResource(name);
        }
    }
}
