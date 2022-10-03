package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.ValidationService;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import org.glassfish.hk2.utilities.reflection.TypeChecker;
import org.glassfish.hk2.api.Filter;
import java.util.Collection;
import org.glassfish.hk2.api.InterceptionService;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.LinkedHashMap;
import org.glassfish.hk2.api.Context;
import java.util.ArrayList;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.api.Self;
import org.jvnet.hk2.annotations.Optional;
import javax.inject.Named;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import java.lang.annotation.Inherited;
import java.lang.reflect.Member;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import org.glassfish.hk2.utilities.reflection.MethodWrapper;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import javax.inject.Singleton;
import org.glassfish.hk2.utilities.NamedImpl;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import javax.inject.Inject;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Unproxiable;
import org.glassfish.hk2.api.Proxiable;
import org.glassfish.hk2.api.ProxyCtl;
import org.glassfish.hk2.api.MethodParameter;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.reflection.ScopeInfo;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.ProxyForSameScope;
import org.glassfish.hk2.api.UseProxy;
import org.glassfish.hk2.utilities.BuilderHelper;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.annotations.ContractsProvided;
import java.util.LinkedHashSet;
import org.glassfish.hk2.api.ContractIndicator;
import org.jvnet.hk2.annotations.Contract;
import org.glassfish.hk2.utilities.reflection.Pretty;
import java.lang.reflect.WildcardType;
import org.glassfish.hk2.api.Factory;
import java.lang.reflect.ParameterizedType;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.lang.reflect.Type;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.HK2Loader;
import java.util.Iterator;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.ErrorService;
import java.util.LinkedList;
import org.glassfish.hk2.utilities.reflection.Constants;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import javax.inject.Scope;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.lang.reflect.Method;
import java.util.Set;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.glassfish.hk2.api.MultiException;
import java.lang.reflect.Constructor;
import org.glassfish.hk2.api.ClassAnalyzer;
import java.util.HashSet;

public class Utilities
{
    private static final String USE_SOFT_REFERENCE_PROPERTY = "org.jvnet.hk2.properties.useSoftReference";
    static final boolean USE_SOFT_REFERENCE;
    private static final AnnotationInformation DEFAULT_ANNOTATION_INFORMATION;
    private static final String PROVIDE_METHOD = "provide";
    private static final HashSet<String> NOT_INTERCEPTED;
    private static final Interceptors EMTPY_INTERCEPTORS;
    private static Boolean proxiesAvailable;
    
    public static ClassAnalyzer getClassAnalyzer(final ServiceLocatorImpl sli, final String analyzerName, final Collector errorCollector) {
        return sli.getAnalyzer(analyzerName, errorCollector);
    }
    
