package com.sun.xml.internal.ws.api.server;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.net.URL;
import com.sun.xml.internal.ws.server.EndpointAwareTube;
import com.sun.xml.internal.ws.api.config.management.EndpointCreationAttributes;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.api.config.management.ManagedEndpointFactory;
import com.sun.xml.internal.ws.server.EndpointFactory;
import org.xml.sax.EntityResolver;
import java.util.Collection;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.model.SEIModel;
import java.util.Iterator;
import java.util.Collections;
import com.sun.xml.internal.ws.api.Component;
import java.util.Set;
import java.util.List;
import com.sun.xml.internal.ws.api.pipe.Engine;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.internal.ws.api.message.Packet;
import java.util.concurrent.Executor;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.namespace.QName;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.ComponentRegistry;

public abstract class WSEndpoint<T> implements ComponentRegistry
{
    @NotNull
    public abstract Codec createCodec();
    
    @NotNull
    public abstract QName getServiceName();
    
    @NotNull
    public abstract QName getPortName();
    
    @NotNull
    public abstract Class<T> getImplementationClass();
    
    @NotNull
    public abstract WSBinding getBinding();
    
    @NotNull
    public abstract Container getContainer();
    
    @Nullable
    public abstract WSDLPort getPort();
    
    public abstract void setExecutor(@NotNull final Executor p0);
    
    public final void schedule(@NotNull final Packet request, @NotNull final CompletionCallback callback) {
        this.schedule(request, callback, null);
    }
    
    public abstract void schedule(@NotNull final Packet p0, @NotNull final CompletionCallback p1, @Nullable final FiberContextSwitchInterceptor p2);
    
    public void process(@NotNull final Packet request, @NotNull final CompletionCallback callback, @Nullable final FiberContextSwitchInterceptor interceptor) {
        this.schedule(request, callback, interceptor);
    }
    
    public Engine getEngine() {
        throw new UnsupportedOperationException();
    }
    
    @NotNull
    public abstract PipeHead createPipeHead();
    
    public abstract void dispose();
    
    @Nullable
    public abstract ServiceDefinition getServiceDefinition();
    
    public List<BoundEndpoint> getBoundEndpoints() {
        final Module m = this.getContainer().getSPI(Module.class);
        return (m != null) ? m.getBoundEndpoints() : null;
    }
    
    @NotNull
    @Deprecated
    public abstract Set<EndpointComponent> getComponentRegistry();
    
    @NotNull
    @Override
    public Set<Component> getComponents() {
        return Collections.emptySet();
    }
    
    @Nullable
    @Override
    public <S> S getSPI(@NotNull final Class<S> spiType) {
        final Set<Component> componentRegistry = this.getComponents();
        if (componentRegistry != null) {
            for (final Component c : componentRegistry) {
                final S s = c.getSPI(spiType);
                if (s != null) {
                    return s;
                }
            }
        }
        return this.getContainer().getSPI(spiType);
    }
    
    @Nullable
    public abstract SEIModel getSEIModel();
    
    @Deprecated
    public abstract PolicyMap getPolicyMap();
    
    @NotNull
    public abstract ManagedObjectManager getManagedObjectManager();
    
    public abstract void closeManagedObjectManager();
    
    @NotNull
    public abstract ServerTubeAssemblerContext getAssemblerContext();
    
