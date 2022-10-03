package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.internal.RankedComparator;
import javax.ws.rs.container.DynamicFeature;
import java.util.List;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ContainerRequestFilter;
import org.glassfish.jersey.model.internal.RankedProvider;
import java.lang.annotation.Annotation;
import javax.ws.rs.core.MultivaluedMap;

public class ProcessingProviders
{
    private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundRequestFilters;
    private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters;
    private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors;
    private final MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors;
    private final MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundRequestFiltersInverse;
    private final MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundResponseFiltersInverse;
    private final MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse;
    private final MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse;
    private final Iterable<RankedProvider<ContainerRequestFilter>> globalRequestFilters;
    private final Iterable<ContainerRequestFilter> sortedGlobalRequestFilters;
    private final List<RankedProvider<ContainerRequestFilter>> preMatchFilters;
    private final Iterable<RankedProvider<ContainerResponseFilter>> globalResponseFilters;
    private final Iterable<ContainerResponseFilter> sortedGlobalResponseFilters;
    private final Iterable<RankedProvider<ReaderInterceptor>> globalReaderInterceptors;
    private final Iterable<ReaderInterceptor> sortedGlobalReaderInterceptors;
    private final Iterable<RankedProvider<WriterInterceptor>> globalWriterInterceptors;
    private final Iterable<WriterInterceptor> sortedGlobalWriterInterceptors;
    private final Iterable<DynamicFeature> dynamicFeatures;
    
    public ProcessingProviders(final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> nameBoundRequestFilters, final MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> nameBoundRequestFiltersInverse, final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> nameBoundResponseFilters, final MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> nameBoundResponseFiltersInverse, final MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> nameBoundReaderInterceptors, final MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> nameBoundReaderInterceptorsInverse, final MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> nameBoundWriterInterceptors, final MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> nameBoundWriterInterceptorsInverse, final Iterable<RankedProvider<ContainerRequestFilter>> globalRequestFilters, final List<RankedProvider<ContainerRequestFilter>> preMatchFilters, final Iterable<RankedProvider<ContainerResponseFilter>> globalResponseFilters, final Iterable<RankedProvider<ReaderInterceptor>> globalReaderInterceptors, final Iterable<RankedProvider<WriterInterceptor>> globalWriterInterceptors, final Iterable<DynamicFeature> dynamicFeatures) {
        this.nameBoundReaderInterceptors = nameBoundReaderInterceptors;
        this.nameBoundReaderInterceptorsInverse = nameBoundReaderInterceptorsInverse;
        this.nameBoundRequestFilters = nameBoundRequestFilters;
        this.nameBoundRequestFiltersInverse = nameBoundRequestFiltersInverse;
        this.nameBoundResponseFilters = nameBoundResponseFilters;
        this.nameBoundResponseFiltersInverse = nameBoundResponseFiltersInverse;
        this.nameBoundWriterInterceptors = nameBoundWriterInterceptors;
        this.nameBoundWriterInterceptorsInverse = nameBoundWriterInterceptorsInverse;
        this.globalRequestFilters = globalRequestFilters;
        this.preMatchFilters = preMatchFilters;
        this.globalResponseFilters = globalResponseFilters;
        this.globalReaderInterceptors = globalReaderInterceptors;
        this.globalWriterInterceptors = globalWriterInterceptors;
        this.dynamicFeatures = dynamicFeatures;
        this.sortedGlobalReaderInterceptors = Providers.sortRankedProviders(new RankedComparator(), (Iterable)globalReaderInterceptors);
        this.sortedGlobalWriterInterceptors = Providers.sortRankedProviders(new RankedComparator(), (Iterable)globalWriterInterceptors);
        this.sortedGlobalRequestFilters = Providers.sortRankedProviders(new RankedComparator(), (Iterable)globalRequestFilters);
        this.sortedGlobalResponseFilters = Providers.sortRankedProviders(new RankedComparator(), (Iterable)globalResponseFilters);
    }
    
    public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerRequestFilter>> getNameBoundRequestFilters() {
        return this.nameBoundRequestFilters;
    }
    
    public MultivaluedMap<RankedProvider<ContainerRequestFilter>, Class<? extends Annotation>> getNameBoundRequestFiltersInverse() {
        return this.nameBoundRequestFiltersInverse;
    }
    
    public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ContainerResponseFilter>> getNameBoundResponseFilters() {
        return this.nameBoundResponseFilters;
    }
    
    public MultivaluedMap<RankedProvider<ContainerResponseFilter>, Class<? extends Annotation>> getNameBoundResponseFiltersInverse() {
        return this.nameBoundResponseFiltersInverse;
    }
    
    public MultivaluedMap<Class<? extends Annotation>, RankedProvider<ReaderInterceptor>> getNameBoundReaderInterceptors() {
        return this.nameBoundReaderInterceptors;
    }
    
    public MultivaluedMap<RankedProvider<ReaderInterceptor>, Class<? extends Annotation>> getNameBoundReaderInterceptorsInverse() {
        return this.nameBoundReaderInterceptorsInverse;
    }
    
    public MultivaluedMap<Class<? extends Annotation>, RankedProvider<WriterInterceptor>> getNameBoundWriterInterceptors() {
        return this.nameBoundWriterInterceptors;
    }
    
    public MultivaluedMap<RankedProvider<WriterInterceptor>, Class<? extends Annotation>> getNameBoundWriterInterceptorsInverse() {
        return this.nameBoundWriterInterceptorsInverse;
    }
    
    public Iterable<RankedProvider<ContainerRequestFilter>> getGlobalRequestFilters() {
        return this.globalRequestFilters;
    }
    
    public Iterable<RankedProvider<ContainerResponseFilter>> getGlobalResponseFilters() {
        return this.globalResponseFilters;
    }
    
    public Iterable<ContainerRequestFilter> getSortedGlobalRequestFilters() {
        return this.sortedGlobalRequestFilters;
    }
    
    public Iterable<ContainerResponseFilter> getSortedGlobalResponseFilters() {
        return this.sortedGlobalResponseFilters;
    }
    
    public Iterable<RankedProvider<ReaderInterceptor>> getGlobalReaderInterceptors() {
        return this.globalReaderInterceptors;
    }
    
    public Iterable<RankedProvider<WriterInterceptor>> getGlobalWriterInterceptors() {
        return this.globalWriterInterceptors;
    }
    
    public Iterable<ReaderInterceptor> getSortedGlobalReaderInterceptors() {
        return this.sortedGlobalReaderInterceptors;
    }
    
    public Iterable<WriterInterceptor> getSortedGlobalWriterInterceptors() {
        return this.sortedGlobalWriterInterceptors;
    }
    
    public Iterable<DynamicFeature> getDynamicFeatures() {
        return this.dynamicFeatures;
    }
    
    public List<RankedProvider<ContainerRequestFilter>> getPreMatchFilters() {
        return this.preMatchFilters;
    }
}
