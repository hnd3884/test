package com.sun.jmx.mbeanserver;

import javax.management.MBeanServerPermission;
import java.security.Permission;
import javax.management.MBeanPermission;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import java.security.PrivilegedExceptionAction;
import javax.management.RuntimeOperationsException;
import javax.management.OperationsException;
import java.io.ObjectInputStream;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.InstanceNotFoundException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import com.sun.jmx.interceptor.DefaultMBeanServerInterceptor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.management.loading.ClassLoaderRepository;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServer;

public final class JmxMBeanServer implements SunJmxMBeanServer
{
    public static final boolean DEFAULT_FAIR_LOCK_POLICY = true;
    private final MBeanInstantiator instantiator;
    private final SecureClassLoaderRepository secureClr;
    private final boolean interceptorsEnabled;
    private final MBeanServer outerShell;
    private volatile MBeanServer mbsInterceptor;
    private final MBeanServerDelegate mBeanServerDelegateObject;
    
    JmxMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate) {
        this(s, mBeanServer, mBeanServerDelegate, null, false);
    }
    
    JmxMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate, final boolean b) {
        this(s, mBeanServer, mBeanServerDelegate, null, false);
    }
    
    JmxMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate, final MBeanInstantiator mBeanInstantiator, final boolean b) {
        this(s, mBeanServer, mBeanServerDelegate, mBeanInstantiator, b, true);
    }
    
    JmxMBeanServer(final String s, MBeanServer outerShell, MBeanServerDelegate mBeanServerDelegateObject, MBeanInstantiator instantiator, final boolean interceptorsEnabled, final boolean b) {
        this.mbsInterceptor = null;
        if (instantiator == null) {
            instantiator = new MBeanInstantiator(new ClassLoaderRepositorySupport());
        }
        this.secureClr = new SecureClassLoaderRepository(AccessController.doPrivileged((PrivilegedAction<ClassLoaderRepository>)new PrivilegedAction<ClassLoaderRepository>() {
            @Override
            public ClassLoaderRepository run() {
                return instantiator.getClassLoaderRepository();
            }
        }));
        if (mBeanServerDelegateObject == null) {
            mBeanServerDelegateObject = new MBeanServerDelegateImpl();
        }
        if (outerShell == null) {
            outerShell = this;
        }
        this.instantiator = instantiator;
        this.mBeanServerDelegateObject = mBeanServerDelegateObject;
        this.outerShell = outerShell;
        this.mbsInterceptor = new DefaultMBeanServerInterceptor(outerShell, mBeanServerDelegateObject, instantiator, new Repository(s));
        this.interceptorsEnabled = interceptorsEnabled;
        this.initialize();
    }
    
    @Override
    public boolean interceptorsEnabled() {
        return this.interceptorsEnabled;
    }
    
    @Override
    public MBeanInstantiator getMBeanInstantiator() {
        if (this.interceptorsEnabled) {
            return this.instantiator;
        }
        throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        return this.mbsInterceptor.createMBean(s, this.cloneObjectName(objectName), null, null);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return this.mbsInterceptor.createMBean(s, this.cloneObjectName(objectName), objectName2, null, null);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        return this.mbsInterceptor.createMBean(s, this.cloneObjectName(objectName), array, array2);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return this.mbsInterceptor.createMBean(s, this.cloneObjectName(objectName), objectName2, array, array2);
    }
    
    @Override
    public ObjectInstance registerMBean(final Object o, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        return this.mbsInterceptor.registerMBean(o, this.cloneObjectName(objectName));
    }
    
    @Override
    public void unregisterMBean(final ObjectName objectName) throws InstanceNotFoundException, MBeanRegistrationException {
        this.mbsInterceptor.unregisterMBean(this.cloneObjectName(objectName));
    }
    
    @Override
    public ObjectInstance getObjectInstance(final ObjectName objectName) throws InstanceNotFoundException {
        return this.mbsInterceptor.getObjectInstance(this.cloneObjectName(objectName));
    }
    
    @Override
    public Set<ObjectInstance> queryMBeans(final ObjectName objectName, final QueryExp queryExp) {
        return this.mbsInterceptor.queryMBeans(this.cloneObjectName(objectName), queryExp);
    }
    
    @Override
    public Set<ObjectName> queryNames(final ObjectName objectName, final QueryExp queryExp) {
        return this.mbsInterceptor.queryNames(this.cloneObjectName(objectName), queryExp);
    }
    
    @Override
    public boolean isRegistered(final ObjectName objectName) {
        return this.mbsInterceptor.isRegistered(objectName);
    }
    
    @Override
    public Integer getMBeanCount() {
        return this.mbsInterceptor.getMBeanCount();
    }
    
    @Override
    public Object getAttribute(final ObjectName objectName, final String s) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        return this.mbsInterceptor.getAttribute(this.cloneObjectName(objectName), s);
    }
    
    @Override
    public AttributeList getAttributes(final ObjectName objectName, final String[] array) throws InstanceNotFoundException, ReflectionException {
        return this.mbsInterceptor.getAttributes(this.cloneObjectName(objectName), array);
    }
    
    @Override
    public void setAttribute(final ObjectName objectName, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        this.mbsInterceptor.setAttribute(this.cloneObjectName(objectName), this.cloneAttribute(attribute));
    }
    
    @Override
    public AttributeList setAttributes(final ObjectName objectName, final AttributeList list) throws InstanceNotFoundException, ReflectionException {
        return this.mbsInterceptor.setAttributes(this.cloneObjectName(objectName), this.cloneAttributeList(list));
    }
    
    @Override
    public Object invoke(final ObjectName objectName, final String s, final Object[] array, final String[] array2) throws InstanceNotFoundException, MBeanException, ReflectionException {
        return this.mbsInterceptor.invoke(this.cloneObjectName(objectName), s, array, array2);
    }
    
    @Override
    public String getDefaultDomain() {
        return this.mbsInterceptor.getDefaultDomain();
    }
    
    @Override
    public String[] getDomains() {
        return this.mbsInterceptor.getDomains();
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        this.mbsInterceptor.addNotificationListener(this.cloneObjectName(objectName), notificationListener, notificationFilter, o);
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        this.mbsInterceptor.addNotificationListener(this.cloneObjectName(objectName), objectName2, notificationFilter, o);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws InstanceNotFoundException, ListenerNotFoundException {
        this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(objectName), notificationListener);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(objectName), notificationListener, notificationFilter, o);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2) throws InstanceNotFoundException, ListenerNotFoundException {
        this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(objectName), objectName2);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        this.mbsInterceptor.removeNotificationListener(this.cloneObjectName(objectName), objectName2, notificationFilter, o);
    }
    
    @Override
    public MBeanInfo getMBeanInfo(final ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        return this.mbsInterceptor.getMBeanInfo(this.cloneObjectName(objectName));
    }
    
    @Override
    public Object instantiate(final String s) throws ReflectionException, MBeanException {
        checkMBeanPermission(s, null, null, "instantiate");
        return this.instantiator.instantiate(s);
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
        checkMBeanPermission(s, null, null, "instantiate");
        return this.instantiator.instantiate(s, objectName, this.outerShell.getClass().getClassLoader());
    }
    
    @Override
    public Object instantiate(final String s, final Object[] array, final String[] array2) throws ReflectionException, MBeanException {
        checkMBeanPermission(s, null, null, "instantiate");
        return this.instantiator.instantiate(s, array, array2, this.outerShell.getClass().getClassLoader());
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, MBeanException, InstanceNotFoundException {
        checkMBeanPermission(s, null, null, "instantiate");
        return this.instantiator.instantiate(s, objectName, array, array2, this.outerShell.getClass().getClassLoader());
    }
    
    @Override
    public boolean isInstanceOf(final ObjectName objectName, final String s) throws InstanceNotFoundException {
        return this.mbsInterceptor.isInstanceOf(this.cloneObjectName(objectName), s);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final ObjectName objectName, final byte[] array) throws InstanceNotFoundException, OperationsException {
        return this.instantiator.deserialize(this.getClassLoaderFor(objectName), array);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final String s, final byte[] array) throws OperationsException, ReflectionException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(), "Null className passed in parameter");
        }
        final ClassLoaderRepository classLoaderRepository = this.getClassLoaderRepository();
        Class<?> loadClass;
        try {
            if (classLoaderRepository == null) {
                throw new ClassNotFoundException(s);
            }
            loadClass = classLoaderRepository.loadClass(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new ReflectionException(ex, "The given class could not be loaded by the default loader repository");
        }
        return this.instantiator.deserialize(loadClass.getClassLoader(), array);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final String s, ObjectName cloneObjectName, final byte[] array) throws InstanceNotFoundException, OperationsException, ReflectionException {
        cloneObjectName = this.cloneObjectName(cloneObjectName);
        try {
            this.getClassLoader(cloneObjectName);
        }
        catch (final SecurityException ex) {
            throw ex;
        }
        catch (final Exception ex2) {}
        return this.instantiator.deserialize(s, cloneObjectName, array, this.outerShell.getClass().getClassLoader());
    }
    
    private void initialize() {
        if (this.instantiator == null) {
            throw new IllegalStateException("instantiator must not be null.");
        }
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    JmxMBeanServer.this.mbsInterceptor.registerMBean(JmxMBeanServer.this.mBeanServerDelegateObject, MBeanServerDelegate.DELEGATE_NAME);
                    return null;
                }
            });
        }
        catch (final SecurityException ex) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected security exception occurred", ex);
            }
            throw ex;
        }
        catch (final Exception ex2) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, JmxMBeanServer.class.getName(), "initialize", "Unexpected exception occurred", ex2);
            }
            throw new IllegalStateException("Can't register delegate.", ex2);
        }
        final ClassLoader classLoader = this.outerShell.getClass().getClassLoader();
        final ModifiableClassLoaderRepository modifiableClassLoaderRepository = AccessController.doPrivileged((PrivilegedAction<ModifiableClassLoaderRepository>)new PrivilegedAction<ModifiableClassLoaderRepository>() {
            @Override
            public ModifiableClassLoaderRepository run() {
                return JmxMBeanServer.this.instantiator.getClassLoaderRepository();
            }
        });
        if (modifiableClassLoaderRepository != null) {
            modifiableClassLoaderRepository.addClassLoader(classLoader);
            final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
            if (systemClassLoader != classLoader) {
                modifiableClassLoaderRepository.addClassLoader(systemClassLoader);
            }
        }
    }
    
    @Override
    public synchronized MBeanServer getMBeanServerInterceptor() {
        if (this.interceptorsEnabled) {
            return this.mbsInterceptor;
        }
        throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
    }
    
    @Override
    public synchronized void setMBeanServerInterceptor(final MBeanServer mbsInterceptor) {
        if (!this.interceptorsEnabled) {
            throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
        }
        if (mbsInterceptor == null) {
            throw new IllegalArgumentException("MBeanServerInterceptor is null");
        }
        this.mbsInterceptor = mbsInterceptor;
    }
    
    @Override
    public ClassLoader getClassLoaderFor(final ObjectName objectName) throws InstanceNotFoundException {
        return this.mbsInterceptor.getClassLoaderFor(this.cloneObjectName(objectName));
    }
    
    @Override
    public ClassLoader getClassLoader(final ObjectName objectName) throws InstanceNotFoundException {
        return this.mbsInterceptor.getClassLoader(this.cloneObjectName(objectName));
    }
    
    @Override
    public ClassLoaderRepository getClassLoaderRepository() {
        checkMBeanPermission(null, null, null, "getClassLoaderRepository");
        return this.secureClr;
    }
    
    @Override
    public MBeanServerDelegate getMBeanServerDelegate() {
        if (!this.interceptorsEnabled) {
            throw new UnsupportedOperationException("MBeanServerInterceptors are disabled.");
        }
        return this.mBeanServerDelegateObject;
    }
    
    public static MBeanServerDelegate newMBeanServerDelegate() {
        return new MBeanServerDelegateImpl();
    }
    
    public static MBeanServer newMBeanServer(final String s, final MBeanServer mBeanServer, final MBeanServerDelegate mBeanServerDelegate, final boolean b) {
        checkNewMBeanServerPermission();
        return new JmxMBeanServer(s, mBeanServer, mBeanServerDelegate, null, b, true);
    }
    
    private ObjectName cloneObjectName(final ObjectName objectName) {
        if (objectName != null) {
            return ObjectName.getInstance(objectName);
        }
        return objectName;
    }
    
    private Attribute cloneAttribute(final Attribute attribute) {
        if (attribute != null && !attribute.getClass().equals(Attribute.class)) {
            return new Attribute(attribute.getName(), attribute.getValue());
        }
        return attribute;
    }
    
    private AttributeList cloneAttributeList(final AttributeList list) {
        if (list == null) {
            return list;
        }
        final List<Attribute> list2 = list.asList();
        if (!list.getClass().equals(AttributeList.class)) {
            final AttributeList list3 = new AttributeList(list2.size());
            final Iterator iterator = list2.iterator();
            while (iterator.hasNext()) {
                list3.add(this.cloneAttribute((Attribute)iterator.next()));
            }
            return list3;
        }
        for (int i = 0; i < list2.size(); ++i) {
            final Attribute attribute = list2.get(i);
            if (!attribute.getClass().equals(Attribute.class)) {
                list.set(i, this.cloneAttribute(attribute));
            }
        }
        return list;
    }
    
    private static void checkMBeanPermission(final String s, final String s2, final ObjectName objectName, final String s3) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanPermission(s, s2, objectName, s3));
        }
    }
    
    private static void checkNewMBeanServerPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanServerPermission("newMBeanServer"));
        }
    }
}
