package org.glassfish.jersey.server;

import org.glassfish.jersey.model.ContractProvider;
import java.lang.reflect.Type;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import javax.ws.rs.container.PreMatching;
import java.util.Set;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import java.util.ArrayList;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.util.Collection;
import org.glassfish.jersey.internal.inject.Providers;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import javax.ws.rs.NameBinding;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.internal.BootstrapBag;
import org.glassfish.jersey.internal.inject.InjectionManager;
import java.util.logging.Logger;
import org.glassfish.jersey.internal.BootstrapConfigurator;

class ProcessingProvidersConfigurator implements BootstrapConfigurator
{
    private static final Logger LOGGER;
    
    public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
    }
    
    public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
        final ServerBootstrapBag serverBag = (ServerBootstrapBag)bootstrapBag;
        final ComponentBag componentBag = serverBag.getRuntimeConfig().getComponentBag();
        final Collection<Class<? extends Annotation>> applicationNameBindings = ReflectionHelper.getAnnotationTypes((AnnotatedElement)ResourceConfig.unwrapApplication(serverBag.getRuntimeConfig()).getClass(), (Class)NameBinding.class);
        final MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundRespFiltersInverse = (MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>>)new MultivaluedHashMap();
        final MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundReqFiltersInverse = (MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>>)new MultivaluedHashMap();
        final MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse = (MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>>)new MultivaluedHashMap();
        final MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse = (MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>>)new MultivaluedHashMap();
        final Iterable<RankedProvider<ContainerResponseFilter>> responseFilters = Providers.getAllRankedProviders(injectionManager, (Class)ContainerResponseFilter.class);
        final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters = filterNameBound(responseFilters, null, componentBag, applicationNameBindings, nameBoundRespFiltersInverse);
        final Iterable<RankedProvider<ContainerRequestFilter>> requestFilters = Providers.getAllRankedProviders(injectionManager, (Class)ContainerRequestFilter.class);
        final List<RankedProvider<ContainerRequestFilter>> preMatchFilters = new ArrayList<RankedProvider<ContainerRequestFilter>>();
        final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundReqFilters = filterNameBound(requestFilters, preMatchFilters, componentBag, applicationNameBindings, nameBoundReqFiltersInverse);
        final Iterable<RankedProvider<ReaderInterceptor>> readerInterceptors = Providers.getAllRankedProviders(injectionManager, (Class)ReaderInterceptor.class);
        final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors = filterNameBound(readerInterceptors, null, componentBag, applicationNameBindings, nameBoundReaderInterceptorsInverse);
        final Iterable<RankedProvider<WriterInterceptor>> writerInterceptors = Providers.getAllRankedProviders(injectionManager, (Class)WriterInterceptor.class);
        final MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors = filterNameBound(writerInterceptors, null, componentBag, applicationNameBindings, nameBoundWriterInterceptorsInverse);
        final Iterable<DynamicFeature> dynamicFeatures = Providers.getAllProviders(injectionManager, (Class)DynamicFeature.class);
        final ProcessingProviders processingProviders = new ProcessingProviders(nameBoundReqFilters, nameBoundReqFiltersInverse, nameBoundResponseFilters, nameBoundRespFiltersInverse, nameBoundReaderInterceptors, nameBoundReaderInterceptorsInverse, nameBoundWriterInterceptors, nameBoundWriterInterceptorsInverse, requestFilters, preMatchFilters, responseFilters, readerInterceptors, writerInterceptors, dynamicFeatures);
        serverBag.setProcessingProviders(processingProviders);
    }
    
    private static <T> MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>> filterNameBound(final Iterable<RankedProvider<T>> all, final Collection<RankedProvider<ContainerRequestFilter>> preMatchingFilters, final ComponentBag componentBag, final Collection<Class<? extends Annotation>> applicationNameBindings, final MultivaluedMap<RankedProvider<T>, Class<? extends Annotation>> inverseNameBoundMap) {
        final MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>> result = (MultivaluedMap<Class<? extends Annotation>, RankedProvider<T>>)new MultivaluedHashMap();
        final Iterator<RankedProvider<T>> it = all.iterator();
        while (it.hasNext()) {
            final RankedProvider<T> provider = it.next();
            Class<?> providerClass = provider.getProvider().getClass();
            final Set<Type> contractTypes = provider.getContractTypes();
            if (contractTypes != null && !contractTypes.contains(providerClass)) {
                providerClass = ReflectionHelper.theMostSpecificTypeOf((Set)contractTypes);
            }
            ContractProvider model = componentBag.getModel((Class)providerClass);
            if (model == null) {
                model = ComponentBag.modelFor((Class)providerClass);
            }
            final boolean preMatching = providerClass.getAnnotation(PreMatching.class) != null;
            if (preMatching && preMatchingFilters != null) {
                it.remove();
                preMatchingFilters.add((RankedProvider<ContainerRequestFilter>)new RankedProvider((Object)provider.getProvider(), model.getPriority((Class)ContainerRequestFilter.class)));
            }
            boolean nameBound = model.isNameBound();
            if (nameBound && !applicationNameBindings.isEmpty() && applicationNameBindings.containsAll(model.getNameBindings())) {
                nameBound = false;
            }
            if (nameBound) {
                if (!preMatching) {
                    it.remove();
                    for (final Class<? extends Annotation> binding : model.getNameBindings()) {
                        result.add((Object)binding, (Object)provider);
                        inverseNameBoundMap.add((Object)provider, (Object)binding);
                    }
                }
                else {
                    ProcessingProvidersConfigurator.LOGGER.warning(LocalizationMessages.PREMATCHING_ALSO_NAME_BOUND(providerClass));
                }
            }
        }
        return result;
    }
    
    static {
        LOGGER = Logger.getLogger(ProcessingProvidersConfigurator.class.getName());
    }
}
