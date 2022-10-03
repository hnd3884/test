package org.glassfish.jersey.servlet;

import org.glassfish.jersey.servlet.spi.FilterUrlMappingsProvider;
import org.glassfish.jersey.internal.ServiceFinderBinder;
import javax.ws.rs.RuntimeType;
import javax.servlet.FilterConfig;
import org.glassfish.jersey.servlet.internal.PersistenceUnitBinder;
import javax.servlet.ServletConfig;
import javax.inject.Singleton;
import java.util.function.Supplier;
import org.glassfish.jersey.internal.inject.SupplierInstanceBinding;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.internal.inject.SupplierClassBinding;
import javax.inject.Inject;
import javax.inject.Provider;
import org.glassfish.jersey.internal.inject.ReferencingFactory;
import javax.ws.rs.core.GenericType;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.server.spi.RequestScopedInitializer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.uri.UriComponent;
import java.util.ArrayList;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import javax.ws.rs.core.Form;
import org.glassfish.jersey.message.internal.MediaTypes;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.server.ResourceFinder;
import java.util.Map;
import org.glassfish.jersey.servlet.internal.Utils;
import java.security.Principal;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.servlet.internal.spi.RequestContextProvider;
import java.io.InputStream;
import org.glassfish.jersey.message.internal.HeaderValueException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.servlet.internal.LocalizationMessages;
import java.util.logging.Level;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import java.io.IOException;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.servlet.internal.ResponseWriter;
import org.glassfish.jersey.internal.util.collection.Value;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import javax.servlet.ServletException;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.servlet.internal.spi.ServletContainerProvider;
import org.glassfish.jersey.server.BackgroundSchedulerLiteral;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.internal.inject.Binder;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.servlet.internal.spi.ExtendedServletContainerProvider;
import org.glassfish.jersey.servlet.internal.ServletContainerProviderFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.util.Iterator;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.servlet.spi.AsyncContextDelegateProvider;
import java.util.concurrent.ScheduledExecutorService;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.servlet.internal.spi.RequestScopedInitializerProvider;
import org.glassfish.jersey.servlet.spi.AsyncContextDelegate;
import java.lang.reflect.Type;
import java.util.logging.Logger;

public class WebComponent
{
    private static final Logger LOGGER;
    private static final Type REQUEST_TYPE;
    private static final Type RESPONSE_TYPE;
    private static final AsyncContextDelegate DEFAULT_ASYNC_DELEGATE;
    private final RequestScopedInitializerProvider requestScopedInitializer;
    private final boolean requestResponseBindingExternalized;
    private static final RequestScopedInitializerProvider DEFAULT_REQUEST_SCOPE_INITIALIZER_PROVIDER;
    final ApplicationHandler appHandler;
    final ScheduledExecutorService backgroundTaskScheduler;
    final WebConfig webConfig;
    final boolean forwardOn404;
    final boolean configSetStatusOverSendError;
    private final AsyncContextDelegateProvider asyncExtensionDelegate;
    private final boolean queryParamsAsFormParams;
    
    private AsyncContextDelegateProvider getAsyncExtensionDelegate() {
        final Iterator<AsyncContextDelegateProvider> providers = Providers.getAllProviders(this.appHandler.getInjectionManager(), (Class)AsyncContextDelegateProvider.class).iterator();
        if (providers.hasNext()) {
            return providers.next();
        }
        return (request, response) -> WebComponent.DEFAULT_ASYNC_DELEGATE;
    }
    
