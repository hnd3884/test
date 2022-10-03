package com.azul.crs.util.logging;

import java.util.HashMap;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.util.logging.Level;
import java.util.Properties;
import java.util.Collection;
import java.util.Collections;
import java.net.URL;
import java.util.HashSet;
import java.text.DateFormat;
import java.util.Map;

public final class Logger
{
    private static final String REGISTRY = "META-INF/crslog.channels.cfg";
    private static final Map<String, Logger> TAG_TO_LOGGER;
    private static final Map<String, String> CLASS_TO_TAG;
    private static boolean registryLoaded;
    private static Level globalLevel;
    private static boolean globalShowStacktrace;
    private static boolean globalShowTimestamp;
    private static DateFormat timestampFormat;
    private final String tag;
    private Level level;
    private Boolean showStacktrace;
    
    private static synchronized void readRegistry() {
        if (Logger.registryLoaded) {
            return;
        }
        try {
            final Set<URL> urls = new HashSet<URL>();
            final ClassLoader classLoader = Logger.class.getClassLoader();
            if (classLoader != null) {
                urls.addAll(Collections.list(classLoader.getResources("META-INF/crslog.channels.cfg")));
            }
            urls.addAll(Collections.list(ClassLoader.getSystemResources("META-INF/crslog.channels.cfg")));
            for (final URL url : urls) {
                final Properties props = new Properties();
                try (final InputStream is = url.openStream()) {
                    props.load(is);
                    final Enumeration<?> names = props.propertyNames();
                    while (names.hasMoreElements()) {
                        final String klassName = names.nextElement().toString();
                        final String tag = props.getProperty(klassName);
                        Logger.TAG_TO_LOGGER.put(tag, new Logger(tag));
                        Logger.CLASS_TO_TAG.put(klassName, tag);
                    }
                }
            }
        }
        catch (final IOException ex) {
            java.util.logging.Logger.getLogger(Logger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        finally {
            Logger.registryLoaded = true;
        }
    }
    
    static void reset() {
        Logger.CLASS_TO_TAG.clear();
        Logger.TAG_TO_LOGGER.clear();
        Logger.globalLevel = Level.ERROR;
        Logger.globalShowStacktrace = false;
        Logger.globalShowTimestamp = false;
        Logger.registryLoaded = false;
    }
    
    private Logger(final String tag) {
        this.level = null;
        this.showStacktrace = null;
        this.tag = tag;
    }
    
    private static Logger loggerForTag(final String tag) {
        return Logger.TAG_TO_LOGGER.get(tag);
    }
    
    public static Logger getLogger(final Class klass) {
        final String name = klass.getCanonicalName();
        String tag = Logger.CLASS_TO_TAG.get(name);
        if (tag == null) {
            readRegistry();
            tag = Logger.CLASS_TO_TAG.get(name);
        }
        return (tag == null) ? null : loggerForTag(tag);
    }
    
    public static void parseOption(final String name, String value) {
        readRegistry();
        boolean showStacktrace = false;
        boolean showTimestamp = false;
        while (true) {
            int len;
            if (value.endsWith("+stack")) {
                showStacktrace = true;
                len = 6;
            }
            else {
                if (!value.endsWith("+time")) {
                    break;
                }
                showTimestamp = true;
                len = 5;
            }
            value = value.substring(0, value.length() - len);
        }
        Level level;
        try {
            level = Level.valueOf(value.toUpperCase());
        }
        catch (final IllegalArgumentException ex) {
            System.err.println("[CRS.log][error] unsupported log level '" + value + "'");
            return;
        }
        if (name.equals("log")) {
            Logger.globalLevel = level;
            Logger.globalShowStacktrace = showStacktrace;
        }
        else {
            if (!name.startsWith("log+")) {
                System.err.println("[CRS.log][error] unknown CRS log option " + name);
                return;
            }
            final String tag = name.substring(4);
            if (!"vm".equals(tag)) {
                final Logger logger = loggerForTag(tag);
                if (logger != null) {
                    logger.setLevel(level).setShowStacktrace(showStacktrace);
                }
                else {
                    System.err.println("[CRS.log][error] unknown CRS log channel " + name.substring(4));
                }
            }
        }
        if (showTimestamp && !Logger.globalShowTimestamp) {
            Logger.globalShowTimestamp = true;
            Logger.timestampFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSSZ");
        }
    }
    
    public boolean isEnabled(final Level level) {
        return this.getLevel().ordinal() <= level.ordinal();
    }
    
    private Logger setLevel(final Level level) {
        this.level = level;
        return this;
    }
    
    private Logger setShowStacktrace(final boolean showStacktrace) {
        this.showStacktrace = showStacktrace;
        return this;
    }
    
    boolean showStacktrace() {
        return (this.showStacktrace == null) ? Logger.globalShowStacktrace : this.showStacktrace;
    }
    
    boolean showTimestamp() {
        return Logger.globalShowTimestamp;
    }
    
    public Level getLevel() {
        return (this.level == null) ? Logger.globalLevel : this.level;
    }
    
    public void trace(final String format, final Object... args) {
        this.log(Level.TRACE, format, args);
    }
    
    public void debug(final String format, final Object... args) {
        this.log(Level.DEBUG, format, args);
    }
    
    public void info(final String format, final Object... args) {
        this.log(Level.INFO, format, args);
    }
    
    public void warning(final String format, final Object... args) {
        this.log(Level.WARNING, format, args);
    }
    
    public void error(final String format, final Object... args) {
        this.log(Level.ERROR, format, args);
    }
    
    public void log(final Level level, final String format, final Object... args) {
        if (!this.isEnabled(level)) {
            return;
        }
        final StringBuilder prefix = new StringBuilder();
        if (this.showTimestamp()) {
            prefix.append('[').append(Logger.timestampFormat.format(new Date())).append(']');
        }
        prefix.append("[CRS.").append(this.tag).append("][").append(level.n).append("] ");
        if (this.getLevel() == Level.TRACE) {
            final Throwable t = new Throwable();
            final StackTraceElement e = t.getStackTrace()[2];
            prefix.append(e.getClassName()).append('.').append(e.getMethodName()).append(": ");
        }
        final StringBuilder out = new StringBuilder(prefix);
        for (final char c : String.format(format, args).toCharArray()) {
            out.append(c);
            if (c == '\n') {
                out.append((CharSequence)prefix);
            }
        }
        System.out.println(out);
        if (this.showStacktrace()) {
            for (final Object arg : args) {
                if (arg instanceof Throwable) {
                    ((Throwable)arg).printStackTrace();
                }
            }
        }
    }
    
    static {
        TAG_TO_LOGGER = new HashMap<String, Logger>();
        CLASS_TO_TAG = new HashMap<String, String>();
        Logger.registryLoaded = false;
        Logger.globalLevel = Level.ERROR;
        Logger.globalShowStacktrace = false;
        Logger.globalShowTimestamp = false;
    }
    
    public enum Level
    {
        TRACE, 
        DEBUG, 
        INFO, 
        WARNING, 
        ERROR, 
        OFF;
        
        private final String n;
        
        private Level() {
            this.n = this.name().toLowerCase();
        }
    }
}
