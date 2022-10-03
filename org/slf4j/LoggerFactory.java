package org.slf4j;

import java.util.Enumeration;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.slf4j.helpers.SubstituteLogger;
import java.net.URL;
import java.util.Set;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticLoggerBinder;
import org.slf4j.helpers.NOPLoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

public final class LoggerFactory
{
    static final String CODES_PREFIX = "http://www.slf4j.org/codes.html";
    static final String NO_STATICLOGGERBINDER_URL = "http://www.slf4j.org/codes.html#StaticLoggerBinder";
    static final String MULTIPLE_BINDINGS_URL = "http://www.slf4j.org/codes.html#multiple_bindings";
    static final String NULL_LF_URL = "http://www.slf4j.org/codes.html#null_LF";
    static final String VERSION_MISMATCH = "http://www.slf4j.org/codes.html#version_mismatch";
    static final String SUBSTITUTE_LOGGER_URL = "http://www.slf4j.org/codes.html#substituteLogger";
    static final String UNSUCCESSFUL_INIT_URL = "http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final String UNSUCCESSFUL_INIT_MSG = "org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit";
    static final int UNINITIALIZED = 0;
    static final int ONGOING_INITIALIZATION = 1;
    static final int FAILED_INITIALIZATION = 2;
    static final int SUCCESSFUL_INITIALIZATION = 3;
    static final int NOP_FALLBACK_INITIALIZATION = 4;
    static int INITIALIZATION_STATE;
    static SubstituteLoggerFactory TEMP_FACTORY;
    static NOPLoggerFactory NOP_FALLBACK_FACTORY;
    private static final String[] API_COMPATIBILITY_LIST;
    private static String STATIC_LOGGER_BINDER_PATH;
    
    private LoggerFactory() {
    }
    
    static void reset() {
        LoggerFactory.INITIALIZATION_STATE = 0;
        LoggerFactory.TEMP_FACTORY = new SubstituteLoggerFactory();
    }
    
    private static final void performInitialization() {
        bind();
        if (LoggerFactory.INITIALIZATION_STATE == 3) {
            versionSanityCheck();
        }
    }
    
    private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(final String msg) {
        return msg != null && (msg.indexOf("org/slf4j/impl/StaticLoggerBinder") != -1 || msg.indexOf("org.slf4j.impl.StaticLoggerBinder") != -1);
    }
    
    private static final void bind() {
        try {
            final Set<URL> staticLoggerBinderPathSet = findPossibleStaticLoggerBinderPathSet();
            reportMultipleBindingAmbiguity(staticLoggerBinderPathSet);
            StaticLoggerBinder.getSingleton();
            LoggerFactory.INITIALIZATION_STATE = 3;
            reportActualBinding(staticLoggerBinderPathSet);
            fixSubstitutedLoggers();
        }
        catch (final NoClassDefFoundError ncde) {
            final String msg = ncde.getMessage();
            if (!messageContainsOrgSlf4jImplStaticLoggerBinder(msg)) {
                failedBinding(ncde);
                throw ncde;
            }
            LoggerFactory.INITIALIZATION_STATE = 4;
            Util.report("Failed to load class \"org.slf4j.impl.StaticLoggerBinder\".");
            Util.report("Defaulting to no-operation (NOP) logger implementation");
            Util.report("See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.");
        }
        catch (final NoSuchMethodError nsme) {
            final String msg = nsme.getMessage();
            if (msg != null && msg.indexOf("org.slf4j.impl.StaticLoggerBinder.getSingleton()") != -1) {
                LoggerFactory.INITIALIZATION_STATE = 2;
                Util.report("slf4j-api 1.6.x (or later) is incompatible with this binding.");
                Util.report("Your binding is version 1.5.5 or earlier.");
                Util.report("Upgrade your binding to version 1.6.x.");
            }
            throw nsme;
        }
        catch (final Exception e) {
            failedBinding(e);
            throw new IllegalStateException("Unexpected initialization failure", e);
        }
    }
    
    static void failedBinding(final Throwable t) {
        LoggerFactory.INITIALIZATION_STATE = 2;
        Util.report("Failed to instantiate SLF4J LoggerFactory", t);
    }
    
    private static final void fixSubstitutedLoggers() {
        final List<SubstituteLogger> loggers = LoggerFactory.TEMP_FACTORY.getLoggers();
        if (loggers.isEmpty()) {
            return;
        }
        Util.report("The following set of substitute loggers may have been accessed");
        Util.report("during the initialization phase. Logging calls during this");
        Util.report("phase were not honored. However, subsequent logging calls to these");
        Util.report("loggers will work as normally expected.");
        Util.report("See also http://www.slf4j.org/codes.html#substituteLogger");
        for (final SubstituteLogger subLogger : loggers) {
            subLogger.setDelegate(getLogger(subLogger.getName()));
            Util.report(subLogger.getName());
        }
        LoggerFactory.TEMP_FACTORY.clear();
    }
    
