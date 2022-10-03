package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.util.Iterator;
import org.glassfish.jersey.internal.util.collection.KeyComparatorHashMap;
import org.glassfish.jersey.message.internal.MessageBodyFactory;
import java.util.ArrayList;
import org.glassfish.jersey.message.internal.MediaTypes;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Type;
import java.util.Map;
import org.glassfish.jersey.spi.ContextResolvers;

public class ContextResolverFactory implements ContextResolvers
{
    private final Map<Type, Map<MediaType, ContextResolver>> resolver;
    private final Map<Type, ConcurrentHashMap<MediaType, ContextResolver>> cache;
    private static final NullContextResolverAdapter NULL_CONTEXT_RESOLVER;
    
    private ContextResolverFactory() {
        this.resolver = new HashMap<Type, Map<MediaType, ContextResolver>>(3);
        this.cache = new HashMap<Type, ConcurrentHashMap<MediaType, ContextResolver>>(3);
    }
    
    private void initialize(final List<ContextResolver> contextResolvers) {
        final Map<Type, Map<MediaType, List<ContextResolver>>> rs = new HashMap<Type, Map<MediaType, List<ContextResolver>>>();
        for (final ContextResolver provider : contextResolvers) {
            final List<MediaType> ms = MediaTypes.createFrom(provider.getClass().getAnnotation(Produces.class));
            final Type type = this.getParameterizedType(provider.getClass());
            Map<MediaType, List<ContextResolver>> mr = rs.get(type);
            if (mr == null) {
                mr = new HashMap<MediaType, List<ContextResolver>>();
                rs.put(type, mr);
            }
            for (final MediaType m : ms) {
                List<ContextResolver> crl = mr.get(m);
                if (crl == null) {
                    crl = new ArrayList<ContextResolver>();
                    mr.put(m, crl);
                }
                crl.add(provider);
            }
        }
        for (final Map.Entry<Type, Map<MediaType, List<ContextResolver>>> e : rs.entrySet()) {
            final Map<MediaType, ContextResolver> mr2 = new KeyComparatorHashMap<MediaType, ContextResolver>(4, MessageBodyFactory.MEDIA_TYPE_KEY_COMPARATOR);
            this.resolver.put(e.getKey(), mr2);
            this.cache.put(e.getKey(), new ConcurrentHashMap<MediaType, ContextResolver>(4));
            for (final Map.Entry<MediaType, List<ContextResolver>> f : e.getValue().entrySet()) {
                mr2.put(f.getKey(), this.reduce(f.getValue()));
            }
        }
    }
    
    private Type getParameterizedType(final Class<?> c) {
        final ReflectionHelper.DeclaringClassInterfacePair p = ReflectionHelper.getClass(c, ContextResolver.class);
        final Type[] as = ReflectionHelper.getParameterizedTypeArguments(p);
        return (as != null) ? as[0] : Object.class;
    }
    
    private ContextResolver reduce(final List<ContextResolver> r) {
        if (r.size() == 1) {
            return r.iterator().next();
        }
        return (ContextResolver)new ContextResolverAdapter(r);
    }
    
    @Override
    public <T> ContextResolver<T> resolve(final Type t, MediaType m) {
        final ConcurrentHashMap<MediaType, ContextResolver> crMapCache = this.cache.get(t);
        if (crMapCache == null) {
            return null;
        }
        if (m == null) {
            m = MediaType.WILDCARD_TYPE;
        }
        ContextResolver<T> cr = (ContextResolver<T>)crMapCache.get(m);
        if (cr == null) {
            final Map<MediaType, ContextResolver> crMap = this.resolver.get(t);
            if (m.isWildcardType()) {
                cr = (ContextResolver<T>)crMap.get(MediaType.WILDCARD_TYPE);
                if (cr == null) {
                    cr = (ContextResolver<T>)ContextResolverFactory.NULL_CONTEXT_RESOLVER;
                }
            }
            else if (m.isWildcardSubtype()) {
                final ContextResolver<T> subTypeWildCard = (ContextResolver<T>)crMap.get(m);
                final ContextResolver<T> wildCard = (ContextResolver<T>)crMap.get(MediaType.WILDCARD_TYPE);
                cr = (ContextResolver<T>)new ContextResolverAdapter(new ContextResolver[] { subTypeWildCard, wildCard }).reduce();
            }
            else {
                final ContextResolver<T> type = (ContextResolver<T>)crMap.get(m);
                final ContextResolver<T> subTypeWildCard2 = (ContextResolver<T>)crMap.get(new MediaType(m.getType(), "*"));
                final ContextResolver<T> wildCard2 = (ContextResolver<T>)crMap.get(MediaType.WILDCARD_TYPE);
                cr = (ContextResolver<T>)new ContextResolverAdapter(new ContextResolver[] { type, subTypeWildCard2, wildCard2 }).reduce();
            }
            final ContextResolver<T> _cr = crMapCache.putIfAbsent(m, cr);
            if (_cr != null) {
                cr = _cr;
            }
        }
        return (cr != ContextResolverFactory.NULL_CONTEXT_RESOLVER) ? cr : null;
    }
    
    static {
        NULL_CONTEXT_RESOLVER = new NullContextResolverAdapter();
    }
    
    public static class ContextResolversConfigurator implements BootstrapConfigurator
    {
        private ContextResolverFactory contextResolverFactory;
        
        @Override
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            this.contextResolverFactory = new ContextResolverFactory(null);
            final InstanceBinding<ContextResolverFactory> binding = Bindings.service(this.contextResolverFactory).to((Class<? super Object>)ContextResolvers.class);
            injectionManager.register(binding);
        }
        
        @Override
        public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            this.contextResolverFactory.initialize(injectionManager.getAllInstances(ContextResolver.class));
            bootstrapBag.setContextResolvers(this.contextResolverFactory);
        }
    }
    
    private static final class NullContextResolverAdapter implements ContextResolver
    {
        public Object getContext(final Class type) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    private static final class ContextResolverAdapter implements ContextResolver
    {
        private final ContextResolver[] cra;
        
        ContextResolverAdapter(final ContextResolver... cra) {
            this(removeNull(cra));
        }
        
        ContextResolverAdapter(final List<ContextResolver> crl) {
            this.cra = crl.toArray(new ContextResolver[crl.size()]);
        }
        
        public Object getContext(final Class objectType) {
            for (final ContextResolver cr : this.cra) {
                final Object c = cr.getContext(objectType);
                if (c != null) {
                    return c;
                }
            }
            return null;
        }
        
        ContextResolver reduce() {
            if (this.cra.length == 0) {
                return (ContextResolver)ContextResolverFactory.NULL_CONTEXT_RESOLVER;
            }
            if (this.cra.length == 1) {
                return this.cra[0];
            }
            return (ContextResolver)this;
        }
        
        private static List<ContextResolver> removeNull(final ContextResolver... cra) {
            final List<ContextResolver> crl = new ArrayList<ContextResolver>(cra.length);
            for (final ContextResolver cr : cra) {
                if (cr != null) {
                    crl.add(cr);
                }
            }
            return crl;
        }
    }
}
