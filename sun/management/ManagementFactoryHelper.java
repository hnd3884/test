package sun.management;

import java.util.logging.LoggingMXBean;
import java.security.PrivilegedAction;
import sun.misc.VM;
import javax.management.RuntimeOperationsException;
import javax.management.InstanceNotFoundException;
import javax.management.DynamicMBean;
import java.util.HashMap;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import javax.management.NotCompliantMBeanException;
import javax.management.MBeanRegistrationException;
import javax.management.InstanceAlreadyExistsException;
import java.security.PrivilegedExceptionAction;
import javax.management.MBeanServer;
import com.sun.management.DiagnosticCommandMBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import javax.management.ObjectName;
import sun.misc.JavaNioAccess;
import sun.nio.ch.FileChannelImpl;
import sun.misc.SharedSecrets;
import sun.util.logging.LoggingSupport;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.util.ArrayList;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.BufferPoolMXBean;
import java.util.List;

public class ManagementFactoryHelper
{
    private static VMManagement jvm;
    private static ClassLoadingImpl classMBean;
    private static MemoryImpl memoryMBean;
    private static ThreadImpl threadMBean;
    private static RuntimeImpl runtimeMBean;
    private static CompilationImpl compileMBean;
    private static OperatingSystemImpl osMBean;
    private static List<BufferPoolMXBean> bufferPools;
    private static final String BUFFER_POOL_MXBEAN_NAME = "java.nio:type=BufferPool";
    private static HotSpotDiagnostic hsDiagMBean;
    private static HotspotRuntime hsRuntimeMBean;
    private static HotspotClassLoading hsClassMBean;
    private static HotspotThread hsThreadMBean;
    private static HotspotCompilation hsCompileMBean;
    private static HotspotMemory hsMemoryMBean;
    private static DiagnosticCommandImpl hsDiagCommandMBean;
    private static final String HOTSPOT_CLASS_LOADING_MBEAN_NAME = "sun.management:type=HotspotClassLoading";
    private static final String HOTSPOT_COMPILATION_MBEAN_NAME = "sun.management:type=HotspotCompilation";
    private static final String HOTSPOT_MEMORY_MBEAN_NAME = "sun.management:type=HotspotMemory";
    private static final String HOTSPOT_RUNTIME_MBEAN_NAME = "sun.management:type=HotspotRuntime";
    private static final String HOTSPOT_THREAD_MBEAN_NAME = "sun.management:type=HotspotThreading";
    static final String HOTSPOT_DIAGNOSTIC_COMMAND_MBEAN_NAME = "com.sun.management:type=DiagnosticCommand";
    private static final int JMM_THREAD_STATE_FLAG_MASK = -1048576;
    private static final int JMM_THREAD_STATE_FLAG_SUSPENDED = 1048576;
    private static final int JMM_THREAD_STATE_FLAG_NATIVE = 4194304;
    
    private ManagementFactoryHelper() {
    }
    
