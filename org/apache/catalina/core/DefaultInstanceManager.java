package org.apache.catalina.core;

import javax.annotation.PreDestroy;
import javax.annotation.PostConstruct;
import java.security.PrivilegedAction;
import org.apache.catalina.Globals;
import java.util.Iterator;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.catalina.ContainerServlet;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import org.apache.catalina.security.SecurityUtil;
import java.lang.reflect.Field;
import java.util.List;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceContext;
import javax.xml.ws.WebServiceRef;
import java.lang.annotation.Annotation;
import javax.ejb.EJB;
import javax.annotation.Resource;
import org.apache.catalina.util.Introspection;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.naming.NamingException;
import java.lang.reflect.InvocationTargetException;
import org.apache.juli.logging.Log;
import java.util.Collections;
import java.util.HashSet;
import org.apache.tomcat.util.collections.ManagedConcurrentWeakHashMap;
import java.util.Set;
import java.util.Map;
import javax.naming.Context;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.InstanceManager;

public class DefaultInstanceManager implements InstanceManager
{
    private static final AnnotationCacheEntry[] ANNOTATIONS_EMPTY;
    protected static final StringManager sm;
    private static final boolean EJB_PRESENT;
    private static final boolean JPA_PRESENT;
    private static final boolean WS_PRESENT;
    private final Context context;
    private final Map<String, Map<String, String>> injectionMap;
    protected final ClassLoader classLoader;
    protected final ClassLoader containerClassLoader;
    protected final boolean privileged;
    protected final boolean ignoreAnnotations;
    private final Set<String> restrictedClasses;
    private final ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]> annotationCache;
    private final Map<String, String> postConstructMethods;
    private final Map<String, String> preDestroyMethods;
    
    public DefaultInstanceManager(final Context context, final Map<String, Map<String, String>> injectionMap, final org.apache.catalina.Context catalinaContext, final ClassLoader containerClassLoader) {
        this.annotationCache = (ManagedConcurrentWeakHashMap<Class<?>, AnnotationCacheEntry[]>)new ManagedConcurrentWeakHashMap();
        this.classLoader = catalinaContext.getLoader().getClassLoader();
        this.privileged = catalinaContext.getPrivileged();
        this.containerClassLoader = containerClassLoader;
        this.ignoreAnnotations = catalinaContext.getIgnoreAnnotations();
        final Log log = catalinaContext.getLogger();
        final Set<String> classNames = new HashSet<String>();
        loadProperties(classNames, "org/apache/catalina/core/RestrictedServlets.properties", "defaultInstanceManager.restrictedServletsResource", log);
        loadProperties(classNames, "org/apache/catalina/core/RestrictedListeners.properties", "defaultInstanceManager.restrictedListenersResource", log);
        loadProperties(classNames, "org/apache/catalina/core/RestrictedFilters.properties", "defaultInstanceManager.restrictedFiltersResource", log);
        this.restrictedClasses = Collections.unmodifiableSet((Set<? extends String>)classNames);
        this.context = context;
        this.injectionMap = injectionMap;
        this.postConstructMethods = catalinaContext.findPostConstructMethods();
        this.preDestroyMethods = catalinaContext.findPreDestroyMethods();
    }
    
    public Object newInstance(final Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        return this.newInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]), clazz);
    }
    
    public Object newInstance(final String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        final Class<?> clazz = this.loadClassMaybePrivileged(className, this.classLoader);
        return this.newInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]), clazz);
    }
    
    public Object newInstance(final String className, final ClassLoader classLoader) throws IllegalAccessException, NamingException, InvocationTargetException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException {
        final Class<?> clazz = classLoader.loadClass(className);
        return this.newInstance(clazz.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]), clazz);
    }
    
    public void newInstance(final Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
        this.newInstance(o, o.getClass());
    }
    
    private Object newInstance(final Object instance, final Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (!this.ignoreAnnotations) {
            final Map<String, String> injections = this.assembleInjectionsFromClassHierarchy(clazz);
            this.populateAnnotationsCache(clazz, injections);
            this.processAnnotations(instance, injections);
            this.postConstruct(instance, clazz);
        }
        return instance;
    }
    
    private Map<String, String> assembleInjectionsFromClassHierarchy(Class<?> clazz) {
        final Map<String, String> injections = new HashMap<String, String>();
        Map<String, String> currentInjections = null;
        while (clazz != null) {
            currentInjections = this.injectionMap.get(clazz.getName());
            if (currentInjections != null) {
                injections.putAll(currentInjections);
            }
            clazz = clazz.getSuperclass();
        }
        return injections;
    }
    
    public void destroyInstance(final Object instance) throws IllegalAccessException, InvocationTargetException {
        if (!this.ignoreAnnotations) {
            this.preDestroy(instance, instance.getClass());
        }
    }
    
    protected void postConstruct(final Object instance, final Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        if (this.context == null) {
            return;
        }
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            this.postConstruct(instance, superClass);
        }
        final AnnotationCacheEntry[] arr$;
        final AnnotationCacheEntry[] annotations = arr$ = (AnnotationCacheEntry[])this.annotationCache.get((Object)clazz);
        for (final AnnotationCacheEntry entry : arr$) {
            if (entry.getType() == AnnotationCacheEntryType.POST_CONSTRUCT) {
                final Method postConstruct = getMethod(clazz, entry);
                synchronized (postConstruct) {
                    final boolean accessibility = postConstruct.isAccessible();
                    postConstruct.setAccessible(true);
                    postConstruct.invoke(instance, new Object[0]);
                    postConstruct.setAccessible(accessibility);
                }
            }
        }
    }
    
    protected void preDestroy(final Object instance, final Class<?> clazz) throws IllegalAccessException, InvocationTargetException {
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass != Object.class) {
            this.preDestroy(instance, superClass);
        }
        final AnnotationCacheEntry[] annotations = (AnnotationCacheEntry[])this.annotationCache.get((Object)clazz);
        if (annotations == null) {
            return;
        }
        for (final AnnotationCacheEntry entry : annotations) {
            if (entry.getType() == AnnotationCacheEntryType.PRE_DESTROY) {
                final Method preDestroy = getMethod(clazz, entry);
                synchronized (preDestroy) {
                    final boolean accessibility = preDestroy.isAccessible();
                    preDestroy.setAccessible(true);
                    preDestroy.invoke(instance, new Object[0]);
                    preDestroy.setAccessible(accessibility);
                }
            }
        }
    }
    
    public void backgroundProcess() {
        this.annotationCache.maintain();
    }
    
    protected void populateAnnotationsCache(Class<?> clazz, final Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        List<AnnotationCacheEntry> annotations = null;
        final Set<String> injectionsMatchedToSetter = new HashSet<String>();
        while (clazz != null) {
            AnnotationCacheEntry[] annotationsArray = (AnnotationCacheEntry[])this.annotationCache.get((Object)clazz);
            if (annotationsArray == null) {
                if (annotations == null) {
                    annotations = new ArrayList<AnnotationCacheEntry>();
                }
                else {
                    annotations.clear();
                }
                final Method[] methods = Introspection.getDeclaredMethods(clazz);
                Method postConstruct = null;
                final String postConstructFromXml = this.postConstructMethods.get(clazz.getName());
                Method preDestroy = null;
                final String preDestroyFromXml = this.preDestroyMethods.get(clazz.getName());
                for (final Method method : methods) {
                    Label_0529: {
                        if (this.context != null) {
                            if (injections != null && Introspection.isValidSetter(method)) {
                                final String fieldName = Introspection.getPropertyName(method);
                                injectionsMatchedToSetter.add(fieldName);
                                if (injections.containsKey(fieldName)) {
                                    annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), injections.get(fieldName), AnnotationCacheEntryType.SETTER));
                                    break Label_0529;
                                }
                            }
                            final Resource resourceAnnotation;
                            if ((resourceAnnotation = method.getAnnotation(Resource.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), resourceAnnotation.name(), AnnotationCacheEntryType.SETTER));
                            }
                            else {
                                final Annotation ejbAnnotation;
                                if (DefaultInstanceManager.EJB_PRESENT && (ejbAnnotation = method.getAnnotation((Class<Annotation>)EJB.class)) != null) {
                                    annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), ((EJB)ejbAnnotation).name(), AnnotationCacheEntryType.SETTER));
                                }
                                else {
                                    final Annotation webServiceRefAnnotation;
                                    if (DefaultInstanceManager.WS_PRESENT && (webServiceRefAnnotation = method.getAnnotation(WebServiceRef.class)) != null) {
                                        annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), ((WebServiceRef)webServiceRefAnnotation).name(), AnnotationCacheEntryType.SETTER));
                                    }
                                    else {
                                        final Annotation persistenceContextAnnotation;
                                        if (DefaultInstanceManager.JPA_PRESENT && (persistenceContextAnnotation = method.getAnnotation((Class<Annotation>)PersistenceContext.class)) != null) {
                                            annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), ((PersistenceContext)persistenceContextAnnotation).name(), AnnotationCacheEntryType.SETTER));
                                        }
                                        else {
                                            final Annotation persistenceUnitAnnotation;
                                            if (DefaultInstanceManager.JPA_PRESENT && (persistenceUnitAnnotation = method.getAnnotation((Class<Annotation>)PersistenceUnit.class)) != null) {
                                                annotations.add(new AnnotationCacheEntry(method.getName(), method.getParameterTypes(), ((PersistenceUnit)persistenceUnitAnnotation).name(), AnnotationCacheEntryType.SETTER));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        postConstruct = findPostConstruct(postConstruct, postConstructFromXml, method);
                        preDestroy = findPreDestroy(preDestroy, preDestroyFromXml, method);
                    }
                }
                if (postConstruct != null) {
                    annotations.add(new AnnotationCacheEntry(postConstruct.getName(), postConstruct.getParameterTypes(), null, AnnotationCacheEntryType.POST_CONSTRUCT));
                }
                else if (postConstructFromXml != null) {
                    throw new IllegalArgumentException(DefaultInstanceManager.sm.getString("defaultInstanceManager.postConstructNotFound", new Object[] { postConstructFromXml, clazz.getName() }));
                }
                if (preDestroy != null) {
                    annotations.add(new AnnotationCacheEntry(preDestroy.getName(), preDestroy.getParameterTypes(), null, AnnotationCacheEntryType.PRE_DESTROY));
                }
                else if (preDestroyFromXml != null) {
                    throw new IllegalArgumentException(DefaultInstanceManager.sm.getString("defaultInstanceManager.preDestroyNotFound", new Object[] { preDestroyFromXml, clazz.getName() }));
                }
                if (this.context != null) {
                    final Field[] arr$2;
                    final Field[] fields = arr$2 = Introspection.getDeclaredFields(clazz);
                    for (final Field field : arr$2) {
                        final String fieldName2 = field.getName();
                        if (injections != null && injections.containsKey(fieldName2) && !injectionsMatchedToSetter.contains(fieldName2)) {
                            annotations.add(new AnnotationCacheEntry(fieldName2, null, injections.get(fieldName2), AnnotationCacheEntryType.FIELD));
                        }
                        else {
                            final Resource resourceAnnotation2;
                            if ((resourceAnnotation2 = field.getAnnotation(Resource.class)) != null) {
                                annotations.add(new AnnotationCacheEntry(fieldName2, null, resourceAnnotation2.name(), AnnotationCacheEntryType.FIELD));
                            }
                            else {
                                final Annotation ejbAnnotation2;
                                if (DefaultInstanceManager.EJB_PRESENT && (ejbAnnotation2 = field.getAnnotation((Class<Annotation>)EJB.class)) != null) {
                                    annotations.add(new AnnotationCacheEntry(fieldName2, null, ((EJB)ejbAnnotation2).name(), AnnotationCacheEntryType.FIELD));
                                }
                                else {
                                    final Annotation webServiceRefAnnotation2;
                                    if (DefaultInstanceManager.WS_PRESENT && (webServiceRefAnnotation2 = field.getAnnotation(WebServiceRef.class)) != null) {
                                        annotations.add(new AnnotationCacheEntry(fieldName2, null, ((WebServiceRef)webServiceRefAnnotation2).name(), AnnotationCacheEntryType.FIELD));
                                    }
                                    else {
                                        final Annotation persistenceContextAnnotation2;
                                        if (DefaultInstanceManager.JPA_PRESENT && (persistenceContextAnnotation2 = field.getAnnotation((Class<Annotation>)PersistenceContext.class)) != null) {
                                            annotations.add(new AnnotationCacheEntry(fieldName2, null, ((PersistenceContext)persistenceContextAnnotation2).name(), AnnotationCacheEntryType.FIELD));
                                        }
                                        else {
                                            final Annotation persistenceUnitAnnotation2;
                                            if (DefaultInstanceManager.JPA_PRESENT && (persistenceUnitAnnotation2 = field.getAnnotation((Class<Annotation>)PersistenceUnit.class)) != null) {
                                                annotations.add(new AnnotationCacheEntry(fieldName2, null, ((PersistenceUnit)persistenceUnitAnnotation2).name(), AnnotationCacheEntryType.FIELD));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (annotations.isEmpty()) {
                    annotationsArray = DefaultInstanceManager.ANNOTATIONS_EMPTY;
                }
                else {
                    annotationsArray = annotations.toArray(new AnnotationCacheEntry[0]);
                }
                synchronized (this.annotationCache) {
                    this.annotationCache.put((Object)clazz, (Object)annotationsArray);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
    
    protected void processAnnotations(final Object instance, final Map<String, String> injections) throws IllegalAccessException, InvocationTargetException, NamingException {
        if (this.context == null) {
            return;
        }
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            final AnnotationCacheEntry[] arr$;
            final AnnotationCacheEntry[] annotations = arr$ = (AnnotationCacheEntry[])this.annotationCache.get((Object)clazz);
            for (final AnnotationCacheEntry entry : arr$) {
                if (entry.getType() == AnnotationCacheEntryType.SETTER) {
                    lookupMethodResource(this.context, instance, getMethod(clazz, entry), entry.getName(), clazz);
                }
                else if (entry.getType() == AnnotationCacheEntryType.FIELD) {
                    lookupFieldResource(this.context, instance, getField(clazz, entry), entry.getName(), clazz);
                }
            }
        }
    }
    
    protected int getAnnotationCacheSize() {
        return this.annotationCache.size();
    }
    
    protected Class<?> loadClassMaybePrivileged(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> clazz = null;
        Label_0066: {
            if (SecurityUtil.isPackageProtectionEnabled()) {
                try {
                    clazz = AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>)new PrivilegedLoadClass(className, classLoader));
                    break Label_0066;
                }
                catch (final PrivilegedActionException e) {
                    final Throwable t = e.getCause();
                    if (t instanceof ClassNotFoundException) {
                        throw (ClassNotFoundException)t;
                    }
                    throw new RuntimeException(t);
                }
            }
            clazz = this.loadClass(className, classLoader);
        }
        this.checkAccess(clazz);
        return clazz;
    }
    
    protected Class<?> loadClass(final String className, final ClassLoader classLoader) throws ClassNotFoundException {
        if (className.startsWith("org.apache.catalina")) {
            return this.containerClassLoader.loadClass(className);
        }
        try {
            final Class<?> clazz = this.containerClassLoader.loadClass(className);
            if (ContainerServlet.class.isAssignableFrom(clazz)) {
                return clazz;
            }
        }
        catch (final Throwable t) {
            ExceptionUtils.handleThrowable(t);
        }
        return classLoader.loadClass(className);
    }
    
    private void checkAccess(Class<?> clazz) {
        if (this.privileged) {
            return;
        }
        if (ContainerServlet.class.isAssignableFrom(clazz)) {
            throw new SecurityException(DefaultInstanceManager.sm.getString("defaultInstanceManager.restrictedContainerServlet", new Object[] { clazz }));
        }
        while (clazz != null) {
            if (this.restrictedClasses.contains(clazz.getName())) {
                throw new SecurityException(DefaultInstanceManager.sm.getString("defaultInstanceManager.restrictedClass", new Object[] { clazz }));
            }
            clazz = clazz.getSuperclass();
        }
    }
    
    protected static void lookupFieldResource(final Context context, final Object instance, final Field field, final String name, final Class<?> clazz) throws NamingException, IllegalAccessException {
        final String normalizedName = normalize(name);
        Object lookedupResource;
        if (normalizedName != null && normalizedName.length() > 0) {
            lookedupResource = context.lookup(normalizedName);
        }
        else {
            lookedupResource = context.lookup(clazz.getName() + "/" + field.getName());
        }
        synchronized (field) {
            final boolean accessibility = field.isAccessible();
            field.setAccessible(true);
            field.set(instance, lookedupResource);
            field.setAccessible(accessibility);
        }
    }
    
    protected static void lookupMethodResource(final Context context, final Object instance, final Method method, final String name, final Class<?> clazz) throws NamingException, IllegalAccessException, InvocationTargetException {
        if (!Introspection.isValidSetter(method)) {
            throw new IllegalArgumentException(DefaultInstanceManager.sm.getString("defaultInstanceManager.invalidInjection"));
        }
        final String normalizedName = normalize(name);
        Object lookedupResource;
        if (normalizedName != null && normalizedName.length() > 0) {
            lookedupResource = context.lookup(normalizedName);
        }
        else {
            lookedupResource = context.lookup(clazz.getName() + "/" + Introspection.getPropertyName(method));
        }
        synchronized (method) {
            final boolean accessibility = method.isAccessible();
            method.setAccessible(true);
            method.invoke(instance, lookedupResource);
            method.setAccessible(accessibility);
        }
    }
    
    private static void loadProperties(final Set<String> classNames, final String resourceName, final String messageKey, final Log log) {
        final Properties properties = new Properties();
        final ClassLoader cl = DefaultInstanceManager.class.getClassLoader();
        try (final InputStream is = cl.getResourceAsStream(resourceName)) {
            if (is == null) {
                log.error((Object)DefaultInstanceManager.sm.getString(messageKey, new Object[] { resourceName }));
            }
            else {
                properties.load(is);
            }
        }
        catch (final IOException ioe) {
            log.error((Object)DefaultInstanceManager.sm.getString(messageKey, new Object[] { resourceName }), (Throwable)ioe);
        }
        if (properties.isEmpty()) {
            return;
        }
        for (final Map.Entry<Object, Object> e : properties.entrySet()) {
            if ("restricted".equals(e.getValue())) {
                classNames.add(e.getKey().toString());
            }
            else {
                log.warn((Object)DefaultInstanceManager.sm.getString("defaultInstanceManager.restrictedWrongValue", new Object[] { resourceName, e.getKey(), e.getValue() }));
            }
        }
    }
    
    private static String normalize(final String jndiName) {
        if (jndiName != null && jndiName.startsWith("java:comp/env/")) {
            return jndiName.substring(14);
        }
        return jndiName;
    }
    
    private static Method getMethod(final Class<?> clazz, final AnnotationCacheEntry entry) {
        Method result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = AccessController.doPrivileged((PrivilegedAction<Method>)new PrivilegedGetMethod(clazz, entry));
        }
        else {
            try {
                result = clazz.getDeclaredMethod(entry.getAccessibleObjectName(), entry.getParamTypes());
            }
            catch (final NoSuchMethodException ex) {}
        }
        return result;
    }
    
    private static Field getField(final Class<?> clazz, final AnnotationCacheEntry entry) {
        Field result = null;
        if (Globals.IS_SECURITY_ENABLED) {
            result = AccessController.doPrivileged((PrivilegedAction<Field>)new PrivilegedGetField(clazz, entry));
        }
        else {
            try {
                result = clazz.getDeclaredField(entry.getAccessibleObjectName());
            }
            catch (final NoSuchFieldException ex) {}
        }
        return result;
    }
    
    private static Method findPostConstruct(final Method currentPostConstruct, final String postConstructFromXml, final Method method) {
        return findLifecycleCallback(currentPostConstruct, postConstructFromXml, method, PostConstruct.class);
    }
    
    private static Method findPreDestroy(final Method currentPreDestroy, final String preDestroyFromXml, final Method method) {
        return findLifecycleCallback(currentPreDestroy, preDestroyFromXml, method, PreDestroy.class);
    }
    
    private static Method findLifecycleCallback(final Method currentMethod, final String methodNameFromXml, final Method method, final Class<? extends Annotation> annotation) {
        Method result = currentMethod;
        if (methodNameFromXml != null) {
            if (method.getName().equals(methodNameFromXml)) {
                if (!Introspection.isValidLifecycleCallback(method)) {
                    throw new IllegalArgumentException("Invalid " + annotation.getName() + " annotation");
                }
                result = method;
            }
        }
        else if (method.isAnnotationPresent(annotation)) {
            if (currentMethod != null || !Introspection.isValidLifecycleCallback(method)) {
                throw new IllegalArgumentException("Invalid " + annotation.getName() + " annotation");
            }
            result = method;
        }
        return result;
    }
    
    static {
        ANNOTATIONS_EMPTY = new AnnotationCacheEntry[0];
        sm = StringManager.getManager((Class)DefaultInstanceManager.class);
        Class<?> clazz = null;
        try {
            clazz = Class.forName("javax.ejb.EJB");
        }
        catch (final ClassNotFoundException ex) {}
        EJB_PRESENT = (clazz != null);
        clazz = null;
        try {
            clazz = Class.forName("javax.persistence.PersistenceContext");
        }
        catch (final ClassNotFoundException ex2) {}
        JPA_PRESENT = (clazz != null);
        clazz = null;
        try {
            clazz = Class.forName("javax.xml.ws.WebServiceRef");
        }
        catch (final ClassNotFoundException ex3) {}
        WS_PRESENT = (clazz != null);
    }
    
    private static final class AnnotationCacheEntry
    {
        private final String accessibleObjectName;
        private final Class<?>[] paramTypes;
        private final String name;
        private final AnnotationCacheEntryType type;
        
        public AnnotationCacheEntry(final String accessibleObjectName, final Class<?>[] paramTypes, final String name, final AnnotationCacheEntryType type) {
            this.accessibleObjectName = accessibleObjectName;
            this.paramTypes = paramTypes;
            this.name = name;
            this.type = type;
        }
        
        public String getAccessibleObjectName() {
            return this.accessibleObjectName;
        }
        
        public Class<?>[] getParamTypes() {
            return this.paramTypes;
        }
        
        public String getName() {
            return this.name;
        }
        
        public AnnotationCacheEntryType getType() {
            return this.type;
        }
    }
    
    private enum AnnotationCacheEntryType
    {
        FIELD, 
        SETTER, 
        POST_CONSTRUCT, 
        PRE_DESTROY;
    }
    
    private static class PrivilegedGetField implements PrivilegedAction<Field>
    {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;
        
        public PrivilegedGetField(final Class<?> clazz, final AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }
        
        @Override
        public Field run() {
            Field result = null;
            try {
                result = this.clazz.getDeclaredField(this.entry.getAccessibleObjectName());
            }
            catch (final NoSuchFieldException ex) {}
            return result;
        }
    }
    
    private static class PrivilegedGetMethod implements PrivilegedAction<Method>
    {
        private final Class<?> clazz;
        private final AnnotationCacheEntry entry;
        
        public PrivilegedGetMethod(final Class<?> clazz, final AnnotationCacheEntry entry) {
            this.clazz = clazz;
            this.entry = entry;
        }
        
        @Override
        public Method run() {
            Method result = null;
            try {
                result = this.clazz.getDeclaredMethod(this.entry.getAccessibleObjectName(), this.entry.getParamTypes());
            }
            catch (final NoSuchMethodException ex) {}
            return result;
        }
    }
    
    private class PrivilegedLoadClass implements PrivilegedExceptionAction<Class<?>>
    {
        private final String className;
        private final ClassLoader classLoader;
        
        public PrivilegedLoadClass(final String className, final ClassLoader classLoader) {
            this.className = className;
            this.classLoader = classLoader;
        }
        
        @Override
        public Class<?> run() throws Exception {
            return DefaultInstanceManager.this.loadClass(this.className, this.classLoader);
        }
    }
}
