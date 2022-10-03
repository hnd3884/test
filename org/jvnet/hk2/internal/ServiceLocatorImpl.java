package org.jvnet.hk2.internal;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.hk2.api.HK2Loader;
import java.lang.reflect.ParameterizedType;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import java.util.StringTokenizer;
import org.glassfish.hk2.api.Validator;
import org.glassfish.hk2.api.TwoPhaseTransactionData;
import org.glassfish.hk2.api.TwoPhaseResource;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import java.util.SortedSet;
import javax.inject.Named;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.MethodParameter;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import java.util.HashSet;
import org.glassfish.hk2.utilities.InjecteeImpl;
import java.lang.reflect.Type;
import org.glassfish.hk2.api.messaging.Topic;
import java.util.Set;
import org.glassfish.hk2.api.IterableProvider;
import javax.inject.Provider;
import org.glassfish.hk2.api.JustInTimeInjectionResolver;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Comparator;
import java.util.TreeSet;
import org.glassfish.hk2.api.DescriptorVisibility;
import java.util.Collections;
import org.glassfish.hk2.api.IndexedFilter;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Operation;
import org.glassfish.hk2.api.Filter;
import java.util.Iterator;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.utilities.reflection.Logger;
import java.util.Collection;
import java.util.List;
import org.glassfish.hk2.utilities.cache.CacheUtilities;
import org.glassfish.hk2.api.Injectee;
import java.util.WeakHashMap;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;
import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.reflection.internal.ClassReflectionHelperImpl;
import org.glassfish.hk2.utilities.cache.WeakCARCache;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.api.InjectionResolver;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.hk2.api.Unqualified;
import org.glassfish.hk2.api.ClassAnalyzer;
import java.util.Map;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.utilities.cache.Cache;
import org.glassfish.hk2.api.InterceptionService;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ErrorService;
import java.util.LinkedList;
import org.glassfish.hk2.api.ValidationService;
import java.util.LinkedHashSet;
import org.glassfish.hk2.api.PerLookup;
import javax.inject.Singleton;
import org.glassfish.hk2.api.Context;
import java.util.HashMap;
import org.glassfish.hk2.utilities.reflection.ClassReflectionHelper;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.glassfish.hk2.api.ServiceLocator;

public class ServiceLocatorImpl implements ServiceLocator
{
    private static final String BIND_TRACING_PATTERN_PROPERTY = "org.jvnet.hk2.properties.bind.tracing.pattern";
    private static final String BIND_TRACING_PATTERN;
    private static final String BIND_TRACING_STACKS_PROPERTY = "org.jvnet.hk2.properties.bind.tracing.stacks";
    private static boolean BIND_TRACING_STACKS;
    private static final int CACHE_SIZE = 20000;
    private static final Object sLock;
    private static long currentLocatorId;
    static final DescriptorComparator DESCRIPTOR_COMPARATOR;
    private static final ServiceHandleComparator HANDLE_COMPARATOR;
    private final ReentrantReadWriteLock readWriteLock;
    private final ReentrantReadWriteLock.WriteLock wLock;
    private final ReentrantReadWriteLock.ReadLock rLock;
    private final AtomicLong nextServiceId;
    private final String locatorName;
    private final long id;
    private final ServiceLocatorImpl parent;
    private volatile boolean neutralContextClassLoader;
    private final ClassReflectionHelper classReflectionHelper;
    private final PerLocatorUtilities perLocatorUtilities;
    private final IndexedListData allDescriptors;
    private final HashMap<String, IndexedListData> descriptorsByAdvertisedContract;
    private final HashMap<String, IndexedListData> descriptorsByName;
    private final Context<Singleton> singletonContext;
    private final Context<PerLookup> perLookupContext;
    private final LinkedHashSet<ValidationService> allValidators;
    private final LinkedList<ErrorService> errorHandlers;
    private final LinkedList<ServiceHandle<?>> configListeners;
    private volatile boolean hasInterceptionServices;
    private final LinkedList<InterceptionService> interceptionServices;
    private final Cache<Class<? extends Annotation>, Context<?>> contextCache;
    private final Map<ServiceLocatorImpl, ServiceLocatorImpl> children;
    private final Object classAnalyzerLock;
    private final HashMap<String, ClassAnalyzer> classAnalyzers;
    private String defaultClassAnalyzer;
    private volatile Unqualified defaultUnqualified;
    private ConcurrentHashMap<Class<? extends Annotation>, InjectionResolver<?>> allResolvers;
    private final Cache<SystemInjecteeImpl, InjectionResolver<?>> injecteeToResolverCache;
    private ServiceLocatorState state;
    private final WeakCARCache<IgdCacheKey, IgdValue> igdCache;
    private final WeakCARCache<IgdCacheKey, IgdValue> igashCache;
    
    private static long getAndIncrementLocatorId() {
        synchronized (ServiceLocatorImpl.sLock) {
            return ServiceLocatorImpl.currentLocatorId++;
        }
    }
    
    public ServiceLocatorImpl(final String name, final ServiceLocatorImpl parent) {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.wLock = this.readWriteLock.writeLock();
        this.rLock = this.readWriteLock.readLock();
        this.nextServiceId = new AtomicLong();
        this.neutralContextClassLoader = true;
        this.classReflectionHelper = (ClassReflectionHelper)new ClassReflectionHelperImpl();
        this.perLocatorUtilities = new PerLocatorUtilities(this);
        this.allDescriptors = new IndexedListData();
        this.descriptorsByAdvertisedContract = new HashMap<String, IndexedListData>();
        this.descriptorsByName = new HashMap<String, IndexedListData>();
        this.singletonContext = (Context<Singleton>)new SingletonContext(this);
        this.perLookupContext = (Context<PerLookup>)new PerLookupContext();
        this.allValidators = new LinkedHashSet<ValidationService>();
        this.errorHandlers = new LinkedList<ErrorService>();
        this.configListeners = new LinkedList<ServiceHandle<?>>();
        this.hasInterceptionServices = false;
        this.interceptionServices = new LinkedList<InterceptionService>();
        this.contextCache = (Cache<Class<? extends Annotation>, Context<?>>)new Cache((Computable)new Computable<Class<? extends Annotation>, Context<?>>() {
            public Context<?> compute(final Class<? extends Annotation> a) {
                return ServiceLocatorImpl.this._resolveContext(a);
            }
        });
        this.children = new WeakHashMap<ServiceLocatorImpl, ServiceLocatorImpl>();
        this.classAnalyzerLock = new Object();
        this.classAnalyzers = new HashMap<String, ClassAnalyzer>();
        this.defaultClassAnalyzer = "default";
        this.defaultUnqualified = null;
        this.allResolvers = new ConcurrentHashMap<Class<? extends Annotation>, InjectionResolver<?>>();
        this.injecteeToResolverCache = (Cache<SystemInjecteeImpl, InjectionResolver<?>>)new Cache((Computable)new Computable<SystemInjecteeImpl, InjectionResolver<?>>() {
            public InjectionResolver<?> compute(final SystemInjecteeImpl key) {
                return ServiceLocatorImpl.this.perLocatorUtilities.getInjectionResolver(ServiceLocatorImpl.this.getMe(), (Injectee)key);
            }
        });
        this.state = ServiceLocatorState.RUNNING;
        this.igdCache = (WeakCARCache<IgdCacheKey, IgdValue>)CacheUtilities.createWeakCARCache((Computable)new Computable<IgdCacheKey, IgdValue>() {
            public IgdValue compute(final IgdCacheKey key) {
                return ServiceLocatorImpl.this.igdCacheCompute(key);
            }
        }, 20000, false);
        this.igashCache = (WeakCARCache<IgdCacheKey, IgdValue>)CacheUtilities.createWeakCARCache((Computable)new Computable<IgdCacheKey, IgdValue>() {
            public IgdValue compute(final IgdCacheKey key) {
                final List<SystemDescriptor<?>> candidates = ServiceLocatorImpl.this.getDescriptors(key.filter, null, true, false, true);
                final ImmediateResults immediate = ServiceLocatorImpl.this.narrow((ServiceLocator)ServiceLocatorImpl.this, candidates, key.contractOrImpl, null, null, false, true, null, key.filter, key.qualifiers);
                final NarrowResults results = immediate.getTimelessResults();
                if (!results.getErrors().isEmpty()) {
                    Utilities.handleErrors(results, new LinkedList<ErrorService>(ServiceLocatorImpl.this.errorHandlers));
                    throw new ComputationErrorException((Object)new IgdValue(results, immediate));
                }
                return new IgdValue(results, immediate);
            }
        }, 20000, false);
        this.locatorName = name;
        this.parent = parent;
        if (parent != null) {
            parent.addChild(this);
        }
        this.id = getAndIncrementLocatorId();
        Logger.getLogger().debug("Created ServiceLocator " + this);
        if (ServiceLocatorImpl.BIND_TRACING_PATTERN != null) {
            Logger.getLogger().debug("HK2 will trace binds and unbinds of " + ServiceLocatorImpl.BIND_TRACING_PATTERN + " with stacks " + ServiceLocatorImpl.BIND_TRACING_STACKS + " in " + this);
        }
    }
    
    private boolean callValidate(final ValidationService vs, final ValidationInformation vi) {
        try {
            return vs.getValidator().validate(vi);
        }
        catch (final Throwable th) {
            final List<ErrorService> localErrorServices = new LinkedList<ErrorService>(this.errorHandlers);
            MultiException useException;
            if (th instanceof MultiException) {
                useException = (MultiException)th;
            }
            else {
                useException = new MultiException(th);
            }
            final ErrorInformationImpl ei = new ErrorInformationImpl(ErrorType.VALIDATE_FAILURE, (Descriptor)vi.getCandidate(), vi.getInjectee(), useException);
            for (final ErrorService errorService : localErrorServices) {
                try {
                    errorService.onFailure((ErrorInformation)ei);
                }
                catch (final Throwable th2) {
                    Logger.getLogger().debug("ServiceLocatorImpl", "callValidate", th2);
                }
            }
            return false;
        }
    }
    
