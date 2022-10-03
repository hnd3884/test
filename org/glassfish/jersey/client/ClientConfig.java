package org.glassfish.jersey.client;

import javax.ws.rs.client.Client;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.CommonProperties;
import java.util.Arrays;
import org.glassfish.jersey.internal.AutoDiscoverableConfigurator;
import org.glassfish.jersey.internal.JaxrsProviders;
import org.glassfish.jersey.internal.ExceptionMapperFactory;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import org.glassfish.jersey.internal.ContextResolverFactory;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import java.util.Collections;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import java.util.List;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import java.util.Iterator;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.BootstrapConfigurator;
import org.glassfish.jersey.client.spi.Connector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;
import org.glassfish.jersey.client.spi.ConnectorProvider;
import java.util.Set;
import javax.ws.rs.core.Feature;
import java.util.Collection;
import javax.ws.rs.RuntimeType;
import java.util.Map;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.ExtendedConfig;
import javax.ws.rs.core.Configurable;

public class ClientConfig implements Configurable<ClientConfig>, ExtendedConfig
{
    private State state;
    
    public ClientConfig() {
        this.state = new State(null);
    }
    
    public ClientConfig(final Class<?>... providerClasses) {
        this();
        for (final Class<?> providerClass : providerClasses) {
            this.state.register(providerClass);
        }
    }
    
    public ClientConfig(final Object... providers) {
        this();
        for (final Object provider : providers) {
            this.state.register(provider);
        }
    }
    
    ClientConfig(final JerseyClient parent) {
        this.state = new State(parent);
    }
    
    ClientConfig(final JerseyClient parent, final Configuration that) {
        if (that instanceof ClientConfig) {
            this.state = ((ClientConfig)that).state.copy(parent);
        }
        else {
            (this.state = new State(parent)).loadFrom(that);
        }
    }
    
    private ClientConfig(final State state) {
        this.state = state;
    }
    
    ClientConfig snapshot() {
        this.state.markAsShared();
        return new ClientConfig(this.state);
    }
    
    public ClientConfig loadFrom(final Configuration config) {
        if (config instanceof ClientConfig) {
            this.state = ((ClientConfig)config).state.copy();
        }
        else {
            this.state.loadFrom(config);
        }
        return this;
    }
    
    public ClientConfig register(final Class<?> providerClass) {
        this.state = this.state.register(providerClass);
        return this;
    }
    
    public ClientConfig register(final Object provider) {
        this.state = this.state.register(provider);
        return this;
    }
    
    public ClientConfig register(final Class<?> providerClass, final int bindingPriority) {
        this.state = this.state.register(providerClass, bindingPriority);
        return this;
    }
    
    public ClientConfig register(final Class<?> providerClass, final Class<?>... contracts) {
        this.state = this.state.register(providerClass, contracts);
        return this;
    }
    
    public ClientConfig register(final Class<?> providerClass, final Map<Class<?>, Integer> contracts) {
        this.state = this.state.register(providerClass, contracts);
        return this;
    }
    
    public ClientConfig register(final Object provider, final int bindingPriority) {
        this.state = this.state.register(provider, bindingPriority);
        return this;
    }
    
    public ClientConfig register(final Object provider, final Class<?>... contracts) {
        this.state = this.state.register(provider, contracts);
        return this;
    }
    
    public ClientConfig register(final Object provider, final Map<Class<?>, Integer> contracts) {
        this.state = this.state.register(provider, contracts);
        return this;
    }
    
    public ClientConfig property(final String name, final Object value) {
        this.state = this.state.property(name, value);
        return this;
    }
    
    public ClientConfig getConfiguration() {
        return this;
    }
    
    public RuntimeType getRuntimeType() {
        return this.state.getRuntimeType();
    }
    
    public Map<String, Object> getProperties() {
        return this.state.getProperties();
    }
    
    public Object getProperty(final String name) {
        return this.state.getProperty(name);
    }
    
    public Collection<String> getPropertyNames() {
        return this.state.getPropertyNames();
    }
    
    public boolean isProperty(final String name) {
        return this.state.isProperty(name);
    }
    
    public boolean isEnabled(final Feature feature) {
        return this.state.isEnabled(feature);
    }
    
    public boolean isEnabled(final Class<? extends Feature> featureClass) {
        return this.state.isEnabled(featureClass);
    }
    
    public boolean isRegistered(final Object component) {
        return this.state.isRegistered(component);
    }
    
    public Map<Class<?>, Integer> getContracts(final Class<?> componentClass) {
        return this.state.getContracts(componentClass);
    }
    
