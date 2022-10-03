package org.glassfish.jersey.logging;

import java.util.Map;
import org.glassfish.jersey.CommonProperties;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.FeatureContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Feature;

public class LoggingFeature implements Feature
{
    public static final String DEFAULT_LOGGER_NAME;
    public static final String DEFAULT_LOGGER_LEVEL;
    public static final int DEFAULT_MAX_ENTITY_SIZE = 8192;
    public static final Verbosity DEFAULT_VERBOSITY;
    private static final String LOGGER_NAME_POSTFIX = ".logger.name";
    private static final String LOGGER_LEVEL_POSTFIX = ".logger.level";
    private static final String VERBOSITY_POSTFIX = ".verbosity";
    private static final String MAX_ENTITY_POSTFIX = ".entity.maxSize";
    private static final String LOGGING_FEATURE_COMMON_PREFIX = "jersey.config.logging";
    public static final String LOGGING_FEATURE_LOGGER_NAME = "jersey.config.logging.logger.name";
    public static final String LOGGING_FEATURE_LOGGER_LEVEL = "jersey.config.logging.logger.level";
    public static final String LOGGING_FEATURE_VERBOSITY = "jersey.config.logging.verbosity";
    public static final String LOGGING_FEATURE_MAX_ENTITY_SIZE = "jersey.config.logging.entity.maxSize";
    private static final String LOGGING_FEATURE_SERVER_PREFIX = "jersey.config.server.logging";
    public static final String LOGGING_FEATURE_LOGGER_NAME_SERVER = "jersey.config.server.logging.logger.name";
    public static final String LOGGING_FEATURE_LOGGER_LEVEL_SERVER = "jersey.config.server.logging.logger.level";
    public static final String LOGGING_FEATURE_VERBOSITY_SERVER = "jersey.config.server.logging.verbosity";
    public static final String LOGGING_FEATURE_MAX_ENTITY_SIZE_SERVER = "jersey.config.server.logging.entity.maxSize";
    private static final String LOGGING_FEATURE_CLIENT_PREFIX = "jersey.config.client.logging";
    public static final String LOGGING_FEATURE_LOGGER_NAME_CLIENT = "jersey.config.client.logging.logger.name";
    public static final String LOGGING_FEATURE_LOGGER_LEVEL_CLIENT = "jersey.config.client.logging.logger.level";
    public static final String LOGGING_FEATURE_VERBOSITY_CLIENT = "jersey.config.client.logging.verbosity";
    public static final String LOGGING_FEATURE_MAX_ENTITY_SIZE_CLIENT = "jersey.config.client.logging.entity.maxSize";
    private final Logger filterLogger;
    private final Verbosity verbosity;
    private final Integer maxEntitySize;
    private final Level level;
    
    public LoggingFeature() {
        this(null, null, null, null);
    }
    
    public LoggingFeature(final Logger logger) {
        this(logger, null, null, null);
    }
    
    public LoggingFeature(final Logger logger, final Verbosity verbosity) {
        this(logger, null, verbosity, null);
    }
    
    public LoggingFeature(final Logger logger, final Integer maxEntitySize) {
        this(logger, null, LoggingFeature.DEFAULT_VERBOSITY, maxEntitySize);
    }
    
    public LoggingFeature(final Logger logger, final Level level, final Verbosity verbosity, final Integer maxEntitySize) {
        this.filterLogger = logger;
        this.level = level;
        this.verbosity = verbosity;
        this.maxEntitySize = maxEntitySize;
    }
    
    public boolean configure(final FeatureContext context) {
        boolean enabled = false;
        if (context.getConfiguration().getRuntimeType() == RuntimeType.CLIENT) {
            final ClientLoggingFilter clientLoggingFilter = (ClientLoggingFilter)this.createLoggingFilter(context, RuntimeType.CLIENT);
            context.register((Object)clientLoggingFilter);
            enabled = true;
        }
        if (context.getConfiguration().getRuntimeType() == RuntimeType.SERVER) {
            final ServerLoggingFilter serverClientFilter = (ServerLoggingFilter)this.createLoggingFilter(context, RuntimeType.SERVER);
            context.register((Object)serverClientFilter);
            enabled = true;
        }
        return enabled;
    }
    
    private LoggingInterceptor createLoggingFilter(final FeatureContext context, final RuntimeType runtimeType) {
        final Map properties = context.getConfiguration().getProperties();
        final String filterLoggerName = CommonProperties.getValue(properties, (runtimeType == RuntimeType.SERVER) ? "jersey.config.server.logging.logger.name" : "jersey.config.client.logging.logger.name", CommonProperties.getValue(properties, "jersey.config.logging.logger.name", LoggingFeature.DEFAULT_LOGGER_NAME));
        final String filterLevel = CommonProperties.getValue(properties, (runtimeType == RuntimeType.SERVER) ? "jersey.config.server.logging.logger.level" : "jersey.config.client.logging.logger.level", CommonProperties.getValue(context.getConfiguration().getProperties(), "jersey.config.logging.logger.level", LoggingFeature.DEFAULT_LOGGER_LEVEL));
        final Verbosity filterVerbosity = CommonProperties.getValue(properties, (runtimeType == RuntimeType.SERVER) ? "jersey.config.server.logging.verbosity" : "jersey.config.client.logging.verbosity", CommonProperties.getValue(properties, "jersey.config.logging.verbosity", LoggingFeature.DEFAULT_VERBOSITY));
        final int filterMaxEntitySize = CommonProperties.getValue(properties, (runtimeType == RuntimeType.SERVER) ? "jersey.config.server.logging.entity.maxSize" : "jersey.config.client.logging.entity.maxSize", CommonProperties.getValue(properties, "jersey.config.logging.entity.maxSize", 8192));
        final Level loggerLevel = Level.parse(filterLevel);
        if (runtimeType == RuntimeType.SERVER) {
            return new ServerLoggingFilter((this.filterLogger != null) ? this.filterLogger : Logger.getLogger(filterLoggerName), (this.level != null) ? this.level : loggerLevel, (this.verbosity != null) ? this.verbosity : filterVerbosity, (this.maxEntitySize != null) ? this.maxEntitySize : filterMaxEntitySize);
        }
        return new ClientLoggingFilter((this.filterLogger != null) ? this.filterLogger : Logger.getLogger(filterLoggerName), (this.level != null) ? this.level : loggerLevel, (this.verbosity != null) ? this.verbosity : filterVerbosity, (this.maxEntitySize != null) ? this.maxEntitySize : filterMaxEntitySize);
    }
    
    static {
        DEFAULT_LOGGER_NAME = LoggingFeature.class.getName();
        DEFAULT_LOGGER_LEVEL = Level.FINE.getName();
        DEFAULT_VERBOSITY = Verbosity.PAYLOAD_TEXT;
    }
    
    public enum Verbosity
    {
        HEADERS_ONLY, 
        PAYLOAD_TEXT, 
        PAYLOAD_ANY;
    }
}
