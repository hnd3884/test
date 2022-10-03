package com.sun.jmx.mbeanserver;

import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Locale;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.lang.ref.SoftReference;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.remote.util.EnvHelp;
import java.lang.reflect.InvocationTargetException;
import javax.management.AttributeNotFoundException;
import javax.management.openmbean.CompositeData;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.Map;
import java.lang.reflect.UndeclaredThrowableException;
import sun.reflect.misc.MethodUtil;
import javax.management.DescriptorKey;
import java.util.HashMap;
import java.lang.annotation.Annotation;
import javax.management.ImmutableDescriptor;
import javax.management.Descriptor;
import java.lang.reflect.AnnotatedElement;
import sun.reflect.misc.ReflectUtil;
import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import java.lang.reflect.Modifier;
import javax.management.DynamicMBean;

public class Introspector
{
    public static final boolean ALLOW_NONPUBLIC_MBEAN;
    
    private Introspector() {
    }
    
    public static final boolean isDynamic(final Class<?> clazz) {
        return DynamicMBean.class.isAssignableFrom(clazz);
    }
    
    public static void testCreation(final Class<?> clazz) throws NotCompliantMBeanException {
        final int modifiers = clazz.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
            throw new NotCompliantMBeanException("MBean class must be concrete");
        }
        if (clazz.getConstructors().length == 0) {
            throw new NotCompliantMBeanException("MBean class must have public constructor");
        }
    }
    
    public static void checkCompliance(final Class<?> clazz) throws NotCompliantMBeanException {
        if (DynamicMBean.class.isAssignableFrom(clazz)) {
            return;
        }
        try {
            getStandardMBeanInterface(clazz);
        }
        catch (final NotCompliantMBeanException ex) {
            final NotCompliantMBeanException ex2 = ex;
            try {
                getMXBeanInterface(clazz);
            }
            catch (final NotCompliantMBeanException ex3) {
                throw new NotCompliantMBeanException("MBean class " + clazz.getName() + " does not implement DynamicMBean, and neither follows the Standard MBean conventions (" + ex2.toString() + ") nor the MXBean conventions (" + ex3.toString() + ")");
            }
        }
    }
    
    public static <T> DynamicMBean makeDynamicMBean(final T t) throws NotCompliantMBeanException {
        if (t instanceof DynamicMBean) {
            return (DynamicMBean)t;
        }
        final Class<?> class1 = t.getClass();
        Class clazz = null;
        try {
            clazz = Util.cast(getStandardMBeanInterface(class1));
        }
        catch (final NotCompliantMBeanException ex) {}
        if (clazz != null) {
            return new StandardMBeanSupport((T)t, clazz);
        }
        try {
            clazz = Util.cast(getMXBeanInterface(class1));
        }
        catch (final NotCompliantMBeanException ex2) {}
        if (clazz != null) {
            return new MXBeanSupport((T)t, clazz);
        }
        checkCompliance(class1);
        throw new NotCompliantMBeanException("Not compliant");
    }
    
    public static MBeanInfo testCompliance(final Class<?> clazz) throws NotCompliantMBeanException {
        if (isDynamic(clazz)) {
            return null;
        }
        return testCompliance(clazz, null);
    }
    
    public static void testComplianceMXBeanInterface(final Class<?> clazz) throws NotCompliantMBeanException {
        MXBeanIntrospector.getInstance().getAnalyzer(clazz);
    }
    
    public static void testComplianceMBeanInterface(final Class<?> clazz) throws NotCompliantMBeanException {
        StandardMBeanIntrospector.getInstance().getAnalyzer(clazz);
    }
    
    public static synchronized MBeanInfo testCompliance(final Class<?> clazz, Class<?> standardMBeanInterface) throws NotCompliantMBeanException {
        if (standardMBeanInterface == null) {
            standardMBeanInterface = getStandardMBeanInterface(clazz);
        }
        ReflectUtil.checkPackageAccess(standardMBeanInterface);
        return getClassMBeanInfo((MBeanIntrospector<Object>)StandardMBeanIntrospector.getInstance(), clazz, standardMBeanInterface);
    }
    
    private static <M> MBeanInfo getClassMBeanInfo(final MBeanIntrospector<M> mBeanIntrospector, final Class<?> clazz, final Class<?> clazz2) throws NotCompliantMBeanException {
        return mBeanIntrospector.getClassMBeanInfo(clazz, mBeanIntrospector.getPerInterface(clazz2));
    }
    
    public static Class<?> getMBeanInterface(final Class<?> clazz) {
        if (isDynamic(clazz)) {
            return null;
        }
        try {
            return getStandardMBeanInterface(clazz);
        }
        catch (final NotCompliantMBeanException ex) {
            return null;
        }
    }
    
    public static <T> Class<? super T> getStandardMBeanInterface(final Class<T> clazz) throws NotCompliantMBeanException {
        Class<T> superclass = clazz;
        Class<? super T> mBeanInterface = null;
        while (superclass != null) {
            mBeanInterface = findMBeanInterface(superclass, superclass.getName());
            if (mBeanInterface != null) {
                break;
            }
            superclass = superclass.getSuperclass();
        }
        if (mBeanInterface != null) {
            return mBeanInterface;
        }
        throw new NotCompliantMBeanException("Class " + clazz.getName() + " is not a JMX compliant Standard MBean");
    }
    
    public static <T> Class<? super T> getMXBeanInterface(final Class<T> clazz) throws NotCompliantMBeanException {
        try {
            return MXBeanSupport.findMXBeanInterface(clazz);
        }
        catch (final Exception ex) {
            throw throwException(clazz, ex);
        }
    }
    
    private static <T> Class<? super T> findMBeanInterface(final Class<T> clazz, final String s) {
        for (Class<T> superclass = clazz; superclass != null; superclass = superclass.getSuperclass()) {
            final Class[] interfaces = superclass.getInterfaces();
            for (int length = interfaces.length, i = 0; i < length; ++i) {
                final Class<? super Object> implementsMBean = implementsMBean(Util.cast(interfaces[i]), s);
                if (implementsMBean != null) {
                    return implementsMBean;
                }
            }
        }
        return null;
    }
    
    public static Descriptor descriptorForElement(final AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        return descriptorForAnnotations(annotatedElement.getAnnotations());
    }
    
    public static Descriptor descriptorForAnnotations(final Annotation[] array) {
        if (array.length == 0) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        final HashMap hashMap = new HashMap();
        for (final Annotation annotation : array) {
            final Class<? extends Annotation> annotationType = annotation.annotationType();
            final Method[] methods = annotationType.getMethods();
            int n = 0;
            for (final Method method : methods) {
                final DescriptorKey descriptorKey = method.getAnnotation(DescriptorKey.class);
                if (descriptorKey != null) {
                    final String value = descriptorKey.value();
                    Object invoke;
                    try {
                        if (n == 0) {
                            ReflectUtil.checkPackageAccess(annotationType);
                            n = 1;
                        }
                        invoke = MethodUtil.invoke(method, annotation, null);
                    }
                    catch (final RuntimeException ex) {
                        throw ex;
                    }
                    catch (final Exception ex2) {
                        throw new UndeclaredThrowableException(ex2);
                    }
                    final Object annotationToField = annotationToField(invoke);
                    final Object put = hashMap.put(value, annotationToField);
                    if (put != null && !equals(put, annotationToField)) {
                        throw new IllegalArgumentException("Inconsistent values for descriptor field " + value + " from annotations: " + annotationToField + " :: " + put);
                    }
                }
            }
        }
        if (hashMap.isEmpty()) {
            return ImmutableDescriptor.EMPTY_DESCRIPTOR;
        }
        return new ImmutableDescriptor(hashMap);
    }
    
    static NotCompliantMBeanException throwException(final Class<?> clazz, final Throwable t) throws NotCompliantMBeanException, SecurityException {
        if (t instanceof SecurityException) {
            throw (SecurityException)t;
        }
        if (t instanceof NotCompliantMBeanException) {
            throw (NotCompliantMBeanException)t;
        }
        final NotCompliantMBeanException ex = new NotCompliantMBeanException(((clazz == null) ? "null class" : clazz.getName()) + ": " + ((t == null) ? "Not compliant" : t.getMessage()));
        ex.initCause(t);
        throw ex;
    }
    
    private static Object annotationToField(final Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number || o instanceof String || o instanceof Character || o instanceof Boolean || o instanceof String[]) {
            return o;
        }
        Class<?> class1 = o.getClass();
        if (class1.isArray()) {
            if (class1.getComponentType().isPrimitive()) {
                return o;
            }
            final Object[] array = (Object[])o;
            final String[] array2 = new String[array.length];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = (String)annotationToField(array[i]);
            }
            return array2;
        }
        else {
            if (o instanceof Class) {
                return ((Class)o).getName();
            }
            if (o instanceof Enum) {
                return ((Enum)o).name();
            }
            if (Proxy.isProxyClass(class1)) {
                class1 = class1.getInterfaces()[0];
            }
            throw new IllegalArgumentException("Illegal type for annotation element using @DescriptorKey: " + class1.getName());
        }
    }
    
    private static boolean equals(final Object o, final Object o2) {
        return Arrays.deepEquals(new Object[] { o }, new Object[] { o2 });
    }
    
    private static <T> Class<? super T> implementsMBean(final Class<T> clazz, final String s) {
        final String string = s + "MBean";
        if (clazz.getName().equals(string)) {
            return clazz;
        }
        final Class[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; ++i) {
            if (interfaces[i].getName().equals(string) && (Modifier.isPublic(interfaces[i].getModifiers()) || Introspector.ALLOW_NONPUBLIC_MBEAN)) {
                return (Class<? super T>)Util.cast(interfaces[i]);
            }
        }
        return null;
    }
    
    public static Object elementFromComplex(final Object o, final String s) throws AttributeNotFoundException {
        try {
            if (o.getClass().isArray() && s.equals("length")) {
                return Array.getLength(o);
            }
            if (o instanceof CompositeData) {
                return ((CompositeData)o).get(s);
            }
            final Class<?> class1 = o.getClass();
            Method method = null;
            if (BeansHelper.isAvailable()) {
                for (final Object o2 : BeansHelper.getPropertyDescriptors(BeansHelper.getBeanInfo(class1))) {
                    if (BeansHelper.getPropertyName(o2).equals(s)) {
                        method = BeansHelper.getReadMethod(o2);
                        break;
                    }
                }
            }
            else {
                method = SimpleIntrospector.getReadMethod(class1, s);
            }
            if (method != null) {
                ReflectUtil.checkPackageAccess(method.getDeclaringClass());
                return MethodUtil.invoke(method, o, new Class[0]);
            }
            throw new AttributeNotFoundException("Could not find the getter method for the property " + s + " using the Java Beans introspector");
        }
        catch (final InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        }
        catch (final AttributeNotFoundException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw EnvHelp.initCause(new AttributeNotFoundException(ex3.getMessage()), ex3);
        }
    }
    
    static {
        ALLOW_NONPUBLIC_MBEAN = Boolean.parseBoolean(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jdk.jmx.mbeans.allowNonPublic")));
    }
    
    private static class SimpleIntrospector
    {
        private static final String GET_METHOD_PREFIX = "get";
        private static final String IS_METHOD_PREFIX = "is";
        private static final Map<Class<?>, SoftReference<List<Method>>> cache;
        
        private static List<Method> getCachedMethods(final Class<?> clazz) {
            final SoftReference softReference = SimpleIntrospector.cache.get(clazz);
            if (softReference != null) {
                final List list = (List)softReference.get();
                if (list != null) {
                    return list;
                }
            }
            return null;
        }
        
        static boolean isReadMethod(final Method method) {
            if (Modifier.isStatic(method.getModifiers())) {
                return false;
            }
            final String name = method.getName();
            if (method.getParameterTypes().length == 0 && name.length() > 2) {
                if (name.startsWith("is")) {
                    return method.getReturnType() == Boolean.TYPE;
                }
                if (name.length() > 3 && name.startsWith("get")) {
                    return method.getReturnType() != Void.TYPE;
                }
            }
            return false;
        }
        
        static List<Method> getReadMethods(final Class<?> clazz) {
            final List<Method> cachedMethods = getCachedMethods(clazz);
            if (cachedMethods != null) {
                return cachedMethods;
            }
            final List<Method> eliminateCovariantMethods = MBeanAnalyzer.eliminateCovariantMethods(StandardMBeanIntrospector.getInstance().getMethods(clazz));
            final LinkedList list = new LinkedList();
            for (final Method method : eliminateCovariantMethods) {
                if (isReadMethod(method)) {
                    if (method.getName().startsWith("is")) {
                        list.add(0, method);
                    }
                    else {
                        list.add(method);
                    }
                }
            }
            SimpleIntrospector.cache.put(clazz, new SoftReference<List<Method>>(list));
            return list;
        }
        
        static Method getReadMethod(final Class<?> clazz, String string) {
            string = string.substring(0, 1).toUpperCase(Locale.ENGLISH) + string.substring(1);
            final String string2 = "get" + string;
            final String string3 = "is" + string;
            for (final Method method : getReadMethods(clazz)) {
                final String name = method.getName();
                if (name.equals(string3) || name.equals(string2)) {
                    return method;
                }
            }
            return null;
        }
        
        static {
            cache = Collections.synchronizedMap(new WeakHashMap<Class<?>, SoftReference<List<Method>>>());
        }
    }
    
    private static class BeansHelper
    {
        private static final Class<?> introspectorClass;
        private static final Class<?> beanInfoClass;
        private static final Class<?> getPropertyDescriptorClass;
        private static final Method getBeanInfo;
        private static final Method getPropertyDescriptors;
        private static final Method getPropertyName;
        private static final Method getReadMethod;
        
        private static Class<?> getClass(final String s) {
            try {
                return Class.forName(s, true, null);
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
        }
        
        private static Method getMethod(final Class<?> clazz, final String s, final Class<?>... array) {
            if (clazz != null) {
                try {
                    return clazz.getMethod(s, (Class[])array);
                }
                catch (final NoSuchMethodException ex) {
                    throw new AssertionError((Object)ex);
                }
            }
            return null;
        }
        
        static boolean isAvailable() {
            return BeansHelper.introspectorClass != null;
        }
        
        static Object getBeanInfo(final Class<?> clazz) throws Exception {
            try {
                return BeansHelper.getBeanInfo.invoke(null, clazz);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof Exception) {
                    throw (Exception)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        static Object[] getPropertyDescriptors(final Object o) {
            try {
                return (Object[])BeansHelper.getPropertyDescriptors.invoke(o, new Object[0]);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        static String getPropertyName(final Object o) {
            try {
                return (String)BeansHelper.getPropertyName.invoke(o, new Object[0]);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        static Method getReadMethod(final Object o) {
            try {
                return (Method)BeansHelper.getReadMethod.invoke(o, new Object[0]);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        static {
            introspectorClass = getClass("java.beans.Introspector");
            beanInfoClass = ((BeansHelper.introspectorClass == null) ? null : getClass("java.beans.BeanInfo"));
            getPropertyDescriptorClass = ((BeansHelper.beanInfoClass == null) ? null : getClass("java.beans.PropertyDescriptor"));
            getBeanInfo = getMethod(BeansHelper.introspectorClass, "getBeanInfo", Class.class);
            getPropertyDescriptors = getMethod(BeansHelper.beanInfoClass, "getPropertyDescriptors", (Class<?>[])new Class[0]);
            getPropertyName = getMethod(BeansHelper.getPropertyDescriptorClass, "getName", (Class<?>[])new Class[0]);
            getReadMethod = getMethod(BeansHelper.getPropertyDescriptorClass, "getReadMethod", (Class<?>[])new Class[0]);
        }
    }
}