    public boolean isRegistered(final Class<?> componentClass) {
        return this.state.isRegistered(componentClass);
    }
    
    public Set<Class<?>> getClasses() {
        return this.state.getClasses();
    }
    
    public Set<Object> getInstances() {
        return this.state.getInstances();
    }
    
    public ClientConfig connectorProvider(final ConnectorProvider connectorProvider) {
        this.state = this.state.connectorProvider(connectorProvider);
        return this;
    }
    
    public ClientConfig executorService(final ExecutorService executorService) {
        this.state = this.state.executorService(executorService);
        return this;
    }
    
    public ClientConfig scheduledExecutorService(final ScheduledExecutorService scheduledExecutorService) {
        this.state = this.state.scheduledExecutorService(scheduledExecutorService);
        return this;
    }
    
    public Connector getConnector() {
        return this.state.getConnector();
    }
    
    public ConnectorProvider getConnectorProvider() {
        return this.state.getConnectorProvider();
    }
    
    public ExecutorService getExecutorService() {
        return this.state.getExecutorService();
    }
    
    public ScheduledExecutorService getScheduledExecutorService() {
        return this.state.getScheduledExecutorService();
    }
    
    ClientRuntime getRuntime() {
        return (ClientRuntime)this.state.runtime.get();
    }
    
    public ClientExecutor getClientExecutor() {
        return (ClientExecutor)this.state.runtime.get();
    }
    
    public JerseyClient getClient() {
        return this.state.getClient();
    }
    
    ClientConfig preInitialize() {
        this.state = this.state.preInitialize();
        return this;
    }
    
    void checkClient() throws IllegalStateException {
        if (this.getClient() == null) {
            throw new IllegalStateException("Client configuration does not contain a parent client instance.");
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final ClientConfig other = (ClientConfig)obj;
        return this.state == other.state || (this.state != null && this.state.equals(other.state));
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + ((this.state != null) ? this.state.hashCode() : 0);
        return hash;
    }
    
    private static class RuntimeConfigConfigurator implements BootstrapConfigurator
    {
        private final State runtimeConfig;
        
        private RuntimeConfigConfigurator(final State runtimeConfig) {
            this.runtimeConfig = runtimeConfig;
        }
        
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            bootstrapBag.setConfiguration((Configuration)this.runtimeConfig);
            injectionManager.register(Bindings.service((Object)this.runtimeConfig).to((Class)Configuration.class));
        }
    }
    
    private static class State implements Configurable<State>, ExtendedConfig
    {
        private static final StateChangeStrategy IDENTITY;
        private static final StateChangeStrategy COPY_ON_CHANGE;
        private volatile StateChangeStrategy strategy;
        private final CommonConfig commonConfig;
        private final JerseyClient client;
        private volatile ConnectorProvider connectorProvider;
        private volatile ExecutorService executorService;
        private volatile ScheduledExecutorService scheduledExecutorService;
        private final LazyValue<ClientRuntime> runtime;
        
        State(final JerseyClient client) {
            this.runtime = (LazyValue<ClientRuntime>)Values.lazy(this::initRuntime);
            this.strategy = State.IDENTITY;
            this.commonConfig = new CommonConfig(RuntimeType.CLIENT, ComponentBag.EXCLUDE_EMPTY);
            this.client = client;
            final Iterator<ConnectorProvider> iterator = ServiceFinder.find((Class)ConnectorProvider.class).iterator();
            if (iterator.hasNext()) {
                this.connectorProvider = iterator.next();
            }
            else {
                this.connectorProvider = new HttpUrlConnectorProvider();
            }
        }
        
        private State(final JerseyClient client, final State original) {
            this.runtime = (LazyValue<ClientRuntime>)Values.lazy(this::initRuntime);
            this.strategy = State.IDENTITY;
            this.client = client;
            this.commonConfig = new CommonConfig(original.commonConfig);
            this.connectorProvider = original.connectorProvider;
            this.executorService = original.executorService;
            this.scheduledExecutorService = original.scheduledExecutorService;
        }
        
        State copy() {
            return new State(this.client, this);
        }
        
        State copy(final JerseyClient client) {
            return new State(client, this);
        }
        
        void markAsShared() {
            this.strategy = State.COPY_ON_CHANGE;
        }
        
        State preInitialize() {
            final State state = this.strategy.onChange(this);
            state.strategy = State.COPY_ON_CHANGE;
            ((ClientRuntime)state.runtime.get()).preInitialize();
            return state;
        }
        
