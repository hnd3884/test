package org.glassfish.jersey.model.internal;

import javax.ws.rs.core.Configurable;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.glassfish.jersey.internal.inject.CompositeBinder;
import java.util.logging.Level;
import javax.ws.rs.ConstrainedTo;
import org.glassfish.jersey.internal.ServiceFinder;
import org.glassfish.jersey.internal.spi.ForcedAutoDiscoverable;
import java.util.TreeSet;
import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import org.glassfish.jersey.internal.spi.AutoDiscoverable;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.Arrays;
import java.util.Iterator;
import javax.ws.rs.core.Configuration;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Collections;
import java.util.HashMap;
import org.glassfish.jersey.model.ContractProvider;
import java.util.function.Predicate;
import javax.ws.rs.core.Feature;
import java.util.Set;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.internal.inject.Binder;
import java.util.function.Function;
import java.util.logging.Logger;
import org.glassfish.jersey.ExtendedConfig;
import javax.ws.rs.core.FeatureContext;

public class CommonConfig implements FeatureContext, ExtendedConfig
{
    private static final Logger LOGGER;
    private static final Function<Object, Binder> CAST_TO_BINDER;
    private final RuntimeType type;
    private final Map<String, Object> properties;
    private final Map<String, Object> immutablePropertiesView;
    private final Collection<String> immutablePropertyNames;
    private final ComponentBag componentBag;
    private final List<FeatureRegistration> newFeatureRegistrations;
    private final Set<Class<? extends Feature>> enabledFeatureClasses;
    private final Set<Feature> enabledFeatures;
    private boolean disableMetaProviderConfiguration;
    
    public CommonConfig(final RuntimeType type, final Predicate<ContractProvider> registrationStrategy) {
        this.type = type;
        this.properties = new HashMap<String, Object>();
        this.immutablePropertiesView = Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
        this.immutablePropertyNames = Collections.unmodifiableCollection((Collection<? extends String>)this.properties.keySet());
        this.componentBag = ComponentBag.newInstance(registrationStrategy);
        this.newFeatureRegistrations = new LinkedList<FeatureRegistration>();
        this.enabledFeatureClasses = Collections.newSetFromMap(new IdentityHashMap<Class<? extends Feature>, Boolean>());
        this.enabledFeatures = new HashSet<Feature>();
        this.disableMetaProviderConfiguration = false;
    }
    
    public CommonConfig(final CommonConfig config) {
        this.type = config.type;
        this.properties = new HashMap<String, Object>(config.properties.size());
        this.immutablePropertiesView = Collections.unmodifiableMap((Map<? extends String, ?>)this.properties);
        this.immutablePropertyNames = Collections.unmodifiableCollection((Collection<? extends String>)this.properties.keySet());
        this.componentBag = config.componentBag.copy();
        this.newFeatureRegistrations = new LinkedList<FeatureRegistration>();
        this.enabledFeatureClasses = Collections.newSetFromMap(new IdentityHashMap<Class<? extends Feature>, Boolean>());
        this.enabledFeatures = new HashSet<Feature>();
        this.copy(config, false);
    }
    
    private void copy(final CommonConfig config, final boolean loadComponentBag) {
        this.properties.clear();
        this.properties.putAll(config.properties);
        this.newFeatureRegistrations.clear();
        this.newFeatureRegistrations.addAll(config.newFeatureRegistrations);
        this.enabledFeatureClasses.clear();
        this.enabledFeatureClasses.addAll(config.enabledFeatureClasses);
        this.enabledFeatures.clear();
        this.enabledFeatures.addAll(config.enabledFeatures);
        this.disableMetaProviderConfiguration = config.disableMetaProviderConfiguration;
        if (loadComponentBag) {
            this.componentBag.loadFrom(config.componentBag);
        }
    }
    
    public ExtendedConfig getConfiguration() {
        return this;
    }
    
    public RuntimeType getRuntimeType() {
        return this.type;
    }
    
    public Map<String, Object> getProperties() {
        return this.immutablePropertiesView;
    }
    
    public Object getProperty(final String name) {
        return this.properties.get(name);
    }
    
    public boolean isProperty(final String name) {
        return PropertiesHelper.isProperty(this.getProperty(name));
    }
    
    public Collection<String> getPropertyNames() {
        return this.immutablePropertyNames;
    }
    
    public boolean isEnabled(final Class<? extends Feature> featureClass) {
        return this.enabledFeatureClasses.contains(featureClass);
    }
    
