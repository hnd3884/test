package com.sun.xml.internal.ws.client;

import javax.xml.ws.Binding;
import java.util.Collections;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import java.util.Collection;
import org.w3c.dom.Element;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import com.sun.xml.internal.ws.addressing.WSEPRExtension;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.util.ArrayList;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.RuntimeVersion;
import java.util.Map;
import javax.management.ObjectName;
import java.io.IOException;
import java.util.logging.Level;
import com.sun.xml.internal.ws.api.pipe.SyncStartForAsyncFeature;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptorFactory;
import com.sun.xml.internal.ws.api.Cancelable;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.oracle.webservices.internal.api.message.PropertySet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.concurrent.Executor;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.RespectBindingFeature;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.pipe.TubelineAssembler;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.api.pipe.TubelineAssemblerFactory;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.model.wsdl.WSDLPortProperties;
import com.sun.xml.internal.ws.model.wsdl.WSDLDirectProperties;
import com.sun.xml.internal.ws.api.ComponentsFeature;
import com.sun.xml.internal.ws.api.ComponentFeature;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import java.util.concurrent.CopyOnWriteArraySet;
import com.sun.xml.internal.ws.api.EndpointAddress;
import java.util.logging.Logger;
import com.sun.xml.internal.ws.api.Component;
import java.util.Set;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.model.wsdl.WSDLProperties;
import com.sun.xml.internal.ws.api.message.Header;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.util.Pool;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.developer.WSBindingProvider;

public abstract class Stub implements WSBindingProvider, ResponseContextReceiver, ComponentRegistry
{
    public static final String PREVENT_SYNC_START_FOR_ASYNC_INVOKE = "com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke";
    private Pool<Tube> tubes;
    private final Engine engine;
    protected final WSServiceDelegate owner;
    @Nullable
    protected WSEndpointReference endpointReference;
    protected final BindingImpl binding;
    protected final WSPortInfo portInfo;
    protected AddressingVersion addrVersion;
    public RequestContext requestContext;
    private final RequestContext cleanRequestContext;
    private ResponseContext responseContext;
    @Nullable
    protected final WSDLPort wsdlPort;
    protected QName portname;
    @Nullable
    private volatile Header[] userOutboundHeaders;
    @NotNull
    private final WSDLProperties wsdlProperties;
    protected OperationDispatcher operationDispatcher;
    @NotNull
    private final ManagedObjectManager managedObjectManager;
    private boolean managedObjectManagerClosed;
    private final Set<Component> components;
    private static final Logger monitoringLogger;
    
    @Deprecated
    protected Stub(final WSServiceDelegate owner, final Tube master, final BindingImpl binding, final WSDLPort wsdlPort, final EndpointAddress defaultEndPointAddress, @Nullable final WSEndpointReference epr) {
        this(owner, master, null, null, binding, wsdlPort, defaultEndPointAddress, epr);
    }
    
    @Deprecated
    protected Stub(final QName portname, final WSServiceDelegate owner, final Tube master, final BindingImpl binding, final WSDLPort wsdlPort, final EndpointAddress defaultEndPointAddress, @Nullable final WSEndpointReference epr) {
        this(owner, master, null, portname, binding, wsdlPort, defaultEndPointAddress, epr);
    }
    
    protected Stub(final WSPortInfo portInfo, final BindingImpl binding, final Tube master, final EndpointAddress defaultEndPointAddress, @Nullable final WSEndpointReference epr) {
        this((WSServiceDelegate)portInfo.getOwner(), master, portInfo, null, binding, portInfo.getPort(), defaultEndPointAddress, epr);
    }
    
    protected Stub(final WSPortInfo portInfo, final BindingImpl binding, final EndpointAddress defaultEndPointAddress, @Nullable final WSEndpointReference epr) {
        this(portInfo, binding, null, defaultEndPointAddress, epr);
    }
    
