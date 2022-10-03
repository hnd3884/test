package jdk.jfr.internal;

import java.io.IOException;

public final class JVMSupport
{
    private static final String UNSUPPORTED_VM_MESSAGE = "Flight Recorder is not supported on this VM";
    private static final boolean notAvailable;
    
    private static boolean checkAvailability() {
        try {
            if (SecuritySupport.getBooleanProperty("jfr.unsupported.vm")) {
                return false;
            }
        }
        catch (final NoClassDefFoundError noClassDefFoundError) {
            return false;
        }
        try {
            JVM.getJVM().isAvailable();
            return true;
        }
        catch (final Throwable t) {
            return false;
        }
    }
    
    public static void ensureWithInternalError() {
        if (JVMSupport.notAvailable) {
            throw new InternalError("Flight Recorder is not supported on this VM");
        }
    }
    
    public static void ensureWithIOException() throws IOException {
        if (JVMSupport.notAvailable) {
            throw new IOException("Flight Recorder is not supported on this VM");
        }
    }
    
    public static void ensureWithIllegalStateException() {
        if (JVMSupport.notAvailable) {
            throw new IllegalStateException("Flight Recorder is not supported on this VM");
        }
    }
    
    public static boolean isNotAvailable() {
        return JVMSupport.notAvailable;
    }
    
    public static void tryToInitializeJVM() {
    }
    
    static {
        notAvailable = !checkAvailability();
    }
}
