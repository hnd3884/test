package org.jvnet.hk2.internal;

import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.reflection.ScopeInfo;
import org.glassfish.hk2.api.Unproxiable;
import org.glassfish.hk2.api.Proxiable;
import org.glassfish.hk2.utilities.reflection.Pretty;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.ServiceLocator;
import java.lang.reflect.TypeVariable;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import java.lang.reflect.AnnotatedElement;
import org.glassfish.hk2.utilities.reflection.ParameterizedTypeImpl;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.api.IndexedFilter;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.ErrorInformation;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.ErrorService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.ServiceHandle;
import java.util.Iterator;
import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.HK2Loader;
import java.util.Map;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.DescriptorType;
import java.util.Collections;
import org.glassfish.hk2.utilities.BuilderHelper;
import java.util.HashSet;
import java.util.LinkedList;
import org.glassfish.hk2.api.InstanceLifecycleListener;
import java.util.List;
import org.glassfish.hk2.api.ValidationService;
import java.util.HashMap;
import java.lang.reflect.Type;
import java.util.Set;
import java.lang.annotation.Annotation;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.ActiveDescriptor;

public class SystemDescriptor<T> implements ActiveDescriptor<T>, Closeable
{
    private final Descriptor baseDescriptor;
    private final Long id;
    private final ActiveDescriptor<T> activeDescriptor;
    private final ServiceLocatorImpl sdLocator;
    private volatile boolean reified;
    private boolean reifying;
    private boolean preAnalyzed;
    private volatile boolean closed;
    private final Object cacheLock;
    private boolean cacheSet;
    private T cachedValue;
    private Class<?> implClass;
    private Annotation scopeAnnotation;
    private Class<? extends Annotation> scope;
    private Set<Type> contracts;
    private Set<Annotation> qualifiers;
    private Creator<T> creator;
    private Long factoryLocatorId;
    private Long factoryServiceId;
    private Type implType;
    private final HashMap<ValidationService, Boolean> validationServiceCache;
    private final List<InstanceLifecycleListener> instanceListeners;
    private final Set<IndexedListData> myLists;
    private int singletonGeneration;
    
