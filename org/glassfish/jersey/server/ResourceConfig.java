package org.glassfish.jersey.server;

import org.glassfish.jersey.internal.Errors;
import java.util.IdentityHashMap;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.ExtendedConfig;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.process.Inflector;
import java.security.PrivilegedAction;
import java.security.AccessController;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.model.internal.CommonConfig;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.util.Tokenizer;
import java.io.InputStream;
import java.io.IOException;
import org.glassfish.jersey.server.internal.scanning.AnnotationAcceptingListener;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import java.util.logging.Level;
import javax.ws.rs.core.Feature;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.model.internal.ComponentBag;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import org.glassfish.jersey.server.internal.scanning.FilesScanner;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.glassfish.jersey.server.model.Resource;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Application;

public class ResourceConfig extends Application implements Configurable<ResourceConfig>, ServerConfig
{
    private static final Logger LOGGER;
    private transient Set<Class<?>> cachedClasses;
    private transient Set<Class<?>> cachedClassesView;
    private transient Set<Object> cachedSingletons;
    private transient Set<Object> cachedSingletonsView;
    private transient boolean resetFinders;
    private volatile State state;
    
    public static ResourceConfig forApplication(final Application application) {
        return (application instanceof ResourceConfig) ? ((ResourceConfig)application) : new WrappingResourceConfig(application, null, null);
    }
    
    public static ResourceConfig forApplicationClass(final Class<? extends Application> applicationClass) {
        return new WrappingResourceConfig(null, applicationClass, null);
    }
    
    public static ResourceConfig forApplicationClass(final Class<? extends Application> applicationClass, final Set<Class<?>> defaultClasses) {
        return new WrappingResourceConfig(null, applicationClass, defaultClasses);
    }
    
    public ResourceConfig() {
        this.cachedClasses = null;
        this.cachedClassesView = null;
        this.cachedSingletons = null;
        this.cachedSingletonsView = null;
        this.resetFinders = false;
        this.state = new State();
    }
    
    public ResourceConfig(final Set<Class<?>> classes) {
        this();
        this.registerClasses(classes);
    }
    
    public ResourceConfig(final Class<?>... classes) {
        this((Set<Class<?>>)Arrays.stream(classes).collect((Collector<? super Class<?>, ?, Set<? super Class<?>>>)Collectors.toSet()));
    }
    
    public ResourceConfig(final ResourceConfig original) {
        this.cachedClasses = null;
        this.cachedClassesView = null;
        this.cachedSingletons = null;
        this.cachedSingletonsView = null;
        this.resetFinders = false;
        this.state = new State(original.state);
    }
    
    public final ResourceConfig addProperties(final Map<String, Object> properties) {
        this.state.addProperties((Map)properties);
        return this;
    }
    
    public ResourceConfig setProperties(final Map<String, ?> properties) {
        this.state.setProperties((Map)properties);
        return this;
    }
    
    public ResourceConfig property(final String name, final Object value) {
        this.state.property(name, value);
        return this;
    }
    
    public ResourceConfig register(final Class<?> componentClass) {
        this.invalidateCache();
        this.state.register((Class)componentClass);
        return this;
    }
    
    public ResourceConfig register(final Class<?> componentClass, final int bindingPriority) {
        this.invalidateCache();
        this.state.register((Class)componentClass, bindingPriority);
        return this;
    }
    
    public ResourceConfig register(final Class<?> componentClass, final Class<?>... contracts) {
        this.invalidateCache();
        this.state.register((Class)componentClass, (Class[])contracts);
        return this;
    }
    
