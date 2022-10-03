package com.sun.jmx.mbeanserver;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.PrivilegedAction;
import java.security.Permissions;
import java.lang.reflect.Modifier;
import java.security.Permission;
import javax.management.MBeanPermission;
import sun.reflect.misc.ConstructorUtil;
import java.io.IOException;
import javax.management.OperationsException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.management.MBeanException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import sun.reflect.misc.ReflectUtil;
import javax.management.RuntimeOperationsException;
import javax.management.NotCompliantMBeanException;
import java.util.Map;

public class MBeanInstantiator
{
    private final ModifiableClassLoaderRepository clr;
    private static final Map<String, Class<?>> primitiveClasses;
    
    MBeanInstantiator(final ModifiableClassLoaderRepository clr) {
        this.clr = clr;
    }
    
    public void testCreation(final Class<?> clazz) throws NotCompliantMBeanException {
        Introspector.testCreation(clazz);
    }
    
    public Class<?> findClassWithDefaultLoaderRepository(final String s) throws ReflectionException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
        }
        ReflectUtil.checkPackageAccess(s);
        Class<?> loadClass;
        try {
            if (this.clr == null) {
                throw new ClassNotFoundException(s);
            }
            loadClass = this.clr.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new ReflectionException(ex, "The MBean class could not be loaded by the default loader repository");
        }
        return loadClass;
    }
    
    public Class<?> findClass(final String s, final ClassLoader classLoader) throws ReflectionException {
        return loadClass(s, classLoader);
    }
    
    public Class<?> findClass(final String s, final ObjectName objectName) throws ReflectionException, InstanceNotFoundException {
        if (objectName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Null loader passed in parameter");
        }
        ClassLoader classLoader = null;
        synchronized (this) {
            classLoader = this.getClassLoader(objectName);
        }
        if (classLoader == null) {
            throw new InstanceNotFoundException("The loader named " + objectName + " is not registered in the MBeanServer");
        }
        return this.findClass(s, classLoader);
    }
    
    public Class<?>[] findSignatureClasses(final String[] array, final ClassLoader classLoader) throws ReflectionException {
        if (array == null) {
            return null;
        }
        final int length = array.length;
        final Class[] array2 = new Class[length];
        if (length == 0) {
            return array2;
        }
        try {
            for (int i = 0; i < length; ++i) {
                final Class clazz = MBeanInstantiator.primitiveClasses.get(array[i]);
                if (clazz != null) {
                    array2[i] = clazz;
                }
                else {
                    ReflectUtil.checkPackageAccess(array[i]);
                    if (classLoader != null) {
                        array2[i] = Class.forName(array[i], false, classLoader);
                    }
                    else {
                        array2[i] = this.findClass(array[i], this.getClass().getClassLoader());
                    }
                }
            }
        }
        catch (final ClassNotFoundException ex) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", ex);
            }
            throw new ReflectionException(ex, "The parameter class could not be found");
        }
        catch (final RuntimeException ex2) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", ex2);
            }
            throw ex2;
        }
        return array2;
    }
    
    public Object instantiate(final Class<?> clazz) throws ReflectionException, MBeanException {
        checkMBeanPermission(clazz, null, null, "instantiate");
        final Constructor<?> constructor = this.findConstructor(clazz, null);
        if (constructor == null) {
            throw new ReflectionException(new NoSuchMethodException("No such constructor"));
        }
        Object instance;
        try {
            ReflectUtil.checkPackageAccess(clazz);
            ensureClassAccess(clazz);
            instance = constructor.newInstance(new Object[0]);
        }
        catch (final InvocationTargetException ex) {
            final Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw new RuntimeMBeanException((RuntimeException)targetException, "RuntimeException thrown in the MBean's empty constructor");
            }
            if (targetException instanceof Error) {
                throw new RuntimeErrorException((Error)targetException, "Error thrown in the MBean's empty constructor");
            }
            throw new MBeanException((Exception)targetException, "Exception thrown in the MBean's empty constructor");
        }
        catch (final NoSuchMethodError noSuchMethodError) {
            throw new ReflectionException(new NoSuchMethodException("No constructor"), "No such constructor");
        }
        catch (final InstantiationException ex2) {
            throw new ReflectionException(ex2, "Exception thrown trying to invoke the MBean's empty constructor");
        }
        catch (final IllegalAccessException ex3) {
            throw new ReflectionException(ex3, "Exception thrown trying to invoke the MBean's empty constructor");
        }
        catch (final IllegalArgumentException ex4) {
            throw new ReflectionException(ex4, "Exception thrown trying to invoke the MBean's empty constructor");
        }
        return instance;
    }
    
    public Object instantiate(final Class<?> clazz, final Object[] array, final String[] array2, final ClassLoader classLoader) throws ReflectionException, MBeanException {
        checkMBeanPermission(clazz, null, null, "instantiate");
        Class<?>[] array3;
        try {
            final ClassLoader classLoader2 = clazz.getClassLoader();
            array3 = (Class<?>[])((array2 == null) ? null : this.findSignatureClasses(array2, classLoader2));
        }
        catch (final IllegalArgumentException ex) {
            throw new ReflectionException(ex, "The constructor parameter classes could not be loaded");
        }
        final Constructor<?> constructor = this.findConstructor(clazz, array3);
        if (constructor == null) {
            throw new ReflectionException(new NoSuchMethodException("No such constructor"));
        }
        Object instance;
        try {
            ReflectUtil.checkPackageAccess(clazz);
            ensureClassAccess(clazz);
            instance = constructor.newInstance(array);
        }
        catch (final NoSuchMethodError noSuchMethodError) {
            throw new ReflectionException(new NoSuchMethodException("No such constructor found"), "No such constructor");
        }
        catch (final InstantiationException ex2) {
            throw new ReflectionException(ex2, "Exception thrown trying to invoke the MBean's constructor");
        }
        catch (final IllegalAccessException ex3) {
            throw new ReflectionException(ex3, "Exception thrown trying to invoke the MBean's constructor");
        }
        catch (final InvocationTargetException ex4) {
            final Throwable targetException = ex4.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw new RuntimeMBeanException((RuntimeException)targetException, "RuntimeException thrown in the MBean's constructor");
            }
            if (targetException instanceof Error) {
                throw new RuntimeErrorException((Error)targetException, "Error thrown in the MBean's constructor");
            }
            throw new MBeanException((Exception)targetException, "Exception thrown in the MBean's constructor");
        }
        return instance;
    }
    
    public ObjectInputStream deserialize(final ClassLoader classLoader, final byte[] array) throws OperationsException {
        if (array == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
        }
        if (array.length == 0) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        ObjectInputStreamWithLoader objectInputStreamWithLoader;
        try {
            objectInputStreamWithLoader = new ObjectInputStreamWithLoader(byteArrayInputStream, classLoader);
        }
        catch (final IOException ex) {
            throw new OperationsException("An IOException occurred trying to de-serialize the data");
        }
        return objectInputStreamWithLoader;
    }
    
    public ObjectInputStream deserialize(final String s, final ObjectName objectName, final byte[] array, final ClassLoader classLoader) throws InstanceNotFoundException, OperationsException, ReflectionException {
        if (array == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Null data passed in parameter");
        }
        if (array.length == 0) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Empty data passed in parameter");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
        }
        ReflectUtil.checkPackageAccess(s);
        Class<?> clazz;
        if (objectName == null) {
            clazz = this.findClass(s, classLoader);
        }
        else {
            try {
                final ClassLoader classLoader2 = this.getClassLoader(objectName);
                if (classLoader2 == null) {
                    throw new ClassNotFoundException(s);
                }
                clazz = Class.forName(s, false, classLoader2);
            }
            catch (final ClassNotFoundException ex) {
                throw new ReflectionException(ex, "The MBean class could not be loaded by the " + objectName.toString() + " class loader");
            }
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
        ObjectInputStreamWithLoader objectInputStreamWithLoader;
        try {
            objectInputStreamWithLoader = new ObjectInputStreamWithLoader(byteArrayInputStream, clazz.getClassLoader());
        }
        catch (final IOException ex2) {
            throw new OperationsException("An IOException occurred trying to de-serialize the data");
        }
        return objectInputStreamWithLoader;
    }
    
    public Object instantiate(final String s) throws ReflectionException, MBeanException {
        return this.instantiate(s, null, null, null);
    }
    
    public Object instantiate(final String s, final ObjectName objectName, final ClassLoader classLoader) throws ReflectionException, MBeanException, InstanceNotFoundException {
        return this.instantiate(s, objectName, null, null, classLoader);
    }
    
    public Object instantiate(final String s, final Object[] array, final String[] array2, final ClassLoader classLoader) throws ReflectionException, MBeanException {
        return this.instantiate(this.findClassWithDefaultLoaderRepository(s), array, array2, classLoader);
    }
    
    public Object instantiate(final String s, final ObjectName objectName, final Object[] array, final String[] array2, final ClassLoader classLoader) throws ReflectionException, MBeanException, InstanceNotFoundException {
        Class<?> clazz;
        if (objectName == null) {
            clazz = this.findClass(s, classLoader);
        }
        else {
            clazz = this.findClass(s, objectName);
        }
        return this.instantiate(clazz, array, array2, classLoader);
    }
    
    public ModifiableClassLoaderRepository getClassLoaderRepository() {
        checkMBeanPermission((String)null, null, null, "getClassLoaderRepository");
        return this.clr;
    }
    
    static Class<?> loadClass(final String s, ClassLoader classLoader) throws ReflectionException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during object instantiation");
        }
        ReflectUtil.checkPackageAccess(s);
        Class<?> clazz;
        try {
            if (classLoader == null) {
                classLoader = MBeanInstantiator.class.getClassLoader();
            }
            if (classLoader != null) {
                clazz = Class.forName(s, false, classLoader);
            }
            else {
                clazz = Class.forName(s);
            }
        }
        catch (final ClassNotFoundException ex) {
            throw new ReflectionException(ex, "The MBean class could not be loaded");
        }
        return clazz;
    }
    
    static Class<?>[] loadSignatureClasses(final String[] array, final ClassLoader classLoader) throws ReflectionException {
        if (array == null) {
            return null;
        }
        final ClassLoader classLoader2 = (classLoader == null) ? MBeanInstantiator.class.getClassLoader() : classLoader;
        final int length = array.length;
        final Class[] array2 = new Class[length];
        if (length == 0) {
            return array2;
        }
        try {
            for (int i = 0; i < length; ++i) {
                final Class clazz = MBeanInstantiator.primitiveClasses.get(array[i]);
                if (clazz != null) {
                    array2[i] = clazz;
                }
                else {
                    ReflectUtil.checkPackageAccess(array[i]);
                    array2[i] = Class.forName(array[i], false, classLoader2);
                }
            }
        }
        catch (final ClassNotFoundException ex) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "The parameter class could not be found", ex);
            }
            throw new ReflectionException(ex, "The parameter class could not be found");
        }
        catch (final RuntimeException ex2) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, MBeanInstantiator.class.getName(), "findSignatureClasses", "Unexpected exception", ex2);
            }
            throw ex2;
        }
        return array2;
    }
    
    private Constructor<?> findConstructor(final Class<?> clazz, final Class<?>[] array) {
        try {
            return ConstructorUtil.getConstructor(clazz, array);
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    private static void checkMBeanPermission(final Class<?> clazz, final String s, final ObjectName objectName, final String s2) {
        if (clazz != null) {
            checkMBeanPermission(clazz.getName(), s, objectName, s2);
        }
    }
    
    private static void checkMBeanPermission(final String s, final String s2, final ObjectName objectName, final String s3) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanPermission(s, s2, objectName, s3));
        }
    }
    
    private static void ensureClassAccess(final Class clazz) throws IllegalAccessException {
        if (!Modifier.isPublic(clazz.getModifiers())) {
            throw new IllegalAccessException("Class is not public and can't be instantiated");
        }
    }
    
    private ClassLoader getClassLoader(final ObjectName objectName) {
        if (this.clr == null) {
            return null;
        }
        final Permissions permissions = new Permissions();
        permissions.add(new MBeanPermission("*", null, objectName, "getClassLoader"));
        return AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return MBeanInstantiator.this.clr.getClassLoader(objectName);
            }
        }, new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) }));
    }
    
    static {
        primitiveClasses = Util.newMap();
        for (final Class clazz : new Class[] { Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Character.TYPE, Boolean.TYPE }) {
            MBeanInstantiator.primitiveClasses.put(clazz.getName(), clazz);
        }
    }
}
