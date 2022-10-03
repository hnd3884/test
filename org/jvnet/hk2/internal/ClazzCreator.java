package org.jvnet.hk2.internal;

import org.glassfish.hk2.api.InstanceLifecycleEvent;
import org.glassfish.hk2.api.InstanceLifecycleEventType;
import org.glassfish.hk2.api.PreDestroy;
import org.glassfish.hk2.api.PostConstruct;
import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.MethodInterceptor;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.api.MultiException;
import java.util.LinkedHashMap;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.InjectionResolver;
import java.util.Map;
import org.glassfish.hk2.api.Injectee;
import java.util.Iterator;
import java.lang.reflect.AnnotatedElement;
import java.util.Collections;
import java.lang.reflect.Field;
import java.util.Collection;
import java.lang.reflect.Constructor;
import java.util.LinkedList;
import org.glassfish.hk2.api.ClassAnalyzer;
import java.util.LinkedHashSet;
import java.lang.reflect.Method;
import java.util.List;
import org.glassfish.hk2.api.ActiveDescriptor;
import java.util.Set;

public class ClazzCreator<T> implements Creator<T>
{
    private final ServiceLocatorImpl locator;
    private final Class<?> implClass;
    private final Set<ResolutionInfo> myInitializers;
    private final Set<ResolutionInfo> myFields;
    private ActiveDescriptor<?> selfDescriptor;
    private ResolutionInfo myConstructor;
    private List<SystemInjecteeImpl> allInjectees;
    private Method postConstructMethod;
    private Method preDestroyMethod;
    
    ClazzCreator(final ServiceLocatorImpl locator, final Class<?> implClass) {
        this.myInitializers = new LinkedHashSet<ResolutionInfo>();
        this.myFields = new LinkedHashSet<ResolutionInfo>();
        this.locator = locator;
        this.implClass = implClass;
    }
    
    void initialize(final ActiveDescriptor<?> selfDescriptor, final String analyzerName, final Collector collector) {
        this.selfDescriptor = selfDescriptor;
        if (selfDescriptor != null && selfDescriptor.getAdvertisedContracts().contains(ClassAnalyzer.class.getName())) {
            String descriptorAnalyzerName = selfDescriptor.getName();
            if (descriptorAnalyzerName == null) {
                descriptorAnalyzerName = this.locator.getDefaultClassAnalyzerName();
            }
            String incomingAnalyzerName = analyzerName;
            if (incomingAnalyzerName == null) {
                incomingAnalyzerName = this.locator.getDefaultClassAnalyzerName();
            }
            if (descriptorAnalyzerName.equals(incomingAnalyzerName)) {
                collector.addThrowable(new IllegalArgumentException("The ClassAnalyzer named " + descriptorAnalyzerName + " is its own ClassAnalyzer. Ensure that an implementation of ClassAnalyzer is not its own ClassAnalyzer"));
                this.myConstructor = null;
                return;
            }
        }
        final ClassAnalyzer analyzer = Utilities.getClassAnalyzer(this.locator, analyzerName, collector);
        if (analyzer == null) {
            this.myConstructor = null;
            return;
        }
        final List<SystemInjecteeImpl> baseAllInjectees = new LinkedList<SystemInjecteeImpl>();
        AnnotatedElement element = Utilities.getConstructor(this.implClass, analyzer, collector);
        if (element == null) {
            this.myConstructor = null;
            return;
        }
        List<SystemInjecteeImpl> injectees = Utilities.getConstructorInjectees((Constructor<?>)element, selfDescriptor);
        if (injectees == null) {
            this.myConstructor = null;
            return;
        }
        baseAllInjectees.addAll(injectees);
        this.myConstructor = new ResolutionInfo(element, (List)injectees);
        final Set<Method> initMethods = Utilities.getInitMethods(this.implClass, analyzer, collector);
        final Iterator<Method> iterator = initMethods.iterator();
        while (iterator.hasNext()) {
            final Method initMethod = (Method)(element = iterator.next());
            injectees = Utilities.getMethodInjectees(this.implClass, initMethod, selfDescriptor);
            if (injectees == null) {
                return;
            }
            baseAllInjectees.addAll(injectees);
            this.myInitializers.add(new ResolutionInfo(element, (List)injectees));
        }
        final Set<Field> fields = Utilities.getInitFields(this.implClass, analyzer, collector);
        final Iterator<Field> iterator2 = fields.iterator();
        while (iterator2.hasNext()) {
            final Field field = (Field)(element = iterator2.next());
            injectees = Utilities.getFieldInjectees(this.implClass, field, selfDescriptor);
            if (injectees == null) {
                return;
            }
            baseAllInjectees.addAll(injectees);
            this.myFields.add(new ResolutionInfo(element, (List)injectees));
        }
        this.postConstructMethod = Utilities.getPostConstruct(this.implClass, analyzer, collector);
        this.preDestroyMethod = Utilities.getPreDestroy(this.implClass, analyzer, collector);
        Utilities.validateSelfInjectees(selfDescriptor, this.allInjectees = Collections.unmodifiableList((List<? extends SystemInjecteeImpl>)baseAllInjectees), collector);
    }
    
