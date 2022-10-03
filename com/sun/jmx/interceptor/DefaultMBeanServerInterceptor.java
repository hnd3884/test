package com.sun.jmx.interceptor;

import com.sun.jmx.mbeanserver.ModifiableClassLoaderRepository;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import javax.management.MBeanTrustPermission;
import java.security.Permission;
import javax.management.MBeanPermission;
import javax.management.loading.ClassLoaderRepository;
import javax.management.OperationsException;
import java.io.ObjectInputStream;
import javax.management.QueryEval;
import com.sun.jmx.mbeanserver.NamedObject;
import javax.management.Notification;
import javax.management.MBeanServerNotification;
import javax.management.IntrospectionException;
import javax.management.JMRuntimeException;
import javax.management.MBeanInfo;
import javax.management.NotificationEmitter;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import com.sun.jmx.mbeanserver.DynamicMBean2;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeErrorException;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import com.sun.jmx.mbeanserver.Util;
import java.util.ArrayList;
import java.util.Iterator;
import javax.management.QueryExp;
import javax.management.MBeanRegistration;
import javax.management.DynamicMBean;
import com.sun.jmx.mbeanserver.Introspector;
import java.util.logging.Level;
import com.sun.jmx.defaults.JmxProperties;
import javax.management.RuntimeOperationsException;
import com.sun.jmx.remote.util.EnvHelp;
import javax.management.InstanceNotFoundException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import java.util.HashSet;
import javax.management.ObjectName;
import java.util.Set;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import com.sun.jmx.mbeanserver.Repository;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServer;
import com.sun.jmx.mbeanserver.MBeanInstantiator;

public class DefaultMBeanServerInterceptor implements MBeanServerInterceptor
{
    private final transient MBeanInstantiator instantiator;
    private transient MBeanServer server;
    private final transient MBeanServerDelegate delegate;
    private final transient Repository repository;
    private final transient WeakHashMap<ListenerWrapper, WeakReference<ListenerWrapper>> listenerWrappers;
    private final String domain;
    private final Set<ObjectName> beingUnregistered;
    
