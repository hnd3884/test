package sun.misc;

import java.security.PrivilegedAction;
import java.security.AccessController;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

public class PerfCounter
{
    private static final Perf perf;
    private static final int V_Constant = 1;
    private static final int V_Monotonic = 2;
    private static final int V_Variable = 3;
    private static final int U_None = 1;
    private final String name;
    private final LongBuffer lb;
    
    private PerfCounter(final String name, final int n) {
        this.name = name;
        final ByteBuffer long1 = PerfCounter.perf.createLong(name, n, 1, 0L);
        long1.order(ByteOrder.nativeOrder());
        this.lb = long1.asLongBuffer();
    }
    
    static PerfCounter newPerfCounter(final String s) {
        return new PerfCounter(s, 3);
    }
    
    static PerfCounter newConstantPerfCounter(final String s) {
        return new PerfCounter(s, 1);
    }
    
    public synchronized long get() {
        return this.lb.get(0);
    }
    
    public synchronized void set(final long n) {
        this.lb.put(0, n);
    }
    
    public synchronized void add(final long n) {
        this.lb.put(0, this.get() + n);
    }
    
    public void increment() {
        this.add(1L);
    }
    
    public void addTime(final long n) {
        this.add(n);
    }
    
    public void addElapsedTimeFrom(final long n) {
        this.add(System.nanoTime() - n);
    }
    
    @Override
    public String toString() {
        return this.name + " = " + this.get();
    }
    
    public static PerfCounter getFindClasses() {
        return CoreCounters.lc;
    }
    
    public static PerfCounter getFindClassTime() {
        return CoreCounters.lct;
    }
    
    public static PerfCounter getReadClassBytesTime() {
        return CoreCounters.rcbt;
    }
    
    public static PerfCounter getParentDelegationTime() {
        return CoreCounters.pdt;
    }
    
    public static PerfCounter getZipFileCount() {
        return CoreCounters.zfc;
    }
    
    public static PerfCounter getZipFileOpenTime() {
        return CoreCounters.zfot;
    }
    
    public static PerfCounter getD3DAvailable() {
        return WindowsClientCounters.d3dAvailable;
    }
    
    static {
        perf = AccessController.doPrivileged((PrivilegedAction<Perf>)new Perf.GetPerfAction());
    }
    
    static class CoreCounters
    {
        static final PerfCounter pdt;
        static final PerfCounter lc;
        static final PerfCounter lct;
        static final PerfCounter rcbt;
        static final PerfCounter zfc;
        static final PerfCounter zfot;
        
        static {
            pdt = PerfCounter.newPerfCounter("sun.classloader.parentDelegationTime");
            lc = PerfCounter.newPerfCounter("sun.classloader.findClasses");
            lct = PerfCounter.newPerfCounter("sun.classloader.findClassTime");
            rcbt = PerfCounter.newPerfCounter("sun.urlClassLoader.readClassBytesTime");
            zfc = PerfCounter.newPerfCounter("sun.zip.zipFiles");
            zfot = PerfCounter.newPerfCounter("sun.zip.zipFile.openTime");
        }
    }
    
    static class WindowsClientCounters
    {
        static final PerfCounter d3dAvailable;
        
        static {
            d3dAvailable = PerfCounter.newConstantPerfCounter("sun.java2d.d3d.available");
        }
    }
}
