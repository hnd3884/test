package jdk.jfr.internal;

import sun.misc.Unsafe;

public final class Options
{
    private static final JVM jvm;
    private static final long WAIT_INTERVAL = 1000L;
    private static final long MIN_MAX_CHUNKSIZE = 1048576L;
    private static final long DEFAULT_GLOBAL_BUFFER_COUNT = 20L;
    private static final long DEFAULT_GLOBAL_BUFFER_SIZE = 524288L;
    private static final long DEFAULT_MEMORY_SIZE = 10485760L;
    private static long DEFAULT_THREAD_BUFFER_SIZE;
    private static final int DEFAULT_STACK_DEPTH = 64;
    private static final boolean DEFAULT_SAMPLE_THREADS = true;
    private static final long DEFAULT_MAX_CHUNK_SIZE = 12582912L;
    private static final SecuritySupport.SafePath DEFAULT_DUMP_PATH;
    private static long memorySize;
    private static long globalBufferSize;
    private static long globalBufferCount;
    private static long threadBufferSize;
    private static int stackDepth;
    private static boolean sampleThreads;
    private static long maxChunkSize;
    private static SecuritySupport.SafePath dumpPath;
    
    public static synchronized void setMaxChunkSize(final long n) {
        if (n < 1048576L) {
            throw new IllegalArgumentException("Max chunk size must be at least 1048576");
        }
        Options.jvm.setFileNotification(n);
        Options.maxChunkSize = n;
    }
    
    public static synchronized long getMaxChunkSize() {
        return Options.maxChunkSize;
    }
    
    public static synchronized void setMemorySize(final long n) {
        Options.jvm.setMemorySize(n);
        Options.memorySize = n;
    }
    
    public static synchronized long getMemorySize() {
        return Options.memorySize;
    }
    
    public static synchronized void setThreadBufferSize(final long n) {
        Options.jvm.setThreadBufferSize(n);
        Options.threadBufferSize = n;
    }
    
    public static synchronized long getThreadBufferSize() {
        return Options.threadBufferSize;
    }
    
    public static synchronized long getGlobalBufferSize() {
        return Options.globalBufferSize;
    }
    
    public static synchronized void setGlobalBufferCount(final long n) {
        Options.jvm.setGlobalBufferCount(n);
        Options.globalBufferCount = n;
    }
    
    public static synchronized long getGlobalBufferCount() {
        return Options.globalBufferCount;
    }
    
    public static synchronized void setGlobalBufferSize(final long n) {
        Options.jvm.setGlobalBufferSize(n);
        Options.globalBufferSize = n;
    }
    
    public static synchronized void setDumpPath(final SecuritySupport.SafePath dumpPath) {
        Options.dumpPath = dumpPath;
    }
    
    public static synchronized SecuritySupport.SafePath getDumpPath() {
        return Options.dumpPath;
    }
    
    public static synchronized void setStackDepth(final Integer n) {
        Options.jvm.setStackDepth(n);
        Options.stackDepth = n;
    }
    
    public static synchronized int getStackDepth() {
        return Options.stackDepth;
    }
    
    public static synchronized void setSampleThreads(final Boolean b) {
        Options.jvm.setSampleThreads(b);
        Options.sampleThreads = b;
    }
    
    public static synchronized boolean getSampleThreads() {
        return Options.sampleThreads;
    }
    
    private static synchronized void reset() {
        setMaxChunkSize(12582912L);
        setMemorySize(10485760L);
        setGlobalBufferSize(524288L);
        setGlobalBufferCount(20L);
        setDumpPath(Options.DEFAULT_DUMP_PATH);
        setSampleThreads(true);
        setStackDepth(64);
        setThreadBufferSize(Options.DEFAULT_THREAD_BUFFER_SIZE);
    }
    
    static synchronized long getWaitInterval() {
        return 1000L;
    }
    
    static void ensureInitialized() {
    }
    
    static {
        jvm = JVM.getJVM();
        DEFAULT_DUMP_PATH = SecuritySupport.USER_HOME;
        final long n = Unsafe.getUnsafe().pageSize();
        Options.DEFAULT_THREAD_BUFFER_SIZE = ((n > 8192L) ? n : 8192L);
        reset();
    }
}