    public ResourceConfig register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        this.invalidateCache();
        this.state.register((Class)componentClass, (Map)contracts);
        return this;
    }
    
    public ResourceConfig register(final Object component) {
        this.invalidateCache();
        this.state.register(component);
        return this;
    }
    
    public ResourceConfig register(final Object component, final int bindingPriority) {
        this.invalidateCache();
        this.state.register(component, bindingPriority);
        return this;
    }
    
    public ResourceConfig register(final Object component, final Class<?>... contracts) {
        this.invalidateCache();
        this.state.register(component, (Class[])contracts);
        return this;
    }
    
    public ResourceConfig register(final Object component, final Map<Class<?>, Integer> contracts) {
        this.invalidateCache();
        this.state.register(component, (Map)contracts);
        return this;
    }
    
    public final ResourceConfig registerClasses(final Set<Class<?>> classes) {
        if (classes == null) {
            return this;
        }
        for (final Class<?> cls : classes) {
            this.register(cls);
        }
        return this;
    }
    
    public final ResourceConfig registerClasses(final Class<?>... classes) {
        if (classes == null) {
            return this;
        }
        return this.registerClasses((Set<Class<?>>)Arrays.stream(classes).collect((Collector<? super Class<?>, ?, Set<? super Class<?>>>)Collectors.toSet()));
    }
    
    public final ResourceConfig registerInstances(final Set<Object> instances) {
        if (instances == null) {
            return this;
        }
        for (final Object instance : instances) {
            this.register(instance);
        }
        return this;
    }
    
    public final ResourceConfig registerInstances(final Object... instances) {
        if (instances == null) {
            return this;
        }
        return this.registerInstances((Set<Object>)Arrays.stream(instances).collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
    }
    
    public final ResourceConfig registerResources(final Resource... resources) {
        if (resources == null) {
            return this;
        }
        return this.registerResources((Set<Resource>)Arrays.stream(resources).collect((Collector<? super Resource, ?, Set<? super Resource>>)Collectors.toSet()));
    }
    
    public final ResourceConfig registerResources(final Set<Resource> resources) {
        if (resources == null) {
            return this;
        }
        this.state.registerResources(resources);
        return this;
    }
    
    public final ResourceConfig registerFinder(final ResourceFinder resourceFinder) {
        if (resourceFinder == null) {
            return this;
        }
        this.invalidateCache();
        this.state.registerFinder(resourceFinder);
        return this;
    }
    
    public final ResourceConfig setApplicationName(final String applicationName) {
        this.state.setApplicationName(applicationName);
        return this;
    }
    
    public final ResourceConfig setClassLoader(final ClassLoader classLoader) {
        this.state.setClassLoader(classLoader);
        return this;
    }
    
    public final ResourceConfig packages(final String... packages) {
        return this.packages(true, packages);
    }
    
    public final ResourceConfig packages(final boolean recursive, final String... packages) {
        if (packages == null || packages.length == 0) {
            return this;
        }
        return this.registerFinder(new PackageNamesScanner(packages, recursive));
    }
    
    public final ResourceConfig files(final String... files) {
        return this.files(true, files);
    }
    
    public final ResourceConfig files(final boolean recursive, final String... files) {
        if (files == null || files.length == 0) {
            return this;
        }
        return this.registerFinder(new FilesScanner(files, recursive));
    }
    
    final void invalidateCache() {
        this.cachedClasses = null;
        this.cachedClassesView = null;
        this.cachedSingletons = null;
        this.cachedSingletonsView = null;
        if (this.resetFinders) {
            for (final ResourceFinder finder : this.state.resourceFinders) {
                finder.reset();
            }
            this.resetFinders = false;
        }
    }
    
    final void lock() {
        final State current = this.state;
        if (!(current instanceof ImmutableState)) {
            this.setupApplicationName();
            this.state = new ImmutableState(current);
        }
    }
    
    public final ServerConfig getConfiguration() {
        return this;
    }
    
    public final Map<String, Object> getProperties() {
        return this.state.getProperties();
    }
    
    public final Object getProperty(final String name) {
        return this.state.getProperty(name);
    }
    
    public Collection<String> getPropertyNames() {
        return this.state.getPropertyNames();
    }
    
    public final boolean isProperty(final String name) {
        return this.state.isProperty(name);
    }
    
    public final Set<Class<?>> getClasses() {
        if (this.cachedClassesView == null) {
            this.cachedClasses = this._getClasses();
            this.cachedClassesView = Collections.unmodifiableSet((Set<? extends Class<?>>)this.cachedClasses);
        }
        return this.cachedClassesView;
    }
    
    public final Set<Object> getInstances() {
        return this.getSingletons();
    }
    
    public final Set<Object> getSingletons() {
        if (this.cachedSingletonsView == null) {
            this.cachedSingletons = this._getSingletons();
            this.cachedSingletonsView = Collections.unmodifiableSet((Set<?>)((this.cachedSingletons == null) ? new HashSet<Object>() : this.cachedSingletons));
        }
        return this.cachedSingletonsView;
    }
    
    final ComponentBag getComponentBag() {
        return this.state.getComponentBag();
    }
    
    final void configureAutoDiscoverableProviders(final InjectionManager injectionManager, final Collection<AutoDiscoverable> autoDiscoverables) {
        this.state.configureAutoDiscoverableProviders(injectionManager, (Collection)autoDiscoverables, false);
    }
    
    final void configureForcedAutoDiscoverableProviders(final InjectionManager injectionManager) {
        this.state.configureAutoDiscoverableProviders(injectionManager, (Collection)Collections.emptyList(), true);
    }
    
    final void configureMetaProviders(final InjectionManager injectionManager, final ManagedObjectsFinalizer finalizer) {
        this.state.configureMetaProviders(injectionManager, finalizer);
    }
    
    public RuntimeType getRuntimeType() {
        return this.state.getRuntimeType();
    }
    
    public boolean isEnabled(final Feature feature) {
        return this.state.isEnabled(feature);
    }
    
    public boolean isEnabled(final Class<? extends Feature> featureClass) {
        return this.state.isEnabled((Class)featureClass);
    }
    
    public boolean isRegistered(final Object component) {
        return this.state.isRegistered(component);
    }
    
    public boolean isRegistered(final Class<?> componentClass) {
        return this.state.isRegistered((Class)componentClass);
    }
    
    public Map<Class<?>, Integer> getContracts(final Class<?> componentClass) {
        return this.state.getContracts((Class)componentClass);
    }
    
    Set<Class<?>> _getClasses() {
        final Set<Class<?>> result = this.scanClasses();
        result.addAll(this.state.getClasses());
        return result;
    }
    
    private Set<Class<?>> scanClasses() {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        final State _state = this.state;
        final Set<ResourceFinder> rfs = new HashSet<ResourceFinder>(_state.getResourceFinders());
        this.resetFinders = true;
        final String[] classNames = this.parsePropertyValue("jersey.config.server.provider.classnames");
        if (classNames != null) {
            for (final String className : classNames) {
                try {
                    result.add(_state.getClassLoader().loadClass(className));
                }
                catch (final ClassNotFoundException e) {
                    ResourceConfig.LOGGER.log(Level.CONFIG, LocalizationMessages.UNABLE_TO_LOAD_CLASS(className));
                }
            }
        }
        final String[] packageNames = this.parsePropertyValue("jersey.config.server.provider.packages");
        if (packageNames != null) {
            final Object p = this.getProperty("jersey.config.server.provider.scanning.recursive");
            final boolean recursive = p == null || PropertiesHelper.isProperty(p);
            rfs.add(new PackageNamesScanner(packageNames, recursive));
        }
        final String[] classPathElements = this.parsePropertyValue("jersey.config.server.provider.classpath");
        if (classPathElements != null) {
            rfs.add(new FilesScanner(classPathElements, true));
        }
        final AnnotationAcceptingListener afl = AnnotationAcceptingListener.newJaxrsResourceAndProviderListener(_state.getClassLoader());
        for (final ResourceFinder resourceFinder : rfs) {
            while (resourceFinder.hasNext()) {
                final String next = resourceFinder.next();
                if (afl.accept(next)) {
                    final InputStream in = resourceFinder.open();
                    try {
                        afl.process(next, in);
                    }
                    catch (final IOException e2) {
                        ResourceConfig.LOGGER.log(Level.WARNING, LocalizationMessages.RESOURCE_CONFIG_UNABLE_TO_PROCESS(next));
                        try {
                            in.close();
                        }
                        catch (final IOException ex) {
                            ResourceConfig.LOGGER.log(Level.FINER, "Error closing resource stream.", ex);
                        }
                    }
                    finally {
                        try {
                            in.close();
                        }
                        catch (final IOException ex2) {
                            ResourceConfig.LOGGER.log(Level.FINER, "Error closing resource stream.", ex2);
                        }
                    }
                }
            }
        }
        result.addAll(afl.getAnnotatedClasses());
        return result;
    }
    
    private String[] parsePropertyValue(final String propertyName) {
        String[] classNames = null;
        final Object o = this.state.getProperties().get(propertyName);
        if (o != null) {
            if (o instanceof String) {
                classNames = Tokenizer.tokenize((String)o);
            }
            else if (o instanceof String[]) {
                classNames = Tokenizer.tokenize((String[])o);
            }
        }
        return classNames;
    }
    
    Set<Class<?>> getRegisteredClasses() {
        return this.state.getComponentBag().getRegistrations();
    }
    
    Set<Object> _getSingletons() {
        final Set<Object> result = new HashSet<Object>();
        result.addAll(this.state.getInstances());
        return result;
    }
    
    public final Set<Resource> getResources() {
        return this.state.getResources();
    }
    
    public final ClassLoader getClassLoader() {
        return this.state.getClassLoader();
    }
    
    public final Application getApplication() {
        return this._getApplication();
    }
    
    Application _getApplication() {
        return this;
    }
    
    public String getApplicationName() {
        return this.state.getApplicationName();
    }
    
    Class<? extends Application> getApplicationClass() {
        return null;
    }
    
    final ResourceConfig setApplication(final Application app) {
        return this._setApplication(app);
    }
    
    ResourceConfig _setApplication(final Application app) {
        throw new UnsupportedOperationException();
    }
    
    static ResourceConfig createRuntimeConfig(final Application application) {
        return (application instanceof ResourceConfig) ? new RuntimeConfig((ResourceConfig)application) : new RuntimeConfig(application);
    }
    
    private static Application unwrapCustomRootApplication(ResourceConfig resourceConfig) {
        Application app;
        for (app = null; resourceConfig != null; resourceConfig = (ResourceConfig)app) {
            app = resourceConfig.getApplication();
            if (app == resourceConfig) {
                return null;
            }
            if (!(app instanceof ResourceConfig)) {
                break;
            }
        }
        return app;
    }
    
    static Application unwrapApplication(Application application) {
        while (application instanceof ResourceConfig) {
            final Application wrappedApplication = ((ResourceConfig)application).getApplication();
            if (wrappedApplication == application) {
                break;
            }
            application = wrappedApplication;
        }
        return application;
    }
    
    private void setupApplicationName() {
        final String appName = ServerProperties.getValue(this.getProperties(), "jersey.config.server.application.name", null, String.class);
        if (appName != null && this.getApplicationName() == null) {
            this.setApplicationName(appName);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ResourceConfig.class.getName());
    }
    
    private static class State extends CommonConfig implements ServerConfig
    {
        private final Set<ResourceFinder> resourceFinders;
        private final Set<Resource> resources;
        private final Set<Resource> resourcesView;
        private volatile String applicationName;
        private volatile ClassLoader classLoader;
        
        public State() {
            super(RuntimeType.SERVER, ComponentBag.INCLUDE_ALL);
            this.classLoader = null;
            this.classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)ReflectionHelper.getContextClassLoaderPA());
            this.resourceFinders = new HashSet<ResourceFinder>();
            this.resources = new HashSet<Resource>();
            this.resourcesView = Collections.unmodifiableSet((Set<? extends Resource>)this.resources);
        }
        
        public State(final State original) {
            super((CommonConfig)original);
            this.classLoader = null;
            this.classLoader = original.classLoader;
            this.applicationName = original.applicationName;
            this.resources = new HashSet<Resource>(original.resources);
            this.resourcesView = Collections.unmodifiableSet((Set<? extends Resource>)this.resources);
            this.resourceFinders = new HashSet<ResourceFinder>(original.resourceFinders);
        }
        
        public void setClassLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
        
        public void setApplicationName(final String applicationName) {
            this.applicationName = applicationName;
        }
        
        public void registerResources(final Set<Resource> resources) {
            this.resources.addAll(resources);
        }
        
        public void registerFinder(final ResourceFinder resourceFinder) {
            this.resourceFinders.add(resourceFinder);
        }
        
        protected Inflector<ContractProvider.Builder, ContractProvider> getModelEnhancer(final Class<?> componentClass) {
            return (Inflector<ContractProvider.Builder, ContractProvider>)(builder -> {
                if (builder.getScope() == null && builder.getContracts().isEmpty() && Resource.getPath(componentClass) != null) {
                    builder.scope((Class)RequestScoped.class);
                }
                return builder.build();
            });
        }
        
        public State loadFrom(final Configuration config) {
            super.loadFrom(config);
            this.resourceFinders.clear();
            this.resources.clear();
            State other = null;
            if (config instanceof ResourceConfig) {
                other = ((ResourceConfig)config).state;
            }
            if (config instanceof State) {
                other = (State)config;
            }
            if (other != null) {
                this.resourceFinders.addAll(other.resourceFinders);
                this.resources.addAll(other.resources);
            }
            return this;
        }
        
        public final Set<Resource> getResources() {
            return this.resourcesView;
        }
        
        public ServerConfig getConfiguration() {
            return this;
        }
        
        public Set<ResourceFinder> getResourceFinders() {
            return this.resourceFinders;
        }
        
        public ClassLoader getClassLoader() {
            return this.classLoader;
        }
        
        private String getApplicationName() {
            return this.applicationName;
        }
    }
    
    private static final class ImmutableState extends State
    {
        private ImmutableState(final State original) {
            super(original);
        }
        
        @Override
        public void setClassLoader(final ClassLoader classLoader) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        @Override
        public void registerResources(final Set<Resource> resources) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        @Override
        public void registerFinder(final ResourceFinder resourceFinder) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State addProperties(final Map<String, ?> properties) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State property(final String name, final Object value) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Class<?> componentClass) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Class<?> componentClass, final int bindingPriority) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Class<?> componentClass, final Class<?>... contracts) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Object component) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Object component, final int bindingPriority) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Object component, final Class<?>... contracts) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State register(final Object component, final Map<Class<?>, Integer> contracts) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public State setProperties(final Map<String, ?> properties) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public void configureAutoDiscoverableProviders(final InjectionManager injectionManager, final Collection<AutoDiscoverable> autoDiscoverables, final boolean forcedOnly) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
        
        public void configureMetaProviders(final InjectionManager injectionManager, final ManagedObjectsFinalizer finalizer) {
            throw new IllegalStateException(LocalizationMessages.RC_NOT_MODIFIABLE());
        }
    }
    
    private static class WrappingResourceConfig extends ResourceConfig
    {
        private Application application;
        private Class<? extends Application> applicationClass;
        private final Set<Class<?>> defaultClasses;
        
        public WrappingResourceConfig(final Application application, final Class<? extends Application> applicationClass, final Set<Class<?>> defaultClasses) {
            this.defaultClasses = new HashSet<Class<?>>();
            if (application == null && applicationClass == null) {
                throw new IllegalArgumentException(LocalizationMessages.RESOURCE_CONFIG_ERROR_NULL_APPLICATIONCLASS());
            }
            this.application = application;
            this.applicationClass = applicationClass;
            if (defaultClasses != null) {
                this.defaultClasses.addAll(defaultClasses);
            }
            this.mergeApplications(application);
        }
        
        @Override
        ResourceConfig _setApplication(final Application application) {
            this.application = application;
            this.applicationClass = null;
            this.mergeApplications(application);
            return this;
        }
        
        @Override
        Application _getApplication() {
            return this.application;
        }
        
        @Override
        Class<? extends Application> getApplicationClass() {
            return this.applicationClass;
        }
        
        private void mergeApplications(final Application application) {
            if (application instanceof ResourceConfig) {
                final ResourceConfig rc = (ResourceConfig)application;
                super.registerResources(rc.getResources());
                rc.invalidateCache();
                rc.addProperties(super.getProperties());
                super.addProperties(rc.getProperties());
                super.setApplicationName(rc.getApplicationName());
                super.setClassLoader(rc.getClassLoader());
                rc.lock();
            }
            else if (application != null) {
                super.addProperties(application.getProperties());
            }
        }
        
        @Override
        Set<Class<?>> _getClasses() {
            final Set<Class<?>> result = new HashSet<Class<?>>();
            final Set<Class<?>> applicationClasses = this.application.getClasses();
            result.addAll((applicationClasses == null) ? new HashSet<Class<?>>() : applicationClasses);
            if (result.isEmpty() && this.getSingletons().isEmpty()) {
                result.addAll(this.defaultClasses);
            }
            if (!(this.application instanceof ResourceConfig)) {
                result.addAll(super._getClasses());
            }
            return result;
        }
        
        @Override
        Set<Object> _getSingletons() {
            return this.application.getSingletons();
        }
    }
    
    private static class RuntimeConfig extends ResourceConfig
    {
        private final Set<Class<?>> originalRegistrations;
        private final Application application;
        
        private RuntimeConfig(final ResourceConfig original) {
            super(original);
            this.application = original;
            final Application customRootApp = unwrapCustomRootApplication(original);
            if (customRootApp != null) {
                this.registerComponentsOf(customRootApp);
            }
            (this.originalRegistrations = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>())).addAll(super.getRegisteredClasses());
            final Set<Object> externalInstances = original.getSingletons().stream().filter(external -> !this.originalRegistrations.contains(external.getClass())).collect((Collector<? super Object, ?, Set<Object>>)Collectors.toSet());
            this.registerInstances(externalInstances);
            final Set<Class<?>> externalClasses = original.getClasses().stream().filter(external -> !this.originalRegistrations.contains(external)).collect((Collector<? super Object, ?, Set<Class<?>>>)Collectors.toSet());
            this.registerClasses(externalClasses);
        }
        
        private void registerComponentsOf(final Application application) {
            Errors.processWithException((Runnable)new Runnable() {
                @Override
                public void run() {
                    final Set<Object> singletons = application.getSingletons();
                    if (singletons != null) {
                        RuntimeConfig.this.registerInstances((Set<Object>)singletons.stream().filter(input -> {
                            final Object val$application = application;
                            if (input == null) {
                                Errors.warning((Object)application, LocalizationMessages.NON_INSTANTIABLE_COMPONENT(null));
                            }
                            return input != null;
                        }).collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
                    }
                    final Set<Class<?>> classes = application.getClasses();
                    if (classes != null) {
                        RuntimeConfig.this.registerClasses((Set<Class<?>>)classes.stream().filter(input -> {
                            final Object val$application2 = application;
                            if (input == null) {
                                Errors.warning((Object)application, LocalizationMessages.NON_INSTANTIABLE_COMPONENT(null));
                            }
                            return input != null;
                        }).collect((Collector<? super Object, ?, Set<? super Object>>)Collectors.toSet()));
                    }
                }
            });
        }
        
        private RuntimeConfig(final Application application) {
            this.application = application;
            if (application != null) {
                this.registerComponentsOf(application);
                this.addProperties(application.getProperties());
            }
            this.originalRegistrations = super.getRegisteredClasses();
        }
        
        @Override
        Set<Class<?>> _getClasses() {
            return this.state.getClasses();
        }
        
        @Override
        Set<Object> _getSingletons() {
            return this.state.getInstances();
        }
        
        @Override
        Set<Class<?>> getRegisteredClasses() {
            return this.originalRegistrations;
        }
        
        @Override
        Application _getApplication() {
            return this.application;
        }
    }
}