    SystemDescriptor(final Descriptor baseDescriptor, final boolean requiresDeepCopy, final ServiceLocatorImpl locator, final Long serviceId) {
        this.reifying = false;
        this.preAnalyzed = false;
        this.closed = false;
        this.cacheLock = new Object();
        this.cacheSet = false;
        this.validationServiceCache = new HashMap<ValidationService, Boolean>();
        this.instanceListeners = new LinkedList<InstanceLifecycleListener>();
        this.myLists = new HashSet<IndexedListData>();
        this.singletonGeneration = Integer.MAX_VALUE;
        if (requiresDeepCopy) {
            this.baseDescriptor = (Descriptor)BuilderHelper.deepCopyDescriptor(baseDescriptor);
        }
        else {
            this.baseDescriptor = baseDescriptor;
        }
        this.sdLocator = locator;
        this.id = serviceId;
        if (baseDescriptor instanceof ActiveDescriptor) {
            final ActiveDescriptor<T> active = (ActiveDescriptor<T>)baseDescriptor;
            if (active.isReified()) {
                this.activeDescriptor = active;
                this.reified = true;
                if (active instanceof AutoActiveDescriptor) {
                    ((AutoActiveDescriptor)active).setHK2Parent(this);
                }
            }
            else {
                this.activeDescriptor = null;
                this.preAnalyzed = true;
                this.implClass = active.getImplementationClass();
                this.implType = active.getImplementationType();
                this.scopeAnnotation = active.getScopeAsAnnotation();
                this.scope = active.getScopeAnnotation();
                this.contracts = Collections.unmodifiableSet((Set<? extends Type>)active.getContractTypes());
                this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)active.getQualifierAnnotations());
            }
        }
        else {
            this.activeDescriptor = null;
        }
    }
    
    public String getImplementation() {
        return this.baseDescriptor.getImplementation();
    }
    
    public Set<String> getAdvertisedContracts() {
        return this.baseDescriptor.getAdvertisedContracts();
    }
    
    public String getScope() {
        return this.baseDescriptor.getScope();
    }
    
    public String getName() {
        return this.baseDescriptor.getName();
    }
    
    public Set<String> getQualifiers() {
        return this.baseDescriptor.getQualifiers();
    }
    
    public DescriptorType getDescriptorType() {
        return this.baseDescriptor.getDescriptorType();
    }
    
    public DescriptorVisibility getDescriptorVisibility() {
        return this.baseDescriptor.getDescriptorVisibility();
    }
    
    public Map<String, List<String>> getMetadata() {
        return this.baseDescriptor.getMetadata();
    }
    
    public HK2Loader getLoader() {
        return this.baseDescriptor.getLoader();
    }
    
    public int getRanking() {
        return this.baseDescriptor.getRanking();
    }
    
    public Boolean isProxiable() {
        return this.baseDescriptor.isProxiable();
    }
    
    public Boolean isProxyForSameScope() {
        return this.baseDescriptor.isProxyForSameScope();
    }
    
    public String getClassAnalysisName() {
        return this.baseDescriptor.getClassAnalysisName();
    }
    
    public int setRanking(final int ranking) {
        return this.sdLocator.unsortIndexes(ranking, this, this.myLists);
    }
    
    int setRankWithLock(final int ranking) {
        return this.baseDescriptor.setRanking(ranking);
    }
    
    void addList(final IndexedListData indexedList) {
        this.myLists.add(indexedList);
    }
    
    void removeList(final IndexedListData indexedList) {
        this.myLists.remove(indexedList);
    }
    
    public Long getServiceId() {
        return this.id;
    }
    
    public T getCache() {
        return this.cachedValue;
    }
    
    public boolean isCacheSet() {
        return this.cacheSet;
    }
    
    public void setCache(final T cacheMe) {
        synchronized (this.cacheLock) {
            this.cachedValue = cacheMe;
            this.cacheSet = true;
        }
    }
    
    public void releaseCache() {
        synchronized (this.cacheLock) {
            this.cacheSet = false;
            this.cachedValue = null;
        }
    }
    
    public boolean isReified() {
        if (this.reified) {
            return true;
        }
        synchronized (this) {
            return this.reified;
        }
    }
    
    public Class<?> getImplementationClass() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getImplementationClass();
        }
        return this.implClass;
    }
    
    public Type getImplementationType() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getImplementationType();
        }
        return this.implType;
    }
    
    public Set<Type> getContractTypes() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getContractTypes();
        }
        return this.contracts;
    }
    
    public Annotation getScopeAsAnnotation() {
        this.checkState();
        return this.scopeAnnotation;
    }
    
    public Class<? extends Annotation> getScopeAnnotation() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getScopeAnnotation();
        }
        return this.scope;
    }
    
    public Set<Annotation> getQualifierAnnotations() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getQualifierAnnotations();
        }
        return this.qualifiers;
    }
    
    public List<Injectee> getInjectees() {
        this.checkState();
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getInjectees();
        }
        return this.creator.getInjectees();
    }
    
    public Long getFactoryServiceId() {
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getFactoryServiceId();
        }
        return this.factoryServiceId;
    }
    
    public Long getFactoryLocatorId() {
        if (this.activeDescriptor != null) {
            return this.activeDescriptor.getFactoryLocatorId();
        }
        return this.factoryLocatorId;
    }
    
    void setFactoryIds(final Long factoryLocatorId, final Long factoryServiceId) {
        this.factoryLocatorId = factoryLocatorId;
        this.factoryServiceId = factoryServiceId;
    }
    
    void invokeInstanceListeners(final InstanceLifecycleEvent event) {
        for (final InstanceLifecycleListener listener : this.instanceListeners) {
            listener.lifecycleEvent(event);
        }
    }
    
    public T create(final ServiceHandle<?> root) {
        this.checkState();
        try {
            T retVal;
            if (this.activeDescriptor != null) {
                if (!(this.activeDescriptor instanceof AutoActiveDescriptor)) {
                    this.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_PRODUCTION, null, (ActiveDescriptor<?>)this));
                }
                retVal = (T)this.activeDescriptor.create((ServiceHandle)root);
                if (!(this.activeDescriptor instanceof AutoActiveDescriptor)) {
                    this.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.POST_PRODUCTION, retVal, (ActiveDescriptor<?>)this));
                }
            }
            else {
                retVal = this.creator.create(root, this);
            }
            return retVal;
        }
        catch (final Throwable re) {
            if (!(re instanceof MultiException)) {
                re = (Throwable)new MultiException(re);
            }
            final MultiException reported = (MultiException)re;
            if (!reported.getReportToErrorService()) {
                throw (RuntimeException)re;
            }
            final LinkedList<ErrorService> errorHandlers = this.sdLocator.getErrorHandlers();
            for (final ErrorService es : errorHandlers) {
                final ErrorInformation ei = (ErrorInformation)new ErrorInformationImpl(ErrorType.SERVICE_CREATION_FAILURE, (Descriptor)this, null, reported);
                try {
                    es.onFailure(ei);
                }
                catch (final Throwable t) {}
            }
            throw (RuntimeException)re;
        }
    }
    
    public void dispose(final T instance) {
        this.checkState();
        final InstanceLifecycleEventImpl event = new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_DESTRUCTION, instance, (ActiveDescriptor<?>)this);
        this.invokeInstanceListeners((InstanceLifecycleEvent)event);
        try {
            if (this.activeDescriptor != null) {
                this.activeDescriptor.dispose((Object)instance);
                return;
            }
            this.creator.dispose(instance);
        }
        catch (final Throwable re) {
            if (!(re instanceof MultiException)) {
                re = (Throwable)new MultiException(re);
            }
            final MultiException reported = (MultiException)re;
            if (!reported.getReportToErrorService()) {
                throw (RuntimeException)re;
            }
            final LinkedList<ErrorService> errorHandlers = this.sdLocator.getErrorHandlers();
            for (final ErrorService es : errorHandlers) {
                final ErrorInformation ei = (ErrorInformation)new ErrorInformationImpl(ErrorType.SERVICE_DESTRUCTION_FAILURE, (Descriptor)this, null, reported);
                try {
                    es.onFailure(ei);
                }
                catch (final Throwable t) {}
            }
            throw (RuntimeException)re;
        }
    }
    
    private void checkState() {
        if (this.reified) {
            return;
        }
        synchronized (this) {
            if (!this.reified) {
                throw new IllegalStateException();
            }
        }
    }
    
    private ActiveDescriptor<?> getFactoryDescriptor(final Method provideMethod, final Type factoryProvidedType, final ServiceLocatorImpl locator, final Collector collector) {
        if (this.factoryServiceId != null && this.factoryLocatorId != null) {
            final Long fFactoryServiceId = this.factoryServiceId;
            final Long fFactoryLocatorId = this.factoryLocatorId;
            final ActiveDescriptor<?> retVal = locator.getBestDescriptor((Filter)new IndexedFilter() {
                public boolean matches(final Descriptor d) {
                    return d.getServiceId() == (long)fFactoryServiceId && d.getLocatorId() == (long)fFactoryLocatorId;
                }
                
                public String getAdvertisedContract() {
                    return Factory.class.getName();
                }
                
                public String getName() {
                    return null;
                }
            });
            if (retVal == null) {
                collector.addThrowable(new IllegalStateException("Could not find a pre-determined factory service for " + factoryProvidedType));
            }
            return retVal;
        }
        final List<ServiceHandle<?>> factoryHandles = locator.getAllServiceHandles((Type)new ParameterizedTypeImpl((Type)Factory.class, new Type[] { factoryProvidedType }), new Annotation[0]);
        ServiceHandle<?> factoryHandle = null;
        for (final ServiceHandle<?> candidate : factoryHandles) {
            if (this.qualifiers.isEmpty()) {
                factoryHandle = candidate;
                break;
            }
            ActiveDescriptor<?> descriptorUnderTest = (ActiveDescriptor<?>)candidate.getActiveDescriptor();
            try {
                descriptorUnderTest = locator.reifyDescriptor((Descriptor)descriptorUnderTest);
            }
            catch (final MultiException me) {
                collector.addThrowable((Throwable)me);
                continue;
            }
            final Method candidateMethod = Utilities.getFactoryProvideMethod(descriptorUnderTest.getImplementationClass());
            final Set<Annotation> candidateQualifiers = Utilities.getAllQualifiers(candidateMethod, Utilities.getDefaultNameFromMethod(candidateMethod, collector), collector);
            if (ReflectionHelper.annotationContainsAll((Set)candidateQualifiers, (Set)this.qualifiers)) {
                factoryHandle = candidate;
                break;
            }
        }
        if (factoryHandle == null) {
            collector.addThrowable(new IllegalStateException("Could not find a factory service for " + factoryProvidedType));
            return null;
        }
        final ActiveDescriptor<?> retVal = (ActiveDescriptor<?>)factoryHandle.getActiveDescriptor();
        this.factoryServiceId = retVal.getServiceId();
        this.factoryLocatorId = retVal.getLocatorId();
        return retVal;
    }
    
    void reify(final Class<?> implClass, final Collector collector) {
        if (this.reified) {
            return;
        }
        synchronized (this) {
            if (this.reified) {
                return;
            }
            while (this.reifying) {
                try {
                    this.wait();
                    continue;
                }
                catch (final InterruptedException e) {
                    collector.addThrowable(e);
                    return;
                }
                break;
            }
            if (this.reified) {
                return;
            }
            this.reifying = true;
        }
        try {
            this.internalReify(implClass, collector);
        }
        finally {
            synchronized (this) {
                this.reifying = false;
                this.notifyAll();
                if (!collector.hasErrors()) {
                    this.reified = true;
                }
                else {
                    collector.addThrowable(new IllegalArgumentException("Errors were discovered while reifying " + this));
                }
            }
        }
    }
    
    private void internalReify(final Class<?> implClass, final Collector collector) {
        if (!this.preAnalyzed) {
            this.implClass = implClass;
            this.implType = implClass;
        }
        else if (!implClass.equals(this.implClass)) {
            collector.addThrowable(new IllegalArgumentException("During reification a class mistmatch was found " + implClass.getName() + " is not the same as " + this.implClass.getName()));
        }
        if (this.getDescriptorType().equals((Object)DescriptorType.CLASS)) {
            if (!this.preAnalyzed) {
                this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)Utilities.getAllQualifiers(implClass, this.baseDescriptor.getName(), collector));
            }
            final ClazzCreator<T> myClazzCreator = new ClazzCreator<T>(this.sdLocator, implClass);
            myClazzCreator.initialize((ActiveDescriptor<?>)this, collector);
            this.creator = myClazzCreator;
            if (!this.preAnalyzed) {
                final ScopeInfo si = Utilities.getScopeAnnotationType(implClass, this.baseDescriptor, collector);
                this.scopeAnnotation = si.getScope();
                this.scope = si.getAnnoType();
                this.contracts = Collections.unmodifiableSet((Set<? extends Type>)ReflectionHelper.getTypeClosure((Type)implClass, this.baseDescriptor.getAdvertisedContracts()));
            }
        }
        else {
            Utilities.checkFactoryType(implClass, collector);
            final Method provideMethod = Utilities.getFactoryProvideMethod(implClass);
            if (provideMethod == null) {
                collector.addThrowable(new IllegalArgumentException("Could not find the provide method on the class " + implClass.getName()));
                return;
            }
            if (!this.preAnalyzed) {
                this.qualifiers = Collections.unmodifiableSet((Set<? extends Annotation>)Utilities.getAllQualifiers(provideMethod, Utilities.getDefaultNameFromMethod(provideMethod, collector), collector));
            }
            Type factoryProvidedType = provideMethod.getGenericReturnType();
            if (factoryProvidedType instanceof TypeVariable) {
                factoryProvidedType = Utilities.getFactoryProductionType(implClass);
            }
            final ActiveDescriptor<?> factoryDescriptor = this.getFactoryDescriptor(provideMethod, factoryProvidedType, this.sdLocator, collector);
            if (factoryDescriptor != null) {
                this.creator = new FactoryCreator<T>((ServiceLocator)this.sdLocator, factoryDescriptor);
            }
            if (!this.preAnalyzed) {
                final ScopeInfo si2 = Utilities.getScopeAnnotationType(provideMethod, this.baseDescriptor, collector);
                this.scopeAnnotation = si2.getScope();
                this.scope = si2.getAnnoType();
                this.contracts = Collections.unmodifiableSet((Set<? extends Type>)ReflectionHelper.getTypeClosure(factoryProvidedType, this.baseDescriptor.getAdvertisedContracts()));
            }
        }
        if (this.baseDescriptor.getScope() == null && this.scope == null) {
            this.scope = (Class<? extends Annotation>)PerLookup.class;
        }
        if (this.baseDescriptor.getScope() != null && this.scope != null) {
            final String scopeName = this.scope.getName();
            if (!scopeName.equals(this.baseDescriptor.getScope())) {
                collector.addThrowable(new IllegalArgumentException("The scope name given in the descriptor (" + this.baseDescriptor.getScope() + ") did not match the scope annotation on the class (" + this.scope.getName() + ") in class " + Pretty.clazz((Class)implClass)));
            }
        }
        if (this.scope.isAnnotationPresent((Class<? extends Annotation>)Proxiable.class) && this.scope.isAnnotationPresent((Class<? extends Annotation>)Unproxiable.class)) {
            collector.addThrowable(new IllegalArgumentException("The scope " + this.scope.getName() + " is marked both @Proxiable and @Unproxiable"));
        }
        if (this.isProxiable() != null && this.isProxiable() && Utilities.isUnproxiableScope(this.scope)) {
            collector.addThrowable(new IllegalArgumentException("The descriptor is in an Unproxiable scope but has  isProxiable set to true"));
        }
    }
    
    public Long getLocatorId() {
        return this.sdLocator.getLocatorId();
    }
    
    public boolean close() {
        if (this.closed) {
            return true;
        }
        synchronized (this) {
            if (this.closed) {
                return true;
            }
            this.closed = true;
            return false;
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    boolean isValidating(final ValidationService service) {
        final Boolean cachedResult = this.validationServiceCache.get(service);
        if (cachedResult != null) {
            return cachedResult;
        }
        boolean decision = true;
        try {
            decision = BuilderHelper.filterMatches((Descriptor)this, service.getLookupFilter());
        }
        catch (final Throwable t) {}
        if (decision) {
            this.validationServiceCache.put(service, Boolean.TRUE);
        }
        else {
            this.validationServiceCache.put(service, Boolean.FALSE);
        }
        return decision;
    }
    
    void reupInstanceListeners(final List<InstanceLifecycleListener> listeners) {
        this.instanceListeners.clear();
        for (final InstanceLifecycleListener listener : listeners) {
            final Filter filter = listener.getFilter();
            if (BuilderHelper.filterMatches((Descriptor)this, filter)) {
                this.instanceListeners.add(listener);
            }
        }
    }
    
    Class<?> getPreAnalyzedClass() {
        return this.implClass;
    }
    
    int getSingletonGeneration() {
        return this.singletonGeneration;
    }
    
    void setSingletonGeneration(final int gen) {
        this.singletonGeneration = gen;
    }
    
    @Override
    public int hashCode() {
        final int low32 = this.id.intValue();
        final int high32 = (int)((long)this.id >> 32);
        final int locatorLow32 = (int)this.sdLocator.getLocatorId();
        final int locatorHigh32 = (int)(this.sdLocator.getLocatorId() >> 32);
        return low32 ^ high32 ^ locatorLow32 ^ locatorHigh32;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof SystemDescriptor)) {
            return false;
        }
        final SystemDescriptor sd = (SystemDescriptor)o;
        return sd.getServiceId().equals(this.id) && sd.getLocatorId().equals(this.sdLocator.getLocatorId());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SystemDescriptor(");
        DescriptorImpl.pretty(sb, (Descriptor)this);
        sb.append("\n\treified=" + this.reified);
        sb.append(")");
        return sb.toString();
    }
}