    private boolean validate(final SystemDescriptor<?> descriptor, final Injectee onBehalfOf, final Filter filter) {
        for (final ValidationService vs : this.getAllValidators()) {
            if (!descriptor.isValidating(vs)) {
                continue;
            }
            if (!this.callValidate(vs, (ValidationInformation)new ValidationInformationImpl(Operation.LOOKUP, (ActiveDescriptor<?>)descriptor, onBehalfOf, filter))) {
                return false;
            }
        }
        return true;
    }
    
    private List<SystemDescriptor<?>> getDescriptors(final Filter filter, final Injectee onBehalfOf, final boolean getParents, final boolean doValidation, final boolean getLocals) {
        if (filter == null) {
            throw new IllegalArgumentException("filter is null");
        }
        this.rLock.lock();
        LinkedList<SystemDescriptor<?>> retVal;
        try {
            Collection<SystemDescriptor<?>> sortMeOut;
            if (filter instanceof IndexedFilter) {
                final IndexedFilter df = (IndexedFilter)filter;
                if (df.getName() != null) {
                    final String name = df.getName();
                    final IndexedListData ild = this.descriptorsByName.get(name);
                    Collection<SystemDescriptor<?>> scopedByName = (ild == null) ? null : ild.getSortedList();
                    if (scopedByName == null) {
                        scopedByName = (Collection<SystemDescriptor<?>>)Collections.emptyList();
                    }
                    if (df.getAdvertisedContract() != null) {
                        sortMeOut = new LinkedList<SystemDescriptor<?>>();
                        for (final SystemDescriptor<?> candidate : scopedByName) {
                            if (candidate.getAdvertisedContracts().contains(df.getAdvertisedContract())) {
                                sortMeOut.add(candidate);
                            }
                        }
                    }
                    else {
                        sortMeOut = scopedByName;
                    }
                }
                else if (df.getAdvertisedContract() != null) {
                    final String advertisedContract = df.getAdvertisedContract();
                    final IndexedListData ild2 = this.descriptorsByAdvertisedContract.get(advertisedContract);
                    sortMeOut = ((ild2 == null) ? null : ild2.getSortedList());
                    if (sortMeOut == null) {
                        sortMeOut = (Collection<SystemDescriptor<?>>)Collections.emptyList();
                    }
                }
                else {
                    sortMeOut = this.allDescriptors.getSortedList();
                }
            }
            else {
                sortMeOut = this.allDescriptors.getSortedList();
            }
            retVal = new LinkedList<SystemDescriptor<?>>();
            for (final SystemDescriptor<?> candidate2 : sortMeOut) {
                if (!getLocals && DescriptorVisibility.LOCAL.equals((Object)candidate2.getDescriptorVisibility())) {
                    continue;
                }
                if (doValidation && !this.validate(candidate2, onBehalfOf, filter)) {
                    continue;
                }
                if (!filter.matches((Descriptor)candidate2)) {
                    continue;
                }
                retVal.add(candidate2);
            }
        }
        finally {
            this.rLock.unlock();
        }
        if (getParents && this.parent != null) {
            final TreeSet<SystemDescriptor<?>> sorter = new TreeSet<SystemDescriptor<?>>((Comparator<? super SystemDescriptor<?>>)ServiceLocatorImpl.DESCRIPTOR_COMPARATOR);
            sorter.addAll(retVal);
            sorter.addAll(this.parent.getDescriptors(filter, onBehalfOf, getParents, doValidation, false));
            retVal.clear();
            retVal.addAll(sorter);
        }
        return retVal;
    }
    
    private List<ActiveDescriptor<?>> protectedGetDescriptors(final Filter filter) {
        return AccessController.doPrivileged((PrivilegedAction<List<ActiveDescriptor<?>>>)new PrivilegedAction<List<ActiveDescriptor<?>>>() {
            @Override
            public List<ActiveDescriptor<?>> run() {
                return ServiceLocatorImpl.this.getDescriptors(filter);
            }
        });
    }
    
    public List<ActiveDescriptor<?>> getDescriptors(final Filter filter) {
        this.checkState();
        return (List)ReflectionHelper.cast((Object)this.getDescriptors(filter, null, true, true, true));
    }
    
