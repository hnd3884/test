package javax.management.remote.rmi;

import sun.reflect.misc.ReflectUtil;
import java.util.Arrays;
import javax.management.RuntimeOperationsException;
import com.sun.jmx.remote.util.OrderClassLoaders;
import java.rmi.UnmarshalException;
import javax.management.remote.JMXServerErrorException;
import java.security.PrivilegedExceptionAction;
import javax.management.remote.NotificationResult;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.IntrospectionException;
import javax.management.MBeanInfo;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import com.sun.jmx.mbeanserver.Util;
import javax.management.QueryExp;
import java.util.Set;
import java.rmi.MarshalledObject;
import javax.management.InstanceNotFoundException;
import java.security.PrivilegedActionException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.ReflectionException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.io.IOException;
import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.Permissions;
import com.sun.jmx.remote.util.EnvHelp;
import java.security.AccessController;
import javax.management.MBeanPermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import javax.management.loading.ClassLoaderRepository;
import com.sun.jmx.remote.security.JMXSubjectDomainCombiner;
import java.util.Collections;
import com.sun.jmx.remote.util.ClassLogger;
import java.util.Map;
import com.sun.jmx.remote.internal.ServerNotifForwarder;
import com.sun.jmx.remote.internal.ServerCommunicatorAdmin;
import com.sun.jmx.remote.util.ClassLoaderWithRepository;
import javax.management.MBeanServer;
import java.security.AccessControlContext;
import com.sun.jmx.remote.security.SubjectDelegator;
import javax.security.auth.Subject;
import java.rmi.server.Unreferenced;

public class RMIConnectionImpl implements RMIConnection, Unreferenced
{
    private static final Object[] NO_OBJECTS;
    private static final String[] NO_STRINGS;
    private final Subject subject;
    private final SubjectDelegator subjectDelegator;
    private final boolean removeCallerContext;
    private final AccessControlContext acc;
    private final RMIServerImpl rmiServer;
    private final MBeanServer mbeanServer;
    private final ClassLoader defaultClassLoader;
    private final ClassLoader defaultContextClassLoader;
    private final ClassLoaderWithRepository classLoaderWithRepository;
    private boolean terminated;
    private final String connectionId;
    private final ServerCommunicatorAdmin serverCommunicatorAdmin;
    private static final int ADD_NOTIFICATION_LISTENERS = 1;
    private static final int ADD_NOTIFICATION_LISTENER_OBJECTNAME = 2;
    private static final int CREATE_MBEAN = 3;
    private static final int CREATE_MBEAN_PARAMS = 4;
    private static final int CREATE_MBEAN_LOADER = 5;
    private static final int CREATE_MBEAN_LOADER_PARAMS = 6;
    private static final int GET_ATTRIBUTE = 7;
    private static final int GET_ATTRIBUTES = 8;
    private static final int GET_DEFAULT_DOMAIN = 9;
    private static final int GET_DOMAINS = 10;
    private static final int GET_MBEAN_COUNT = 11;
    private static final int GET_MBEAN_INFO = 12;
    private static final int GET_OBJECT_INSTANCE = 13;
    private static final int INVOKE = 14;
    private static final int IS_INSTANCE_OF = 15;
    private static final int IS_REGISTERED = 16;
    private static final int QUERY_MBEANS = 17;
    private static final int QUERY_NAMES = 18;
    private static final int REMOVE_NOTIFICATION_LISTENER = 19;
    private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME = 20;
    private static final int REMOVE_NOTIFICATION_LISTENER_OBJECTNAME_FILTER_HANDBACK = 21;
    private static final int SET_ATTRIBUTE = 22;
    private static final int SET_ATTRIBUTES = 23;
    private static final int UNREGISTER_MBEAN = 24;
    private ServerNotifForwarder serverNotifForwarder;
    private Map<String, ?> env;
    private static final ClassLogger logger;
    
