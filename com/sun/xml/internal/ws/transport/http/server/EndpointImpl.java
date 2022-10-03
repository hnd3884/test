package com.sun.xml.internal.ws.transport.http.server;

import java.lang.reflect.Method;
import com.sun.xml.internal.ws.api.message.Packet;
import java.lang.reflect.InvocationTargetException;
import javax.xml.ws.WebServiceContext;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import com.sun.xml.internal.ws.transport.http.HttpAdapterList;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.transport.http.HttpAdapter;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import javax.xml.ws.EndpointReference;
import org.w3c.dom.Element;
import java.util.Iterator;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.transform.TransformerException;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferResult;
import java.util.ArrayList;
import com.sun.xml.internal.ws.api.databinding.MetadataReader;
import com.sun.xml.internal.ws.api.server.SDDocumentSource;
import java.util.Collection;
import org.xml.sax.EntityResolver;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.server.EndpointFactory;
import java.security.Permission;
import java.util.HashMap;
import com.sun.net.httpserver.HttpContext;
import javax.xml.ws.Binding;
import java.net.MalformedURLException;
import java.net.URL;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.BindingImpl;
import java.util.Collections;
import com.sun.xml.internal.ws.api.server.InstanceResolver;
import javax.xml.ws.WebServiceFeature;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.istack.internal.NotNull;
import javax.xml.ws.EndpointContext;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.xml.transform.Source;
import java.util.List;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import javax.xml.ws.WebServicePermission;
import javax.xml.ws.Endpoint;

public class EndpointImpl extends Endpoint
{
    private static final WebServicePermission ENDPOINT_PUBLISH_PERMISSION;
    private Object actualEndpoint;
    private final WSBinding binding;
    @Nullable
    private final Object implementor;
    private List<Source> metadata;
    private Executor executor;
    private Map<String, Object> properties;
    private boolean stopped;
    @Nullable
    private EndpointContext endpointContext;
    @NotNull
    private final Class<?> implClass;
    private final Invoker invoker;
    private Container container;
    
    public EndpointImpl(@NotNull final BindingID bindingId, @NotNull final Object impl, final WebServiceFeature... features) {
        this(bindingId, impl, impl.getClass(), InstanceResolver.createSingleton(impl).createInvoker(), features);
    }
    
    public EndpointImpl(@NotNull final BindingID bindingId, @NotNull final Class implClass, final javax.xml.ws.spi.Invoker invoker, final WebServiceFeature... features) {
        this(bindingId, null, implClass, new InvokerImpl(invoker), features);
    }
    
    private EndpointImpl(@NotNull final BindingID bindingId, final Object impl, @NotNull final Class implClass, final Invoker invoker, final WebServiceFeature... features) {
        this.properties = Collections.emptyMap();
        this.binding = BindingImpl.create(bindingId, features);
        this.implClass = implClass;
        this.invoker = invoker;
        this.implementor = impl;
    }
    
    @Deprecated
    public EndpointImpl(final WSEndpoint wse, final Object serverContext) {
        this(wse, serverContext, null);
    }
    
    @Deprecated
    public EndpointImpl(final WSEndpoint wse, final Object serverContext, final EndpointContext ctxt) {
        this.properties = Collections.emptyMap();
        this.endpointContext = ctxt;
        this.actualEndpoint = new HttpEndpoint(null, this.getAdapter(wse, ""));
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
        this.binding = wse.getBinding();
        this.implementor = null;
        this.implClass = null;
        this.invoker = null;
    }
    
    @Deprecated
    public EndpointImpl(final WSEndpoint wse, final String address) {
        this(wse, address, null);
    }
    
    @Deprecated
    public EndpointImpl(final WSEndpoint wse, final String address, final EndpointContext ctxt) {
        this.properties = Collections.emptyMap();
        URL url;
        try {
            url = new URL(address);
        }
        catch (final MalformedURLException ex) {
            throw new IllegalArgumentException("Cannot create URL for this address " + address);
        }
        if (!url.getProtocol().equals("http")) {
            throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
        }
        if (!url.getPath().startsWith("/")) {
            throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
        }
        this.endpointContext = ctxt;
        this.actualEndpoint = new HttpEndpoint(null, this.getAdapter(wse, url.getPath()));
        ((HttpEndpoint)this.actualEndpoint).publish(address);
        this.binding = wse.getBinding();
        this.implementor = null;
        this.implClass = null;
        this.invoker = null;
    }
    