    public static synchronized ClassLoadingMXBean getClassLoadingMXBean() {
        if (ManagementFactoryHelper.classMBean == null) {
            ManagementFactoryHelper.classMBean = new ClassLoadingImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.classMBean;
    }
    
    public static synchronized MemoryMXBean getMemoryMXBean() {
        if (ManagementFactoryHelper.memoryMBean == null) {
            ManagementFactoryHelper.memoryMBean = new MemoryImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.memoryMBean;
    }
    
    public static synchronized ThreadMXBean getThreadMXBean() {
        if (ManagementFactoryHelper.threadMBean == null) {
            ManagementFactoryHelper.threadMBean = new ThreadImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.threadMBean;
    }
    
    public static synchronized RuntimeMXBean getRuntimeMXBean() {
        if (ManagementFactoryHelper.runtimeMBean == null) {
            ManagementFactoryHelper.runtimeMBean = new RuntimeImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.runtimeMBean;
    }
    
    public static synchronized CompilationMXBean getCompilationMXBean() {
        if (ManagementFactoryHelper.compileMBean == null && ManagementFactoryHelper.jvm.getCompilerName() != null) {
            ManagementFactoryHelper.compileMBean = new CompilationImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.compileMBean;
    }
    
    public static synchronized OperatingSystemMXBean getOperatingSystemMXBean() {
        if (ManagementFactoryHelper.osMBean == null) {
            ManagementFactoryHelper.osMBean = new OperatingSystemImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.osMBean;
    }
    
    public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
        final MemoryPoolMXBean[] memoryPools = MemoryImpl.getMemoryPools();
        final ArrayList list = new ArrayList(memoryPools.length);
        final MemoryPoolMXBean[] array = memoryPools;
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add((Object)array[i]);
        }
        return (List<MemoryPoolMXBean>)list;
    }
    
    public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
        final MemoryManagerMXBean[] memoryManagers = MemoryImpl.getMemoryManagers();
        final ArrayList list = new ArrayList(memoryManagers.length);
        final MemoryManagerMXBean[] array = memoryManagers;
        for (int length = array.length, i = 0; i < length; ++i) {
            list.add((Object)array[i]);
        }
        return (List<MemoryManagerMXBean>)list;
    }
    
    public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
        final MemoryManagerMXBean[] memoryManagers = MemoryImpl.getMemoryManagers();
        final ArrayList list = new ArrayList(memoryManagers.length);
        for (final MemoryManagerMXBean memoryManagerMXBean : memoryManagers) {
            if (GarbageCollectorMXBean.class.isInstance(memoryManagerMXBean)) {
                list.add((Object)GarbageCollectorMXBean.class.cast(memoryManagerMXBean));
            }
        }
        return (List<GarbageCollectorMXBean>)list;
    }
    
    public static PlatformLoggingMXBean getPlatformLoggingMXBean() {
        if (LoggingSupport.isAvailable()) {
            return PlatformLoggingImpl.instance;
        }
        return null;
    }
    
    public static synchronized List<BufferPoolMXBean> getBufferPoolMXBeans() {
        if (ManagementFactoryHelper.bufferPools == null) {
            (ManagementFactoryHelper.bufferPools = new ArrayList<BufferPoolMXBean>(2)).add(createBufferPoolMXBean(SharedSecrets.getJavaNioAccess().getDirectBufferPool()));
            ManagementFactoryHelper.bufferPools.add(createBufferPoolMXBean(FileChannelImpl.getMappedBufferPool()));
        }
        return ManagementFactoryHelper.bufferPools;
    }
    
    private static BufferPoolMXBean createBufferPoolMXBean(final JavaNioAccess.BufferPool bufferPool) {
        return new BufferPoolMXBean() {
            private volatile ObjectName objname;
            
            @Override
            public ObjectName getObjectName() {
                ObjectName objname = this.objname;
                if (objname == null) {
                    synchronized (this) {
                        objname = this.objname;
                        if (objname == null) {
                            objname = Util.newObjectName("java.nio:type=BufferPool,name=" + bufferPool.getName());
                            this.objname = objname;
                        }
                    }
                }
                return objname;
            }
            
            @Override
            public String getName() {
                return bufferPool.getName();
            }
            
            @Override
            public long getCount() {
                return bufferPool.getCount();
            }
            
            @Override
            public long getTotalCapacity() {
                return bufferPool.getTotalCapacity();
            }
            
            @Override
            public long getMemoryUsed() {
                return bufferPool.getMemoryUsed();
            }
        };
    }
    
    public static synchronized HotSpotDiagnosticMXBean getDiagnosticMXBean() {
        if (ManagementFactoryHelper.hsDiagMBean == null) {
            ManagementFactoryHelper.hsDiagMBean = new HotSpotDiagnostic();
        }
        return ManagementFactoryHelper.hsDiagMBean;
    }
    
    public static synchronized HotspotRuntimeMBean getHotspotRuntimeMBean() {
        if (ManagementFactoryHelper.hsRuntimeMBean == null) {
            ManagementFactoryHelper.hsRuntimeMBean = new HotspotRuntime(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsRuntimeMBean;
    }
    
    public static synchronized HotspotClassLoadingMBean getHotspotClassLoadingMBean() {
        if (ManagementFactoryHelper.hsClassMBean == null) {
            ManagementFactoryHelper.hsClassMBean = new HotspotClassLoading(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsClassMBean;
    }
    
    public static synchronized HotspotThreadMBean getHotspotThreadMBean() {
        if (ManagementFactoryHelper.hsThreadMBean == null) {
            ManagementFactoryHelper.hsThreadMBean = new HotspotThread(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsThreadMBean;
    }
    
    public static synchronized HotspotMemoryMBean getHotspotMemoryMBean() {
        if (ManagementFactoryHelper.hsMemoryMBean == null) {
            ManagementFactoryHelper.hsMemoryMBean = new HotspotMemory(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsMemoryMBean;
    }
    
    public static synchronized DiagnosticCommandMBean getDiagnosticCommandMBean() {
        if (ManagementFactoryHelper.hsDiagCommandMBean == null && ManagementFactoryHelper.jvm.isRemoteDiagnosticCommandsSupported()) {
            ManagementFactoryHelper.hsDiagCommandMBean = new DiagnosticCommandImpl(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsDiagCommandMBean;
    }
    
    public static synchronized HotspotCompilationMBean getHotspotCompilationMBean() {
        if (ManagementFactoryHelper.hsCompileMBean == null) {
            ManagementFactoryHelper.hsCompileMBean = new HotspotCompilation(ManagementFactoryHelper.jvm);
        }
        return ManagementFactoryHelper.hsCompileMBean;
    }
    
    private static void addMBean(final MBeanServer mBeanServer, final Object o, final String s) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                final /* synthetic */ ObjectName val$objName = Util.newObjectName(s);
                
                @Override
                public Void run() throws MBeanRegistrationException, NotCompliantMBeanException {
                    try {
                        mBeanServer.registerMBean(o, this.val$objName);
                        return null;
                    }
                    catch (final InstanceAlreadyExistsException ex) {
                        return null;
                    }
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw Util.newException(ex.getException());
        }
    }
    
    public static HashMap<ObjectName, DynamicMBean> getPlatformDynamicMBeans() {
        final HashMap hashMap = new HashMap();
        final DiagnosticCommandMBean diagnosticCommandMBean = getDiagnosticCommandMBean();
        if (diagnosticCommandMBean != null) {
            hashMap.put(Util.newObjectName("com.sun.management:type=DiagnosticCommand"), diagnosticCommandMBean);
        }
        return hashMap;
    }
    
    static void registerInternalMBeans(final MBeanServer mBeanServer) {
        addMBean(mBeanServer, getHotspotClassLoadingMBean(), "sun.management:type=HotspotClassLoading");
        addMBean(mBeanServer, getHotspotMemoryMBean(), "sun.management:type=HotspotMemory");
        addMBean(mBeanServer, getHotspotRuntimeMBean(), "sun.management:type=HotspotRuntime");
        addMBean(mBeanServer, getHotspotThreadMBean(), "sun.management:type=HotspotThreading");
        if (getCompilationMXBean() != null) {
            addMBean(mBeanServer, getHotspotCompilationMBean(), "sun.management:type=HotspotCompilation");
        }
    }
    
    private static void unregisterMBean(final MBeanServer mBeanServer, final String s) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                final /* synthetic */ ObjectName val$objName = Util.newObjectName(s);
                
                @Override
                public Void run() throws MBeanRegistrationException, RuntimeOperationsException {
                    try {
                        mBeanServer.unregisterMBean(this.val$objName);
                    }
                    catch (final InstanceNotFoundException ex) {}
                    return null;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            throw Util.newException(ex.getException());
        }
    }
    
    static void unregisterInternalMBeans(final MBeanServer mBeanServer) {
        unregisterMBean(mBeanServer, "sun.management:type=HotspotClassLoading");
        unregisterMBean(mBeanServer, "sun.management:type=HotspotMemory");
        unregisterMBean(mBeanServer, "sun.management:type=HotspotRuntime");
        unregisterMBean(mBeanServer, "sun.management:type=HotspotThreading");
        if (getCompilationMXBean() != null) {
            unregisterMBean(mBeanServer, "sun.management:type=HotspotCompilation");
        }
    }
    
    public static boolean isThreadSuspended(final int n) {
        return (n & 0x100000) != 0x0;
    }
    
    public static boolean isThreadRunningNative(final int n) {
        return (n & 0x400000) != 0x0;
    }
    
    public static Thread.State toThreadState(final int n) {
        return VM.toThreadState(n & 0xFFFFF);
    }
    
    static {
        ManagementFactoryHelper.classMBean = null;
        ManagementFactoryHelper.memoryMBean = null;
        ManagementFactoryHelper.threadMBean = null;
        ManagementFactoryHelper.runtimeMBean = null;
        ManagementFactoryHelper.compileMBean = null;
        ManagementFactoryHelper.osMBean = null;
        ManagementFactoryHelper.bufferPools = null;
        ManagementFactoryHelper.hsDiagMBean = null;
        ManagementFactoryHelper.hsRuntimeMBean = null;
        ManagementFactoryHelper.hsClassMBean = null;
        ManagementFactoryHelper.hsThreadMBean = null;
        ManagementFactoryHelper.hsCompileMBean = null;
        ManagementFactoryHelper.hsMemoryMBean = null;
        ManagementFactoryHelper.hsDiagCommandMBean = null;
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("management");
                return null;
            }
        });
        ManagementFactoryHelper.jvm = new VMManagementImpl();
    }
    
    static class PlatformLoggingImpl implements LoggingMXBean
    {
        static final PlatformLoggingMXBean instance;
        static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
        private volatile ObjectName objname;
        
        @Override
        public ObjectName getObjectName() {
            ObjectName objname = this.objname;
            if (objname == null) {
                synchronized (this) {
                    objname = this.objname;
                    if (objname == null) {
                        objname = Util.newObjectName("java.util.logging:type=Logging");
                        this.objname = objname;
                    }
                }
            }
            return objname;
        }
        
        @Override
        public List<String> getLoggerNames() {
            return LoggingSupport.getLoggerNames();
        }
        
        @Override
        public String getLoggerLevel(final String s) {
            return LoggingSupport.getLoggerLevel(s);
        }
        
        @Override
        public void setLoggerLevel(final String s, final String s2) {
            LoggingSupport.setLoggerLevel(s, s2);
        }
        
        @Override
        public String getParentLoggerName(final String s) {
            return LoggingSupport.getParentLoggerName(s);
        }
        
        static {
            instance = new PlatformLoggingImpl();
        }
    }
    
    public interface LoggingMXBean extends PlatformLoggingMXBean, java.util.logging.LoggingMXBean
    {
    }
}