    public WebComponent(final WebConfig webConfig, ResourceConfig resourceConfig) throws ServletException {
        this.webConfig = webConfig;
        if (resourceConfig == null) {
            resourceConfig = createResourceConfig(webConfig);
        }
        final ServletContainerProvider[] allServletContainerProviders = ServletContainerProviderFactory.getAllServletContainerProviders();
        this.configure(resourceConfig, allServletContainerProviders);
        boolean rrbExternalized = false;
        RequestScopedInitializerProvider rsiProvider = null;
        for (final ServletContainerProvider servletContainerProvider : allServletContainerProviders) {
            if (servletContainerProvider instanceof ExtendedServletContainerProvider) {
                final ExtendedServletContainerProvider extendedProvider = (ExtendedServletContainerProvider)servletContainerProvider;
                if (extendedProvider.bindsServletRequestResponse()) {
                    rrbExternalized = true;
                }
                if (rsiProvider == null) {
                    rsiProvider = extendedProvider.getRequestScopedInitializerProvider();
                }
            }
        }
        this.requestScopedInitializer = ((rsiProvider != null) ? rsiProvider : WebComponent.DEFAULT_REQUEST_SCOPE_INITIALIZER_PROVIDER);
        this.requestResponseBindingExternalized = rrbExternalized;
        final AbstractBinder webComponentBinder = new WebComponentBinder(resourceConfig.getProperties());
        resourceConfig.register((Object)webComponentBinder);
        final Object locator = webConfig.getServletContext().getAttribute("jersey.config.servlet.context.serviceLocator");
        this.appHandler = new ApplicationHandler((Application)resourceConfig, (Binder)webComponentBinder, locator);
        this.asyncExtensionDelegate = this.getAsyncExtensionDelegate();
        this.forwardOn404 = (webConfig.getConfigType() == WebConfig.ConfigType.FilterConfig && resourceConfig.isProperty("jersey.config.servlet.filter.forwardOn404"));
        this.queryParamsAsFormParams = !resourceConfig.isProperty("jersey.config.servlet.form.queryParams.disabled");
        this.configSetStatusOverSendError = (boolean)ServerProperties.getValue(resourceConfig.getProperties(), "jersey.config.server.response.setStatusOverSendError", (Object)false, (Class)Boolean.class);
        this.backgroundTaskScheduler = (ScheduledExecutorService)this.appHandler.getInjectionManager().getInstance((Class)ScheduledExecutorService.class, new Annotation[] { (Annotation)BackgroundSchedulerLiteral.INSTANCE });
    }
    