    private Stub(final WSServiceDelegate owner, @Nullable final Tube master, @Nullable final WSPortInfo portInfo, final QName portname, final BindingImpl binding, @Nullable final WSDLPort wsdlPort, final EndpointAddress defaultEndPointAddress, @Nullable final WSEndpointReference epr) {
        this.requestContext = new RequestContext();
        this.operationDispatcher = null;
        this.managedObjectManagerClosed = false;
        this.components = new CopyOnWriteArraySet<Component>();
        final Container old = ContainerResolver.getDefault().enterContainer(owner.getContainer());
        try {
            this.owner = owner;
            this.portInfo = portInfo;
            this.wsdlPort = ((wsdlPort != null) ? wsdlPort : ((portInfo != null) ? portInfo.getPort() : null));
            this.portname = portname;
            if (portname == null) {
                if (portInfo != null) {
                    this.portname = portInfo.getPortName();
                }
                else if (wsdlPort != null) {
                    this.portname = wsdlPort.getName();
                }
            }
            this.binding = binding;
            final ComponentFeature cf = binding.getFeature(ComponentFeature.class);
            if (cf != null && ComponentFeature.Target.STUB.equals(cf.getTarget())) {
                this.components.add(cf.getComponent());
            }
            final ComponentsFeature csf = binding.getFeature(ComponentsFeature.class);
            if (csf != null) {
                for (final ComponentFeature cfi : csf.getComponentFeatures()) {
                    if (ComponentFeature.Target.STUB.equals(cfi.getTarget())) {
                        this.components.add(cfi.getComponent());
                    }
                }
            }
            if (epr != null) {
                this.requestContext.setEndPointAddressString(epr.getAddress());
            }
            else {
                this.requestContext.setEndpointAddress(defaultEndPointAddress);
            }
            this.engine = new Engine(this.getStringId(), owner.getContainer(), owner.getExecutor());
            this.endpointReference = epr;
            this.wsdlProperties = ((wsdlPort == null) ? new WSDLDirectProperties(owner.getServiceName(), portname) : new WSDLPortProperties(wsdlPort));
            this.cleanRequestContext = this.requestContext.copy();
            this.managedObjectManager = new MonitorRootClient(this).createManagedObjectManager(this);
            if (master != null) {
                this.tubes = new Pool.TubePool(master);
            }
            else {
                this.tubes = new Pool.TubePool(this.createPipeline(portInfo, binding));
            }
            this.addrVersion = binding.getAddressingVersion();
            this.managedObjectManager.resumeJMXRegistration();
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }
    
    private Tube createPipeline(final WSPortInfo portInfo, final WSBinding binding) {
        checkAllWSDLExtensionsUnderstood(portInfo, binding);
        SEIModel seiModel = null;
        Class sei = null;
        if (portInfo instanceof SEIPortInfo) {
            final SEIPortInfo sp = (SEIPortInfo)portInfo;
            seiModel = sp.model;
            sei = sp.sei;
        }
        final BindingID bindingId = portInfo.getBindingId();
        final TubelineAssembler assembler = TubelineAssemblerFactory.create(Thread.currentThread().getContextClassLoader(), bindingId, this.owner.getContainer());
        if (assembler == null) {
            throw new WebServiceException("Unable to process bindingID=" + bindingId);
        }
        return assembler.createClient(new ClientTubeAssemblerContext(portInfo.getEndpointAddress(), portInfo.getPort(), this, binding, this.owner.getContainer(), ((BindingImpl)binding).createCodec(), seiModel, sei));
    }
    
    public WSDLPort getWSDLPort() {
        return this.wsdlPort;
    }
    
    public WSService getService() {
        return this.owner;
    }
    
    public Pool<Tube> getTubes() {
        return this.tubes;
    }
    
    private static void checkAllWSDLExtensionsUnderstood(final WSPortInfo port, final WSBinding binding) {
        if (port.getPort() != null && binding.isFeatureEnabled(RespectBindingFeature.class)) {
            port.getPort().areRequiredExtensionsUnderstood();
        }
    }
    
    @Override
    public WSPortInfo getPortInfo() {
        return this.portInfo;
    }
    
    @Nullable
    public OperationDispatcher getOperationDispatcher() {
        if (this.operationDispatcher == null && this.wsdlPort != null) {
            this.operationDispatcher = new OperationDispatcher(this.wsdlPort, this.binding, null);
        }
        return this.operationDispatcher;
    }
    
    @NotNull
    protected abstract QName getPortName();
    
    @NotNull
    protected final QName getServiceName() {
        return this.owner.getServiceName();
    }
    
    public final Executor getExecutor() {
        return this.owner.getExecutor();
    }
    
    protected final Packet process(final Packet packet, final RequestContext requestContext, final ResponseContextReceiver receiver) {
        packet.isSynchronousMEP = true;
        ((Stub)(packet.component = this)).configureRequestPacket(packet, requestContext);
        final Pool<Tube> pool = this.tubes;
        if (pool == null) {
            throw new WebServiceException("close method has already been invoked");
        }
        final Fiber fiber = this.engine.createFiber();
        this.configureFiber(fiber);
        final Tube tube = pool.take();
        try {
            return fiber.runSync(tube, packet);
        }
        finally {
            final Packet reply = (fiber.getPacket() == null) ? packet : fiber.getPacket();
            receiver.setResponseContext(new ResponseContext(reply));
            pool.recycle(tube);
        }
    }
    
    private void configureRequestPacket(final Packet packet, final RequestContext requestContext) {
        packet.proxy = this;
        packet.handlerConfig = this.binding.getHandlerConfig();
        final Header[] hl = this.userOutboundHeaders;
        if (hl != null) {
            final MessageHeaders mh = packet.getMessage().getHeaders();
            for (final Header h : hl) {
                mh.add(h);
            }
        }
        requestContext.fill(packet, this.binding.getAddressingVersion() != null);
        packet.addSatellite(this.wsdlProperties);
        if (this.addrVersion != null) {
            final MessageHeaders headerList = packet.getMessage().getHeaders();
            AddressingUtils.fillRequestAddressingHeaders(headerList, this.wsdlPort, this.binding, packet);
            if (this.endpointReference != null) {
                this.endpointReference.addReferenceParametersToList(packet.getMessage().getHeaders());
            }
        }
    }
    
    protected final void processAsync(final AsyncResponseImpl<?> receiver, final Packet request, final RequestContext requestContext, final Fiber.CompletionCallback completionCallback) {
        ((Stub)(request.component = this)).configureRequestPacket(request, requestContext);
        final Pool<Tube> pool = this.tubes;
        if (pool == null) {
            throw new WebServiceException("close method has already been invoked");
        }
        final Fiber fiber = this.engine.createFiber();
        this.configureFiber(fiber);
        receiver.setCancelable(fiber);
        if (receiver.isCancelled()) {
            return;
        }
        final FiberContextSwitchInterceptorFactory fcsif = this.owner.getSPI(FiberContextSwitchInterceptorFactory.class);
        if (fcsif != null) {
            fiber.addInterceptor(fcsif.create());
        }
        final Tube tube = pool.take();
        final Fiber.CompletionCallback fiberCallback = new Fiber.CompletionCallback() {
            @Override
            public void onCompletion(@NotNull final Packet response) {
                pool.recycle(tube);
                completionCallback.onCompletion(response);
            }
            
            @Override
            public void onCompletion(@NotNull final Throwable error) {
                completionCallback.onCompletion(error);
            }
        };
        fiber.start(tube, request, fiberCallback, this.getBinding().isFeatureEnabled(SyncStartForAsyncFeature.class) && !requestContext.containsKey("com.sun.xml.internal.ws.client.StubRequestSyncStartForAsyncInvoke"));
    }
    
    protected void configureFiber(final Fiber fiber) {
    }
    
    @Override
    public void close() {
        final Pool.TubePool tp = (Pool.TubePool)this.tubes;
        if (tp != null) {
            final Tube p = tp.takeMaster();
            p.preDestroy();
            this.tubes = null;
        }
        if (!this.managedObjectManagerClosed) {
            try {
                final ObjectName name = this.managedObjectManager.getObjectName(this.managedObjectManager.getRoot());
                if (name != null) {
                    Stub.monitoringLogger.log(Level.INFO, "Closing Metro monitoring root: {0}", name);
                }
                this.managedObjectManager.close();
            }
            catch (final IOException e) {
                Stub.monitoringLogger.log(Level.WARNING, "Ignoring error when closing Managed Object Manager", e);
            }
            this.managedObjectManagerClosed = true;
        }
    }
    
    @Override
    public final WSBinding getBinding() {
        return this.binding;
    }
    
    @Override
    public final Map<String, Object> getRequestContext() {
        return this.requestContext.asMap();
    }
    
    public void resetRequestContext() {
        this.requestContext = this.cleanRequestContext.copy();
    }
    
    @Override
    public final ResponseContext getResponseContext() {
        return this.responseContext;
    }
    
    @Override
    public void setResponseContext(final ResponseContext rc) {
        this.responseContext = rc;
    }
    
    private String getStringId() {
        return RuntimeVersion.VERSION + ": Stub for " + this.getRequestContext().get("javax.xml.ws.service.endpoint.address");
    }
    
    @Override
    public String toString() {
        return this.getStringId();
    }
    
    @Override
    public final WSEndpointReference getWSEndpointReference() {
        if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
            throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference(Class<T> class)", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
        }
        if (this.endpointReference != null) {
            return this.endpointReference;
        }
        final String eprAddress = this.requestContext.getEndpointAddress().toString();
        QName portTypeName = null;
        String wsdlAddress = null;
        final List<WSEndpointReference.EPRExtension> wsdlEPRExtensions = new ArrayList<WSEndpointReference.EPRExtension>();
        if (this.wsdlPort != null) {
            portTypeName = this.wsdlPort.getBinding().getPortTypeName();
            wsdlAddress = eprAddress + "?wsdl";
            try {
                final WSEndpointReference wsdlEpr = this.wsdlPort.getEPR();
                if (wsdlEpr != null) {
                    for (final WSEndpointReference.EPRExtension extnEl : wsdlEpr.getEPRExtensions()) {
                        wsdlEPRExtensions.add(new WSEPRExtension(XMLStreamBuffer.createNewBufferFromXMLStreamReader(extnEl.readAsXMLStreamReader()), extnEl.getQName()));
                    }
                }
            }
            catch (final XMLStreamException ex) {
                throw new WebServiceException(ex);
            }
        }
        final AddressingVersion av = AddressingVersion.W3C;
        return this.endpointReference = new WSEndpointReference(av, eprAddress, this.getServiceName(), this.getPortName(), portTypeName, null, wsdlAddress, null, wsdlEPRExtensions, null);
    }
    