    void initialize(final ActiveDescriptor<?> selfDescriptor, final Collector collector) {
        this.initialize(selfDescriptor, (selfDescriptor == null) ? null : selfDescriptor.getClassAnalysisName(), collector);
    }
    
    void resetSelfDescriptor(final ActiveDescriptor<?> selfDescriptor) {
        this.selfDescriptor = selfDescriptor;
        for (final Injectee injectee : this.allInjectees) {
            if (!(injectee instanceof SystemInjecteeImpl)) {
                continue;
            }
            ((SystemInjecteeImpl)injectee).resetInjecteeDescriptor(selfDescriptor);
        }
    }
    
    private void resolve(final Map<SystemInjecteeImpl, Object> addToMe, final InjectionResolver<?> resolver, final SystemInjecteeImpl injectee, final ServiceHandle<?> root, final Collector errorCollection) {
        if (injectee.isSelf()) {
            addToMe.put(injectee, this.selfDescriptor);
            return;
        }
        Object addIn = null;
        try {
            addIn = resolver.resolve((Injectee)injectee, (ServiceHandle)root);
        }
        catch (final Throwable th) {
            errorCollection.addThrowable(th);
        }
        if (addIn != null) {
            addToMe.put(injectee, addIn);
        }
    }
    
    private Map<SystemInjecteeImpl, Object> resolveAllDependencies(final ServiceHandle<?> root) throws MultiException, IllegalStateException {
        final Collector errorCollector = new Collector();
        final Map<SystemInjecteeImpl, Object> retVal = new LinkedHashMap<SystemInjecteeImpl, Object>();
        for (final SystemInjecteeImpl injectee : this.myConstructor.injectees) {
            final InjectionResolver<?> resolver = this.locator.getInjectionResolverForInjectee(injectee);
            this.resolve(retVal, resolver, injectee, root, errorCollector);
        }
        for (final ResolutionInfo fieldRI : this.myFields) {
            for (final SystemInjecteeImpl injectee2 : fieldRI.injectees) {
                final InjectionResolver<?> resolver2 = this.locator.getInjectionResolverForInjectee(injectee2);
                this.resolve(retVal, resolver2, injectee2, root, errorCollector);
            }
        }
        for (final ResolutionInfo methodRI : this.myInitializers) {
            for (final SystemInjecteeImpl injectee2 : methodRI.injectees) {
                final InjectionResolver<?> resolver2 = this.locator.getInjectionResolverForInjectee(injectee2);
                this.resolve(retVal, resolver2, injectee2, root, errorCollector);
            }
        }
        if (errorCollector.hasErrors()) {
            errorCollector.addThrowable(new IllegalArgumentException("While attempting to resolve the dependencies of " + this.implClass.getName() + " errors were found"));
            errorCollector.throwIfErrors();
        }
        return retVal;
    }
    
    private Object createMe(final Map<SystemInjecteeImpl, Object> resolved) throws Throwable {
        final Constructor<?> c = (Constructor<?>)this.myConstructor.baseElement;
        final List<SystemInjecteeImpl> injectees = this.myConstructor.injectees;
        final Object[] args = new Object[injectees.size()];
        for (final Injectee injectee : injectees) {
            args[injectee.getPosition()] = resolved.get(injectee);
        }
        final Utilities.Interceptors interceptors = Utilities.getAllInterceptors(this.locator, this.selfDescriptor, this.implClass, c);
        final Map<Method, List<MethodInterceptor>> methodInterceptors = interceptors.getMethodInterceptors();
        final List<ConstructorInterceptor> constructorInterceptors = interceptors.getConstructorInterceptors();
        if ((methodInterceptors == null || methodInterceptors.isEmpty()) && (constructorInterceptors == null || constructorInterceptors.isEmpty())) {
            return ReflectionHelper.makeMe((Constructor)c, args, this.locator.getNeutralContextClassLoader());
        }
        if (!Utilities.proxiesAvailable()) {
            throw new IllegalStateException("A service " + this.selfDescriptor + " needs either method or constructor interception, but proxies are not available");
        }
        final boolean neutral = this.locator.getNeutralContextClassLoader();
        if (methodInterceptors == null || methodInterceptors.isEmpty()) {
            return ConstructorInterceptorHandler.construct(c, args, neutral, constructorInterceptors);
        }
        return ConstructorInterceptorHandler.construct(c, args, neutral, constructorInterceptors, new ConstructorActionImpl<Object>(this, methodInterceptors));
    }
    
