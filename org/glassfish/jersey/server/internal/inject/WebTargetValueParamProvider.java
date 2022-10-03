package org.glassfish.jersey.server.internal.inject;

import org.glassfish.jersey.server.ExtendedUriInfo;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;
import java.util.List;
import java.util.Map;
import javax.ws.rs.ProcessingException;
import org.glassfish.jersey.server.ClientBinding;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Arrays;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.internal.Errors;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.server.ContainerRequest;
import java.util.Iterator;
import org.glassfish.jersey.internal.util.collection.Values;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configurable;
import org.glassfish.jersey.client.ClientConfig;
import javax.ws.rs.client.ClientBuilder;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Provider;
import org.glassfish.jersey.server.model.Parameter;
import org.glassfish.jersey.internal.util.collection.Value;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import javax.ws.rs.core.Configuration;
import java.util.function.Function;

final class WebTargetValueParamProvider extends AbstractValueParamProvider
{
    private final Function<Class<? extends Configuration>, Configuration> clientConfigProvider;
    private final Supplier<Configuration> serverConfig;
    private final ConcurrentMap<BindingModel, Value<ManagedClient>> managedClients;
    
    public WebTargetValueParamProvider(final Supplier<Configuration> serverConfig, final Function<Class<? extends Configuration>, Configuration> clientConfigProvider) {
        super(null, new Parameter.Source[] { Parameter.Source.URI });
        this.clientConfigProvider = clientConfigProvider;
        this.serverConfig = serverConfig;
        (this.managedClients = new ConcurrentHashMap<BindingModel, Value<ManagedClient>>()).put(BindingModel.EMPTY, (Value<ManagedClient>)Values.lazy((Value)new Value<ManagedClient>() {
            public ManagedClient get() {
                Client client;
                if (serverConfig.get() == null) {
                    client = ClientBuilder.newClient();
                }
                else {
                    final ClientConfig clientConfig = new ClientConfig();
                    WebTargetValueParamProvider.this.copyProviders(serverConfig.get(), (Configurable<?>)clientConfig);
                    client = ClientBuilder.newClient((Configuration)clientConfig);
                }
                return new ManagedClient(client, "");
            }
        }));
    }
    
    private void copyProviders(final Configuration source, final Configurable<?> target) {
        final Configuration targetConfig = target.getConfiguration();
        for (final Class<?> c : source.getClasses()) {
            if (!targetConfig.isRegistered((Class)c)) {
                target.register((Class)c, source.getContracts((Class)c));
            }
        }
        for (final Object o : source.getInstances()) {
            final Class<?> c2 = o.getClass();
            if (!targetConfig.isRegistered(o)) {
                target.register((Class)c2, source.getContracts((Class)c2));
            }
        }
    }
    
    @Override
    protected Function<ContainerRequest, ?> createValueProvider(final Parameter parameter) {
        return (Function)Errors.processWithException((Producer)new Producer<Function<ContainerRequest, ?>>() {
            public Function<ContainerRequest, ?> call() {
                final String targetUriTemplate = parameter.getSourceName();
                if (targetUriTemplate == null || targetUriTemplate.length() == 0) {
                    Errors.warning((Object)this, LocalizationMessages.INJECTED_WEBTARGET_URI_INVALID(targetUriTemplate));
                    return null;
                }
                final Class<?> rawParameterType = parameter.getRawType();
                if (rawParameterType == WebTarget.class) {
                    final BindingModel binding = BindingModel.create(Arrays.asList(parameter.getAnnotations()));
                    Value<ManagedClient> client = (Value<ManagedClient>)WebTargetValueParamProvider.this.managedClients.get(binding);
                    if (client == null) {
                        client = (Value<ManagedClient>)Values.lazy((Value)new Value<ManagedClient>() {
                            public ManagedClient get() {
                                final String prefix = binding.getAnnotation().annotationType().getName() + ".";
                                final String baseUriProperty = prefix + "baseUri";
                                final Object bu = WebTargetValueParamProvider.this.serverConfig.get().getProperty(baseUriProperty);
                                final String customBaseUri = (bu != null) ? bu.toString() : binding.baseUri();
                                final String configClassProperty = prefix + "configClass";
                                final ClientConfig cfg = WebTargetValueParamProvider.this.resolveConfig(configClassProperty, binding);
                                final String inheritProvidersProperty = prefix + "inheritServerProviders";
                                if (PropertiesHelper.isProperty(WebTargetValueParamProvider.this.serverConfig.get().getProperty(inheritProvidersProperty)) || binding.inheritProviders()) {
                                    WebTargetValueParamProvider.this.copyProviders(WebTargetValueParamProvider.this.serverConfig.get(), (Configurable<?>)cfg);
                                }
                                final String propertyPrefix = prefix + "property.";
                                String property = null;
                                final Collection<String> clientProperties = WebTargetValueParamProvider.this.serverConfig.get().getPropertyNames().stream().filter(property -> property.startsWith(propertyPrefix)).collect((Collector<? super Object, ?, Collection<String>>)Collectors.toSet());
                                final Iterator<String> iterator = clientProperties.iterator();
                                while (iterator.hasNext()) {
                                    property = iterator.next();
                                    cfg.property(property.substring(propertyPrefix.length()), WebTargetValueParamProvider.this.serverConfig.get().getProperty(property));
                                }
                                return new ManagedClient(ClientBuilder.newClient((Configuration)cfg), customBaseUri);
                            }
                        });
                        final Value<ManagedClient> previous = WebTargetValueParamProvider.this.managedClients.putIfAbsent(binding, client);
                        if (previous != null) {
                            client = previous;
                        }
                    }
                    return new WebTargetValueSupplier(targetUriTemplate, client);
                }
                Errors.warning((Object)this, LocalizationMessages.UNSUPPORTED_URI_INJECTION_TYPE(rawParameterType));
                return null;
            }
        });
    }
    