    @Override
    public Binding getBinding() {
        return this.binding;
    }
    
    @Override
    public Object getImplementor() {
        return this.implementor;
    }
    
    @Override
    public void publish(final String address) {
        this.canPublish();
        URL url;
        try {
            url = new URL(address);
        }
        catch (final MalformedURLException ex) {
            throw new IllegalArgumentException("Cannot create URL for this address " + address);
        }
        if (!url.getProtocol().equals("http")) {
            throw new IllegalArgumentException(url.getProtocol() + " protocol based address is not supported");
        }
        if (!url.getPath().startsWith("/")) {
            throw new IllegalArgumentException("Incorrect WebService address=" + address + ". The address's path should start with /");
        }
        this.createEndpoint(url.getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(address);
    }
    
    @Override
    public void publish(final Object serverContext) {
        this.canPublish();
        if (!HttpContext.class.isAssignableFrom(serverContext.getClass())) {
            throw new IllegalArgumentException(serverContext.getClass() + " is not a supported context.");
        }
        this.createEndpoint(((HttpContext)serverContext).getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
    }
    
    @Override
    public void publish(final javax.xml.ws.spi.http.HttpContext serverContext) {
        this.canPublish();
        this.createEndpoint(serverContext.getPath());
        ((HttpEndpoint)this.actualEndpoint).publish(serverContext);
    }
    
    @Override
    public void stop() {
        if (this.isPublished()) {
            ((HttpEndpoint)this.actualEndpoint).stop();
            this.actualEndpoint = null;
            this.stopped = true;
        }
    }
    
    @Override
    public boolean isPublished() {
        return this.actualEndpoint != null;
    }
    
    @Override
    public List<Source> getMetadata() {
        return this.metadata;
    }
    
    @Override
    public void setMetadata(final List<Source> metadata) {
        if (this.isPublished()) {
            throw new IllegalStateException("Cannot set Metadata. Endpoint is already published");
        }
        this.metadata = metadata;
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
    public Map<String, Object> getProperties() {
        return new HashMap<String, Object>(this.properties);
    }
    
    @Override
    public void setProperties(final Map<String, Object> map) {
        this.properties = new HashMap<String, Object>(map);
    }
    
    private void createEndpoint(final String urlPattern) {
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(EndpointImpl.ENDPOINT_PUBLISH_PERMISSION);
        }
        try {
            Class.forName("com.sun.net.httpserver.HttpServer");
        }
        catch (final Exception e) {
            throw new UnsupportedOperationException("Couldn't load light weight http server", e);
        }
        this.container = this.getContainer();
        final MetadataReader metadataReader = EndpointFactory.getExternalMetadatReader(this.implClass, this.binding);
        final WSEndpoint wse = WSEndpoint.create(this.implClass, true, this.invoker, this.getProperty(QName.class, "javax.xml.ws.wsdl.service"), this.getProperty(QName.class, "javax.xml.ws.wsdl.port"), this.container, this.binding, this.getPrimaryWsdl(metadataReader), this.buildDocList(), null, false);
        this.actualEndpoint = new HttpEndpoint(this.executor, this.getAdapter(wse, urlPattern));
    }
    
    private <T> T getProperty(final Class<T> type, final String key) {
        final Object o = this.properties.get(key);
        if (o == null) {
            return null;
        }
        if (type.isInstance(o)) {
            return type.cast(o);
        }
        throw new IllegalArgumentException("Property " + key + " has to be of type " + type);
    }
    
    private List<SDDocumentSource> buildDocList() {
        final List<SDDocumentSource> r = new ArrayList<SDDocumentSource>();
        if (this.metadata != null) {
            for (final Source source : this.metadata) {
                try {
                    final XMLStreamBufferResult xsbr = XmlUtil.identityTransform(source, new XMLStreamBufferResult());
                    final String systemId = source.getSystemId();
                    r.add(SDDocumentSource.create(new URL(systemId), xsbr.getXMLStreamBuffer()));
                }
                catch (final TransformerException te) {
                    throw new ServerRtException("server.rt.err", new Object[] { te });
                }
                catch (final IOException te2) {
                    throw new ServerRtException("server.rt.err", new Object[] { te2 });
                }
                catch (final SAXException e) {
                    throw new ServerRtException("server.rt.err", new Object[] { e });
                }
                catch (final ParserConfigurationException e2) {
                    throw new ServerRtException("server.rt.err", new Object[] { e2 });
                }
            }
        }
        return r;
    }
    
    @Nullable
    private SDDocumentSource getPrimaryWsdl(final MetadataReader metadataReader) {
        EndpointFactory.verifyImplementorClass(this.implClass, metadataReader);
        final String wsdlLocation = EndpointFactory.getWsdlLocation(this.implClass, metadataReader);
        if (wsdlLocation == null) {
            return null;
        }
        final ClassLoader cl = this.implClass.getClassLoader();
        final URL url = cl.getResource(wsdlLocation);
        if (url != null) {
            return SDDocumentSource.create(url);
        }
        throw new ServerRtException("cannot.load.wsdl", new Object[] { wsdlLocation });
    }
    
    private void canPublish() {
        if (this.isPublished()) {
            throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already published.");
        }
        if (this.stopped) {
            throw new IllegalStateException("Cannot publish this endpoint. Endpoint has been already stopped.");
        }
    }
    
    @Override
    public EndpointReference getEndpointReference(final Element... referenceParameters) {
        return this.getEndpointReference(W3CEndpointReference.class, referenceParameters);
    }
    
    @Override
    public <T extends EndpointReference> T getEndpointReference(final Class<T> clazz, final Element... referenceParameters) {
        if (!this.isPublished()) {
            throw new WebServiceException("Endpoint is not published yet");
        }
        return ((HttpEndpoint)this.actualEndpoint).getEndpointReference(clazz, referenceParameters);
    }
    
    @Override
    public void setEndpointContext(final EndpointContext ctxt) {
        this.endpointContext = ctxt;
    }
    
    private HttpAdapter getAdapter(final WSEndpoint endpoint, final String urlPattern) {
        HttpAdapterList adapterList = null;
        if (this.endpointContext != null) {
            if (this.endpointContext instanceof Component) {
                adapterList = ((Component)this.endpointContext).getSPI(HttpAdapterList.class);
            }
            if (adapterList == null) {
                for (final Endpoint e : this.endpointContext.getEndpoints()) {
                    if (e.isPublished() && e != this) {
                        adapterList = ((HttpEndpoint)((EndpointImpl)e).actualEndpoint).getAdapterOwner();
                        assert adapterList != null;
                        break;
                    }
                }
            }
        }
        if (adapterList == null) {
            adapterList = new ServerAdapterList();
        }
        return adapterList.createAdapter("", urlPattern, endpoint);
    }
    
    private Container getContainer() {
        if (this.endpointContext != null) {
            if (this.endpointContext instanceof Component) {
                final Container c = ((Component)this.endpointContext).getSPI(Container.class);
                if (c != null) {
                    return c;
                }
            }
            for (final Endpoint e : this.endpointContext.getEndpoints()) {
                if (e.isPublished() && e != this) {
                    return ((EndpointImpl)e).container;
                }
            }
        }
        return new ServerContainer();
    }
    
    static {
        ENDPOINT_PUBLISH_PERMISSION = new WebServicePermission("publishEndpoint");
    }
    
    private static class InvokerImpl extends Invoker
    {
        private javax.xml.ws.spi.Invoker spiInvoker;
        
        InvokerImpl(final javax.xml.ws.spi.Invoker spiInvoker) {
            this.spiInvoker = spiInvoker;
        }
        
        @Override
        public void start(@NotNull final WSWebServiceContext wsc, @NotNull final WSEndpoint endpoint) {
            try {
                this.spiInvoker.inject(wsc);
            }
            catch (final IllegalAccessException e) {
                throw new WebServiceException(e);
            }
            catch (final InvocationTargetException e2) {
                throw new WebServiceException(e2);
            }
        }
        
        @Override
        public Object invoke(@NotNull final Packet p, @NotNull final Method m, @NotNull final Object... args) throws InvocationTargetException, IllegalAccessException {
            return this.spiInvoker.invoke(m, args);
        }
    }
}
