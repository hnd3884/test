package org.glassfish.jersey.message.filtering.spi;

import java.util.Collections;
import java.util.Collection;
import java.util.HashSet;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Set;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import org.glassfish.jersey.internal.guava.CacheBuilder;
import javax.inject.Inject;
import org.glassfish.jersey.internal.guava.Cache;

public abstract class AbstractObjectProvider<T> implements ObjectProvider<T>, ObjectGraphTransformer<T>
{
    private static final int PROVIDER_CACHE_SIZE = 1000;
    private final Cache<EntityContext, T> filteringObjects;
    @Inject
    private ScopeProvider scopeProvider;
    @Inject
    private EntityInspector entityInspector;
    @Inject
    private EntityGraphProvider graphProvider;
    
    public AbstractObjectProvider() {
        this.filteringObjects = (Cache<EntityContext, T>)CacheBuilder.newBuilder().maximumSize(1000L).build();
    }
    
    @Override
    public final T getFilteringObject(final Type genericType, final boolean forWriter, final Annotation... annotations) {
        return this.getFilteringObject(FilteringHelper.getEntityClass(genericType), forWriter, annotations);
    }
    
    private T getFilteringObject(final Class<?> entityClass, final boolean forWriter, final Annotation... annotations) {
        if (FilteringHelper.filterableEntityClass(entityClass)) {
            this.entityInspector.inspect(entityClass, forWriter);
            final Set<String> filteringScope = this.scopeProvider.getFilteringScopes(this.getEntityAnnotations(annotations), true);
            final EntityContext entityContext = new EntityContext((Class)entityClass, (Set)filteringScope);
            T filteringObject = (T)this.filteringObjects.getIfPresent((Object)entityContext);
            if (filteringObject == null) {
                filteringObject = this.createFilteringObject(entityClass, filteringScope, forWriter);
                this.filteringObjects.put((Object)entityContext, (Object)filteringObject);
            }
            return filteringObject;
        }
        return null;
    }
    
    private Annotation[] getEntityAnnotations(final Annotation[] annotations) {
        final ArrayList<Annotation> entityAnnotations = new ArrayList<Annotation>();
        for (final Annotation annotation : annotations) {
            if (!(annotation instanceof Proxy)) {
                entityAnnotations.add(annotation);
            }
        }
        return entityAnnotations.toArray(new Annotation[entityAnnotations.size()]);
    }
    
    private T createFilteringObject(final Class<?> entityClass, final Set<String> filteringScopes, final boolean forWriter) {
        return this.transform(this.graphProvider.createObjectGraph(entityClass, filteringScopes, forWriter));
    }
    
    protected Set<String> immutableSetOf(final Set<String> set, final String item) {
        final Set<String> duplicate = new HashSet<String>(set);
        duplicate.add(item);
        return Collections.unmodifiableSet((Set<? extends String>)duplicate);
    }
    
    protected String subgraphIdentifier(final Class<?> parent, final String field, final Class<?> fieldClass) {
        return parent.getName() + "_" + field + "_" + fieldClass.getName();
    }
    
    private static final class EntityContext
    {
        private final Class<?> entityClass;
        private final Set<String> filteringContext;
        
        private EntityContext(final Class<?> entityClass, final Set<String> filteringScopes) {
            this.entityClass = entityClass;
            this.filteringContext = filteringScopes;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof EntityContext)) {
                return false;
            }
            final EntityContext that = (EntityContext)o;
            return this.entityClass.equals(that.entityClass) && this.filteringContext.equals(that.filteringContext);
        }
        
        @Override
        public int hashCode() {
            int result = this.entityClass.hashCode();
            result = 47 * result + this.filteringContext.hashCode();
            return result;
        }
    }
}
