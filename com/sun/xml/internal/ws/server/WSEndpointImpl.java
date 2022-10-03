package com.sun.xml.internal.ws.server;

import java.util.HashSet;
import com.sun.xml.internal.ws.api.server.ServiceDefinition;
import javax.management.ObjectName;
import java.io.IOException;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import java.util.List;
import java.util.Arrays;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.api.server.EndpointComponent;
import java.lang.reflect.Method;
import com.sun.xml.internal.ws.resources.HandlerMessages;
import java.util.logging.Level;
import javax.annotation.PreDestroy;
import javax.xml.ws.handler.Handler;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.model.CheckedExceptionImpl;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.concurrent.Executor;
import java.util.Collection;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.api.pipe.ServerPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.addressing.EPRSDDocumentFilter;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.server.EndpointReferenceExtensionContributor;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.api.server.EndpointAwareCodec;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.ComponentFeature;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.HashMap;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.Component;
import java.util.Set;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import java.util.Map;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.server.LazyMOMProvider;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public class WSEndpointImpl<T> extends WSEndpoint<T> implements LazyMOMProvider.WSEndpointScopeChangeListener
{
    private static final Logger logger;
    @NotNull
    private final QName serviceName;
    @NotNull
    private final QName portName;
    protected final WSBinding binding;
    private final SEIModel seiModel;
    @NotNull
    private final Container container;
    private final WSDLPort port;
    protected final Tube masterTubeline;
    private final ServiceDefinitionImpl serviceDef;
    private final SOAPVersion soapVersion;
    private final Engine engine;
    @NotNull
    private final Codec masterCodec;
    @NotNull
    private final PolicyMap endpointPolicy;
    private final Pool<Tube> tubePool;
    private final OperationDispatcher operationDispatcher;
    @NotNull
    private ManagedObjectManager managedObjectManager;
    private boolean managedObjectManagerClosed;
    private final Object managedObjectManagerLock;
    private LazyMOMProvider.Scope lazyMOMProviderScope;
    @NotNull
    private final ServerTubeAssemblerContext context;
    private Map<QName, WSEndpointReference.EPRExtension> endpointReferenceExtensions;
    private boolean disposed;
    private final Class<T> implementationClass;
    @NotNull
    private final WSDLProperties wsdlProperties;
    private final Set<Component> componentRegistry;
    private static final Logger monitoringLogger;
    
    protected WSEndpointImpl(@NotNull final QName serviceName, @NotNull final QName portName, final WSBinding binding, final Container container, final SEIModel seiModel, final WSDLPort port, final Class<T> implementationClass, @Nullable final ServiceDefinitionImpl serviceDef, final EndpointAwareTube terminalTube, final boolean isSynchronous, final PolicyMap endpointPolicy) {
        this.managedObjectManagerClosed = false;
        this.managedObjectManagerLock = new Object();
        this.lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
        this.endpointReferenceExtensions = new HashMap<QName, WSEndpointReference.EPRExtension>();
        this.componentRegistry = new CopyOnWriteArraySet<Component>();
        this.serviceName = serviceName;
        this.portName = portName;
        this.binding = binding;
        this.soapVersion = binding.getSOAPVersion();
        this.container = container;
        this.port = port;
        this.implementationClass = implementationClass;
        this.serviceDef = serviceDef;
        this.seiModel = seiModel;
        this.endpointPolicy = endpointPolicy;
        LazyMOMProvider.INSTANCE.registerEndpoint(this);
        this.initManagedObjectManager();
        if (serviceDef != null) {
            serviceDef.setOwner(this);
        }
        final ComponentFeature cf = binding.getFeature(ComponentFeature.class);
        if (cf != null) {
            switch (cf.getTarget()) {
                case ENDPOINT: {
                    this.componentRegistry.add(cf.getComponent());
                    break;
                }
                case CONTAINER: {
                    container.getComponents().add(cf.getComponent());
                    break;
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        final ComponentsFeature csf = binding.getFeature(ComponentsFeature.class);
        if (csf != null) {
            for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                switch (cfi.getTarget()) {
                    case ENDPOINT: {
                        this.componentRegistry.add(cfi.getComponent());
                        continue;
                    }
                    case CONTAINER: {
                        container.getComponents().add(cfi.getComponent());
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }
        final TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), binding.getBindingId(), container);
        assert assembler != null;
        this.operationDispatcher = ((port == null) ? null : new OperationDispatcher(port, binding, seiModel));
        this.context = this.createServerTubeAssemblerContext(terminalTube, isSynchronous);
        this.masterTubeline = assembler.createServer(this.context);
        Codec c = this.context.getCodec();
        if (c instanceof EndpointAwareCodec) {
            c = c.copy();
            ((EndpointAwareCodec)c).setEndpoint(this);
        }
        this.masterCodec = c;
        this.tubePool = new Pool.TubePool(this.masterTubeline);
        terminalTube.setEndpoint(this);
        this.engine = new Engine(this.toString(), container);
        this.wsdlProperties = ((port == null) ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel));
        final Map<QName, WSEndpointReference.EPRExtension> eprExtensions = new HashMap<QName, WSEndpointReference.EPRExtension>();
        try {
            if (port != null) {
                final WSEndpointReference wsdlEpr = port.getEPR();
                if (wsdlEpr != null) {
                    for (final WSEndpointReference.EPRExtension extnEl : wsdlEpr.getEPRExtensions()) {
                        eprExtensions.put(extnEl.getQName(), extnEl);
                    }
                }
            }
            final EndpointReferenceExtensionContributor[] array;
            final EndpointReferenceExtensionContributor[] eprExtnContributors = array = ServiceFinder.find(EndpointReferenceExtensionContributor.class).toArray();
            for (final EndpointReferenceExtensionContributor eprExtnContributor : array) {
                final WSEndpointReference.EPRExtension wsdlEPRExtn = eprExtensions.remove(eprExtnContributor.getQName());
                final WSEndpointReference.EPRExtension endpointEprExtn = eprExtnContributor.getEPRExtension(this, wsdlEPRExtn);
                if (endpointEprExtn != null) {
                    eprExtensions.put(endpointEprExtn.getQName(), endpointEprExtn);
                }
            }
            for (final WSEndpointReference.EPRExtension extn : eprExtensions.values()) {
                this.endpointReferenceExtensions.put(extn.getQName(), new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(extn.readAsXMLStreamReader()), extn.getQName()));
            }
        }
        catch (final XMLStreamException ex) {
            throw new WebServiceException(ex);
        }
        if (!eprExtensions.isEmpty()) {
            serviceDef.addFilter(new EPRSDDocumentFilter(this));
        }
    }
    
    protected ServerTubeAssemblerContext createServerTubeAssemblerContext(final EndpointAwareTube terminalTube, final boolean isSynchronous) {
        final ServerTubeAssemblerContext ctx = new ServerPipeAssemblerContext(this.seiModel, this.port, this, terminalTube, isSynchronous);
        return ctx;
    }
    
    protected WSEndpointImpl(@NotNull final QName serviceName, @NotNull final QName portName, final WSBinding binding, final Container container, final SEIModel seiModel, final WSDLPort port, final Tube masterTubeline) {
        this.managedObjectManagerClosed = false;
        this.managedObjectManagerLock = new Object();
        this.lazyMOMProviderScope = LazyMOMProvider.Scope.STANDALONE;
        this.endpointReferenceExtensions = new HashMap<QName, WSEndpointReference.EPRExtension>();
        this.componentRegistry = new CopyOnWriteArraySet<Component>();
        this.serviceName = serviceName;
        this.portName = portName;
        this.binding = binding;
        this.soapVersion = binding.getSOAPVersion();
        this.container = container;
        this.endpointPolicy = null;
        this.port = port;
        this.seiModel = seiModel;
        this.serviceDef = null;
        this.implementationClass = null;
        this.masterTubeline = masterTubeline;
        this.masterCodec = ((BindingImpl)this.binding).createCodec();
        LazyMOMProvider.INSTANCE.registerEndpoint(this);
        this.initManagedObjectManager();
        this.operationDispatcher = ((port == null) ? null : new OperationDispatcher(port, binding, seiModel));
        this.context = new ServerPipeAssemblerContext(seiModel, port, this, null, false);
        this.tubePool = new Pool.TubePool(masterTubeline);
        this.engine = new Engine(this.toString(), container);
        this.wsdlProperties = ((port == null) ? new WSDLDirectProperties(serviceName, portName, seiModel) : new WSDLPortProperties(port, seiModel));
    }
    
    public Collection<WSEndpointReference.EPRExtension> getEndpointReferenceExtensions() {
        return this.endpointReferenceExtensions.values();
    }
    
    @Nullable
    @Override
    public OperationDispatcher getOperationDispatcher() {
        return this.operationDispatcher;
    }
    
    @Override
    public PolicyMap getPolicyMap() {
        return this.endpointPolicy;
    }
    
    @NotNull
    @Override
    public Class<T> getImplementationClass() {
        return this.implementationClass;
    }
    
    @NotNull
    @Override
    public WSBinding getBinding() {
        return this.binding;
    }
    
    @NotNull
    @Override
    public Container getContainer() {
        return this.container;
    }
    
    @Override
    public WSDLPort getPort() {
        return this.port;
    }
    
    @Nullable
    @Override
    public SEIModel getSEIModel() {
        return this.seiModel;
    }
    
    @Override
    public void setExecutor(final Executor exec) {
        this.engine.setExecutor(exec);
    }
    
    @Override
    public Engine getEngine() {
        return this.engine;
    }
    
    @Override
    public void schedule(final Packet request, final CompletionCallback callback, final FiberContextSwitchInterceptor interceptor) {
        this.processAsync(request, callback, interceptor, true);
    }
    
    private void processAsync(final Packet request, final CompletionCallback callback, final FiberContextSwitchInterceptor interceptor, final boolean schedule) {
        final Container old = ContainerResolver.getDefault().enterContainer(this.container);
        try {
            request.endpoint = this;
            request.addSatellite(this.wsdlProperties);
            final Fiber fiber = this.engine.createFiber();
            fiber.setDeliverThrowableInPacket(true);
            if (interceptor != null) {
                fiber.addInterceptor(interceptor);
            }
            final Tube tube = this.tubePool.take();
            final Fiber.CompletionCallback cbak = new Fiber.CompletionCallback() {
                @Override
                public void onCompletion(@NotNull Packet response) {
                    final ThrowableContainerPropertySet tc = response.getSatellite(ThrowableContainerPropertySet.class);
                    if (tc == null) {
                        WSEndpointImpl.this.tubePool.recycle(tube);
                    }
                    if (callback != null) {
                        if (tc != null) {
                            response = WSEndpointImpl.this.createServiceResponseForException(tc, response, WSEndpointImpl.this.soapVersion, request.endpoint.getPort(), null, request.endpoint.getBinding());
                        }
                        callback.onCompletion(response);
                    }
                }
                
                @Override
                public void onCompletion(@NotNull final Throwable error) {
                    throw new IllegalStateException();
                }
            };
            fiber.start(tube, request, cbak, this.binding.isFeatureEnabled(SyncStartForAsyncFeature.class) || !schedule);
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    @Override
    public Packet createServiceResponseForException(final ThrowableContainerPropertySet tc, final Packet responsePacket, final SOAPVersion soapVersion, final WSDLPort wsdlPort, final SEIModel seiModel, final WSBinding binding) {
        if (tc.isFaultCreated()) {
            return responsePacket;
        }
        final Message faultMessage = SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, null, tc.getThrowable());
        final Packet result = responsePacket.createServerResponse(faultMessage, wsdlPort, seiModel, binding);
        tc.setFaultMessage(faultMessage);
        tc.setResponsePacket(responsePacket);
        tc.setFaultCreated(true);
        return result;
    }
    
    @Override
    public void process(final Packet request, final CompletionCallback callback, final FiberContextSwitchInterceptor interceptor) {
        this.processAsync(request, callback, interceptor, false);
    }
    
    @NotNull
    @Override
    public PipeHead createPipeHead() {
        return new PipeHead() {
            private final Tube tube = TubeCloner.clone(WSEndpointImpl.this.masterTubeline);
            
            @NotNull
            @Override
            public Packet process(final Packet request, final WebServiceContextDelegate wscd, final TransportBackChannel tbc) {
                final Container old = ContainerResolver.getDefault().enterContainer(WSEndpointImpl.this.container);
                try {
                    request.webServiceContextDelegate = wscd;
                    request.transportBackChannel = tbc;
                    request.endpoint = WSEndpointImpl.this;
                    request.addSatellite(WSEndpointImpl.this.wsdlProperties);
                    final Fiber fiber = WSEndpointImpl.this.engine.createFiber();
                    Packet response;
                    try {
                        response = fiber.runSync(this.tube, request);
                    }
                    catch (final RuntimeException re) {
                        final Message faultMsg = SOAPFaultBuilder.createSOAPFaultMessage(WSEndpointImpl.this.soapVersion, null, re);
                        response = request.createServerResponse(faultMsg, request.endpoint.getPort(), null, request.endpoint.getBinding());
                    }
                    return response;
                }
                finally {
                    ContainerResolver.getDefault().exitContainer(old);
                }
            }
        };
    }
    
    @Override
    public synchronized void dispose() {
        if (this.disposed) {
            return;
        }
        this.disposed = true;
        this.masterTubeline.preDestroy();
        for (final Handler handler : this.binding.getHandlerChain()) {
            for (final Method method : handler.getClass().getMethods()) {
                if (method.getAnnotation(PreDestroy.class) != null) {
                    try {
                        method.invoke(handler, new Object[0]);
                    }
                    catch (final Exception e) {
                        WSEndpointImpl.logger.log(Level.WARNING, HandlerMessages.HANDLER_PREDESTROY_IGNORE(e.getMessage()), e);
                    }
                    break;
                }
            }
        }
        this.closeManagedObjectManager();
        LazyMOMProvider.INSTANCE.unregisterEndpoint(this);
    }
    
    @Override
    public ServiceDefinitionImpl getServiceDefinition() {
        return this.serviceDef;
    }
    
    @Override
    public Set<EndpointComponent> getComponentRegistry() {
        final Set<EndpointComponent> sec = new EndpointComponentSet();
        for (final Component c : this.componentRegistry) {
            sec.add((c instanceof EndpointComponentWrapper) ? ((EndpointComponentWrapper)c).component : new ComponentWrapper(c));
        }
        return sec;
    }
    
    @NotNull
    @Override
    public Set<Component> getComponents() {
        return this.componentRegistry;
    }
    
    @Override
    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final String address, final String wsdlAddress, final Element... referenceParameters) {
        List<Element> refParams = null;
        if (referenceParameters != null) {
            refParams = Arrays.asList(referenceParameters);
        }
        return this.getEndpointReference(clazz, address, wsdlAddress, null, refParams);
    }
    
    @Override
    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final String address, final String wsdlAddress, final List<Element> metadata, final List<Element> referenceParameters) {
        QName portType = null;
        if (this.port != null) {
            portType = this.port.getBinding().getPortTypeName();
        }
        final AddressingVersion av = AddressingVersion.fromSpecClass(clazz);
        return new WSEndpointReference(av, address, this.serviceName, this.portName, portType, metadata, wsdlAddress, referenceParameters, this.endpointReferenceExtensions.values(), null).toSpec(clazz);
    }
    
    @NotNull
    @Override
    public QName getPortName() {
        return this.portName;
    }
    
    @NotNull
    @Override
    public Codec createCodec() {
        return this.masterCodec.copy();
    }
    
    @NotNull
    @Override
    public QName getServiceName() {
        return this.serviceName;
    }
    
    private void initManagedObjectManager() {
        synchronized (this.managedObjectManagerLock) {
            if (this.managedObjectManager == null) {
                switch (this.lazyMOMProviderScope) {
                    case GLASSFISH_NO_JMX: {
                        this.managedObjectManager = new WSEndpointMOMProxy(this);
                        break;
                    }
                    default: {
                        this.managedObjectManager = this.obtainManagedObjectManager();
                        break;
                    }
                }
            }
        }
    }
    
    @NotNull
    @Override
    public ManagedObjectManager getManagedObjectManager() {
        return this.managedObjectManager;
    }
    
    @NotNull
    ManagedObjectManager obtainManagedObjectManager() {
        final MonitorRootService monitorRootService = new MonitorRootService(this);
        final ManagedObjectManager mOM = monitorRootService.createManagedObjectManager(this);
        mOM.resumeJMXRegistration();
        return mOM;
    }
    
    @Override
    public void scopeChanged(final LazyMOMProvider.Scope scope) {
        synchronized (this.managedObjectManagerLock) {
            if (this.managedObjectManagerClosed) {
                return;
            }
            this.lazyMOMProviderScope = scope;
            if (this.managedObjectManager == null) {
                if (scope != LazyMOMProvider.Scope.GLASSFISH_NO_JMX) {
                    this.managedObjectManager = this.obtainManagedObjectManager();
                }
                else {
                    this.managedObjectManager = new WSEndpointMOMProxy(this);
                }
            }
            else if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
                ((WSEndpointMOMProxy)this.managedObjectManager).setManagedObjectManager(this.obtainManagedObjectManager());
            }
        }
    }
    
    @Override
    public void closeManagedObjectManager() {
        synchronized (this.managedObjectManagerLock) {
            if (this.managedObjectManagerClosed) {
                return;
            }
            if (this.managedObjectManager != null) {
                boolean close = true;
                if (this.managedObjectManager instanceof WSEndpointMOMProxy && !((WSEndpointMOMProxy)this.managedObjectManager).isInitialized()) {
                    close = false;
                }
                if (close) {
                    try {
                        final ObjectName name = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
                        if (name != null) {
                            WSEndpointImpl.monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", name);
                        }
                        this.managedObjectManager.close();
                    }
                    catch (final IOException e) {
                        WSEndpointImpl.monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", e);
                    }
                }
            }
            this.managedObjectManagerClosed = true;
        }
    }
    
    @NotNull
    @Override
    public ServerTubeAssemblerContext getAssemblerContext() {
        return this.context;
    }
    
    static {
        logger = Logger.getLogger("com.sun.xml.internal.ws.server.endpoint");
        monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
    }
    
    private class EndpointComponentSet extends HashSet<EndpointComponent>
    {
        @Override
        public Iterator<EndpointComponent> iterator() {
            final Iterator<EndpointComponent> it = super.iterator();
            return new Iterator<EndpointComponent>() {
                private EndpointComponent last = null;
                
                @Override
                public boolean hasNext() {
                    return it.hasNext();
                }
                
                @Override
                public EndpointComponent next() {
                    return this.last = it.next();
                }
                
                @Override
                public void remove() {
                    it.remove();
                    if (this.last != null) {
                        WSEndpointImpl.this.componentRegistry.remove((this.last instanceof ComponentWrapper) ? ((ComponentWrapper)this.last).component : new EndpointComponentWrapper(this.last));
                    }
                    this.last = null;
                }
            };
        }
        
        @Override
        public boolean add(final EndpointComponent e) {
            final boolean result = super.add(e);
            if (result) {
                WSEndpointImpl.this.componentRegistry.add(new EndpointComponentWrapper(e));
            }
            return result;
        }
        
        @Override
        public boolean remove(final Object o) {
            final boolean result = super.remove(o);
            if (result) {
                WSEndpointImpl.this.componentRegistry.remove((o instanceof ComponentWrapper) ? ((ComponentWrapper)o).component : new EndpointComponentWrapper((EndpointComponent)o));
            }
            return result;
        }
    }
    
    private static class ComponentWrapper implements EndpointComponent
    {
        private final Component component;
        
        public ComponentWrapper(final Component component) {
            this.component = component;
        }
        
        @Override
        public <S> S getSPI(final Class<S> spiType) {
            return this.component.getSPI(spiType);
        }
        
        @Override
        public int hashCode() {
            return this.component.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this.component.equals(obj);
        }
    }
    
    private static class EndpointComponentWrapper implements Component
    {
        private final EndpointComponent component;
        
        public EndpointComponentWrapper(final EndpointComponent component) {
            this.component = component;
        }
        
        @Override
        public <S> S getSPI(final Class<S> spiType) {
            return this.component.getSPI(spiType);
        }
        
        @Override
        public int hashCode() {
            return this.component.hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this.component.equals(obj);
        }
    }
}
