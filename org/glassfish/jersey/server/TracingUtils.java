package org.glassfish.jersey.server;

import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.core.Configuration;
import java.util.Iterator;
import org.glassfish.jersey.server.internal.ServerTraceEvent;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.message.internal.TracingLogger;
import java.util.List;

public final class TracingUtils
{
    private static final List<String> SUMMARY_HEADERS;
    private static final TracingConfig DEFAULT_CONFIGURATION_TYPE;
    
    private TracingUtils() {
    }
    
    public static void initTracingSupport(final TracingConfig type, final TracingLogger.Level appThreshold, final ContainerRequest containerRequest) {
        TracingLogger tracingLogger;
        if (isTracingSupportEnabled(type, containerRequest)) {
            tracingLogger = TracingLogger.create(getTracingThreshold(appThreshold, containerRequest), getTracingLoggerNameSuffix(containerRequest));
        }
        else {
            tracingLogger = TracingLogger.empty();
        }
        containerRequest.setProperty(TracingLogger.PROPERTY_NAME, tracingLogger);
    }
    
    public static void logStart(final ContainerRequest request) {
        final TracingLogger tracingLogger = TracingLogger.getInstance((PropertiesDelegate)request);
        if (tracingLogger.isLogEnabled((TracingLogger.Event)ServerTraceEvent.START)) {
            final StringBuilder textSB = new StringBuilder();
            textSB.append(String.format("baseUri=[%s] requestUri=[%s] method=[%s] authScheme=[%s]", request.getBaseUri(), request.getRequestUri(), request.getMethod(), toStringOrNA(request.getSecurityContext().getAuthenticationScheme())));
            for (final String header : TracingUtils.SUMMARY_HEADERS) {
                textSB.append(String.format(" %s=%s", header, toStringOrNA(request.getRequestHeaders().get((Object)header))));
            }
            tracingLogger.log((TracingLogger.Event)ServerTraceEvent.START, new Object[] { textSB.toString() });
        }
        if (tracingLogger.isLogEnabled((TracingLogger.Event)ServerTraceEvent.START_HEADERS)) {
            final StringBuilder textSB = new StringBuilder();
            for (final String header : request.getRequestHeaders().keySet()) {
                if (!TracingUtils.SUMMARY_HEADERS.contains(header)) {
                    textSB.append(String.format(" %s=%s", header, toStringOrNA(request.getRequestHeaders().get((Object)header))));
                }
            }
            if (textSB.length() > 0) {
                textSB.insert(0, "Other request headers:");
            }
            tracingLogger.log((TracingLogger.Event)ServerTraceEvent.START_HEADERS, new Object[] { textSB.toString() });
        }
    }
    
    private static boolean isTracingSupportEnabled(final TracingConfig type, final ContainerRequest containerRequest) {
        return type == TracingConfig.ALL || (type == TracingConfig.ON_DEMAND && containerRequest.getHeaderString("X-Jersey-Tracing-Accept") != null);
    }
    
    static TracingConfig getTracingConfig(final Configuration configuration) {
        final String tracingText = ServerProperties.getValue((Map<String, ?>)configuration.getProperties(), "jersey.config.server.tracing.type", String.class);
        TracingConfig result;
        if (tracingText != null) {
            result = TracingConfig.valueOf(tracingText);
        }
        else {
            result = TracingUtils.DEFAULT_CONFIGURATION_TYPE;
        }
        return result;
    }
    
    private static String getTracingLoggerNameSuffix(final ContainerRequest request) {
        return request.getHeaderString("X-Jersey-Tracing-Logger");
    }
    
    static TracingLogger.Level getTracingThreshold(final Configuration configuration) {
        final String thresholdText = ServerProperties.getValue((Map<String, ?>)configuration.getProperties(), "jersey.config.server.tracing.threshold", String.class);
        return (thresholdText == null) ? TracingLogger.DEFAULT_LEVEL : TracingLogger.Level.valueOf(thresholdText);
    }
    
    private static TracingLogger.Level getTracingThreshold(final TracingLogger.Level appThreshold, final ContainerRequest containerRequest) {
        final String thresholdText = containerRequest.getHeaderString("X-Jersey-Tracing-Threshold");
        return (thresholdText == null) ? appThreshold : TracingLogger.Level.valueOf(thresholdText);
    }
    
    private static String toStringOrNA(final Object object) {
        if (object == null) {
            return "n/a";
        }
        return String.valueOf(object);
    }
    
    static {
        (SUMMARY_HEADERS = new ArrayList<String>()).add("Accept".toLowerCase());
        TracingUtils.SUMMARY_HEADERS.add("Accept-Encoding".toLowerCase());
        TracingUtils.SUMMARY_HEADERS.add("Accept-Charset".toLowerCase());
        TracingUtils.SUMMARY_HEADERS.add("Accept-Language".toLowerCase());
        TracingUtils.SUMMARY_HEADERS.add("Content-Type".toLowerCase());
        TracingUtils.SUMMARY_HEADERS.add("Content-Length".toLowerCase());
        DEFAULT_CONFIGURATION_TYPE = TracingConfig.OFF;
    }
}
