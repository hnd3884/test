package org.glassfish.jersey.internal;

import org.glassfish.jersey.internal.inject.Binding;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import java.util.SortedSet;
import org.glassfish.jersey.internal.util.collection.ClassTypePair;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.ParameterizedType;
import javax.ws.rs.ProcessingException;
import java.util.logging.Level;
import java.util.Collection;
import org.glassfish.jersey.internal.util.collection.Values;
import java.lang.reflect.Type;
import java.util.TreeSet;
import java.lang.reflect.Proxy;
import org.glassfish.jersey.internal.inject.ServiceHolder;
import java.util.LinkedHashSet;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;
import java.util.Iterator;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Set;
import org.glassfish.jersey.internal.util.collection.Value;
import java.util.logging.Logger;
import org.glassfish.jersey.spi.ExceptionMappers;

public class ExceptionMapperFactory implements ExceptionMappers
{
    private static final Logger LOGGER;
    private final Value<Set<ExceptionMapperType>> exceptionMapperTypes;
    
    @Override
    public <T extends Throwable> ExceptionMapper<T> findMapping(final T exceptionInstance) {
        return this.find((Class<T>)exceptionInstance.getClass(), exceptionInstance);
    }
    
    @Override
    public <T extends Throwable> ExceptionMapper<T> find(final Class<T> type) {
        return this.find(type, (T)null);
    }
    
    private <T extends Throwable> ExceptionMapper<T> find(final Class<T> type, final T exceptionInstance) {
        ExceptionMapper<T> mapper = null;
        int minDistance = Integer.MAX_VALUE;
        for (final ExceptionMapperType mapperType : this.exceptionMapperTypes.get()) {
            final int d = this.distance(type, mapperType.exceptionType);
            if (d >= 0 && d <= minDistance) {
                final ExceptionMapper<T> candidate = (ExceptionMapper<T>)mapperType.mapper.getInstance();
                if (!this.isPreferredCandidate(exceptionInstance, candidate, d == minDistance)) {
                    continue;
                }
                mapper = candidate;
                if ((minDistance = d) == 0) {
                    return mapper;
                }
                continue;
            }
        }
        return mapper;
    }
    
    private <T extends Throwable> boolean isPreferredCandidate(final T exceptionInstance, final ExceptionMapper<T> candidate, final boolean sameDistance) {
        if (exceptionInstance == null) {
            return true;
        }
        if (candidate instanceof ExtendedExceptionMapper) {
            return !sameDistance && ((ExtendedExceptionMapper)candidate).isMappable(exceptionInstance);
        }
        return !sameDistance;
    }
    
    public ExceptionMapperFactory(final InjectionManager injectionManager) {
        this.exceptionMapperTypes = this.createLazyExceptionMappers(injectionManager);
    }
    
    private LazyValue<Set<ExceptionMapperType>> createLazyExceptionMappers(final InjectionManager injectionManager) {
        return Values.lazy(() -> {
            final Collection<ServiceHolder<ExceptionMapper>> mapperHandles = Providers.getAllServiceHolders(injectionManager, ExceptionMapper.class);
            final LinkedHashSet<ExceptionMapperType> exceptionMapperTypes = new LinkedHashSet<ExceptionMapperType>();
            mapperHandles.iterator();
            final Iterator iterator;
            while (iterator.hasNext()) {
                final ServiceHolder<ExceptionMapper> mapperHandle = iterator.next();
                final ExceptionMapper mapper = mapperHandle.getInstance();
                if (Proxy.isProxyClass(mapper.getClass())) {
                    final TreeSet<Class<? extends ExceptionMapper>> mapperTypes = (TreeSet<Class<? extends ExceptionMapper>>)new TreeSet<Object>((o1, o2) -> o1.isAssignableFrom(o2) ? -1 : 1);
                    final Set contracts = mapperHandle.getContractTypes();
                    contracts.iterator();
                    final Iterator iterator2;
                    while (iterator2.hasNext()) {
                        final Type contract = iterator2.next();
                        if (contract instanceof Class && ExceptionMapper.class.isAssignableFrom((Class<?>)contract) && contract != ExceptionMapper.class) {
                            mapperTypes.add((Class)contract);
                        }
                    }
                    if (!mapperTypes.isEmpty()) {
                        final Class<? extends Throwable> c = this.getExceptionType(mapperTypes.first());
                        if (c != null) {
                            exceptionMapperTypes.add(new ExceptionMapperType(mapperHandle, c));
                        }
                        else {
                            continue;
                        }
                    }
                    else {
                        continue;
                    }
                }
                else {
                    final Class<? extends Throwable> c2 = this.getExceptionType(mapper.getClass());
                    if (c2 != null) {
                        exceptionMapperTypes.add(new ExceptionMapperType(mapperHandle, c2));
                    }
                    else {
                        continue;
                    }
                }
            }
            return exceptionMapperTypes;
        });
    }
    
