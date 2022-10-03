package java.lang.management;

import java.security.PrivilegedActionException;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.StandardMBean;
import javax.management.StandardEmitterMBean;
import javax.management.NotificationEmitter;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.io.IOException;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import sun.misc.VM;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.management.MBeanServerConnection;
import java.util.Iterator;
import sun.management.ExtendedPlatformComponent;
import javax.management.ObjectName;
import javax.management.DynamicMBean;
import java.util.Map;
import javax.management.MBeanServerFactory;
import java.security.Permission;
import javax.management.MBeanServerPermission;
import java.util.List;
import sun.management.ManagementFactoryHelper;
import javax.management.MBeanServer;

public class ManagementFactory
{
    public static final String CLASS_LOADING_MXBEAN_NAME = "java.lang:type=ClassLoading";
    public static final String COMPILATION_MXBEAN_NAME = "java.lang:type=Compilation";
    public static final String MEMORY_MXBEAN_NAME = "java.lang:type=Memory";
    public static final String OPERATING_SYSTEM_MXBEAN_NAME = "java.lang:type=OperatingSystem";
    public static final String RUNTIME_MXBEAN_NAME = "java.lang:type=Runtime";
    public static final String THREAD_MXBEAN_NAME = "java.lang:type=Threading";
    public static final String GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE = "java.lang:type=GarbageCollector";
    public static final String MEMORY_MANAGER_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryManager";
    public static final String MEMORY_POOL_MXBEAN_DOMAIN_TYPE = "java.lang:type=MemoryPool";
    private static MBeanServer platformMBeanServer;
    private static final String NOTIF_EMITTER = "javax.management.NotificationEmitter";
    
    private ManagementFactory() {
    }
    
    public static ClassLoadingMXBean getClassLoadingMXBean() {
        return ManagementFactoryHelper.getClassLoadingMXBean();
    }
    
    public static MemoryMXBean getMemoryMXBean() {
        return ManagementFactoryHelper.getMemoryMXBean();
    }
    
    public static ThreadMXBean getThreadMXBean() {
        return ManagementFactoryHelper.getThreadMXBean();
    }
    
