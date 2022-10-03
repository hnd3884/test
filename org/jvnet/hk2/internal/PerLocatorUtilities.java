package org.jvnet.hk2.internal;

import org.glassfish.hk2.utilities.reflection.Pretty;
import javax.inject.Inject;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.Injectee;
import org.jvnet.hk2.annotations.Service;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.glassfish.hk2.api.InjectionPointIndicator;
import java.lang.reflect.AnnotatedElement;
import java.util.WeakHashMap;
import org.glassfish.hk2.utilities.general.Hk2ThreadLocal;

public class PerLocatorUtilities
{
    private final Hk2ThreadLocal<WeakHashMap<Class<?>, String>> threadLocalAutoAnalyzerNameCache;
    private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>> threadLocalAnnotationCache;
    private final Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>> hasInjectCache;
    private volatile ProxyUtilities proxyUtilities;
    private final ServiceLocatorImpl parent;
    
    PerLocatorUtilities(final ServiceLocatorImpl parent) {
        this.threadLocalAutoAnalyzerNameCache = new Hk2ThreadLocal<WeakHashMap<Class<?>, String>>() {
            protected WeakHashMap<Class<?>, String> initialValue() {
                return new WeakHashMap<Class<?>, String>();
            }
        };
        this.threadLocalAnnotationCache = new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>>() {
            protected WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo> initialValue() {
                return new WeakHashMap<AnnotatedElement, SoftAnnotatedElementAnnotationInfo>();
            }
        };
        this.hasInjectCache = new Hk2ThreadLocal<WeakHashMap<AnnotatedElement, Boolean>>() {
            protected WeakHashMap<AnnotatedElement, Boolean> initialValue() {
                return new WeakHashMap<AnnotatedElement, Boolean>();
            }
        };
        this.parent = parent;
    }
    
    boolean hasInjectAnnotation(final AnnotatedElement annotated) {
        final WeakHashMap<AnnotatedElement, Boolean> cache = (WeakHashMap<AnnotatedElement, Boolean>)this.hasInjectCache.get();
        final Boolean rv = cache.get(annotated);
        if (rv != null) {
            return rv;
        }
        for (final Annotation anno : annotated.getAnnotations()) {
            if (anno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
                cache.put(annotated, true);
                return true;
            }
            if (this.parent.isInjectAnnotation(anno)) {
                cache.put(annotated, true);
                return true;
            }
        }
        boolean isConstructor;
        Annotation[][] allAnnotations;
        if (annotated instanceof Method) {
            final Method m = (Method)annotated;
            isConstructor = false;
            allAnnotations = m.getParameterAnnotations();
        }
        else {
            if (!(annotated instanceof Constructor)) {
                cache.put(annotated, false);
                return false;
            }
            final Constructor<?> c = (Constructor<?>)annotated;
            isConstructor = true;
            allAnnotations = c.getParameterAnnotations();
        }
        for (final Annotation[] array2 : allAnnotations) {
            final Annotation[] allParamAnnotations = array2;
            for (final Annotation paramAnno : array2) {
                if (paramAnno.annotationType().getAnnotation(InjectionPointIndicator.class) != null) {
                    cache.put(annotated, true);
                    return true;
                }
                if (this.parent.isInjectAnnotation(paramAnno, isConstructor)) {
                    cache.put(annotated, true);
                    return true;
                }
            }
        }
        cache.put(annotated, false);
        return false;
    }
    
    public String getAutoAnalyzerName(final Class<?> c) {
        String retVal = ((WeakHashMap)this.threadLocalAutoAnalyzerNameCache.get()).get(c);
        if (retVal != null) {
            return retVal;
        }
        final Service s = c.getAnnotation(Service.class);
        if (s == null) {
            return null;
        }
        retVal = s.analyzer();
        ((WeakHashMap)this.threadLocalAutoAnalyzerNameCache.get()).put(c, retVal);
        return retVal;
    }
    
    public InjectionResolver<?> getInjectionResolver(final ServiceLocatorImpl locator, final Injectee injectee) throws IllegalStateException {
        return this.getInjectionResolver(locator, injectee.getParent(), injectee.getPosition());
    }
    
    InjectionResolver<?> getInjectionResolver(final ServiceLocatorImpl locator, final AnnotatedElement annotatedGuy) throws IllegalStateException {
        if (annotatedGuy instanceof Method || annotatedGuy instanceof Constructor) {
            throw new IllegalArgumentException("Annotated element '" + annotatedGuy + "' can be neither a Method nor a Constructor.");
        }
        return this.getInjectionResolver(locator, annotatedGuy, -1);
    }
    
    private InjectionResolver<?> getInjectionResolver(final ServiceLocatorImpl locator, final AnnotatedElement annotatedGuy, final int position) throws IllegalStateException {
        final boolean methodOrConstructor = annotatedGuy instanceof Method || annotatedGuy instanceof Constructor;
        final Annotation injectAnnotation = this.getInjectAnnotation(locator, annotatedGuy, methodOrConstructor, position);
        final Class<? extends Annotation> injectType = (Class<? extends Annotation>)((injectAnnotation == null) ? Inject.class : injectAnnotation.annotationType());
        final InjectionResolver<?> retVal = locator.getInjectionResolver(injectType);
        if (retVal == null) {
            throw new IllegalStateException("There is no installed injection resolver for " + Pretty.clazz((Class)injectType) + " for type " + annotatedGuy);
        }
        return retVal;
    }
    
    private Annotation getInjectAnnotation(final ServiceLocatorImpl locator, final AnnotatedElement annotated, final boolean checkParams, final int position) {
        final AnnotatedElementAnnotationInfo annotationInfo = this.computeElementAnnotationInfo(annotated);
        if (checkParams && annotationInfo.hasParams) {
            for (final Annotation paramAnno : annotationInfo.paramAnnotations[position]) {
                if (locator.isInjectAnnotation(paramAnno, annotationInfo.isConstructor)) {
                    return paramAnno;
                }
            }
        }
        for (final Annotation annotation : annotationInfo.elementAnnotations) {
            if (locator.isInjectAnnotation(annotation)) {
                return annotation;
            }
        }
        return null;
    }
    
    private AnnotatedElementAnnotationInfo computeElementAnnotationInfo(final AnnotatedElement ae) {
        SoftAnnotatedElementAnnotationInfo soft = ((WeakHashMap)this.threadLocalAnnotationCache.get()).get(ae);
        AnnotatedElementAnnotationInfo hard;
        if (soft != null) {
            hard = soft.harden(ae);
        }
        else {
            hard = Utilities.computeAEAI(ae);
            soft = hard.soften();
            ((WeakHashMap)this.threadLocalAnnotationCache.get()).put(ae, soft);
        }
        return hard;
    }
    
    public synchronized void releaseCaches() {
        this.hasInjectCache.removeAll();
        if (this.proxyUtilities != null) {
            this.proxyUtilities.releaseCache();
        }
    }
    
    public void shutdown() {
        this.releaseCaches();
        this.threadLocalAutoAnalyzerNameCache.removeAll();
        this.threadLocalAnnotationCache.removeAll();
    }
    
    public ProxyUtilities getProxyUtilities() {
        if (this.proxyUtilities != null) {
            return this.proxyUtilities;
        }
        synchronized (this) {
            if (this.proxyUtilities != null) {
                return this.proxyUtilities;
            }
            return this.proxyUtilities = new ProxyUtilities();
        }
    }
}