    public boolean isEnabled(final Feature feature) {
        return this.enabledFeatures.contains(feature);
    }
    
    public boolean isRegistered(final Object component) {
        return this.componentBag.getInstances().contains(component);
    }
    
    public boolean isRegistered(final Class<?> componentClass) {
        return this.componentBag.getRegistrations().contains(componentClass);
    }
    
    public Map<Class<?>, Integer> getContracts(final Class<?> componentClass) {
        final ContractProvider model = this.componentBag.getModel(componentClass);
        return (model == null) ? Collections.emptyMap() : model.getContractMap();
    }
    
    public Set<Class<?>> getClasses() {
        return this.componentBag.getClasses();
    }
    
    public Set<Object> getInstances() {
        return this.componentBag.getInstances();
    }
    
    public final ComponentBag getComponentBag() {
        return this.componentBag;
    }
    
    protected Inflector<ContractProvider.Builder, ContractProvider> getModelEnhancer(final Class<?> componentClass) {
        return ComponentBag.AS_IS;
    }
    
    public CommonConfig setProperties(final Map<String, ?> properties) {
        this.properties.clear();
        if (properties != null) {
            this.properties.putAll(properties);
        }
        return this;
    }
    
    public CommonConfig addProperties(final Map<String, ?> properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
        return this;
    }
    
    public CommonConfig property(final String name, final Object value) {
        if (value == null) {
            this.properties.remove(name);
        }
        else {
            this.properties.put(name, value);
        }
        return this;
    }
    
