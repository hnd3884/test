package org.glassfish.hk2.utilities.reflection;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Level;

public class Logger
{
    private static final Logger INSTANCE;
    private static final String HK2_LOGGER_NAME = "org.jvnet.hk2.logger";
    private static final boolean STDOUT_DEBUG;
    private final java.util.logging.Logger jdkLogger;
    
    private Logger() {
        this.jdkLogger = java.util.logging.Logger.getLogger("org.jvnet.hk2.logger");
    }
    
    public static Logger getLogger() {
        return Logger.INSTANCE;
    }
    
    public void debug(final String debuggingMessage) {
        this.jdkLogger.finer(debuggingMessage);
        if (Logger.STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: " + debuggingMessage);
        }
    }
    
    public void debug(final String debuggingMessage, final Throwable th) {
        this.jdkLogger.log(Level.FINER, debuggingMessage, th);
        if (Logger.STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: " + debuggingMessage);
            printThrowable(th);
        }
    }
    
    public void warning(final String warningMessage) {
        this.jdkLogger.warning(warningMessage);
        if (Logger.STDOUT_DEBUG) {
            System.out.println("HK2DEBUG (Warning): " + warningMessage);
        }
    }
    
    public void warning(final String warningMessage, final Throwable th) {
        this.jdkLogger.log(Level.WARNING, warningMessage, th);
        if (Logger.STDOUT_DEBUG) {
            System.out.println("HK2DEBUG (Warning): " + warningMessage);
            printThrowable(th);
        }
    }
    
    public static void printThrowable(final Throwable th) {
        int lcv = 0;
        for (Throwable cause = th; cause != null; cause = cause.getCause()) {
            System.out.println("HK2DEBUG: Throwable[" + lcv++ + "] message is " + cause.getMessage());
            cause.printStackTrace(System.out);
        }
    }
    
    public void debug(final String className, final String methodName, final Throwable th) {
        this.jdkLogger.throwing(className, methodName, th);
        if (Logger.STDOUT_DEBUG) {
            System.out.println("HK2DEBUG: className=" + className + " methodName=" + methodName);
            printThrowable(th);
        }
    }
    
    static {
        INSTANCE = new Logger();
        STDOUT_DEBUG = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.parseBoolean(System.getProperty("org.jvnet.hk2.logger.debugToStdout", "false"));
            }
        });
    }
}
