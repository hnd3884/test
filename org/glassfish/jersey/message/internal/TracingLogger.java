package org.glassfish.jersey.message.internal;

import javax.ws.rs.WebApplicationException;
import java.lang.annotation.Annotation;
import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import org.glassfish.jersey.internal.PropertiesDelegate;

public abstract class TracingLogger
{
    public static final String PROPERTY_NAME;
    private static final String HEADER_TRACING_PREFIX = "X-Jersey-Tracing-";
    public static final String HEADER_THRESHOLD = "X-Jersey-Tracing-Threshold";
    public static final String HEADER_ACCEPT = "X-Jersey-Tracing-Accept";
    public static final String HEADER_LOGGER = "X-Jersey-Tracing-Logger";
    private static final String HEADER_RESPONSE_FORMAT = "X-Jersey-Tracing-%03d";
    public static final Level DEFAULT_LEVEL;
    private static final String TRACING_LOGGER_NAME_PREFIX = "org.glassfish.jersey.tracing";
    private static final String DEFAULT_LOGGER_NAME_SUFFIX = "general";
    private static final TracingLogger EMPTY;
    
    public static TracingLogger getInstance(final PropertiesDelegate propertiesDelegate) {
        if (propertiesDelegate == null) {
            return TracingLogger.EMPTY;
        }
        final TracingLogger tracingLogger = (TracingLogger)propertiesDelegate.getProperty(TracingLogger.PROPERTY_NAME);
        return (tracingLogger != null) ? tracingLogger : TracingLogger.EMPTY;
    }
    
    public static TracingLogger create(final Level threshold, final String loggerNameSuffix) {
        return new TracingLoggerImpl(threshold, loggerNameSuffix);
    }
    
    public static TracingLogger empty() {
        return TracingLogger.EMPTY;
    }
    
    public abstract boolean isLogEnabled(final Event p0);
    
    public abstract void log(final Event p0, final Object... p1);
    
    public abstract void logDuration(final Event p0, final long p1, final Object... p2);
    
    public abstract long timestamp(final Event p0);
    
    public abstract void flush(final MultivaluedMap<String, Object> p0);
    
    static {
        PROPERTY_NAME = TracingLogger.class.getName();
        DEFAULT_LEVEL = Level.TRACE;
        EMPTY = new TracingLogger() {
            @Override
            public boolean isLogEnabled(final Event event) {
                return false;
            }
            
            @Override
            public void log(final Event event, final Object... args) {
            }
            
            @Override
            public void logDuration(final Event event, final long fromTimestamp, final Object... args) {
            }
            
            @Override
            public long timestamp(final Event event) {
                return -1L;
            }
            
            @Override
            public void flush(final MultivaluedMap<String, Object> headers) {
            }
        };
    }
    
    private static final class TracingLoggerImpl extends TracingLogger
    {
        private final Logger logger;
        private final Level threshold;
        private final TracingInfo tracingInfo;
        
        public TracingLoggerImpl(final Level threshold, String loggerNameSuffix) {
            this.threshold = threshold;
            this.tracingInfo = new TracingInfo();
            loggerNameSuffix = ((loggerNameSuffix != null) ? loggerNameSuffix : "general");
            this.logger = Logger.getLogger("org.glassfish.jersey.tracing." + loggerNameSuffix);
        }
        
        @Override
        public boolean isLogEnabled(final Event event) {
            return this.isEnabled(event.level());
        }
        
        @Override
        public void log(final Event event, final Object... args) {
            this.logDuration(event, -1L, args);
        }
        
        @Override
        public void logDuration(final Event event, final long fromTimestamp, final Object... args) {
            if (this.isEnabled(event.level())) {
                long toTimestamp;
                if (fromTimestamp == -1L) {
                    toTimestamp = -1L;
                }
                else {
                    toTimestamp = System.nanoTime();
                }
                long duration = 0L;
                if (fromTimestamp != -1L && toTimestamp != -1L) {
                    duration = toTimestamp - fromTimestamp;
                }
                this.logImpl(event, duration, args);
            }
        }
        