    public CommonConfig register(final Class<?> componentClass) {
        this.checkComponentClassNotNull(componentClass);
        if (this.componentBag.register(componentClass, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(null, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Class<?> componentClass, final int bindingPriority) {
        this.checkComponentClassNotNull(componentClass);
        if (this.componentBag.register(componentClass, bindingPriority, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(null, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Class<?> componentClass, final Class<?>... contracts) {
        this.checkComponentClassNotNull(componentClass);
        if (contracts == null || contracts.length == 0) {
            CommonConfig.LOGGER.warning(LocalizationMessages.COMPONENT_CONTRACTS_EMPTY_OR_NULL(componentClass));
            return this;
        }
        if (this.componentBag.register(componentClass, this.asNewIdentitySet(contracts), this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(null, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Class<?> componentClass, final Map<Class<?>, Integer> contracts) {
        this.checkComponentClassNotNull(componentClass);
        if (this.componentBag.register(componentClass, contracts, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(null, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Object component) {
        this.checkProviderNotNull(component);
        final Class<?> componentClass = component.getClass();
        if (this.componentBag.register(component, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(component, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Object component, final int bindingPriority) {
        this.checkProviderNotNull(component);
        final Class<?> componentClass = component.getClass();
        if (this.componentBag.register(component, bindingPriority, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(component, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Object component, final Class<?>... contracts) {
        this.checkProviderNotNull(component);
        final Class<?> componentClass = component.getClass();
        if (contracts == null || contracts.length == 0) {
            CommonConfig.LOGGER.warning(LocalizationMessages.COMPONENT_CONTRACTS_EMPTY_OR_NULL(componentClass));
            return this;
        }
        if (this.componentBag.register(component, this.asNewIdentitySet(contracts), this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(component, componentClass);
        }
        return this;
    }
    
    public CommonConfig register(final Object component, final Map<Class<?>, Integer> contracts) {
        this.checkProviderNotNull(component);
        final Class<?> componentClass = component.getClass();
        if (this.componentBag.register(component, contracts, this.getModelEnhancer(componentClass))) {
            this.processFeatureRegistration(component, componentClass);
        }
        return this;
    }
    
    private void processFeatureRegistration(final Object component, final Class<?> componentClass) {
        final ContractProvider model = this.componentBag.getModel(componentClass);
        if (model.getContracts().contains(Feature.class)) {
            final FeatureRegistration registration = (component != null) ? new FeatureRegistration((Feature)component) : new FeatureRegistration((Class)componentClass);
            this.newFeatureRegistrations.add(registration);
        }
    }
    
    public CommonConfig loadFrom(final Configuration config) {
        if (config instanceof CommonConfig) {
            final CommonConfig commonConfig = (CommonConfig)config;
            this.copy(commonConfig, true);
            this.disableMetaProviderConfiguration = !commonConfig.enabledFeatureClasses.isEmpty();
        }
        else {
            this.setProperties(config.getProperties());
            this.enabledFeatures.clear();
            this.enabledFeatureClasses.clear();
            this.componentBag.clear();
            this.resetRegistrations();
            for (final Class<?> clazz : config.getClasses()) {
                if (Feature.class.isAssignableFrom(clazz) && config.isEnabled((Class)clazz)) {
                    this.disableMetaProviderConfiguration = true;
                }
                this.register(clazz, config.getContracts((Class)clazz));
            }
            for (final Object instance : config.getInstances()) {
                if (instance instanceof Feature && config.isEnabled((Feature)instance)) {
                    this.disableMetaProviderConfiguration = true;
                }
                this.register(instance, config.getContracts((Class)instance.getClass()));
            }
        }
        return this;
    }
    
    private Set<Class<?>> asNewIdentitySet(final Class<?>... contracts) {
        final Set<Class<?>> result = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        result.addAll(Arrays.asList(contracts));
        return result;
    }
    
    private void checkProviderNotNull(final Object provider) {
        if (provider == null) {
            throw new IllegalArgumentException(LocalizationMessages.COMPONENT_CANNOT_BE_NULL());
        }
    }
    
    private void checkComponentClassNotNull(final Class<?> componentClass) {
        if (componentClass == null) {
            throw new IllegalArgumentException(LocalizationMessages.COMPONENT_CLASS_CANNOT_BE_NULL());
        }
    }
    
    public void configureAutoDiscoverableProviders(final InjectionManager injectionManager, final Collection<AutoDiscoverable> autoDiscoverables, final boolean forcedOnly) {
        if (!this.disableMetaProviderConfiguration) {
            final Set<AutoDiscoverable> providers = new TreeSet<AutoDiscoverable>((o1, o2) -> {
                final int p2 = o1.getClass().isAnnotationPresent((Class<? extends Annotation>)Priority.class) ? o1.getClass().getAnnotation(Priority.class).value() : 5000;
                final int p3 = o2.getClass().isAnnotationPresent((Class<? extends Annotation>)Priority.class) ? o2.getClass().getAnnotation(Priority.class).value() : 5000;
                return (p2 < p3 || p2 == p3) ? -1 : 1;
            });
            final List<ForcedAutoDiscoverable> forcedAutoDiscroverables = new LinkedList<ForcedAutoDiscoverable>();
            for (final Class<ForcedAutoDiscoverable> forcedADType : ServiceFinder.find(ForcedAutoDiscoverable.class, true).toClassArray()) {
                forcedAutoDiscroverables.add(injectionManager.createAndInitialize(forcedADType));
            }
            providers.addAll(forcedAutoDiscroverables);
            if (!forcedOnly) {
                providers.addAll(autoDiscoverables);
            }
            for (final AutoDiscoverable autoDiscoverable : providers) {
                final ConstrainedTo constrainedTo = autoDiscoverable.getClass().getAnnotation(ConstrainedTo.class);
                if (constrainedTo != null) {
                    if (!this.type.equals((Object)constrainedTo.value())) {
                        continue;
                    }
                }
                try {
                    autoDiscoverable.configure((FeatureContext)this);
                }
                catch (final Exception e) {
                    CommonConfig.LOGGER.log(Level.FINE, LocalizationMessages.AUTODISCOVERABLE_CONFIGURATION_FAILED(autoDiscoverable.getClass()), e);
                }
            }
        }
    }
    
    public void configureMetaProviders(final InjectionManager injectionManager, final ManagedObjectsFinalizer finalizer) {
        final Set<Binder> configuredBinders = this.configureBinders(injectionManager, Collections.emptySet());
        if (!this.disableMetaProviderConfiguration) {
            this.configureExternalObjects(injectionManager);
            this.configureFeatures(injectionManager, new HashSet<FeatureRegistration>(), this.resetRegistrations(), finalizer);
            this.configureBinders(injectionManager, configuredBinders);
        }
    }
    
    private Set<Binder> configureBinders(final InjectionManager injectionManager, final Set<Binder> configured) {
        final Set<Binder> allConfigured = Collections.newSetFromMap(new IdentityHashMap<Binder, Boolean>());
        allConfigured.addAll(configured);
        final Collection<Binder> binders = this.getBinder(configured);
        if (!binders.isEmpty()) {
            injectionManager.register(CompositeBinder.wrap(binders));
            allConfigured.addAll(binders);
        }
        return allConfigured;
    }
    
    private Collection<Binder> getBinder(final Set<Binder> configured) {
        return this.componentBag.getInstances(ComponentBag.BINDERS_ONLY).stream().map((Function<? super Object, ?>)CommonConfig.CAST_TO_BINDER).filter(binder -> !configured.contains(binder)).collect((Collector<? super Object, ?, Collection<Binder>>)Collectors.toList());
    }
    
    private void configureExternalObjects(final InjectionManager injectionManager) {
        this.componentBag.getInstances(model -> ComponentBag.EXTERNAL_ONLY.test(model, injectionManager)).forEach(injectionManager::register);
        this.componentBag.getClasses(model -> ComponentBag.EXTERNAL_ONLY.test(model, injectionManager)).forEach(injectionManager::register);
    }
    
    private void configureFeatures(final InjectionManager injectionManager, final Set<FeatureRegistration> processed, final List<FeatureRegistration> unprocessed, final ManagedObjectsFinalizer managedObjectsFinalizer) {
        FeatureContextWrapper featureContextWrapper = null;
        for (final FeatureRegistration registration : unprocessed) {
            if (processed.contains(registration)) {
                CommonConfig.LOGGER.config(LocalizationMessages.FEATURE_HAS_ALREADY_BEEN_PROCESSED(registration.getFeatureClass()));
            }
            else {
                Feature feature = registration.getFeature();
                if (feature == null) {
                    feature = injectionManager.createAndInitialize(registration.getFeatureClass());
                    managedObjectsFinalizer.registerForPreDestroyCall(feature);
                }
                else if (!RuntimeType.CLIENT.equals((Object)this.type)) {
                    injectionManager.inject(feature);
                }
                if (this.enabledFeatures.contains(feature)) {
                    CommonConfig.LOGGER.config(LocalizationMessages.FEATURE_HAS_ALREADY_BEEN_PROCESSED(feature));
                }
                else {
                    if (featureContextWrapper == null) {
                        featureContextWrapper = new FeatureContextWrapper((FeatureContext)this, injectionManager);
                    }
                    final boolean success = feature.configure((FeatureContext)featureContextWrapper);
                    if (!success) {
                        continue;
                    }
                    processed.add(registration);
                    this.configureFeatures(injectionManager, processed, this.resetRegistrations(), managedObjectsFinalizer);
                    this.enabledFeatureClasses.add(registration.getFeatureClass());
                    this.enabledFeatures.add(feature);
                }
            }
        }
    }
    
    private List<FeatureRegistration> resetRegistrations() {
        final List<FeatureRegistration> result = new ArrayList<FeatureRegistration>(this.newFeatureRegistrations);
        this.newFeatureRegistrations.clear();
        return result;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommonConfig)) {
            return false;
        }
        final CommonConfig that = (CommonConfig)o;
        return this.type == that.type && this.properties.equals(that.properties) && this.componentBag.equals(that.componentBag) && this.enabledFeatureClasses.equals(that.enabledFeatureClasses) && this.enabledFeatures.equals(that.enabledFeatures) && this.newFeatureRegistrations.equals(that.newFeatureRegistrations);
    }
    
    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.properties.hashCode();
        result = 31 * result + this.componentBag.hashCode();
        result = 31 * result + this.newFeatureRegistrations.hashCode();
        result = 31 * result + this.enabledFeatures.hashCode();
        result = 31 * result + this.enabledFeatureClasses.hashCode();
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger(CommonConfig.class.getName());
        CAST_TO_BINDER = Binder.class::cast;
    }
    
    private static final class FeatureRegistration
    {
        private final Class<? extends Feature> featureClass;
        private final Feature feature;
        
        private FeatureRegistration(final Class<? extends Feature> featureClass) {
            this.featureClass = featureClass;
            this.feature = null;
        }
        
        private FeatureRegistration(final Feature feature) {
            this.featureClass = feature.getClass();
            this.feature = feature;
        }
        
        Class<? extends Feature> getFeatureClass() {
            return this.featureClass;
        }
        
        public Feature getFeature() {
            return this.feature;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FeatureRegistration)) {
                return false;
            }
            final FeatureRegistration other = (FeatureRegistration)obj;
            return this.featureClass == other.featureClass || (this.feature != null && (this.feature == other.feature || this.feature.equals(other.feature)));
        }
        
        @Override
        public int hashCode() {
            int hash = 47;
            hash = 13 * hash + ((this.feature != null) ? this.feature.hashCode() : 0);
            hash = 13 * hash + ((this.featureClass != null) ? this.featureClass.hashCode() : 0);
            return hash;
        }
    }
}
