package org.apache.commons.logging.impl;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.logging.LogConfigurationException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Properties;
import java.io.Serializable;
import org.apache.commons.logging.Log;

public class SimpleLog implements Log, Serializable
{
    protected static final String systemPrefix = "org.apache.commons.logging.simplelog.";
    protected static final Properties simpleLogProps;
    protected static final String DEFAULT_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss:SSS zzz";
    protected static boolean showLogName;
    protected static boolean showShortName;
    protected static boolean showDateTime;
    protected static String dateTimeFormat;
    protected static DateFormat dateFormatter;
    public static final int LOG_LEVEL_TRACE = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_WARN = 4;
    public static final int LOG_LEVEL_ERROR = 5;
    public static final int LOG_LEVEL_FATAL = 6;
    public static final int LOG_LEVEL_ALL = 0;
    public static final int LOG_LEVEL_OFF = 7;
    protected String logName;
    protected int currentLogLevel;
    private String shortLogName;
    static /* synthetic */ Class class$java$lang$Thread;
    static /* synthetic */ Class class$org$apache$commons$logging$impl$SimpleLog;
    
    static {
        simpleLogProps = new Properties();
        SimpleLog.showLogName = false;
        SimpleLog.showShortName = true;
        SimpleLog.showDateTime = false;
        SimpleLog.dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
        SimpleLog.dateFormatter = null;
        final InputStream in = getResourceAsStream("simplelog.properties");
        if (in != null) {
            try {
                SimpleLog.simpleLogProps.load(in);
                in.close();
            }
            catch (final IOException ex) {}
        }
        SimpleLog.showLogName = getBooleanProperty("org.apache.commons.logging.simplelog.showlogname", SimpleLog.showLogName);
        SimpleLog.showShortName = getBooleanProperty("org.apache.commons.logging.simplelog.showShortLogname", SimpleLog.showShortName);
        SimpleLog.showDateTime = getBooleanProperty("org.apache.commons.logging.simplelog.showdatetime", SimpleLog.showDateTime);
        if (SimpleLog.showDateTime) {
            SimpleLog.dateTimeFormat = getStringProperty("org.apache.commons.logging.simplelog.dateTimeFormat", SimpleLog.dateTimeFormat);
            try {
                SimpleLog.dateFormatter = new SimpleDateFormat(SimpleLog.dateTimeFormat);
            }
            catch (final IllegalArgumentException ex2) {
                SimpleLog.dateTimeFormat = "yyyy/MM/dd HH:mm:ss:SSS zzz";
                SimpleLog.dateFormatter = new SimpleDateFormat(SimpleLog.dateTimeFormat);
            }
        }
    }
    
