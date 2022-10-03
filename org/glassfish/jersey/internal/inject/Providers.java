package org.glassfish.jersey.internal.inject;

import java.util.stream.Stream;
import java.util.LinkedList;
import org.glassfish.jersey.spi.Contract;
import java.util.logging.Level;
import org.glassfish.jersey.internal.LocalizationMessages;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import org.glassfish.jersey.model.ContractProvider;
import java.util.Collections;
import java.util.IdentityHashMap;
import javax.annotation.Priority;
import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import org.glassfish.jersey.model.internal.RankedComparator;
import java.util.Iterator;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.Comparator;
import java.util.Collection;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.ws.rs.core.Feature;
import javax.ws.rs.client.RxInvokerProvider;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ContextResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Providers
{
    private static final Logger LOGGER;
    private static final Map<Class<?>, ProviderRuntime> JAX_RS_PROVIDER_INTERFACE_WHITELIST;
    private static final Map<Class<?>, ProviderRuntime> EXTERNAL_PROVIDER_INTERFACE_WHITELIST;
    
    private Providers() {
    }
    
    private static Map<Class<?>, ProviderRuntime> getJaxRsProviderInterfaces() {
        final Map<Class<?>, ProviderRuntime> interfaces = new HashMap<Class<?>, ProviderRuntime>();
        interfaces.put(ContextResolver.class, ProviderRuntime.BOTH);
        interfaces.put(ExceptionMapper.class, ProviderRuntime.BOTH);
        interfaces.put(MessageBodyReader.class, ProviderRuntime.BOTH);
        interfaces.put(MessageBodyWriter.class, ProviderRuntime.BOTH);
        interfaces.put(ReaderInterceptor.class, ProviderRuntime.BOTH);
        interfaces.put(WriterInterceptor.class, ProviderRuntime.BOTH);
        interfaces.put(ParamConverterProvider.class, ProviderRuntime.BOTH);
        interfaces.put(ContainerRequestFilter.class, ProviderRuntime.SERVER);
        interfaces.put(ContainerResponseFilter.class, ProviderRuntime.SERVER);
        interfaces.put(DynamicFeature.class, ProviderRuntime.SERVER);
        interfaces.put(ClientResponseFilter.class, ProviderRuntime.CLIENT);
        interfaces.put(ClientRequestFilter.class, ProviderRuntime.CLIENT);
        interfaces.put(RxInvokerProvider.class, ProviderRuntime.CLIENT);
        return interfaces;
    }
    
    private static Map<Class<?>, ProviderRuntime> getExternalProviderInterfaces() {
        final Map<Class<?>, ProviderRuntime> interfaces = new HashMap<Class<?>, ProviderRuntime>();
        interfaces.putAll(Providers.JAX_RS_PROVIDER_INTERFACE_WHITELIST);
        interfaces.put(Feature.class, ProviderRuntime.BOTH);
        interfaces.put(Binder.class, ProviderRuntime.BOTH);
        return interfaces;
    }
    
    public static <T> Set<T> getProviders(final InjectionManager injectionManager, final Class<T> contract) {
        final Collection<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract, new Annotation[0]);
        return getProviderClasses(providers);
    }
    
    public static <T> Set<T> getCustomProviders(final InjectionManager injectionManager, final Class<T> contract) {
        final List<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract, Comparator.comparingInt((ToIntFunction<? super Class<?>>)Providers::getPriority), CustomAnnotationLiteral.INSTANCE);
        return getProviderClasses(providers);
    }
    
    public static <T> Iterable<T> getAllProviders(final InjectionManager injectionManager, final Class<T> contract) {
        return getAllProviders(injectionManager, contract, (Comparator<T>)null);
    }
    
    public static <T> Iterable<RankedProvider<T>> getAllRankedProviders(final InjectionManager injectionManager, final Class<T> contract) {
        final List<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract, CustomAnnotationLiteral.INSTANCE);
        providers.addAll(getServiceHolders(injectionManager, contract, new Annotation[0]));
        final LinkedHashMap<ServiceHolder<T>, RankedProvider<T>> providerMap = new LinkedHashMap<ServiceHolder<T>, RankedProvider<T>>();
        for (final ServiceHolder<T> provider : providers) {
            if (!providerMap.containsKey(provider)) {
                final Set<Type> contractTypes = provider.getContractTypes();
                final Class<?> implementationClass = provider.getImplementationClass();
                boolean proxyGenerated = true;
                for (final Type ct : contractTypes) {
                    if (((Class)ct).isAssignableFrom(implementationClass)) {
                        proxyGenerated = false;
                        break;
                    }
                }
                final Set<Type> contracts = proxyGenerated ? contractTypes : null;
                providerMap.put(provider, new RankedProvider<T>(provider.getInstance(), provider.getRank(), contracts));
            }
        }
        return providerMap.values();
    }
    
    public static <T> Iterable<T> sortRankedProviders(final RankedComparator<T> comparator, final Iterable<RankedProvider<T>> providers) {
        return StreamSupport.stream(providers.spliterator(), false).sorted(comparator).map((Function<? super RankedProvider<T>, ?>)RankedProvider::getProvider).collect((Collector<? super Object, ?, Iterable<T>>)Collectors.toList());
    }
    
    public static <T> Iterable<T> getAllRankedSortedProviders(final InjectionManager injectionManager, final Class<T> contract) {
        final Iterable<RankedProvider<T>> allRankedProviders = (Iterable<RankedProvider<T>>)getAllRankedProviders(injectionManager, (Class<Object>)contract);
        return sortRankedProviders(new RankedComparator<T>(), allRankedProviders);
    }
    
    public static <T> Iterable<T> mergeAndSortRankedProviders(final RankedComparator<T> comparator, final Iterable<Iterable<RankedProvider<T>>> providerIterables) {
        return StreamSupport.stream(providerIterables.spliterator(), false).flatMap(rankedProviders -> StreamSupport.stream(rankedProviders.spliterator(), false)).sorted((Comparator<? super Object>)comparator).map((Function<? super Object, ?>)RankedProvider::getProvider).collect((Collector<? super Object, ?, Iterable<T>>)Collectors.toList());
    }
    
    public static <T> Iterable<T> getAllProviders(final InjectionManager injectionManager, final Class<T> contract, final RankedComparator<T> comparator) {
        return sortRankedProviders(comparator, (Iterable<RankedProvider<T>>)getAllRankedProviders(injectionManager, (Class<T>)contract));
    }
    
    public static <T> Collection<ServiceHolder<T>> getAllServiceHolders(final InjectionManager injectionManager, final Class<T> contract) {
        final List<ServiceHolder<T>> providers = getServiceHolders(injectionManager, contract, Comparator.comparingInt((ToIntFunction<? super Class<?>>)Providers::getPriority), CustomAnnotationLiteral.INSTANCE);
        providers.addAll(getServiceHolders(injectionManager, contract, new Annotation[0]));
        final LinkedHashSet<ServiceHolder<T>> providersSet = new LinkedHashSet<ServiceHolder<T>>();
        for (final ServiceHolder<T> provider : providers) {
            if (!providersSet.contains(provider)) {
                providersSet.add(provider);
            }
        }
        return providersSet;
    }
    
    private static <T> List<ServiceHolder<T>> getServiceHolders(final InjectionManager bm, final Class<T> contract, final Annotation... qualifiers) {
        return bm.getAllServiceHolders(contract, qualifiers);
    }
    
    private static <T> List<ServiceHolder<T>> getServiceHolders(final InjectionManager injectionManager, final Class<T> contract, final Comparator<Class<?>> objectComparator, final Annotation... qualifiers) {
        final List<ServiceHolder<T>> serviceHolders = injectionManager.getAllServiceHolders(contract, qualifiers);
        serviceHolders.sort((o1, o2) -> objectComparator.compare(o1.getImplementationClass(), o2.getImplementationClass()));
        return serviceHolders;
    }
    
    public static boolean isJaxRsProvider(final Class<?> clazz) {
        for (final Class<?> providerType : Providers.JAX_RS_PROVIDER_INTERFACE_WHITELIST.keySet()) {
            if (providerType.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> Iterable<T> getAllProviders(final InjectionManager injectionManager, final Class<T> contract, final Comparator<T> comparator) {
        final List<T> providerList = new ArrayList<T>((Collection<? extends T>)getProviderClasses((Collection<ServiceHolder<Object>>)getAllServiceHolders(injectionManager, (Class<T>)contract)));
        if (comparator != null) {
            providerList.sort(comparator);
        }
        return providerList;
    }
    
    private static <T> Set<T> getProviderClasses(final Collection<ServiceHolder<T>> providers) {
        return providers.stream().map((Function<? super ServiceHolder<T>, ?>)Providers::holder2service).collect((Collector<? super Object, ?, Set<T>>)Collectors.toCollection((Supplier<R>)LinkedHashSet::new));
    }
    
    private static <T> T holder2service(final ServiceHolder<T> holder) {
        return (holder != null) ? holder.getInstance() : null;
    }
    
    private static int getPriority(final Class<?> serviceClass) {
        final Priority annotation = serviceClass.getAnnotation(Priority.class);
        if (annotation != null) {
            return annotation.value();
        }
        return 5000;
    }
    
    public static Set<Class<?>> getProviderContracts(final Class<?> clazz) {
        final Set<Class<?>> contracts = Collections.newSetFromMap(new IdentityHashMap<Class<?>, Boolean>());
        computeProviderContracts(clazz, contracts);
        return contracts;
    }
    
    private static void computeProviderContracts(final Class<?> clazz, final Set<Class<?>> contracts) {
        for (final Class<?> contract : getImplementedContracts(clazz)) {
            if (isSupportedContract(contract)) {
                contracts.add(contract);
            }
            computeProviderContracts(contract, contracts);
        }
    }
    
    public static boolean checkProviderRuntime(final Class<?> component, final ContractProvider model, final RuntimeType runtimeConstraint, final boolean scanned, final boolean isResource) {
        final Set<Class<?>> contracts = model.getContracts();
        final ConstrainedTo constrainedTo = component.getAnnotation(ConstrainedTo.class);
        final RuntimeType componentConstraint = (constrainedTo == null) ? null : constrainedTo.value();
        if (Feature.class.isAssignableFrom(component)) {
            return true;
        }
        final StringBuilder warnings = new StringBuilder();
        try {
            boolean foundComponentCompatible = componentConstraint == null;
            boolean foundRuntimeCompatibleContract = isResource && runtimeConstraint == RuntimeType.SERVER;
            for (final Class<?> contract : contracts) {
                final RuntimeType contractConstraint = getContractConstraint(contract, componentConstraint);
                foundRuntimeCompatibleContract |= (contractConstraint == null || contractConstraint == runtimeConstraint);
                if (componentConstraint != null) {
                    if (contractConstraint != componentConstraint) {
                        warnings.append(LocalizationMessages.WARNING_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(component.getName(), componentConstraint.name(), contract.getName(), contractConstraint.name())).append(" ");
                    }
                    else {
                        foundComponentCompatible = true;
                    }
                }
            }
            if (!foundComponentCompatible) {
                warnings.append(LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_WRONG_PACKAGE(component.getName(), componentConstraint.name())).append(" ");
                logProviderSkipped(warnings, component, isResource);
                return false;
            }
            final boolean isProviderRuntimeCompatible = componentConstraint == null || componentConstraint == runtimeConstraint;
            if (!isProviderRuntimeCompatible && !scanned) {
                warnings.append(LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_WRONG_RUNTIME(component.getName(), componentConstraint.name(), runtimeConstraint.name())).append(" ");
                logProviderSkipped(warnings, component, isResource);
            }
            if (!foundRuntimeCompatibleContract && !scanned) {
                warnings.append(LocalizationMessages.ERROR_PROVIDER_REGISTERED_WRONG_RUNTIME(component.getName(), runtimeConstraint.name())).append(" ");
                logProviderSkipped(warnings, component, isResource);
                return false;
            }
            return isProviderRuntimeCompatible && foundRuntimeCompatibleContract;
        }
        finally {
            if (warnings.length() > 0) {
                Providers.LOGGER.log(Level.WARNING, warnings.toString());
            }
        }
    }
    
    private static void logProviderSkipped(final StringBuilder sb, final Class<?> provider, final boolean alsoResourceClass) {
        sb.append(alsoResourceClass ? LocalizationMessages.ERROR_PROVIDER_AND_RESOURCE_CONSTRAINED_TO_IGNORED(provider.getName()) : LocalizationMessages.ERROR_PROVIDER_CONSTRAINED_TO_IGNORED(provider.getName())).append(" ");
    }
    
    public static boolean isSupportedContract(final Class<?> type) {
        return Providers.EXTERNAL_PROVIDER_INTERFACE_WHITELIST.get(type) != null || type.isAnnotationPresent(Contract.class);
    }
    
    private static RuntimeType getContractConstraint(final Class<?> clazz, final RuntimeType defaultConstraint) {
        final ProviderRuntime jaxRsProvider = Providers.EXTERNAL_PROVIDER_INTERFACE_WHITELIST.get(clazz);
        RuntimeType result = null;
        if (jaxRsProvider != null) {
            result = jaxRsProvider.getRuntime();
        }
        else if (clazz.getAnnotation(Contract.class) != null) {
            final ConstrainedTo constrainedToAnnotation = clazz.getAnnotation(ConstrainedTo.class);
            if (constrainedToAnnotation != null) {
                result = constrainedToAnnotation.value();
            }
        }
        return (result == null) ? defaultConstraint : result;
    }
    
    private static Iterable<Class<?>> getImplementedContracts(final Class<?> clazz) {
        final Collection<Class<?>> list = new LinkedList<Class<?>>();
        Collections.addAll(list, clazz.getInterfaces());
        final Class<?> superclass = clazz.getSuperclass();
        if (superclass != null) {
            list.add(superclass);
        }
        return list;
    }
    
    public static boolean isProvider(final Class<?> clazz) {
        return findFirstProviderContract(clazz);
    }
    
    public static void ensureContract(final Class<?> contract, final Class<?>... implementations) {
        if (implementations == null || implementations.length <= 0) {
            return;
        }
        final StringBuilder invalidClassNames = new StringBuilder();
        for (final Class<?> impl : implementations) {
            if (!contract.isAssignableFrom(impl)) {
                if (invalidClassNames.length() > 0) {
                    invalidClassNames.append(", ");
                }
                invalidClassNames.append(impl.getName());
            }
        }
        if (invalidClassNames.length() > 0) {
            throw new IllegalArgumentException(LocalizationMessages.INVALID_SPI_CLASSES(contract.getName(), invalidClassNames.toString()));
        }
    }
    
    private static boolean findFirstProviderContract(final Class<?> clazz) {
        for (final Class<?> contract : getImplementedContracts(clazz)) {
            if (isSupportedContract(contract)) {
                return true;
            }
            if (findFirstProviderContract(contract)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(Providers.class.getName());
        JAX_RS_PROVIDER_INTERFACE_WHITELIST = getJaxRsProviderInterfaces();
        EXTERNAL_PROVIDER_INTERFACE_WHITELIST = getExternalProviderInterfaces();
    }
    
    private enum ProviderRuntime
    {
        BOTH((RuntimeType)null), 
        SERVER(RuntimeType.SERVER), 
        CLIENT(RuntimeType.CLIENT);
        
        private final RuntimeType runtime;
        
        private ProviderRuntime(final RuntimeType runtime) {
            this.runtime = runtime;
        }
        
        public RuntimeType getRuntime() {
            return this.runtime;
        }
    }
}
