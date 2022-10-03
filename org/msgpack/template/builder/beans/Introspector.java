package org.msgpack.template.builder.beans;

import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Map;

public class Introspector
{
    public static final int IGNORE_ALL_BEANINFO = 3;
    public static final int IGNORE_IMMEDIATE_BEANINFO = 2;
    public static final int USE_ALL_BEANINFO = 1;
    private static final String DEFAULT_BEANINFO_SEARCHPATH = "sun.beans.infos";
    private static String[] searchPath;
    private static final int DEFAULT_CAPACITY = 128;
    private static Map<Class<?>, StandardBeanInfo> theCache;
    
    private Introspector() {
    }
    
    public static String decapitalize(final String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 0 || (name.length() > 1 && Character.isUpperCase(name.charAt(1)))) {
            return name;
        }
        final char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
    
    public static void flushCaches() {
        Introspector.theCache.clear();
    }
    
    public static void flushFromCaches(final Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        Introspector.theCache.remove(clazz);
    }
    
    public static BeanInfo getBeanInfo(final Class<?> beanClass) throws IntrospectionException {
        StandardBeanInfo beanInfo = Introspector.theCache.get(beanClass);
        if (beanInfo == null) {
            beanInfo = getBeanInfoImplAndInit(beanClass, null, 1);
            Introspector.theCache.put(beanClass, beanInfo);
        }
        return beanInfo;
    }
    
    public static BeanInfo getBeanInfo(final Class<?> beanClass, final Class<?> stopClass) throws IntrospectionException {
        if (stopClass == null) {
            return getBeanInfo(beanClass);
        }
        return getBeanInfoImplAndInit(beanClass, stopClass, 1);
    }
    
    public static BeanInfo getBeanInfo(final Class<?> beanClass, final int flags) throws IntrospectionException {
        if (flags == 1) {
            return getBeanInfo(beanClass);
        }
        return getBeanInfoImplAndInit(beanClass, null, flags);
    }
    
    public static String[] getBeanInfoSearchPath() {
        final String[] path = new String[Introspector.searchPath.length];
        System.arraycopy(Introspector.searchPath, 0, path, 0, Introspector.searchPath.length);
        return path;
    }
    
    public static void setBeanInfoSearchPath(final String[] path) {
        if (System.getSecurityManager() != null) {
            System.getSecurityManager().checkPropertiesAccess();
        }
        Introspector.searchPath = path;
    }
    
    private static StandardBeanInfo getBeanInfoImpl(final Class<?> beanClass, final Class<?> stopClass, final int flags) throws IntrospectionException {
        BeanInfo explicitInfo = null;
        if (flags == 1) {
            explicitInfo = getExplicitBeanInfo(beanClass);
        }
        final StandardBeanInfo beanInfo = new StandardBeanInfo(beanClass, explicitInfo, stopClass);
        if (beanInfo.additionalBeanInfo != null) {
            for (int i = beanInfo.additionalBeanInfo.length - 1; i >= 0; --i) {
                final BeanInfo info = beanInfo.additionalBeanInfo[i];
                beanInfo.mergeBeanInfo(info, true);
            }
        }
        final Class<?> beanSuperClass = beanClass.getSuperclass();
        if (beanSuperClass != stopClass) {
            if (beanSuperClass == null) {
                throw new IntrospectionException("Stop class is not super class of bean class");
            }
            final int superflags = (flags == 2) ? 1 : flags;
            final BeanInfo superBeanInfo = getBeanInfoImpl(beanSuperClass, stopClass, superflags);
            if (superBeanInfo != null) {
                beanInfo.mergeBeanInfo(superBeanInfo, false);
            }
        }
        return beanInfo;
    }
    
    private static BeanInfo getExplicitBeanInfo(final Class<?> beanClass) {
        String beanInfoClassName = beanClass.getName() + "BeanInfo";
        try {
            return loadBeanInfo(beanInfoClassName, beanClass);
        }
        catch (final Exception e) {
            final int index = beanInfoClassName.lastIndexOf(46);
            final String beanInfoName = (index >= 0) ? beanInfoClassName.substring(index + 1) : beanInfoClassName;
            BeanInfo theBeanInfo = null;
            BeanDescriptor beanDescriptor = null;
            for (int i = 0; i < Introspector.searchPath.length; ++i) {
                beanInfoClassName = Introspector.searchPath[i] + "." + beanInfoName;
                try {
                    theBeanInfo = loadBeanInfo(beanInfoClassName, beanClass);
                }
                catch (final Exception e2) {
                    continue;
                }
                beanDescriptor = theBeanInfo.getBeanDescriptor();
                if (beanDescriptor != null && beanClass == beanDescriptor.getBeanClass()) {
                    return theBeanInfo;
                }
            }
            if (BeanInfo.class.isAssignableFrom(beanClass)) {
                try {
                    return loadBeanInfo(beanClass.getName(), beanClass);
                }
                catch (final Exception ex) {}
            }
            return null;
        }
    }
    
    private static BeanInfo loadBeanInfo(final String beanInfoClassName, final Class<?> beanClass) throws Exception {
        try {
            final ClassLoader cl = beanClass.getClassLoader();
            if (cl != null) {
                return (BeanInfo)Class.forName(beanInfoClassName, true, beanClass.getClassLoader()).newInstance();
            }
        }
        catch (final Exception ex) {}
        try {
            return (BeanInfo)Class.forName(beanInfoClassName, true, ClassLoader.getSystemClassLoader()).newInstance();
        }
        catch (final Exception e) {
            return (BeanInfo)Class.forName(beanInfoClassName, true, Thread.currentThread().getContextClassLoader()).newInstance();
        }
    }
    
    private static StandardBeanInfo getBeanInfoImplAndInit(final Class<?> beanClass, final Class<?> stopClass, final int flag) throws IntrospectionException {
        final StandardBeanInfo standardBeanInfo = getBeanInfoImpl(beanClass, stopClass, flag);
        standardBeanInfo.init();
        return standardBeanInfo;
    }
    
    static {
        Introspector.searchPath = new String[] { "sun.beans.infos" };
        Introspector.theCache = Collections.synchronizedMap(new WeakHashMap<Class<?>, StandardBeanInfo>(128));
    }
}