    public Value<Integer> service(final URI baseUri, final URI requestUri, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws ServletException, IOException {
        final ResponseWriter responseWriter = this.serviceImpl(baseUri, requestUri, servletRequest, servletResponse);
        return (Value<Integer>)Values.lazy((Value)new Value<Integer>() {
            public Integer get() {
                return responseWriter.responseContextResolved() ? responseWriter.getResponseStatus() : -1;
            }
        });
    }
    
    ResponseWriter serviceImpl(final URI baseUri, final URI requestUri, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) throws ServletException, IOException {
        final ResponseWriter responseWriter = new ResponseWriter(this.forwardOn404, this.configSetStatusOverSendError, servletResponse, this.asyncExtensionDelegate.createDelegate(servletRequest, servletResponse), this.backgroundTaskScheduler);
        try {
            final ContainerRequest requestContext = new ContainerRequest(baseUri, requestUri, servletRequest.getMethod(), getSecurityContext(servletRequest), (PropertiesDelegate)new ServletPropertiesDelegate(servletRequest));
            this.initContainerRequest(requestContext, servletRequest, servletResponse, responseWriter);
            this.appHandler.handle(requestContext);
        }
        catch (final HeaderValueException hve) {
            if (WebComponent.LOGGER.isLoggable(Level.FINE)) {
                WebComponent.LOGGER.log(Level.FINE, LocalizationMessages.HEADER_VALUE_READ_FAILED(), (Throwable)hve);
            }
            final Response.Status status = Response.Status.BAD_REQUEST;
            if (this.configSetStatusOverSendError) {
                servletResponse.reset();
                servletResponse.setStatus(status.getStatusCode(), status.getReasonPhrase());
            }
            else {
                servletResponse.sendError(status.getStatusCode(), status.getReasonPhrase());
            }
        }
        catch (final Exception e) {
            throw new ServletException((Throwable)e);
        }
        return responseWriter;
    }
    
    private void initContainerRequest(final ContainerRequest requestContext, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final ResponseWriter responseWriter) throws IOException {
        requestContext.setEntityStream((InputStream)servletRequest.getInputStream());
        requestContext.setRequestScopedInitializer(this.requestScopedInitializer.get(new RequestContextProvider() {
            @Override
            public HttpServletRequest getHttpServletRequest() {
                return servletRequest;
            }
            
            @Override
            public HttpServletResponse getHttpServletResponse() {
                return servletResponse;
            }
        }));
        requestContext.setWriter((ContainerResponseWriter)responseWriter);
        this.addRequestHeaders(servletRequest, requestContext);
        this.filterFormParameters(servletRequest, requestContext);
    }
    
    private static SecurityContext getSecurityContext(final HttpServletRequest request) {
        return (SecurityContext)new SecurityContext() {
            public Principal getUserPrincipal() {
                return request.getUserPrincipal();
            }
            
            public boolean isUserInRole(final String role) {
                return request.isUserInRole(role);
            }
            
            public boolean isSecure() {
                return request.isSecure();
            }
            
            public String getAuthenticationScheme() {
                return request.getAuthType();
            }
        };
    }
    
    private static ResourceConfig createResourceConfig(final WebConfig config) throws ServletException {
        final ServletContext servletContext = config.getServletContext();
        ResourceConfig resourceConfig = Utils.retrieve(config.getServletContext(), config.getName());
        if (resourceConfig != null) {
            return resourceConfig;
        }
        final Map<String, Object> initParams = getInitParams(config);
        final Map<String, Object> contextParams = Utils.getContextParams(servletContext);
        final String jaxrsApplicationClassName = config.getInitParameter("javax.ws.rs.Application");
        if (jaxrsApplicationClassName == null) {
            resourceConfig = new ResourceConfig().addProperties((Map)initParams).addProperties((Map)contextParams);
            final String webApp = config.getInitParameter("jersey.config.servlet.provider.webapp");
            if (webApp != null && !"false".equals(webApp)) {
                resourceConfig.registerFinder((ResourceFinder)new WebAppResourcesScanner(servletContext));
            }
            return resourceConfig;
        }
        try {
            final Class<? extends Application> jaxrsApplicationClass = AccessController.doPrivileged((PrivilegedExceptionAction<Class<? extends Application>>)ReflectionHelper.classForNameWithExceptionPEA(jaxrsApplicationClassName));
            if (Application.class.isAssignableFrom(jaxrsApplicationClass)) {
                return ResourceConfig.forApplicationClass((Class)jaxrsApplicationClass).addProperties((Map)initParams).addProperties((Map)contextParams);
            }
            throw new ServletException(LocalizationMessages.RESOURCE_CONFIG_PARENT_CLASS_INVALID(jaxrsApplicationClassName, Application.class));
        }
        catch (final PrivilegedActionException e) {
            throw new ServletException(LocalizationMessages.RESOURCE_CONFIG_UNABLE_TO_LOAD(jaxrsApplicationClassName), e.getCause());
        }
        catch (final ClassNotFoundException e2) {
            throw new ServletException(LocalizationMessages.RESOURCE_CONFIG_UNABLE_TO_LOAD(jaxrsApplicationClassName), (Throwable)e2);
        }
    }
    
    private void configure(final ResourceConfig resourceConfig, final ServletContainerProvider[] allServletContainerProviders) throws ServletException {
        for (final ServletContainerProvider servletContainerProvider : allServletContainerProviders) {
            servletContainerProvider.configure(resourceConfig);
        }
    }
    
    private void addRequestHeaders(final HttpServletRequest request, final ContainerRequest requestContext) {
        final Enumeration<String> names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            final Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                final String value = values.nextElement();
                if (value != null) {
                    requestContext.header(name, (Object)value);
                }
            }
        }
    }
    
    private static Map<String, Object> getInitParams(final WebConfig webConfig) {
        final Map<String, Object> props = new HashMap<String, Object>();
        final Enumeration names = webConfig.getInitParameterNames();
        while (names.hasMoreElements()) {
            final String name = names.nextElement();
            props.put(name, webConfig.getInitParameter(name));
        }
        return props;
    }
    
    private void filterFormParameters(final HttpServletRequest servletRequest, final ContainerRequest containerRequest) {
        if (MediaTypes.typeEqual(MediaType.APPLICATION_FORM_URLENCODED_TYPE, containerRequest.getMediaType()) && !containerRequest.hasEntity()) {
            final Form form = new Form();
            final Enumeration parameterNames = servletRequest.getParameterNames();
            final String queryString = servletRequest.getQueryString();
            final List<String> queryParams = (queryString != null) ? this.getDecodedQueryParamList(queryString) : Collections.emptyList();
            final boolean keepQueryParams = this.queryParamsAsFormParams || queryParams.isEmpty();
            final MultivaluedMap<String, String> formMap = (MultivaluedMap<String, String>)form.asMap();
            while (parameterNames.hasMoreElements()) {
                final String name = parameterNames.nextElement();
                final List<String> values = Arrays.asList(servletRequest.getParameterValues(name));
                formMap.put((Object)name, (Object)(keepQueryParams ? values : this.filterQueryParams(name, values, queryParams)));
            }
            if (!formMap.isEmpty()) {
                containerRequest.setProperty("jersey.config.server.representation.decoded.form", (Object)form);
                if (WebComponent.LOGGER.isLoggable(Level.WARNING)) {
                    WebComponent.LOGGER.log(Level.WARNING, LocalizationMessages.FORM_PARAM_CONSUMED(containerRequest.getRequestUri()));
                }
            }
        }
    }
    
    private List<String> getDecodedQueryParamList(final String queryString) {
        final List<String> params = new ArrayList<String>();
        for (final String param : queryString.split("&")) {
            params.add(UriComponent.decode(param, UriComponent.Type.QUERY_PARAM));
        }
        return params;
    }
    
    private List<String> filterQueryParams(final String name, final List<String> values, final Collection<String> params) {
        return values.stream().filter(s -> {
            final boolean b;
            if (!params.remove(name + "=" + s)) {
                if (!params.remove(name + "[]=" + s)) {
                    return b;
                }
            }
            return b;
        }).collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
    }
    
    public ApplicationHandler getAppHandler() {
        return this.appHandler;
    }
    
    static {
        LOGGER = Logger.getLogger(WebComponent.class.getName());
        REQUEST_TYPE = new GenericType<Ref<HttpServletRequest>>() {}.getType();
        RESPONSE_TYPE = new GenericType<Ref<HttpServletResponse>>() {}.getType();
        DEFAULT_ASYNC_DELEGATE = new AsyncContextDelegate() {
            @Override
            public void suspend() throws IllegalStateException {
                throw new UnsupportedOperationException(LocalizationMessages.ASYNC_PROCESSING_NOT_SUPPORTED());
            }
            
            @Override
            public void complete() {
            }
        };
        DEFAULT_REQUEST_SCOPE_INITIALIZER_PROVIDER = (context -> injectionManager -> {
            ((Ref)injectionManager.getInstance(WebComponent.REQUEST_TYPE)).set((Object)context.getHttpServletRequest());
            ((Ref)injectionManager.getInstance(WebComponent.RESPONSE_TYPE)).set((Object)context.getHttpServletResponse());
        });
    }
    
    private static class HttpServletRequestReferencingFactory extends ReferencingFactory<HttpServletRequest>
    {
        @Inject
        public HttpServletRequestReferencingFactory(final Provider<Ref<HttpServletRequest>> referenceFactory) {
            super((Provider)referenceFactory);
        }
    }
    
    private static class HttpServletResponseReferencingFactory extends ReferencingFactory<HttpServletResponse>
    {
        @Inject
        public HttpServletResponseReferencingFactory(final Provider<Ref<HttpServletResponse>> referenceFactory) {
            super((Provider)referenceFactory);
        }
    }
    
    private final class WebComponentBinder extends AbstractBinder
    {
        private final Map<String, Object> applicationProperties;
        
        private WebComponentBinder(final Map<String, Object> applicationProperties) {
            this.applicationProperties = applicationProperties;
        }
        
        protected void configure() {
            if (!WebComponent.this.requestResponseBindingExternalized) {
                ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)HttpServletRequestReferencingFactory.class).to((Type)HttpServletRequest.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
                ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<HttpServletRequest>>() {})).in((Class)RequestScoped.class);
                ((SupplierClassBinding)((SupplierClassBinding)((SupplierClassBinding)this.bindFactory((Class)HttpServletResponseReferencingFactory.class).to((Type)HttpServletResponse.class)).proxy(true)).proxyForSameScope(false)).in((Class)RequestScoped.class);
                ((SupplierInstanceBinding)this.bindFactory(ReferencingFactory.referenceFactory()).to((GenericType)new GenericType<Ref<HttpServletResponse>>() {})).in((Class)RequestScoped.class);
            }
            ((SupplierInstanceBinding)this.bindFactory((Supplier)WebComponent.this.webConfig::getServletContext).to((Type)ServletContext.class)).in((Class)Singleton.class);
            final ServletConfig servletConfig = WebComponent.this.webConfig.getServletConfig();
            if (WebComponent.this.webConfig.getConfigType() == WebConfig.ConfigType.ServletConfig) {
                ((SupplierInstanceBinding)this.bindFactory(() -> servletConfig).to((Type)ServletConfig.class)).in((Class)Singleton.class);
                final Enumeration initParams = servletConfig.getInitParameterNames();
                while (initParams.hasMoreElements()) {
                    final String initParamName = initParams.nextElement();
                    if (initParamName.startsWith("unit:")) {
                        this.install(new AbstractBinder[] { new PersistenceUnitBinder(servletConfig) });
                        break;
                    }
                }
            }
            else {
                ((SupplierInstanceBinding)this.bindFactory((Supplier)WebComponent.this.webConfig::getFilterConfig).to((Type)FilterConfig.class)).in((Class)Singleton.class);
            }
            ((SupplierInstanceBinding)this.bindFactory(() -> WebComponent.this.webConfig).to((Type)WebConfig.class)).in((Class)Singleton.class);
            this.install(new AbstractBinder[] { (AbstractBinder)new ServiceFinderBinder((Class)AsyncContextDelegateProvider.class, (Map)this.applicationProperties, RuntimeType.SERVER) });
            this.install(new AbstractBinder[] { (AbstractBinder)new ServiceFinderBinder((Class)FilterUrlMappingsProvider.class, (Map)this.applicationProperties, RuntimeType.SERVER) });
        }
    }
}
