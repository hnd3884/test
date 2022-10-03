package jdk.jfr.internal;

import jdk.jfr.Event;
import java.util.List;

public final class JVM
{
    private static final JVM jvm;
    static final Object FILE_DELTA_CHANGE;
    static final long RESERVED_CLASS_ID_LIMIT = 400L;
    private volatile boolean recording;
    private volatile boolean nativeOK;
    
    private static native void registerNatives();
    
    public static JVM getJVM() {
        return JVM.jvm;
    }
    
    private JVM() {
    }
    
    public native void beginRecording();
    
    public static native long counterTime();
    
    public native boolean emitEvent(final long p0, final long p1, final long p2);
    
    public native void endRecording();
    
    public native List<Class<? extends Event>> getAllEventClasses();
    
    public native long getUnloadedEventClassCount();
    
    public static native long getClassId(final Class<?> p0);
    
    public static native long getClassIdNonIntrinsic(final Class<?> p0);
    
    public native String getPid();
    
    public native long getStackTraceId(final int p0);
    
    public native long getThreadId(final Thread p0);
    
    public native long getTicksFrequency();
    
    public static native void log(final int p0, final int p1, final String p2);
    
    public static native void subscribeLogLevel(final LogTag p0, final int p1);
    
    public synchronized native void retransformClasses(final Class<?>[] p0);
    
    public native void setEnabled(final long p0, final boolean p1);
    
    public native void setFileNotification(final long p0);
    
    public native void setGlobalBufferCount(final long p0) throws IllegalArgumentException, IllegalStateException;
    
    public native void setGlobalBufferSize(final long p0) throws IllegalArgumentException;
    
    public native void setMemorySize(final long p0) throws IllegalArgumentException;
    
    public native void setMethodSamplingInterval(final long p0, final long p1);
    
    public native void setOutput(final String p0);
    
    public native void setForceInstrumentation(final boolean p0);
    
    public native void setSampleThreads(final boolean p0) throws IllegalStateException;
    
    public native void setCompressedIntegers(final boolean p0) throws IllegalStateException;
    
    public native void setStackDepth(final int p0) throws IllegalArgumentException, IllegalStateException;
    
    public native void setStackTraceEnabled(final long p0, final boolean p1);
    
    public native void setThreadBufferSize(final long p0) throws IllegalArgumentException, IllegalStateException;
    
    public native boolean setThreshold(final long p0, final long p1);
    
    public native void storeMetadataDescriptor(final byte[] p0);
    
    public void endRecording_() {
        this.endRecording();
        this.recording = false;
    }
    
    public void beginRecording_() {
        this.beginRecording();
        this.recording = true;
    }
    
    public boolean isRecording() {
        return this.recording;
    }
    
    public native boolean getAllowedToDoEventRetransforms();
    
    private native boolean createJFR(final boolean p0) throws IllegalStateException;
    
    private native boolean destroyJFR();
    
    public boolean createFailedNativeJFR() throws IllegalStateException {
        return this.createJFR(true);
    }
    
    public void createNativeJFR() {
        this.nativeOK = this.createJFR(false);
    }
    
    public boolean destroyNativeJFR() {
        final boolean destroyJFR = this.destroyJFR();
        this.nativeOK = !destroyJFR;
        return destroyJFR;
    }
    
    public boolean hasNativeJFR() {
        return this.nativeOK;
    }
    
    public native boolean isAvailable();
    
    public native double getTimeConversionFactor();
    
    public native long getTypeId(final Class<?> p0);
    
    public static native Object getEventWriter();
    
    public static native EventWriter newEventWriter();
    
    public static native boolean flush(final EventWriter p0, final int p1, final int p2);
    
    public native void setRepositoryLocation(final String p0);
    
    public native void abort(final String p0);
    
    public static native boolean addStringConstant(final boolean p0, final long p1, final String p2);
    
    public native long getEpochAddress();
    
    public native void uncaughtException(final Thread p0, final Throwable p1);
    
    public native boolean setCutoff(final long p0, final long p1);
    
    public native void emitOldObjectSamples(final long p0, final boolean p1);
    
    public native boolean shouldRotateDisk();
    
    static {
        jvm = new JVM();
        FILE_DELTA_CHANGE = new Object();
        registerNatives();
        Options.ensureInitialized();
        EventHandlerProxyCreator.ensureInitialized();
    }
}