    @Override
    public final W3CEndpointReference getEndpointReference() {
        if (this.binding.getBindingID().equals("http://www.w3.org/2004/08/wsdl/http")) {
            throw new UnsupportedOperationException(ClientMessages.UNSUPPORTED_OPERATION("BindingProvider.getEndpointReference()", "XML/HTTP Binding", "SOAP11 or SOAP12 Binding"));
        }
        return this.getEndpointReference(W3CEndpointReference.class);
    }
    
    @Override
    public final <T extends EndpointReference> T getEndpointReference(final Class<T> clazz) {
        return this.getWSEndpointReference().toSpec(clazz);
    }
    
    @NotNull
    @Override
    public ManagedObjectManager getManagedObjectManager() {
        return this.managedObjectManager;
    }
    
    @Override
    public final void setOutboundHeaders(final List<Header> headers) {
        if (headers == null) {
            this.userOutboundHeaders = null;
        }
        else {
            for (final Header h : headers) {
                if (h == null) {
                    throw new IllegalArgumentException();
                }
            }
            this.userOutboundHeaders = headers.toArray(new Header[headers.size()]);
        }
    }
    
    @Override
    public final void setOutboundHeaders(final Header... headers) {
        if (headers == null) {
            this.userOutboundHeaders = null;
        }
        else {
            for (final Header h : headers) {
                if (h == null) {
                    throw new IllegalArgumentException();
                }
            }
            final Header[] hl = new Header[headers.length];
            System.arraycopy(headers, 0, hl, 0, headers.length);
            this.userOutboundHeaders = hl;
        }
    }
    
    @Override
    public final List<Header> getInboundHeaders() {
        return Collections.unmodifiableList((List<? extends Header>)((MessageHeaders)this.responseContext.get("com.sun.xml.internal.ws.api.message.HeaderList")).asList());
    }
    
    @Override
    public final void setAddress(final String address) {
        this.requestContext.put("javax.xml.ws.service.endpoint.address", address);
    }
    
    @Override
    public <S> S getSPI(final Class<S> spiType) {
        for (final Component c : this.components) {
            final S s = c.getSPI(spiType);
            if (s != null) {
                return s;
            }
        }
        return this.owner.getSPI(spiType);
    }
    
    @Override
    public Set<Component> getComponents() {
        return this.components;
    }
    
    static {
        monitoringLogger = Logger.getLogger("com.sun.xml.internal.ws.monitoring");
    }
}