        @Override
        public long timestamp(final Event event) {
            if (this.isEnabled(event.level())) {
                return System.nanoTime();
            }
            return -1L;
        }
        
        @Override
        public void flush(final MultivaluedMap<String, Object> headers) {
            final String[] messages = this.tracingInfo.getMessages();
            for (int i = 0; i < messages.length; ++i) {
                headers.putSingle((Object)String.format("X-Jersey-Tracing-%03d", i), (Object)messages[i]);
            }
        }
        
        private void logImpl(final Event event, final long duration, final Object... messageArgs) {
            if (this.isEnabled(event.level())) {
                final String[] messageArgsStr = new String[messageArgs.length];
                for (int i = 0; i < messageArgs.length; ++i) {
                    messageArgsStr[i] = formatInstance(messageArgs[i]);
                }
                final TracingInfo.Message message = new TracingInfo.Message(event, duration, messageArgsStr);
                this.tracingInfo.addMessage(message);
                java.util.logging.Level loggingLevel = null;
                switch (event.level()) {
                    case SUMMARY: {
                        loggingLevel = java.util.logging.Level.FINE;
                        break;
                    }
                    case TRACE: {
                        loggingLevel = java.util.logging.Level.FINER;
                        break;
                    }
                    case VERBOSE: {
                        loggingLevel = java.util.logging.Level.FINEST;
                        break;
                    }
                    default: {
                        loggingLevel = java.util.logging.Level.OFF;
                        break;
                    }
                }
                if (this.logger.isLoggable(loggingLevel)) {
                    this.logger.log(loggingLevel, event.name() + ' ' + message.toString() + " [" + TracingInfo.formatDuration(duration) + " ms]");
                }
            }
        }
        
        private boolean isEnabled(final Level level) {
            return this.threshold.ordinal() >= level.ordinal();
        }
        
        private static String formatInstance(final Object instance) {
            final StringBuilder textSB = new StringBuilder();
            if (instance == null) {
                textSB.append("null");
            }
            else if (instance instanceof Number || instance instanceof String || instance instanceof Method) {
                textSB.append(instance.toString());
            }
            else if (instance instanceof Response.StatusType) {
                textSB.append(formatStatusInfo((Response.StatusType)instance));
            }
            else {
                textSB.append('[');
                formatInstance(instance, textSB);
                if (instance.getClass().isAnnotationPresent((Class<? extends Annotation>)Priority.class)) {
                    textSB.append(" #").append(instance.getClass().getAnnotation(Priority.class).value());
                }
                if (instance instanceof WebApplicationException) {
                    formatResponse(((WebApplicationException)instance).getResponse(), textSB);
                }
                else if (instance instanceof Response) {
                    formatResponse((Response)instance, textSB);
                }
                textSB.append(']');
            }
            return textSB.toString();
        }
        
        private static void formatInstance(final Object instance, final StringBuilder textSB) {
            textSB.append(instance.getClass().getName()).append(" @").append(Integer.toHexString(System.identityHashCode(instance)));
        }
        
        private static void formatResponse(final Response response, final StringBuilder textSB) {
            textSB.append(" <").append(formatStatusInfo(response.getStatusInfo())).append('|');
            if (response.hasEntity()) {
                formatInstance(response.getEntity(), textSB);
            }
            else {
                textSB.append("-no-entity-");
            }
            textSB.append('>');
        }
        
        private static String formatStatusInfo(final Response.StatusType statusInfo) {
            return String.valueOf(statusInfo.getStatusCode()) + '/' + statusInfo.getFamily() + '|' + statusInfo.getReasonPhrase();
        }
    }
    
    public enum Level
    {
        SUMMARY, 
        TRACE, 
        VERBOSE;
    }
    
    public interface Event
    {
        String name();
        
        String category();
        
        Level level();
        
        String messageFormat();
    }
}