    public DefaultMBeanServerInterceptor(final MBeanServer server, final MBeanServerDelegate delegate, final MBeanInstantiator instantiator, final Repository repository) {
        this.server = null;
        this.listenerWrappers = new WeakHashMap<ListenerWrapper, WeakReference<ListenerWrapper>>();
        this.beingUnregistered = new HashSet<ObjectName>();
        if (server == null) {
            throw new IllegalArgumentException("outer MBeanServer cannot be null");
        }
        if (delegate == null) {
            throw new IllegalArgumentException("MBeanServerDelegate cannot be null");
        }
        if (instantiator == null) {
            throw new IllegalArgumentException("MBeanInstantiator cannot be null");
        }
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.server = server;
        this.delegate = delegate;
        this.instantiator = instantiator;
        this.repository = repository;
        this.domain = repository.getDefaultDomain();
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        return this.createMBean(s, objectName, null, null);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return this.createMBean(s, objectName, objectName2, null, null);
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException {
        try {
            return this.createMBean(s, objectName, null, true, array, array2);
        }
        catch (final InstanceNotFoundException ex) {
            throw EnvHelp.initCause(new IllegalArgumentException("Unexpected exception: " + ex), ex);
        }
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        return this.createMBean(s, objectName, objectName2, false, array, array2);
    }
    
    private ObjectInstance createMBean(final String s, ObjectName nonDefaultDomain, ObjectName nonDefaultDomain2, final boolean b, final Object[] array, final String[] array2) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException {
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("The class name cannot be null"), "Exception occurred during MBean creation");
        }
        if (nonDefaultDomain != null) {
            if (nonDefaultDomain.isPattern()) {
                throw new RuntimeOperationsException(new IllegalArgumentException("Invalid name->" + nonDefaultDomain.toString()), "Exception occurred during MBean creation");
            }
            nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        }
        checkMBeanPermission(s, null, null, "instantiate");
        checkMBeanPermission(s, null, nonDefaultDomain, "registerMBean");
        Class<?> clazz;
        if (b) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + s + ", ObjectName = " + nonDefaultDomain);
            }
            clazz = this.instantiator.findClassWithDefaultLoaderRepository(s);
        }
        else if (nonDefaultDomain2 == null) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + s + ", ObjectName = " + nonDefaultDomain + ", Loader name = null");
            }
            clazz = this.instantiator.findClass(s, this.server.getClass().getClassLoader());
        }
        else {
            nonDefaultDomain2 = this.nonDefaultDomain(nonDefaultDomain2);
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "createMBean", "ClassName = " + s + ", ObjectName = " + nonDefaultDomain + ", Loader name = " + nonDefaultDomain2);
            }
            clazz = this.instantiator.findClass(s, nonDefaultDomain2);
        }
        checkMBeanTrustPermission(clazz);
        Introspector.testCreation(clazz);
        Introspector.checkCompliance(clazz);
        final Object instantiate = this.instantiator.instantiate(clazz, array, array2, this.server.getClass().getClassLoader());
        return this.registerObject(getNewMBeanClassName(instantiate), instantiate, nonDefaultDomain);
    }
    
    @Override
    public ObjectInstance registerMBean(final Object o, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        final Class<?> class1 = o.getClass();
        Introspector.checkCompliance(class1);
        final String newMBeanClassName = getNewMBeanClassName(o);
        checkMBeanPermission(newMBeanClassName, null, objectName, "registerMBean");
        checkMBeanTrustPermission(class1);
        return this.registerObject(newMBeanClassName, o, objectName);
    }
    
    private static String getNewMBeanClassName(final Object o) throws NotCompliantMBeanException {
        if (!(o instanceof DynamicMBean)) {
            return o.getClass().getName();
        }
        final DynamicMBean dynamicMBean = (DynamicMBean)o;
        String className;
        try {
            className = dynamicMBean.getMBeanInfo().getClassName();
        }
        catch (final Exception ex) {
            final NotCompliantMBeanException ex2 = new NotCompliantMBeanException("Bad getMBeanInfo()");
            ex2.initCause(ex);
            throw ex2;
        }
        if (className == null) {
            throw new NotCompliantMBeanException("MBeanInfo has null class name");
        }
        return className;
    }
    
    @Override
    public void unregisterMBean(ObjectName nonDefaultDomain) throws InstanceNotFoundException, MBeanRegistrationException {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to unregister the MBean");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        synchronized (this.beingUnregistered) {
            while (this.beingUnregistered.contains(nonDefaultDomain)) {
                try {
                    this.beingUnregistered.wait();
                    continue;
                }
                catch (final InterruptedException ex) {
                    throw new MBeanRegistrationException(ex, ex.toString());
                }
                break;
            }
            this.beingUnregistered.add(nonDefaultDomain);
        }
        try {
            this.exclusiveUnregisterMBean(nonDefaultDomain);
        }
        finally {
            synchronized (this.beingUnregistered) {
                this.beingUnregistered.remove(nonDefaultDomain);
                this.beingUnregistered.notifyAll();
            }
        }
    }
    
    private void exclusiveUnregisterMBean(final ObjectName objectName) throws InstanceNotFoundException, MBeanRegistrationException {
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "unregisterMBean");
        if (mBean instanceof MBeanRegistration) {
            preDeregisterInvoke((MBeanRegistration)mBean);
        }
        final ResourceContext unregisterFromRepository = this.unregisterFromRepository(getResource(mBean), mBean, objectName);
        try {
            if (mBean instanceof MBeanRegistration) {
                postDeregisterInvoke(objectName, (MBeanRegistration)mBean);
            }
        }
        finally {
            unregisterFromRepository.done();
        }
    }
    
    @Override
    public ObjectInstance getObjectInstance(ObjectName nonDefaultDomain) throws InstanceNotFoundException {
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        checkMBeanPermission(mBean, null, nonDefaultDomain, "getObjectInstance");
        return new ObjectInstance(nonDefaultDomain, getClassName(mBean));
    }
    
    @Override
    public Set<ObjectInstance> queryMBeans(final ObjectName objectName, final QueryExp queryExp) {
        if (System.getSecurityManager() != null) {
            checkMBeanPermission((String)null, null, null, "queryMBeans");
            final Set<ObjectInstance> queryMBeansImpl = this.queryMBeansImpl(objectName, null);
            final HashSet set = new HashSet(queryMBeansImpl.size());
            for (final ObjectInstance objectInstance : queryMBeansImpl) {
                try {
                    checkMBeanPermission(objectInstance.getClassName(), null, objectInstance.getObjectName(), "queryMBeans");
                    set.add((Object)objectInstance);
                }
                catch (final SecurityException ex) {}
            }
            return this.filterListOfObjectInstances((Set<ObjectInstance>)set, queryExp);
        }
        return this.queryMBeansImpl(objectName, queryExp);
    }
    
    private Set<ObjectInstance> queryMBeansImpl(final ObjectName objectName, final QueryExp queryExp) {
        return this.objectInstancesFromFilteredNamedObjects(this.repository.query(objectName, queryExp), queryExp);
    }
    
    @Override
    public Set<ObjectName> queryNames(final ObjectName objectName, final QueryExp queryExp) {
        Set<ObjectName> queryNamesImpl;
        if (System.getSecurityManager() != null) {
            checkMBeanPermission((String)null, null, null, "queryNames");
            final Set<ObjectInstance> queryMBeansImpl = this.queryMBeansImpl(objectName, null);
            final HashSet set = new HashSet(queryMBeansImpl.size());
            for (final ObjectInstance objectInstance : queryMBeansImpl) {
                try {
                    checkMBeanPermission(objectInstance.getClassName(), null, objectInstance.getObjectName(), "queryNames");
                    set.add((Object)objectInstance);
                }
                catch (final SecurityException ex) {}
            }
            final Set<ObjectInstance> filterListOfObjectInstances = this.filterListOfObjectInstances((Set<ObjectInstance>)set, queryExp);
            queryNamesImpl = new HashSet<ObjectName>(filterListOfObjectInstances.size());
            final Iterator iterator2 = filterListOfObjectInstances.iterator();
            while (iterator2.hasNext()) {
                queryNamesImpl.add(((ObjectInstance)iterator2.next()).getObjectName());
            }
        }
        else {
            queryNamesImpl = this.queryNamesImpl(objectName, queryExp);
        }
        return queryNamesImpl;
    }
    
    private Set<ObjectName> queryNamesImpl(final ObjectName objectName, final QueryExp queryExp) {
        return this.objectNamesFromFilteredNamedObjects(this.repository.query(objectName, queryExp), queryExp);
    }
    
    @Override
    public boolean isRegistered(ObjectName nonDefaultDomain) {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Object name cannot be null");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        return this.repository.contains(nonDefaultDomain);
    }
    
    @Override
    public String[] getDomains() {
        if (System.getSecurityManager() != null) {
            checkMBeanPermission((String)null, null, null, "getDomains");
            final String[] domains = this.repository.getDomains();
            final ArrayList list = new ArrayList(domains.length);
            for (int i = 0; i < domains.length; ++i) {
                try {
                    checkMBeanPermission((String)null, null, Util.newObjectName(domains[i] + ":x=x"), "getDomains");
                    list.add((Object)domains[i]);
                }
                catch (final SecurityException ex) {}
            }
            return (String[])list.toArray((Object[])new String[list.size()]);
        }
        return this.repository.getDomains();
    }
    
    @Override
    public Integer getMBeanCount() {
        return this.repository.getCount();
    }
    
    @Override
    public Object getAttribute(ObjectName nonDefaultDomain, final String s) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
        }
        if (s == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttribute", "Attribute = " + s + ", ObjectName = " + nonDefaultDomain);
        }
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        checkMBeanPermission(mBean, s, nonDefaultDomain, "getAttribute");
        try {
            return mBean.getAttribute(s);
        }
        catch (final AttributeNotFoundException ex) {
            throw ex;
        }
        catch (final Throwable t) {
            rethrowMaybeMBeanException(t);
            throw new AssertionError();
        }
    }
    
    @Override
    public AttributeList getAttributes(ObjectName nonDefaultDomain, final String[] array) throws InstanceNotFoundException, ReflectionException {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
        }
        if (array == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attributes cannot be null"), "Exception occurred trying to invoke the getter on the MBean");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getAttributes", "ObjectName = " + nonDefaultDomain);
        }
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        String[] array2;
        if (System.getSecurityManager() == null) {
            array2 = array;
        }
        else {
            final String className = getClassName(mBean);
            checkMBeanPermission(className, null, nonDefaultDomain, "getAttribute");
            final ArrayList list = new ArrayList(array.length);
            for (final String s : array) {
                try {
                    checkMBeanPermission(className, s, nonDefaultDomain, "getAttribute");
                    list.add(s);
                }
                catch (final SecurityException ex) {}
            }
            array2 = (String[])list.toArray(new String[list.size()]);
        }
        try {
            return mBean.getAttributes(array2);
        }
        catch (final Throwable t) {
            rethrow(t);
            throw new AssertionError();
        }
    }
    
    @Override
    public void setAttribute(ObjectName nonDefaultDomain, final Attribute attribute) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "setAttribute", "ObjectName = " + nonDefaultDomain + ", Attribute = " + attribute.getName());
        }
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        checkMBeanPermission(mBean, attribute.getName(), nonDefaultDomain, "setAttribute");
        try {
            mBean.setAttribute(attribute);
        }
        catch (final AttributeNotFoundException ex) {
            throw ex;
        }
        catch (final InvalidAttributeValueException ex2) {
            throw ex2;
        }
        catch (final Throwable t) {
            rethrowMaybeMBeanException(t);
            throw new AssertionError();
        }
    }
    
    @Override
    public AttributeList setAttributes(ObjectName nonDefaultDomain, final AttributeList list) throws InstanceNotFoundException, ReflectionException {
        if (nonDefaultDomain == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("ObjectName name cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
        }
        if (list == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList  cannot be null"), "Exception occurred trying to invoke the setter on the MBean");
        }
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        AttributeList attributes;
        if (System.getSecurityManager() == null) {
            attributes = list;
        }
        else {
            final String className = getClassName(mBean);
            checkMBeanPermission(className, null, nonDefaultDomain, "setAttribute");
            attributes = new AttributeList(list.size());
            for (final Attribute attribute : list.asList()) {
                try {
                    checkMBeanPermission(className, attribute.getName(), nonDefaultDomain, "setAttribute");
                    attributes.add(attribute);
                }
                catch (final SecurityException ex) {}
            }
        }
        try {
            return mBean.setAttributes(attributes);
        }
        catch (final Throwable t) {
            rethrow(t);
            throw new AssertionError();
        }
    }
    
    @Override
    public Object invoke(ObjectName nonDefaultDomain, final String s, final Object[] array, final String[] array2) throws InstanceNotFoundException, MBeanException, ReflectionException {
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        final DynamicMBean mBean = this.getMBean(nonDefaultDomain);
        checkMBeanPermission(mBean, s, nonDefaultDomain, "invoke");
        try {
            return mBean.invoke(s, array, array2);
        }
        catch (final Throwable t) {
            rethrowMaybeMBeanException(t);
            throw new AssertionError();
        }
    }
    
    private static void rethrow(final Throwable t) throws ReflectionException {
        try {
            throw t;
        }
        catch (final ReflectionException ex) {
            throw ex;
        }
        catch (final RuntimeOperationsException ex2) {
            throw ex2;
        }
        catch (final RuntimeErrorException ex3) {
            throw ex3;
        }
        catch (final RuntimeException ex4) {
            throw new RuntimeMBeanException(ex4, ex4.toString());
        }
        catch (final Error error) {
            throw new RuntimeErrorException(error, error.toString());
        }
        catch (final Throwable t2) {
            throw new RuntimeException("Unexpected exception", t2);
        }
    }
    
    private static void rethrowMaybeMBeanException(final Throwable t) throws ReflectionException, MBeanException {
        if (t instanceof MBeanException) {
            throw (MBeanException)t;
        }
        rethrow(t);
    }
    
    private ObjectInstance registerObject(final String s, final Object o, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (o == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Cannot add null object"), "Exception occurred trying to register the MBean");
        }
        return this.registerDynamicMBean(s, Introspector.makeDynamicMBean(o), objectName);
    }
    
    private ObjectInstance registerDynamicMBean(final String s, final DynamicMBean dynamicMBean, ObjectName nonDefaultDomain) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        nonDefaultDomain = this.nonDefaultDomain(nonDefaultDomain);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "registerMBean", "ObjectName = " + nonDefaultDomain);
        }
        ObjectName objectName = preRegister(dynamicMBean, this.server, nonDefaultDomain);
        boolean b = false;
        boolean b2 = false;
        ResourceContext registerWithRepository = null;
        try {
            if (dynamicMBean instanceof DynamicMBean2) {
                try {
                    ((DynamicMBean2)dynamicMBean).preRegister2(this.server, objectName);
                    b2 = true;
                }
                catch (final Exception ex) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException)ex;
                    }
                    if (ex instanceof InstanceAlreadyExistsException) {
                        throw (InstanceAlreadyExistsException)ex;
                    }
                    throw new RuntimeException(ex);
                }
            }
            if (objectName != nonDefaultDomain && objectName != null) {
                objectName = ObjectName.getInstance(this.nonDefaultDomain(objectName));
            }
            checkMBeanPermission(s, null, objectName, "registerMBean");
            if (objectName == null) {
                throw new RuntimeOperationsException(new IllegalArgumentException("No object name specified"), "Exception occurred trying to register the MBean");
            }
            registerWithRepository = this.registerWithRepository(getResource(dynamicMBean), dynamicMBean, objectName);
            b2 = false;
            b = true;
        }
        finally {
            try {
                postRegister(objectName, dynamicMBean, b, b2);
            }
            finally {
                if (b && registerWithRepository != null) {
                    registerWithRepository.done();
                }
            }
        }
        return new ObjectInstance(objectName, s);
    }
    
    private static void throwMBeanRegistrationException(final Throwable t, final String s) throws MBeanRegistrationException {
        if (t instanceof RuntimeException) {
            throw new RuntimeMBeanException((RuntimeException)t, "RuntimeException thrown " + s);
        }
        if (t instanceof Error) {
            throw new RuntimeErrorException((Error)t, "Error thrown " + s);
        }
        if (t instanceof MBeanRegistrationException) {
            throw (MBeanRegistrationException)t;
        }
        if (t instanceof Exception) {
            throw new MBeanRegistrationException((Exception)t, "Exception thrown " + s);
        }
        throw new RuntimeException(t);
    }
    
    private static ObjectName preRegister(final DynamicMBean dynamicMBean, final MBeanServer mBeanServer, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException {
        ObjectName preRegister = null;
        try {
            if (dynamicMBean instanceof MBeanRegistration) {
                preRegister = ((MBeanRegistration)dynamicMBean).preRegister(mBeanServer, objectName);
            }
        }
        catch (final Throwable t) {
            throwMBeanRegistrationException(t, "in preRegister method");
        }
        if (preRegister != null) {
            return preRegister;
        }
        return objectName;
    }
    
    private static void postRegister(final ObjectName objectName, final DynamicMBean dynamicMBean, final boolean b, final boolean b2) {
        if (b2 && dynamicMBean instanceof DynamicMBean2) {
            ((DynamicMBean2)dynamicMBean).registerFailed();
        }
        try {
            if (dynamicMBean instanceof MBeanRegistration) {
                ((MBeanRegistration)dynamicMBean).postRegister(b);
            }
        }
        catch (final RuntimeException ex) {
            JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + objectName + "]: Exception thrown by postRegister: rethrowing <" + ex + ">, but keeping the MBean registered");
            throw new RuntimeMBeanException(ex, "RuntimeException thrown in postRegister method: rethrowing <" + ex + ">, but keeping the MBean registered");
        }
        catch (final Error error) {
            JmxProperties.MBEANSERVER_LOGGER.fine("While registering MBean [" + objectName + "]: Error thrown by postRegister: rethrowing <" + error + ">, but keeping the MBean registered");
            throw new RuntimeErrorException(error, "Error thrown in postRegister method: rethrowing <" + error + ">, but keeping the MBean registered");
        }
    }
    
    private static void preDeregisterInvoke(final MBeanRegistration mBeanRegistration) throws MBeanRegistrationException {
        try {
            mBeanRegistration.preDeregister();
        }
        catch (final Throwable t) {
            throwMBeanRegistrationException(t, "in preDeregister method");
        }
    }
    
    private static void postDeregisterInvoke(final ObjectName objectName, final MBeanRegistration mBeanRegistration) {
        try {
            mBeanRegistration.postDeregister();
        }
        catch (final RuntimeException ex) {
            JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + objectName + "]: Exception thrown by postDeregister: rethrowing <" + ex + ">, although the MBean is succesfully unregistered");
            throw new RuntimeMBeanException(ex, "RuntimeException thrown in postDeregister method: rethrowing <" + ex + ">, although the MBean is sucessfully unregistered");
        }
        catch (final Error error) {
            JmxProperties.MBEANSERVER_LOGGER.fine("While unregistering MBean [" + objectName + "]: Error thrown by postDeregister: rethrowing <" + error + ">, although the MBean is succesfully unregistered");
            throw new RuntimeErrorException(error, "Error thrown in postDeregister method: rethrowing <" + error + ">, although the MBean is sucessfully unregistered");
        }
    }
    
    private DynamicMBean getMBean(final ObjectName objectName) throws InstanceNotFoundException {
        if (objectName == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Object name cannot be null"), "Exception occurred trying to get an MBean");
        }
        final DynamicMBean retrieve = this.repository.retrieve(objectName);
        if (retrieve == null) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "getMBean", objectName + " : Found no object");
            }
            throw new InstanceNotFoundException(objectName.toString());
        }
        return retrieve;
    }
    
    private static Object getResource(final DynamicMBean dynamicMBean) {
        if (dynamicMBean instanceof DynamicMBean2) {
            return ((DynamicMBean2)dynamicMBean).getResource();
        }
        return dynamicMBean;
    }
    
    private ObjectName nonDefaultDomain(final ObjectName objectName) {
        if (objectName == null || objectName.getDomain().length() > 0) {
            return objectName;
        }
        return Util.newObjectName(this.domain + objectName);
    }
    
    @Override
    public String getDefaultDomain() {
        return this.domain;
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + objectName);
        }
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "addNotificationListener");
        final NotificationBroadcaster notificationBroadcaster = getNotificationBroadcaster(objectName, mBean, NotificationBroadcaster.class);
        if (notificationListener == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Null listener"), "Null listener");
        }
        notificationBroadcaster.addNotificationListener(this.getListenerWrapper(notificationListener, objectName, mBean, true), notificationFilter, o);
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException {
        final Object resource = getResource(this.getMBean(objectName2));
        if (!(resource instanceof NotificationListener)) {
            throw new RuntimeOperationsException(new IllegalArgumentException(objectName2.getCanonicalName()), "The MBean " + objectName2.getCanonicalName() + "does not implement the NotificationListener interface");
        }
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addNotificationListener", "ObjectName = " + objectName + ", Listener = " + objectName2);
        }
        this.server.addNotificationListener(objectName, (NotificationListener)resource, notificationFilter, o);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener) throws InstanceNotFoundException, ListenerNotFoundException {
        this.removeNotificationListener(objectName, notificationListener, null, null, true);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        this.removeNotificationListener(objectName, notificationListener, notificationFilter, o, false);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2) throws InstanceNotFoundException, ListenerNotFoundException {
        final NotificationListener listener = this.getListener(objectName2);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + objectName + ", Listener = " + objectName2);
        }
        this.server.removeNotificationListener(objectName, listener);
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final NotificationFilter notificationFilter, final Object o) throws InstanceNotFoundException, ListenerNotFoundException {
        final NotificationListener listener = this.getListener(objectName2);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + objectName + ", Listener = " + objectName2);
        }
        this.server.removeNotificationListener(objectName, listener, notificationFilter, o);
    }
    
    private NotificationListener getListener(final ObjectName objectName) throws ListenerNotFoundException {
        DynamicMBean mBean;
        try {
            mBean = this.getMBean(objectName);
        }
        catch (final InstanceNotFoundException ex) {
            throw EnvHelp.initCause(new ListenerNotFoundException(ex.getMessage()), ex);
        }
        final Object resource = getResource(mBean);
        if (!(resource instanceof NotificationListener)) {
            throw new RuntimeOperationsException(new IllegalArgumentException(objectName.getCanonicalName()), "MBean " + objectName.getCanonicalName() + " does not implement " + NotificationListener.class.getName());
        }
        return (NotificationListener)resource;
    }
    
    private void removeNotificationListener(final ObjectName objectName, final NotificationListener notificationListener, final NotificationFilter notificationFilter, final Object o, final boolean b) throws InstanceNotFoundException, ListenerNotFoundException {
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "removeNotificationListener", "ObjectName = " + objectName);
        }
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "removeNotificationListener");
        final NotificationEmitter notificationBroadcaster = getNotificationBroadcaster(objectName, mBean, (Class<NotificationEmitter>)(b ? NotificationBroadcaster.class : NotificationEmitter.class));
        final NotificationListener listenerWrapper = this.getListenerWrapper(notificationListener, objectName, mBean, false);
        if (listenerWrapper == null) {
            throw new ListenerNotFoundException("Unknown listener");
        }
        if (b) {
            notificationBroadcaster.removeNotificationListener(listenerWrapper);
        }
        else {
            notificationBroadcaster.removeNotificationListener(listenerWrapper, notificationFilter, o);
        }
    }
    
    private static <T extends NotificationBroadcaster> T getNotificationBroadcaster(final ObjectName objectName, Object resource, final Class<T> clazz) {
        if (clazz.isInstance(resource)) {
            return clazz.cast(resource);
        }
        if (resource instanceof DynamicMBean2) {
            resource = ((DynamicMBean2)resource).getResource();
        }
        if (clazz.isInstance(resource)) {
            return clazz.cast(resource);
        }
        throw new RuntimeOperationsException(new IllegalArgumentException(objectName.getCanonicalName()), "MBean " + objectName.getCanonicalName() + " does not implement " + clazz.getName());
    }
    
    @Override
    public MBeanInfo getMBeanInfo(final ObjectName objectName) throws InstanceNotFoundException, IntrospectionException, ReflectionException {
        final DynamicMBean mBean = this.getMBean(objectName);
        MBeanInfo mBeanInfo;
        try {
            mBeanInfo = mBean.getMBeanInfo();
        }
        catch (final RuntimeMBeanException ex) {
            throw ex;
        }
        catch (final RuntimeErrorException ex2) {
            throw ex2;
        }
        catch (final RuntimeException ex3) {
            throw new RuntimeMBeanException(ex3, "getMBeanInfo threw RuntimeException");
        }
        catch (final Error error) {
            throw new RuntimeErrorException(error, "getMBeanInfo threw Error");
        }
        if (mBeanInfo == null) {
            throw new JMRuntimeException("MBean " + objectName + "has no MBeanInfo");
        }
        checkMBeanPermission(mBeanInfo.getClassName(), null, objectName, "getMBeanInfo");
        return mBeanInfo;
    }
    
    @Override
    public boolean isInstanceOf(final ObjectName objectName, final String s) throws InstanceNotFoundException {
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "isInstanceOf");
        try {
            final Object resource = getResource(mBean);
            final String s2 = (resource instanceof DynamicMBean) ? getClassName((DynamicMBean)resource) : ((DynamicMBean)resource).getClass().getName();
            if (s2.equals(s)) {
                return true;
            }
            final ClassLoader classLoader = ((DynamicMBean)resource).getClass().getClassLoader();
            final Class<?> forName = Class.forName(s, false, classLoader);
            return forName.isInstance(resource) || forName.isAssignableFrom(Class.forName(s2, false, classLoader));
        }
        catch (final Exception ex) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "isInstanceOf", "Exception calling isInstanceOf", ex);
            }
            return false;
        }
    }
    
    @Override
    public ClassLoader getClassLoaderFor(final ObjectName objectName) throws InstanceNotFoundException {
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "getClassLoaderFor");
        return getResource(mBean).getClass().getClassLoader();
    }
    
    @Override
    public ClassLoader getClassLoader(final ObjectName objectName) throws InstanceNotFoundException {
        if (objectName == null) {
            checkMBeanPermission((String)null, null, null, "getClassLoader");
            return this.server.getClass().getClassLoader();
        }
        final DynamicMBean mBean = this.getMBean(objectName);
        checkMBeanPermission(mBean, null, objectName, "getClassLoader");
        final Object resource = getResource(mBean);
        if (!(resource instanceof ClassLoader)) {
            throw new InstanceNotFoundException(objectName.toString() + " is not a classloader");
        }
        return (ClassLoader)resource;
    }
    
    private void sendNotification(final String s, final ObjectName objectName) {
        final MBeanServerNotification mBeanServerNotification = new MBeanServerNotification(s, MBeanServerDelegate.DELEGATE_NAME, 0L, objectName);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "sendNotification", s + " " + objectName);
        }
        this.delegate.sendNotification(mBeanServerNotification);
    }
    
    private Set<ObjectName> objectNamesFromFilteredNamedObjects(final Set<NamedObject> set, final QueryExp queryExp) {
        final HashSet set2 = new HashSet();
        if (queryExp == null) {
            final Iterator<NamedObject> iterator = set.iterator();
            while (iterator.hasNext()) {
                set2.add(iterator.next().getName());
            }
        }
        else {
            final MBeanServer mBeanServer = QueryEval.getMBeanServer();
            queryExp.setMBeanServer(this.server);
            try {
                for (final NamedObject namedObject : set) {
                    boolean apply;
                    try {
                        apply = queryExp.apply(namedObject.getName());
                    }
                    catch (final Exception ex) {
                        apply = false;
                    }
                    if (apply) {
                        set2.add(namedObject.getName());
                    }
                }
            }
            finally {
                queryExp.setMBeanServer(mBeanServer);
            }
        }
        return set2;
    }
    
    private Set<ObjectInstance> objectInstancesFromFilteredNamedObjects(final Set<NamedObject> set, final QueryExp queryExp) {
        final HashSet set2 = new HashSet();
        if (queryExp == null) {
            for (final NamedObject namedObject : set) {
                set2.add(new ObjectInstance(namedObject.getName(), safeGetClassName(namedObject.getObject())));
            }
        }
        else {
            final MBeanServer mBeanServer = QueryEval.getMBeanServer();
            queryExp.setMBeanServer(this.server);
            try {
                for (final NamedObject namedObject2 : set) {
                    final DynamicMBean object = namedObject2.getObject();
                    boolean apply;
                    try {
                        apply = queryExp.apply(namedObject2.getName());
                    }
                    catch (final Exception ex) {
                        apply = false;
                    }
                    if (apply) {
                        set2.add(new ObjectInstance(namedObject2.getName(), safeGetClassName(object)));
                    }
                }
            }
            finally {
                queryExp.setMBeanServer(mBeanServer);
            }
        }
        return set2;
    }
    
    private static String safeGetClassName(final DynamicMBean dynamicMBean) {
        try {
            return getClassName(dynamicMBean);
        }
        catch (final Exception ex) {
            if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINEST)) {
                JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINEST, DefaultMBeanServerInterceptor.class.getName(), "safeGetClassName", "Exception getting MBean class name", ex);
            }
            return null;
        }
    }
    
    private Set<ObjectInstance> filterListOfObjectInstances(final Set<ObjectInstance> set, final QueryExp queryExp) {
        if (queryExp == null) {
            return set;
        }
        final HashSet set2 = new HashSet();
        for (final ObjectInstance objectInstance : set) {
            boolean apply = false;
            final MBeanServer mBeanServer = QueryEval.getMBeanServer();
            queryExp.setMBeanServer(this.server);
            try {
                apply = queryExp.apply(objectInstance.getObjectName());
            }
            catch (final Exception ex) {
                apply = false;
            }
            finally {
                queryExp.setMBeanServer(mBeanServer);
            }
            if (apply) {
                set2.add(objectInstance);
            }
        }
        return set2;
    }
    
    private NotificationListener getListenerWrapper(final NotificationListener notificationListener, final ObjectName objectName, final DynamicMBean dynamicMBean, final boolean b) {
        final ListenerWrapper listenerWrapper = new ListenerWrapper(notificationListener, objectName, getResource(dynamicMBean));
        synchronized (this.listenerWrappers) {
            final WeakReference weakReference = this.listenerWrappers.get(listenerWrapper);
            if (weakReference != null) {
                final NotificationListener notificationListener2 = (NotificationListener)weakReference.get();
                if (notificationListener2 != null) {
                    return notificationListener2;
                }
            }
            if (b) {
                this.listenerWrappers.put(listenerWrapper, new WeakReference<ListenerWrapper>(listenerWrapper));
                return listenerWrapper;
            }
            return null;
        }
    }
    
    @Override
    public Object instantiate(final String s) throws ReflectionException, MBeanException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName) throws ReflectionException, MBeanException, InstanceNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object instantiate(final String s, final Object[] array, final String[] array2) throws ReflectionException, MBeanException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Object instantiate(final String s, final ObjectName objectName, final Object[] array, final String[] array2) throws ReflectionException, MBeanException, InstanceNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ObjectInputStream deserialize(final ObjectName objectName, final byte[] array) throws InstanceNotFoundException, OperationsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ObjectInputStream deserialize(final String s, final byte[] array) throws OperationsException, ReflectionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ObjectInputStream deserialize(final String s, final ObjectName objectName, final byte[] array) throws InstanceNotFoundException, OperationsException, ReflectionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public ClassLoaderRepository getClassLoaderRepository() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private static String getClassName(final DynamicMBean dynamicMBean) {
        if (dynamicMBean instanceof DynamicMBean2) {
            return ((DynamicMBean2)dynamicMBean).getClassName();
        }
        return dynamicMBean.getMBeanInfo().getClassName();
    }
    
    private static void checkMBeanPermission(final DynamicMBean dynamicMBean, final String s, final ObjectName objectName, final String s2) {
        if (System.getSecurityManager() != null) {
            checkMBeanPermission(safeGetClassName(dynamicMBean), s, objectName, s2);
        }
    }
    
    private static void checkMBeanPermission(final String s, final String s2, final ObjectName objectName, final String s3) {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanPermission(s, s2, objectName, s3));
        }
    }
    
    private static void checkMBeanTrustPermission(final Class<?> clazz) throws SecurityException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanTrustPermission("register"), new AccessControlContext(new ProtectionDomain[] { AccessController.doPrivileged((PrivilegedAction<ProtectionDomain>)new PrivilegedAction<ProtectionDomain>() {
                    @Override
                    public ProtectionDomain run() {
                        return clazz.getProtectionDomain();
                    }
                }) }));
        }
    }
    
    private ResourceContext registerWithRepository(final Object o, final DynamicMBean dynamicMBean, final ObjectName objectName) throws InstanceAlreadyExistsException, MBeanRegistrationException {
        final ResourceContext resourceContext = this.makeResourceContextFor(o, objectName);
        this.repository.addMBean(dynamicMBean, objectName, resourceContext);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "addObject", "Send create notification of object " + objectName.getCanonicalName());
        }
        this.sendNotification("JMX.mbean.registered", objectName);
        return resourceContext;
    }
    
    private ResourceContext unregisterFromRepository(final Object o, final DynamicMBean dynamicMBean, final ObjectName objectName) throws InstanceNotFoundException {
        final ResourceContext resourceContext = this.makeResourceContextFor(o, objectName);
        this.repository.remove(objectName, resourceContext);
        if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER)) {
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, DefaultMBeanServerInterceptor.class.getName(), "unregisterMBean", "Send delete notification of object " + objectName.getCanonicalName());
        }
        this.sendNotification("JMX.mbean.unregistered", objectName);
        return resourceContext;
    }
    
    private void addClassLoader(final ClassLoader classLoader, final ObjectName objectName) {
        final ModifiableClassLoaderRepository instantiatorCLR = this.getInstantiatorCLR();
        if (instantiatorCLR == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException("Dynamic addition of class loaders is not supported"), "Exception occurred trying to register the MBean as a class loader");
        }
        instantiatorCLR.addClassLoader(objectName, classLoader);
    }
    
    private void removeClassLoader(final ClassLoader classLoader, final ObjectName objectName) {
        if (classLoader != this.server.getClass().getClassLoader()) {
            final ModifiableClassLoaderRepository instantiatorCLR = this.getInstantiatorCLR();
            if (instantiatorCLR != null) {
                instantiatorCLR.removeClassLoader(objectName);
            }
        }
    }
    
    private ResourceContext createClassLoaderContext(final ClassLoader classLoader, final ObjectName objectName) {
        return new ResourceContext() {
            @Override
            public void registering() {
                DefaultMBeanServerInterceptor.this.addClassLoader(classLoader, objectName);
            }
            
            @Override
            public void unregistered() {
                DefaultMBeanServerInterceptor.this.removeClassLoader(classLoader, objectName);
            }
            
            @Override
            public void done() {
            }
        };
    }
    
    private ResourceContext makeResourceContextFor(final Object o, final ObjectName objectName) {
        if (o instanceof ClassLoader) {
            return this.createClassLoaderContext((ClassLoader)o, objectName);
        }
        return ResourceContext.NONE;
    }
    
    private ModifiableClassLoaderRepository getInstantiatorCLR() {
        return AccessController.doPrivileged((PrivilegedAction<ModifiableClassLoaderRepository>)new PrivilegedAction<ModifiableClassLoaderRepository>() {
            @Override
            public ModifiableClassLoaderRepository run() {
                return (DefaultMBeanServerInterceptor.this.instantiator != null) ? DefaultMBeanServerInterceptor.this.instantiator.getClassLoaderRepository() : null;
            }
        });
    }
    
    private static class ListenerWrapper implements NotificationListener
    {
        private NotificationListener listener;
        private ObjectName name;
        private Object mbean;
        
        ListenerWrapper(final NotificationListener listener, final ObjectName name, final Object mbean) {
            this.listener = listener;
            this.name = name;
            this.mbean = mbean;
        }
        
        @Override
        public void handleNotification(final Notification notification, final Object o) {
            if (notification != null && notification.getSource() == this.mbean) {
                notification.setSource(this.name);
            }
            this.listener.handleNotification(notification, o);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof ListenerWrapper)) {
                return false;
            }
            final ListenerWrapper listenerWrapper = (ListenerWrapper)o;
            return listenerWrapper.listener == this.listener && listenerWrapper.mbean == this.mbean && listenerWrapper.name.equals(this.name);
        }
        
        @Override
        public int hashCode() {
            return System.identityHashCode(this.listener) ^ System.identityHashCode(this.mbean);
        }
    }
    
    private interface ResourceContext extends Repository.RegistrationContext
    {
        public static final ResourceContext NONE = new ResourceContext() {
            @Override
            public void done() {
            }
            
            @Override
            public void registering() {
            }
            
            @Override
            public void unregistered() {
            }
        };
        
        void done();
    }
}