    public SimpleLog(String name) {
        this.logName = null;
        this.shortLogName = null;
        this.logName = name;
        this.setLevel(3);
        String lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + this.logName);
        for (int i = String.valueOf(name).lastIndexOf("."); lvl == null && i > -1; lvl = getStringProperty("org.apache.commons.logging.simplelog.log." + name), i = String.valueOf(name).lastIndexOf(".")) {
            name = name.substring(0, i);
        }
        if (lvl == null) {
            lvl = getStringProperty("org.apache.commons.logging.simplelog.defaultlog");
        }
        if ("all".equalsIgnoreCase(lvl)) {
            this.setLevel(0);
        }
        else if ("trace".equalsIgnoreCase(lvl)) {
            this.setLevel(1);
        }
        else if ("debug".equalsIgnoreCase(lvl)) {
            this.setLevel(2);
        }
        else if ("info".equalsIgnoreCase(lvl)) {
            this.setLevel(3);
        }
        else if ("warn".equalsIgnoreCase(lvl)) {
            this.setLevel(4);
        }
        else if ("error".equalsIgnoreCase(lvl)) {
            this.setLevel(5);
        }
        else if ("fatal".equalsIgnoreCase(lvl)) {
            this.setLevel(6);
        }
        else if ("off".equalsIgnoreCase(lvl)) {
            this.setLevel(7);
        }
    }
    
    static /* synthetic */ Class class$(final String class$) {
        try {
            return Class.forName(class$);
        }
        catch (final ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }
    
    public final void debug(final Object message) {
        if (this.isLevelEnabled(2)) {
            this.log(2, message, null);
        }
    }
    
    public final void debug(final Object message, final Throwable t) {
        if (this.isLevelEnabled(2)) {
            this.log(2, message, t);
        }
    }
    
    public final void error(final Object message) {
        if (this.isLevelEnabled(5)) {
            this.log(5, message, null);
        }
    }
    
    public final void error(final Object message, final Throwable t) {
        if (this.isLevelEnabled(5)) {
            this.log(5, message, t);
        }
    }
    
    public final void fatal(final Object message) {
        if (this.isLevelEnabled(6)) {
            this.log(6, message, null);
        }
    }
    
    public final void fatal(final Object message, final Throwable t) {
        if (this.isLevelEnabled(6)) {
            this.log(6, message, t);
        }
    }
    
    private static boolean getBooleanProperty(final String name, final boolean dephault) {
        final String prop = getStringProperty(name);
        return (prop == null) ? dephault : "true".equalsIgnoreCase(prop);
    }
    
    private static ClassLoader getContextClassLoader() {
        ClassLoader classLoader = null;
        if (classLoader == null) {
            try {
                final Method method = ((SimpleLog.class$java$lang$Thread != null) ? SimpleLog.class$java$lang$Thread : (SimpleLog.class$java$lang$Thread = class$("java.lang.Thread"))).getMethod("getContextClassLoader", (Class[])null);
                try {
                    classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Object[])null);
                }
                catch (final IllegalAccessException ex) {}
                catch (final InvocationTargetException e) {
                    if (!(e.getTargetException() instanceof SecurityException)) {
                        throw new LogConfigurationException("Unexpected InvocationTargetException", e.getTargetException());
                    }
                }
            }
            catch (final NoSuchMethodException ex2) {}
        }
        if (classLoader == null) {
            classLoader = ((SimpleLog.class$org$apache$commons$logging$impl$SimpleLog != null) ? SimpleLog.class$org$apache$commons$logging$impl$SimpleLog : (SimpleLog.class$org$apache$commons$logging$impl$SimpleLog = class$("org.apache.commons.logging.impl.SimpleLog"))).getClassLoader();
        }
        return classLoader;
    }
    
    public int getLevel() {
        return this.currentLogLevel;
    }
    
    private static InputStream getResourceAsStream(final String name) {
        return AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
            private final /* synthetic */ String val$name = val$name;
            
            public Object run() {
                final ClassLoader threadCL = getContextClassLoader();
                if (threadCL != null) {
                    return threadCL.getResourceAsStream(this.val$name);
                }
                return ClassLoader.getSystemResourceAsStream(this.val$name);
            }
        });
    }
    
    private static String getStringProperty(final String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        }
        catch (final SecurityException ex) {}
        return (prop == null) ? SimpleLog.simpleLogProps.getProperty(name) : prop;
    }
    
    private static String getStringProperty(final String name, final String dephault) {
        final String prop = getStringProperty(name);
        return (prop == null) ? dephault : prop;
    }
    
    public final void info(final Object message) {
        if (this.isLevelEnabled(3)) {
            this.log(3, message, null);
        }
    }
    
    public final void info(final Object message, final Throwable t) {
        if (this.isLevelEnabled(3)) {
            this.log(3, message, t);
        }
    }
    
    public final boolean isDebugEnabled() {
        return this.isLevelEnabled(2);
    }
    
    public final boolean isErrorEnabled() {
        return this.isLevelEnabled(5);
    }
    
    public final boolean isFatalEnabled() {
        return this.isLevelEnabled(6);
    }
    
    public final boolean isInfoEnabled() {
        return this.isLevelEnabled(3);
    }
    
    protected boolean isLevelEnabled(final int logLevel) {
        return logLevel >= this.currentLogLevel;
    }
    
    public final boolean isTraceEnabled() {
        return this.isLevelEnabled(1);
    }
    
    public final boolean isWarnEnabled() {
        return this.isLevelEnabled(4);
    }
    
    protected void log(final int type, final Object message, final Throwable t) {
        final StringBuffer buf = new StringBuffer();
        if (SimpleLog.showDateTime) {
            buf.append(SimpleLog.dateFormatter.format(new Date()));
            buf.append(" ");
        }
        switch (type) {
            case 1: {
                buf.append("[TRACE] ");
                break;
            }
            case 2: {
                buf.append("[DEBUG] ");
                break;
            }
            case 3: {
                buf.append("[INFO] ");
                break;
            }
            case 4: {
                buf.append("[WARN] ");
                break;
            }
            case 5: {
                buf.append("[ERROR] ");
                break;
            }
            case 6: {
                buf.append("[FATAL] ");
                break;
            }
        }
        if (SimpleLog.showShortName) {
            if (this.shortLogName == null) {
                this.shortLogName = this.logName.substring(this.logName.lastIndexOf(".") + 1);
                this.shortLogName = this.shortLogName.substring(this.shortLogName.lastIndexOf("/") + 1);
            }
            buf.append(String.valueOf(this.shortLogName)).append(" - ");
        }
        else if (SimpleLog.showLogName) {
            buf.append(String.valueOf(this.logName)).append(" - ");
        }
        buf.append(String.valueOf(message));
        if (t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");
            final StringWriter sw = new StringWriter(1024);
            final PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            buf.append(sw.toString());
        }
        this.write(buf);
    }
    
    public void setLevel(final int currentLogLevel) {
        this.currentLogLevel = currentLogLevel;
    }
    
    public final void trace(final Object message) {
        if (this.isLevelEnabled(1)) {
            this.log(1, message, null);
        }
    }
    
    public final void trace(final Object message, final Throwable t) {
        if (this.isLevelEnabled(1)) {
            this.log(1, message, t);
        }
    }
    
    public final void warn(final Object message) {
        if (this.isLevelEnabled(4)) {
            this.log(4, message, null);
        }
    }
    
    public final void warn(final Object message, final Throwable t) {
        if (this.isLevelEnabled(4)) {
            this.log(4, message, t);
        }
    }
    
    protected void write(final StringBuffer buffer) {
        System.err.println(buffer.toString());
    }
}
