package sun.management;

import sun.management.counter.Counter;
import java.nio.ByteBuffer;
import java.io.IOException;
import sun.misc.Perf;
import java.util.Collections;
import java.util.Arrays;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.net.UnknownHostException;
import java.net.InetAddress;
import sun.management.counter.perf.PerfInstrumentation;
import java.util.List;

class VMManagementImpl implements VMManagement
{
    private static String version;
    private static boolean compTimeMonitoringSupport;
    private static boolean threadContentionMonitoringSupport;
    private static boolean currentThreadCpuTimeSupport;
    private static boolean otherThreadCpuTimeSupport;
    private static boolean bootClassPathSupport;
    private static boolean objectMonitorUsageSupport;
    private static boolean synchronizerUsageSupport;
    private static boolean threadAllocatedMemorySupport;
    private static boolean gcNotificationSupport;
    private static boolean remoteDiagnosticCommandsSupport;
    private List<String> vmArgs;
    private PerfInstrumentation perfInstr;
    private boolean noPerfData;
    
    VMManagementImpl() {
        this.vmArgs = null;
        this.perfInstr = null;
        this.noPerfData = false;
    }
    
    private static native String getVersion0();
    
    private static native void initOptionalSupportFields();
    
    @Override
    public boolean isCompilationTimeMonitoringSupported() {
        return VMManagementImpl.compTimeMonitoringSupport;
    }
    
    @Override
    public boolean isThreadContentionMonitoringSupported() {
        return VMManagementImpl.threadContentionMonitoringSupport;
    }
    
    @Override
    public boolean isCurrentThreadCpuTimeSupported() {
        return VMManagementImpl.currentThreadCpuTimeSupport;
    }
    
    @Override
    public boolean isOtherThreadCpuTimeSupported() {
        return VMManagementImpl.otherThreadCpuTimeSupport;
    }
    
    @Override
    public boolean isBootClassPathSupported() {
        return VMManagementImpl.bootClassPathSupport;
    }
    
    @Override
    public boolean isObjectMonitorUsageSupported() {
        return VMManagementImpl.objectMonitorUsageSupport;
    }
    
    @Override
    public boolean isSynchronizerUsageSupported() {
        return VMManagementImpl.synchronizerUsageSupport;
    }
    
    @Override
    public boolean isThreadAllocatedMemorySupported() {
        return VMManagementImpl.threadAllocatedMemorySupport;
    }
    
    @Override
    public boolean isGcNotificationSupported() {
        return VMManagementImpl.gcNotificationSupport;
    }
    
    @Override
    public boolean isRemoteDiagnosticCommandsSupported() {
        return VMManagementImpl.remoteDiagnosticCommandsSupport;
    }
    
    @Override
    public native boolean isThreadContentionMonitoringEnabled();
    
    @Override
    public native boolean isThreadCpuTimeEnabled();
    
    @Override
    public native boolean isThreadAllocatedMemoryEnabled();
    
    @Override
    public int getLoadedClassCount() {
        return (int)(this.getTotalClassCount() - this.getUnloadedClassCount());
    }
    
    @Override
    public native long getTotalClassCount();
    
    @Override
    public native long getUnloadedClassCount();
    
    @Override
    public native boolean getVerboseClass();
    
    @Override
    public native boolean getVerboseGC();
    
    @Override
    public String getManagementVersion() {
        return VMManagementImpl.version;
    }
    
    @Override
    public String getVmId() {
        final int processId = this.getProcessId();
        String hostName = "localhost";
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException ex) {}
        return processId + "@" + hostName;
    }
    
    private native int getProcessId();
    
    @Override
    public String getVmName() {
        return System.getProperty("java.vm.name");
    }
    
    @Override
    public String getVmVendor() {
        return System.getProperty("java.vm.vendor");
    }
    
    @Override
    public String getVmVersion() {
        return System.getProperty("java.vm.version");
    }
    
    @Override
    public String getVmSpecName() {
        return System.getProperty("java.vm.specification.name");
    }
    
    @Override
    public String getVmSpecVendor() {
        return System.getProperty("java.vm.specification.vendor");
    }
    
    @Override
    public String getVmSpecVersion() {
        return System.getProperty("java.vm.specification.version");
    }
    
    @Override
    public String getClassPath() {
        return System.getProperty("java.class.path");
    }
    
    @Override
    public String getLibraryPath() {
        return System.getProperty("java.library.path");
    }
    
    @Override
    public String getBootClassPath() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.boot.class.path"));
    }
    
    @Override
    public long getUptime() {
        return this.getUptime0();
    }
    
    @Override
    public synchronized List<String> getVmArguments() {
        if (this.vmArgs == null) {
            final String[] vmArguments0 = this.getVmArguments0();
            this.vmArgs = (List<String>)Collections.unmodifiableList((List<?>)((vmArguments0 != null && vmArguments0.length != 0) ? Arrays.asList(vmArguments0) : Collections.emptyList()));
        }
        return this.vmArgs;
    }
    
    public native String[] getVmArguments0();
    
    @Override
    public native long getStartupTime();
    
    private native long getUptime0();
    
    @Override
    public native int getAvailableProcessors();
    
    @Override
    public String getCompilerName() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("sun.management.compiler");
            }
        });
    }
    
    @Override
    public native long getTotalCompileTime();
    
    @Override
    public native long getTotalThreadCount();
    
    @Override
    public native int getLiveThreadCount();
    
    @Override
    public native int getPeakThreadCount();
    
    @Override
    public native int getDaemonThreadCount();
    
    @Override
    public String getOsName() {
        return System.getProperty("os.name");
    }
    
    @Override
    public String getOsArch() {
        return System.getProperty("os.arch");
    }
    
    @Override
    public String getOsVersion() {
        return System.getProperty("os.version");
    }
    
    @Override
    public native long getSafepointCount();
    
    @Override
    public native long getTotalSafepointTime();
    
    @Override
    public native long getSafepointSyncTime();
    
    @Override
    public native long getTotalApplicationNonStoppedTime();
    
    @Override
    public native long getLoadedClassSize();
    
    @Override
    public native long getUnloadedClassSize();
    
    @Override
    public native long getClassLoadingTime();
    
    @Override
    public native long getMethodDataSize();
    
    @Override
    public native long getInitializedClassCount();
    
    @Override
    public native long getClassInitializationTime();
    
    @Override
    public native long getClassVerificationTime();
    
    private synchronized PerfInstrumentation getPerfInstrumentation() {
        if (this.noPerfData || this.perfInstr != null) {
            return this.perfInstr;
        }
        final Perf perf = AccessController.doPrivileged((PrivilegedAction<Perf>)new Perf.GetPerfAction());
        try {
            final ByteBuffer attach = perf.attach(0, "r");
            if (attach.capacity() == 0) {
                this.noPerfData = true;
                return null;
            }
            this.perfInstr = new PerfInstrumentation(attach);
        }
        catch (final IllegalArgumentException ex) {
            this.noPerfData = true;
        }
        catch (final IOException ex2) {
            throw new AssertionError((Object)ex2);
        }
        return this.perfInstr;
    }
    
    @Override
    public List<Counter> getInternalCounters(final String s) {
        final PerfInstrumentation perfInstrumentation = this.getPerfInstrumentation();
        if (perfInstrumentation != null) {
            return perfInstrumentation.findByPattern(s);
        }
        return Collections.emptyList();
    }
    
    static {
        VMManagementImpl.version = getVersion0();
        if (VMManagementImpl.version == null) {
            throw new AssertionError((Object)"Invalid Management Version");
        }
        initOptionalSupportFields();
    }
}
