package org.glassfish.jersey.inject.hk2;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import java.util.stream.Collector;
import org.glassfish.jersey.internal.util.collection.ImmutableCollectors;
import java.util.function.Function;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import org.glassfish.jersey.internal.Errors;
import java.security.AccessController;
import org.glassfish.hk2.api.MultiException;
import javax.inject.Inject;
import java.lang.reflect.Modifier;
import org.glassfish.jersey.internal.LocalizationMessages;
import java.lang.reflect.Constructor;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.internal.inject.InjectionResolver;
import java.util.List;
import java.util.function.Supplier;
import java.util.Set;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import javax.inject.Named;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ClassAnalyzer;

@Singleton
@Named("JerseyClassAnalyzer")
public final class JerseyClassAnalyzer implements ClassAnalyzer
{
    public static final String NAME = "JerseyClassAnalyzer";
    private final ClassAnalyzer defaultAnalyzer;
    private final LazyValue<Set<Class>> resolverAnnotations;
    
    private JerseyClassAnalyzer(final ClassAnalyzer defaultAnalyzer, final Supplier<List<InjectionResolver>> supplierResolvers) {
        this.defaultAnalyzer = defaultAnalyzer;
        final Value<Set<Class>> resolvers = (Value<Set<Class>>)(() -> supplierResolvers.get().stream().filter(InjectionResolver::isConstructorParameterIndicator).map((Function<? super Object, ?>)InjectionResolver::getAnnotation).collect((Collector<? super Object, Object, Set>)ImmutableCollectors.toImmutableSet()));
        this.resolverAnnotations = (LazyValue<Set<Class>>)Values.lazy((Value)resolvers);
    }
    
    public <T> Constructor<T> getConstructor(final Class<T> clazz) throws MultiException, NoSuchMethodException {
        if (clazz.isLocalClass()) {
            throw new NoSuchMethodException(LocalizationMessages.INJECTION_ERROR_LOCAL_CLASS_NOT_SUPPORTED((Object)clazz.getName()));
        }
        if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
            throw new NoSuchMethodException(LocalizationMessages.INJECTION_ERROR_NONSTATIC_MEMBER_CLASS_NOT_SUPPORTED((Object)clazz.getName()));
        }
        try {
            final Constructor<T> retVal = this.defaultAnalyzer.getConstructor((Class)clazz);
            final Class<?>[] args = retVal.getParameterTypes();
            if (args.length != 0) {
                return retVal;
            }
            final Inject i = retVal.getAnnotation(Inject.class);
            if (i != null) {
                return retVal;
            }
        }
        catch (final NoSuchMethodException ex) {}
        catch (final MultiException me) {
            if (me.getErrors().size() != 1 && !(me.getErrors().get(0) instanceof IllegalArgumentException)) {
                throw me;
            }
        }
        final Constructor<?>[] constructors = AccessController.doPrivileged(clazz::getDeclaredConstructors);
        Constructor<?> selected = null;
        int selectedSize = 0;
        int maxParams = -1;
        for (final Constructor<?> constructor : constructors) {
            final Class<?>[] params = constructor.getParameterTypes();
            if (params.length >= maxParams && this.isCompatible(constructor)) {
                if (params.length > maxParams) {
                    maxParams = params.length;
                    selectedSize = 0;
                }
                selected = constructor;
                ++selectedSize;
            }
        }
        if (selectedSize == 0) {
            throw new NoSuchMethodException(LocalizationMessages.INJECTION_ERROR_SUITABLE_CONSTRUCTOR_NOT_FOUND((Object)clazz.getName()));
        }
        if (selectedSize > 1) {
            Errors.warning((Object)clazz, LocalizationMessages.MULTIPLE_MATCHING_CONSTRUCTORS_FOUND((Object)selectedSize, (Object)maxParams, (Object)clazz.getName(), (Object)selected.toGenericString()));
        }
        return (Constructor<T>)selected;
    }
    
    private boolean isCompatible(final Constructor<?> constructor) {
        if (constructor.getAnnotation(Inject.class) != null) {
            return true;
        }
        final int paramSize = constructor.getParameterTypes().length;
        if (paramSize != 0 && ((Set)this.resolverAnnotations.get()).isEmpty()) {
            return false;
        }
        if (!Modifier.isPublic(constructor.getModifiers())) {
            return paramSize == 0 && (constructor.getDeclaringClass().getModifiers() & 0x7) == constructor.getModifiers();
        }
        for (final Annotation[] paramAnnotations : constructor.getParameterAnnotations()) {
            boolean found = false;
            for (final Annotation paramAnnotation : paramAnnotations) {
                if (((Set)this.resolverAnnotations.get()).contains(paramAnnotation.annotationType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    public <T> Set<Method> getInitializerMethods(final Class<T> clazz) throws MultiException {
        return this.defaultAnalyzer.getInitializerMethods((Class)clazz);
    }
    
    public <T> Set<Field> getFields(final Class<T> clazz) throws MultiException {
        return this.defaultAnalyzer.getFields((Class)clazz);
    }
    
    public <T> Method getPostConstructMethod(final Class<T> clazz) throws MultiException {
        return this.defaultAnalyzer.getPostConstructMethod((Class)clazz);
    }
    
    public <T> Method getPreDestroyMethod(final Class<T> clazz) throws MultiException {
        return this.defaultAnalyzer.getPreDestroyMethod((Class)clazz);
    }
    
    public static final class Binder extends AbstractBinder
    {
        private final ServiceLocator serviceLocator;
        
        public Binder(final ServiceLocator serviceLocator) {
            this.serviceLocator = serviceLocator;
        }
        
        protected void configure() {
            final ClassAnalyzer defaultAnalyzer = (ClassAnalyzer)this.serviceLocator.getService((Class)ClassAnalyzer.class, "default", new Annotation[0]);
            final Supplier<List<InjectionResolver>> resolvers = (Supplier<List<InjectionResolver>>)(() -> this.serviceLocator.getAllServices((Class)InjectionResolver.class, new Annotation[0]));
            this.bind((Object)new JerseyClassAnalyzer(defaultAnalyzer, resolvers, null)).analyzeWith("default").named("JerseyClassAnalyzer").to((Class)ClassAnalyzer.class);
        }
    }
}
