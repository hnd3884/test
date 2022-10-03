package sun.util.logging;

import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.io.PrintStream;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.util.Map;

public class PlatformLogger
{
    private static final int OFF = Integer.MAX_VALUE;
    private static final int SEVERE = 1000;
    private static final int WARNING = 900;
    private static final int INFO = 800;
    private static final int CONFIG = 700;
    private static final int FINE = 500;
    private static final int FINER = 400;
    private static final int FINEST = 300;
    private static final int ALL = Integer.MIN_VALUE;
    private static final Level DEFAULT_LEVEL;
    private static boolean loggingEnabled;
    private static Map<String, WeakReference<PlatformLogger>> loggers;
    private volatile LoggerProxy loggerProxy;
    private volatile JavaLoggerProxy javaLoggerProxy;
    
    public static synchronized PlatformLogger getLogger(final String s) {
        Object o = null;
        final WeakReference weakReference = PlatformLogger.loggers.get(s);
        if (weakReference != null) {
            o = weakReference.get();
        }
        if (o == null) {
            o = new PlatformLogger(s);
            PlatformLogger.loggers.put(s, new WeakReference<PlatformLogger>((PlatformLogger)o));
        }
        return (PlatformLogger)o;
    }
    
    public static synchronized void redirectPlatformLoggers() {
        if (PlatformLogger.loggingEnabled || !LoggingSupport.isAvailable()) {
            return;
        }
        PlatformLogger.loggingEnabled = true;
        final Iterator<Map.Entry<String, WeakReference<PlatformLogger>>> iterator = PlatformLogger.loggers.entrySet().iterator();
        while (iterator.hasNext()) {
            final PlatformLogger platformLogger = ((Map.Entry<K, WeakReference>)iterator.next()).getValue().get();
            if (platformLogger != null) {
                platformLogger.redirectToJavaLoggerProxy();
            }
        }
    }
    
    private void redirectToJavaLoggerProxy() {
        final DefaultLoggerProxy defaultLoggerProxy = DefaultLoggerProxy.class.cast(this.loggerProxy);
        final JavaLoggerProxy javaLoggerProxy = new JavaLoggerProxy(defaultLoggerProxy.name, defaultLoggerProxy.level);
        this.javaLoggerProxy = javaLoggerProxy;
        this.loggerProxy = javaLoggerProxy;
    }
    
    private PlatformLogger(final String s) {
        if (PlatformLogger.loggingEnabled) {
            final JavaLoggerProxy javaLoggerProxy = new JavaLoggerProxy(s);
            this.javaLoggerProxy = javaLoggerProxy;
            this.loggerProxy = javaLoggerProxy;
        }
        else {
            this.loggerProxy = new DefaultLoggerProxy(s);
        }
    }
    
    public boolean isEnabled() {
        return this.loggerProxy.isEnabled();
    }
    
    public String getName() {
        return this.loggerProxy.name;
    }
    
    public boolean isLoggable(final Level level) {
        if (level == null) {
            throw new NullPointerException();
        }
        final JavaLoggerProxy javaLoggerProxy = this.javaLoggerProxy;
        return (javaLoggerProxy != null) ? javaLoggerProxy.isLoggable(level) : this.loggerProxy.isLoggable(level);
    }
    
    public Level level() {
        return this.loggerProxy.getLevel();
    }
    
    public void setLevel(final Level level) {
        this.loggerProxy.setLevel(level);
    }
    
    public void severe(final String s) {
        this.loggerProxy.doLog(Level.SEVERE, s);
    }
    
