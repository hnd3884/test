package com.sun.jmx.remote.security;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.ListenerNotFoundException;
import java.util.Set;
import javax.management.QueryExp;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.loading.ClassLoaderRepository;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.OperationsException;
import java.io.ObjectInputStream;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import javax.management.InstanceNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.remote.MBeanServerForwarder;

public abstract class MBeanServerAccessController implements MBeanServerForwarder
{
    private MBeanServer mbs;
    
    @Override
    public MBeanServer getMBeanServer() {
        return this.mbs;
    }
    
    @Override
    public void setMBeanServer(final MBeanServer mbs) {
        if (mbs == null) {
            throw new IllegalArgumentException("Null MBeanServer");
        }
        if (this.mbs != null) {
            throw new IllegalArgumentException("MBeanServer object already initialized");
        }
        this.mbs = mbs;
    }
    
    protected abstract void checkRead();
    
    protected abstract void checkWrite();
    
    protected void checkCreate(final String s) {
        this.checkWrite();
    }
    
    protected void checkUnregister(final ObjectName objectName) {
        this.checkWrite();
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        this.checkRead();
        this.getMBeanServer().addNotificationListener(objectName, notificationListener, notificationFilter, o);
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        this.checkRead();
        this.getMBeanServer().addNotificationListener(objectName, objectName2, notificationFilter, o);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        this.checkCreate(s);
        if (System.getSecurityManager() == null) {
            final Object instantiate = this.getMBeanServer().instantiate(s);
            this.checkClassLoader(instantiate);
            return this.getMBeanServer().registerMBean(instantiate, objectName);
        }
        return this.getMBeanServer().createMBean(s, objectName);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        this.checkCreate(s);
        if (System.getSecurityManager() == null) {
            final Object instantiate = this.getMBeanServer().instantiate(s, array, array2);
            this.checkClassLoader(instantiate);
            return this.getMBeanServer().registerMBean(instantiate, objectName);
        }
        return this.getMBeanServer().createMBean(s, objectName, array, array2);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        this.checkCreate(s);
        if (System.getSecurityManager() == null) {
            final Object instantiate = this.getMBeanServer().instantiate(s, objectName2);
            this.checkClassLoader(instantiate);
            return this.getMBeanServer().registerMBean(instantiate, objectName);
        }
        return this.getMBeanServer().createMBean(s, objectName, objectName2);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        this.checkCreate(s);
        if (System.getSecurityManager() == null) {
            final Object instantiate = this.getMBeanServer().instantiate(s, objectName2, array, array2);
            this.checkClassLoader(instantiate);
            return this.getMBeanServer().registerMBean(instantiate, objectName);
        }
        return this.getMBeanServer().createMBean(s, objectName, objectName2, array, array2);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final ObjectName objectName, final byte[] array) throws InstanceNotFoundException, OperationsException {
        this.checkRead();
        return this.getMBeanServer().deserialize(objectName, array);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final String s, final byte[] array) throws OperationsException, ReflectionException {
        this.checkRead();
        return this.getMBeanServer().deserialize(s, array);
    }
    
    @Deprecated
    @Override
    public ObjectInputStream deserialize(final String s, final ObjectName objectName, final byte[] array) throws InstanceNotFoundException, OperationsException, ReflectionException {
        this.checkRead();
        return this.getMBeanServer().deserialize(s, objectName, array);
    }
    