    public static <T> Constructor<T> getConstructor(final Class<T> implClass, final ClassAnalyzer analyzer, final Collector collector) {
        Constructor<T> element = null;
        try {
            element = analyzer.getConstructor((Class)implClass);
        }
        catch (final MultiException me) {
            collector.addMultiException(me);
            return element;
        }
        catch (final Throwable th) {
            collector.addThrowable(th);
            return element;
        }
        if (element == null) {
            collector.addThrowable(new AssertionError((Object)("null return from getConstructor method of analyzer " + analyzer + " for class " + implClass.getName())));
            return element;
        }
        final Constructor<T> result = element;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                result.setAccessible(true);
                return null;
            }
        });
        return element;
    }
    
    public static Set<Method> getInitMethods(final Class<?> implClass, final ClassAnalyzer analyzer, final Collector collector) {
        Set<Method> retVal;
        try {
            retVal = analyzer.getInitializerMethods((Class)implClass);
        }
        catch (final MultiException me) {
            collector.addMultiException(me);
            return Collections.emptySet();
        }
        catch (final Throwable th) {
            collector.addThrowable(th);
            return Collections.emptySet();
        }
        if (retVal == null) {
            collector.addThrowable(new AssertionError((Object)("null return from getInitializerMethods method of analyzer " + analyzer + " for class " + implClass.getName())));
            return Collections.emptySet();
        }
        return retVal;
    }
    
    public static Set<Field> getInitFields(final Class<?> implClass, final ClassAnalyzer analyzer, final Collector collector) {
        Set<Field> retVal;
        try {
            retVal = analyzer.getFields((Class)implClass);
        }
        catch (final MultiException me) {
            collector.addMultiException(me);
            return Collections.emptySet();
        }
        catch (final Throwable th) {
            collector.addThrowable(th);
            return Collections.emptySet();
        }
        if (retVal == null) {
            collector.addThrowable(new AssertionError((Object)("null return from getFields method of analyzer " + analyzer + " for class " + implClass.getName())));
            return Collections.emptySet();
        }
        return retVal;
    }
    
    public static Method getPostConstruct(final Class<?> implClass, final ClassAnalyzer analyzer, final Collector collector) {
        try {
            return analyzer.getPostConstructMethod((Class)implClass);
        }
        catch (final MultiException me) {
            collector.addMultiException(me);
            return null;
        }
        catch (final Throwable th) {
            collector.addThrowable(th);
            return null;
        }
    }
    
    public static Method getPreDestroy(final Class<?> implClass, final ClassAnalyzer analyzer, final Collector collector) {
        try {
            return analyzer.getPreDestroyMethod((Class)implClass);
        }
        catch (final MultiException me) {
            collector.addMultiException(me);
            return null;
        }
        catch (final Throwable th) {
            collector.addThrowable(th);
            return null;
        }
    }
    
    public static Class<?> getFactoryAwareImplementationClass(final ActiveDescriptor<?> descriptor) {
        if (descriptor.getDescriptorType().equals((Object)DescriptorType.CLASS)) {
            return descriptor.getImplementationClass();
        }
        return getFactoryProductionClass(descriptor);
    }
    
    public static void checkLookupType(final Class<?> checkMe) {
        if (!checkMe.isAnnotation()) {
            return;
        }
        if (checkMe.isAnnotationPresent((Class<? extends Annotation>)Scope.class)) {
            return;
        }
        if (checkMe.isAnnotationPresent((Class<? extends Annotation>)Qualifier.class)) {
            return;
        }
        throw new IllegalArgumentException("Lookup type " + checkMe + " must be a scope or annotation");
    }
    
    public static Class<?> translatePrimitiveType(final Class<?> type) {
        final Class<?> translation = Constants.PRIMITIVE_MAP.get(type);
        if (translation == null) {
            return type;
        }
        return translation;
    }
    
    public static void handleErrors(final NarrowResults results, final LinkedList<ErrorService> callThese) {
        final Collector collector = new Collector();
        for (final ErrorResults errorResult : results.getErrors()) {
            for (final ErrorService eService : callThese) {
                try {
                    eService.onFailure((ErrorInformation)new ErrorInformationImpl(ErrorType.FAILURE_TO_REIFY, (Descriptor)errorResult.getDescriptor(), errorResult.getInjectee(), errorResult.getMe()));
                }
                catch (final MultiException me) {
                    for (final Throwable th : me.getErrors()) {
                        collector.addThrowable(th);
                    }
                }
                catch (final Throwable th2) {
                    collector.addThrowable(th2);
                }
            }
        }
        collector.throwIfErrors();
    }
    
    public static Class<?> loadClass(final String loadMe, final Descriptor fromMe, final Collector collector) {
        final HK2Loader loader = fromMe.getLoader();
        if (loader == null) {
            ClassLoader cl = Utilities.class.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }
            try {
                return cl.loadClass(loadMe);
            }
            catch (final Throwable th) {
                collector.addThrowable(th);
                return null;
            }
        }
        try {
            return loader.loadClass(loadMe);
        }
        catch (final Throwable th2) {
            if (th2 instanceof MultiException) {
                final MultiException me = (MultiException)th2;
                for (final Throwable th3 : me.getErrors()) {
                    collector.addThrowable(th3);
                }
            }
            else {
                collector.addThrowable(th2);
            }
            return null;
        }
    }
    
    public static Class<?> loadClass(final String implementation, final Injectee injectee) {
        ClassLoader loader;
        if (injectee != null) {
            final AnnotatedElement parent = injectee.getParent();
            if (parent instanceof Constructor) {
                loader = ((Constructor)parent).getDeclaringClass().getClassLoader();
            }
            else if (parent instanceof Method) {
                loader = ((Method)parent).getDeclaringClass().getClassLoader();
            }
            else if (parent instanceof Field) {
                loader = ((Field)parent).getDeclaringClass().getClassLoader();
            }
            else {
                loader = injectee.getClass().getClassLoader();
            }
        }
        else {
            loader = Utilities.class.getClassLoader();
        }
        try {
            return loader.loadClass(implementation);
        }
        catch (final Throwable th) {
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            if (ccl != null) {
                try {
                    return ccl.loadClass(implementation);
                }
                catch (final Throwable th2) {
                    final MultiException me = new MultiException(th);
                    me.addError(th2);
                    throw me;
                }
            }
            throw new MultiException(th);
        }
    }
    
    public static Class<? extends Annotation> getInjectionResolverType(final ActiveDescriptor<?> desc) {
        for (final Type advertisedType : desc.getContractTypes()) {
            final Class<?> rawClass = ReflectionHelper.getRawClass(advertisedType);
            if (!InjectionResolver.class.equals(rawClass)) {
                continue;
            }
            if (!(advertisedType instanceof ParameterizedType)) {
                return null;
            }
            final Type firstType = ReflectionHelper.getFirstTypeArgument(advertisedType);
            if (!(firstType instanceof Class)) {
                return null;
            }
            final Class<?> retVal = (Class<?>)firstType;
            if (!Annotation.class.isAssignableFrom(retVal)) {
                return null;
            }
            return (Class<? extends Annotation>)retVal;
        }
        return null;
    }
    
    private static Class<?> getFactoryProductionClass(final ActiveDescriptor<?> descriptor) {
        final Class<?> factoryClass = descriptor.getImplementationClass();
        final Type factoryProvidedType = getFactoryProductionType(factoryClass);
        Class<?> retVal = ReflectionHelper.getRawClass(factoryProvidedType);
        if (retVal == null && descriptor.getContractTypes().size() == 1) {
            final Type contract = descriptor.getContractTypes().iterator().next();
            retVal = ReflectionHelper.getRawClass(contract);
        }
        if (retVal == null) {
            throw new MultiException((Throwable)new AssertionError((Object)("Could not find true produced type of factory " + factoryClass.getName())));
        }
        return retVal;
    }
    
    public static Type getFactoryProductionType(final Class<?> factoryClass) {
        final Set<Type> factoryTypes = ReflectionHelper.getTypeClosure((Type)factoryClass, (Set)Collections.singleton(Factory.class.getName()));
        final ParameterizedType parameterizedType = factoryTypes.iterator().next();
        final Type factoryProvidedType = parameterizedType.getActualTypeArguments()[0];
        return factoryProvidedType;
    }
    
    public static void checkFactoryType(final Class<?> factoryClass, final Collector collector) {
        for (final Type type : factoryClass.getGenericInterfaces()) {
            final Class<?> rawClass = ReflectionHelper.getRawClass(type);
            if (rawClass != null) {
                if (Factory.class.equals(rawClass)) {
                    final Type firstType = ReflectionHelper.getFirstTypeArgument(type);
                    if (firstType instanceof WildcardType) {
                        collector.addThrowable(new IllegalArgumentException("The class " + Pretty.clazz((Class)factoryClass) + " has a Wildcard as its type"));
                    }
                }
            }
        }
    }
    
    private static boolean hasContract(final Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isAnnotationPresent((Class<? extends Annotation>)Contract.class)) {
            return true;
        }
        for (final Annotation clazzAnnotation : clazz.getAnnotations()) {
            if (clazzAnnotation.annotationType().isAnnotationPresent((Class<? extends Annotation>)ContractIndicator.class)) {
                return true;
            }
        }
        return false;
    }
    
    private static Set<Type> getAutoAdvertisedTypes(final Type t) {
        final LinkedHashSet<Type> retVal = new LinkedHashSet<Type>();
        retVal.add(t);
        final Class<?> rawClass = ReflectionHelper.getRawClass(t);
        if (rawClass == null) {
            return retVal;
        }
        final ContractsProvided provided = rawClass.getAnnotation(ContractsProvided.class);
        if (provided != null) {
            retVal.clear();
            for (final Class<?> providedContract : provided.value()) {
                retVal.add(providedContract);
            }
            return retVal;
        }
        final Set<Type> allTypes = ReflectionHelper.getAllTypes(t);
        for (final Type candidate : allTypes) {
            if (hasContract(ReflectionHelper.getRawClass(candidate))) {
                retVal.add(candidate);
            }
        }
        return retVal;
    }
    
    public static <T> AutoActiveDescriptor<T> createAutoDescriptor(final Class<T> clazz, final ServiceLocatorImpl locator) throws MultiException, IllegalArgumentException, IllegalStateException {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        Boolean proxy = null;
        Boolean proxyForSameScope = null;
        String serviceMetadata = null;
        String serviceName = null;
        final Service serviceAnno = clazz.getAnnotation(Service.class);
        if (serviceAnno != null) {
            if (!"".equals(serviceAnno.name())) {
                serviceName = serviceAnno.name();
            }
            if (!"".equals(serviceAnno.metadata())) {
                serviceMetadata = serviceAnno.metadata();
            }
        }
        Set<Annotation> qualifiers = ReflectionHelper.getQualifierAnnotations((AnnotatedElement)clazz);
        String name = ReflectionHelper.getNameFromAllQualifiers((Set)qualifiers, (AnnotatedElement)clazz);
        if (serviceName != null && name != null) {
            if (!serviceName.equals(name)) {
                throw new IllegalArgumentException("The class " + clazz.getName() + " has an @Service name of " + serviceName + " and has an @Named value of " + name + " which names do not match");
            }
        }
        else if (name == null && serviceName != null) {
            name = serviceName;
        }
        qualifiers = getAllQualifiers(clazz, name, collector);
        final Set<Type> contracts = getAutoAdvertisedTypes(clazz);
        final ScopeInfo scopeInfo = getScopeInfo(clazz, null, collector);
        final Class<? extends Annotation> scope = scopeInfo.getAnnoType();
        final String analyzerName = locator.getPerLocatorUtilities().getAutoAnalyzerName(clazz);
        final ClazzCreator<T> creator = new ClazzCreator<T>(locator, clazz);
        final Map<String, List<String>> metadata = new HashMap<String, List<String>>();
        if (serviceMetadata != null) {
            try {
                ReflectionHelper.readMetadataMap(serviceMetadata, (Map)metadata);
            }
            catch (final IOException ioe) {
                metadata.clear();
                ReflectionHelper.parseServiceMetadataString(serviceMetadata, (Map)metadata);
            }
        }
        collector.throwIfErrors();
        if (scopeInfo.getScope() != null) {
            BuilderHelper.getMetadataValues(scopeInfo.getScope(), (Map)metadata);
        }
        for (final Annotation qualifier : qualifiers) {
            BuilderHelper.getMetadataValues(qualifier, (Map)metadata);
        }
        final UseProxy useProxy = clazz.getAnnotation(UseProxy.class);
        if (useProxy != null) {
            proxy = useProxy.value();
        }
        final ProxyForSameScope pfss = clazz.getAnnotation(ProxyForSameScope.class);
        if (pfss != null) {
            proxyForSameScope = pfss.value();
        }
        DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
        final Visibility vi = clazz.getAnnotation(Visibility.class);
        if (vi != null) {
            visibility = vi.value();
        }
        final int rank = BuilderHelper.getRank((Class)clazz);
        final AutoActiveDescriptor<T> retVal = new AutoActiveDescriptor<T>(clazz, creator, contracts, scope, name, qualifiers, visibility, rank, proxy, proxyForSameScope, analyzerName, metadata, DescriptorType.CLASS, clazz);
        retVal.setScopeAsAnnotation(scopeInfo.getScope());
        creator.initialize((ActiveDescriptor<?>)retVal, analyzerName, collector);
        collector.throwIfErrors();
        return retVal;
    }
    
    public static <T> AutoActiveDescriptor<T> createAutoFactoryDescriptor(final Class<T> parentClazz, final ActiveDescriptor<?> factoryDescriptor, final ServiceLocatorImpl locator) throws MultiException, IllegalArgumentException, IllegalStateException {
        if (parentClazz == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        final Type factoryProductionType = getFactoryProductionType(parentClazz);
        final Method provideMethod = getFactoryProvideMethod(parentClazz);
        if (provideMethod == null) {
            collector.addThrowable(new IllegalArgumentException("Could not find the provide method on the class " + parentClazz.getName()));
            collector.throwIfErrors();
        }
        Boolean proxy = null;
        Boolean proxyForSameScope = null;
        final Set<Annotation> qualifiers = ReflectionHelper.getQualifierAnnotations((AnnotatedElement)provideMethod);
        final String name = ReflectionHelper.getNameFromAllQualifiers((Set)qualifiers, (AnnotatedElement)provideMethod);
        final Set<Type> contracts = getAutoAdvertisedTypes(factoryProductionType);
        final ScopeInfo scopeInfo = getScopeInfo(provideMethod, null, collector);
        final Class<? extends Annotation> scope = scopeInfo.getAnnoType();
        final FactoryCreator<T> creator = new FactoryCreator<T>((ServiceLocator)locator, factoryDescriptor);
        collector.throwIfErrors();
        final Map<String, List<String>> metadata = new HashMap<String, List<String>>();
        if (scopeInfo.getScope() != null) {
            BuilderHelper.getMetadataValues(scopeInfo.getScope(), (Map)metadata);
        }
        for (final Annotation qualifier : qualifiers) {
            BuilderHelper.getMetadataValues(qualifier, (Map)metadata);
        }
        final UseProxy useProxy = provideMethod.getAnnotation(UseProxy.class);
        if (useProxy != null) {
            proxy = useProxy.value();
        }
        final ProxyForSameScope pfss = provideMethod.getAnnotation(ProxyForSameScope.class);
        if (pfss != null) {
            proxyForSameScope = pfss.value();
        }
        DescriptorVisibility visibility = DescriptorVisibility.NORMAL;
        final Visibility vi = provideMethod.getAnnotation(Visibility.class);
        if (vi != null) {
            visibility = vi.value();
        }
        int rank = 0;
        final Rank ranking = provideMethod.getAnnotation(Rank.class);
        if (ranking != null) {
            rank = ranking.value();
        }
        final AutoActiveDescriptor<T> retVal = new AutoActiveDescriptor<T>(factoryDescriptor.getImplementationClass(), creator, contracts, scope, name, qualifiers, visibility, rank, proxy, proxyForSameScope, null, metadata, DescriptorType.PROVIDE_METHOD, null);
        retVal.setScopeAsAnnotation(scopeInfo.getScope());
        collector.throwIfErrors();
        return retVal;
    }
    
    public static void justPreDestroy(final Object preMe, final ServiceLocatorImpl locator, final String strategy) {
        if (preMe == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        final ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
        collector.throwIfErrors();
        collector.throwIfErrors();
        final Class<?> baseClass = preMe.getClass();
        final Method preDestroy = getPreDestroy(baseClass, analyzer, collector);
        collector.throwIfErrors();
        if (preDestroy == null) {
            return;
        }
        try {
            ReflectionHelper.invoke(preMe, preDestroy, new Object[0], locator.getNeutralContextClassLoader());
        }
        catch (final Throwable e) {
            throw new MultiException(e);
        }
    }
    
    public static void justPostConstruct(final Object postMe, final ServiceLocatorImpl locator, final String strategy) {
        if (postMe == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        final ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
        collector.throwIfErrors();
        final Class<?> baseClass = postMe.getClass();
        final Method postConstruct = getPostConstruct(baseClass, analyzer, collector);
        collector.throwIfErrors();
        if (postConstruct == null) {
            return;
        }
        try {
            ReflectionHelper.invoke(postMe, postConstruct, new Object[0], locator.getNeutralContextClassLoader());
        }
        catch (final Throwable e) {
            throw new MultiException(e);
        }
    }
    
    public static Object justAssistedInject(final Object injectMe, final Method method, final ServiceLocatorImpl locator, final ServiceHandle<?> root, MethodParameter... givenValues) {
        if (injectMe == null || method == null) {
            throw new IllegalArgumentException("injectMe=" + injectMe + " method=" + method);
        }
        if (givenValues == null) {
            givenValues = new MethodParameter[0];
        }
        final int numParameters = method.getParameterTypes().length;
        final Map<Integer, MethodParameter> knownValues = new HashMap<Integer, MethodParameter>();
        for (final MethodParameter mp : givenValues) {
            final int index = mp.getParameterPosition();
            if (knownValues.containsKey(index)) {
                throw new IllegalArgumentException("The given values contain more than one value for index " + index);
            }
            knownValues.put(index, mp);
            if (index < 0 || index >= numParameters) {
                throw new IllegalArgumentException("Index of " + mp + " is out of range of the method parameters " + method);
            }
        }
        final List<SystemInjecteeImpl> injectees = getMethodInjectees(injectMe.getClass(), method, null, knownValues);
        final Object[] args = new Object[numParameters];
        for (int lcv = 0; lcv < injectees.size(); ++lcv) {
            final SystemInjecteeImpl injectee = injectees.get(lcv);
            if (injectee == null) {
                final MethodParameter mp2 = knownValues.get(lcv);
                if (mp2 == null) {
                    throw new AssertionError((Object)("Error getting values " + lcv + " method=" + method + " injectMe=" + injectMe + " knownValues=" + knownValues));
                }
                args[lcv] = mp2.getParameterValue();
            }
            else {
                final InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, (Injectee)injectee);
                args[lcv] = resolver.resolve((Injectee)injectee, (ServiceHandle)root);
            }
        }
        try {
            return ReflectionHelper.invoke(injectMe, method, args, locator.getNeutralContextClassLoader());
        }
        catch (final MultiException me) {
            throw me;
        }
        catch (final Throwable e) {
            throw new MultiException(e);
        }
    }
    
    public static void justInject(final Object injectMe, final ServiceLocatorImpl locator, final String strategy) {
        if (injectMe == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        final ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
        collector.throwIfErrors();
        final Class<?> baseClass = injectMe.getClass();
        final Set<Field> fields = getInitFields(baseClass, analyzer, collector);
        final Set<Method> methods = getInitMethods(baseClass, analyzer, collector);
        collector.throwIfErrors();
        for (final Field field : fields) {
            final InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, field);
            final List<SystemInjecteeImpl> injecteeFields = getFieldInjectees(baseClass, field, null);
            validateSelfInjectees(null, injecteeFields, collector);
            collector.throwIfErrors();
            final Injectee injectee = (Injectee)injecteeFields.get(0);
            final Object fieldValue = resolver.resolve(injectee, (ServiceHandle)null);
            try {
                ReflectionHelper.setField(field, injectMe, fieldValue);
            }
            catch (final MultiException me) {
                throw me;
            }
            catch (final Throwable th) {
                throw new MultiException(th);
            }
        }
        for (final Method method : methods) {
            final List<SystemInjecteeImpl> injectees = getMethodInjectees(baseClass, method, null);
            validateSelfInjectees(null, injectees, collector);
            collector.throwIfErrors();
            final Object[] args = new Object[injectees.size()];
            for (final SystemInjecteeImpl injectee2 : injectees) {
                final InjectionResolver<?> resolver2 = locator.getPerLocatorUtilities().getInjectionResolver(locator, (Injectee)injectee2);
                args[injectee2.getPosition()] = resolver2.resolve((Injectee)injectee2, (ServiceHandle)null);
            }
            try {
                ReflectionHelper.invoke(injectMe, method, args, locator.getNeutralContextClassLoader());
            }
            catch (final MultiException me2) {
                throw me2;
            }
            catch (final Throwable e) {
                throw new MultiException(e);
            }
        }
    }
    
    public static <T> T justCreate(final Class<T> createMe, final ServiceLocatorImpl locator, final String strategy) {
        if (createMe == null) {
            throw new IllegalArgumentException();
        }
        final Collector collector = new Collector();
        final ClassAnalyzer analyzer = getClassAnalyzer(locator, strategy, collector);
        collector.throwIfErrors();
        final Constructor<?> c = getConstructor((Class<?>)createMe, analyzer, collector);
        collector.throwIfErrors();
        final List<SystemInjecteeImpl> injectees = getConstructorInjectees(c, null);
        validateSelfInjectees(null, injectees, collector);
        collector.throwIfErrors();
        final Object[] args = new Object[injectees.size()];
        for (final SystemInjecteeImpl injectee : injectees) {
            final InjectionResolver<?> resolver = locator.getPerLocatorUtilities().getInjectionResolver(locator, (Injectee)injectee);
            args[injectee.getPosition()] = resolver.resolve((Injectee)injectee, (ServiceHandle)null);
        }
        try {
            return (T)ReflectionHelper.makeMe((Constructor)c, args, locator.getNeutralContextClassLoader());
        }
        catch (final Throwable th) {
            throw new MultiException(th);
        }
    }
    
    public static Class<?>[] getInterfacesForProxy(final Set<Type> contracts) {
        final LinkedList<Class<?>> retVal = new LinkedList<Class<?>>();
        retVal.add(ProxyCtl.class);
        for (final Type type : contracts) {
            final Class<?> rawClass = ReflectionHelper.getRawClass(type);
            if (rawClass == null) {
                continue;
            }
            if (!rawClass.isInterface()) {
                continue;
            }
            retVal.add(rawClass);
        }
        return retVal.toArray(new Class[retVal.size()]);
    }
    
    public static boolean isProxiableScope(final Class<? extends Annotation> scope) {
        return scope.isAnnotationPresent((Class<? extends Annotation>)Proxiable.class);
    }
    
    public static boolean isUnproxiableScope(final Class<? extends Annotation> scope) {
        return scope.isAnnotationPresent((Class<? extends Annotation>)Unproxiable.class);
    }
    
    private static boolean isProxiable(final ActiveDescriptor<?> desc, final Injectee injectee) {
        final Boolean directed = desc.isProxiable();
        if (directed != null) {
            if (injectee == null) {
                return directed;
            }
            if (!directed) {
                return false;
            }
            final ActiveDescriptor<?> injecteeDescriptor = (ActiveDescriptor<?>)injectee.getInjecteeDescriptor();
            if (injecteeDescriptor == null) {
                return true;
            }
            final Boolean sameScope = desc.isProxyForSameScope();
            return sameScope == null || sameScope || !desc.getScope().equals(injecteeDescriptor.getScope());
        }
        else {
            final Class<? extends Annotation> scopeAnnotation = desc.getScopeAnnotation();
            if (!scopeAnnotation.isAnnotationPresent((Class<? extends Annotation>)Proxiable.class)) {
                return false;
            }
            if (injectee == null) {
                return true;
            }
            final ActiveDescriptor<?> injecteeDescriptor2 = (ActiveDescriptor<?>)injectee.getInjecteeDescriptor();
            if (injecteeDescriptor2 == null) {
                return true;
            }
            final Proxiable proxiable = scopeAnnotation.getAnnotation(Proxiable.class);
            final Boolean proxyForSameScope = desc.isProxyForSameScope();
            if (proxyForSameScope != null) {
                if (proxyForSameScope) {
                    return true;
                }
            }
            else if (proxiable == null || proxiable.proxyForSameScope()) {
                return true;
            }
            return !desc.getScope().equals(injecteeDescriptor2.getScope());
        }
    }
    
    public static <T> T getFirstThingInList(final List<T> set) {
        final Iterator<T> iterator = set.iterator();
        if (iterator.hasNext()) {
            final T t = iterator.next();
            return t;
        }
        return null;
    }
    
    public static ActiveDescriptor<ServiceLocator> getLocatorDescriptor(final ServiceLocator locator) {
        final HashSet<Type> contracts = new HashSet<Type>();
        contracts.add(ServiceLocator.class);
        final Set<Annotation> qualifiers = Collections.emptySet();
        final ActiveDescriptor<ServiceLocator> retVal = (ActiveDescriptor<ServiceLocator>)new ConstantActiveDescriptor(locator, contracts, (Class<? extends Annotation>)PerLookup.class, null, qualifiers, DescriptorVisibility.LOCAL, 0, null, null, null, locator.getLocatorId(), null);
        return retVal;
    }
    
    public static ActiveDescriptor<InjectionResolver<Inject>> getThreeThirtyDescriptor(final ServiceLocatorImpl locator) {
        final ThreeThirtyResolver threeThirtyResolver = new ThreeThirtyResolver(locator);
        final HashSet<Type> contracts = new HashSet<Type>();
        final Type[] actuals = { Inject.class };
        contracts.add((Type)new ParameterizedTypeImpl((Type)InjectionResolver.class, actuals));
        final Set<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add((Annotation)new NamedImpl("SystemInjectResolver"));
        final ActiveDescriptor<InjectionResolver<Inject>> retVal = (ActiveDescriptor<InjectionResolver<Inject>>)new ConstantActiveDescriptor(threeThirtyResolver, contracts, (Class<? extends Annotation>)Singleton.class, "SystemInjectResolver", qualifiers, DescriptorVisibility.LOCAL, 0, null, null, null, locator.getLocatorId(), null);
        return retVal;
    }
    
    public static Constructor<?> findProducerConstructor(final Class<?> annotatedType, final ServiceLocatorImpl locator, final Collector collector) {
        Constructor<?> zeroArgConstructor = null;
        Constructor<?> aConstructorWithInjectAnnotation = null;
        final Set<Constructor<?>> allConstructors = getAllConstructors(annotatedType);
        for (final Constructor<?> constructor : allConstructors) {
            final Type[] rawParameters = constructor.getGenericParameterTypes();
            if (rawParameters.length <= 0) {
                zeroArgConstructor = constructor;
            }
            if (locator.hasInjectAnnotation(constructor)) {
                if (aConstructorWithInjectAnnotation != null) {
                    collector.addThrowable(new IllegalArgumentException("There is more than one constructor on class " + Pretty.clazz((Class)annotatedType)));
                    return null;
                }
                aConstructorWithInjectAnnotation = constructor;
            }
            if (!isProperConstructor(constructor)) {
                collector.addThrowable(new IllegalArgumentException("The constructor for " + Pretty.clazz((Class)annotatedType) + " may not have an annotation as a parameter"));
                return null;
            }
        }
        if (aConstructorWithInjectAnnotation != null) {
            return aConstructorWithInjectAnnotation;
        }
        if (zeroArgConstructor == null) {
            collector.addThrowable(new NoSuchMethodException("The class " + Pretty.clazz((Class)annotatedType) + " has no constructor marked @Inject and no zero argument constructor"));
            return null;
        }
        return zeroArgConstructor;
    }
    
    private static boolean isProperConstructor(final Constructor<?> c) {
        for (final Class<?> pClazz : c.getParameterTypes()) {
            if (pClazz.isAnnotation()) {
                return false;
            }
        }
        return true;
    }
    
    private static Set<Constructor<?>> getAllConstructors(final Class<?> clazz) {
        final HashSet<Constructor<?>> retVal = new LinkedHashSet<Constructor<?>>();
        final Constructor[] array;
        final Constructor<?>[] constructors = array = AccessController.doPrivileged((PrivilegedAction<Constructor[]>)new PrivilegedAction<Constructor<?>[]>() {
            @Override
            public Constructor<?>[] run() {
                return clazz.getDeclaredConstructors();
            }
        });
        for (final Constructor<?> constructor : array) {
            retVal.add(constructor);
        }
        return retVal;
    }
    
    private static boolean hasSubscribeToAnnotation(final Method method) {
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int outer = 0; outer < paramAnnotations.length; ++outer) {
            final Annotation[] paramAnnos = paramAnnotations[outer];
            for (int inner = 0; inner < paramAnnos.length; ++inner) {
                if (SubscribeTo.class.equals(paramAnnos[inner].annotationType())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static Set<Method> findInitializerMethods(final Class<?> annotatedType, final ServiceLocatorImpl locator, final Collector errorCollector) {
        final LinkedHashSet<Method> retVal = new LinkedHashSet<Method>();
        final ClassReflectionHelper crh = locator.getClassReflectionHelper();
        for (final MethodWrapper methodWrapper : crh.getAllMethods((Class)annotatedType)) {
            final Method method = methodWrapper.getMethod();
            if (locator.hasInjectAnnotation(method) && !method.isSynthetic()) {
                if (method.isBridge()) {
                    continue;
                }
                if (hasSubscribeToAnnotation(method)) {
                    continue;
                }
                if (!isProperMethod(method)) {
                    errorCollector.addThrowable(new IllegalArgumentException("An initializer method " + Pretty.method(method) + " is static, abstract or has a parameter that is an annotation"));
                }
                else {
                    retVal.add(method);
                }
            }
        }
        return retVal;
    }
    
    public static Method findPostConstruct(final Class<?> clazz, final ServiceLocatorImpl locator, final Collector collector) {
        try {
            return locator.getClassReflectionHelper().findPostConstruct((Class)clazz, (Class)PostConstruct.class);
        }
        catch (final IllegalArgumentException iae) {
            collector.addThrowable(iae);
            return null;
        }
    }
    
    public static Method findPreDestroy(final Class<?> clazz, final ServiceLocatorImpl locator, final Collector collector) {
        try {
            return locator.getClassReflectionHelper().findPreDestroy((Class)clazz, (Class)PreDestroy.class);
        }
        catch (final IllegalArgumentException iae) {
            collector.addThrowable(iae);
            return null;
        }
    }
    
    public static Set<Field> findInitializerFields(final Class<?> annotatedType, final ServiceLocatorImpl locator, final Collector errorCollector) {
        final LinkedHashSet<Field> retVal = new LinkedHashSet<Field>();
        final ClassReflectionHelper crh = locator.getClassReflectionHelper();
        final Set<Field> fields = crh.getAllFields((Class)annotatedType);
        for (final Field field : fields) {
            if (!locator.hasInjectAnnotation(field)) {
                continue;
            }
            if (!isProperField(field)) {
                errorCollector.addThrowable(new IllegalArgumentException("The field " + Pretty.field(field) + " may not be static, final or have an Annotation type"));
            }
            else {
                retVal.add(field);
            }
        }
        return retVal;
    }
    
    static AnnotatedElementAnnotationInfo computeAEAI(final AnnotatedElement annotatedElement) {
        if (annotatedElement instanceof Method) {
            final Method m = (Method)annotatedElement;
            return new AnnotatedElementAnnotationInfo(m.getAnnotations(), true, m.getParameterAnnotations(), false);
        }
        if (annotatedElement instanceof Constructor) {
            final Constructor<?> c = (Constructor<?>)annotatedElement;
            return new AnnotatedElementAnnotationInfo(c.getAnnotations(), true, c.getParameterAnnotations(), true);
        }
        return new AnnotatedElementAnnotationInfo(annotatedElement.getAnnotations(), false, new Annotation[0][], false);
    }
    
    private static boolean isProperMethod(final Method member) {
        if (ReflectionHelper.isStatic((Member)member)) {
            return false;
        }
        if (isAbstract(member)) {
            return false;
        }
        for (final Class<?> paramClazz : member.getParameterTypes()) {
            if (paramClazz.isAnnotation()) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isProperField(final Field field) {
        if (ReflectionHelper.isStatic((Member)field)) {
            return false;
        }
        if (isFinal(field)) {
            return false;
        }
        final Class<?> type = field.getType();
        return !type.isAnnotation();
    }
    
    public static boolean isAbstract(final Member member) {
        final int modifiers = member.getModifiers();
        return (modifiers & 0x400) != 0x0;
    }
    
    public static boolean isFinal(final Member member) {
        final int modifiers = member.getModifiers();
        return (modifiers & 0x10) != 0x0;
    }
    
    private static boolean isFinal(final Class<?> clazz) {
        final int modifiers = clazz.getModifiers();
        return (modifiers & 0x10) != 0x0;
    }
    
    private static ScopeInfo getScopeInfo(AnnotatedElement annotatedGuy, final Descriptor defaultScope, final Collector collector) {
        final AnnotatedElement topLevelElement = annotatedGuy;
        Annotation winnerScope = null;
        while (annotatedGuy != null) {
            final Annotation current = internalGetScopeAnnotationType(annotatedGuy, collector);
            if (current != null) {
                if (annotatedGuy.equals(topLevelElement)) {
                    winnerScope = current;
                    break;
                }
                if (current.annotationType().isAnnotationPresent(Inherited.class)) {
                    winnerScope = current;
                    break;
                }
                break;
            }
            else if (annotatedGuy instanceof Class) {
                annotatedGuy = ((Class)annotatedGuy).getSuperclass();
            }
            else {
                final Method theMethod = (Method)annotatedGuy;
                final Class<?> methodClass = theMethod.getDeclaringClass();
                annotatedGuy = null;
                for (Class<?> methodSuperclass = methodClass.getSuperclass(); methodSuperclass != null; methodSuperclass = methodSuperclass.getSuperclass()) {
                    if (Factory.class.isAssignableFrom(methodSuperclass)) {
                        annotatedGuy = getFactoryProvideMethod(methodSuperclass);
                        break;
                    }
                }
            }
        }
        if (winnerScope != null) {
            return new ScopeInfo(winnerScope, (Class)winnerScope.annotationType());
        }
        if (topLevelElement.isAnnotationPresent((Class<? extends Annotation>)Service.class)) {
            return new ScopeInfo((Annotation)ServiceLocatorUtilities.getSingletonAnnotation(), (Class)Singleton.class);
        }
        if (defaultScope != null && defaultScope.getScope() != null) {
            final Class<? extends Annotation> descScope = (Class<? extends Annotation>)loadClass(defaultScope.getScope(), defaultScope, collector);
            if (descScope != null) {
                return new ScopeInfo((Annotation)null, (Class)descScope);
            }
        }
        return new ScopeInfo((Annotation)ServiceLocatorUtilities.getPerLookupAnnotation(), (Class)PerLookup.class);
    }
    
    public static Class<? extends Annotation> getScopeAnnotationType(final Class<?> fromThis, final Descriptor defaultScope) {
        final Collector collector = new Collector();
        final ScopeInfo si = getScopeInfo(fromThis, defaultScope, collector);
        collector.throwIfErrors();
        return si.getAnnoType();
    }
    
    public static ScopeInfo getScopeAnnotationType(final AnnotatedElement annotatedGuy, final Descriptor defaultScope, final Collector collector) {
        final ScopeInfo si = getScopeInfo(annotatedGuy, defaultScope, collector);
        return si;
    }
    
    private static Annotation internalGetScopeAnnotationType(final AnnotatedElement annotatedGuy, final Collector collector) {
        boolean epicFail = false;
        Annotation retVal = null;
        for (final Annotation annotation : annotatedGuy.getDeclaredAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent((Class<? extends Annotation>)Scope.class)) {
                if (retVal != null) {
                    collector.addThrowable(new IllegalArgumentException("The type " + annotatedGuy + " may not have more than one scope.  It has at least " + Pretty.clazz((Class)retVal.annotationType()) + " and " + Pretty.clazz((Class)annotation.annotationType())));
                    epicFail = true;
                }
                else {
                    retVal = annotation;
                }
            }
        }
        if (epicFail) {
            return null;
        }
        return retVal;
    }
    
    public static Method getFactoryProvideMethod(final Class<?> clazz) {
        try {
            return clazz.getMethod("provide", (Class<?>[])new Class[0]);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
    }
    
    public static String getDefaultNameFromMethod(final Method parent, final Collector collector) {
        final Named named = parent.getAnnotation(Named.class);
        if (named == null) {
            return null;
        }
        if (named.value() == null || named.value().equals("")) {
            collector.addThrowable(new IllegalArgumentException("@Named on the provide method of a factory must have an explicit value"));
        }
        return named.value();
    }
    
    public static Set<Annotation> getAllQualifiers(final AnnotatedElement annotatedGuy, final String name, final Collector collector) {
        Named namedQualifier = null;
        final Set<Annotation> retVal = ReflectionHelper.getQualifierAnnotations(annotatedGuy);
        for (final Annotation anno : retVal) {
            if (anno instanceof Named) {
                namedQualifier = (Named)anno;
                break;
            }
        }
        if (name == null) {
            if (namedQualifier != null) {
                collector.addThrowable(new IllegalArgumentException("No name was in the descriptor, but this element(" + annotatedGuy + " has a Named annotation with value: " + namedQualifier.value()));
                retVal.remove(namedQualifier);
            }
            return retVal;
        }
        if (namedQualifier == null || namedQualifier.value().equals("")) {
            if (namedQualifier != null) {
                retVal.remove(namedQualifier);
            }
            namedQualifier = (Named)new NamedImpl(name);
            retVal.add((Annotation)namedQualifier);
        }
        if (!name.equals(namedQualifier.value())) {
            collector.addThrowable(new IllegalArgumentException("The class had an @Named qualifier that was inconsistent.  The expected name is " + name + " but the annotation has name " + namedQualifier.value()));
        }
        return retVal;
    }
    
    private static AnnotationInformation getParamInformation(final Annotation[] memberAnnotations) {
        boolean useDefault = true;
        Set<Annotation> qualifiers = null;
        boolean optional = false;
        boolean self = false;
        Unqualified unqualified = null;
        for (final Annotation anno : memberAnnotations) {
            if (ReflectionHelper.isAnnotationAQualifier(anno)) {
                if (qualifiers == null) {
                    qualifiers = new HashSet<Annotation>();
                }
                qualifiers.add(anno);
                useDefault = false;
            }
            else if (Optional.class.equals(anno.annotationType())) {
                optional = true;
                useDefault = false;
            }
            else if (Self.class.equals(anno.annotationType())) {
                self = true;
                useDefault = false;
            }
            else if (Unqualified.class.equals(anno.annotationType())) {
                unqualified = (Unqualified)anno;
                useDefault = false;
            }
        }
        if (useDefault) {
            return Utilities.DEFAULT_ANNOTATION_INFORMATION;
        }
        if (qualifiers == null) {
            qualifiers = Utilities.DEFAULT_ANNOTATION_INFORMATION.qualifiers;
        }
        return new AnnotationInformation((Set)qualifiers, optional, self, unqualified);
    }
    
    public static List<SystemInjecteeImpl> getConstructorInjectees(final Constructor<?> c, final ActiveDescriptor<?> injecteeDescriptor) {
        final Type[] genericTypeParams = c.getGenericParameterTypes();
        final Annotation[][] paramAnnotations = c.getParameterAnnotations();
        final List<SystemInjecteeImpl> retVal = new LinkedList<SystemInjecteeImpl>();
        for (int lcv = 0; lcv < genericTypeParams.length; ++lcv) {
            final AnnotationInformation ai = getParamInformation(paramAnnotations[lcv]);
            retVal.add(new SystemInjecteeImpl(genericTypeParams[lcv], ai.qualifiers, lcv, c, ai.optional, ai.self, ai.unqualified, injecteeDescriptor));
        }
        return retVal;
    }
    
    public static List<SystemInjecteeImpl> getMethodInjectees(final Class<?> actualClass, final Method c, final ActiveDescriptor<?> injecteeDescriptor) {
        return getMethodInjectees(actualClass, c, injecteeDescriptor, Collections.emptyMap());
    }
    
    public static List<SystemInjecteeImpl> getMethodInjectees(final Class<?> actualClass, final Method c, final ActiveDescriptor<?> injecteeDescriptor, final Map<Integer, MethodParameter> knownValues) {
        final Type[] genericTypeParams = c.getGenericParameterTypes();
        final Annotation[][] paramAnnotations = c.getParameterAnnotations();
        final List<SystemInjecteeImpl> retVal = new ArrayList<SystemInjecteeImpl>();
        final Class<?> declaringClass = c.getDeclaringClass();
        for (int lcv = 0; lcv < genericTypeParams.length; ++lcv) {
            if (knownValues.containsKey(lcv)) {
                retVal.add(null);
            }
            else {
                final AnnotationInformation ai = getParamInformation(paramAnnotations[lcv]);
                final Type adjustedType = ReflectionHelper.resolveMember((Class)actualClass, genericTypeParams[lcv], (Class)declaringClass);
                retVal.add(new SystemInjecteeImpl(adjustedType, ai.qualifiers, lcv, c, ai.optional, ai.self, ai.unqualified, injecteeDescriptor));
            }
        }
        return retVal;
    }
    
    private static Set<Annotation> getFieldAdjustedQualifierAnnotations(final Field f, final Set<Annotation> qualifiers) {
        final Named n = f.getAnnotation(Named.class);
        if (n == null) {
            return qualifiers;
        }
        if (n.value() != null && !"".equals(n.value())) {
            return qualifiers;
        }
        final HashSet<Annotation> retVal = new HashSet<Annotation>();
        for (final Annotation qualifier : qualifiers) {
            if (qualifier.annotationType().equals(Named.class)) {
                retVal.add((Annotation)new NamedImpl(f.getName()));
            }
            else {
                retVal.add(qualifier);
            }
        }
        return retVal;
    }
    
    public static List<SystemInjecteeImpl> getFieldInjectees(final Class<?> actualClass, final Field f, final ActiveDescriptor<?> injecteeDescriptor) {
        final List<SystemInjecteeImpl> retVal = new LinkedList<SystemInjecteeImpl>();
        final AnnotationInformation ai = getParamInformation(f.getAnnotations());
        final Type adjustedType = ReflectionHelper.resolveField((Class)actualClass, f);
        retVal.add(new SystemInjecteeImpl(adjustedType, getFieldAdjustedQualifierAnnotations(f, ai.qualifiers), -1, f, ai.optional, ai.self, ai.unqualified, injecteeDescriptor));
        return retVal;
    }
    
    public static void validateSelfInjectees(final ActiveDescriptor<?> givenDescriptor, final List<SystemInjecteeImpl> injectees, final Collector collector) {
        for (final Injectee injectee : injectees) {
            if (!injectee.isSelf()) {
                continue;
            }
            final Class<?> requiredRawClass = ReflectionHelper.getRawClass(injectee.getRequiredType());
            if (requiredRawClass == null || !ActiveDescriptor.class.equals(requiredRawClass)) {
                collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " does not have the required type of ActiveDescriptor"));
            }
            if (injectee.isOptional()) {
                collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " is marked both @Optional and @Self"));
            }
            if (!injectee.getRequiredQualifiers().isEmpty()) {
                collector.addThrowable(new IllegalArgumentException("Injection point " + injectee + " is marked @Self but has other qualifiers"));
            }
            if (givenDescriptor != null) {
                continue;
            }
            collector.addThrowable(new IllegalArgumentException("A class with injection point " + injectee + " is being created or injected via the non-managed ServiceLocator API"));
        }
    }
    
    public static Set<Annotation> fixAndCheckQualifiers(final Annotation[] qualifiers, final String name) {
        final Set<Annotation> retVal = new HashSet<Annotation>();
        final Set<String> dupChecker = new HashSet<String>();
        Named named = null;
        for (final Annotation qualifier : qualifiers) {
            final String annotationType = qualifier.annotationType().getName();
            if (dupChecker.contains(annotationType)) {
                throw new IllegalArgumentException(annotationType + " appears more than once in the qualifier list");
            }
            dupChecker.add(annotationType);
            retVal.add(qualifier);
            if (qualifier instanceof Named) {
                named = (Named)qualifier;
                if (named.value().equals("")) {
                    throw new IllegalArgumentException("The @Named qualifier must have a value");
                }
                if (name != null && !name.equals(named.value())) {
                    throw new IllegalArgumentException("The name passed to the method (" + name + ") does not match the value of the @Named qualifier (" + named.value() + ")");
                }
            }
        }
        if (named == null && name != null) {
            retVal.add((Annotation)new NamedImpl(name));
        }
        return retVal;
    }
    
    public static <T> T createService(ActiveDescriptor<T> root, final Injectee injectee, final ServiceLocatorImpl locator, final ServiceHandle<T> handle, final Class<?> requestedClass) {
        if (root == null) {
            throw new IllegalArgumentException();
        }
        T service = null;
        if (!root.isReified()) {
            root = (ActiveDescriptor<T>)locator.reifyDescriptor((Descriptor)root, injectee);
        }
        if (isProxiable(root, injectee)) {
            if (!proxiesAvailable()) {
                throw new IllegalStateException("A descriptor " + root + " requires a proxy, but the proxyable library is not on the classpath");
            }
            return locator.getPerLocatorUtilities().getProxyUtilities().generateProxy(requestedClass, locator, root, (ServiceHandleImpl)handle, injectee);
        }
        else {
            Context<?> context;
            try {
                context = locator.resolveContext(root.getScopeAnnotation());
            }
            catch (final Throwable th) {
                if (injectee != null && injectee.isOptional()) {
                    return null;
                }
                final Exception addMe = new IllegalStateException("While attempting to create a service for " + root + " in scope " + root.getScope() + " an error occured while locating the context");
                if (th instanceof MultiException) {
                    final MultiException me = (MultiException)th;
                    me.addError((Throwable)addMe);
                    throw me;
                }
                final MultiException me = new MultiException(th);
                me.addError((Throwable)addMe);
                throw me;
            }
            try {
                service = (T)context.findOrCreate((ActiveDescriptor)root, (ServiceHandle)handle);
            }
            catch (final MultiException me2) {
                throw me2;
            }
            catch (final Throwable th) {
                throw new MultiException(th);
            }
            if (service == null && !context.supportsNullCreation()) {
                throw new MultiException((Throwable)new IllegalStateException("Context " + context + " findOrCreate returned a null for descriptor " + root + " and handle " + handle));
            }
            return service;
        }
    }
    
    static Interceptors getAllInterceptors(final ServiceLocatorImpl impl, final ActiveDescriptor<?> descriptor, final Class<?> clazz, final Constructor<?> c) {
        if (descriptor == null || clazz == null || isFinal(clazz)) {
            return Utilities.EMTPY_INTERCEPTORS;
        }
        final ClassReflectionHelper crh = impl.getClassReflectionHelper();
        final List<InterceptionService> interceptionServices = impl.getInterceptionServices();
        if (interceptionServices == null || interceptionServices.isEmpty()) {
            return Utilities.EMTPY_INTERCEPTORS;
        }
        for (final String contract : descriptor.getAdvertisedContracts()) {
            if (Utilities.NOT_INTERCEPTED.contains(contract)) {
                return Utilities.EMTPY_INTERCEPTORS;
            }
        }
        final LinkedHashMap<Method, List<MethodInterceptor>> retVal = new LinkedHashMap<Method, List<MethodInterceptor>>();
        final ArrayList<ConstructorInterceptor> cRetVal = new ArrayList<ConstructorInterceptor>();
        for (final InterceptionService interceptionService : interceptionServices) {
            final Filter filter = interceptionService.getDescriptorFilter();
            if (BuilderHelper.filterMatches((Descriptor)descriptor, filter)) {
                for (final MethodWrapper methodWrapper : crh.getAllMethods((Class)clazz)) {
                    final Method method = methodWrapper.getMethod();
                    if (isFinal(method)) {
                        continue;
                    }
                    final List<MethodInterceptor> interceptors = interceptionService.getMethodInterceptors(method);
                    if (interceptors == null || interceptors.isEmpty()) {
                        continue;
                    }
                    List<MethodInterceptor> addToMe = retVal.get(method);
                    if (addToMe == null) {
                        addToMe = new ArrayList<MethodInterceptor>();
                        retVal.put(method, addToMe);
                    }
                    addToMe.addAll(interceptors);
                }
                final List<ConstructorInterceptor> cInterceptors = interceptionService.getConstructorInterceptors((Constructor)c);
                if (cInterceptors == null || cInterceptors.isEmpty()) {
                    continue;
                }
                cRetVal.addAll(cInterceptors);
            }
        }
        return new Interceptors() {
            @Override
            public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
                return retVal;
            }
            
            @Override
            public List<ConstructorInterceptor> getConstructorInterceptors() {
                return cRetVal;
            }
        };
    }
    
    public static boolean isTypeSafe(final Type requiredType, final Type beanType) {
        if (TypeChecker.isRawTypeSafe(requiredType, beanType)) {
            return true;
        }
        final Class<?> requiredClass = ReflectionHelper.getRawClass(requiredType);
        if (requiredClass == null) {
            return false;
        }
        if (!requiredClass.isAnnotation()) {
            return false;
        }
        final Class<?> beanClass = ReflectionHelper.getRawClass(beanType);
        if (beanClass == null) {
            return false;
        }
        if (beanClass.isAnnotationPresent((Class<? extends Annotation>)requiredClass)) {
            return true;
        }
        final Class<? extends Annotation> trueScope = getScopeAnnotationType(beanClass, null);
        return trueScope.equals(requiredClass);
    }
    
    public static synchronized boolean proxiesAvailable() {
        if (Utilities.proxiesAvailable != null) {
            return Utilities.proxiesAvailable;
        }
        ClassLoader loader = Utilities.class.getClassLoader();
        if (loader == null) {
            loader = ClassLoader.getSystemClassLoader();
        }
        try {
            loader.loadClass("javassist.util.proxy.MethodHandler");
            Utilities.proxiesAvailable = true;
            return true;
        }
        catch (final Throwable th) {
            Utilities.proxiesAvailable = false;
            return false;
        }
    }
    
    static {
        USE_SOFT_REFERENCE = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.useSoftReference", "true"));
            }
        });
        DEFAULT_ANNOTATION_INFORMATION = new AnnotationInformation((Set)Collections.emptySet(), false, false, (Unqualified)null);
        (NOT_INTERCEPTED = new HashSet<String>()).add(ServiceLocator.class.getName());
        Utilities.NOT_INTERCEPTED.add(InstanceLifecycleListener.class.getName());
        Utilities.NOT_INTERCEPTED.add(InjectionResolver.class.getName());
        Utilities.NOT_INTERCEPTED.add(ErrorService.class.getName());
        Utilities.NOT_INTERCEPTED.add(ClassAnalyzer.class.getName());
        Utilities.NOT_INTERCEPTED.add(DynamicConfigurationListener.class.getName());
        Utilities.NOT_INTERCEPTED.add(DynamicConfigurationService.class.getName());
        Utilities.NOT_INTERCEPTED.add(InterceptionService.class.getName());
        Utilities.NOT_INTERCEPTED.add(ValidationService.class.getName());
        Utilities.NOT_INTERCEPTED.add(Context.class.getName());
        EMTPY_INTERCEPTORS = new Interceptors() {
            @Override
            public Map<Method, List<MethodInterceptor>> getMethodInterceptors() {
                return null;
            }
            
            @Override
            public List<ConstructorInterceptor> getConstructorInterceptors() {
                return null;
            }
        };
        Utilities.proxiesAvailable = null;
    }
    
    private static class AnnotationInformation
    {
        private final Set<Annotation> qualifiers;
        private final boolean optional;
        private final boolean self;
        private final Unqualified unqualified;
        
        private AnnotationInformation(final Set<Annotation> qualifiers, final boolean optional, final boolean self, final Unqualified unqualified) {
            this.qualifiers = qualifiers;
            this.optional = optional;
            this.self = self;
            this.unqualified = unqualified;
        }
    }
    
    public interface Interceptors
    {
        Map<Method, List<MethodInterceptor>> getMethodInterceptors();
        
        List<ConstructorInterceptor> getConstructorInterceptors();
    }
}
