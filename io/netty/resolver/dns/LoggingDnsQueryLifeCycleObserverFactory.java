package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.ObjectUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;

public final class LoggingDnsQueryLifeCycleObserverFactory implements DnsQueryLifecycleObserverFactory
{
    private static final InternalLogger DEFAULT_LOGGER;
    private final InternalLogger logger;
    private final InternalLogLevel level;
    
    public LoggingDnsQueryLifeCycleObserverFactory() {
        this(LogLevel.DEBUG);
    }
    
    public LoggingDnsQueryLifeCycleObserverFactory(final LogLevel level) {
        this.level = checkAndConvertLevel(level);
        this.logger = LoggingDnsQueryLifeCycleObserverFactory.DEFAULT_LOGGER;
    }
    
    public LoggingDnsQueryLifeCycleObserverFactory(final Class<?> classContext, final LogLevel level) {
        this.level = checkAndConvertLevel(level);
        this.logger = InternalLoggerFactory.getInstance(ObjectUtil.checkNotNull(classContext, "classContext"));
    }
    
    public LoggingDnsQueryLifeCycleObserverFactory(final String name, final LogLevel level) {
        this.level = checkAndConvertLevel(level);
        this.logger = InternalLoggerFactory.getInstance(ObjectUtil.checkNotNull(name, "name"));
    }
    
    private static InternalLogLevel checkAndConvertLevel(final LogLevel level) {
        return ObjectUtil.checkNotNull(level, "level").toInternalLevel();
    }
    
    @Override
    public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(final DnsQuestion question) {
        return new LoggingDnsQueryLifecycleObserver(question, this.logger, this.level);
    }
    
    static {
        DEFAULT_LOGGER = InternalLoggerFactory.getInstance(LoggingDnsQueryLifeCycleObserverFactory.class);
    }
}