    public void severe(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.SEVERE, s, t);
    }
    
    public void severe(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.SEVERE, s, array);
    }
    
    public void warning(final String s) {
        this.loggerProxy.doLog(Level.WARNING, s);
    }
    
    public void warning(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.WARNING, s, t);
    }
    
    public void warning(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.WARNING, s, array);
    }
    
    public void info(final String s) {
        this.loggerProxy.doLog(Level.INFO, s);
    }
    
    public void info(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.INFO, s, t);
    }
    
    public void info(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.INFO, s, array);
    }
    
    public void config(final String s) {
        this.loggerProxy.doLog(Level.CONFIG, s);
    }
    
    public void config(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.CONFIG, s, t);
    }
    
    public void config(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.CONFIG, s, array);
    }
    
    public void fine(final String s) {
        this.loggerProxy.doLog(Level.FINE, s);
    }
    
    public void fine(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.FINE, s, t);
    }
    
    public void fine(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.FINE, s, array);
    }
    
    public void finer(final String s) {
        this.loggerProxy.doLog(Level.FINER, s);
    }
    
    public void finer(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.FINER, s, t);
    }
    
    public void finer(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.FINER, s, array);
    }
    
    public void finest(final String s) {
        this.loggerProxy.doLog(Level.FINEST, s);
    }
    
    public void finest(final String s, final Throwable t) {
        this.loggerProxy.doLog(Level.FINEST, s, t);
    }
    
    public void finest(final String s, final Object... array) {
        this.loggerProxy.doLog(Level.FINEST, s, array);
    }
    
    static {
        DEFAULT_LEVEL = Level.INFO;
        PlatformLogger.loggingEnabled = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                final String property = System.getProperty("java.util.logging.config.class");
                final String property2 = System.getProperty("java.util.logging.config.file");
                return property != null || property2 != null;
            }
        });
        try {
            Class.forName("sun.util.logging.PlatformLogger$DefaultLoggerProxy", false, PlatformLogger.class.getClassLoader());
            Class.forName("sun.util.logging.PlatformLogger$JavaLoggerProxy", false, PlatformLogger.class.getClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            throw new InternalError(ex);
        }
        PlatformLogger.loggers = new HashMap<String, WeakReference<PlatformLogger>>();
    }
    
    public enum Level
    {
        ALL, 
        FINEST, 
        FINER, 
        FINE, 
        CONFIG, 
        INFO, 
        WARNING, 
        SEVERE, 
        OFF;
        
        Object javaLevel;
        private static final int[] LEVEL_VALUES;
        
        public int intValue() {
            return Level.LEVEL_VALUES[this.ordinal()];
        }
        
        static Level valueOf(final int n) {
            switch (n) {
                case 300: {
                    return Level.FINEST;
                }
                case 500: {
                    return Level.FINE;
                }
                case 400: {
                    return Level.FINER;
                }
                case 800: {
                    return Level.INFO;
                }
                case 900: {
                    return Level.WARNING;
                }
                case 700: {
                    return Level.CONFIG;
                }
                case 1000: {
                    return Level.SEVERE;
                }
                case Integer.MAX_VALUE: {
                    return Level.OFF;
                }
                case Integer.MIN_VALUE: {
                    return Level.ALL;
                }
                default: {
                    final int binarySearch = Arrays.binarySearch(Level.LEVEL_VALUES, 0, Level.LEVEL_VALUES.length - 2, n);
                    return values()[(binarySearch >= 0) ? binarySearch : (-binarySearch - 1)];
                }
            }
        }
        
        static {
            LEVEL_VALUES = new int[] { Integer.MIN_VALUE, 300, 400, 500, 700, 800, 900, 1000, Integer.MAX_VALUE };
        }
    }
    
    private abstract static class LoggerProxy
    {
        final String name;
        
        protected LoggerProxy(final String name) {
            this.name = name;
        }
        
        abstract boolean isEnabled();
        
        abstract Level getLevel();
        
        abstract void setLevel(final Level p0);
        
        abstract void doLog(final Level p0, final String p1);
        
        abstract void doLog(final Level p0, final String p1, final Throwable p2);
        
        abstract void doLog(final Level p0, final String p1, final Object... p2);
        
        abstract boolean isLoggable(final Level p0);
    }
    
    private static final class DefaultLoggerProxy extends LoggerProxy
    {
        volatile Level effectiveLevel;
        volatile Level level;
        private static final String formatString;
        private Date date;
        
        private static PrintStream outputStream() {
            return System.err;
        }
        
        DefaultLoggerProxy(final String s) {
            super(s);
            this.date = new Date();
            this.effectiveLevel = this.deriveEffectiveLevel(null);
            this.level = null;
        }
        
        @Override
        boolean isEnabled() {
            return this.effectiveLevel != Level.OFF;
        }
        
        @Override
        Level getLevel() {
            return this.level;
        }
        
        @Override
        void setLevel(final Level level) {
            if (this.level != level) {
                this.level = level;
                this.effectiveLevel = this.deriveEffectiveLevel(level);
            }
        }
        
        @Override
        void doLog(final Level level, final String s) {
            if (this.isLoggable(level)) {
                outputStream().print(this.format(level, s, null));
            }
        }
        
        @Override
        void doLog(final Level level, final String s, final Throwable t) {
            if (this.isLoggable(level)) {
                outputStream().print(this.format(level, s, t));
            }
        }
        
        @Override
        void doLog(final Level level, final String s, final Object... array) {
            if (this.isLoggable(level)) {
                outputStream().print(this.format(level, this.formatMessage(s, array), null));
            }
        }
        
        @Override
        boolean isLoggable(final Level level) {
            final Level effectiveLevel = this.effectiveLevel;
            return level.intValue() >= effectiveLevel.intValue() && effectiveLevel != Level.OFF;
        }
        
        private Level deriveEffectiveLevel(final Level level) {
            return (level == null) ? PlatformLogger.DEFAULT_LEVEL : level;
        }
        
        private String formatMessage(final String s, final Object... array) {
            try {
                if (array == null || array.length == 0) {
                    return s;
                }
                if (s.indexOf("{0") >= 0 || s.indexOf("{1") >= 0 || s.indexOf("{2") >= 0 || s.indexOf("{3") >= 0) {
                    return MessageFormat.format(s, array);
                }
                return s;
            }
            catch (final Exception ex) {
                return s;
            }
        }
        
        private synchronized String format(final Level level, final String s, final Throwable t) {
            this.date.setTime(System.currentTimeMillis());
            String string = "";
            if (t != null) {
                final StringWriter stringWriter = new StringWriter();
                final PrintWriter printWriter = new PrintWriter(stringWriter);
                printWriter.println();
                t.printStackTrace(printWriter);
                printWriter.close();
                string = stringWriter.toString();
            }
            return String.format(DefaultLoggerProxy.formatString, this.date, this.getCallerInfo(), this.name, level.name(), s, string);
        }
        
        private String getCallerInfo() {
            String s = null;
            String methodName = null;
            final JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
            final Throwable t = new Throwable();
            final int stackTraceDepth = javaLangAccess.getStackTraceDepth(t);
            final String s2 = "sun.util.logging.PlatformLogger";
            int n = 1;
            for (int i = 0; i < stackTraceDepth; ++i) {
                final StackTraceElement stackTraceElement = javaLangAccess.getStackTraceElement(t, i);
                final String className = stackTraceElement.getClassName();
                if (n != 0) {
                    if (className.equals(s2)) {
                        n = 0;
                    }
                }
                else if (!className.equals(s2)) {
                    s = className;
                    methodName = stackTraceElement.getMethodName();
                    break;
                }
            }
            if (s != null) {
                return s + " " + methodName;
            }
            return this.name;
        }
        
        static {
            formatString = LoggingSupport.getSimpleFormat(false);
        }
    }
    
    private static final class JavaLoggerProxy extends LoggerProxy
    {
        private final Object javaLogger;
        
        JavaLoggerProxy(final String s) {
            this(s, null);
        }
        
        JavaLoggerProxy(final String s, final Level level) {
            super(s);
            this.javaLogger = LoggingSupport.getLogger(s);
            if (level != null) {
                LoggingSupport.setLevel(this.javaLogger, level.javaLevel);
            }
        }
        
        @Override
        void doLog(final Level level, final String s) {
            LoggingSupport.log(this.javaLogger, level.javaLevel, s);
        }
        
        @Override
        void doLog(final Level level, final String s, final Throwable t) {
            LoggingSupport.log(this.javaLogger, level.javaLevel, s, t);
        }
        
        @Override
        void doLog(final Level level, final String s, final Object... array) {
            if (!this.isLoggable(level)) {
                return;
            }
            final int n = (array != null) ? array.length : 0;
            final String[] array2 = new String[n];
            for (int i = 0; i < n; ++i) {
                array2[i] = String.valueOf(array[i]);
            }
            LoggingSupport.log(this.javaLogger, level.javaLevel, s, (Object[])array2);
        }
        
        @Override
        boolean isEnabled() {
            return LoggingSupport.isLoggable(this.javaLogger, Level.OFF.javaLevel);
        }
        
        @Override
        Level getLevel() {
            final Object level = LoggingSupport.getLevel(this.javaLogger);
            if (level == null) {
                return null;
            }
            try {
                return Level.valueOf(LoggingSupport.getLevelName(level));
            }
            catch (final IllegalArgumentException ex) {
                return Level.valueOf(LoggingSupport.getLevelValue(level));
            }
        }
        
        @Override
        void setLevel(final Level level) {
            LoggingSupport.setLevel(this.javaLogger, (level == null) ? null : level.javaLevel);
        }
        
        @Override
        boolean isLoggable(final Level level) {
            return LoggingSupport.isLoggable(this.javaLogger, level.javaLevel);
        }
        
        static {
            for (final Level level : Level.values()) {
                level.javaLevel = LoggingSupport.parseLevel(level.name());
            }
        }
    }
}