        public State property(final String name, final Object value) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.property(name, value);
            return state;
        }
        
        public State loadFrom(final Configuration config) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.loadFrom(config);
            return state;
        }
        
        public State register(final Class<?> providerClass) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register((Class)providerClass);
            return state;
        }
        
        public State register(final Object provider) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register(provider);
            return state;
        }
        
        public State register(final Class<?> providerClass, final int bindingPriority) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register((Class)providerClass, bindingPriority);
            return state;
        }
        
        public State register(final Class<?> providerClass, final Class<?>... contracts) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register((Class)providerClass, (Class[])contracts);
            return state;
        }
        
        public State register(final Class<?> providerClass, final Map<Class<?>, Integer> contracts) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register((Class)providerClass, (Map)contracts);
            return state;
        }
        
        public State register(final Object provider, final int bindingPriority) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register(provider, bindingPriority);
            return state;
        }
        
        public State register(final Object provider, final Class<?>... contracts) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register(provider, (Class[])contracts);
            return state;
        }
        
        public State register(final Object provider, final Map<Class<?>, Integer> contracts) {
            final State state = this.strategy.onChange(this);
            state.commonConfig.register(provider, (Map)contracts);
            return state;
        }
        
        State connectorProvider(final ConnectorProvider provider) {
            if (provider == null) {
                throw new NullPointerException(LocalizationMessages.NULL_CONNECTOR_PROVIDER());
            }
            final State state = this.strategy.onChange(this);
            state.connectorProvider = provider;
            return state;
        }
        
        State executorService(final ExecutorService executorService) {
            if (executorService == null) {
                throw new NullPointerException(LocalizationMessages.NULL_EXECUTOR_SERVICE());
            }
            final State state = this.strategy.onChange(this);
            state.executorService = executorService;
            return state;
        }
        
        State scheduledExecutorService(final ScheduledExecutorService scheduledExecutorService) {
            if (scheduledExecutorService == null) {
                throw new NullPointerException(LocalizationMessages.NULL_SCHEDULED_EXECUTOR_SERVICE());
            }
            final State state = this.strategy.onChange(this);
            state.scheduledExecutorService = scheduledExecutorService;
            return state;
        }
        
        Connector getConnector() {
            return this.runtime.isInitialized() ? ((ClientRuntime)this.runtime.get()).getConnector() : null;
        }
        
        ConnectorProvider getConnectorProvider() {
            return this.connectorProvider;
        }
        
        ExecutorService getExecutorService() {
            return this.executorService;
        }
        
        ScheduledExecutorService getScheduledExecutorService() {
            return this.scheduledExecutorService;
        }
        
        JerseyClient getClient() {
            return this.client;
        }
        
        public State getConfiguration() {
            return this;
        }
        
        public RuntimeType getRuntimeType() {
            return this.commonConfig.getConfiguration().getRuntimeType();
        }
        
        public Map<String, Object> getProperties() {
            return this.commonConfig.getConfiguration().getProperties();
        }
        
        public Object getProperty(final String name) {
            return this.commonConfig.getConfiguration().getProperty(name);
        }
        
        public Collection<String> getPropertyNames() {
            return this.commonConfig.getConfiguration().getPropertyNames();
        }
        
        public boolean isProperty(final String name) {
            return this.commonConfig.getConfiguration().isProperty(name);
        }
        
        public boolean isEnabled(final Feature feature) {
            return this.commonConfig.getConfiguration().isEnabled(feature);
        }
        
        public boolean isEnabled(final Class<? extends Feature> featureClass) {
            return this.commonConfig.getConfiguration().isEnabled((Class)featureClass);
        }
        
        public boolean isRegistered(final Object component) {
            return this.commonConfig.getConfiguration().isRegistered(component);
        }
        
        public boolean isRegistered(final Class<?> componentClass) {
            return this.commonConfig.getConfiguration().isRegistered((Class)componentClass);
        }
        
        public Map<Class<?>, Integer> getContracts(final Class<?> componentClass) {
            return this.commonConfig.getConfiguration().getContracts((Class)componentClass);
        }
        
        public Set<Class<?>> getClasses() {
            return this.commonConfig.getConfiguration().getClasses();
        }
        
        public Set<Object> getInstances() {
            return this.commonConfig.getConfiguration().getInstances();
        }
        
        public void configureAutoDiscoverableProviders(final InjectionManager injectionManager, final List<AutoDiscoverable> autoDiscoverables) {
            this.commonConfig.configureAutoDiscoverableProviders(injectionManager, (Collection)autoDiscoverables, false);
        }
        
        public void configureForcedAutoDiscoverableProviders(final InjectionManager injectionManager) {
            this.commonConfig.configureAutoDiscoverableProviders(injectionManager, (Collection)Collections.emptyList(), true);
        }
        
        public void configureMetaProviders(final InjectionManager injectionManager, final ManagedObjectsFinalizer finalizer) {
            this.commonConfig.configureMetaProviders(injectionManager, finalizer);
        }
        
        public ComponentBag getComponentBag() {
            return this.commonConfig.getComponentBag();
        }
        
        private ClientRuntime initRuntime() {
            this.markAsShared();
            final State runtimeCfgState = this.copy();
            runtimeCfgState.markAsShared();
            final InjectionManager injectionManager = Injections.createInjectionManager();
            injectionManager.register((Binder)new ClientBinder(runtimeCfgState.getProperties()));
            final BootstrapBag bootstrapBag = new BootstrapBag();
            bootstrapBag.setManagedObjectsFinalizer(new ManagedObjectsFinalizer(injectionManager));
            final List<BootstrapConfigurator> bootstrapConfigurators = Arrays.asList((BootstrapConfigurator)new RequestScope.RequestScopeConfigurator(), (BootstrapConfigurator)new RuntimeConfigConfigurator(runtimeCfgState), (BootstrapConfigurator)new ContextResolverFactory.ContextResolversConfigurator(), (BootstrapConfigurator)new MessageBodyFactory.MessageBodyWorkersConfigurator(), (BootstrapConfigurator)new ExceptionMapperFactory.ExceptionMappersConfigurator(), (BootstrapConfigurator)new JaxrsProviders.ProvidersConfigurator(), (BootstrapConfigurator)new AutoDiscoverableConfigurator(RuntimeType.CLIENT));
            bootstrapConfigurators.forEach(configurator -> configurator.init(injectionManager, bootstrapBag));
            if (!(boolean)CommonProperties.getValue((Map)runtimeCfgState.getProperties(), RuntimeType.CLIENT, "jersey.config.disableAutoDiscovery", (Object)Boolean.FALSE, (Class)Boolean.class)) {
                runtimeCfgState.configureAutoDiscoverableProviders(injectionManager, bootstrapBag.getAutoDiscoverables());
            }
            else {
                runtimeCfgState.configureForcedAutoDiscoverableProviders(injectionManager);
            }
            runtimeCfgState.configureMetaProviders(injectionManager, bootstrapBag.getManagedObjectsFinalizer());
            ProviderBinder.bindProviders(runtimeCfgState.getComponentBag(), RuntimeType.CLIENT, (Set)null, injectionManager);
            final ClientExecutorProvidersConfigurator executorProvidersConfigurator = new ClientExecutorProvidersConfigurator(runtimeCfgState.getComponentBag(), runtimeCfgState.client, this.executorService, this.scheduledExecutorService);
            executorProvidersConfigurator.init(injectionManager, bootstrapBag);
            injectionManager.completeRegistration();
            bootstrapConfigurators.forEach(configurator -> configurator.postInit(injectionManager, bootstrapBag));
            final ClientConfig configuration = new ClientConfig(runtimeCfgState, null);
            final Connector connector = this.connectorProvider.getConnector((Client)this.client, (Configuration)configuration);
            final ClientRuntime crt = new ClientRuntime(configuration, connector, injectionManager, bootstrapBag);
            this.client.registerShutdownHook(crt);
            return crt;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final State state = (State)o;
            if (this.client != null) {
                if (this.client.equals(state.client)) {
                    return this.commonConfig.equals((Object)state.commonConfig) && ((this.connectorProvider == null) ? (state.connectorProvider == null) : this.connectorProvider.equals(state.connectorProvider));
                }
            }
            else if (state.client == null) {
                return this.commonConfig.equals((Object)state.commonConfig) && ((this.connectorProvider == null) ? (state.connectorProvider == null) : this.connectorProvider.equals(state.connectorProvider));
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = this.commonConfig.hashCode();
            result = 31 * result + ((this.client != null) ? this.client.hashCode() : 0);
            result = 31 * result + ((this.connectorProvider != null) ? this.connectorProvider.hashCode() : 0);
            return result;
        }
        
        static {
            IDENTITY = (state -> state);
            COPY_ON_CHANGE = State::copy;
        }
        
        private interface StateChangeStrategy
        {
            State onChange(final State p0);
        }
    }
}
