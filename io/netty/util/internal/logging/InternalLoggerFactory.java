package io.netty.util.internal.logging;

import io.netty.util.internal.ObjectUtil;

public abstract class InternalLoggerFactory
{
    private static volatile InternalLoggerFactory defaultFactory;
    
    private static InternalLoggerFactory newDefaultFactory(final String name) {
        InternalLoggerFactory f = useSlf4JLoggerFactory(name);
        if (f != null) {
            return f;
        }
        f = useLog4J2LoggerFactory(name);
        if (f != null) {
            return f;
        }
        f = useLog4JLoggerFactory(name);
        if (f != null) {
            return f;
        }
        return useJdkLoggerFactory(name);
    }
    
    private static InternalLoggerFactory useSlf4JLoggerFactory(final String name) {
        try {
            final InternalLoggerFactory f = Slf4JLoggerFactory.INSTANCE_WITH_NOP_CHECK;
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
            return f;
        }
        catch (final LinkageError ignore) {
            return null;
        }
        catch (final Exception ignore2) {
            return null;
        }
    }
    
    private static InternalLoggerFactory useLog4J2LoggerFactory(final String name) {
        try {
            final InternalLoggerFactory f = Log4J2LoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using Log4J2 as the default logging framework");
            return f;
        }
        catch (final LinkageError ignore) {
            return null;
        }
        catch (final Exception ignore2) {
            return null;
        }
    }
    
    private static InternalLoggerFactory useLog4JLoggerFactory(final String name) {
        try {
            final InternalLoggerFactory f = Log4JLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using Log4J as the default logging framework");
            return f;
        }
        catch (final LinkageError ignore) {
            return null;
        }
        catch (final Exception ignore2) {
            return null;
        }
    }
    
    private static InternalLoggerFactory useJdkLoggerFactory(final String name) {
        final InternalLoggerFactory f = JdkLoggerFactory.INSTANCE;
        f.newInstance(name).debug("Using java.util.logging as the default logging framework");
        return f;
    }
    
    public static InternalLoggerFactory getDefaultFactory() {
        if (InternalLoggerFactory.defaultFactory == null) {
            InternalLoggerFactory.defaultFactory = newDefaultFactory(InternalLoggerFactory.class.getName());
        }
        return InternalLoggerFactory.defaultFactory;
    }
    
    public static void setDefaultFactory(final InternalLoggerFactory defaultFactory) {
        InternalLoggerFactory.defaultFactory = ObjectUtil.checkNotNull(defaultFactory, "defaultFactory");
    }
    
    public static InternalLogger getInstance(final Class<?> clazz) {
        return getInstance(clazz.getName());
    }
    
    public static InternalLogger getInstance(final String name) {
        return getDefaultFactory().newInstance(name);
    }
    
    protected abstract InternalLogger newInstance(final String p0);
}