    private ClientConfig resolveConfig(final String configClassProperty, final BindingModel binding) {
        Class<? extends Configuration> configClass = binding.getConfigClass();
        final Object _cc = this.serverConfig.get().getProperty(configClassProperty);
        if (_cc != null) {
            Class<?> cc;
            if (_cc instanceof String) {
                cc = AccessController.doPrivileged((PrivilegedAction<Class<?>>)ReflectionHelper.classForNamePA((String)_cc));
            }
            else if (_cc instanceof Class) {
                cc = (Class)_cc;
            }
            else {
                cc = null;
            }
            if (cc != null && Configuration.class.isAssignableFrom(cc)) {
                configClass = cc.asSubclass(Configuration.class);
            }
            else {
                Errors.warning((Object)this, LocalizationMessages.ILLEGAL_CLIENT_CONFIG_CLASS_PROPERTY_VALUE(configClassProperty, _cc, configClass.getName()));
            }
        }
        final Configuration cfg = this.clientConfigProvider.apply(configClass);
        return (cfg instanceof ClientConfig) ? cfg : new ClientConfig().loadFrom(cfg);
    }
    
    private static class ManagedClient
    {
        private final Client instance;
        private final String customBaseUri;
        
        private ManagedClient(final Client instance, final String customBaseUri) {
            this.instance = instance;
            this.customBaseUri = customBaseUri;
        }
    }
    
    private static class BindingModel
    {
        public static final BindingModel EMPTY;
        private final Annotation annotation;
        private final Class<? extends Configuration> configClass;
        private final boolean inheritProviders;
        private final String baseUri;
        
        public static BindingModel create(final Annotation binding) {
            if (binding == null || binding.annotationType().getAnnotation(ClientBinding.class) == null) {
                return BindingModel.EMPTY;
            }
            return new BindingModel(binding);
        }
        
        public static BindingModel create(final Collection<Annotation> bindingCandidates) {
            final Collection<Annotation> filtered = bindingCandidates.stream().filter(input -> input != null && input.annotationType().getAnnotation(ClientBinding.class) != null).collect((Collector<? super Annotation, ?, Collection<Annotation>>)Collectors.toList());
            if (filtered.isEmpty()) {
                return BindingModel.EMPTY;
            }
            if (filtered.size() > 1) {
                throw new ProcessingException("Too many client binding annotations.");
            }
            return new BindingModel(filtered.iterator().next());
        }
        
        private BindingModel(final Annotation annotation) {
            if (annotation == null) {
                this.annotation = null;
                this.configClass = (Class<? extends Configuration>)ClientConfig.class;
                this.inheritProviders = true;
                this.baseUri = "";
            }
            else {
                this.annotation = annotation;
                final ClientBinding cba = annotation.annotationType().getAnnotation(ClientBinding.class);
                this.configClass = cba.configClass();
                this.inheritProviders = cba.inheritServerProviders();
                this.baseUri = cba.baseUri();
            }
        }
        
        public Annotation getAnnotation() {
            return this.annotation;
        }
        
        public Class<? extends Configuration> getConfigClass() {
            return this.configClass;
        }
        
        public boolean inheritProviders() {
            return this.inheritProviders;
        }
        
        public String baseUri() {
            return this.baseUri;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final BindingModel that = (BindingModel)o;
            return (this.annotation != null) ? this.annotation.equals(that.annotation) : (that.annotation == null);
        }
        
        @Override
        public int hashCode() {
            return (this.annotation != null) ? this.annotation.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "BindingModel{binding=" + this.annotation + ", configClass=" + this.configClass + ", inheritProviders=" + this.inheritProviders + ", baseUri=" + this.baseUri + '}';
        }
        
        static {
            EMPTY = new BindingModel(null);
        }
    }
    
    private static final class WebTargetValueSupplier implements Function<ContainerRequest, WebTarget>
    {
        private final String uri;
        private final Value<ManagedClient> client;
        
        WebTargetValueSupplier(final String uri, final Value<ManagedClient> client) {
            this.uri = uri;
            this.client = client;
        }
        
        @Override
        public WebTarget apply(final ContainerRequest containerRequest) {
            final ExtendedUriInfo uriInfo = containerRequest.getUriInfo();
            final Map<String, Object> pathParamValues = (Map<String, Object>)uriInfo.getPathParameters().entrySet().stream().collect(Collectors.toMap((Function<? super Object, ?>)Map.Entry::getKey, stringObjectEntry -> {
                final List<String> input = stringObjectEntry.getValue();
                return input.isEmpty() ? null : input.get(0);
            }));
            JerseyUriBuilder uriBuilder = new JerseyUriBuilder().uri(this.uri).resolveTemplates((Map)pathParamValues);
            final ManagedClient managedClient = (ManagedClient)this.client.get();
            if (!uriBuilder.isAbsolute()) {
                final String customBaseUri = managedClient.customBaseUri;
                final String rootUri = customBaseUri.isEmpty() ? uriInfo.getBaseUri().toString() : customBaseUri;
                uriBuilder = new JerseyUriBuilder().uri(rootUri).path(uriBuilder.toTemplate());
            }
            return managedClient.instance.target((UriBuilder)uriBuilder);
        }
    }
}