    private void fieldMe(final Map<SystemInjecteeImpl, Object> resolved, final T t) throws Throwable {
        for (final ResolutionInfo ri : this.myFields) {
            final Field field = (Field)ri.baseElement;
            final List<SystemInjecteeImpl> injectees = ri.injectees;
            Injectee fieldInjectee = null;
            final Iterator<SystemInjecteeImpl> iterator2 = injectees.iterator();
            while (iterator2.hasNext()) {
                final Injectee candidate = fieldInjectee = (Injectee)iterator2.next();
            }
            final Object putMeIn = resolved.get(fieldInjectee);
            ReflectionHelper.setField(field, (Object)t, putMeIn);
        }
    }
    
    private void methodMe(final Map<SystemInjecteeImpl, Object> resolved, final T t) throws Throwable {
        for (final ResolutionInfo ri : this.myInitializers) {
            final Method m = (Method)ri.baseElement;
            final List<SystemInjecteeImpl> injectees = ri.injectees;
            final Object[] args = new Object[injectees.size()];
            for (final Injectee injectee : injectees) {
                args[injectee.getPosition()] = resolved.get(injectee);
            }
            ReflectionHelper.invoke((Object)t, m, args, this.locator.getNeutralContextClassLoader());
        }
    }
    
    private void postConstructMe(final T t) throws Throwable {
        if (t == null) {
            return;
        }
        if (t instanceof PostConstruct) {
            ((PostConstruct)t).postConstruct();
            return;
        }
        if (this.postConstructMethod == null) {
            return;
        }
        ReflectionHelper.invoke((Object)t, this.postConstructMethod, new Object[0], this.locator.getNeutralContextClassLoader());
    }
    
    private void preDestroyMe(final T t) throws Throwable {
        if (t == null) {
            return;
        }
        if (t instanceof PreDestroy) {
            ((PreDestroy)t).preDestroy();
            return;
        }
        if (this.preDestroyMethod == null) {
            return;
        }
        ReflectionHelper.invoke((Object)t, this.preDestroyMethod, new Object[0], this.locator.getNeutralContextClassLoader());
    }
    
    @Override
    public T create(final ServiceHandle<?> root, final SystemDescriptor<?> eventThrower) {
        String failureLocation = "resolve";
        try {
            final Map<SystemInjecteeImpl, Object> allResolved = this.resolveAllDependencies(root);
            if (eventThrower != null) {
                eventThrower.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.PRE_PRODUCTION, null, (Map<Injectee, Object>)ReflectionHelper.cast((Object)allResolved), (ActiveDescriptor<?>)eventThrower));
            }
            failureLocation = "create";
            final T retVal = (T)this.createMe(allResolved);
            failureLocation = "field inject";
            this.fieldMe(allResolved, retVal);
            failureLocation = "method inject";
            this.methodMe(allResolved, retVal);
            failureLocation = "post construct";
            this.postConstructMe(retVal);
            if (eventThrower != null) {
                eventThrower.invokeInstanceListeners((InstanceLifecycleEvent)new InstanceLifecycleEventImpl(InstanceLifecycleEventType.POST_PRODUCTION, retVal, (Map<Injectee, Object>)ReflectionHelper.cast((Object)allResolved), (ActiveDescriptor<?>)eventThrower));
            }
            return retVal;
        }
        catch (final Throwable th) {
            if (th instanceof MultiException) {
                final MultiException me = (MultiException)th;
                me.addError((Throwable)new IllegalStateException("Unable to perform operation: " + failureLocation + " on " + this.implClass.getName()));
                throw me;
            }
            final MultiException me = new MultiException(th);
            me.addError((Throwable)new IllegalStateException("Unable to perform operation: " + failureLocation + " on " + this.implClass.getName()));
            throw me;
        }
    }
    
    @Override
    public void dispose(final T instance) {
        try {
            this.preDestroyMe(instance);
        }
        catch (final Throwable th) {
            if (th instanceof MultiException) {
                throw (MultiException)th;
            }
            throw new MultiException(th);
        }
    }
    
    @Override
    public List<Injectee> getInjectees() {
        return (List)ReflectionHelper.cast((Object)this.allInjectees);
    }
    
    ServiceLocatorImpl getServiceLocator() {
        return this.locator;
    }
    
    Class<?> getImplClass() {
        return this.implClass;
    }
    
    ActiveDescriptor<?> getUnderlyingDescriptor() {
        return this.selfDescriptor;
    }
    
    @Override
    public String toString() {
        return "ClazzCreator(" + this.locator + "," + this.implClass.getName() + "," + System.identityHashCode(this) + ")";
    }
    
    private static class ResolutionInfo
    {
        private final AnnotatedElement baseElement;
        private final List<SystemInjecteeImpl> injectees;
        
        private ResolutionInfo(final AnnotatedElement baseElement, final List<SystemInjecteeImpl> injectees) {
            this.injectees = new LinkedList<SystemInjecteeImpl>();
            this.baseElement = baseElement;
            this.injectees.addAll(injectees);
        }
        
        @Override
        public String toString() {
            return "ResolutionInfo(" + this.baseElement + "," + this.injectees + "," + System.identityHashCode(this) + ")";
        }
    }
}