    private int distance(Class<?> c, final Class<?> emtc) {
        int distance = 0;
        if (!emtc.isAssignableFrom(c)) {
            return -1;
        }
        while (c != emtc) {
            c = c.getSuperclass();
            ++distance;
        }
        return distance;
    }
    
    private Class<? extends Throwable> getExceptionType(final Class<? extends ExceptionMapper> c) {
        final Class<?> t = this.getType(c);
        if (Throwable.class.isAssignableFrom(t)) {
            return (Class<? extends Throwable>)t;
        }
        if (ExceptionMapperFactory.LOGGER.isLoggable(Level.WARNING)) {
            ExceptionMapperFactory.LOGGER.warning(LocalizationMessages.EXCEPTION_MAPPER_SUPPORTED_TYPE_UNKNOWN(c.getName()));
        }
        return null;
    }
    
    private Class getType(final Class<? extends ExceptionMapper> clazz) {
        for (Class clazzHolder = clazz; clazzHolder != Object.class; clazzHolder = clazzHolder.getSuperclass()) {
            final Class type = this.getTypeFromInterface(clazzHolder, clazz);
            if (type != null) {
                return type;
            }
        }
        throw new ProcessingException(LocalizationMessages.ERROR_FINDING_EXCEPTION_MAPPER_TYPE(clazz));
    }
    
    private Class getTypeFromInterface(Class<?> clazz, final Class<? extends ExceptionMapper> original) {
        final Type[] genericInterfaces;
        final Type[] types = genericInterfaces = clazz.getGenericInterfaces();
        for (final Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)type;
                if (pt.getRawType() == ExceptionMapper.class || pt.getRawType() == ExtendedExceptionMapper.class) {
                    return this.getResolvedType(pt.getActualTypeArguments()[0], original, clazz);
                }
            }
            else if (type instanceof Class) {
                clazz = (Class)type;
                if (ExceptionMapper.class.isAssignableFrom(clazz)) {
                    return this.getTypeFromInterface(clazz, original);
                }
            }
        }
        return null;
    }
    
    private Class getResolvedType(final Type t, final Class c, final Class dc) {
        if (t instanceof Class) {
            return (Class)t;
        }
        if (t instanceof TypeVariable) {
            final ClassTypePair ct = ReflectionHelper.resolveTypeVariable(c, dc, (TypeVariable)t);
            if (ct != null) {
                return ct.rawClass();
            }
            return null;
        }
        else {
            if (t instanceof ParameterizedType) {
                final ParameterizedType pt = (ParameterizedType)t;
                return (Class)pt.getRawType();
            }
            return null;
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ExceptionMapperFactory.class.getName());
    }
    
    public static class ExceptionMappersConfigurator implements BootstrapConfigurator
    {
        private ExceptionMapperFactory exceptionMapperFactory;
        
        @Override
        public void init(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            this.exceptionMapperFactory = new ExceptionMapperFactory(injectionManager);
            final InstanceBinding<ExceptionMapperFactory> binding = Bindings.service(this.exceptionMapperFactory).to((Class<? super Object>)ExceptionMappers.class);
            injectionManager.register(binding);
        }
        
        @Override
        public void postInit(final InjectionManager injectionManager, final BootstrapBag bootstrapBag) {
            bootstrapBag.setExceptionMappers(this.exceptionMapperFactory);
        }
    }
    
    private static class ExceptionMapperType
    {
        ServiceHolder<ExceptionMapper> mapper;
        Class<? extends Throwable> exceptionType;
        
        public ExceptionMapperType(final ServiceHolder<ExceptionMapper> mapper, final Class<? extends Throwable> exceptionType) {
            this.mapper = mapper;
            this.exceptionType = exceptionType;
        }
    }
}
