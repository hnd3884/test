package com.sun.naming.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import javax.naming.NamingEnumeration;
import java.io.InputStream;
import java.io.IOException;
import javax.naming.ConfigurationException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import javax.naming.Context;
import javax.naming.NamingException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.WeakHashMap;

public final class ResourceManager
{
    private static final String PROVIDER_RESOURCE_FILE_NAME = "jndiprovider.properties";
    private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";
    private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";
    private static final String DISABLE_APP_RESOURCE_FILES = "com.sun.naming.disable.app.resource.files";
    private static final String[] listProperties;
    private static final VersionHelper helper;
    private static final WeakHashMap<Object, Hashtable<? super String, Object>> propertiesCache;
    private static final WeakHashMap<ClassLoader, Map<String, List<NamedWeakReference<Object>>>> factoryCache;
    private static final WeakHashMap<ClassLoader, Map<String, WeakReference<Object>>> urlFactoryCache;
    private static final WeakReference<Object> NO_FACTORY;
    
    private ResourceManager() {
    }
    
    public static Hashtable<?, ?> getInitialEnvironment(Hashtable<?, ?> hashtable) throws NamingException {
        final String[] props = VersionHelper.PROPS;
        if (hashtable == null) {
            hashtable = new Hashtable<Object, String>(11);
        }
        final String value = hashtable.get("java.naming.applet");
        final String[] jndiProperties = ResourceManager.helper.getJndiProperties();
        for (int i = 0; i < props.length; ++i) {
            Object o = hashtable.get(props[i]);
            if (o == null) {
                if (value != null) {
                    o = AppletParameter.get(value, props[i]);
                }
                if (o == null) {
                    o = ((jndiProperties != null) ? jndiProperties[i] : ResourceManager.helper.getJndiProperty(i));
                }
                if (o != null) {
                    hashtable.put((Object)props[i], (Object)o);
                }
            }
        }
        final String s = hashtable.get("com.sun.naming.disable.app.resource.files");
        if (s != null && s.equalsIgnoreCase("true")) {
            return hashtable;
        }
        mergeTables((Hashtable<? super String, Object>)hashtable, getApplicationResources());
        return hashtable;
    }
    
    public static String getProperty(final String s, final Hashtable<?, ?> hashtable, final Context context, final boolean b) throws NamingException {
        final String s2 = (hashtable != null) ? ((String)hashtable.get(s)) : null;
        if (context == null || (s2 != null && !b)) {
            return s2;
        }
        final String s3 = getProviderResource(context).get(s);
        if (s2 == null) {
            return s3;
        }
        if (s3 == null || !b) {
            return s2;
        }
        return s2 + ":" + s3;
    }
    
