package sun.io;

import sun.misc.VM;

public class Win32ErrorMode
{
    private static final long SEM_FAILCRITICALERRORS = 1L;
    private static final long SEM_NOGPFAULTERRORBOX = 2L;
    private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
    private static final long SEM_NOOPENFILEERRORBOX = 32768L;
    
    private Win32ErrorMode() {
    }
    
    public static void initialize() {
        if (!VM.isBooted()) {
            final String property = System.getProperty("sun.io.allowCriticalErrorMessageBox");
            if (property == null || property.equals(Boolean.FALSE.toString())) {
                setErrorMode(setErrorMode(0L) | 0x1L);
            }
        }
    }
    
    private static native long setErrorMode(final long p0);
}
