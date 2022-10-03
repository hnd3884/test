package org.jfree.util;

import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class Log
{
    private int debuglevel;
    private LogTarget[] logTargets;
    private HashMap logContexts;
    private static Log singleton;
    
    protected Log() {
        this.logContexts = new HashMap();
        this.logTargets = new LogTarget[0];
        this.debuglevel = 100;
    }
    
    public synchronized void addTarget(final LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        final LogTarget[] data = new LogTarget[this.logTargets.length + 1];
        System.arraycopy(this.logTargets, 0, data, 0, this.logTargets.length);
        data[this.logTargets.length] = target;
        this.logTargets = data;
    }
    
    public static LogContext createContext(final Class context) {
        return createContext(context.getName());
    }
    
    public static LogContext createContext(final String context) {
        return getInstance().internalCreateContext(context);
    }
    
    public static void debug(final Object message) {
        log(3, message);
    }
    
    public static void debug(final Object message, final Exception e) {
        log(3, message, e);
    }
    
    protected static synchronized void defineLog(final Log log) {
        Log.singleton = log;
    }
    
    protected void doLog(int level, final Object message) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (int i = 0; i < this.logTargets.length; ++i) {
                final LogTarget t = this.logTargets[i];
                t.log(level, message);
            }
        }
    }
    
    protected void doLog(int level, final Object message, final Exception e) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (int i = 0; i < this.logTargets.length; ++i) {
                final LogTarget t = this.logTargets[i];
                t.log(level, message, e);
            }
        }
    }
    
    public static void error(final Object message) {
        log(0, message);
    }
    
    public static void error(final Object message, final Exception e) {
        log(0, message, e);
    }
    
    public int getDebuglevel() {
        return this.debuglevel;
    }
    
    public static synchronized Log getInstance() {
        if (Log.singleton == null) {
            Log.singleton = new Log();
        }
        return Log.singleton;
    }
    
    public LogTarget[] getTargets() {
        return this.logTargets.clone();
    }
    
    public static void info(final Object message) {
        log(2, message);
    }
    
    public static void info(final Object message, final Exception e) {
        log(2, message, e);
    }
    
    public void init() {
    }
    
    protected LogContext internalCreateContext(final String context) {
        synchronized (this) {
            LogContext ctx = this.logContexts.get(context);
            if (ctx == null) {
                ctx = new LogContext(context);
                this.logContexts.put(context, ctx);
            }
            return ctx;
        }
    }
    
    public static boolean isDebugEnabled() {
        return getInstance().getDebuglevel() >= 3;
    }
    
    public static boolean isErrorEnabled() {
        return getInstance().getDebuglevel() >= 0;
    }
    
    public static boolean isInfoEnabled() {
        return getInstance().getDebuglevel() >= 2;
    }
    
    public static boolean isWarningEnabled() {
        return getInstance().getDebuglevel() >= 1;
    }
    
    public static void log(final int level, final Object message) {
        getInstance().doLog(level, message);
    }
    
    public static void log(final int level, final Object message, final Exception e) {
        getInstance().doLog(level, message, e);
    }
    
    public synchronized void removeTarget(final LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        final ArrayList l = new ArrayList();
        l.addAll(Arrays.asList(this.logTargets));
        l.remove(target);
        final LogTarget[] targets = new LogTarget[l.size()];
        this.logTargets = l.toArray(targets);
    }
    
    public synchronized void replaceTargets(final LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        this.logTargets = new LogTarget[] { target };
    }
    
    protected void setDebuglevel(final int debuglevel) {
        this.debuglevel = debuglevel;
    }
    
    public static void warn(final Object message) {
        log(1, message);
    }
    
    public static void warn(final Object message, final Exception e) {
        log(1, message, e);
    }
    
    public static class SimpleMessage
    {
        private String message;
        private Object[] param;
        
        public SimpleMessage(final String message, final Object param1) {
            this.message = message;
            this.param = new Object[] { param1 };
        }
        
        public SimpleMessage(final String message, final Object param1, final Object param2) {
            this.message = message;
            this.param = new Object[] { param1, param2 };
        }
        
        public SimpleMessage(final String message, final Object param1, final Object param2, final Object param3) {
            this.message = message;
            this.param = new Object[] { param1, param2, param3 };
        }
        
        public SimpleMessage(final String message, final Object param1, final Object param2, final Object param3, final Object param4) {
            this.message = message;
            this.param = new Object[] { param1, param2, param3, param4 };
        }
        
        public SimpleMessage(final String message, final Object[] param) {
            this.message = message;
            this.param = param;
        }
        
        public String toString() {
            final StringBuffer b = new StringBuffer();
            b.append(this.message);
            if (this.param != null) {
                for (int i = 0; i < this.param.length; ++i) {
                    b.append(this.param[i]);
                }
            }
            return b.toString();
        }
    }
}
