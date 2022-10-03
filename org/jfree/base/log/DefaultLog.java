package org.jfree.base.log;

import org.jfree.util.LogTarget;
import org.jfree.util.PrintStreamLogTarget;
import org.jfree.util.Log;

public class DefaultLog extends Log
{
    private static final PrintStreamLogTarget DEFAULT_LOG_TARGET;
    private static final DefaultLog defaultLogInstance;
    
    static {
        DEFAULT_LOG_TARGET = new PrintStreamLogTarget();
        (defaultLogInstance = new DefaultLog()).addTarget(DefaultLog.DEFAULT_LOG_TARGET);
        try {
            final String property = System.getProperty("org.jfree.DebugDefault", "false");
            if (Boolean.valueOf(property)) {
                DefaultLog.defaultLogInstance.setDebuglevel(3);
            }
            else {
                DefaultLog.defaultLogInstance.setDebuglevel(1);
            }
        }
        catch (final SecurityException ex) {
            DefaultLog.defaultLogInstance.setDebuglevel(1);
        }
    }
    
    protected DefaultLog() {
    }
    
    public synchronized void addTarget(final LogTarget target) {
        super.addTarget(target);
        if (target != DefaultLog.DEFAULT_LOG_TARGET) {
            this.removeTarget(DefaultLog.DEFAULT_LOG_TARGET);
        }
    }
    
    public static DefaultLog getDefaultLog() {
        return DefaultLog.defaultLogInstance;
    }
    
    public void init() {
        this.removeTarget(DefaultLog.DEFAULT_LOG_TARGET);
        final String logLevel = LogConfiguration.getLogLevel();
        if (logLevel.equalsIgnoreCase("error")) {
            this.setDebuglevel(0);
        }
        else if (logLevel.equalsIgnoreCase("warn")) {
            this.setDebuglevel(1);
        }
        else if (logLevel.equalsIgnoreCase("info")) {
            this.setDebuglevel(2);
        }
        else if (logLevel.equalsIgnoreCase("debug")) {
            this.setDebuglevel(3);
        }
    }
    
    public static void installDefaultLog() {
        Log.defineLog(DefaultLog.defaultLogInstance);
    }
}