    private static final void versionSanityCheck() {
        try {
            final String requested = StaticLoggerBinder.REQUESTED_API_VERSION;
            boolean match = false;
            for (int i = 0; i < LoggerFactory.API_COMPATIBILITY_LIST.length; ++i) {
                if (requested.startsWith(LoggerFactory.API_COMPATIBILITY_LIST[i])) {
                    match = true;
                }
            }
            if (!match) {
                Util.report("The requested version " + requested + " by your slf4j binding is not compatible with " + Arrays.asList(LoggerFactory.API_COMPATIBILITY_LIST).toString());
                Util.report("See http://www.slf4j.org/codes.html#version_mismatch for further details.");
            }
        }
        catch (final NoSuchFieldError nsfe) {}
        catch (final Throwable e) {
            Util.report("Unexpected problem occured during version sanity check", e);
        }
    }
    
    private static Set<URL> findPossibleStaticLoggerBinderPathSet() {
        final Set<URL> staticLoggerBinderPathSet = new LinkedHashSet<URL>();
        try {
            final ClassLoader loggerFactoryClassLoader = LoggerFactory.class.getClassLoader();
            Enumeration<URL> paths;
            if (loggerFactoryClassLoader == null) {
                paths = ClassLoader.getSystemResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            else {
                paths = loggerFactoryClassLoader.getResources(LoggerFactory.STATIC_LOGGER_BINDER_PATH);
            }
            while (paths.hasMoreElements()) {
                final URL path = paths.nextElement();
                staticLoggerBinderPathSet.add(path);
            }
        }
        catch (final IOException ioe) {
            Util.report("Error getting resources from path", ioe);
        }
        return staticLoggerBinderPathSet;
    }
    
    private static boolean isAmbiguousStaticLoggerBinderPathSet(final Set<URL> staticLoggerBinderPathSet) {
        return staticLoggerBinderPathSet.size() > 1;
    }
    
    private static void reportMultipleBindingAmbiguity(final Set<URL> staticLoggerBinderPathSet) {
        if (isAmbiguousStaticLoggerBinderPathSet(staticLoggerBinderPathSet)) {
            Util.report("Class path contains multiple SLF4J bindings.");
            for (final URL path : staticLoggerBinderPathSet) {
                Util.report("Found binding in [" + path + "]");
            }
            Util.report("See http://www.slf4j.org/codes.html#multiple_bindings for an explanation.");
        }
    }
    
    private static void reportActualBinding(final Set<URL> staticLoggerBinderPathSet) {
        if (isAmbiguousStaticLoggerBinderPathSet(staticLoggerBinderPathSet)) {
            Util.report("Actual binding is of type [" + StaticLoggerBinder.getSingleton().getLoggerFactoryClassStr() + "]");
        }
    }
    
    public static Logger getLogger(final String name) {
        final ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(name);
    }
    
    public static Logger getLogger(final Class clazz) {
        return getLogger(clazz.getName());
    }
    
    public static ILoggerFactory getILoggerFactory() {
        if (LoggerFactory.INITIALIZATION_STATE == 0) {
            LoggerFactory.INITIALIZATION_STATE = 1;
            performInitialization();
        }
        switch (LoggerFactory.INITIALIZATION_STATE) {
            case 3: {
                return StaticLoggerBinder.getSingleton().getLoggerFactory();
            }
            case 4: {
                return LoggerFactory.NOP_FALLBACK_FACTORY;
            }
            case 2: {
                throw new IllegalStateException("org.slf4j.LoggerFactory could not be successfully initialized. See also http://www.slf4j.org/codes.html#unsuccessfulInit");
            }
            case 1: {
                return LoggerFactory.TEMP_FACTORY;
            }
            default: {
                throw new IllegalStateException("Unreachable code");
            }
        }
    }
    
    static {
        LoggerFactory.INITIALIZATION_STATE = 0;
        LoggerFactory.TEMP_FACTORY = new SubstituteLoggerFactory();
        LoggerFactory.NOP_FALLBACK_FACTORY = new NOPLoggerFactory();
        API_COMPATIBILITY_LIST = new String[] { "1.6", "1.7" };
        LoggerFactory.STATIC_LOGGER_BINDER_PATH = "org/slf4j/impl/StaticLoggerBinder.class";
    }
}