    public static <T> WSEndpoint<T> create(@NotNull final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, @Nullable final EntityResolver resolver, final boolean isTransportSynchronous) {
        return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, true);
    }
    
    public static <T> WSEndpoint<T> create(@NotNull final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, @Nullable final EntityResolver resolver, final boolean isTransportSynchronous, final boolean isStandard) {
        final WSEndpoint<T> endpoint = EndpointFactory.createEndpoint(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, isTransportSynchronous, isStandard);
        final Iterator<ManagedEndpointFactory> managementFactories = ServiceFinder.find(ManagedEndpointFactory.class).iterator();
        if (managementFactories.hasNext()) {
            final ManagedEndpointFactory managementFactory = managementFactories.next();
            final EndpointCreationAttributes attributes = new EndpointCreationAttributes(processHandlerAnnotation, invoker, resolver, isTransportSynchronous);
            final WSEndpoint<T> managedEndpoint = managementFactory.createEndpoint(endpoint, attributes);
            if (endpoint.getAssemblerContext().getTerminalTube() instanceof EndpointAwareTube) {
                ((EndpointAwareTube)endpoint.getAssemblerContext().getTerminalTube()).setEndpoint(managedEndpoint);
            }
            return managedEndpoint;
        }
        return endpoint;
    }
    
    @Deprecated
    public static <T> WSEndpoint<T> create(@NotNull final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, @Nullable final EntityResolver resolver) {
        return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, resolver, false);
    }
    
    public static <T> WSEndpoint<T> create(@NotNull final Class<T> implType, final boolean processHandlerAnnotation, @Nullable final Invoker invoker, @Nullable final QName serviceName, @Nullable final QName portName, @Nullable final Container container, @Nullable final WSBinding binding, @Nullable final SDDocumentSource primaryWsdl, @Nullable final Collection<? extends SDDocumentSource> metadata, @Nullable final URL catalogUrl) {
        return create(implType, processHandlerAnnotation, invoker, serviceName, portName, container, binding, primaryWsdl, metadata, XmlUtil.createEntityResolver(catalogUrl), false);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class endpointClass) {
        return getDefaultServiceName(endpointClass, true, null);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class endpointClass, final MetadataReader metadataReader) {
        return getDefaultServiceName(endpointClass, true, metadataReader);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class endpointClass, final boolean isStandard) {
        return getDefaultServiceName(endpointClass, isStandard, null);
    }
    
    @NotNull
    public static QName getDefaultServiceName(final Class endpointClass, final boolean isStandard, final MetadataReader metadataReader) {
        return EndpointFactory.getDefaultServiceName(endpointClass, isStandard, metadataReader);
    }
    
    @NotNull
    public static QName getDefaultPortName(@NotNull final QName serviceName, final Class endpointClass) {
        return getDefaultPortName(serviceName, endpointClass, null);
    }
    
    @NotNull
    public static QName getDefaultPortName(@NotNull final QName serviceName, final Class endpointClass, final MetadataReader metadataReader) {
        return getDefaultPortName(serviceName, endpointClass, true, metadataReader);
    }
    
    @NotNull
    public static QName getDefaultPortName(@NotNull final QName serviceName, final Class endpointClass, final boolean isStandard) {
        return getDefaultPortName(serviceName, endpointClass, isStandard, null);
    }
    
    @NotNull
    public static QName getDefaultPortName(@NotNull final QName serviceName, final Class endpointClass, final boolean isStandard, final MetadataReader metadataReader) {
        return EndpointFactory.getDefaultPortName(serviceName, endpointClass, isStandard, metadataReader);
    }
    
    public abstract <T extends EndpointReference> T getEndpointReference(final Class<T> p0, final String p1, final String p2, final Element... p3);
    
    public abstract <T extends EndpointReference> T getEndpointReference(final Class<T> p0, final String p1, final String p2, final List<Element> p3, final List<Element> p4);
    
    public boolean equalsProxiedInstance(final WSEndpoint endpoint) {
        return endpoint != null && this.equals(endpoint);
    }
    
    @Nullable
    public abstract OperationDispatcher getOperationDispatcher();
    
    public abstract Packet createServiceResponseForException(final ThrowableContainerPropertySet p0, final Packet p1, final SOAPVersion p2, final WSDLPort p3, final SEIModel p4, final WSBinding p5);
    
    public interface PipeHead
    {
        @NotNull
        Packet process(@NotNull final Packet p0, @Nullable final WebServiceContextDelegate p1, @Nullable final TransportBackChannel p2);
    }
    
    public interface CompletionCallback
    {
        void onCompletion(@NotNull final Packet p0);
    }
}