    public RMIConnectionImpl(final RMIServerImpl rmiServer, final String connectionId, final ClassLoader defaultClassLoader, final Subject subject, Map<String, ?> emptyMap) {
        this.terminated = false;
        if (rmiServer == null || connectionId == null) {
            throw new NullPointerException("Illegal null argument");
        }
        if (emptyMap == null) {
            emptyMap = Collections.emptyMap();
        }
        this.rmiServer = rmiServer;
        this.connectionId = connectionId;
        this.defaultClassLoader = defaultClassLoader;
        this.subjectDelegator = new SubjectDelegator();
        if ((this.subject = subject) == null) {
            this.acc = null;
            this.removeCallerContext = false;
        }
        else {
            this.removeCallerContext = SubjectDelegator.checkRemoveCallerContext(subject);
            if (this.removeCallerContext) {
                this.acc = JMXSubjectDomainCombiner.getDomainCombinerContext(subject);
            }
            else {
                this.acc = JMXSubjectDomainCombiner.getContext(subject);
            }
        }
        this.mbeanServer = rmiServer.getMBeanServer();
        this.classLoaderWithRepository = AccessController.doPrivileged((PrivilegedAction<ClassLoaderWithRepository>)new PrivilegedAction<ClassLoaderWithRepository>() {
            final /* synthetic */ ClassLoaderRepository val$repository = AccessController.doPrivileged((PrivilegedAction<ClassLoaderRepository>)new PrivilegedAction<ClassLoaderRepository>(this) {
                @Override
                public ClassLoaderRepository run() {
                    return RMIConnectionImpl.this.mbeanServer.getClassLoaderRepository();
                }
            }, withPermissions(new Permission[] { new MBeanPermission("*", "getClassLoaderRepository") }));
            
            @Override
            public ClassLoaderWithRepository run() {
                return new ClassLoaderWithRepository(this.val$repository, defaultClassLoader);
            }
        }, withPermissions(new RuntimePermission("createClassLoader")));
        this.defaultContextClassLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return new CombinedClassLoader(Thread.currentThread().getContextClassLoader(), defaultClassLoader);
            }
        });
        this.serverCommunicatorAdmin = new RMIServerCommunicatorAdmin(EnvHelp.getServerConnectionTimeout(emptyMap));
        this.env = emptyMap;
    }
    
    private static AccessControlContext withPermissions(final Permission... array) {
        final Permissions permissions = new Permissions();
        for (int length = array.length, i = 0; i < length; ++i) {
            permissions.add(array[i]);
        }
        return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
    }
    
    private synchronized ServerNotifForwarder getServerNotifFwd() {
        if (this.serverNotifForwarder == null) {
            this.serverNotifForwarder = new ServerNotifForwarder(this.mbeanServer, this.env, this.rmiServer.getNotifBuffer(), this.connectionId);
        }
        return this.serverNotifForwarder;
    }
    
    @Override
    public String getConnectionId() throws IOException {
        return this.connectionId;
    }
    
    @Override
    public void close() throws IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        final String s = debugOn ? ("[" + this.toString() + "]") : null;
        synchronized (this) {
            if (this.terminated) {
                if (debugOn) {
                    RMIConnectionImpl.logger.debug("close", s + " already terminated.");
                }
                return;
            }
            if (debugOn) {
                RMIConnectionImpl.logger.debug("close", s + " closing.");
            }
            this.terminated = true;
            if (this.serverCommunicatorAdmin != null) {
                this.serverCommunicatorAdmin.terminate();
            }
            if (this.serverNotifForwarder != null) {
                this.serverNotifForwarder.terminate();
            }
        }
        this.rmiServer.clientClosed(this);
        if (debugOn) {
            RMIConnectionImpl.logger.debug("close", s + " closed.");
        }
    }
    
    @Override
    public void unreferenced() {
        RMIConnectionImpl.logger.debug("unreferenced", "called");
        try {
            this.close();
            RMIConnectionImpl.logger.debug("unreferenced", "done");
        }
        catch (final IOException ex) {
            RMIConnectionImpl.logger.fine("unreferenced", ex);
        }
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        try {
            final Object[] array = { s, objectName };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("createMBean(String,ObjectName)", "connectionId=" + this.connectionId + ", className=" + s + ", name=" + objectName);
            }
            return (ObjectInstance)this.doPrivilegedOperation(3, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)exception;
            }
            if (exception instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        try {
            final Object[] array = { s, objectName, objectName2 };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("createMBean(String,ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", className=" + s + ", name=" + objectName + ", loaderName=" + objectName2);
            }
            return (ObjectInstance)this.doPrivilegedOperation(5, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)exception;
            }
            if (exception instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)exception;
            }
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping parameters using classLoaderWithRepository.");
        }
        final Object[] nullIsEmpty = nullIsEmpty(this.unwrap(marshalledObject, this.classLoaderWithRepository, Object[].class, subject));
        try {
            final Object[] array2 = { s, objectName, nullIsEmpty, nullIsEmpty(array) };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("createMBean(String,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + s + ", name=" + objectName + ", signature=" + strings(array));
            }
            return (ObjectInstance)this.doPrivilegedOperation(4, array2, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)exception;
            }
            if (exception instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public ObjectInstance createMBean(final String s, final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException, IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", unwrapping params with MBean extended ClassLoader.");
        }
        final Object[] nullIsEmpty = nullIsEmpty(this.unwrap(marshalledObject, this.getClassLoader(objectName2), this.defaultClassLoader, Object[].class, subject));
        try {
            final Object[] array2 = { s, objectName, objectName2, nullIsEmpty, nullIsEmpty(array) };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("createMBean(String,ObjectName,ObjectName,Object[],String[])", "connectionId=" + this.connectionId + ", className=" + s + ", name=" + objectName + ", loaderName=" + objectName2 + ", signature=" + strings(array));
            }
            return (ObjectInstance)this.doPrivilegedOperation(6, array2, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof InstanceAlreadyExistsException) {
                throw (InstanceAlreadyExistsException)exception;
            }
            if (exception instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof NotCompliantMBeanException) {
                throw (NotCompliantMBeanException)exception;
            }
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void unregisterMBean(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, MBeanRegistrationException, IOException {
        try {
            final Object[] array = { objectName };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("unregisterMBean", "connectionId=" + this.connectionId + ", name=" + objectName);
            }
            this.doPrivilegedOperation(24, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof MBeanRegistrationException) {
                throw (MBeanRegistrationException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public ObjectInstance getObjectInstance(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, IOException {
        checkNonNull("ObjectName", objectName);
        try {
            final Object[] array = { objectName };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getObjectInstance", "connectionId=" + this.connectionId + ", name=" + objectName);
            }
            return (ObjectInstance)this.doPrivilegedOperation(13, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Set<ObjectInstance> queryMBeans(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("queryMBeans", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
        }
        final QueryExp queryExp = this.unwrap(marshalledObject, this.defaultContextClassLoader, QueryExp.class, subject);
        try {
            final Object[] array = { objectName, queryExp };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("queryMBeans", "connectionId=" + this.connectionId + ", name=" + objectName + ", query=" + marshalledObject);
            }
            return (Set<ObjectInstance>)Util.cast(this.doPrivilegedOperation(17, array, subject));
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Set<ObjectName> queryNames(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("queryNames", "connectionId=" + this.connectionId + " unwrapping query with defaultClassLoader.");
        }
        final QueryExp queryExp = this.unwrap(marshalledObject, this.defaultContextClassLoader, QueryExp.class, subject);
        try {
            final Object[] array = { objectName, queryExp };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("queryNames", "connectionId=" + this.connectionId + ", name=" + objectName + ", query=" + marshalledObject);
            }
            return (Set<ObjectName>)Util.cast(this.doPrivilegedOperation(18, array, subject));
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public boolean isRegistered(final ObjectName objectName, final Subject subject) throws IOException {
        try {
            return (boolean)this.doPrivilegedOperation(16, new Object[] { objectName }, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Integer getMBeanCount(final Subject subject) throws IOException {
        try {
            final Object[] array = new Object[0];
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getMBeanCount", "connectionId=" + this.connectionId);
            }
            return (Integer)this.doPrivilegedOperation(11, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Object getAttribute(final ObjectName objectName, final String s, final Subject subject) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException, IOException {
        try {
            final Object[] array = { objectName, s };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getAttribute", "connectionId=" + this.connectionId + ", name=" + objectName + ", attribute=" + s);
            }
            return this.doPrivilegedOperation(7, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof AttributeNotFoundException) {
                throw (AttributeNotFoundException)exception;
            }
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public AttributeList getAttributes(final ObjectName objectName, final String[] array, final Subject subject) throws InstanceNotFoundException, ReflectionException, IOException {
        try {
            final Object[] array2 = { objectName, array };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getAttributes", "connectionId=" + this.connectionId + ", name=" + objectName + ", attributes=" + strings(array));
            }
            return (AttributeList)this.doPrivilegedOperation(8, array2, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void setAttribute(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException, IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("setAttribute", "connectionId=" + this.connectionId + " unwrapping attribute with MBean extended ClassLoader.");
        }
        final Attribute attribute = this.unwrap(marshalledObject, this.getClassLoaderFor(objectName), this.defaultClassLoader, Attribute.class, subject);
        try {
            final Object[] array = { objectName, attribute };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("setAttribute", "connectionId=" + this.connectionId + ", name=" + objectName + ", attribute name=" + attribute.getName());
            }
            this.doPrivilegedOperation(22, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof AttributeNotFoundException) {
                throw (AttributeNotFoundException)exception;
            }
            if (exception instanceof InvalidAttributeValueException) {
                throw (InvalidAttributeValueException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public AttributeList setAttributes(final ObjectName objectName, final MarshalledObject marshalledObject, final Subject subject) throws InstanceNotFoundException, ReflectionException, IOException {
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("setAttributes", "connectionId=" + this.connectionId + " unwrapping attributes with MBean extended ClassLoader.");
        }
        final AttributeList list = this.unwrap(marshalledObject, this.getClassLoaderFor(objectName), this.defaultClassLoader, AttributeList.class, subject);
        try {
            final Object[] array = { objectName, list };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("setAttributes", "connectionId=" + this.connectionId + ", name=" + objectName + ", attribute names=" + RMIConnector.getAttributesNames(list));
            }
            return (AttributeList)this.doPrivilegedOperation(23, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Object invoke(final ObjectName objectName, final String s, final MarshalledObject marshalledObject, final String[] array, final Subject subject) throws InstanceNotFoundException, MBeanException, ReflectionException, IOException {
        checkNonNull("ObjectName", objectName);
        checkNonNull("Operation name", s);
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        if (debugOn) {
            RMIConnectionImpl.logger.debug("invoke", "connectionId=" + this.connectionId + " unwrapping params with MBean extended ClassLoader.");
        }
        final Object[] nullIsEmpty = nullIsEmpty(this.unwrap(marshalledObject, this.getClassLoaderFor(objectName), this.defaultClassLoader, Object[].class, subject));
        try {
            final Object[] array2 = { objectName, s, nullIsEmpty, nullIsEmpty(array) };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("invoke", "connectionId=" + this.connectionId + ", name=" + objectName + ", operationName=" + s + ", signature=" + strings(array));
            }
            return this.doPrivilegedOperation(14, array2, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof MBeanException) {
                throw (MBeanException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public String getDefaultDomain(final Subject subject) throws IOException {
        try {
            final Object[] array = new Object[0];
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getDefaultDomain", "connectionId=" + this.connectionId);
            }
            return (String)this.doPrivilegedOperation(9, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public String[] getDomains(final Subject subject) throws IOException {
        try {
            final Object[] array = new Object[0];
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getDomains", "connectionId=" + this.connectionId);
            }
            return (String[])this.doPrivilegedOperation(10, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public MBeanInfo getMBeanInfo(final ObjectName objectName, final Subject subject) throws InstanceNotFoundException, IntrospectionException, ReflectionException, IOException {
        checkNonNull("ObjectName", objectName);
        try {
            final Object[] array = { objectName };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("getMBeanInfo", "connectionId=" + this.connectionId + ", name=" + objectName);
            }
            return (MBeanInfo)this.doPrivilegedOperation(12, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IntrospectionException) {
                throw (IntrospectionException)exception;
            }
            if (exception instanceof ReflectionException) {
                throw (ReflectionException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public boolean isInstanceOf(final ObjectName objectName, final String s, final Subject subject) throws InstanceNotFoundException, IOException {
        checkNonNull("ObjectName", objectName);
        try {
            final Object[] array = { objectName, s };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("isInstanceOf", "connectionId=" + this.connectionId + ", name=" + objectName + ", className=" + s);
            }
            return (boolean)this.doPrivilegedOperation(15, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public Integer[] addNotificationListeners(final ObjectName[] array, final MarshalledObject[] array2, final Subject[] array3) throws InstanceNotFoundException, IOException {
        if (array == null || array2 == null) {
            throw new IllegalArgumentException("Got null arguments.");
        }
        final Subject[] array4 = (array3 != null) ? array3 : new Subject[array.length];
        if (array.length != array2.length || array2.length != array4.length) {
            throw new IllegalArgumentException("The value lengths of 3 parameters are not same.");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new IllegalArgumentException("Null Object name.");
            }
        }
        int j = 0;
        final NotificationFilter[] array5 = new NotificationFilter[array.length];
        final Integer[] array6 = new Integer[array.length];
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        try {
            while (j < array.length) {
                final ClassLoader classLoader = this.getClassLoaderFor(array[j]);
                if (debugOn) {
                    RMIConnectionImpl.logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
                }
                array5[j] = this.unwrap(array2[j], classLoader, this.defaultClassLoader, NotificationFilter.class, array4[j]);
                if (debugOn) {
                    RMIConnectionImpl.logger.debug("addNotificationListener(ObjectName,NotificationFilter)", "connectionId=" + this.connectionId + ", name=" + array[j] + ", filter=" + array5[j]);
                }
                array6[j] = (Integer)this.doPrivilegedOperation(1, new Object[] { array[j], array5[j] }, array4[j]);
                ++j;
            }
            return array6;
        }
        catch (final Exception exception) {
            for (int k = 0; k < j; ++k) {
                try {
                    this.getServerNotifFwd().removeNotificationListener(array[k], array6[k]);
                }
                catch (final Exception ex) {}
            }
            if (exception instanceof PrivilegedActionException) {
                exception = extractException(exception);
            }
            if (exception instanceof ClassCastException) {
                throw (ClassCastException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof RuntimeException) {
                throw (RuntimeException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void addNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws InstanceNotFoundException, IOException {
        checkNonNull("Target MBean name", objectName);
        checkNonNull("Listener MBean name", objectName2);
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        final ClassLoader classLoader = this.getClassLoaderFor(objectName);
        if (debugOn) {
            RMIConnectionImpl.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
        }
        final NotificationFilter notificationFilter = this.unwrap(marshalledObject, classLoader, this.defaultClassLoader, NotificationFilter.class, subject);
        if (debugOn) {
            RMIConnectionImpl.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
        }
        final Object unwrap = this.unwrap(marshalledObject2, classLoader, this.defaultClassLoader, Object.class, subject);
        try {
            final Object[] array = { objectName, objectName2, notificationFilter, unwrap };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("addNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + objectName + ", listenerName=" + objectName2 + ", filter=" + notificationFilter + ", handback=" + unwrap);
            }
            this.doPrivilegedOperation(2, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void removeNotificationListeners(final ObjectName objectName, final Integer[] array, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        if (objectName == null || array == null) {
            throw new IllegalArgumentException("Illegal null parameter");
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                throw new IllegalArgumentException("Null listener ID");
            }
        }
        try {
            final Object[] array2 = { objectName, array };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("removeNotificationListener(ObjectName,Integer[])", "connectionId=" + this.connectionId + ", name=" + objectName + ", listenerIDs=" + objects(array));
            }
            this.doPrivilegedOperation(19, array2, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        checkNonNull("Target MBean name", objectName);
        checkNonNull("Listener MBean name", objectName2);
        try {
            final Object[] array = { objectName, objectName2 };
            if (RMIConnectionImpl.logger.debugOn()) {
                RMIConnectionImpl.logger.debug("removeNotificationListener(ObjectName,ObjectName)", "connectionId=" + this.connectionId + ", name=" + objectName + ", listenerName=" + objectName2);
            }
            this.doPrivilegedOperation(20, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public void removeNotificationListener(final ObjectName objectName, final ObjectName objectName2, final MarshalledObject marshalledObject, final MarshalledObject marshalledObject2, final Subject subject) throws InstanceNotFoundException, ListenerNotFoundException, IOException {
        checkNonNull("Target MBean name", objectName);
        checkNonNull("Listener MBean name", objectName2);
        final boolean debugOn = RMIConnectionImpl.logger.debugOn();
        final ClassLoader classLoader = this.getClassLoaderFor(objectName);
        if (debugOn) {
            RMIConnectionImpl.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping filter with target extended ClassLoader.");
        }
        final NotificationFilter notificationFilter = this.unwrap(marshalledObject, classLoader, this.defaultClassLoader, NotificationFilter.class, subject);
        if (debugOn) {
            RMIConnectionImpl.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + " unwrapping handback with target extended ClassLoader.");
        }
        final Object unwrap = this.unwrap(marshalledObject2, classLoader, this.defaultClassLoader, Object.class, subject);
        try {
            final Object[] array = { objectName, objectName2, notificationFilter, unwrap };
            if (debugOn) {
                RMIConnectionImpl.logger.debug("removeNotificationListener(ObjectName,ObjectName,NotificationFilter,Object)", "connectionId=" + this.connectionId + ", name=" + objectName + ", listenerName=" + objectName2 + ", filter=" + notificationFilter + ", handback=" + unwrap);
            }
            this.doPrivilegedOperation(21, array, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof InstanceNotFoundException) {
                throw (InstanceNotFoundException)exception;
            }
            if (exception instanceof ListenerNotFoundException) {
                throw (ListenerNotFoundException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw newIOException("Got unexpected server exception: " + exception, exception);
        }
    }
    
    @Override
    public NotificationResult fetchNotifications(final long n, final int n2, final long n3) throws IOException {
        if (RMIConnectionImpl.logger.debugOn()) {
            RMIConnectionImpl.logger.debug("fetchNotifications", "connectionId=" + this.connectionId + ", timeout=" + n3);
        }
        if (n2 < 0 || n3 < 0L) {
            throw new IllegalArgumentException("Illegal negative argument");
        }
        final boolean reqIncoming = this.serverCommunicatorAdmin.reqIncoming();
        try {
            if (reqIncoming) {
                if (RMIConnectionImpl.logger.debugOn()) {
                    RMIConnectionImpl.logger.debug("fetchNotifications", "The notification server has been closed, returns null to force the client to stop fetching");
                }
                return null;
            }
            final PrivilegedAction<NotificationResult> privilegedAction = new PrivilegedAction<NotificationResult>() {
                @Override
                public NotificationResult run() {
                    return RMIConnectionImpl.this.getServerNotifFwd().fetchNotifs(n, n3, n2);
                }
            };
            if (this.acc == null) {
                return privilegedAction.run();
            }
            return AccessController.doPrivileged((PrivilegedAction<NotificationResult>)privilegedAction, this.acc);
        }
        finally {
            this.serverCommunicatorAdmin.rspOutgoing();
        }
    }
    
    @Override
    public String toString() {
        return super.toString() + ": connectionId=" + this.connectionId;
    }
    
    private ClassLoader getClassLoader(final ObjectName objectName) throws InstanceNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<ClassLoader>)new PrivilegedExceptionAction<ClassLoader>() {
                @Override
                public ClassLoader run() throws InstanceNotFoundException {
                    return RMIConnectionImpl.this.mbeanServer.getClassLoader(objectName);
                }
            }, withPermissions(new MBeanPermission("*", "getClassLoader")));
        }
        catch (final PrivilegedActionException ex) {
            throw (InstanceNotFoundException)extractException(ex);
        }
    }
    
    private ClassLoader getClassLoaderFor(final ObjectName objectName) throws InstanceNotFoundException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<ClassLoader>)new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws InstanceNotFoundException {
                    return RMIConnectionImpl.this.mbeanServer.getClassLoaderFor(objectName);
                }
            }, withPermissions(new MBeanPermission("*", "getClassLoaderFor")));
        }
        catch (final PrivilegedActionException ex) {
            throw (InstanceNotFoundException)extractException(ex);
        }
    }
    
    private Object doPrivilegedOperation(final int n, final Object[] array, final Subject subject) throws PrivilegedActionException, IOException {
        this.serverCommunicatorAdmin.reqIncoming();
        try {
            AccessControlContext accessControlContext;
            if (subject == null) {
                accessControlContext = this.acc;
            }
            else {
                if (this.subject == null) {
                    throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place");
                }
                accessControlContext = this.subjectDelegator.delegatedContext(this.acc, subject, this.removeCallerContext);
            }
            final PrivilegedOperation privilegedOperation = new PrivilegedOperation(n, array);
            if (accessControlContext == null) {
                try {
                    return privilegedOperation.run();
                }
                catch (final Exception ex) {
                    if (ex instanceof RuntimeException) {
                        throw (RuntimeException)ex;
                    }
                    throw new PrivilegedActionException(ex);
                }
            }
            return AccessController.doPrivileged((PrivilegedExceptionAction<Object>)privilegedOperation, accessControlContext);
        }
        catch (final Error error) {
            throw new JMXServerErrorException(error.toString(), error);
        }
        finally {
            this.serverCommunicatorAdmin.rspOutgoing();
        }
    }
    
    private Object doOperation(final int n, final Object[] array) throws Exception {
        switch (n) {
            case 3: {
                return this.mbeanServer.createMBean((String)array[0], (ObjectName)array[1]);
            }
            case 5: {
                return this.mbeanServer.createMBean((String)array[0], (ObjectName)array[1], (ObjectName)array[2]);
            }
            case 4: {
                return this.mbeanServer.createMBean((String)array[0], (ObjectName)array[1], (Object[])array[2], (String[])array[3]);
            }
            case 6: {
                return this.mbeanServer.createMBean((String)array[0], (ObjectName)array[1], (ObjectName)array[2], (Object[])array[3], (String[])array[4]);
            }
            case 7: {
                return this.mbeanServer.getAttribute((ObjectName)array[0], (String)array[1]);
            }
            case 8: {
                return this.mbeanServer.getAttributes((ObjectName)array[0], (String[])array[1]);
            }
            case 9: {
                return this.mbeanServer.getDefaultDomain();
            }
            case 10: {
                return this.mbeanServer.getDomains();
            }
            case 11: {
                return this.mbeanServer.getMBeanCount();
            }
            case 12: {
                return this.mbeanServer.getMBeanInfo((ObjectName)array[0]);
            }
            case 13: {
                return this.mbeanServer.getObjectInstance((ObjectName)array[0]);
            }
            case 14: {
                return this.mbeanServer.invoke((ObjectName)array[0], (String)array[1], (Object[])array[2], (String[])array[3]);
            }
            case 15: {
                return this.mbeanServer.isInstanceOf((ObjectName)array[0], (String)array[1]) ? Boolean.TRUE : Boolean.FALSE;
            }
            case 16: {
                return this.mbeanServer.isRegistered((ObjectName)array[0]) ? Boolean.TRUE : Boolean.FALSE;
            }
            case 17: {
                return this.mbeanServer.queryMBeans((ObjectName)array[0], (QueryExp)array[1]);
            }
            case 18: {
                return this.mbeanServer.queryNames((ObjectName)array[0], (QueryExp)array[1]);
            }
            case 22: {
                this.mbeanServer.setAttribute((ObjectName)array[0], (Attribute)array[1]);
                return null;
            }
            case 23: {
                return this.mbeanServer.setAttributes((ObjectName)array[0], (AttributeList)array[1]);
            }
            case 24: {
                this.mbeanServer.unregisterMBean((ObjectName)array[0]);
                return null;
            }
            case 1: {
                return this.getServerNotifFwd().addNotificationListener((ObjectName)array[0], (NotificationFilter)array[1]);
            }
            case 2: {
                this.mbeanServer.addNotificationListener((ObjectName)array[0], (ObjectName)array[1], (NotificationFilter)array[2], array[3]);
                return null;
            }
            case 19: {
                this.getServerNotifFwd().removeNotificationListener((ObjectName)array[0], (Integer[])array[1]);
                return null;
            }
            case 20: {
                this.mbeanServer.removeNotificationListener((ObjectName)array[0], (ObjectName)array[1]);
                return null;
            }
            case 21: {
                this.mbeanServer.removeNotificationListener((ObjectName)array[0], (ObjectName)array[1], (NotificationFilter)array[2], array[3]);
                return null;
            }
            default: {
                throw new IllegalArgumentException("Invalid operation");
            }
        }
    }
    
    private <T> T unwrap(final MarshalledObject<?> marshalledObject, final ClassLoader classLoader, final Class<T> clazz, final Subject subject) throws IOException {
        if (marshalledObject == null) {
            return null;
        }
        try {
            final ClassLoader classLoader2 = AccessController.doPrivileged((PrivilegedExceptionAction<ClassLoader>)new SetCcl(classLoader));
            try {
                AccessControlContext accessControlContext;
                if (subject == null) {
                    accessControlContext = this.acc;
                }
                else {
                    if (this.subject == null) {
                        throw new SecurityException("Subject delegation cannot be enabled unless an authenticated subject is put in place");
                    }
                    accessControlContext = this.subjectDelegator.delegatedContext(this.acc, subject, this.removeCallerContext);
                }
                if (accessControlContext != null) {
                    return AccessController.doPrivileged(() -> clazz2.cast(marshalledObject2.get()), accessControlContext);
                }
                return clazz.cast(marshalledObject.get());
            }
            finally {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new SetCcl(classLoader2));
            }
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof ClassNotFoundException) {
                throw new UnmarshalException(exception.toString(), exception);
            }
            RMIConnectionImpl.logger.warning("unwrap", "Failed to unmarshall object: " + exception);
            RMIConnectionImpl.logger.debug("unwrap", exception);
        }
        catch (final ClassNotFoundException ex2) {
            RMIConnectionImpl.logger.warning("unwrap", "Failed to unmarshall object: " + ex2);
            RMIConnectionImpl.logger.debug("unwrap", ex2);
            throw new UnmarshalException(ex2.toString(), ex2);
        }
        return null;
    }
    
    private <T> T unwrap(final MarshalledObject<?> marshalledObject, final ClassLoader classLoader, final ClassLoader classLoader2, final Class<T> clazz, final Subject subject) throws IOException {
        if (marshalledObject == null) {
            return null;
        }
        try {
            return this.unwrap(marshalledObject, AccessController.doPrivileged((PrivilegedExceptionAction<ClassLoader>)new PrivilegedExceptionAction<ClassLoader>() {
                @Override
                public ClassLoader run() throws Exception {
                    return new CombinedClassLoader(Thread.currentThread().getContextClassLoader(), (ClassLoader)new OrderClassLoaders(classLoader, classLoader2));
                }
            }), clazz, subject);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = extractException(ex);
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            if (exception instanceof ClassNotFoundException) {
                throw new UnmarshalException(exception.toString(), exception);
            }
            RMIConnectionImpl.logger.warning("unwrap", "Failed to unmarshall object: " + exception);
            RMIConnectionImpl.logger.debug("unwrap", exception);
            return null;
        }
    }
    
    private static IOException newIOException(final String s, final Throwable t) {
        return EnvHelp.initCause(new IOException(s), t);
    }
    
    private static Exception extractException(Exception exception) {
        while (exception instanceof PrivilegedActionException) {
            exception = ((PrivilegedActionException)exception).getException();
        }
        return exception;
    }
    
    private static Object[] nullIsEmpty(final Object[] array) {
        return (array == null) ? RMIConnectionImpl.NO_OBJECTS : array;
    }
    
    private static String[] nullIsEmpty(final String[] array) {
        return (array == null) ? RMIConnectionImpl.NO_STRINGS : array;
    }
    
    private static void checkNonNull(final String s, final Object o) {
        if (o == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(s + " must not be null"));
        }
    }
    
    private static String objects(final Object[] array) {
        if (array == null) {
            return "null";
        }
        return Arrays.asList(array).toString();
    }
    
    private static String strings(final String[] array) {
        return objects(array);
    }
    
    static {
        NO_OBJECTS = new Object[0];
        NO_STRINGS = new String[0];
        logger = new ClassLogger("javax.management.remote.rmi", "RMIConnectionImpl");
    }
    
    private class PrivilegedOperation implements PrivilegedExceptionAction<Object>
    {
        private int operation;
        private Object[] params;
        
        public PrivilegedOperation(final int operation, final Object[] params) {
            this.operation = operation;
            this.params = params;
        }
        
        @Override
        public Object run() throws Exception {
            return RMIConnectionImpl.this.doOperation(this.operation, this.params);
        }
    }
    
    private class RMIServerCommunicatorAdmin extends ServerCommunicatorAdmin
    {
        public RMIServerCommunicatorAdmin(final long n) {
            super(n);
        }
        
        @Override
        protected void doStop() {
            try {
                RMIConnectionImpl.this.close();
            }
            catch (final IOException ex) {
                RMIConnectionImpl.logger.warning("RMIServerCommunicatorAdmin-doStop", "Failed to close: " + ex);
                RMIConnectionImpl.logger.debug("RMIServerCommunicatorAdmin-doStop", ex);
            }
        }
    }
    
    private static class SetCcl implements PrivilegedExceptionAction<ClassLoader>
    {
        private final ClassLoader classLoader;
        
        SetCcl(final ClassLoader classLoader) {
            this.classLoader = classLoader;
        }
        
        @Override
        public ClassLoader run() {
            final Thread currentThread = Thread.currentThread();
            final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
            currentThread.setContextClassLoader(this.classLoader);
            return contextClassLoader;
        }
    }
    
    private static final class CombinedClassLoader extends ClassLoader
    {
        final ClassLoaderWrapper defaultCL;
        
        private CombinedClassLoader(final ClassLoader classLoader, final ClassLoader classLoader2) {
            super(classLoader);
            this.defaultCL = new ClassLoaderWrapper(classLoader2);
        }
        
        @Override
        protected Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
            ReflectUtil.checkPackageAccess(s);
            try {
                super.loadClass(s, b);
            }
            catch (final Exception ex) {
                for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
                    if (cause instanceof SecurityException) {
                        throw (cause == ex) ? cause : new SecurityException(cause.getMessage(), ex);
                    }
                }
                return this.defaultCL.loadClass(s, b);
            }
            return this.defaultCL.loadClass(s, b);
        }
        
        private static final class ClassLoaderWrapper extends ClassLoader
        {
            ClassLoaderWrapper(final ClassLoader classLoader) {
                super(classLoader);
            }
            
            @Override
            protected Class<?> loadClass(final String s, final boolean b) throws ClassNotFoundException {
                return super.loadClass(s, b);
            }
        }
    }
}
