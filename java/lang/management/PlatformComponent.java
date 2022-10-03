package java.lang.management;

import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.UnixOperatingSystemMXBean;
import java.util.HashMap;
import java.util.Collection;
import javax.management.QueryExp;
import sun.management.Util;
import javax.management.ObjectName;
import java.io.IOException;
import javax.management.MBeanServerConnection;
import java.util.HashSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import sun.management.ManagementFactoryHelper;
import java.util.List;
import java.util.Map;
import java.util.Set;

enum PlatformComponent
{
    CLASS_LOADING("java.lang.management.ClassLoadingMXBean", "java.lang", "ClassLoading", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<ClassLoadingMXBean>() {
        @Override
        public List<ClassLoadingMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getClassLoadingMXBean());
        }
    }, new PlatformComponent[0]), 
    COMPILATION("java.lang.management.CompilationMXBean", "java.lang", "Compilation", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<CompilationMXBean>() {
        @Override
        public List<CompilationMXBean> getMXBeans() {
            final CompilationMXBean compilationMXBean = ManagementFactoryHelper.getCompilationMXBean();
            if (compilationMXBean == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(compilationMXBean);
        }
    }, new PlatformComponent[0]), 
    MEMORY("java.lang.management.MemoryMXBean", "java.lang", "Memory", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<MemoryMXBean>() {
        @Override
        public List<MemoryMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getMemoryMXBean());
        }
    }, new PlatformComponent[0]), 
    GARBAGE_COLLECTOR("java.lang.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties("name"), false, (MXBeanFetcher<?>)new MXBeanFetcher<GarbageCollectorMXBean>() {
        @Override
        public List<GarbageCollectorMXBean> getMXBeans() {
            return ManagementFactoryHelper.getGarbageCollectorMXBeans();
        }
    }, new PlatformComponent[0]), 
    MEMORY_MANAGER("java.lang.management.MemoryManagerMXBean", "java.lang", "MemoryManager", keyProperties("name"), false, (MXBeanFetcher<?>)new MXBeanFetcher<MemoryManagerMXBean>() {
        @Override
        public List<MemoryManagerMXBean> getMXBeans() {
            return ManagementFactoryHelper.getMemoryManagerMXBeans();
        }
    }, new PlatformComponent[] { PlatformComponent.GARBAGE_COLLECTOR }), 
    MEMORY_POOL("java.lang.management.MemoryPoolMXBean", "java.lang", "MemoryPool", keyProperties("name"), false, (MXBeanFetcher<?>)new MXBeanFetcher<MemoryPoolMXBean>() {
        @Override
        public List<MemoryPoolMXBean> getMXBeans() {
            return ManagementFactoryHelper.getMemoryPoolMXBeans();
        }
    }, new PlatformComponent[0]), 
    OPERATING_SYSTEM("java.lang.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<OperatingSystemMXBean>() {
        @Override
        public List<OperatingSystemMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getOperatingSystemMXBean());
        }
    }, new PlatformComponent[0]), 
    RUNTIME("java.lang.management.RuntimeMXBean", "java.lang", "Runtime", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<RuntimeMXBean>() {
        @Override
        public List<RuntimeMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getRuntimeMXBean());
        }
    }, new PlatformComponent[0]), 
    THREADING("java.lang.management.ThreadMXBean", "java.lang", "Threading", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<ThreadMXBean>() {
        @Override
        public List<ThreadMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getThreadMXBean());
        }
    }, new PlatformComponent[0]), 
    LOGGING("java.lang.management.PlatformLoggingMXBean", "java.util.logging", "Logging", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<PlatformLoggingMXBean>() {
        @Override
        public List<PlatformLoggingMXBean> getMXBeans() {
            final PlatformLoggingMXBean platformLoggingMXBean = ManagementFactoryHelper.getPlatformLoggingMXBean();
            if (platformLoggingMXBean == null) {
                return Collections.emptyList();
            }
            return Collections.singletonList(platformLoggingMXBean);
        }
    }, new PlatformComponent[0]), 
    BUFFER_POOL("java.lang.management.BufferPoolMXBean", "java.nio", "BufferPool", keyProperties("name"), false, (MXBeanFetcher<?>)new MXBeanFetcher<BufferPoolMXBean>() {
        @Override
        public List<BufferPoolMXBean> getMXBeans() {
            return ManagementFactoryHelper.getBufferPoolMXBeans();
        }
    }, new PlatformComponent[0]), 
    SUN_GARBAGE_COLLECTOR("com.sun.management.GarbageCollectorMXBean", "java.lang", "GarbageCollector", keyProperties("name"), false, (MXBeanFetcher<?>)new MXBeanFetcher<com.sun.management.GarbageCollectorMXBean>() {
        @Override
        public List<com.sun.management.GarbageCollectorMXBean> getMXBeans() {
            return (List<com.sun.management.GarbageCollectorMXBean>)getGcMXBeanList((Class<GarbageCollectorMXBean>)com.sun.management.GarbageCollectorMXBean.class);
        }
    }, new PlatformComponent[0]), 
    SUN_OPERATING_SYSTEM("com.sun.management.OperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<com.sun.management.OperatingSystemMXBean>() {
        @Override
        public List<com.sun.management.OperatingSystemMXBean> getMXBeans() {
            return (List<com.sun.management.OperatingSystemMXBean>)getOSMXBeanList((Class<OperatingSystemMXBean>)com.sun.management.OperatingSystemMXBean.class);
        }
    }, new PlatformComponent[0]), 
    SUN_UNIX_OPERATING_SYSTEM("com.sun.management.UnixOperatingSystemMXBean", "java.lang", "OperatingSystem", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<UnixOperatingSystemMXBean>() {
        @Override
        public List<UnixOperatingSystemMXBean> getMXBeans() {
            return (List<UnixOperatingSystemMXBean>)getOSMXBeanList((Class<OperatingSystemMXBean>)UnixOperatingSystemMXBean.class);
        }
    }, new PlatformComponent[0]), 
    HOTSPOT_DIAGNOSTIC("com.sun.management.HotSpotDiagnosticMXBean", "com.sun.management", "HotSpotDiagnostic", defaultKeyProperties(), true, (MXBeanFetcher<?>)new MXBeanFetcher<HotSpotDiagnosticMXBean>() {
        @Override
        public List<HotSpotDiagnosticMXBean> getMXBeans() {
            return Collections.singletonList(ManagementFactoryHelper.getDiagnosticMXBean());
        }
    }, new PlatformComponent[0]);
    
    private final String mxbeanInterfaceName;
    private final String domain;
    private final String type;
    private final Set<String> keyProperties;
    private final MXBeanFetcher<?> fetcher;
    private final PlatformComponent[] subComponents;
    private final boolean singleton;
    private static Set<String> defaultKeyProps;
    private static Map<String, PlatformComponent> enumMap;
    private static final long serialVersionUID = 6992337162326171013L;
    
    private static <T extends GarbageCollectorMXBean> List<T> getGcMXBeanList(final Class<T> clazz) {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactoryHelper.getGarbageCollectorMXBeans();
        final ArrayList list = new ArrayList(garbageCollectorMXBeans.size());
        for (final GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            if (clazz.isInstance(garbageCollectorMXBean)) {
                list.add(clazz.cast(garbageCollectorMXBean));
            }
        }
        return (List<T>)list;
    }
    
    private static <T extends OperatingSystemMXBean> List<T> getOSMXBeanList(final Class<T> clazz) {
        final OperatingSystemMXBean operatingSystemMXBean = ManagementFactoryHelper.getOperatingSystemMXBean();
        if (clazz.isInstance(operatingSystemMXBean)) {
            return Collections.singletonList(clazz.cast(operatingSystemMXBean));
        }
        return Collections.emptyList();
    }
    
    private PlatformComponent(final String mxbeanInterfaceName, final String domain, final String type, final Set<String> keyProperties, final boolean singleton, final MXBeanFetcher<?> fetcher, final PlatformComponent[] subComponents) {
        this.mxbeanInterfaceName = mxbeanInterfaceName;
        this.domain = domain;
        this.type = type;
        this.keyProperties = keyProperties;
        this.singleton = singleton;
        this.fetcher = fetcher;
        this.subComponents = subComponents;
    }
    
    private static Set<String> defaultKeyProperties() {
        if (PlatformComponent.defaultKeyProps == null) {
            PlatformComponent.defaultKeyProps = Collections.singleton("type");
        }
        return PlatformComponent.defaultKeyProps;
    }
    
    private static Set<String> keyProperties(final String... array) {
        final HashSet set = new HashSet();
        set.add("type");
        for (int length = array.length, i = 0; i < length; ++i) {
            set.add(array[i]);
        }
        return set;
    }
    
    boolean isSingleton() {
        return this.singleton;
    }
    
    String getMXBeanInterfaceName() {
        return this.mxbeanInterfaceName;
    }
    
    Class<? extends PlatformManagedObject> getMXBeanInterface() {
        try {
            return (Class<? extends PlatformManagedObject>)Class.forName(this.mxbeanInterfaceName, false, PlatformManagedObject.class.getClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            throw new AssertionError((Object)ex);
        }
    }
    
     <T extends PlatformManagedObject> List<T> getMXBeans(final Class<T> clazz) {
        return (List<T>)this.fetcher.getMXBeans();
    }
    
     <T extends PlatformManagedObject> T getSingletonMXBean(final Class<T> clazz) {
        if (!this.singleton) {
            throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
        }
        final List<T> mxBeans = this.getMXBeans(clazz);
        assert mxBeans.size();
        return (T)(mxBeans.isEmpty() ? null : ((T)mxBeans.get(0)));
    }
    
     <T extends PlatformManagedObject> T getSingletonMXBean(final MBeanServerConnection mBeanServerConnection, final Class<T> clazz) throws IOException {
        if (!this.singleton) {
            throw new IllegalArgumentException(this.mxbeanInterfaceName + " can have zero or more than one instances");
        }
        assert this.keyProperties.size() == 1;
        return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, this.domain + ":type=" + this.type, clazz);
    }
    
     <T extends PlatformManagedObject> List<T> getMXBeans(final MBeanServerConnection mBeanServerConnection, final Class<T> clazz) throws IOException {
        final ArrayList list = new ArrayList();
        final Iterator<ObjectName> iterator = this.getObjectNames(mBeanServerConnection).iterator();
        while (iterator.hasNext()) {
            list.add(ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, iterator.next().getCanonicalName(), clazz));
        }
        return list;
    }
    
    private Set<ObjectName> getObjectNames(final MBeanServerConnection mBeanServerConnection) throws IOException {
        String s = this.domain + ":type=" + this.type;
        if (this.keyProperties.size() > 1) {
            s += ",*";
        }
        final Set<ObjectName> queryNames = mBeanServerConnection.queryNames(Util.newObjectName(s), null);
        final PlatformComponent[] subComponents = this.subComponents;
        for (int length = subComponents.length, i = 0; i < length; ++i) {
            queryNames.addAll(subComponents[i].getObjectNames(mBeanServerConnection));
        }
        return queryNames;
    }
    
    private static synchronized void ensureInitialized() {
        if (PlatformComponent.enumMap == null) {
            PlatformComponent.enumMap = new HashMap<String, PlatformComponent>();
            for (final PlatformComponent platformComponent : values()) {
                PlatformComponent.enumMap.put(platformComponent.getMXBeanInterfaceName(), platformComponent);
            }
        }
    }
    
    static boolean isPlatformMXBean(final String s) {
        ensureInitialized();
        return PlatformComponent.enumMap.containsKey(s);
    }
    
    static <T extends PlatformManagedObject> PlatformComponent getPlatformComponent(final Class<T> clazz) {
        ensureInitialized();
        final PlatformComponent platformComponent = PlatformComponent.enumMap.get(clazz.getName());
        if (platformComponent != null && platformComponent.getMXBeanInterface() == clazz) {
            return platformComponent;
        }
        return null;
    }
    
    interface MXBeanFetcher<T extends PlatformManagedObject>
    {
        List<T> getMXBeans();
    }
}