    public static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactoryHelper.getRuntimeMXBean();
    }
    
    public static CompilationMXBean getCompilationMXBean() {
        return ManagementFactoryHelper.getCompilationMXBean();
    }
    
    public static OperatingSystemMXBean getOperatingSystemMXBean() {
        return ManagementFactoryHelper.getOperatingSystemMXBean();
    }
    
    public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
        return ManagementFactoryHelper.getMemoryPoolMXBeans();
    }
    
    public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
        return ManagementFactoryHelper.getMemoryManagerMXBeans();
    }
    
    public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        return ManagementFactoryHelper.getGarbageCollectorMXBeans();
    }
    
    public static synchronized MBeanServer getPlatformMBeanServer() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(new MBeanServerPermission("createMBeanServer"));
        }
        if (ManagementFactory.platformMBeanServer == null) {
            ManagementFactory.platformMBeanServer = MBeanServerFactory.createMBeanServer();
            for (final PlatformComponent platformComponent : PlatformComponent.values()) {
                for (final PlatformManagedObject platformManagedObject : platformComponent.getMXBeans(platformComponent.getMXBeanInterface())) {
                    if (!ManagementFactory.platformMBeanServer.isRegistered(platformManagedObject.getObjectName())) {
                        addMXBean(ManagementFactory.platformMBeanServer, platformManagedObject);
                    }
                }
            }
            for (final Map.Entry entry : ManagementFactoryHelper.getPlatformDynamicMBeans().entrySet()) {
                addDynamicMBean(ManagementFactory.platformMBeanServer, (DynamicMBean)entry.getValue(), (ObjectName)entry.getKey());
            }
            for (final PlatformManagedObject platformManagedObject2 : ExtendedPlatformComponent.getMXBeans()) {
                if (!ManagementFactory.platformMBeanServer.isRegistered(platformManagedObject2.getObjectName())) {
                    addMXBean(ManagementFactory.platformMBeanServer, platformManagedObject2);
                }
            }
        }
        return ManagementFactory.platformMBeanServer;
    }
    
    public static <T> T newPlatformMXBeanProxy(final MBeanServerConnection mBeanServerConnection, final String s, final Class<T> clazz) throws IOException {
        if (!VM.isSystemDomainLoader(AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
            @Override
            public ClassLoader run() {
                return clazz.getClassLoader();
            }
        }))) {
            throw new IllegalArgumentException(s + " is not a platform MXBean");
        }
        try {
            final ObjectName objectName = new ObjectName(s);
            if (!mBeanServerConnection.isInstanceOf(objectName, clazz.getName())) {
                throw new IllegalArgumentException(s + " is not an instance of " + clazz);
            }
            return JMX.newMXBeanProxy(mBeanServerConnection, objectName, clazz, mBeanServerConnection.isInstanceOf(objectName, "javax.management.NotificationEmitter"));
        }
        catch (final InstanceNotFoundException | MalformedObjectNameException ex) {
            throw new IllegalArgumentException((Throwable)ex);
        }
    }
    
    public static <T extends PlatformManagedObject> T getPlatformMXBean(final Class<T> clazz) {
        final PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(clazz);
        if (platformComponent == null) {
            final PlatformManagedObject mxBean = ExtendedPlatformComponent.getMXBean(clazz);
            if (mxBean != null) {
                return (T)mxBean;
            }
            throw new IllegalArgumentException(clazz.getName() + " is not a platform management interface");
        }
        else {
            if (!platformComponent.isSingleton()) {
                throw new IllegalArgumentException(clazz.getName() + " can have zero or more than one instances");
            }
            return platformComponent.getSingletonMXBean(clazz);
        }
    }
    
    public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(final Class<T> clazz) {
        final PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(clazz);
        if (platformComponent != null) {
            return (List<T>)Collections.unmodifiableList((List<?>)platformComponent.getMXBeans((Class<? extends T>)clazz));
        }
        final PlatformManagedObject mxBean = ExtendedPlatformComponent.getMXBean(clazz);
        if (mxBean != null) {
            return Collections.singletonList(mxBean);
        }
        throw new IllegalArgumentException(clazz.getName() + " is not a platform management interface");
    }
    
    public static <T extends PlatformManagedObject> T getPlatformMXBean(final MBeanServerConnection mBeanServerConnection, final Class<T> clazz) throws IOException {
        final PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(clazz);
        if (platformComponent == null) {
            final PlatformManagedObject mxBean = ExtendedPlatformComponent.getMXBean(clazz);
            if (mxBean != null) {
                return newPlatformMXBeanProxy(mBeanServerConnection, mxBean.getObjectName().getCanonicalName(), clazz);
            }
            throw new IllegalArgumentException(clazz.getName() + " is not a platform management interface");
        }
        else {
            if (!platformComponent.isSingleton()) {
                throw new IllegalArgumentException(clazz.getName() + " can have zero or more than one instances");
            }
            return platformComponent.getSingletonMXBean(mBeanServerConnection, clazz);
        }
    }
    
    public static <T extends PlatformManagedObject> List<T> getPlatformMXBeans(final MBeanServerConnection mBeanServerConnection, final Class<T> clazz) throws IOException {
        final PlatformComponent platformComponent = PlatformComponent.getPlatformComponent(clazz);
        if (platformComponent != null) {
            return (List<T>)Collections.unmodifiableList((List<?>)platformComponent.getMXBeans(mBeanServerConnection, (Class<? extends T>)clazz));
        }
        final PlatformManagedObject mxBean = ExtendedPlatformComponent.getMXBean(clazz);
        if (mxBean != null) {
            return Collections.singletonList(newPlatformMXBeanProxy(mBeanServerConnection, mxBean.getObjectName().getCanonicalName(), clazz));
        }
        throw new IllegalArgumentException(clazz.getName() + " is not a platform management interface");
    }
    
    public static Set<Class<? extends PlatformManagedObject>> getPlatformManagementInterfaces() {
        final HashSet set = new HashSet();
        final PlatformComponent[] values = PlatformComponent.values();
        for (int length = values.length, i = 0; i < length; ++i) {
            set.add(values[i].getMXBeanInterface());
        }
        return (Set<Class<? extends PlatformManagedObject>>)Collections.unmodifiableSet((Set<?>)set);
    }
    
    private static void addMXBean(final MBeanServer mBeanServer, final PlatformManagedObject platformManagedObject) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
                    DynamicMBean dynamicMBean;
                    if (platformManagedObject instanceof DynamicMBean) {
                        dynamicMBean = DynamicMBean.class.cast(platformManagedObject);
                    }
                    else if (platformManagedObject instanceof NotificationEmitter) {
                        dynamicMBean = new StandardEmitterMBean((T)platformManagedObject, null, true, (NotificationEmitter)platformManagedObject);
                    }
                    else {
                        dynamicMBean = new StandardMBean((T)platformManagedObject, null, true);
                    }
                    mBeanServer.registerMBean(dynamicMBean, platformManagedObject.getObjectName());
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new RuntimeException(ex.getException());
        }
    }
    
    private static void addDynamicMBean(final MBeanServer mBeanServer, final DynamicMBean dynamicMBean, final ObjectName objectName) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
                    mBeanServer.registerMBean(dynamicMBean, objectName);
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw new RuntimeException(ex.getException());
        }
    }
}
