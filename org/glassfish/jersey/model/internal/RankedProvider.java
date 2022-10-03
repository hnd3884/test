package org.glassfish.jersey.model.internal;

import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import java.lang.reflect.Type;
import java.util.Set;

public class RankedProvider<T>
{
    private final T provider;
    private final int rank;
    private final Set<Type> contractTypes;
    
    public RankedProvider(final T provider) {
        this.provider = provider;
        this.rank = this.computeRank(provider, -1);
        this.contractTypes = null;
    }
    
    public RankedProvider(final T provider, final int rank) {
        this(provider, rank, null);
    }
    
    public RankedProvider(final T provider, final int rank, final Set<Type> contracts) {
        this.provider = provider;
        this.rank = this.computeRank(provider, rank);
        this.contractTypes = contracts;
    }
    
    private int computeRank(final T provider, final int rank) {
        if (rank > 0) {
            return rank;
        }
        Class<?> clazz;
        for (clazz = provider.getClass(); clazz.isSynthetic(); clazz = clazz.getSuperclass()) {}
        if (clazz.isAnnotationPresent((Class<? extends Annotation>)Priority.class)) {
            return clazz.getAnnotation(Priority.class).value();
        }
        return 5000;
    }
    
    public T getProvider() {
        return this.provider;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public Set<Type> getContractTypes() {
        return this.contractTypes;
    }
    
    @Override
    public String toString() {
        return this.provider.getClass().getName();
    }
}