    @Override
    public Object getAttribute(final ObjectName objectName, final String s) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        this.checkRead();
        return this.getMBeanServer().getAttribute(objectName, s);
    }
    
    @Override
    public AttributeList getAttributes(final ObjectName objectName, final String[] array) throws InstanceNotFoundException, ReflectionException {
        this.checkRead();
        return this.getMBeanServer().getAttributes(objectName, array);
    }
    
    @Override
    public ClassLoader getClassLoader(final ObjectName objectName) throws InstanceNotFoundException {
        this.checkRead();
        return this.getMBeanServer().getClassLoader(objectName);
    }
    
    @Override
    public ClassLoader getClassLoaderFor(final ObjectName objectName) throws InstanceNotFoundException {
        this.checkRead();
        return this.getMBeanServer().getClassLoaderFor(objectName);
    }
    
    @Override
    public ClassLoaderRepository getClassLoaderRepository() {
        this.checkRead();
        return this.getMBeanServer().getClassLoaderRepository();
    }
    
    @Override
    public String getDefaultDomain() {
        this.checkRead();
        return this.getMBeanServer().getDefaultDomain();
    }
    
    @Override
    public String[] getDomains() {
        this.checkRead();
        return this.getMBeanServer().getDomains();
    }
    
    @Override
    public Integer getMBeanCount() {
        this.checkRead();
        return this.getMBeanServer().getMBeanCount();
    }
    
    @Override
    public MBeanInfo getMBeanInfo(final ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        this.checkRead();
        return this.getMBeanServer().getMBeanInfo(objectName);
    }
    
    @Override
    public ObjectInstance getObjectInstance(final ObjectName objectName) throws InstanceNotFoundException {
        this.checkRead();
        return this.getMBeanServer().getObjectInstance(objectName);
    }
    
    @Override
    public Object instantiate(final String s) throws ReflectionException, MBeanException {
        this.checkCreate(s);
        return this.getMBeanServer().instantiate(s);
    }
    
    @Override
    public Object instantiate(final String s, final Object[] array, final String[] array2) throws ReflectionException, MBeanException {
        this.checkCreate(s);
        return this.getMBeanServer().instantiate(s, array, array2);
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
        this.checkCreate(s);
        return this.getMBeanServer().instantiate(s, objectName);
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, MBeanException, InstanceNotFoundException {
        this.checkCreate(s);
        return this.getMBeanServer().instantiate(s, objectName, array, array2);
    }
    
    @Override
    public Object invoke(final ObjectName objectName, final String s, final Object[] array, final String[] array2) throws InstanceNotFoundException, MBeanException, ReflectionException {
        this.checkWrite();
        this.checkMLetMethods(objectName, s);
        return this.getMBeanServer().invoke(objectName, s, array, array2);
    }
    
    @Override
    public boolean isInstanceOf(final ObjectName objectName, final String s) throws InstanceNotFoundException {
        this.checkRead();
        return this.getMBeanServer().isInstanceOf(objectName, s);
    }
    
    @Override
    public boolean isRegistered(final ObjectName objectName) {
        this.checkRead();
        return this.getMBeanServer().isRegistered(objectName);
    }
    
    @Override
    public Set<ObjectInstance> queryMBeans(final ObjectName objectName, final QueryExp queryExp) {
        this.checkRead();
        return this.getMBeanServer().queryMBeans(objectName, queryExp);
    }
    
    @Override
    public Set<ObjectName> queryNames(final ObjectName objectName, final QueryExp queryExp) {
        this.checkRead();
        return this.getMBeanServer().queryNames(objectName, queryExp);
    }
    
    @Override
    public ObjectInstance registerMBean(final Object o, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        this.checkWrite();
        return this.getMBeanServer().registerMBean(o, objectName);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws InstanceNotFoundException, ListenerNotFoundException {
        this.checkRead();
        this.getMBeanServer().removeNotificationListener(objectName, notificationListener);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        this.checkRead();
        this.getMBeanServer().removeNotificationListener(objectName, notificationListener, notificationFilter, o);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2) throws InstanceNotFoundException, ListenerNotFoundException {
        this.checkRead();
        this.getMBeanServer().removeNotificationListener(objectName, objectName2);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        this.checkRead();
        this.getMBeanServer().removeNotificationListener(objectName, objectName2, notificationFilter, o);
    }
    
    @Override
    public void setAttribute(final ObjectName objectName, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        this.checkWrite();
        this.getMBeanServer().setAttribute(objectName, attribute);
    }
    
    @Override
    public AttributeList setAttributes(final ObjectName objectName, final AttributeList list) throws InstanceNotFoundException, ReflectionException {
        this.checkWrite();
        return this.getMBeanServer().setAttributes(objectName, list);
    }
    
    @Override
    public void unregisterMBean(final ObjectName objectName) throws InstanceNotFoundException, MBeanRegistrationException {
        this.checkUnregister(objectName);
        this.getMBeanServer().unregisterMBean(objectName);
    }
    
    private void checkClassLoader(final Object o) {
        if (o instanceof ClassLoader) {
            throw new SecurityException("Access denied! Creating an MBean that is a ClassLoader is forbidden unless a security manager is installed.");
        }
    }
    
    private void checkMLetMethods(final ObjectName objectName, final String s) throws InstanceNotFoundException {
        if (System.getSecurityManager() != null) {
            return;
        }
        if (!s.equals("addURL") && !s.equals("getMBeansFromURL")) {
            return;
        }
        if (!this.getMBeanServer().isInstanceOf(objectName, "javax.management.loading.MLet")) {
            return;
        }
        if (s.equals("addURL")) {
            throw new SecurityException("Access denied! MLet method addURL cannot be invoked unless a security manager is installed.");
        }
        if (!"true".equalsIgnoreCase(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.remote.x.mlet.allow.getMBeansFromURL")))) {
            throw new SecurityException("Access denied! MLet method getMBeansFromURL cannot be invoked unless a security manager is installed or the system property -Djmx.remote.x.mlet.allow.getMBeansFromURL=true is specified.");
        }
    }
}