    public ActiveDescriptor<?> getBestDescriptor(final Filter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter is null");
        }
        this.checkState();
        final List<ActiveDescriptor<?>> sorted = this.getDescriptors(filter);
        return Utilities.getFirstThingInList(sorted);
    }
    
    public ActiveDescriptor<?> reifyDescriptor(final Descriptor descriptor, final Injectee injectee) throws MultiException {
        this.checkState();
        if (descriptor == null) {
            throw new IllegalArgumentException();
        }
        if (!(descriptor instanceof ActiveDescriptor)) {
            final SystemDescriptor<?> sd = new SystemDescriptor<Object>(descriptor, true, this, null);
            final Class<?> implClass = this.loadClass(descriptor, injectee);
            final Collector collector = new Collector();
            sd.reify(implClass, collector);
            collector.throwIfErrors();
            return (ActiveDescriptor<?>)sd;
        }
        final ActiveDescriptor<?> active = (ActiveDescriptor<?>)descriptor;
        if (active.isReified()) {
            return active;
        }
        SystemDescriptor<?> sd2;
        if (active instanceof SystemDescriptor) {
            sd2 = (SystemDescriptor)active;
        }
        else {
            sd2 = new SystemDescriptor<Object>(descriptor, true, this, null);
        }
        Class<?> implClass2 = sd2.getPreAnalyzedClass();
        if (implClass2 == null) {
            implClass2 = this.loadClass(descriptor, injectee);
        }
        final Collector collector2 = new Collector();
        sd2.reify(implClass2, collector2);
        collector2.throwIfErrors();
        return (ActiveDescriptor<?>)sd2;
    }
    
    public ActiveDescriptor<?> reifyDescriptor(final Descriptor descriptor) throws MultiException {
        this.checkState();
        return this.reifyDescriptor(descriptor, null);
    }
    
    private ActiveDescriptor<?> secondChanceResolve(final Injectee injectee) {
        final Collector collector = new Collector();
        final List<ServiceHandle<JustInTimeInjectionResolver>> jitResolvers = (List<ServiceHandle<JustInTimeInjectionResolver>>)ReflectionHelper.cast((Object)this.getAllServiceHandles(JustInTimeInjectionResolver.class, new Annotation[0]));
        try {
            boolean modified = false;
            boolean aJITFailed = false;
            for (final ServiceHandle<JustInTimeInjectionResolver> handle : jitResolvers) {
                if (injectee.getInjecteeClass() != null && injectee.getInjecteeClass().getName().equals(handle.getActiveDescriptor().getImplementation())) {
                    continue;
                }
                JustInTimeInjectionResolver jitResolver;
                try {
                    jitResolver = (JustInTimeInjectionResolver)handle.getService();
                }
                catch (final MultiException me) {
                    Logger.getLogger().debug(handle.toString(), "secondChanceResolver", (Throwable)me);
                    continue;
                }
                boolean jitModified = false;
                try {
                    jitModified = jitResolver.justInTimeResolution(injectee);
                }
                catch (final Throwable th) {
                    collector.addThrowable(th);
                    aJITFailed = true;
                }
                modified = (jitModified || modified);
            }
            if (aJITFailed) {
                collector.throwIfErrors();
            }
            if (!modified) {
                return null;
            }
            return this.internalGetInjecteeDescriptor(injectee, true);
        }
        finally {
            for (final ServiceHandle<JustInTimeInjectionResolver> jitResolver2 : jitResolvers) {
                if (jitResolver2.getActiveDescriptor().getScope() == null || PerLookup.class.getName().equals(jitResolver2.getActiveDescriptor().getScope())) {
                    jitResolver2.destroy();
                }
            }
        }
    }
    
    private ActiveDescriptor<?> internalGetInjecteeDescriptor(final Injectee injectee, final boolean calledFromSecondChanceResolveMethod) {
        if (injectee == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        final Type requiredType = injectee.getRequiredType();
        final Class<?> rawType = ReflectionHelper.getRawClass(requiredType);
        if (rawType == null) {
            throw new MultiException((Throwable)new IllegalArgumentException("Invalid injectee with required type of " + injectee.getRequiredType() + " passed to getInjecteeDescriptor"));
        }
        if (Provider.class.equals(rawType) || Iterable.class.equals(rawType) || IterableProvider.class.equals(rawType)) {
            final boolean isIterable = IterableProvider.class.equals(rawType);
            final IterableProviderImpl<?> value = new IterableProviderImpl<Object>(this, ReflectionHelper.getFirstTypeArgument(requiredType), injectee.getRequiredQualifiers(), injectee.getUnqualified(), injectee, isIterable);
            return (ActiveDescriptor<?>)new ConstantActiveDescriptor(value, this);
        }
        if (Topic.class.equals(rawType)) {
            final TopicImpl<?> value2 = new TopicImpl<Object>(this, ReflectionHelper.getFirstTypeArgument(requiredType), injectee.getRequiredQualifiers());
            return (ActiveDescriptor<?>)new ConstantActiveDescriptor(value2, this);
        }
        final Set<Annotation> qualifiersAsSet = injectee.getRequiredQualifiers();
        final String name = ReflectionHelper.getNameFromAllQualifiers((Set)qualifiersAsSet, injectee.getParent());
        final Annotation[] qualifiers = qualifiersAsSet.toArray(new Annotation[qualifiersAsSet.size()]);
        return this.internalGetDescriptor(injectee, requiredType, name, injectee.getUnqualified(), false, calledFromSecondChanceResolveMethod, qualifiers);
    }
    
    public ActiveDescriptor<?> getInjecteeDescriptor(final Injectee injectee) throws MultiException {
        return this.internalGetInjecteeDescriptor(injectee, false);
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final ActiveDescriptor<T> activeDescriptor, final Injectee injectee) throws MultiException {
        if (activeDescriptor != null) {
            if (!(activeDescriptor instanceof SystemDescriptor) && !(activeDescriptor instanceof ConstantActiveDescriptor)) {
                throw new IllegalArgumentException("The descriptor passed to getServiceHandle must have been bound into a ServiceLocator.  The descriptor is of type " + activeDescriptor.getClass().getName());
            }
            final Long sdLocator = activeDescriptor.getLocatorId();
            if (sdLocator == null) {
                throw new IllegalArgumentException("The descriptor passed to getServiceHandle is not associated with any ServiceLocator");
            }
            if (sdLocator != this.id) {
                if (this.parent != null) {
                    return (ServiceHandle<T>)this.parent.getServiceHandle((org.glassfish.hk2.api.ActiveDescriptor<Object>)activeDescriptor, injectee);
                }
                throw new IllegalArgumentException("The descriptor passed to getServiceHandle is not associated with this ServiceLocator (id=" + this.id + ").  It is associated ServiceLocator id=" + sdLocator);
            }
            else {
                final Long sdSID = activeDescriptor.getServiceId();
                if (activeDescriptor instanceof SystemDescriptor && sdSID == null) {
                    throw new IllegalArgumentException("The descriptor passed to getServiceHandle was never added to this ServiceLocator (id=" + this.id + ")");
                }
            }
        }
        return (ServiceHandle<T>)this.getServiceHandleImpl(activeDescriptor, injectee);
    }
    
    private <T> ServiceHandleImpl<T> getServiceHandleImpl(final ActiveDescriptor<T> activeDescriptor, final Injectee injectee) throws MultiException {
        if (activeDescriptor == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        return new ServiceHandleImpl<T>(this, activeDescriptor, injectee);
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final ActiveDescriptor<T> activeDescriptor) throws MultiException {
        return this.getServiceHandle(activeDescriptor, null);
    }
    
    private <T> ServiceHandleImpl<T> internalGetServiceHandle(final ActiveDescriptor<T> activeDescriptor, final Type requestedType, final Injectee originalRequest) {
        if (activeDescriptor == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        if (requestedType == null) {
            return this.getServiceHandleImpl(activeDescriptor, null);
        }
        final Injectee useInjectee = (Injectee)((originalRequest != null) ? originalRequest : new InjecteeImpl(requestedType));
        return this.getServiceHandleImpl(activeDescriptor, useInjectee);
    }
    
    @Deprecated
    public <T> T getService(final ActiveDescriptor<T> activeDescriptor, final ServiceHandle<?> root) throws MultiException {
        return this.getService(activeDescriptor, root, null);
    }
    
    public <T> T getService(final ActiveDescriptor<T> activeDescriptor, final ServiceHandle<?> root, final Injectee originalRequest) throws MultiException {
        this.checkState();
        final Type contractOrImpl = (originalRequest == null) ? null : originalRequest.getRequiredType();
        final Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
        if (root == null) {
            final ServiceHandleImpl<T> tmpRoot = new ServiceHandleImpl<T>(this, activeDescriptor, originalRequest);
            return Utilities.createService(activeDescriptor, originalRequest, this, (org.glassfish.hk2.api.ServiceHandle<T>)tmpRoot, rawClass);
        }
        final ServiceHandleImpl<?> rootImpl = (ServiceHandleImpl)root;
        final ServiceHandleImpl<T> subHandle = this.internalGetServiceHandle(activeDescriptor, contractOrImpl, originalRequest);
        if (PerLookup.class.equals(activeDescriptor.getScopeAnnotation())) {
            rootImpl.addSubHandle(subHandle);
        }
        rootImpl.pushInjectee(originalRequest);
        try {
            return subHandle.getService((org.glassfish.hk2.api.ServiceHandle<T>)root);
        }
        finally {
            rootImpl.popInjectee();
        }
    }
    
    public <T> T getService(final Class<T> contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return this.internalGetService(contractOrImpl, null, null, qualifiers);
    }
    
    public <T> T getService(final Type contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return this.internalGetService(contractOrImpl, null, null, qualifiers);
    }
    
    public <T> T getService(final Class<T> contractOrImpl, final String name, final Annotation... qualifiers) throws MultiException {
        return this.internalGetService(contractOrImpl, name, null, qualifiers);
    }
    
    public <T> T getService(final Type contractOrImpl, final String name, final Annotation... qualifiers) throws MultiException {
        return this.internalGetService(contractOrImpl, name, null, qualifiers);
    }
    
    private <T> T internalGetService(final Type contractOrImpl, final String name, final Unqualified unqualified, final Annotation... qualifiers) {
        return this.internalGetService(contractOrImpl, name, unqualified, false, qualifiers);
    }
    
    private <T> T internalGetService(final Type contractOrImpl, final String name, final Unqualified unqualified, final boolean calledFromSecondChanceResolveMethod, final Annotation... qualifiers) {
        this.checkState();
        final Class<?> rawType = ReflectionHelper.getRawClass(contractOrImpl);
        if (rawType != null && (Provider.class.equals(rawType) || IterableProvider.class.equals(rawType))) {
            final boolean isIterable = IterableProvider.class.equals(rawType);
            final Type requiredType = ReflectionHelper.getFirstTypeArgument(contractOrImpl);
            final HashSet<Annotation> requiredQualifiers = new HashSet<Annotation>();
            for (final Annotation qualifier : qualifiers) {
                requiredQualifiers.add(qualifier);
            }
            final InjecteeImpl injectee = new InjecteeImpl(requiredType);
            injectee.setRequiredQualifiers((Set)requiredQualifiers);
            injectee.setUnqualified(unqualified);
            final IterableProviderImpl<?> retVal = new IterableProviderImpl<Object>(this, requiredType, requiredQualifiers, unqualified, (Injectee)injectee, isIterable);
            return (T)retVal;
        }
        final ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, name, unqualified, false, calledFromSecondChanceResolveMethod, qualifiers);
        if (ad == null) {
            return null;
        }
        final T retVal2 = Utilities.createService(ad, null, this, null, rawType);
        return retVal2;
    }
    
     <T> T getUnqualifiedService(final Type contractOrImpl, final Unqualified unqualified, final boolean isIterable, final Annotation... qualifiers) throws MultiException {
        return this.internalGetService(contractOrImpl, null, unqualified, true, qualifiers);
    }
    
    private <T> List<T> protectedGetAllServices(final Type contractOrImpl, final Annotation... qualifiers) {
        return AccessController.doPrivileged((PrivilegedAction<List<T>>)new PrivilegedAction<List<T>>() {
            @Override
            public List<T> run() {
                return (List<T>)ServiceLocatorImpl.this.getAllServices(contractOrImpl, qualifiers);
            }
        });
    }
    
    public <T> List<T> getAllServices(final Class<T> contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return this.getAllServices((Type)contractOrImpl, qualifiers);
    }
    
    public <T> List<T> getAllServices(final Type contractOrImpl, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        final List<T> retVal = (List<T>)this.internalGetAllServiceHandles(contractOrImpl, null, false, false, qualifiers);
        return retVal;
    }
    
    public <T> List<T> getAllServices(final Annotation qualifier, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        final List<ServiceHandle<?>> services = this.getAllServiceHandles(qualifier, qualifiers);
        final List<T> retVal = new LinkedList<T>();
        for (final ServiceHandle<?> service : services) {
            retVal.add((T)service.getService());
        }
        return retVal;
    }
    
    public List<?> getAllServices(final Filter searchCriteria) throws MultiException {
        this.checkState();
        final List<ServiceHandle<?>> handleSet = this.getAllServiceHandles(searchCriteria);
        final List<Object> retVal = new LinkedList<Object>();
        for (final ServiceHandle<?> handle : handleSet) {
            retVal.add(handle.getService());
        }
        return retVal;
    }
    
    public String getName() {
        return this.locatorName;
    }
    
    public ServiceLocatorState getState() {
        this.rLock.lock();
        try {
            return this.state;
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    public void shutdown() {
        this.wLock.lock();
        try {
            if (this.state.equals((Object)ServiceLocatorState.SHUTDOWN)) {
                return;
            }
            if (this.parent != null) {
                this.parent.removeChild(this);
            }
        }
        finally {
            this.wLock.unlock();
        }
        final List<ServiceHandle<?>> handles = this.getAllServiceHandles((Filter)new IndexedFilter() {
            public boolean matches(final Descriptor d) {
                return d.getLocatorId().equals(ServiceLocatorImpl.this.id);
            }
            
            public String getAdvertisedContract() {
                return Context.class.getName();
            }
            
            public String getName() {
                return null;
            }
        });
        for (final ServiceHandle<?> handle : handles) {
            if (handle.isActive()) {
                final Context<?> context = (Context<?>)handle.getService();
                context.shutdown();
            }
        }
        this.singletonContext.shutdown();
        this.wLock.lock();
        try {
            this.state = ServiceLocatorState.SHUTDOWN;
            this.allDescriptors.clear();
            this.descriptorsByAdvertisedContract.clear();
            this.descriptorsByName.clear();
            this.allResolvers.clear();
            this.injecteeToResolverCache.clear();
            this.allValidators.clear();
            this.errorHandlers.clear();
            this.igdCache.clear();
            this.igashCache.clear();
            this.classReflectionHelper.dispose();
            this.contextCache.clear();
            this.perLocatorUtilities.shutdown();
            synchronized (this.children) {
                this.children.clear();
            }
            Logger.getLogger().debug("Shutdown ServiceLocator " + this);
        }
        finally {
            this.wLock.unlock();
        }
        ServiceLocatorFactory.getInstance().destroy((ServiceLocator)this);
        Logger.getLogger().debug("ServiceLocator " + this + " has been shutdown");
    }
    
    public <T> T create(final Class<T> createMe) {
        return this.create(createMe, null);
    }
    
    public <T> T create(final Class<T> createMe, final String strategy) {
        this.checkState();
        return Utilities.justCreate(createMe, this, strategy);
    }
    
    public void inject(final Object injectMe) {
        this.inject(injectMe, null);
    }
    
    public Object assistedInject(final Object injectMe, final Method method, final MethodParameter... params) {
        return this.assistedInject(injectMe, method, (ServiceHandle<?>)null, params);
    }
    
    public Object assistedInject(final Object injectMe, final Method method, final ServiceHandle<?> root, final MethodParameter... params) {
        this.checkState();
        return Utilities.justAssistedInject(injectMe, method, this, root, params);
    }
    
    public void inject(final Object injectMe, final String strategy) {
        this.checkState();
        Utilities.justInject(injectMe, this, strategy);
    }
    
    public void postConstruct(final Object postConstructMe) {
        this.postConstruct(postConstructMe, null);
    }
    
    public void postConstruct(final Object postConstructMe, final String strategy) {
        this.checkState();
        if (postConstructMe == null) {
            throw new IllegalArgumentException();
        }
        if ((strategy == null || strategy.equals("default")) && postConstructMe instanceof PostConstruct) {
            ((PostConstruct)postConstructMe).postConstruct();
        }
        else {
            Utilities.justPostConstruct(postConstructMe, this, strategy);
        }
    }
    
    public void preDestroy(final Object preDestroyMe) {
        this.preDestroy(preDestroyMe, null);
    }
    
    public void preDestroy(final Object preDestroyMe, final String strategy) {
        this.checkState();
        if (preDestroyMe == null) {
            throw new IllegalArgumentException();
        }
        if ((strategy == null || strategy.equals("default")) && preDestroyMe instanceof PreDestroy) {
            ((PreDestroy)preDestroyMe).preDestroy();
        }
        else {
            Utilities.justPreDestroy(preDestroyMe, this, strategy);
        }
    }
    
    public <U> U createAndInitialize(final Class<U> createMe) {
        return this.createAndInitialize(createMe, null);
    }
    
    public <U> U createAndInitialize(final Class<U> createMe, final String strategy) {
        final U retVal = this.create(createMe, strategy);
        this.inject(retVal, strategy);
        this.postConstruct(retVal, strategy);
        return retVal;
    }
    
    private static String getName(final String name, final Annotation... qualifiers) {
        if (name != null) {
            return name;
        }
        for (final Annotation qualifier : qualifiers) {
            if (qualifier instanceof Named) {
                final Named named = (Named)qualifier;
                if (named.value() != null && !named.value().isEmpty()) {
                    return named.value();
                }
            }
        }
        return null;
    }
    
    private IgdValue igdCacheCompute(final IgdCacheKey key) {
        final List<SystemDescriptor<?>> candidates = this.getDescriptors(key.filter, key.onBehalfOf, true, false, true);
        final ImmediateResults immediate = this.narrow((ServiceLocator)this, candidates, key.contractOrImpl, key.name, key.onBehalfOf, true, true, null, key.filter, key.qualifiers);
        final NarrowResults results = immediate.getTimelessResults();
        if (!results.getErrors().isEmpty()) {
            Utilities.handleErrors(results, new LinkedList<ErrorService>(this.errorHandlers));
            throw new ComputationErrorException((Object)new IgdValue(results, immediate));
        }
        return new IgdValue(results, immediate);
    }
    
    private Unqualified getEffectiveUnqualified(final Unqualified givenUnqualified, final boolean isIterable, final Annotation[] qualifiers) {
        if (givenUnqualified != null) {
            return givenUnqualified;
        }
        if (qualifiers.length > 0) {
            return null;
        }
        if (isIterable) {
            return null;
        }
        return this.defaultUnqualified;
    }
    
    private <T> ActiveDescriptor<T> internalGetDescriptor(final Injectee onBehalfOf, final Type contractOrImpl, final String name, final Unqualified unqualified, final boolean isIterable, final Annotation... qualifiers) throws MultiException {
        return this.internalGetDescriptor(onBehalfOf, contractOrImpl, name, unqualified, isIterable, false, qualifiers);
    }
    
    private <T> ActiveDescriptor<T> internalGetDescriptor(final Injectee onBehalfOf, final Type contractOrImpl, String name, Unqualified unqualified, final boolean isIterable, final boolean calledFromSecondChanceResolveMethod, final Annotation... qualifiers) throws MultiException {
        if (contractOrImpl == null) {
            throw new IllegalArgumentException();
        }
        Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
        if (rawClass == null) {
            return null;
        }
        Utilities.checkLookupType(rawClass);
        rawClass = Utilities.translatePrimitiveType(rawClass);
        name = getName(name, qualifiers);
        NarrowResults results = null;
        LinkedList<ErrorService> currentErrorHandlers = null;
        ImmediateResults immediate = null;
        unqualified = this.getEffectiveUnqualified(unqualified, isIterable, qualifiers);
        final CacheKey cacheKey = new CacheKey(contractOrImpl, name, unqualified, qualifiers);
        final Filter filter = (Filter)new UnqualifiedIndexedFilter(rawClass.getName(), name, unqualified);
        final IgdCacheKey igdCacheKey = new IgdCacheKey(cacheKey, name, onBehalfOf, contractOrImpl, rawClass, qualifiers, filter);
        this.rLock.lock();
        try {
            final IgdValue value = (IgdValue)this.igdCache.compute((Object)igdCacheKey);
            final boolean freshOne = value.freshnessKeeper.compareAndSet(1, 2);
            if (!freshOne) {
                immediate = this.narrow((ServiceLocator)this, null, contractOrImpl, name, onBehalfOf, true, true, value.results, filter, qualifiers);
                results = immediate.getTimelessResults();
            }
            else {
                results = value.results;
                immediate = value.immediate;
            }
            if (!results.getErrors().isEmpty()) {
                currentErrorHandlers = new LinkedList<ErrorService>(this.errorHandlers);
            }
        }
        finally {
            this.rLock.unlock();
        }
        if (currentErrorHandlers != null) {
            Utilities.handleErrors(results, currentErrorHandlers);
        }
        ActiveDescriptor<T> postValidateResult = (ActiveDescriptor<T>)(immediate.getImmediateResults().isEmpty() ? null : ((ActiveDescriptor)immediate.getImmediateResults().get(0)));
        if (!calledFromSecondChanceResolveMethod && postValidateResult == null) {
            Injectee injectee;
            if (onBehalfOf == null) {
                final HashSet<Annotation> requiredQualifiers = new HashSet<Annotation>();
                if (qualifiers != null && qualifiers.length > 0) {
                    for (final Annotation qualifier : qualifiers) {
                        if (qualifier != null) {
                            requiredQualifiers.add(qualifier);
                        }
                    }
                }
                final InjecteeImpl injecteeImpl = new InjecteeImpl(contractOrImpl);
                injecteeImpl.setRequiredQualifiers((Set)requiredQualifiers);
                injecteeImpl.setUnqualified(unqualified);
                injectee = (Injectee)injecteeImpl;
            }
            else {
                injectee = onBehalfOf;
            }
            postValidateResult = (ActiveDescriptor<T>)this.secondChanceResolve(injectee);
        }
        return postValidateResult;
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final Class<T> contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return this.getServiceHandle((Type)contractOrImpl, qualifiers);
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final Type contractOrImpl, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        final ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, null, null, false, qualifiers);
        if (ad == null) {
            return null;
        }
        return this.getServiceHandle(ad, (Injectee)new InjecteeImpl(contractOrImpl));
    }
    
     <T> ServiceHandle<T> getUnqualifiedServiceHandle(final Type contractOrImpl, final Unqualified unqualified, final boolean isIterable, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        final ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, null, unqualified, isIterable, qualifiers);
        if (ad == null) {
            return null;
        }
        return this.getServiceHandle(ad, (Injectee)new InjecteeImpl(contractOrImpl));
    }
    
    private List<ServiceHandle<?>> protectedGetAllServiceHandles(final Type contractOrImpl, final Annotation... qualifiers) {
        return AccessController.doPrivileged((PrivilegedAction<List<ServiceHandle<?>>>)new PrivilegedAction<List<ServiceHandle<?>>>() {
            @Override
            public List<ServiceHandle<?>> run() {
                return ServiceLocatorImpl.this.getAllServiceHandles(contractOrImpl, qualifiers);
            }
        });
    }
    
    public <T> List<ServiceHandle<T>> getAllServiceHandles(final Class<T> contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return (List)ReflectionHelper.cast((Object)this.getAllServiceHandles((Type)contractOrImpl, qualifiers));
    }
    
    public List<ServiceHandle<?>> getAllServiceHandles(final Type contractOrImpl, final Annotation... qualifiers) throws MultiException {
        return (List<ServiceHandle<?>>)this.internalGetAllServiceHandles(contractOrImpl, null, true, false, qualifiers);
    }
    
    List<ServiceHandle<?>> getAllUnqualifiedServiceHandles(final Type contractOrImpl, final Unqualified unqualified, final boolean isIterable, final Annotation... qualifiers) throws MultiException {
        return (List<ServiceHandle<?>>)this.internalGetAllServiceHandles(contractOrImpl, unqualified, true, isIterable, qualifiers);
    }
    
    private List<?> internalGetAllServiceHandles(final Type contractOrImpl, Unqualified unqualified, final boolean getHandles, final boolean isIterable, final Annotation... qualifiers) throws MultiException {
        if (contractOrImpl == null) {
            throw new IllegalArgumentException();
        }
        this.checkState();
        final Class<?> rawClass = ReflectionHelper.getRawClass(contractOrImpl);
        if (rawClass == null) {
            throw new MultiException((Throwable)new IllegalArgumentException("Type must be a class or parameterized type, it was " + contractOrImpl));
        }
        final String name = rawClass.getName();
        NarrowResults results = null;
        LinkedList<ErrorService> currentErrorHandlers = null;
        ImmediateResults immediate = null;
        unqualified = this.getEffectiveUnqualified(unqualified, isIterable, qualifiers);
        final CacheKey cacheKey = new CacheKey(contractOrImpl, null, unqualified, qualifiers);
        final Filter filter = (Filter)new UnqualifiedIndexedFilter(name, (String)null, unqualified);
        final IgdCacheKey igdCacheKey = new IgdCacheKey(cacheKey, name, null, contractOrImpl, rawClass, qualifiers, filter);
        this.rLock.lock();
        try {
            final IgdValue value = (IgdValue)this.igashCache.compute((Object)igdCacheKey);
            final boolean freshOne = value.freshnessKeeper.compareAndSet(1, 2);
            if (!freshOne) {
                immediate = this.narrow((ServiceLocator)this, null, contractOrImpl, null, null, false, true, value.results, filter, qualifiers);
                results = immediate.getTimelessResults();
            }
            else {
                results = value.results;
                immediate = value.immediate;
            }
            if (!results.getErrors().isEmpty()) {
                currentErrorHandlers = new LinkedList<ErrorService>(this.errorHandlers);
            }
        }
        finally {
            this.rLock.unlock();
        }
        if (currentErrorHandlers != null) {
            Utilities.handleErrors(results, currentErrorHandlers);
        }
        final LinkedList<Object> retVal = new LinkedList<Object>();
        for (final ActiveDescriptor<?> candidate : immediate.getImmediateResults()) {
            if (getHandles) {
                retVal.add(this.internalGetServiceHandle(candidate, contractOrImpl, null));
            }
            else {
                final Object service = Utilities.createService(candidate, null, this, null, rawClass);
                retVal.add(service);
            }
        }
        return retVal;
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final Class<T> contractOrImpl, final String name, final Annotation... qualifiers) throws MultiException {
        return this.getServiceHandle((Type)contractOrImpl, name, qualifiers);
    }
    
    public <T> ServiceHandle<T> getServiceHandle(final Type contractOrImpl, final String name, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        final ActiveDescriptor<T> ad = this.internalGetDescriptor(null, contractOrImpl, name, null, false, qualifiers);
        if (ad == null) {
            return null;
        }
        return (ServiceHandle<T>)this.internalGetServiceHandle(ad, contractOrImpl, null);
    }
    
    public List<ServiceHandle<?>> getAllServiceHandles(final Filter searchCriteria) throws MultiException {
        this.checkState();
        LinkedList<ErrorService> currentErrorHandlers = null;
        final List<SystemDescriptor<?>> candidates = (List<SystemDescriptor<?>>)ReflectionHelper.cast((Object)this.getDescriptors(searchCriteria));
        final ImmediateResults immediate = this.narrow((ServiceLocator)this, candidates, null, null, null, false, false, null, searchCriteria, new Annotation[0]);
        final NarrowResults results = immediate.getTimelessResults();
        if (!results.getErrors().isEmpty()) {
            currentErrorHandlers = new LinkedList<ErrorService>(this.errorHandlers);
        }
        if (currentErrorHandlers != null) {
            Utilities.handleErrors(results, currentErrorHandlers);
        }
        final SortedSet<ServiceHandle<?>> retVal = new TreeSet<ServiceHandle<?>>(ServiceLocatorImpl.HANDLE_COMPARATOR);
        for (final ActiveDescriptor<?> candidate : results.getResults()) {
            retVal.add(this.getServiceHandle(candidate));
        }
        return new LinkedList<ServiceHandle<?>>(retVal);
    }
    
    public List<ServiceHandle<?>> getAllServiceHandles(final Annotation qualifier, final Annotation... qualifiers) throws MultiException {
        this.checkState();
        if (qualifier == null) {
            throw new IllegalArgumentException("qualifier is null");
        }
        final Set<String> allQualifiers = new LinkedHashSet<String>();
        allQualifiers.add(qualifier.annotationType().getName());
        for (final Annotation anno : qualifiers) {
            final String addMe = anno.annotationType().getName();
            if (allQualifiers.contains(addMe)) {
                throw new IllegalArgumentException("Multiple qualifiers with name " + addMe);
            }
            allQualifiers.add(addMe);
        }
        return this.getAllServiceHandles((Filter)new Filter() {
            public boolean matches(final Descriptor d) {
                return d.getQualifiers().containsAll(allQualifiers);
            }
        });
    }
    
    List<InterceptionService> getInterceptionServices() {
        if (!this.hasInterceptionServices) {
            return null;
        }
        this.rLock.lock();
        try {
            return new LinkedList<InterceptionService>(this.interceptionServices);
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    private CheckConfigurationData checkConfiguration(final DynamicConfigurationImpl dci) {
        final List<SystemDescriptor<?>> retVal = new LinkedList<SystemDescriptor<?>>();
        boolean addOrRemoveOfInstanceListener = false;
        boolean addOrRemoveOfInjectionResolver = false;
        boolean addOrRemoveOfErrorHandler = false;
        boolean addOrRemoveOfClazzAnalyzer = false;
        boolean addOrRemoveOfConfigListener = false;
        boolean addOrRemoveOfInterceptionService = false;
        final HashSet<String> affectedContracts = new HashSet<String>();
        final TwoPhaseTransactionDataImpl transactionData = new TwoPhaseTransactionDataImpl();
        for (final Filter unbindFilter : dci.getUnbindFilters()) {
            final List<SystemDescriptor<?>> results = this.getDescriptors(unbindFilter, null, false, false, true);
            for (final SystemDescriptor<?> candidate : results) {
                affectedContracts.addAll((Collection<?>)getAllContracts((ActiveDescriptor<?>)candidate));
                if (retVal.contains(candidate)) {
                    continue;
                }
                for (final ValidationService vs : this.getAllValidators()) {
                    if (!this.callValidate(vs, (ValidationInformation)new ValidationInformationImpl(Operation.UNBIND, (ActiveDescriptor<?>)candidate))) {
                        throw new MultiException((Throwable)new IllegalArgumentException("Descriptor " + candidate + " did not pass the UNBIND validation"));
                    }
                }
                if (candidate.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
                    addOrRemoveOfInstanceListener = true;
                }
                if (candidate.getAdvertisedContracts().contains(InjectionResolver.class.getName())) {
                    addOrRemoveOfInjectionResolver = true;
                }
                if (candidate.getAdvertisedContracts().contains(ErrorService.class.getName())) {
                    addOrRemoveOfErrorHandler = true;
                }
                if (candidate.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
                    addOrRemoveOfClazzAnalyzer = true;
                }
                if (candidate.getAdvertisedContracts().contains(DynamicConfigurationListener.class.getName())) {
                    addOrRemoveOfConfigListener = true;
                }
                if (candidate.getAdvertisedContracts().contains(InterceptionService.class.getName())) {
                    addOrRemoveOfInterceptionService = true;
                }
                retVal.add(candidate);
                transactionData.toRemove((ActiveDescriptor<?>)candidate);
            }
        }
        for (final SystemDescriptor<?> sd : dci.getAllDescriptors()) {
            transactionData.toAdd((ActiveDescriptor<?>)sd);
            affectedContracts.addAll((Collection<?>)getAllContracts((ActiveDescriptor<?>)sd));
            boolean checkScope = false;
            if (sd.getAdvertisedContracts().contains(ValidationService.class.getName()) || sd.getAdvertisedContracts().contains(ErrorService.class.getName()) || sd.getAdvertisedContracts().contains(InterceptionService.class.getName()) || sd.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
                this.reifyDescriptor((Descriptor)sd);
                checkScope = true;
                if (sd.getAdvertisedContracts().contains(ErrorService.class.getName())) {
                    addOrRemoveOfErrorHandler = true;
                }
                if (sd.getAdvertisedContracts().contains(InstanceLifecycleListener.class.getName())) {
                    addOrRemoveOfInstanceListener = true;
                }
                if (sd.getAdvertisedContracts().contains(InterceptionService.class.getName())) {
                    addOrRemoveOfInterceptionService = true;
                }
            }
            if (sd.getAdvertisedContracts().contains(InjectionResolver.class.getName())) {
                this.reifyDescriptor((Descriptor)sd);
                checkScope = true;
                if (Utilities.getInjectionResolverType((ActiveDescriptor<?>)sd) == null) {
                    throw new MultiException((Throwable)new IllegalArgumentException("An implementation of InjectionResolver must be a parameterized type and the actual type must be an annotation"));
                }
                addOrRemoveOfInjectionResolver = true;
            }
            if (sd.getAdvertisedContracts().contains(DynamicConfigurationListener.class.getName())) {
                this.reifyDescriptor((Descriptor)sd);
                checkScope = true;
                addOrRemoveOfConfigListener = true;
            }
            if (sd.getAdvertisedContracts().contains(Context.class.getName())) {
                checkScope = true;
            }
            if (sd.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
                addOrRemoveOfClazzAnalyzer = true;
            }
            if (checkScope) {
                final String scope = (sd.getScope() == null) ? PerLookup.class.getName() : sd.getScope();
                if (!scope.equals(Singleton.class.getName())) {
                    throw new MultiException((Throwable)new IllegalArgumentException("The implementation class " + sd.getImplementation() + " must be in the Singleton scope"));
                }
            }
            for (final ValidationService vs2 : this.getAllValidators()) {
                final Validator validator = vs2.getValidator();
                if (validator == null) {
                    throw new MultiException((Throwable)new IllegalArgumentException("Validator was null from validation service" + vs2));
                }
                if (!this.callValidate(vs2, (ValidationInformation)new ValidationInformationImpl(Operation.BIND, (ActiveDescriptor<?>)sd))) {
                    throw new MultiException((Throwable)new IllegalArgumentException("Descriptor " + sd + " did not pass the BIND validation"));
                }
            }
        }
        final List<Filter> idempotentFilters = dci.getIdempotentFilters();
        if (!idempotentFilters.isEmpty()) {
            final List<ActiveDescriptor<?>> allValidatedDescriptors = this.getDescriptors(BuilderHelper.allFilter());
            final List<Throwable> idempotentFailures = new LinkedList<Throwable>();
            for (final ActiveDescriptor<?> aValidatedDescriptor : allValidatedDescriptors) {
                for (final Filter idempotentFilter : idempotentFilters) {
                    if (BuilderHelper.filterMatches((Descriptor)aValidatedDescriptor, idempotentFilter)) {
                        idempotentFailures.add((Throwable)new DuplicateServiceException((Descriptor)aValidatedDescriptor));
                    }
                }
            }
            if (!idempotentFailures.isEmpty()) {
                throw new MultiException((List)idempotentFailures);
            }
        }
        final LinkedList<TwoPhaseResource> resources = dci.getResources();
        final List<TwoPhaseResource> completedPrepares = new LinkedList<TwoPhaseResource>();
        for (final TwoPhaseResource resource : resources) {
            try {
                resource.prepareDynamicConfiguration((TwoPhaseTransactionData)transactionData);
                completedPrepares.add(resource);
            }
            catch (final Throwable th) {
                for (final TwoPhaseResource rollMe : completedPrepares) {
                    try {
                        rollMe.rollbackDynamicConfiguration((TwoPhaseTransactionData)transactionData);
                    }
                    catch (final Throwable ignore) {
                        Logger.getLogger().debug("Rollback of TwoPhaseResource " + resource + " failed with exception", ignore);
                    }
                }
                if (th instanceof RuntimeException) {
                    throw (RuntimeException)th;
                }
                throw new RuntimeException(th);
            }
        }
        return new CheckConfigurationData((List)retVal, addOrRemoveOfInstanceListener, addOrRemoveOfInjectionResolver, addOrRemoveOfErrorHandler, addOrRemoveOfClazzAnalyzer, addOrRemoveOfConfigListener, (HashSet)affectedContracts, addOrRemoveOfInterceptionService, (TwoPhaseTransactionData)transactionData);
    }
    
    private static List<String> getAllContracts(final ActiveDescriptor<?> desc) {
        final LinkedList<String> allContracts = new LinkedList<String>(desc.getAdvertisedContracts());
        allContracts.addAll(desc.getQualifiers());
        final String scope = (desc.getScope() == null) ? PerLookup.class.getName() : desc.getScope();
        allContracts.add(scope);
        return allContracts;
    }
    
    private void removeConfigurationInternal(final List<SystemDescriptor<?>> unbinds) {
        for (final SystemDescriptor<?> unbind : unbinds) {
            if (ServiceLocatorImpl.BIND_TRACING_PATTERN != null && doTrace((ActiveDescriptor<?>)unbind)) {
                Logger.getLogger().debug("HK2 Bind Tracing: Removing Descriptor " + unbind);
                if (ServiceLocatorImpl.BIND_TRACING_STACKS) {
                    Logger.getLogger().debug("ServiceLocatorImpl", "removeConfigurationInternal", new Throwable());
                }
            }
            this.allDescriptors.removeDescriptor(unbind);
            for (final String advertisedContract : getAllContracts((ActiveDescriptor<?>)unbind)) {
                final IndexedListData ild = this.descriptorsByAdvertisedContract.get(advertisedContract);
                if (ild == null) {
                    continue;
                }
                ild.removeDescriptor(unbind);
                if (!ild.isEmpty()) {
                    continue;
                }
                this.descriptorsByAdvertisedContract.remove(advertisedContract);
            }
            final String unbindName = unbind.getName();
            if (unbindName != null) {
                final IndexedListData ild2 = this.descriptorsByName.get(unbindName);
                if (ild2 != null) {
                    ild2.removeDescriptor(unbind);
                    if (ild2.isEmpty()) {
                        this.descriptorsByName.remove(unbindName);
                    }
                }
            }
            if (unbind.getAdvertisedContracts().contains(ValidationService.class.getName())) {
                final ServiceHandle<ValidationService> handle = this.getServiceHandle((org.glassfish.hk2.api.ActiveDescriptor<ValidationService>)unbind);
                final ValidationService vs = (ValidationService)handle.getService();
                this.allValidators.remove(vs);
            }
            if (unbind.isReified()) {
                for (final Injectee injectee : unbind.getInjectees()) {
                    if (injectee instanceof SystemInjecteeImpl) {
                        this.injecteeToResolverCache.remove((Object)injectee);
                    }
                }
                this.classReflectionHelper.clean((Class)unbind.getImplementationClass());
            }
        }
        boolean hasOneUnbind = false;
        for (final SystemDescriptor<?> unbind2 : unbinds) {
            hasOneUnbind = true;
            unbind2.close();
        }
        if (hasOneUnbind) {
            this.perLocatorUtilities.releaseCaches();
        }
    }
    
    private static boolean doTrace(final ActiveDescriptor<?> desc) {
        if (ServiceLocatorImpl.BIND_TRACING_PATTERN == null) {
            return false;
        }
        if ("*".equals(ServiceLocatorImpl.BIND_TRACING_PATTERN)) {
            return true;
        }
        if (desc.getImplementation() == null) {
            return true;
        }
        final StringTokenizer st = new StringTokenizer(ServiceLocatorImpl.BIND_TRACING_PATTERN, "|");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (desc.getImplementation().contains(token)) {
                return true;
            }
            for (final String contract : desc.getAdvertisedContracts()) {
                if (contract.contains(token)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private List<SystemDescriptor<?>> addConfigurationInternal(final DynamicConfigurationImpl dci) {
        final List<SystemDescriptor<?>> thingsAdded = new LinkedList<SystemDescriptor<?>>();
        for (final SystemDescriptor<?> sd : dci.getAllDescriptors()) {
            if (ServiceLocatorImpl.BIND_TRACING_PATTERN != null && doTrace((ActiveDescriptor<?>)sd)) {
                Logger.getLogger().debug("HK2 Bind Tracing: Adding Descriptor " + sd);
                if (ServiceLocatorImpl.BIND_TRACING_STACKS) {
                    Logger.getLogger().debug("ServiceLocatorImpl", "addConfigurationInternal", new Throwable());
                }
            }
            thingsAdded.add(sd);
            this.allDescriptors.addDescriptor(sd);
            final List<String> allContracts = getAllContracts((ActiveDescriptor<?>)sd);
            for (final String advertisedContract : allContracts) {
                IndexedListData ild = this.descriptorsByAdvertisedContract.get(advertisedContract);
                if (ild == null) {
                    ild = new IndexedListData();
                    this.descriptorsByAdvertisedContract.put(advertisedContract, ild);
                }
                ild.addDescriptor(sd);
            }
            if (sd.getName() != null) {
                final String name = sd.getName();
                IndexedListData ild2 = this.descriptorsByName.get(name);
                if (ild2 == null) {
                    ild2 = new IndexedListData();
                    this.descriptorsByName.put(name, ild2);
                }
                ild2.addDescriptor(sd);
            }
            if (sd.getAdvertisedContracts().contains(ValidationService.class.getName())) {
                final ServiceHandle<ValidationService> handle = this.getServiceHandle((org.glassfish.hk2.api.ActiveDescriptor<ValidationService>)sd);
                final ValidationService vs = (ValidationService)handle.getService();
                this.allValidators.add(vs);
            }
        }
        return thingsAdded;
    }
    
    private void reupInjectionResolvers() {
        final HashMap<Class<? extends Annotation>, InjectionResolver<?>> newResolvers = new HashMap<Class<? extends Annotation>, InjectionResolver<?>>();
        final Filter injectionResolverFilter = (Filter)BuilderHelper.createContractFilter(InjectionResolver.class.getName());
        final List<ActiveDescriptor<?>> resolverDescriptors = this.protectedGetDescriptors(injectionResolverFilter);
        for (final ActiveDescriptor<?> resolverDescriptor : resolverDescriptors) {
            final Class<? extends Annotation> iResolve = Utilities.getInjectionResolverType(resolverDescriptor);
            if (iResolve != null && !newResolvers.containsKey(iResolve)) {
                final InjectionResolver<?> resolver = (InjectionResolver<?>)this.getServiceHandle(resolverDescriptor).getService();
                newResolvers.put(iResolve, resolver);
            }
        }
        synchronized (this.allResolvers) {
            this.allResolvers.clear();
            this.allResolvers.putAll(newResolvers);
        }
        this.injecteeToResolverCache.clear();
    }
    
    private void reupInterceptionServices() {
        final List<InterceptionService> allInterceptionServices = this.protectedGetAllServices(InterceptionService.class, new Annotation[0]);
        this.interceptionServices.clear();
        this.interceptionServices.addAll(allInterceptionServices);
        this.hasInterceptionServices = !this.interceptionServices.isEmpty();
    }
    
    private void reupErrorHandlers() {
        final List<ErrorService> allErrorServices = this.protectedGetAllServices(ErrorService.class, new Annotation[0]);
        this.errorHandlers.clear();
        this.errorHandlers.addAll(allErrorServices);
    }
    
    private void reupConfigListeners() {
        final List<ServiceHandle<?>> allConfigListeners = this.protectedGetAllServiceHandles(DynamicConfigurationListener.class, new Annotation[0]);
        this.configListeners.clear();
        this.configListeners.addAll(allConfigListeners);
    }
    
    private void reupInstanceListenersHandlers(final Collection<SystemDescriptor<?>> checkList) {
        final List<InstanceLifecycleListener> allLifecycleListeners = this.protectedGetAllServices(InstanceLifecycleListener.class, new Annotation[0]);
        for (final SystemDescriptor<?> descriptor : checkList) {
            descriptor.reupInstanceListeners(allLifecycleListeners);
        }
    }
    
    private void reupClassAnalyzers() {
        final List<ServiceHandle<?>> allAnalyzers = this.protectedGetAllServiceHandles(ClassAnalyzer.class, new Annotation[0]);
        synchronized (this.classAnalyzerLock) {
            this.classAnalyzers.clear();
            for (final ServiceHandle<?> handle : allAnalyzers) {
                final ActiveDescriptor<?> descriptor = (ActiveDescriptor<?>)handle.getActiveDescriptor();
                final String name = descriptor.getName();
                if (name == null) {
                    continue;
                }
                final ClassAnalyzer created = (ClassAnalyzer)handle.getService();
                if (created == null) {
                    continue;
                }
                this.classAnalyzers.put(name, created);
            }
        }
    }
    
    private void reupCache(final HashSet<String> affectedContracts) {
        this.wLock.lock();
        try {
            for (final String fAffectedContract : affectedContracts) {
                final String affectedContract = fAffectedContract;
                final CacheKeyFilter<IgdCacheKey> cacheKeyFilter = (CacheKeyFilter<IgdCacheKey>)new CacheKeyFilter<IgdCacheKey>() {
                    public boolean matches(final IgdCacheKey key) {
                        return key.cacheKey.matchesRemovalName(fAffectedContract);
                    }
                };
                this.igdCache.releaseMatching((CacheKeyFilter)cacheKeyFilter);
                this.igashCache.releaseMatching((CacheKeyFilter)cacheKeyFilter);
            }
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    private void reup(final List<SystemDescriptor<?>> thingsAdded, final boolean instanceListenersModified, final boolean injectionResolversModified, final boolean errorHandlersModified, final boolean classAnalyzersModified, final boolean dynamicConfigurationListenersModified, final HashSet<String> affectedContracts, final boolean interceptionServicesModified) {
        this.reupCache(affectedContracts);
        if (injectionResolversModified) {
            this.reupInjectionResolvers();
        }
        if (errorHandlersModified) {
            this.reupErrorHandlers();
        }
        if (dynamicConfigurationListenersModified) {
            this.reupConfigListeners();
        }
        if (instanceListenersModified) {
            this.reupInstanceListenersHandlers(this.allDescriptors.getSortedList());
        }
        else {
            this.reupInstanceListenersHandlers(thingsAdded);
        }
        if (classAnalyzersModified) {
            this.reupClassAnalyzers();
        }
        if (interceptionServicesModified) {
            this.reupInterceptionServices();
        }
        this.contextCache.clear();
    }
    
    private void getAllChildren(final LinkedList<ServiceLocatorImpl> allMyChildren) {
        final LinkedList<ServiceLocatorImpl> addMe;
        synchronized (this.children) {
            addMe = new LinkedList<ServiceLocatorImpl>(this.children.keySet());
        }
        allMyChildren.addAll(addMe);
        for (final ServiceLocatorImpl sli : addMe) {
            sli.getAllChildren(allMyChildren);
        }
    }
    
    private void callAllConfigurationListeners(final List<ServiceHandle<?>> allListeners) {
        if (allListeners == null) {
            return;
        }
        for (final ServiceHandle<?> listener : allListeners) {
            final ActiveDescriptor<?> listenerDescriptor = (ActiveDescriptor<?>)listener.getActiveDescriptor();
            if (listenerDescriptor.getLocatorId() != this.id) {
                continue;
            }
            try {
                ((DynamicConfigurationListener)listener.getService()).configurationChanged();
            }
            catch (final Throwable t) {}
        }
    }
    
    void addConfiguration(final DynamicConfigurationImpl dci) {
        List<ServiceHandle<?>> allConfigurationListeners = null;
        MultiException configurationError = null;
        this.wLock.lock();
        CheckConfigurationData checkData;
        try {
            checkData = this.checkConfiguration(dci);
            this.removeConfigurationInternal(checkData.getUnbinds());
            final List<SystemDescriptor<?>> thingsAdded = this.addConfigurationInternal(dci);
            this.reup(thingsAdded, checkData.getInstanceLifecycleModificationsMade(), checkData.getInjectionResolverModificationMade(), checkData.getErrorHandlerModificationMade(), checkData.getClassAnalyzerModificationMade(), checkData.getDynamicConfigurationListenerModificationMade(), checkData.getAffectedContracts(), checkData.getInterceptionServiceModificationMade());
            allConfigurationListeners = new LinkedList<ServiceHandle<?>>(this.configListeners);
        }
        catch (final MultiException me) {
            configurationError = me;
            throw me;
        }
        finally {
            List<ErrorService> errorServices = null;
            if (configurationError != null) {
                errorServices = new LinkedList<ErrorService>(this.errorHandlers);
            }
            this.wLock.unlock();
            if (errorServices != null && !errorServices.isEmpty()) {
                for (final ErrorService errorService : errorServices) {
                    try {
                        errorService.onFailure((ErrorInformation)new ErrorInformationImpl(ErrorType.DYNAMIC_CONFIGURATION_FAILURE, null, null, configurationError));
                    }
                    catch (final Throwable t) {}
                }
            }
        }
        final LinkedList<ServiceLocatorImpl> allMyChildren = new LinkedList<ServiceLocatorImpl>();
        this.getAllChildren(allMyChildren);
        for (final ServiceLocatorImpl sli : allMyChildren) {
            sli.reupCache(checkData.getAffectedContracts());
        }
        this.callAllConfigurationListeners(allConfigurationListeners);
        final LinkedList<TwoPhaseResource> resources = dci.getResources();
        for (final TwoPhaseResource resource : resources) {
            try {
                resource.activateDynamicConfiguration(checkData.getTransactionData());
            }
            catch (final Throwable ignore) {
                Logger.getLogger().debug("Activate of TwoPhaseResource " + resource + " failed with exception", ignore);
            }
        }
    }
    
    boolean isInjectAnnotation(final Annotation annotation) {
        return this.allResolvers.containsKey(annotation.annotationType());
    }
    
    boolean isInjectAnnotation(final Annotation annotation, final boolean isConstructor) {
        final InjectionResolver<?> resolver = this.allResolvers.get(annotation.annotationType());
        if (resolver == null) {
            return false;
        }
        if (isConstructor) {
            return resolver.isConstructorParameterIndicator();
        }
        return resolver.isMethodParameterIndicator();
    }
    
    InjectionResolver<?> getInjectionResolver(final Class<? extends Annotation> annoType) {
        return this.allResolvers.get(annoType);
    }
    
    private Context<?> _resolveContext(final Class<? extends Annotation> scope) throws IllegalStateException {
        Context<?> retVal = null;
        final Type[] actuals = { scope };
        final ParameterizedType findContext = (ParameterizedType)new ParameterizedTypeImpl((Type)Context.class, actuals);
        final List<ServiceHandle<Context<?>>> contextHandles = (List<ServiceHandle<Context<?>>>)ReflectionHelper.cast((Object)this.protectedGetAllServiceHandles(findContext, new Annotation[0]));
        for (final ServiceHandle<Context<?>> contextHandle : contextHandles) {
            final Context<?> context = (Context<?>)contextHandle.getService();
            if (!context.isActive()) {
                continue;
            }
            if (retVal != null) {
                throw new IllegalStateException("There is more than one active context for " + scope.getName());
            }
            retVal = context;
        }
        if (retVal == null) {
            throw new IllegalStateException("Could not find an active context for " + scope.getName());
        }
        return retVal;
    }
    
    Context<?> resolveContext(final Class<? extends Annotation> scope) throws IllegalStateException {
        if (scope.equals(Singleton.class)) {
            return this.singletonContext;
        }
        if (scope.equals(PerLookup.class)) {
            return this.perLookupContext;
        }
        final Context<?> retVal = (Context<?>)this.contextCache.compute((Object)scope);
        if (retVal.isActive()) {
            return retVal;
        }
        this.contextCache.remove((Object)scope);
        return (Context<?>)this.contextCache.compute((Object)scope);
    }
    
    private Class<?> loadClass(final Descriptor descriptor, final Injectee injectee) {
        if (descriptor == null) {
            throw new IllegalArgumentException();
        }
        final HK2Loader loader = descriptor.getLoader();
        if (loader == null) {
            return Utilities.loadClass(descriptor.getImplementation(), injectee);
        }
        Class<?> retVal;
        try {
            retVal = loader.loadClass(descriptor.getImplementation());
        }
        catch (final MultiException me) {
            me.addError((Throwable)new IllegalStateException("Could not load descriptor " + descriptor));
            throw me;
        }
        catch (final Throwable th) {
            final MultiException me2 = new MultiException(th);
            me2.addError((Throwable)new IllegalStateException("Could not load descriptor " + descriptor));
            throw me2;
        }
        return retVal;
    }
    
    private ImmediateResults narrow(final ServiceLocator locator, final List<SystemDescriptor<?>> candidates, Type requiredType, final String name, final Injectee injectee, final boolean onlyOne, final boolean doValidation, NarrowResults cachedResults, final Filter filter, final Annotation... qualifiers) {
        final ImmediateResults retVal = new ImmediateResults(cachedResults);
        cachedResults = retVal.getTimelessResults();
        if (candidates != null) {
            final List<ActiveDescriptor<?>> lCandidates = (List<ActiveDescriptor<?>>)ReflectionHelper.cast((Object)candidates);
            cachedResults.setUnnarrowedResults(lCandidates);
        }
        final Set<Annotation> requiredAnnotations = Utilities.fixAndCheckQualifiers(qualifiers, name);
        for (final ActiveDescriptor<?> previousResult : cachedResults.getResults()) {
            if (doValidation && !this.validate((SystemDescriptor)previousResult, injectee, filter)) {
                continue;
            }
            retVal.addValidatedResult(previousResult);
            if (onlyOne) {
                return retVal;
            }
        }
        if (requiredType != null && requiredType instanceof Class && ((Class)requiredType).isAnnotation()) {
            requiredType = null;
        }
        ActiveDescriptor<?> candidate;
        while ((candidate = cachedResults.removeUnnarrowedResult()) != null) {
            boolean doReify = false;
            if ((requiredType != null || !requiredAnnotations.isEmpty()) && !candidate.isReified()) {
                doReify = true;
            }
            if (doReify) {
                try {
                    candidate = (ActiveDescriptor<?>)locator.reifyDescriptor((Descriptor)candidate, injectee);
                }
                catch (final MultiException me) {
                    cachedResults.addError(candidate, injectee, me);
                    continue;
                }
                catch (final Throwable th) {
                    cachedResults.addError(candidate, injectee, new MultiException(th));
                    continue;
                }
            }
            if (requiredType != null) {
                boolean safe = false;
                for (final Type candidateType : candidate.getContractTypes()) {
                    if (Utilities.isTypeSafe(requiredType, candidateType)) {
                        safe = true;
                        break;
                    }
                }
                if (!safe) {
                    continue;
                }
            }
            if (!requiredAnnotations.isEmpty()) {
                final Set<Annotation> candidateAnnotations = candidate.getQualifierAnnotations();
                if (!ReflectionHelper.annotationContainsAll((Set)candidateAnnotations, (Set)requiredAnnotations)) {
                    continue;
                }
            }
            cachedResults.addGoodResult(candidate);
            if (doValidation && !this.validate((SystemDescriptor)candidate, injectee, filter)) {
                continue;
            }
            retVal.addValidatedResult(candidate);
            if (onlyOne) {
                return retVal;
            }
        }
        return retVal;
    }
    
    public long getLocatorId() {
        return this.id;
    }
    
    long getNextServiceId() {
        return this.nextServiceId.getAndIncrement();
    }
    
    private void addChild(final ServiceLocatorImpl child) {
        synchronized (this.children) {
            this.children.put(child, null);
        }
    }
    
    private void removeChild(final ServiceLocatorImpl child) {
        synchronized (this.children) {
            this.children.remove(child);
        }
    }
    
    private void checkState() {
        if (ServiceLocatorState.SHUTDOWN.equals((Object)this.state)) {
            throw new IllegalStateException(this + " has been shut down");
        }
    }
    
    private LinkedHashSet<ValidationService> getAllValidators() {
        if (this.parent == null) {
            return this.allValidators;
        }
        final LinkedHashSet<ValidationService> retVal = new LinkedHashSet<ValidationService>();
        retVal.addAll((Collection<?>)this.parent.getAllValidators());
        retVal.addAll((Collection<?>)this.allValidators);
        return retVal;
    }
    
    public String getDefaultClassAnalyzerName() {
        synchronized (this.classAnalyzerLock) {
            return this.defaultClassAnalyzer;
        }
    }
    
    public void setDefaultClassAnalyzerName(final String defaultClassAnalyzer) {
        synchronized (this.classAnalyzerLock) {
            if (defaultClassAnalyzer == null) {
                this.defaultClassAnalyzer = "default";
            }
            else {
                this.defaultClassAnalyzer = defaultClassAnalyzer;
            }
        }
    }
    
    public Unqualified getDefaultUnqualified() {
        this.rLock.lock();
        try {
            return this.defaultUnqualified;
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    public void setDefaultUnqualified(final Unqualified unqualified) {
        this.wLock.lock();
        try {
            this.defaultUnqualified = unqualified;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    ClassAnalyzer getAnalyzer(String name, final Collector collector) {
        final ClassAnalyzer retVal;
        synchronized (this.classAnalyzerLock) {
            if (name == null) {
                name = this.defaultClassAnalyzer;
            }
            retVal = this.classAnalyzers.get(name);
        }
        if (retVal == null) {
            collector.addThrowable(new IllegalStateException("Could not find an implementation of ClassAnalyzer with name " + name));
            return null;
        }
        return retVal;
    }
    
    public ServiceLocator getParent() {
        return (ServiceLocator)this.parent;
    }
    
    public boolean getNeutralContextClassLoader() {
        return this.neutralContextClassLoader;
    }
    
    public void setNeutralContextClassLoader(final boolean neutralContextClassLoader) {
        this.wLock.lock();
        try {
            this.neutralContextClassLoader = neutralContextClassLoader;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    private ServiceLocatorImpl getMe() {
        return this;
    }
    
    boolean hasInjectAnnotation(final AnnotatedElement annotated) {
        return this.perLocatorUtilities.hasInjectAnnotation(annotated);
    }
    
    InjectionResolver<?> getInjectionResolverForInjectee(final SystemInjecteeImpl injectee) {
        return (InjectionResolver<?>)this.injecteeToResolverCache.compute((Object)injectee);
    }
    
    ClassReflectionHelper getClassReflectionHelper() {
        return this.classReflectionHelper;
    }
    
    LinkedList<ErrorService> getErrorHandlers() {
        this.rLock.lock();
        try {
            return new LinkedList<ErrorService>(this.errorHandlers);
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    PerLocatorUtilities getPerLocatorUtilities() {
        return this.perLocatorUtilities;
    }
    
    int getNumberOfDescriptors() {
        this.rLock.lock();
        try {
            return this.allDescriptors.size();
        }
        finally {
            this.rLock.unlock();
        }
    }
    
    int getNumberOfChildren() {
        return this.children.size();
    }
    
    int getServiceCacheSize() {
        return this.igdCache.getValueSize();
    }
    
    int getServiceCacheMaximumSize() {
        return this.igdCache.getMaxSize();
    }
    
    void clearServiceCache() {
        this.igdCache.clear();
    }
    
    int getReflectionCacheSize() {
        return this.classReflectionHelper.size();
    }
    
    void clearReflectionCache() {
        this.wLock.lock();
        try {
            this.classReflectionHelper.dispose();
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    int unsortIndexes(final int newRank, final SystemDescriptor<?> desc, final Set<IndexedListData> myLists) {
        this.wLock.lock();
        try {
            final int retVal = desc.setRankWithLock(newRank);
            for (final IndexedListData myList : myLists) {
                myList.unSort();
            }
            return retVal;
        }
        finally {
            this.wLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return "ServiceLocatorImpl(" + this.locatorName + "," + this.id + "," + System.identityHashCode(this) + ")";
    }
    
    static {
        BIND_TRACING_PATTERN = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("org.jvnet.hk2.properties.bind.tracing.pattern");
            }
        });
        ServiceLocatorImpl.BIND_TRACING_STACKS = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.properties.bind.tracing.stacks", "false"));
            }
        });
        sLock = new Object();
        ServiceLocatorImpl.currentLocatorId = 0L;
        DESCRIPTOR_COMPARATOR = new DescriptorComparator();
        HANDLE_COMPARATOR = new ServiceHandleComparator();
    }
    
    private static final class IgdCacheKey
    {
        private final CacheKey cacheKey;
        private final String name;
        private final Injectee onBehalfOf;
        private final Type contractOrImpl;
        private final Annotation[] qualifiers;
        private final Filter filter;
        private final int hashCode;
        
        IgdCacheKey(final CacheKey key, final String name, final Injectee onBehalfOf, final Type contractOrImpl, final Class<?> rawClass, final Annotation[] qualifiers, final Filter filter) {
            this.cacheKey = key;
            this.name = name;
            this.onBehalfOf = onBehalfOf;
            this.contractOrImpl = contractOrImpl;
            this.qualifiers = qualifiers;
            this.filter = filter;
            int hash = 5;
            hash = 41 * hash + this.cacheKey.hashCode();
            this.hashCode = hash;
        }
        
        @Override
        public int hashCode() {
            return this.hashCode;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof IgdCacheKey)) {
                return false;
            }
            final IgdCacheKey other = (IgdCacheKey)obj;
            if (this.hashCode != other.hashCode) {
                return false;
            }
            if (this.cacheKey == null) {
                if (other.cacheKey == null) {
                    return true;
                }
            }
            else if (this.cacheKey.equals(other.cacheKey)) {
                return true;
            }
            return false;
        }
        
        @Override
        public String toString() {
            return "IgdCacheKey(" + this.cacheKey + "," + this.name + "," + this.onBehalfOf + "," + this.contractOrImpl + "," + Arrays.toString(this.qualifiers) + "," + this.filter + "," + this.filter + "," + System.identityHashCode(this) + ")";
        }
    }
    
    private class IgdValue
    {
        final NarrowResults results;
        final ImmediateResults immediate;
        final AtomicInteger freshnessKeeper;
        
        public IgdValue(final NarrowResults results, final ImmediateResults immediate) {
            this.freshnessKeeper = new AtomicInteger(1);
            this.results = results;
            this.immediate = immediate;
        }
    }
    
    private static class CheckConfigurationData
    {
        private final List<SystemDescriptor<?>> unbinds;
        private final boolean instanceLifeycleModificationMade;
        private final boolean injectionResolverModificationMade;
        private final boolean errorHandlerModificationMade;
        private final boolean classAnalyzerModificationMade;
        private final boolean dynamicConfigurationListenerModificationMade;
        private final HashSet<String> affectedContracts;
        private final boolean interceptionServiceModificationMade;
        private final TwoPhaseTransactionData transactionData;
        
        private CheckConfigurationData(final List<SystemDescriptor<?>> unbinds, final boolean instanceLifecycleModificationMade, final boolean injectionResolverModificationMade, final boolean errorHandlerModificationMade, final boolean classAnalyzerModificationMade, final boolean dynamicConfigurationListenerModificationMade, final HashSet<String> affectedContracts, final boolean interceptionServiceModificationMade, final TwoPhaseTransactionData transactionData) {
            this.unbinds = unbinds;
            this.instanceLifeycleModificationMade = instanceLifecycleModificationMade;
            this.injectionResolverModificationMade = injectionResolverModificationMade;
            this.errorHandlerModificationMade = errorHandlerModificationMade;
            this.classAnalyzerModificationMade = classAnalyzerModificationMade;
            this.dynamicConfigurationListenerModificationMade = dynamicConfigurationListenerModificationMade;
            this.affectedContracts = affectedContracts;
            this.interceptionServiceModificationMade = interceptionServiceModificationMade;
            this.transactionData = transactionData;
        }
        
        private List<SystemDescriptor<?>> getUnbinds() {
            return this.unbinds;
        }
        
        private boolean getInstanceLifecycleModificationsMade() {
            return this.instanceLifeycleModificationMade;
        }
        
        private boolean getInjectionResolverModificationMade() {
            return this.injectionResolverModificationMade;
        }
        
        private boolean getErrorHandlerModificationMade() {
            return this.errorHandlerModificationMade;
        }
        
        private boolean getClassAnalyzerModificationMade() {
            return this.classAnalyzerModificationMade;
        }
        
        private boolean getDynamicConfigurationListenerModificationMade() {
            return this.dynamicConfigurationListenerModificationMade;
        }
        
        private HashSet<String> getAffectedContracts() {
            return this.affectedContracts;
        }
        
        private boolean getInterceptionServiceModificationMade() {
            return this.interceptionServiceModificationMade;
        }
        
        private TwoPhaseTransactionData getTransactionData() {
            return this.transactionData;
        }
    }
    
    private static class UnqualifiedIndexedFilter implements IndexedFilter
    {
        private final String contract;
        private final String name;
        private final Unqualified unqualified;
        
        private UnqualifiedIndexedFilter(final String contract, final String name, final Unqualified unqualified) {
            this.contract = contract;
            this.name = name;
            this.unqualified = unqualified;
        }
        
        public boolean matches(final Descriptor d) {
            if (this.unqualified == null) {
                return true;
            }
            final Class<? extends Annotation>[] unqualifiedAnnos = this.unqualified.value();
            if (unqualifiedAnnos.length <= 0) {
                return d.getQualifiers().isEmpty();
            }
            final Set<String> notAllowed = new HashSet<String>();
            for (final Class<? extends Annotation> notMe : unqualifiedAnnos) {
                notAllowed.add(notMe.getName());
            }
            for (final String qualifier : d.getQualifiers()) {
                if (notAllowed.contains(qualifier)) {
                    return false;
                }
            }
            return true;
        }
        
        public String getAdvertisedContract() {
            return this.contract;
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public String toString() {
            return "UnqualifiedIndexFilter(" + this.contract + "," + this.name + "," + this.unqualified + "," + System.identityHashCode(this) + ")";
        }
    }
}
