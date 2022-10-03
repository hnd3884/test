package sun.java2d.marlin;

import sun.util.logging.PlatformLogger;

public final class MarlinUtils
{
    private static final PlatformLogger LOG;
    
    private MarlinUtils() {
    }
    
    public static void logInfo(final String s) {
        if (MarlinConst.USE_LOGGER) {
            MarlinUtils.LOG.info(s);
        }
        else if (MarlinConst.ENABLE_LOGS) {
            System.out.print("INFO: ");
            System.out.println(s);
        }
    }
    
    public static void logException(final String s, final Throwable t) {
        if (MarlinConst.USE_LOGGER) {
            MarlinUtils.LOG.warning(s, t);
        }
        else if (MarlinConst.ENABLE_LOGS) {
            System.out.print("WARNING: ");
            System.out.println(s);
            t.printStackTrace(System.err);
        }
    }
    
    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        for (ThreadGroup threadGroup2 = threadGroup.getParent(); threadGroup2 != null; threadGroup2 = threadGroup.getParent()) {
            threadGroup = threadGroup2;
        }
        return threadGroup;
    }
    
    static {
        if (MarlinConst.USE_LOGGER) {
            LOG = PlatformLogger.getLogger("sun.java2d.marlin");
        }
        else {
            LOG = null;
        }
    }
}