    public static FactoryEnumeration getFactories(final String s, final Hashtable<?, ?> hashtable, final Context context) throws NamingException {
        final String property = getProperty(s, hashtable, context, true);
        if (property == null) {
            return null;
        }
        final ClassLoader contextClassLoader = ResourceManager.helper.getContextClassLoader();
        Object o = null;
        synchronized (ResourceManager.factoryCache) {
            o = ResourceManager.factoryCache.get(contextClassLoader);
            if (o == null) {
                o = new HashMap<String, ArrayList>(11);
                ResourceManager.factoryCache.put(contextClassLoader, (Map<String, List<NamedWeakReference<Object>>>)o);
            }
        }
        synchronized (o) {
            final List list = ((Map<String, List>)o).get(property);
            if (list != null) {
                return (list.size() == 0) ? null : new FactoryEnumeration(list, contextClassLoader);
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(property, ":");
            final ArrayList list2 = new ArrayList(5);
            while (stringTokenizer.hasMoreTokens()) {
                try {
                    final String nextToken = stringTokenizer.nextToken();
                    list2.add(new NamedWeakReference(ResourceManager.helper.loadClass(nextToken, contextClassLoader), nextToken));
                }
                catch (final Exception ex) {}
            }
            ((Map<String, ArrayList>)o).put(property, list2);
            return new FactoryEnumeration(list2, contextClassLoader);
        }
    }
    
    public static Object getFactory(final String s, final Hashtable<?, ?> hashtable, final Context context, final String s2, final String s3) throws NamingException {
        final String property = getProperty(s, hashtable, context, true);
        String string;
        if (property != null) {
            string = property + ":" + s3;
        }
        else {
            string = s3;
        }
        final ClassLoader contextClassLoader = ResourceManager.helper.getContextClassLoader();
        final String string2 = s2 + " " + string;
        Object o = null;
        synchronized (ResourceManager.urlFactoryCache) {
            o = ResourceManager.urlFactoryCache.get(contextClassLoader);
            if (o == null) {
                o = new HashMap<String, WeakReference>(11);
                ResourceManager.urlFactoryCache.put(contextClassLoader, (Map<String, WeakReference<Object>>)o);
            }
        }
        synchronized (o) {
            Object o2 = null;
            final WeakReference weakReference = ((Map<String, WeakReference>)o).get(string2);
            if (weakReference == ResourceManager.NO_FACTORY) {
                return null;
            }
            if (weakReference != null) {
                o2 = weakReference.get();
                if (o2 != null) {
                    return o2;
                }
            }
            final StringTokenizer stringTokenizer = new StringTokenizer(string, ":");
            while (o2 == null && stringTokenizer.hasMoreTokens()) {
                final String string3 = stringTokenizer.nextToken() + s2;
                try {
                    o2 = ResourceManager.helper.loadClass(string3, contextClassLoader).newInstance();
                    continue;
                }
                catch (final InstantiationException rootCause) {
                    final NamingException ex = new NamingException("Cannot instantiate " + string3);
                    ex.setRootCause(rootCause);
                    throw ex;
                }
                catch (final IllegalAccessException rootCause2) {
                    final NamingException ex2 = new NamingException("Cannot access " + string3);
                    ex2.setRootCause(rootCause2);
                    throw ex2;
                }
                catch (final Exception ex3) {
                    continue;
                }
                break;
            }
            ((Map<String, WeakReference<Object>>)o).put(string2, (o2 != null) ? new WeakReference<Object>(o2) : ResourceManager.NO_FACTORY);
            return o2;
        }
    }
    
    private static Hashtable<? super String, Object> getProviderResource(final Object o) throws NamingException {
        if (o == null) {
            return new Hashtable<Object, Object>(1);
        }
        synchronized (ResourceManager.propertiesCache) {
            final Class<?> class1 = o.getClass();
            final Hashtable hashtable = ResourceManager.propertiesCache.get(class1);
            if (hashtable != null) {
                return hashtable;
            }
            final Properties properties = new Properties();
            final InputStream resourceAsStream = ResourceManager.helper.getResourceAsStream(class1, "jndiprovider.properties");
            if (resourceAsStream != null) {
                try {
                    properties.load(resourceAsStream);
                }
                catch (final IOException rootCause) {
                    final ConfigurationException ex = new ConfigurationException("Error reading provider resource file for " + class1);
                    ex.setRootCause(rootCause);
                    throw ex;
                }
            }
            ResourceManager.propertiesCache.put(class1, properties);
            return properties;
        }
    }
    
    private static Hashtable<? super String, Object> getApplicationResources() throws NamingException {
        final ClassLoader contextClassLoader = ResourceManager.helper.getContextClassLoader();
        synchronized (ResourceManager.propertiesCache) {
            Hashtable<? super String, Object> hashtable = ResourceManager.propertiesCache.get(contextClassLoader);
            if (hashtable != null) {
                return hashtable;
            }
            try {
                final NamingEnumeration<InputStream> resources = ResourceManager.helper.getResources(contextClassLoader, "jndi.properties");
                try {
                    while (resources.hasMore()) {
                        final Properties properties = new Properties();
                        final InputStream inputStream = resources.next();
                        try {
                            properties.load(inputStream);
                        }
                        finally {
                            inputStream.close();
                        }
                        if (hashtable == null) {
                            hashtable = properties;
                        }
                        else {
                            mergeTables(hashtable, properties);
                        }
                    }
                    while (resources.hasMore()) {
                        resources.next().close();
                    }
                }
                finally {
                    while (resources.hasMore()) {
                        resources.next().close();
                    }
                }
                final InputStream javaHomeLibStream = ResourceManager.helper.getJavaHomeLibStream("jndi.properties");
                if (javaHomeLibStream != null) {
                    try {
                        final Properties properties2 = new Properties();
                        properties2.load(javaHomeLibStream);
                        if (hashtable == null) {
                            hashtable = properties2;
                        }
                        else {
                            mergeTables(hashtable, properties2);
                        }
                    }
                    finally {
                        javaHomeLibStream.close();
                    }
                }
            }
            catch (final IOException rootCause) {
                final ConfigurationException ex = new ConfigurationException("Error reading application resource file");
                ex.setRootCause(rootCause);
                throw ex;
            }
            if (hashtable == null) {
                hashtable = new Hashtable<Object, Object>(11);
            }
            ResourceManager.propertiesCache.put(contextClassLoader, hashtable);
            return hashtable;
        }
    }
    
    private static void mergeTables(final Hashtable<? super String, Object> hashtable, final Hashtable<? super String, Object> hashtable2) {
        for (final String s : hashtable2.keySet()) {
            final String value = hashtable.get(s);
            if (value == null) {
                hashtable.put(s, hashtable2.get(s));
            }
            else {
                if (!isListProperty(s)) {
                    continue;
                }
                hashtable.put(s, value + ":" + hashtable2.get(s));
            }
        }
    }
    
    private static boolean isListProperty(String intern) {
        intern = intern.intern();
        for (int i = 0; i < ResourceManager.listProperties.length; ++i) {
            if (intern == ResourceManager.listProperties[i]) {
                return true;
            }
        }
        return false;
    }
    
    static {
        listProperties = new String[] { "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.factory.control" };
        helper = VersionHelper.getVersionHelper();
        propertiesCache = new WeakHashMap<Object, Hashtable<? super String, Object>>(11);
        factoryCache = new WeakHashMap<ClassLoader, Map<String, List<NamedWeakReference<Object>>>>(11);
        urlFactoryCache = new WeakHashMap<ClassLoader, Map<String, WeakReference<Object>>>(11);
        NO_FACTORY = new WeakReference<Object>(null);
    }
    
    private static class AppletParameter
    {
        private static final Class<?> clazz;
        private static final Method getMethod;
        
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
        
        static Object get(final Object o, final String s) {
            if (AppletParameter.clazz == null || !AppletParameter.clazz.isInstance(o)) {
                throw new ClassCastException(o.getClass().getName());
            }
            try {
                return AppletParameter.getMethod.invoke(o, s);
            }
            catch (final InvocationTargetException | IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }
        
        static {
            clazz = getClass("java.applet.Applet");
            getMethod = getMethod(AppletParameter.clazz, "getParameter", String.class);
        }
    }
}
